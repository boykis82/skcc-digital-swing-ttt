package org.skcc.team1.legacy.customerclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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


    @Test
    public void test고객명과생일로조회() throws Exception {
        List<CustResponseDto> mockDtos = Arrays.asList(
                CustResponseDto.builder()
                        .custNum(1L)
                        .build(),
                CustResponseDto.builder()
                        .custNum(2L)
                        .build()
        );

        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(mockDtos))
                .addHeader("Content-Type", "application/json"));

        baseUrl = String.format("http://localhost:%s/swing/api/v1", mockBackEnd.getPort());

        StepVerifier.create(
                CustomerService.client(WebClient.create(baseUrl)).getCustomers("kang", LocalDate.of(1982,1,1))
        )
                .expectNextCount(1)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/customers?custNm=kang&birthDt=1982-01-01");
    }

}
