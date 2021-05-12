package org.caltech.miniswing.serviceserver.mapper;

import org.caltech.miniswing.serviceclient.dto.SvcResponseDto;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SvcResponseMapper {
    SvcResponseDto entityToDto(Svc entity);
    List<SvcResponseDto> entityListToDtoList(List<Svc> entity);
}
