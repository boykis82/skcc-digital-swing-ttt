package org.caltech.miniswing.productclient.dto;

import lombok.*;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProdSubscribeRequestDto {
    private long svcMgmtNum;
    private String prodId;
    private SvcProdCd svcProdCd;

    @Builder
    public ProdSubscribeRequestDto(long svcMgmtNum,
                                   String prodId,
                                   SvcProdCd svcProdCd) {
        this.svcMgmtNum = svcMgmtNum;
        this.prodId = prodId;
        this.svcProdCd = svcProdCd;
    }
}
