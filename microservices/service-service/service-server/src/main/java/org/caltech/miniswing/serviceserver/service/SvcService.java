package org.caltech.miniswing.serviceserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.domain.SvcRepository;
import org.caltech.miniswing.serviceserver.domain.SvcRepositorySupport;
import org.caltech.miniswing.serviceserver.dto.ServiceCreateRequestDto;
import org.caltech.miniswing.serviceserver.mapper.SvcCreateRequestMapper;
import org.caltech.miniswing.serviceserver.mapper.SvcResponseMapper;
import org.caltech.miniswing.serviceserver.messaging.MessagePublisher;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SvcService {
    private final AsyncHelper asyncHelper;

    private final SvcRepository svcRepository;
    private final SvcRepositorySupport svcRepositorySupport;

    private final SvcResponseMapper svcResponseMapper;
    private final SvcCreateRequestMapper svcCreateRequestMapper;

    private final MessagePublisher messagePublisher;

    @Autowired
    private final PlmClient plmClient;

    @Autowired
    public SvcService(AsyncHelper asyncHelper,
                      SvcRepository svcRepository,
                      SvcRepositorySupport svcRepositorySupport,
                      SvcResponseMapper svcResponseMapper,
                      SvcCreateRequestMapper svcCreateRequestMapper,
                      MessagePublisher messagePublisher,
                      PlmClient plmClient) {
        this.asyncHelper            = asyncHelper;
        this.svcRepository          = svcRepository;
        this.svcResponseMapper      = svcResponseMapper;
        this.svcRepositorySupport   = svcRepositorySupport;
        this.svcCreateRequestMapper = svcCreateRequestMapper;
        this.messagePublisher       = messagePublisher;
        this.plmClient              = plmClient;
    }

    @Transactional(readOnly = true)
    public Mono<ServiceDto> getService(long svcMgmtNum) {
        Mono<ServiceDto> service = asyncHelper.mono( () ->
                Mono.fromCallable( () ->
                        svcResponseMapper.entityToDto(
                                svcRepository.findById(svcMgmtNum)
                                             .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) )
                        )
        ));

        Mono<Tuple2<ServiceDto, Map<String, String>>> combined =
                service.zipWhen(s ->
                        plmClient.getProdNmByIds(Collections.singletonList(s.getFeeProdId()))
                                .map(plm -> plm.stream()
                                               .collect( Collectors.toMap(ProdResponseDto::getProdId, ProdResponseDto::getProdNm)) )
                );

        return combined.map(c -> {
            ServiceDto svc = c.getT1();
            Map<String, String> pinm = c.getT2();
            svc.setFeeProdNm(pinm.get(svc.getFeeProdId()));
            return svc;
        });
    }

    @Transactional(readOnly = true)
    public Mono<List<ServiceDto>> getServicesByCustomer(int offset, int limit, long custNum, boolean includeTermSvc) {
        Mono<List<ServiceDto>> services = asyncHelper.mono( () ->
                Mono.fromCallable( () ->
                        svcResponseMapper.entityListToDtoList(
                                svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custNum, includeTermSvc)
                        )
                )
        );

        Mono<Tuple2<List<ServiceDto>, Map<String, String>>> combined =
                services.zipWhen(ss ->
                        plmClient.getProdNmByIds(ss.stream().map( ServiceDto::getFeeProdId ).collect( Collectors.toList() ))
                                .map(plm -> plm.stream()
                                               .collect( Collectors.toMap(ProdResponseDto::getProdId, ProdResponseDto::getProdNm)) )
                );

        return combined.map(c -> {
            List<ServiceDto> ss = c.getT1();
            Map<String, String> pinm = c.getT2();
            ss.forEach(sp -> sp.setFeeProdNm(pinm.get(sp.getFeeProdId())));
            return ss;
        });
    }

    @Transactional
    public ServiceDto createService(ServiceCreateRequestDto dto) {
        Svc s = svcCreateRequestMapper.dtoToEntity(dto);
        s.subscribe( LocalDateTime.now() );
        s = svcRepository.save(s);

        ServiceDto serviceDto = svcResponseMapper.entityToDto(s);

        //-- ServiceCreated event publishing
        messagePublisher.publishServiceCreatedEvent(serviceDto);

        return serviceDto;
    }

    @Transactional
    public void changeFeeProduct(long svcMgmtNum, String feeProdId) {
        Svc s = svcRepository.findById(svcMgmtNum)
                .orElseThrow(() -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum));
        s.setFeeProdId(feeProdId);
        svcRepository.save(s);
    }

    @Transactional
    public void changeServiceStatus(long svcMgmtNum, ServiceStatusChangeRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();
        Svc s = svcRepository.findByIdWithSvcStHsts(svcMgmtNum)
                .orElseThrow(() -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum));

        SvcStCd beforeSvcStCd = s.getSvcStCd();

        if (SvcStCd.AC == dto.getAfterSvcStCd()) {
            s.activate(now);
            messagePublisher.publishServiceActivatedEvent(s.getSvcMgmtNum(), beforeSvcStCd);
        } else if (SvcStCd.SP == dto.getAfterSvcStCd()) {
            s.suspend(now);
            messagePublisher.publishServiceSuspendedEvent(s.getSvcMgmtNum(), beforeSvcStCd);
        } else if (SvcStCd.TG == dto.getAfterSvcStCd()) {
            s.terminate(now);
            messagePublisher.publishServiceTerminatedEvent(s.getSvcMgmtNum(), beforeSvcStCd);
        }

        svcRepository.save(s);
    }
}
