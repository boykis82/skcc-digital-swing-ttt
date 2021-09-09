package org.skcc.team1.tobe.customerserver.dto;

import lombok.*;
import org.skcc.team1.tobe.customerserver.domain.Cust;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustCreateRequestDto {
    private long custNum;

    private String custNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    private CustTypCd custTypCd;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate custRgstDt;

    @Builder
    public CustCreateRequestDto(long custNum, String custNm, LocalDate birthDt, CustTypCd custTypCd, LocalDate custRgstDt) {
        this.custNum = custNum;
        this.custNm = custNm;
        this.birthDt = birthDt;
        this.custTypCd = custTypCd;
        this.custRgstDt = custRgstDt;
    }

    public Cust toEntity() {
        return Cust.builder()
                .custNm(custNm)
                .custNum(custNum)
                .custRgstDt(custRgstDt)
                .custTypCd(custTypCd)
                .birthDt(birthDt)
                .build();
    }

}
