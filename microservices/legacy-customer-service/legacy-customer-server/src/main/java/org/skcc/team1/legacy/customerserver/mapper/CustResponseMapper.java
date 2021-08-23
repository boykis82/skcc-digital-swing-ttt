package org.skcc.team1.legacy.customerserver.mapper;

import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerserver.domain.Cust;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustResponseMapper {
    CustResponseDto entityToDto(Cust entity);
    List<CustResponseDto> entityListToDtoList(List<Cust> entities);
}
