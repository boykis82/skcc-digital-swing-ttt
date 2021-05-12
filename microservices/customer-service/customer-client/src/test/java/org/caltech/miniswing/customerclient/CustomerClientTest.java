package org.caltech.miniswing.customerclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerClientTest {
    private static MockWebServer mockBackEnd;
    private ObjectMapper mapper = new ObjectMapper();
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
    public void test고객조회() throws Exception {
        CustResponseDto mockDto = CustResponseDto.builder()
                .custNum(1L)
                .build();
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(mockDto))
                .addHeader("Content-Type", "application/json"));

        baseUrl = String.format("http://localhost:%s/swing/api/v1", mockBackEnd.getPort());

        StepVerifier.create(
                CustomerService.client(WebClient.create(baseUrl)).getCustomer(1)
        )
                .expectNextMatches(cust -> 1L == cust.getCustNum())
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/customers/1");
    }

}
