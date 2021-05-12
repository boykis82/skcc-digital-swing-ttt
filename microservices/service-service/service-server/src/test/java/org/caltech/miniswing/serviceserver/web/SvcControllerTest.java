package org.caltech.miniswing.serviceserver.web;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.productclient.ProductClient;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.domain.SvcRepository;
import org.caltech.miniswing.serviceserver.dto.SvcCreateRequestDto;
import org.caltech.miniswing.serviceserver.service.SvcService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SvcControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private SvcRepository svcRepository;

    @Autowired
    private WebTestClient client;

    @MockBean(name = "plmClient")
    private PlmClient plmClient;

    @MockBean(name = "productClient")
    private ProductClient productClient;

    @Autowired
    private SvcService svcService;

    private String urlPrefix;

    private long custNum = 1;

    private List<String> prods;

    @Before
    public void setUp() {
        prods = Arrays.asList("NA00000001", "NA00000002", "NA00000003");

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/services";
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
    }

    @Test
    public void test_서비스생성() throws Exception {
        SvcCreateRequestDto dto = SvcCreateRequestDto.builder()
                .svcNum("01012345667")
                .svcCd(SvcCd.C)
                .custNum(custNum)
                .feeProdId(prods.get(0))
                .build();

        given( productClient.subscribeProduct(anyLong(), any(ProdSubscribeRequestDto.class)) )
                .willReturn(Mono.empty().then());

        client.post()
                .uri(urlPrefix)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(svcRepository.findAll()).hasSize(1);
    }

    @Test
    public void test_서비스정지() throws Exception {
        Svc s = subscribeSampleSvc();

        long svcMgmtNum = svcRepository.findAll().get(0).getSvcMgmtNum();

        client.put()
                .uri(urlPrefix + "/" + s.getSvcMgmtNum())
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(SvcUpdateRequestDto.createSuspendServiceDto()))
                .exchange()
                    .expectStatus().isOk()
        ;

        assertThat(svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd()).isEqualTo(SvcStCd.SP);
    }

    @Test
    public void test_서비스활성화() throws Exception {
        Svc s = subscribeSampleSvc();
        s.suspend(LocalDateTime.now().minusDays(3));
        svcRepository.save(s);

        client.put()
                .uri(urlPrefix + "/" + s.getSvcMgmtNum())
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(SvcUpdateRequestDto.createActivateServiceDto()))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd()).isEqualTo(SvcStCd.AC);
    }

    @Test
    public void test_서비스해지() throws Exception {
        Svc s = subscribeSampleSvc();

        given( productClient.terminateAllProducts(s.getSvcMgmtNum()) )
                .willReturn(Mono.empty().then());

        client.put()
                .uri(urlPrefix + "/" + s.getSvcMgmtNum())
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(SvcUpdateRequestDto.createTerminateServiceDto()))
                .exchange()
                    .expectStatus().isOk()
        ;

        assertThat(svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd()).isEqualTo(SvcStCd.TG);
    }

    @Test
    public void 기본요금제변경() throws Exception {
        Svc s = subscribeSampleSvc();
        String afterProdId = "NA00000003";

        given( productClient.subscribeProduct(anyLong(), any(ProdSubscribeRequestDto.class)) )
                .willReturn(Mono.empty().then());

        client.put()
                .uri(urlPrefix + "/" + s.getSvcMgmtNum())
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(SvcUpdateRequestDto.createChangeBasicProdDto(afterProdId)))
                .exchange()
                    .expectStatus().isOk()
        ;

        assertThat(svcService.getService(s.getSvcMgmtNum()).block().getFeeProdId()).isEqualTo(afterProdId);
    }

    @Test
    public void test_서비스조회() throws Exception {
        Svc s = subscribeSampleSvc();

        String url = urlPrefix + "/" + s.getSvcMgmtNum();

        given( plmClient.getProdNmByIds(Collections.singletonList(prods.get(0))) )
                .willReturn( Mono.just(Collections.singletonList(
                        ProdResponseDto.builder().prodId(prods.get(0)).prodNm("표준요금제").build()))
                );

        client.get()
                .uri(urlPrefix + "/" + s.getSvcMgmtNum())
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.svcMgmtNum").isEqualTo(s.getSvcMgmtNum())
                    .jsonPath("$.svcNum").isEqualTo(s.getSvcNum())
                    .jsonPath("$.feeProdId").isEqualTo(s.getFeeProdId())
                    .jsonPath("$.feeProdNm").isEqualTo("표준요금제");
    }

    private Svc subscribeSampleSvc() {
        Svc s = Svc.builder()
                .svcCd(SvcCd.C)
                .svcStCd(SvcStCd.AC)
                .svcNum("0101234567")
                .svcScrbDt(LocalDate.now())
                .feeProdId(prods.get(0))
                .custNum(custNum)
                .build();
        s.subscribe(LocalDateTime.now());
        return svcRepository.save(s);
    }
}


