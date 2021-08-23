package org.skcc.team1.legacy.customerclient;

import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface CustomerClient {
    Mono<CustResponseDto> getCustomer(long custNum);
    Mono<List<CustResponseDto>> getCustomers(String customerName, LocalDate birthday);
}

