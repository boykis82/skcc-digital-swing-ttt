package org.caltech.miniswing.productclient.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ProductSubscriptionChangedEvent extends ProductEvent {
    private List<SvcProdResponseDto> subscribedProds;
    private List<SvcProdResponseDto> terminatedProds;

    public ProductSubscriptionChangedEvent(long svcMgmtNum,
                                           List<SvcProdResponseDto> subscribedProds,
                                           List<SvcProdResponseDto> terminatedProds) {
        super(svcMgmtNum);
        this.subscribedProds = subscribedProds;
        this.terminatedProds = terminatedProds;
    }
}
