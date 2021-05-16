package org.caltech.miniswing.productserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productserver.domain.Svc;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.caltech.miniswing.productserver.mapper.SvcProdResponseMapper;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ProdService {
    private final AsyncHelper asyncHelper;
    private final SvcProdRepository svcProdRepository;
    private final SvcProdResponseMapper svcProdResponseMapper;

    @Autowired
    public ProdService(AsyncHelper asyncHelper,
                       SvcProdRepository svcProdRepository,
                       SvcProdResponseMapper svcProdResponseMapper) {
        this.asyncHelper           = asyncHelper;
        this.svcProdRepository     = svcProdRepository;
        this.svcProdResponseMapper = svcProdResponseMapper;
    }

    @Transactional
    public Mono<Void> subscribeProduct(long svcMgmtNum, ProdSubscribeRequestDto dto) {
        return asyncHelper.mono( () -> Mono.fromCallable( () -> svcProdRepository.findActiveSvcProds(svcMgmtNum)) )
                .zipWhen(svcProds -> {
                    Svc svc = Svc.builder()
                            .svcMgmtNum(svcMgmtNum)
                            .svcProds(svcProds)
                            .build();

                    return asyncHelper.mono( () -> Mono.fromCallable( () ->
                            svcProdRepository.saveAll(svc.subscribeProduct(dto.getProdId(), dto.getSvcProdCd()))) ).then();
                }, (_a_, _b) -> _b).log();
    }

    @Transactional
    public Mono<Void> terminateProduct(long svcMgmtNum, long svcProdId) {
        return asyncHelper.mono( () -> Mono.fromCallable( () -> svcProdRepository.findActiveSvcProds(svcMgmtNum)) )
                .zipWhen(svcProds -> {
                    Svc svc = Svc.builder()
                            .svcMgmtNum(svcMgmtNum)
                            .svcProds(svcProds)
                            .build();

                    return asyncHelper.mono( () -> Mono.fromCallable( () ->
                            svcProdRepository.saveAll(svc.terminateProduct(svcProdId))) ).then().log();
                }, (_a_, _b) -> _b).log();
    }

    @Transactional
    public Mono<Void> terminateAllProducts(long svcMgmtNum) {
        return asyncHelper.mono( () -> Mono.fromCallable( () -> svcProdRepository.findActiveSvcProds(svcMgmtNum)) )
                .zipWhen(svcProds -> {
                    Svc svc = Svc.builder()
                            .svcMgmtNum(svcMgmtNum)
                            .svcProds(svcProds)
                            .build();

                    return asyncHelper.mono( () -> Mono.fromCallable( () ->
                            svcProdRepository.saveAll(svc.terminateAllProducts())) ).then().log();
                }, (_a_, _b) -> _b).log();
    }

    @Transactional(readOnly = true)
    public Mono<List<SvcProdResponseDto>> getServiceProducts(long svcMgmtNum, boolean includeTermProd) {
        return asyncHelper.mono( () ->
                Mono.fromCallable( () -> includeTermProd
                        ? svcProdResponseMapper.entityListToDtoList(svcProdRepository.findAllSvcProds(svcMgmtNum))
                        : svcProdResponseMapper.entityListToDtoList(svcProdRepository.findActiveSvcProds(svcMgmtNum))
                )
                .log()
        );
    }
}
