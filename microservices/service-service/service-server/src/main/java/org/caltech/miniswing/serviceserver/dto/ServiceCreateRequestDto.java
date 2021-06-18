package org.caltech.miniswing.serviceserver.dto;

import lombok.*;
import org.caltech.miniswing.serviceclient.dto.SvcCd;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ServiceCreateRequestDto {
    private String svcNum;
    private SvcCd svcCd;
    private long custNum;
    private String feeProdId;

    @Builder
    public ServiceCreateRequestDto(String svcNum, SvcCd svcCd, long custNum, String feeProdId) {
        this.svcNum = svcNum;
        this.svcCd = svcCd;
        this.custNum = custNum;
        this.feeProdId = feeProdId;
    }
}
