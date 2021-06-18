package org.caltech.miniswing.productserver.web;

import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
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
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false"}
)
public class ProdControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private SvcProdRepository svcProdRepository;

    @Autowired
    private WebTestClient client;

    @MockBean(name = "serviceClient")
    private ServiceClient serviceClient;

    @MockBean(name = "plmClient")
    private PlmClient plmClient;

    private String urlPrefix;

    private long svcMgmtNum;

    @Before
    public void setUp() {
        svcMgmtNum = subscribeSampleSvc();

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/products";
    }

    @After
    public void tearDown() {
        svcProdRepository.deleteAll();
    }


    @Test
    public void test_상품가입_부가서비스() {
        String prodId = "NA00000007";

        //-- mock에서 살아있는 서비스 리턴
        given( serviceClient.getService(svcMgmtNum) )
                .willReturn( Mono.just(
                        ServiceDto.builder()
                                .svcMgmtNum(svcMgmtNum)
                                .svcStCd(SvcStCd.AC)
                                .build()
                ) );


        /* messaging 기반으로 변경
        given( serviceClient.changeBasicProduct(svcMgmtNum, prodId) )
                .willReturn(Mono.empty().then());

         */

        ProdSubscribeRequestDto dto = ProdSubscribeRequestDto.builder()
                .svcMgmtNum(svcMgmtNum)
                .prodId(prodId)
                .svcProdCd(SvcProdCd.P3)
                .build();

        client.post()
                .uri(urlPrefix)
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(svcProdRepository.findAllSvcProds(1)).hasSize(3);
    }

    @Test
    public void test_상품가입_기본요금제() {
        String prodId = "NA00000002";

        given( serviceClient.getService(svcMgmtNum) )
                .willReturn( Mono.just(
                        ServiceDto.builder()
                                .svcMgmtNum(svcMgmtNum)
                                .svcStCd(SvcStCd.AC)
                                .build()
                ) );

        /* messaging 기반으로 변경
        given( serviceClient.changeBasicProduct(svcMgmtNum, prodId) )
                .willReturn(Mono.empty().then());

         */

        ProdSubscribeRequestDto dto = ProdSubscribeRequestDto.builder()
                .svcMgmtNum(svcMgmtNum)
                .prodId(prodId)
                .svcProdCd(SvcProdCd.P1)
                .build();

        client.post()
                .uri(urlPrefix)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(svcProdRepository.findActiveSvcProds(1)).hasSize(2);
        assertThat(svcProdRepository.findAllSvcProds(1)).hasSize(3);
    }


    @Test
    public void test_가입상품조회() {
        given( plmClient.getProdNmByIds( Arrays.asList("NA00000001", "NA00000006") ) )
                        .willReturn( Mono.just(Arrays.asList(
                                ProdResponseDto.builder().prodId("NA00000001").prodNm("표준요금제").build(),
                                ProdResponseDto.builder().prodId("NA00000006").prodNm("Flo").build())
                        ));

        client.get()
                .uri(urlPrefix +  "?svcMgmtNum=" + svcMgmtNum + "&includeTermProd=false")
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.length()").isEqualTo(2)
                    .jsonPath("$[0].svcMgmtNum").isEqualTo(svcMgmtNum)
                    .jsonPath("$[0].prodId").isEqualTo("NA00000001")
                    .jsonPath("$[0].prodNm").isEqualTo("표준요금제")
                    .jsonPath("$[1].svcMgmtNum").isEqualTo(svcMgmtNum)
                    .jsonPath("$[1].prodId").isEqualTo("NA00000006")
                    .jsonPath("$[1].prodNm").isEqualTo("Flo");
    }


    @Test
    public void test_상품해지() {
        assertThat(svcProdRepository.findAllSvcProds(svcMgmtNum)).hasSize(2);
        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);
        assertThat(activeSvcProds).hasSize(2);

        given( serviceClient.getService(svcMgmtNum) )
                .willReturn( Mono.just(
                        ServiceDto.builder()
                                .svcMgmtNum(svcMgmtNum)
                                .svcStCd(SvcStCd.AC)
                                .build()
                ) );

        client.delete()
                .uri(urlPrefix +  "/" + activeSvcProds.get(1).getId() + "?svcMgmtNum=" + svcMgmtNum)
                    .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(svcProdRepository.findAllSvcProds(svcMgmtNum)).hasSize(2);
        assertThat(svcProdRepository.findActiveSvcProds(svcMgmtNum)).hasSize(1);
    }

    //-- 서비스 개통 & 상품 3개 가입한 샘플 서비스
    private long subscribeSampleSvc() {
        long svcMgmtNum = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<SvcProd> svcProds = Arrays.asList(
                SvcProd.createNewSvcProd(svcMgmtNum, "NA00000001", SvcProdCd.P1, now),
                SvcProd.createNewSvcProd(svcMgmtNum, "NA00000006", SvcProdCd.P3, now)
        );
        svcProdRepository.saveAll(svcProds);
        return svcMgmtNum;

    }

}
