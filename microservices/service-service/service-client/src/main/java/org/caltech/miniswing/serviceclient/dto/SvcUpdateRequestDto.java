package org.caltech.miniswing.serviceclient.dto;

import lombok.*;

import static org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto.SvcUpdateTyp.SVC_STATUS_UPDATE;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class SvcUpdateRequestDto {
    public enum SvcUpdateTyp {SVC_STATUS_UPDATE, FEE_PROD_UPDATE}

    SvcUpdateTyp svcUpdateTyp;
    SvcStCd afterSvcStCd;
    String afterFeeProdId;

    @Builder
    public SvcUpdateRequestDto(SvcUpdateTyp svcUpdateTyp, SvcStCd afterSvcStCd, String afterFeeProdId) {
        this.svcUpdateTyp = svcUpdateTyp;
        this.afterSvcStCd = afterSvcStCd;
        this.afterFeeProdId = afterFeeProdId;
    }

    public static SvcUpdateRequestDto createSuspendServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.SP)
                .build();
    }

    public static SvcUpdateRequestDto createActivateServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.AC)
                .build();
    }

    public static SvcUpdateRequestDto createTerminateServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.TG)
                .build();
    }

    public static SvcUpdateRequestDto createChangeBasicProdDto(String afterFeeProdId) {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SvcUpdateTyp.FEE_PROD_UPDATE)
                .afterFeeProdId(afterFeeProdId)
                .build();
    }

    public boolean isTerminateSvcDto() {
        return SVC_STATUS_UPDATE == getSvcUpdateTyp() && SvcStCd.TG == getAfterSvcStCd();
    }
}
