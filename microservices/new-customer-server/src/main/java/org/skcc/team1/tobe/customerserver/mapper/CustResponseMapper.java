package org.skcc.team1.tobe.customerserver.mapper;

import org.mapstruct.Mapper;
import org.skcc.team1.tobe.customerserver.domain.Cust;
import org.skcc.team1.tobe.customerserver.dto.CustResponseDto;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CustResponseMapper {
    CustResponseDto entityToDto(Cust entity);
    List<CustResponseDto> entityListToDtoList(List<Cust> entities);
}
