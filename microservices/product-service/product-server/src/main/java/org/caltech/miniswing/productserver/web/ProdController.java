package org.caltech.miniswing.productserver.web;

import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productserver.service.ProdService;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/swing/api/v1/products")
public class ProdController {
    @Autowired
    private ProdService prodService;

    @Autowired
    private ServiceClient serviceClient;

    @Autowired
    private PlmClient plmClient;

    @PostMapping
    public Mono<Void> subscribeProduct(@RequestBody ProdSubscribeRequestDto dto) {
        return serviceClient.getService(dto.getSvcMgmtNum())
                .zipWhen(svc -> {
                    if (svc.getSvcStCd() != SvcStCd.AC && svc.getSvcStCd() != SvcStCd.SP) {
                        return Mono.error(new InvalidInputException("서비스가 해지되어 상품 가입 불가! svc_mgmt_num = " + dto.getSvcMgmtNum()));
                    }
                    if (SvcProdCd.P1 == dto.getSvcProdCd()) {
                        //-- 상품이력 꺾어주고 새로운 상품이력 넣는 것과, 서비스 서버 상의 기본요금제 변경은 상관관계가 없다고 본다. 따라서 when 사용... 인데 나중에 saga 적용하려면 어떻게 해야 하려나
                        return Mono.when(
                                prodService.subscribeProduct(dto),
                                serviceClient.changeBasicProduct(dto.getSvcMgmtNum(), dto.getProdId())
                        ).then();
                    } else {
                        return prodService.subscribeProduct(dto);
                    }
                }, (svc, v) -> v);
    }

    @GetMapping
    public Mono<List<SvcProdResponseDto>> getServiceProducts(@RequestParam(value = "svcMgmtNum") long svcMgmtNum,
                                                             @RequestParam(value = "includeTermProd") boolean includeTermProd) {
        //-- 상품정보가 n개이고 이 n개의 상품명을 가져오기 위해 plm server를 찌른다. 순차 처리해야하므로 zipWhen
        return prodService.getServiceProducts(svcMgmtNum, includeTermProd)
                .zipWhen(svcProds -> plmClient
                                      .getProdNmByIds(svcProds.stream()
                                                              .map( SvcProdResponseDto::getProdId )
                                                              .collect( Collectors.toList() )
                                      )
                                      .map(plm -> plm.stream()
                                                     .collect( Collectors.toMap(ProdResponseDto::getProdId, ProdResponseDto::getProdNm)) )
                                      .zipWhen(prodIdNmMap -> {
                                                  svcProds.forEach(sp -> sp.setProdNm(prodIdNmMap.get(sp.getProdId())));
                                                  return Mono.just(svcProds);
                                              }, (prodIdNmMap, _svcProds) -> _svcProds)
                            , (svcProds, _svcProds) -> _svcProds)
                .log();
    }

    @DeleteMapping("/{svcProdId}")
    public Mono<Void> terminateProduct(@RequestParam("svcMgmtNum") long svcMgmtNum,
                                       @PathVariable("svcProdId") long svcProdId) {
        return serviceClient.getService(svcMgmtNum)
                .zipWhen(svc -> {
                    if (svc.getSvcStCd() != SvcStCd.AC && svc.getSvcStCd() != SvcStCd.SP) {
                        return Mono.error(new InvalidInputException("서비스가 해지되어 상품 해지 불가! svc_mgmt_num = " + svcMgmtNum));
                    }
                    return prodService.terminateProduct(svcMgmtNum, svcProdId);
                }, (svc, v) -> v);
    }

    //-- private api (svc 해지시 호출)
    @DeleteMapping
    public Mono<Void> terminateAllProducts(@RequestParam("svcMgmtNum") long svcMgmtNum) {
        return prodService.terminateAllProducts(svcMgmtNum);
    }
}
