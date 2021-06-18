package org.caltech.miniswing.serviceclient.event;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

@NoArgsConstructor
@ToString
public class ServiceActivatedEvent extends ServiceStatusChangedEvent {
    public ServiceActivatedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        super(svcMgmtNum, beforeSvcStCd);
    }
}
