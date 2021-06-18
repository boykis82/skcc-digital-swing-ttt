package org.caltech.miniswing.serviceclient.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ServiceStatusChangeRequestDto {
    SvcStCd afterSvcStCd;

    @Builder
    public ServiceStatusChangeRequestDto(SvcStCd afterSvcStCd) {
        this.afterSvcStCd = afterSvcStCd;
    }

    public static ServiceStatusChangeRequestDto createSuspendServiceDto() {
        return ServiceStatusChangeRequestDto.builder()
                .afterSvcStCd(SvcStCd.SP)
                .build();
    }

    public static ServiceStatusChangeRequestDto createActivateServiceDto() {
        return ServiceStatusChangeRequestDto.builder()
                .afterSvcStCd(SvcStCd.AC)
                .build();
    }

    public static ServiceStatusChangeRequestDto createTerminateServiceDto() {
        return ServiceStatusChangeRequestDto.builder()
                .afterSvcStCd(SvcStCd.TG)
                .build();
    }

    public boolean isTerminateSvcDto() {
        return SvcStCd.TG == getAfterSvcStCd();
    }
}
