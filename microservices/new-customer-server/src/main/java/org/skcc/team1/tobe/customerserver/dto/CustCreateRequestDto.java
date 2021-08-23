package org.skcc.team1.tobe.customerserver.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustCreateRequestDto {
    private String custNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    private CustTypCd custTypCd;

    @Builder
    public CustCreateRequestDto(String custNm, LocalDate birthDt, CustTypCd custTypCd) {
        this.custNm = custNm;
        this.birthDt = birthDt;
        this.custTypCd = custTypCd;
    }
}
