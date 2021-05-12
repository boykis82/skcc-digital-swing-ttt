package org.caltech.miniswing.customerserver.mapper;

import org.caltech.miniswing.customerserver.dto.CustCreateRequestDto;
import org.caltech.miniswing.customerserver.domain.Cust;
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
