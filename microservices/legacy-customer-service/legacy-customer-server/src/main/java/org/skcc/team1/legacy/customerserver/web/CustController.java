package org.skcc.team1.legacy.customerserver.web;

import org.skcc.team1.legacy.customerserver.dto.CustCreateRequestDto;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerserver.service.CustService;
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
    public Flux<CustResponseDto> getCustomers(@RequestParam(value = "custNm", required = false) String custNm,
                                              @RequestParam(value = "birthDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDt)  {
        if (custNm.length() == 0)
            return custService.getAllCustomers();
        else
            return custService.getCustomers(custNm, birthDt);
    }

    @PostMapping
    public CustResponseDto createCustomer(@RequestBody CustCreateRequestDto dto) {
        return custService.createCustomer(dto);
    }
}
