package org.caltech.miniswing.serviceserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.caltech.miniswing.serviceclient.dto.SvcResponseDto;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.domain.SvcRepository;
import org.caltech.miniswing.serviceserver.domain.SvcRepositorySupport;
import org.caltech.miniswing.serviceserver.dto.SvcCreateRequestDto;
import org.caltech.miniswing.serviceserver.mapper.SvcCreateRequestMapper;
import org.caltech.miniswing.serviceserver.mapper.SvcResponseMapper;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto.SvcUpdateTyp.FEE_PROD_UPDATE;
import static org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto.SvcUpdateTyp.SVC_STATUS_UPDATE;

@Service
@Slf4j
public class SvcService {
    private final AsyncHelper asyncHelper;

    private final SvcRepository svcRepository;
    private final SvcRepositorySupport svcRepositorySupport;

    private final SvcResponseMapper svcResponseMapper;
    private final SvcCreateRequestMapper svcCreateRequestMapper;

    @Autowired
    public SvcService(AsyncHelper asyncHelper,
                      SvcRepository svcRepository,
                      SvcRepositorySupport svcRepositorySupport,
                      SvcResponseMapper svcResponseMapper,
                      SvcCreateRequestMapper svcCreateRequestMapper) {
        this.asyncHelper            = asyncHelper;
        this.svcRepository          = svcRepository;
        this.svcResponseMapper      = svcResponseMapper;
        this.svcRepositorySupport   = svcRepositorySupport;
        this.svcCreateRequestMapper = svcCreateRequestMapper;
    }

    @Transactional(readOnly = true)
    public Mono<SvcResponseDto> getService(long svcMgmtNum) {
        return asyncHelper.mono( () ->
                Mono.just(
                        svcResponseMapper.entityToDto(
                                svcRepository.findById(svcMgmtNum)
                                             .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) ))
        ));
    }

    @Transactional(readOnly = true)
    public Flux<SvcResponseDto> getServicesByCustomer(int offset, int limit, int custNum, boolean includeTermSvc) {
        return asyncHelper.flux( () ->
                Flux.fromIterable(
                        svcResponseMapper.entityListToDtoList(
                                svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custNum, includeTermSvc)
                ))
        ).log();
    }

    @Transactional
    public Mono<SvcResponseDto> createService(SvcCreateRequestDto dto) {
        Svc s = svcCreateRequestMapper.dtoToEntity(dto);
        s.subscribe(LocalDateTime.now());
        return asyncHelper.mono( () ->
                Mono.just(
                        svcResponseMapper.entityToDto(svcRepository.save(s)))
        ).log();
    }

    @Transactional
    public Mono<Void> updateService(long svcMgmtNum, SvcUpdateRequestDto dto) {
        return asyncHelper.mono( () -> _updateService(svcMgmtNum, dto) ).log();
    }

    private Mono<Void> _updateService(long svcMgmtNum, SvcUpdateRequestDto dto) {
        Svc s = svcRepository.findByIdWithSvcStHsts(svcMgmtNum)
                .orElseThrow(() -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum));
        if (SVC_STATUS_UPDATE == dto.getSvcUpdateTyp()) {
            updateServiceStatus(s, dto.getAfterSvcStCd());
        } else if (FEE_PROD_UPDATE == dto.getSvcUpdateTyp()) {
            updateFeeProd(s, dto.getAfterFeeProdId());
        } else {
            throw new InvalidInputException("알 수 없는 서비스 변경 구분입니다!");
        }
        svcRepository.save(s);

        return Mono.empty().then();     //-- 이상하다..
    }

    private void updateServiceStatus(Svc s, SvcStCd afterSvcStCd) {
        LocalDateTime now = LocalDateTime.now();

        if (SvcStCd.AC == afterSvcStCd) {
            s.activate(now);
        } else if (SvcStCd.SP == afterSvcStCd) {
            s.suspend(now);
        } else if (SvcStCd.TG == afterSvcStCd) {
            s.terminate(now);
        }
    }

    private void updateFeeProd(Svc s, String afterFeeProdId) {
        s.setFeeProdId(afterFeeProdId);
    }
}
