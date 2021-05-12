package org.caltech.miniswing.productclient.dto;

import lombok.*;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProdSubscribeRequestDto {
    private String prodId;
    private SvcProdCd svcProdCd;

    @Builder
    public ProdSubscribeRequestDto(String prodId,
                                   SvcProdCd svcProdCd) {

        this.prodId = prodId;
        this.svcProdCd = svcProdCd;
    }
}
