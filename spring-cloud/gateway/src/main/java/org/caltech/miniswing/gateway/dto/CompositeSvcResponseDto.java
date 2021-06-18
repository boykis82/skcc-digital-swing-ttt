package org.caltech.miniswing.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompositeSvcResponseDto {
    private final ServiceDto serviceDto;
    private final CustResponseDto custResponseDto;
    private final List<SvcProdResponseDto> svcProdResponseDtos;
}
