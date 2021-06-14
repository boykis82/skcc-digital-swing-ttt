package org.caltech.miniswing.plmserver.web;

import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.plmserver.domain.ProdRepository;
import org.caltech.miniswing.plmserver.dto.ProdCreateRequestDto;
import org.caltech.miniswing.plmserver.test.ProdFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false"}
)
public class ProdControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProdRepository prodRepository;

    private final String urlPrefix = "/swing/api/v1/plm";

    @Before
    public void setUp() {
        prodRepository.saveAll(ProdFactory.createManyProds_1());
        prodRepository.saveAll(ProdFactory.createManyProds_2());
        prodRepository.saveAll(ProdFactory.createManyProds_3());
    }

    @After
    public void tearDown() {
        prodRepository.deleteAll();
    }

    @Test
    public void test_상품단건조회() {
        client.get()
                .uri(uriBuilder ->
                        uriBuilder.path(urlPrefix + "/{prodId}")
                                .build("NA00000001"))
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.prodId").isEqualTo("NA00000001")
                    .jsonPath("$.prodNm").isEqualTo("표준요금제")
                    .jsonPath("$.svcProdCd.key").isEqualTo(SvcProdCd.P1.getKey())
                    .jsonPath("$.description").isEqualTo("111")
        ;
    }

    @Test
    public void test_상품전체조회() {
        client.get()
                .uri(urlPrefix)
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(8)
                    .jsonPath("$[0].prodId").isEqualTo("NA00000001")
                    .jsonPath("$[0].prodNm").isEqualTo("표준요금제")
                    .jsonPath("$[0].svcProdCd.key").isEqualTo(SvcProdCd.P1.getKey())
                    .jsonPath("$[0].description").isEqualTo("111")
                    .jsonPath("$[7].prodId").isEqualTo("NA00000008")
                    .jsonPath("$[7].prodNm").isEqualTo("Flo")
                    .jsonPath("$[7].svcProdCd.key").isEqualTo(SvcProdCd.P3.getKey())
                    .jsonPath("$[7].description").isEqualTo("111")
        ;
    }

    @Test
    public void test_상품명으로조회() {
        client.get()
                .uri(uriBuilder ->
                        uriBuilder.path(urlPrefix)
                                .queryParam("prodNm", "부가")
                                .build())
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(2)
                    .jsonPath("$[0].prodId").isEqualTo("NA00000004")
                    .jsonPath("$[1].prodId").isEqualTo("NA00000005")
        ;
    }

    @Test
    public void test_상품ID여러개로조회() {
        client.get()
                .uri(uriBuilder ->
                        uriBuilder.path(urlPrefix)
                                .queryParam("prodIds", "NA00000001,NA00000004,NA00000007")
                                .build())
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(3)
                    .jsonPath("$[0].prodId").isEqualTo("NA00000001")
                    .jsonPath("$[1].prodId").isEqualTo("NA00000004")
                    .jsonPath("$[2].prodId").isEqualTo("NA00000007")
                    .jsonPath("$[0].prodNm").isEqualTo("표준요금제")
                    .jsonPath("$[1].prodNm").isEqualTo("부가요금제1")
                    .jsonPath("$[2].prodNm").isEqualTo("Wavve")
        ;
    }

    @Test
    public void test_상품생성()  {
        ProdCreateRequestDto dto = ProdCreateRequestDto.builder()
                .prodId("NI00000001")
                .prodNm("가나다")
                .svcProdCd(SvcProdCd.P1)
                .description("aaa")
                .build();

        client.post()
                .uri(urlPrefix)
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
        ;
    }
}
