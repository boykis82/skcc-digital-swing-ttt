package org.caltech.miniswing.billingclient.event;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class UnpaidFeeClearedEvent extends BillingEvent {
    public UnpaidFeeClearedEvent(long svcMgmtNum, long acntNum) {
        super(svcMgmtNum, acntNum);
    }
}
