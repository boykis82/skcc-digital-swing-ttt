package org.caltech.miniswing.plmserver.web;

import lombok.NoArgsConstructor;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmserver.dto.ProdCreateRequestDto;
import org.caltech.miniswing.plmserver.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/plm")
public class ProdController {
    @Autowired
    private ProdService prodService;

    @GetMapping("/{prodId}")
    public Mono<ProdResponseDto> getProduct(@PathVariable("prodId") String prodId) {
        return prodService.getProduct(prodId);
    }

    @PostMapping
    public ProdResponseDto createProduct(@RequestBody ProdCreateRequestDto dto) {
        return prodService.createProduct(dto);
    }

    @GetMapping
    public Flux<ProdResponseDto> getProducts(@RequestParam(value = "prodNm", required = false) String prodNm,
                                             @RequestParam(value = "prodIds", required = false) List<String> prodIds) {
        if (prodNm != null && prodNm.length() > 0) {
            return prodService.getProducts(prodNm);
        }
        else if (prodIds != null && prodIds.size() > 0) {
            return prodService.getProductsByIds(prodIds);
        }
        else {
            return prodService.getAllProducts();
        }
    }
}
