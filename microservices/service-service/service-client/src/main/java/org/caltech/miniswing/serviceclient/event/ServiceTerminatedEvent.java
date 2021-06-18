package org.caltech.miniswing.serviceclient.event;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;

@NoArgsConstructor
@ToString
public class ServiceTerminatedEvent extends ServiceStatusChangedEvent{
    public ServiceTerminatedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        super(svcMgmtNum, beforeSvcStCd);
    }
}
