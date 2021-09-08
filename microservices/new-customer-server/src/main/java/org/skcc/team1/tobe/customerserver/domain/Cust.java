package org.skcc.team1.tobe.customerserver.domain;

import lombok.*;
import org.caltech.miniswing.domain.BaseEntity;
import org.skcc.team1.tobe.customerserver.dto.CustTypCd;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cust")
@ToString
public class Cust extends BaseEntity {

    @Id
    @Column(name = "custNum")
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
    public Cust(long custNum, String custNm, LocalDate custRgstDt, LocalDate birthDt, CustTypCd custTypCd) {
        this.id = custNum;
        this.custNm = custNm;
        this.custRgstDt = custRgstDt;
        this.birthDt = birthDt;
        this.custTypCd = custTypCd;
    }

    public long getCustNum() {
        return id;
    }
}
