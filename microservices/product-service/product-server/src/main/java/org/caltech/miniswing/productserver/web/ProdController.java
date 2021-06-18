package org.caltech.miniswing.productserver.web;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/swing/api/v1/products")
public class ProdController {
    @Autowired
    private ProdService prodService;

    @PostMapping
    public void subscribeProduct(@RequestBody ProdSubscribeRequestDto dto) {
        log.info( String.format("[API] subscribeProduct called! dto = [%s]", dto));
        prodService.subscribeProduct(dto);
    }

    @GetMapping
    public Mono<List<SvcProdResponseDto>> getServiceProducts(@RequestParam(value = "svcMgmtNum") long svcMgmtNum,
                                                             @RequestParam(value = "includeTermProd") boolean includeTermProd) {
        log.info( String.format("[API] getServiceProducts called! svcMgmtNum = [%d], includeTermProd = [%b]", svcMgmtNum, includeTermProd));
        return prodService.getServiceProducts(svcMgmtNum, includeTermProd);
    }

    @DeleteMapping("/{svcProdId}")
    public void terminateProduct(@RequestParam("svcMgmtNum") long svcMgmtNum,
                                 @PathVariable("svcProdId") long svcProdId) {
        log.info( String.format("[API] terminateProduct called! svcMgmtNum = [%d], svcProdId = [%d]", svcMgmtNum, svcProdId));
        prodService.terminateProduct(svcMgmtNum, svcProdId);
    }
}
