package org.caltech.miniswing.customerserver.mapper;

import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.caltech.miniswing.customerserver.domain.Cust;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustResponseMapper {
    CustResponseDto entityToDto(Cust entity);
    List<CustResponseDto> entityListToDtoList(List<Cust> entities);
}
