package org.caltech.miniswing.productclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductClientTest {
    private static MockWebServer mockBackEnd;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String baseUrl = String.format("http://localhost:%s/swing/api/v1", mockBackEnd.getPort());
    private final long reqSvcMgmtNum = 1L;

    @BeforeClass
    public static void beforeClass() throws IOException  {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterClass
    public static void afterClass() throws IOException  {
        mockBackEnd.shutdown();
    }

    @Test
    public void 가입상품이력조회_해지포함() throws Exception {
        List<SvcProdResponseDto> expected = Arrays.asList(
                SvcProdResponseDto.builder().id(1L).build(),
                SvcProdResponseDto.builder().id(2L).build(),
                SvcProdResponseDto.builder().id(3L).build()
        );
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(
                ProductService.client( WebClient.create(baseUrl) )
                        .getServiceProducts(reqSvcMgmtNum, true)
        )
                .expectNextCount(1)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1/products?includeTermProd=true");
    }

    @Test
    public void 상품가입() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ProductService.client( WebClient.create(baseUrl) )
                        .subscribeProduct(reqSvcMgmtNum, ProdSubscribeRequestDto.builder().prodId("NA00000001").build())
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1/products");
    }

    @Test
    public void 상품단건해지() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ProductService.client( WebClient.create(baseUrl) )
                        .terminateProduct(reqSvcMgmtNum, 2)
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("DELETE");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1/products/2");
    }

    @Test
    public void 상품모두해지() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ProductService.client( WebClient.create(baseUrl) )
                        .terminateAllProducts(reqSvcMgmtNum)
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("DELETE");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1/products");
    }
}
