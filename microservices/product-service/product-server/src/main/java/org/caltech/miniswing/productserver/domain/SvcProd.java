package org.caltech.miniswing.productserver.domain;

import lombok.*;
import org.caltech.miniswing.domain.BaseEntity;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        /*
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"svc_mgmt_num", "prod_id", "eff_sta_dtm", "eff_end_dtm"})
        },*/
        name = "svc_prod",
        indexes = {
                @Index(name = "svc_prod_n1",
                        columnList = "svcMgmtNum, prodId, effEndDtm desc, effStaDtm desc",
                        unique = true)
        }
)
public class SvcProd  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long svcMgmtNum;

    @Column(nullable = false)
    private String prodId;

    @Column(nullable = false, length=2)
    @Enumerated(EnumType.STRING)
    private SvcProdCd svcProdCd;

    @Column(nullable = false)
    private LocalDateTime effStaDtm;

    @Column(nullable = false)
    private LocalDateTime effEndDtm;

    @Column(nullable = false)
    private LocalDate scrbDt;

    @Column
    private LocalDate termDt;

    @Builder
    public SvcProd(long svcMgmtNum,
                   String prodId,
                   SvcProdCd svcProdCd,
                   LocalDateTime effStaDtm,
                   LocalDateTime effEndDtm,
                   LocalDate scrbDt,
                   LocalDate termDt) {
        this.svcMgmtNum = svcMgmtNum;
        this.prodId     = prodId;
        this.svcProdCd  = svcProdCd;
        this.effStaDtm  = effStaDtm;
        this.effEndDtm  = effEndDtm;
        this.scrbDt     = scrbDt;
        this.termDt     = termDt;
    }

    public static SvcProd createNewSvcProd(long svcMgmtNum,
                                           String prodId,
                                           SvcProdCd svcProdCd,
                                           LocalDateTime scrbDtm) {
        return SvcProd.builder()
                .svcMgmtNum(svcMgmtNum)
                .prodId(prodId)
                .svcProdCd(svcProdCd)
                .effEndDtm(LocalDateTime.of(9999,12,31,23,59,59))
                .effStaDtm(scrbDtm)
                .scrbDt(scrbDtm.toLocalDate())
                .build();
    }

    public void terminate(LocalDateTime termDtm, boolean force) {
        if (!force && SvcProdCd.P1 == svcProdCd) {
            throw new InvalidInputException("기본요금제는 해지 불가!");
        }
        if( wasTerminated() ) {
            throw new InvalidInputException("이미 해지된 상품은 또 해지 불가!");
        }
        effEndDtm = termDtm;
        termDt = termDtm.toLocalDate();
    }

    public boolean isActive() {
        return termDt == null;
    }

    public boolean wasTerminated() {
        return termDt != null;
    }

    public boolean isSubscribingProd(String prodId) {
        return this.prodId.equals(prodId) && isActive();
    }

    public boolean isBasicProd() { return this.svcProdCd == SvcProdCd.P1; }
}
