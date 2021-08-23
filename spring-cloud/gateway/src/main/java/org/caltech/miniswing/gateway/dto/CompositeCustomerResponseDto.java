package org.caltech.miniswing.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompositeCustomerResponseDto {
    private final CustResponseDto custResponseDto;
    private final List<CustResponseDto> sameNameAndBirthdayCustsResponseDtos;
}
