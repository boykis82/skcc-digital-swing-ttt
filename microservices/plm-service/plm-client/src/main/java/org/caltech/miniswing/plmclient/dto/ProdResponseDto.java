package org.caltech.miniswing.plmclient.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProdResponseDto {
    private String prodId;
    private String prodNm;
    private SvcProdCd svcProdCd;
    private String description;

    @Builder
    public ProdResponseDto(String prodId, String prodNm, SvcProdCd svcProdCd, String description) {
        this.prodId = prodId;
        this.prodNm = prodNm;
        this.svcProdCd = svcProdCd;
        this.description = description;
    }
}
