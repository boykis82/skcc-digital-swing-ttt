package org.caltech.miniswing.productserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productserver.domain.Prod;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.caltech.miniswing.productserver.mapper.SvcProdResponseMapper;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        List<SvcProd> svcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);
        LocalDateTime now = LocalDateTime.now();

        //-- 동일한 상품 가입되어 있으면 예외 처리
        if ( svcProds.stream().anyMatch(svcProd -> svcProd.isSubscribingProd(dto.getProdId())) )
            throw new InvalidInputException( String.format("동일한 상풍 가입 못함! svc_mgmt_num = [%d], prod_id = [%s]", svcMgmtNum, dto.getProdId()) );

        //-- svc_prod_Cd가 1,2 면 기존 이력 종료해야 함
        if (dto.getSvcProdCd() == SvcProdCd.P1 || dto.getSvcProdCd() == SvcProdCd.P2) {
            //-- svcProd에서 svcProdCd가 1,2인 거 찾아서 이력 종료 후 가입
            List<SvcProd> prevSvcProds = svcProds.stream()
                    .filter( svcProd -> svcProd.getSvcProdCd() == dto.getSvcProdCd() && svcProd.isActive() )
                    .collect(Collectors.toList());

            //-- 살아있는 기본요금제나 부가요금제가 1개 초과면 데이터 정합성 오류
            if (prevSvcProds.size() > 1) {
                throw new DataIntegrityViolationException(String.format("사용중인 기본요금제/부가요금제가 여러 개임! svc_mgmt_num = [%d], svc_prod_Cd = [%s]", svcMgmtNum, dto.getSvcProdCd().getValue()));
            }
            //-- 살아있는 기본요금제나 부가요금제 있으면 종료
            else if (prevSvcProds.size() == 1) {
                prevSvcProds.get(0).terminate(now.minusSeconds(1), true);
            }
        }

        SvcProd newSvcProd = SvcProd.createNewSvcProd(svcMgmtNum, dto.getProdId(), dto.getSvcProdCd(), now);
        return asyncHelper.mono( () ->
                Mono.just(svcProdRepository.save(newSvcProd))
                        .then()
                        .log()
        );
    }

    @Transactional
    public Mono<Void> terminateProduct(long svcMgmtNum, long svcProdId) {
        SvcProd termSvcProd = svcProdRepository.findActiveSvcProds(svcMgmtNum).stream()
                .filter(sp -> sp.getId() == svcProdId)
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(String.format("존재하지 않는 svc_prod_id ! [%ld]", svcProdId)));
        termSvcProd.terminate(LocalDateTime.now(), false);

        return asyncHelper.mono( () ->
                Mono.just(svcProdRepository.save(termSvcProd))
                        .then()
                        .log()
        );
    }

    @Transactional
    public Mono<Void> terminateAllProducts(long svcMgmtNum) {
        LocalDateTime now = LocalDateTime.now();
        List<SvcProd> svcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);
        svcProds.forEach(sp -> sp.terminate(now, true));

        return asyncHelper.mono( () ->
                Mono.just(svcProdRepository.saveAll(svcProds))
                        .then()
                        .log()
        );
    }

    @Transactional(readOnly = true)
    public Mono<List<SvcProdResponseDto>> getServiceProducts(long svcMgmtNum, boolean includeTermProd) {
        return asyncHelper.mono( () ->
                Mono.just(includeTermProd
                        ? svcProdResponseMapper.entityListToDtoList(svcProdRepository.findAllSvcProds(svcMgmtNum))
                        : svcProdResponseMapper.entityListToDtoList(svcProdRepository.findActiveSvcProds(svcMgmtNum))
                )
                .log()
        );
    }
}
