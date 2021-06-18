package org.caltech.miniswing.billingclient.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class UnpaidFeeOccurredEvent extends BillingEvent {
    public UnpaidFeeOccurredEvent(long svcMgmtNum, long acntNum) {
        super(svcMgmtNum, acntNum);
    }
}
