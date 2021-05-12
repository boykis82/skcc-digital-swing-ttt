package org.caltech.miniswing.serviceserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswing.serviceclient.dto.SvcCd;

@NoArgsConstructor
@Setter
@Getter
public class SvcCreateRequestDto {
    private String svcNum;
    private SvcCd svcCd;
    private long custNum;
    private String feeProdId;

    @Builder
    public SvcCreateRequestDto(String svcNum, SvcCd svcCd, long custNum, String feeProdId) {
        this.svcNum = svcNum;
        this.svcCd = svcCd;
        this.custNum = custNum;
        this.feeProdId = feeProdId;
    }
}
