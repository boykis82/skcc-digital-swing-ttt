package org.skcc.team1.tobe.customerserver.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustResponseDto {
    private long custNum;
    private String custNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate custRgstDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    private CustTypCd custTypCd;

    @Builder
    public CustResponseDto(long custNum, String custNm, LocalDate custRgstDt, LocalDate birthDt, CustTypCd custTypCd) {
        this.custNm = custNm;
        this.custNum = custNum;
        this.custRgstDt = custRgstDt;
        this.birthDt = birthDt;
        this.custTypCd = custTypCd;
    }
}
