package org.caltech.miniswing.customerserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.customerserver.dto.CustCreateRequestDto;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.customerserver.domain.Cust;
import org.caltech.miniswing.customerserver.domain.CustRepository;
import org.caltech.miniswing.customerserver.mapper.CustCreateRequestMapper;
import org.caltech.miniswing.customerserver.mapper.CustResponseMapper;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.caltech.miniswing.util.AsyncHelper;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static reactor.core.publisher.Mono.error;

@Service
@Slf4j
public class CustService {
    private final CustRepository custRepository;

    private final CustResponseMapper custResponseMapper;
    private final CustCreateRequestMapper custCreateRequestMapper;

    private final AsyncHelper asyncHelper;

    @Autowired
    public CustService(AsyncHelper asyncHelper,
                       CustRepository custRepository,
                       CustResponseMapper custResponseMapper,
                       CustCreateRequestMapper custCreateRequestMapper) {
        this.asyncHelper             = asyncHelper;
        this.custRepository          = custRepository;
        this.custResponseMapper      = custResponseMapper;
        this.custCreateRequestMapper = custCreateRequestMapper;
    }

    @Transactional(readOnly = true)
    public Mono<CustResponseDto> getCustomer(long custNum) {
        return asyncHelper.mono( () ->
                Mono.fromCallable( () -> custRepository.findById(custNum))
                        .map(oc -> oc.orElseThrow(() -> new NotFoundDataException("고객이 없습니다.! cust_num = " + custNum)))
                        .map(custResponseMapper::entityToDto)
                        .log()
        );
    }

    @Transactional(readOnly = true)
    public Flux<CustResponseDto> getCustomers(String custNm, LocalDate birthDt) {
        return asyncHelper.flux( () ->
                Flux.fromIterable(custResponseMapper.entityListToDtoList(
                        custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc(custNm, birthDt)))
                        .log()
        );
    }

    @Transactional
    public Mono<CustResponseDto> createCustomer(CustCreateRequestDto dto) {
        Cust c = custCreateRequestMapper.dtoToEntity(dto);
        c.setCustRgstDt(LocalDate.now());
        return asyncHelper.mono( () ->
                Mono.fromCallable( () -> custRepository.save(c))
                        .map(custResponseMapper::entityToDto)
                        .log()
        );
    }
}
