package org.caltech.miniswing.serviceserver.web;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.caltech.miniswing.serviceserver.dto.ServiceCreateRequestDto;
import org.caltech.miniswing.serviceserver.service.SvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/services")
public class SvcController {
    @Autowired
    private SvcService svcService;


    @GetMapping("/{svcMgmtNum}")
    public Mono<ServiceDto> getService(@PathVariable("svcMgmtNum") long svcMgmtNum) {
        log.info( String.format("[API] getService called! svcMgmtNum = [%d]", svcMgmtNum));
        return svcService.getService(svcMgmtNum);
    }

    @GetMapping
    public Mono<List<ServiceDto>> getServicesByCustomer(@RequestParam("custNum") long custNum,
                                                        @RequestParam("offset") int offset,
                                                        @RequestParam("limit") int limit,
                                                        @RequestParam("includeTermSvc") boolean includeTermSvc) {
        log.info( String.format("[API] getServicesByCustomer called! custNum = [%d], offset = [%d], limit = [%d], includeTermSvc = [%b]",
                custNum, offset, limit, includeTermSvc));
        return svcService.getServicesByCustomer(offset, limit, custNum, includeTermSvc);
    }

    @PostMapping
    public ServiceDto createService(@RequestBody ServiceCreateRequestDto dto) {
        log.info( String.format("[API] createService called! dto = [%s]", dto));
        return svcService.createService(dto);
    }

    @PutMapping("/{svcMgmtNum}/status")
    public void changeServiceStatus(@PathVariable("svcMgmtNum") long svcMgmtNum,
                                    @RequestBody ServiceStatusChangeRequestDto dto) {
        log.info( String.format("[API] changeServiceStatus called! svcMgmtNum = [%d], dto = [%s]", svcMgmtNum, dto));
        svcService.changeServiceStatus(svcMgmtNum, dto);
    }
}
