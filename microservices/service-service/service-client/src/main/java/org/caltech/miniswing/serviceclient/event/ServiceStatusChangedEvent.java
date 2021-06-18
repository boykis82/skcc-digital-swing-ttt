package org.caltech.miniswing.serviceclient.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

@Getter
@NoArgsConstructor
@ToString
public class ServiceStatusChangedEvent extends ServiceEvent {
    private  SvcStCd beforeSvcStCd;

    public ServiceStatusChangedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        super(svcMgmtNum);
        this.beforeSvcStCd = beforeSvcStCd;
    }
}
