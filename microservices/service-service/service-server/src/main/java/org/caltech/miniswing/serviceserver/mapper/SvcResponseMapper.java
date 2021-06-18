package org.caltech.miniswing.serviceserver.mapper;

import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SvcResponseMapper {
    ServiceDto entityToDto(Svc entity);
    List<ServiceDto> entityListToDtoList(List<Svc> entity);
}
