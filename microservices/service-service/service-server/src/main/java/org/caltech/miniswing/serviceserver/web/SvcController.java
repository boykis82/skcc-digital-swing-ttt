package org.caltech.miniswing.serviceserver.web;

import lombok.NoArgsConstructor;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.ProductClient;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.serviceclient.dto.SvcResponseDto;
import org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto;
import org.caltech.miniswing.serviceserver.dto.SvcCreateRequestDto;
import org.caltech.miniswing.serviceserver.service.SvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto.SvcUpdateTyp.FEE_PROD_UPDATE;

@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/services")
public class SvcController {
    @Autowired
    private SvcService svcService;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PlmClient plmClient;

    @GetMapping("/{svcMgmtNum}")
    public Mono<SvcResponseDto> getService(@PathVariable("svcMgmtNum") long svcMgmtNum) {
        //-- 서비스정보의 feeprodnm을 채워주기 위해 plm서버로 찌른다. 순차 처리해야 하므로 zipWhen
        return svcService.getService(svcMgmtNum)
                .zipWhen(s -> plmClient.getProdNmByIds(Collections.singletonList(s.getFeeProdId()))
                                       .map(plm -> plm.stream()
                                                      .collect( Collectors.toMap(ProdResponseDto::getProdId, ProdResponseDto::getProdNm)) )
                                       .zipWhen(prodIdNmMap -> {
                                            s.setFeeProdNm(prodIdNmMap.get(s.getFeeProdId()));
                                            return Mono.just(s);
                                        }, (prodIdNmMap, _s) -> _s)
                        , (s, _s) -> _s)
                .log();
    }

    @PostMapping
    public Mono<SvcResponseDto> createService(@RequestBody SvcCreateRequestDto dto) {
        //-- 서비스 생성 후 서비스관리번호를 채번해야 product server로 상품가입요청을 보낼 수 있다. 순차 처리해야 하므로 zipWhen
        return svcService.createService(dto)
                         .zipWhen(svc -> productClient.subscribeProduct(ProdSubscribeRequestDto.builder()
                                 .svcMgmtNum(svc.getSvcMgmtNum())
                                 .prodId(dto.getFeeProdId())
                                 .svcProdCd(SvcProdCd.P1)
                                 .build()
                         ).then(), (svc, v) -> svc)
                ;
    }

    //-- 일부는 private api (svc 해지시 호출)
    @PutMapping("/{svcMgmtNum}")
    public Mono<Void> updateService(@PathVariable("svcMgmtNum") int svcMgmtNum, @RequestBody SvcUpdateRequestDto dto) {
        //-- 서비스 해지처리하는 것과 종속된 상품 모두 종료처리하는 것 사이에 상관관계가 없음. 따라서 when
        return Mono.when(svcService.updateService(svcMgmtNum, dto),
                         dto.isTerminateSvcDto()
                            ? productClient.terminateAllProducts(svcMgmtNum)
                            : Mono.empty()
        ).log();
    }
}
