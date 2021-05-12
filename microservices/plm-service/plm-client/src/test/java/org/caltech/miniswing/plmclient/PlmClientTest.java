package org.caltech.miniswing.plmclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PlmClientTest {
    private static MockWebServer mockBackEnd;
    private final ObjectMapper mapper = new ObjectMapper();
    private String baseUrl;

    @BeforeClass
    public static void setUp() throws IOException  {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterClass
    public static void tearDown() throws IOException  {
        mockBackEnd.shutdown();
    }

    @Test
    public void test상품ID로조회() throws Exception {
        List<String> prodIds = Arrays.asList("NA00000001", "NA00000003");
        List<ProdResponseDto> expected = Arrays.asList(
                ProdResponseDto.builder().prodId("NA00000001").prodNm("표준요금제").build(),
                ProdResponseDto.builder().prodId("NA00000003").prodNm("Wavve").build()
        );
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        baseUrl = String.format("http://localhost:%s/swing/api/v1", mockBackEnd.getPort());

        StepVerifier.create(
                PlmService.client(WebClient.create(baseUrl)).getProdNmByIds(prodIds)
        )
                .expectNextCount(1)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/products?prodIds=NA00000001,NA00000003");
    }

}
