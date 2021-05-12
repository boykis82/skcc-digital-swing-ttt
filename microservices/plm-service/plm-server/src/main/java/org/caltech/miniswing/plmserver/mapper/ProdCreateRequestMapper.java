package org.caltech.miniswing.plmserver.mapper;

import org.caltech.miniswing.plmserver.domain.Prod;
import org.caltech.miniswing.plmserver.dto.ProdCreateRequestDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdCreateRequestMapper {
    Prod dtoToEntity(ProdCreateRequestDto dto);
}
