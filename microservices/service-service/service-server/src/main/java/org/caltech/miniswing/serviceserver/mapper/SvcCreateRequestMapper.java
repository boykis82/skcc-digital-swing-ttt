package org.caltech.miniswing.serviceserver.mapper;

import org.caltech.miniswing.mapper.ToEntity;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.dto.ServiceCreateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SvcCreateRequestMapper {
    @ToEntity
    Svc dtoToEntity(ServiceCreateRequestDto dto);
}
