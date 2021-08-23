package org.caltech.miniswing.gateway.web;

import org.skcc.team1.legacy.customerclient.CustomerClient;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.gateway.dto.CompositeCustomerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@RestController
@RequestMapping("/swing/api/v1")
public class CompositeApi {

    @Autowired
    private CustomerClient customerClient;

    @GetMapping("/composite-api/{custNum}")
    public Mono<CompositeCustomerResponseDto> getCompositeCustomers(@PathVariable("custNum") long custNum) {
        Mono<CustResponseDto> customerResponseDtoMono = customerClient.getCustomer(custNum);

        Mono<List<CustResponseDto>> sameNameAndBirthdayCustomersResponseDtosMono = customerResponseDtoMono.zipWhen(
                c -> customerClient.getCustomers(c.getCustNm(), c.getBirthDt()),
                (c, c2) -> c2
        );

        Mono<Tuple2<CustResponseDto, List<CustResponseDto>>> combined =
                Mono.zip(customerResponseDtoMono, sameNameAndBirthdayCustomersResponseDtosMono);

        return combined.map(this::createCompositeCustomerResponseDto);
    }

    private CompositeCustomerResponseDto createCompositeCustomerResponseDto(Tuple2<CustResponseDto, List<CustResponseDto>> tuple) {
        return new CompositeCustomerResponseDto(tuple.getT1(), tuple.getT2());
    }
}