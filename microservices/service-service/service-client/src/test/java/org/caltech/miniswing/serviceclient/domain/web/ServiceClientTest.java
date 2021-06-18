package org.caltech.miniswing.serviceclient.domain.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.caltech.miniswing.serviceclient.ServiceService;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceClientTest {
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
    public void 서비스조회() throws Exception {
        ServiceDto expected = ServiceDto.builder().svcMgmtNum(reqSvcMgmtNum).build();
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).getService(reqSvcMgmtNum)
        )
                .expectNextCount(1)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1");
    }

    @Test
    public void 동일명의서비스조회() throws Exception {
        List<ServiceDto> expected = Arrays.asList(
                ServiceDto.builder().svcMgmtNum(1).build(),
                ServiceDto.builder().svcMgmtNum(2).build()
        );
        mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).getServicesByCustNum(0, 5, 1, true)
        )
                .expectNextCount(2)
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services?cust_num=1&include_term_svc=true&offset=0&limit=5");
    }

    /*
    @Test
    public void 서비스직권해지() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).terminateServiceByAuthority(reqSvcMgmtNum)
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1");

        String body = recordedRequest.getBody().readUtf8();
        assertThat(body).contains("TG");
        assertThat(body).contains("SVC_STATUS_UPDATE");
    }

    @Test
    public void 서비스정지() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).suspendService(reqSvcMgmtNum)
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1");

        String body = recordedRequest.getBody().readUtf8();
        assertThat(body).contains("SP");
        assertThat(body).contains("SVC_STATUS_UPDATE");
    }

    @Test
    public void 서비스활성화() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).activateService(reqSvcMgmtNum)
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1");

        String body = recordedRequest.getBody().readUtf8();
        assertThat(body).contains("AC");
        assertThat(body).contains("SVC_STATUS_UPDATE");
    }

    @Test
    public void 기본요금제변경() throws Exception {
        mockBackEnd.enqueue(new MockResponse());

        StepVerifier.create(
                ServiceService.client( WebClient.create(baseUrl) ).changeBasicProduct(reqSvcMgmtNum, "NA00000005")
        ).verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
        assertThat(recordedRequest.getPath()).isEqualTo("/swing/api/v1/services/1");

        String body = recordedRequest.getBody().readUtf8();
        assertThat(body).contains("NA00000005");
        assertThat(body).contains("FEE_PROD_UPDATE");
    }

     */
}
