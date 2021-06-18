package org.caltech.miniswing.serviceserver.messaging;

import org.caltech.miniswing.billingclient.event.UnpaidFeeClearedEvent;
import org.caltech.miniswing.billingclient.event.UnpaidFeeOccurredEvent;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productclient.event.ProductSubscriptionChangedEvent;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceserver.domain.Svc;
import org.caltech.miniswing.serviceserver.domain.SvcRepository;
import org.caltech.miniswing.serviceserver.dto.ServiceCreateRequestDto;
import org.caltech.miniswing.serviceserver.service.SvcService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    private SvcMessageChannels channels;

    @Autowired
    private MessageCollector collector;

    @MockBean(name = "plmClient")
    private PlmClient plmClient;

    private AbstractMessageChannel inputFromProduct;
    private AbstractMessageChannel inputFromBilling;

    BlockingQueue<Message<?>> queueService = null;

    String urlPrefix;

    @Autowired
    private SvcRepository svcRepository;

    @Autowired
    private SvcService svcService;

    Svc s;

    @Before
    public void setUp() {
        queueService = getQueue(channels.serviceOutput());
        urlPrefix = "http://localhost:" + port + "/swing/api/v1/services";

        inputFromBilling = (AbstractMessageChannel) channels.billingInput();
        inputFromProduct = (AbstractMessageChannel) channels.productInput();

        s = subscribeSampleSvc();

        queueService.clear();
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
    }

    @Test
    public void test_서비스생성이벤트발행() {
        ServiceCreateRequestDto dto = ServiceCreateRequestDto.builder()
                .svcNum("01012345667")
                .svcCd(SvcCd.C)
                .custNum(1)
                .feeProdId("NA00000001")
                .build();

        assertThat(queueService.size()).isEqualTo(0);

        client.post()
                .uri(urlPrefix)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
        ;

        assertThat(queueService.size()).isEqualTo(1);
    }

    @Test
    public void test_미납발생이벤트수신() {
        assertThat(s.getSvcStCd()).isEqualTo(SvcStCd.AC);

        sendUnpaidFeeOccurredEvent(s.getSvcMgmtNum(), 1);

        given( plmClient.getProdNmByIds(any()) )
                .willReturn( Mono.just(Collections.singletonList(
                        ProdResponseDto.builder().prodId(s.getFeeProdId()).prodNm("표준요금제").build()))
                );

        assertThat( svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd() ).isEqualTo(SvcStCd.SP);
    }

    @Test
    public void test_미납해소이벤트수신() {
        given( plmClient.getProdNmByIds(any()) )
                .willReturn( Mono.just(Collections.singletonList(
                        ProdResponseDto.builder().prodId(s.getFeeProdId()).prodNm("표준요금제").build()))
                );

        svcService.changeServiceStatus( s.getSvcMgmtNum(), ServiceStatusChangeRequestDto.createSuspendServiceDto() );
        assertThat( svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd() ).isEqualTo(SvcStCd.SP);

        sendUnpaidFeeClearedEvent(s.getSvcMgmtNum(), 1);

        assertThat( svcService.getService(s.getSvcMgmtNum()).block().getSvcStCd() ).isEqualTo(SvcStCd.AC);
    }

    @Test
    public void test_상품변경이벤트수신_기본요금제_FeeProdId변경되어야함() {
        sendProductSubscriptionChangedEvent( s.getSvcMgmtNum(),
                Collections.singletonList(createLightSvcProdResponseDto("NA00000002", SvcProdCd.P1)),
                Collections.singletonList(createLightSvcProdResponseDto("NA00000001", SvcProdCd.P1))
        );

        given( plmClient.getProdNmByIds(any()) )
                .willReturn( Mono.just(Collections.singletonList(
                        ProdResponseDto.builder().prodId(s.getFeeProdId()).prodNm("표준요금제").build()))
                );

        assertThat( svcService.getService(s.getSvcMgmtNum()).block().getFeeProdId() ).isEqualTo("NA00000002");
    }

    @Test
    public void test_상품변경이벤트수신_부가서비스_아무변경없어야함() {
        sendProductSubscriptionChangedEvent( s.getSvcMgmtNum(),
                Collections.singletonList(createLightSvcProdResponseDto("NA00000007", SvcProdCd.P3)),
                Collections.singletonList(createLightSvcProdResponseDto("NA00000008", SvcProdCd.P3))
        );

        given( plmClient.getProdNmByIds(any()) )
                .willReturn( Mono.just(Collections.singletonList(
                        ProdResponseDto.builder().prodId(s.getFeeProdId()).prodNm("표준요금제").build()))
                );

        assertThat( svcService.getService(s.getSvcMgmtNum()).block().getFeeProdId() ).isEqualTo("NA00000001");
    }

    private SvcProdResponseDto createLightSvcProdResponseDto(String prodId, SvcProdCd svcProdCd) {
        return SvcProdResponseDto.builder()
                .prodId(prodId)
                .svcProdCd(svcProdCd)
                .build();
    }

    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    private void sendUnpaidFeeOccurredEvent(long svcMgmtNum, long acntNum) {

        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new UnpaidFeeOccurredEvent(svcMgmtNum, acntNum)
                )).setHeader("type", "UnpaidFeeOccurred").build();

        inputFromBilling.send(msg);
    }

    private void sendUnpaidFeeClearedEvent(long svcMgmtNum, long acntNum) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new UnpaidFeeClearedEvent(svcMgmtNum, acntNum)
                )).setHeader("type", "UnpaidFeeCleared").build();

        inputFromBilling.send(msg);
    }

    private void sendProductSubscriptionChangedEvent(long svcMgmtNum,
                                            List<SvcProdResponseDto> subscribedProducts,
                                            List<SvcProdResponseDto> terminatedProducts) {

        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ProductSubscriptionChangedEvent(svcMgmtNum, subscribedProducts, terminatedProducts)
                )).setHeader("type", "ProductSubscriptionChanged").build();

        inputFromProduct.send(msg);
    }

    //-- 서비스 개통 샘플 서비스
    private Svc subscribeSampleSvc() {
        Svc svc = Svc.builder()
                .svcCd(SvcCd.C)
                .svcNum("01012345678")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.AC)
                .custNum(1)
                .feeProdId("NA00000001")
                .build();

        LocalDateTime now = LocalDateTime.now();

        svc.subscribe(now);

        return svcRepository.save(svc);
    }
}
