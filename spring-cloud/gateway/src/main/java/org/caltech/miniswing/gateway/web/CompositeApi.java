package org.caltech.miniswing.gateway.web;

import org.caltech.miniswing.customerclient.CustomerClient;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.gateway.dto.CompositeSvcResponseDto;
import org.caltech.miniswing.productclient.ProductClient;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;

@RestController
@RequestMapping("/swing/api/v1")
public class CompositeApi {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private ServiceClient serviceClient;

    @Autowired
    private CustomerClient customerClient;

    @GetMapping("/composite-service/{svcMgmtNum}")
    public Mono<CompositeSvcResponseDto> getCompositeService(@PathVariable("svcMgmtNum") long svcMgmtNum) {
        Mono<ServiceDto> svcResponseDtoMono = serviceClient.getService(svcMgmtNum);

        Mono<List<SvcProdResponseDto>> svcProdResponseDtosMono = productClient.getServiceProducts(svcMgmtNum, false);

        Mono<CustResponseDto> custResponseDtoMono = svcResponseDtoMono.zipWhen(
                s -> customerClient.getCustomer(s.getCustNum()),
                (s, c) -> c
        );

        Mono<Tuple3<ServiceDto, CustResponseDto, List<SvcProdResponseDto>>> combined =
                Mono.zip(svcResponseDtoMono, custResponseDtoMono, svcProdResponseDtosMono);

        return combined.map(this::createCompositeSvcResponseDto);
    }

    private CompositeSvcResponseDto createCompositeSvcResponseDto(Tuple3<ServiceDto, CustResponseDto, List<SvcProdResponseDto>> tuple) {
        return new CompositeSvcResponseDto(tuple.getT1(), tuple.getT2(), tuple.getT3());
    }
}