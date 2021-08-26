package org.skcc.team1.tobe.customerserver.mapper;

import org.caltech.miniswing.mapper.ToEntity;
import org.mapstruct.Mapper;
import org.skcc.team1.tobe.customerserver.domain.Cust;
import org.skcc.team1.tobe.customerserver.dto.CustCreateRequestDto;

@Mapper(componentModel = "spring")
public interface CustCreateRequestMapper {
    @ToEntity
    Cust dtoToEntity(CustCreateRequestDto dto);
}
