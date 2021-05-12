package org.caltech.miniswing.serviceserver.mapper;

import org.caltech.miniswing.mapper.ToEntity;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.dto.SvcCreateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SvcCreateRequestMapper {
    @ToEntity
    Svc dtoToEntity(SvcCreateRequestDto dto);
}
