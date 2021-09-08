package org.skcc.team1.legacy.customerserver.web;

import lombok.RequiredArgsConstructor;
import org.skcc.team1.legacy.customerserver.dto.CustCreateRequestDto;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerserver.exception.TestException;
import org.skcc.team1.legacy.customerserver.service.CustService;
import org.skcc.team1.legacy.customerserver.service.TestExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/swing/api/v1/customers")
@RequiredArgsConstructor
public class CustController {


    private final CustService custService;
    private final TestExceptionService testExceptionService;

    @GetMapping("/{custNum}")
    public Mono<CustResponseDto> getCustomer(@PathVariable("custNum") long custNum) {
        return custService.getCustomer(custNum);
    }

    @GetMapping("/exception")
    public void exceptionHandler(@RequestParam("custNum") String custNum){

        testExceptionService.testExceptionTestFunction(custNum);
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
