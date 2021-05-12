package org.caltech.miniswing.serviceserver.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswing.domain.BaseEntity;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table( name = "svc_st_hst",
        indexes = {
                @Index( name = "svc_st_hst_n1",
                        columnList = "svc_mgmt_num, eff_end_dtm desc, eff_sta_dtm desc",
                        unique = true)
        }
)
public class SvcStHst  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "svc_mgmt_num")
    private Svc svc;

    @Column(nullable = false, name = "eff_sta_dtm")
    private LocalDateTime effStaDtm;

    @Column(nullable = false, name = "eff_end_dtm")
    private LocalDateTime effEndDtm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=2)
    private SvcStCd svcStCd;

    @Builder
    public SvcStHst(Svc svc, LocalDateTime effStaDtm, LocalDateTime effEndDtm, SvcStCd svcStCd) {
        this.svc       = svc;
        this.effStaDtm = effStaDtm;
        this.effEndDtm = effEndDtm;
        this.svcStCd   = svcStCd;
    }

    public void terminate(LocalDateTime endDtm) {
        this.effEndDtm = endDtm;
    }

    public static SvcStHst createNewSvcStHst(Svc svc, SvcStCd svcStCd, LocalDateTime staDtm) {
        return SvcStHst.builder()
                .svc(svc)
                .svcStCd(svcStCd)
                .effStaDtm(staDtm)
                .effEndDtm(LocalDateTime.of(9999,12,31,23,59,59))
                .build();
    }

    public boolean isLastHst() {
        return effEndDtm.equals(LocalDateTime.of(9999,12,31,23,59,59));
    }
}
