package org.caltech.miniswing.serviceserver.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswing.domain.BaseEntity;
import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.IllegalServiceStatusException;
import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "svc")
public class Svc extends BaseEntity {
    @Id
    @Column(name = "svc_mgmt_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String svcNum;

    @Column(nullable = false)
    private LocalDate svcScrbDt;

    @Column
    private LocalDate svcTermDt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=1)
    private SvcCd svcCd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=2)
    private SvcStCd svcStCd;

    @Column(nullable = false)
    private long custNum;

    @Column(nullable = false, length=10)
    private String feeProdId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "svc", cascade = CascadeType.ALL)
    private List<SvcStHst> svcStHsts = new ArrayList<>();

    @Builder
    public Svc(String svcNum, LocalDate svcScrbDt, SvcCd svcCd, SvcStCd svcStCd, long custNum, String feeProdId) {
        this.svcNum    = svcNum;
        this.svcScrbDt = svcScrbDt;
        this.svcStCd   = svcStCd;
        this.svcCd     = svcCd;
        this.custNum   = custNum;
        this.feeProdId = feeProdId;
    }

    public long getSvcMgmtNum() {
        return id;
    }

    public void setFeeProdId(String feeProdId) {
        if (this.svcStCd == SvcStCd.TG || this.svcTermDt != null)
            throw new IllegalServiceStatusException("이미 해지되었는데 요금제 변경 불가!");
        this.feeProdId = feeProdId;
    }

    //-- 서비스 가입
    public void subscribe(LocalDateTime svcScrbDtm) {
        //-- 가입일, 서비스상태 셋팅
        this.svcScrbDt = svcScrbDtm.toLocalDate();
        this.svcStCd = SvcStCd.AC;

        //-- AC 상태 밀어넣고
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.AC, svcScrbDtm) );
    }

    //-- 서비스 해지
    public void terminate(LocalDateTime svcTermDtm) {
        if (this.svcStCd == SvcStCd.TG || this.svcTermDt != null)
            throw new IllegalServiceStatusException("이미 해지되었는데 다시 해지할 수 없음!");

        //-- 해지일, 상태 등 셋팅
        this.svcTermDt = svcTermDtm.toLocalDate();
        this.svcStCd = SvcStCd.TG;

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(svcTermDtm);

        //-- 신규 해지 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.TG, svcTermDtm.plusSeconds(1)) );
    }

    //-- 정지
    public void suspend(LocalDateTime suspDtm) {
        if (this.svcStCd != SvcStCd.AC)
            throw new IllegalServiceStatusException("AC상태가 아닌데 정지걸 수 없음!");
        this.svcStCd = SvcStCd.SP;

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(suspDtm);

        //-- 신규 정지 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.SP, suspDtm.plusSeconds(1)) );
    }

    //-- 활성화
    public void activate(LocalDateTime activeDtm) {
        if (this.svcStCd != SvcStCd.SP)
            throw new IllegalServiceStatusException("SP상태가 아닌데 정지해제할 수 없음!");
        this.svcStCd = SvcStCd.AC;

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(activeDtm);

        //-- 신규 AC 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.AC, activeDtm.plusSeconds(1)) );
    }

    //-- 최종 이력 종료
    private void terminateLastSvcStHst(LocalDateTime termDtm) {
        this.svcStHsts.stream()
                .filter(SvcStHst::isLastHst)
                .findFirst()
                .orElseThrow( () -> {
                    throw new DataIntegrityViolationException("최종 서비스상태 이력이 없음!");
                } )
                .terminate(termDtm);
    }
}
