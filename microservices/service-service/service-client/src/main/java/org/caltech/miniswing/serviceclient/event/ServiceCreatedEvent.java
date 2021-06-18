package org.caltech.miniswing.serviceclient.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@ToString
public class ServiceCreatedEvent extends ServiceEvent {
    private String feeProdId;
    private LocalDate svcScrbDt;
    private SvcCd svcCd;
    private SvcStCd svcStCd;
    private long custNum;

    @Builder
    public ServiceCreatedEvent(long svcMgmtNum,
                               String feeProdId,
                               LocalDate svcScrbDt,
                               SvcCd svcCd,
                               SvcStCd svcStCd,
                               long custNum) {
        super(svcMgmtNum);
        this.feeProdId = feeProdId;
        this.svcScrbDt = svcScrbDt;
        this.svcCd = svcCd;
        this.svcStCd = svcStCd;
        this.custNum = custNum;
    }
}
