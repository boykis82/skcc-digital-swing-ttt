package org.skcc.team1.legacy.customerserver.web;

import org.junit.Ignore;
import org.skcc.team1.legacy.customerclient.dto.CustTypCd;
import org.skcc.team1.legacy.customerserver.domain.Cust;
import org.skcc.team1.legacy.customerserver.domain.CustRepository;
import org.skcc.team1.legacy.customerserver.dto.CustCreateRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CustControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    @Autowired
    private CustRepository custRepository;

    private final String urlPrefix = "/swing/api/v1/customers";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        custRepository.deleteAll();
    }

    @Ignore
    public void test_고객생성() throws Exception {
        CustCreateRequestDto dto = CustCreateRequestDto.builder()
                .custNm("강인수")
                .birthDt(LocalDate.of(1982,1,1))
                .custTypCd(CustTypCd.C01)
                .build();

        client.post()
                .uri(uriBuilder -> uriBuilder.path(urlPrefix).build())
                    .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
        ;
    }

    @Test
    public void test_고객조회_고객번호() throws Exception {
        Cust c = custRepository.save(Cust.builder()
                .custNm("강인수")
                .custRgstDt(LocalDate.of(2021,4,28))
                .custTypCd(CustTypCd.C01)
                .birthDt(LocalDate.of(1982,1,1))
                .build());

        client.get()
                .uri(uriBuilder ->
                        uriBuilder.path(urlPrefix + "/{custNum}")
                                .build(c.getCustNum()))
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.custNm").isEqualTo("강인수")
                    .jsonPath("$.birthDt").isEqualTo("1982-01-01")
                    .jsonPath("$.custTypCd.key").isEqualTo(CustTypCd.C01.getKey())
        ;
    }

    @Test
    public void test_고객조회_이름과생일() throws Exception {
        custRepository.save(Cust.builder()
                .custNm("강인수")
                .custRgstDt(LocalDate.of(2021,4,28))
                .custTypCd(CustTypCd.C01)
                .birthDt(LocalDate.of(1982,1,1))
                .build());

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPrefix)
                        .queryParam("custNm", "강인수")
                        .queryParam("birthDt", "1982-01-01")
                        .build())
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$[0].custNm").isEqualTo("강인수")
                    .jsonPath("$[0].birthDt").isEqualTo("1982-01-01")
                    .jsonPath("$[0].custTypCd.key").isEqualTo(CustTypCd.C01.getKey())
        ;
    }

    @Test
    public void test_고객조회_이름과생일_여러명() throws Exception {
        custRepository.saveAll(Arrays.asList(
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2021,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1982,1,1))
                        .build(),
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2018,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1983,1,1))
                        .build(),
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2019,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1982,1,1))
                        .build()
        ));

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPrefix)
                        .queryParam("custNm", "강인수")
                        .queryParam("birthDt", "1982-01-01")
                        .build())
                    .accept(APPLICATION_JSON)
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$[0].custNm").isEqualTo("강인수")
                    .jsonPath("$[0].birthDt").isEqualTo("1982-01-01")
                    .jsonPath("$[0].custRgstDt").isEqualTo("2021-04-28")
                    .jsonPath("$[0].custTypCd.key").isEqualTo(CustTypCd.C01.getKey())
                    .jsonPath("$[1].custNm").isEqualTo("강인수")
                    .jsonPath("$[1].birthDt").isEqualTo("1982-01-01")
                    .jsonPath("$[1].custRgstDt").isEqualTo("2019-04-28")
                    .jsonPath("$[1].custTypCd.key").isEqualTo(CustTypCd.C01.getKey())
        ;
    }
}
