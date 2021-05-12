package org.caltech.miniswing.plmserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;

@Getter
@Setter
@Builder
@ToString
public class ProdCreateRequestDto {
    private String prodId;
    private String prodNm;
    private SvcProdCd svcProdCd;
    private String description;
}
