package org.caltech.miniswing.productserver.messaging;

import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productclient.event.ProductSubscriptionChangedEvent;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.caltech.miniswing.productserver.service.ProdService;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceclient.event.ServiceTerminatedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false"}
)
public class MessageTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductMessageChannels channels;

    @Autowired
    private MessageCollector collector;

    private AbstractMessageChannel inputFromService;

    BlockingQueue<Message<?>> queueProduct = null;

    String urlPrefix;

    @Autowired
    private SvcProdRepository svcProdRepository;

    @Autowired
    private ProdService prodService;

    private long svcMgmtNum;

    @Before
    public void setUp() {
        queueProduct = getQueue(channels.productOutput());
        urlPrefix = "http://localhost:" + port + "/swing/api/v1/products";

        svcMgmtNum = subscribeSampleSvc();

        inputFromService = (AbstractMessageChannel) channels.serviceInput();

        queueProduct.clear();
    }

    @After
    public void tearDown() {
        svcProdRepository.deleteAll();
    }

    @Test
    public void test_상품가입이벤트발행() {
        ProdSubscribeRequestDto dto = ProdSubscribeRequestDto.builder()
                .svcMgmtNum(1)
                .svcProdCd(SvcProdCd.P1)
                .prodId("NA00000002")
                .build();

        assertThat(queueProduct.size()).isEqualTo(0);

        client.post()
                .uri(urlPrefix + "?svcMgmtNum=1")
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(queueProduct.size()).isEqualTo(1);
    }


    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    @Test
    public void test_서비스해지수신() {
        assertThat( prodService.getServiceProducts(svcMgmtNum, true)
                .block()
                .stream()
                .allMatch( sp -> sp.getTermDt() == null ) ).isEqualTo(true);

        sendServiceTerminatedEvent(svcMgmtNum);

        assertThat( prodService.getServiceProducts(svcMgmtNum, true)
                        .block()
                        .stream()
                        .allMatch( sp -> sp.getTermDt() != null ) ).isEqualTo(true);
    }

    private void sendServiceTerminatedEvent(long svcMgmtNum) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ServiceTerminatedEvent(svcMgmtNum, SvcStCd.SP)
                )).setHeader("type", "ServiceTerminated").build();

        inputFromService.send(msg);
    }

    //-- 서비스 개통 & 상품 2개 가입한 샘플 서비스
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

