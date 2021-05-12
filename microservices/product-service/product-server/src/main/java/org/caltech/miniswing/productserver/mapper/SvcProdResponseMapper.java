package org.caltech.miniswing.productserver.mapper;

import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SvcProdResponseMapper {
    SvcProdResponseDto entityToDto(SvcProd entity);

    List<SvcProdResponseDto> entityListToDtoList(List<SvcProd> entities);
}
