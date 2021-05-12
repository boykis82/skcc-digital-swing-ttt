package org.caltech.miniswing.customerserver.web;

import org.caltech.miniswing.customerserver.dto.CustCreateRequestDto;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.customerserver.service.CustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/swing/api/v1/customers")
public class CustController {
    @Autowired
    private CustService custService;

    @GetMapping("/{custNum}")
    public Mono<CustResponseDto> getCustomer(@PathVariable("custNum") long custNum) {
        return custService.getCustomer(custNum);
    }

    @GetMapping
    public Flux<CustResponseDto> getCustomers(@RequestParam("custNm") String custNm,
                                              @RequestParam("birthDt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDt)  {
        return custService.getCustomers(custNm, birthDt);
    }

    @PostMapping
    public Mono<CustResponseDto> createCustomer(@RequestBody CustCreateRequestDto dto) {
        return custService.createCustomer(dto);
    }
}
