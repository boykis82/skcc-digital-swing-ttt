package org.caltech.miniswing.productclient.dto;

import lombok.*;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class SvcProdResponseDto {
    private long id;
    private long svcMgmtNum;
    private String prodId;
    private String prodNm;
    private SvcProdCd svcProdCd;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scrbDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate termDt;

    @Builder
    public SvcProdResponseDto(long id, long svcMgmtNum, String prodId, String prodNm, LocalDate scrbDt, LocalDate termDt, SvcProdCd svcProdCd) {
        this.id = id;
        this.svcMgmtNum = svcMgmtNum;
        this.prodId = prodId;
        this.prodNm = prodNm;
        this.scrbDt = scrbDt;
        this.termDt = termDt;
        this.svcProdCd = svcProdCd;
    }
}
