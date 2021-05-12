package org.caltech.miniswing.serviceclient.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class SvcResponseDto {
    private long svcMgmtNum;
    private String svcNum;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate svcScrbDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate svcTermDt;

    private SvcCd svcCd;
    private SvcStCd svcStCd;

    private String feeProdId;
    private String feeProdNm;

    @Builder
    public SvcResponseDto(long svcMgmtNum,
                          String svcNum,
                          LocalDate svcScrbDt,
                          LocalDate svcTermDt,
                          SvcCd svcCd,
                          SvcStCd svcStCd,
                          String feeProdId,
                          String feeProdNm) {
        this.svcMgmtNum = svcMgmtNum;
        this.svcNum = svcNum;
        this.svcScrbDt = svcScrbDt;
        this.svcTermDt = svcTermDt;
        this.svcCd = svcCd;
        this.svcStCd = svcStCd;
        this.feeProdId = feeProdId;
        this.feeProdNm = feeProdNm;
    }
}
