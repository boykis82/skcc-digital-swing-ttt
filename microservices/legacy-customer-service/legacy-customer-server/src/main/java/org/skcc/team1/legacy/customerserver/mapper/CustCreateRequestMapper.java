package org.skcc.team1.legacy.customerserver.mapper;

import org.skcc.team1.legacy.customerserver.dto.CustCreateRequestDto;
import org.skcc.team1.legacy.customerserver.domain.Cust;
import org.caltech.miniswing.mapper.ToEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustCreateRequestMapper {
    @ToEntity
    @Mappings({
            @Mapping(target = "custRgstDt", ignore = true)
    })
    Cust dtoToEntity(CustCreateRequestDto dto);
}
