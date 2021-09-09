package org.skcc.team1.legacy.customerserver.domain;

import lombok.*;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerclient.dto.CustTypCd;
import org.caltech.miniswing.domain.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cust")
@ToString
public class Cust  extends BaseEntity {
    @Id
    @Column(name = "cust_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 80, nullable = false)
    private String custNm;

    @Column(nullable = false)
    private LocalDate custRgstDt;

    @Column
    private LocalDate birthDt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=3)
    private CustTypCd custTypCd;

    @Builder
    public Cust(String custNm, LocalDate custRgstDt, LocalDate birthDt, CustTypCd custTypCd) {
        this.custNm     = custNm;
        this.custRgstDt = custRgstDt;
        this.birthDt    = birthDt;
        this.custTypCd  = custTypCd;
    }

    public CustResponseDto toCustResponseDto() {
        return CustResponseDto.builder()
                .custNum(getCustNum())
                .custNm(custNm)
                .birthDt(birthDt)
                .custRgstDt(custRgstDt)
                .custTypCd(custTypCd)
                .build();
    }

    public long getCustNum() {
        return id;
    }
}
