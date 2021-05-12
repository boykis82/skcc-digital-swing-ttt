package org.caltech.miniswing.productclient.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class SvcProdResponseDto {
    private long id;
    private String prodId;
    private String prodNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scrbDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate termDt;

    @Builder
    public SvcProdResponseDto(long id, String prodId, String prodNm, LocalDate scrbDt, LocalDate termDt) {
        this.id = id;
        this.prodId = prodId;
        this.prodNm = prodNm;
        this.scrbDt = scrbDt;
        this.termDt = termDt;
    }
}
