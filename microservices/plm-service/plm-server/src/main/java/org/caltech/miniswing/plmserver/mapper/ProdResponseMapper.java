package org.caltech.miniswing.plmserver.mapper;

import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmserver.domain.Prod;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdResponseMapper {
    ProdResponseDto entityToDto(Prod entity);
    List<ProdResponseDto> entityListToDtoList(List<Prod> entity);
}
