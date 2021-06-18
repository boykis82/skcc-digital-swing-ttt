package org.caltech.miniswing.plmserver.service;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.exception.NotFoundDataException;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmserver.domain.ProdRepository;
import org.caltech.miniswing.plmserver.dto.ProdCreateRequestDto;
import org.caltech.miniswing.plmserver.mapper.ProdCreateRequestMapper;
import org.caltech.miniswing.plmserver.mapper.ProdResponseMapper;
import org.caltech.miniswing.util.AsyncHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ProdService {
    private final AsyncHelper asyncHelper;
    private final ProdRepository prodRepository;
    private final ProdResponseMapper prodResponseMapper;
    private final ProdCreateRequestMapper prodCreateRequestMapper;

    @Autowired
    public ProdService(AsyncHelper asyncHelper,
                       ProdRepository prodRepository,
                       ProdResponseMapper prodResponseMapper,
                       ProdCreateRequestMapper prodCreateRequestMapper) {
        this.asyncHelper = asyncHelper;
        this.prodRepository = prodRepository;
        this.prodResponseMapper = prodResponseMapper;
        this.prodCreateRequestMapper = prodCreateRequestMapper;
    }

    @Transactional(readOnly = true)
    public Mono<ProdResponseDto> getProduct(String prodId) {
        return asyncHelper.mono( () ->
                Mono.fromCallable( () -> prodRepository.findById(prodId))
                        .map(oc -> oc.orElseThrow(() -> new NotFoundDataException("상품이 없습니다.! prod_id = " + prodId)))
                        .map(prodResponseMapper::entityToDto)
                        .log()
        );
    }

    @Transactional(readOnly = true)
    public Flux<ProdResponseDto> getProducts(String prodNm) {
        return asyncHelper.flux( () ->
                Flux.fromIterable(prodResponseMapper.entityListToDtoList(
                        prodRepository.findByProdNmContainingOrderByProdId(prodNm)))
        );
    }

    @Transactional(readOnly = true)
    public Flux<ProdResponseDto> getAllProducts() {
        return asyncHelper.flux( () ->
                Flux.fromIterable(prodResponseMapper.entityListToDtoList(
                        prodRepository.findAll()))
        );
    }

    @Transactional(readOnly = true)
    public Flux<ProdResponseDto> getProductsByIds(List<String> prodIds) {
        return asyncHelper.flux( () ->
                Flux.fromIterable(prodResponseMapper.entityListToDtoList(
                        prodRepository.findByProdIdInOrderByProdId(prodIds)))
        );
    }

    @Transactional
    public ProdResponseDto createProduct(ProdCreateRequestDto dto) {
        return prodResponseMapper.entityToDto(
                prodRepository.save(
                        prodCreateRequestMapper.dtoToEntity(dto)
                )
        );
    }
}
