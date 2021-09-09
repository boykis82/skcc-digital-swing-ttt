package org.skcc.team1.legacy.customerserver.service;

import lombok.extern.slf4j.Slf4j;
import org.skcc.team1.legacy.customerserver.dto.CustCreateRequestDto;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerserver.domain.Cust;
import org.skcc.team1.legacy.customerserver.domain.CustRepository;
import org.skcc.team1.legacy.customerserver.mapper.CustCreateRequestMapper;
import org.skcc.team1.legacy.customerserver.mapper.CustResponseMapper;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.caltech.miniswing.util.AsyncHelper;
import org.skcc.team1.legacy.customerserver.messaging.CustomerMessagePublisher;
import org.skcc.team1.legacy.customerserver.messaging.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static reactor.core.publisher.Mono.error;

@Service
@Slf4j
public class CustService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CustRepository custRepository;
    private final CustomerMessagePublisher customerMessagePublisher;

    private final CustResponseMapper custResponseMapper;
    private final CustCreateRequestMapper custCreateRequestMapper;

    private final AsyncHelper asyncHelper;

    @Autowired
    public CustService(AsyncHelper asyncHelper,
                       CustRepository custRepository,
                       CustomerMessagePublisher customerMessagePublisher,
                       CustResponseMapper custResponseMapper,
                       CustCreateRequestMapper custCreateRequestMapper) {
        this.asyncHelper = asyncHelper;
        this.custRepository = custRepository;
        this.customerMessagePublisher = customerMessagePublisher;
        this.custResponseMapper = custResponseMapper;
        this.custCreateRequestMapper = custCreateRequestMapper;
    }

    @Transactional(readOnly = true)
    public Mono<CustResponseDto> getCustomer(long custNum) {
        return asyncHelper.mono( () ->
                Mono.fromCallable( () -> custRepository.findById(custNum))
                        .map(oc -> oc.orElseThrow(() -> new NotFoundDataException("고객이 없습니다.! cust_num = " + custNum)))
                        .map(custResponseMapper::entityToDto)
        );
    }

    @Transactional(readOnly = true)
    public Flux<CustResponseDto> getCustomers(String custNm, LocalDate birthDt) {
        return asyncHelper.flux( () ->
                Flux.fromIterable(custResponseMapper.entityListToDtoList(
                        custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc(custNm, birthDt)))
        );
    }

    @Transactional
    public CustResponseDto createCustomer(CustCreateRequestDto custCreateRequestDto) {

        // Save Data (Transactional Required)
        Cust newCust = custCreateRequestMapper.dtoToEntity(custCreateRequestDto);
        newCust.setCustRgstDt(LocalDate.now());
        newCust = custRepository.save(newCust);

        // Build
        CustResponseDto custResponseDto = newCust.toCustResponseDto();
        logger.trace("SQS Message SENDING: \t" + custResponseDto.toString());

        // Publish
        customerMessagePublisher.sendCustomerCreatedEvent(custResponseDto);
        return custResponseDto;
    }

    @Transactional(readOnly = true)
    public Flux<CustResponseDto> getAllCustomers() {
        return asyncHelper.flux( () ->
                Flux.fromIterable(custResponseMapper.entityListToDtoList(
                        custRepository.findAllDesc()))
        );
    }
}
