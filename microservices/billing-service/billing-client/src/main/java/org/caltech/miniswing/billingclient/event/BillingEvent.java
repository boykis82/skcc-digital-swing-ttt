package org.caltech.miniswing.billingclient.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillingEvent {
    private long svcMgmtNum;
    private long acntNum;
}
