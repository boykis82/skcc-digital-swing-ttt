package org.caltech.miniswing.productserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productserver.domain.SvcProds;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.caltech.miniswing.productserver.mapper.SvcProdResponseMapper;
import org.caltech.miniswing.productserver.messaging.MessagePublisher;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProdService {
    private final AsyncHelper asyncHelper;
    private final SvcProdRepository svcProdRepository;
    private final SvcProdResponseMapper svcProdResponseMapper;
    private final MessagePublisher messagePublisher;
    private final PlmClient plmClient;

    @Autowired
    public ProdService(AsyncHelper asyncHelper,
                       SvcProdRepository svcProdRepository,
                       SvcProdResponseMapper svcProdResponseMapper,
                       MessagePublisher messagePublisher,
                       PlmClient plmClient) {
        this.asyncHelper           = asyncHelper;
        this.svcProdRepository     = svcProdRepository;
        this.svcProdResponseMapper = svcProdResponseMapper;
        this.messagePublisher      = messagePublisher;
        this.plmClient             = plmClient;
    }

    @Transactional
    public void subscribeProduct(ProdSubscribeRequestDto dto) {
        List<SvcProd> svcProds = svcProdRepository.findActiveSvcProds(dto.getSvcMgmtNum());

        SvcProds svc = SvcProds.builder()
                .svcMgmtNum(dto.getSvcMgmtNum())
                .svcProds(svcProds)
                .build();

        List<SvcProd> subTermSvcProds = svc.subscribeProduct( dto.getProdId(), dto.getSvcProdCd() );
        svcProdRepository.saveAll(subTermSvcProds);

        messagePublisher.publishProductSubscriptionChangedEvent(
                dto.getSvcMgmtNum(),
                svcProdResponseMapper.entityListToDtoList(subTermSvcProds)
        );
    }

    @Transactional
    public void terminateProduct(long svcMgmtNum, long svcProdId) {
        List<SvcProd> svcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);

        SvcProds svc = SvcProds.builder()
                .svcMgmtNum(svcMgmtNum)
                .svcProds(svcProds)
                .build();

        List<SvcProd> subTermSvcProds = svc.terminateProduct(svcProdId);
        svcProdRepository.saveAll(subTermSvcProds);

        messagePublisher.publishProductSubscriptionChangedEvent(
                svcMgmtNum,
                svcProdResponseMapper.entityListToDtoList(subTermSvcProds)
        );
    }

    @Transactional
    public void terminateAllProducts(long svcMgmtNum) {
        List<SvcProd> svcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);

        SvcProds svc = SvcProds.builder()
                .svcMgmtNum(svcMgmtNum)
                .svcProds(svcProds)
                .build();

        List<SvcProd> subTermSvcProds = svc.terminateAllProducts();
        svcProdRepository.saveAll(subTermSvcProds);

        messagePublisher.publishProductSubscriptionChangedEvent(
                svcMgmtNum,
                svcProdResponseMapper.entityListToDtoList(subTermSvcProds)
        );
    }

    @Transactional(readOnly = true)
    public Mono<List<SvcProdResponseDto>> getServiceProducts(long svcMgmtNum, boolean includeTermProd) {
        Mono<List<SvcProdResponseDto>> svcProds =  asyncHelper.mono( () ->
                Mono.fromCallable( () -> includeTermProd
                        ? svcProdResponseMapper.entityListToDtoList(svcProdRepository.findAllSvcProds(svcMgmtNum))
                        : svcProdResponseMapper.entityListToDtoList(svcProdRepository.findActiveSvcProds(svcMgmtNum))
                )
        );

        Mono<Tuple2<List<SvcProdResponseDto>, Map<String, String>>> combined =
                svcProds.zipWhen(sps ->
                    plmClient.getProdNmByIds(sps.stream().map( SvcProdResponseDto::getProdId ).collect( Collectors.toList() ))
                             .map(plm -> plm.stream()
                                            .collect( Collectors.toMap(ProdResponseDto::getProdId, ProdResponseDto::getProdNm)) )
                );

        return combined.map(c -> {
            List<SvcProdResponseDto> sps = c.getT1();
            Map<String, String> pinm = c.getT2();
            sps.forEach(sp -> sp.setProdNm(pinm.get(sp.getProdId())));
            return sps;
        });
    }
}
