package org.skcc.team1.tobe.customerserver.web;

import org.skcc.team1.tobe.customerserver.dto.CustCreateRequestDto;
import org.skcc.team1.tobe.customerserver.dto.CustResponseDto;
import org.skcc.team1.tobe.customerserver.service.CustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

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
    public CustResponseDto createCustomer(@RequestBody CustCreateRequestDto dto) {
        return custService.createCustomer(dto);
    }
}
