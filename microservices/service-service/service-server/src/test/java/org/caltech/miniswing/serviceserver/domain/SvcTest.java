package org.caltech.miniswing.serviceserver.domain;

import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(     //-- mysql57dialect로 변경한 뒤에는 datajpatest가 동작을 안한다.. 뭘까.
        properties = {"eureka.client.enabled=false"}
)
public class SvcTest {
    @Autowired
    SvcRepository svcRepository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
    }

    @Test
    public void test_서비스를_서비스번호와_서비스상태로_검색() {
        List<Svc> svcs = Arrays.asList(
                Svc.builder()
                        .svcCd(SvcCd.C)
                        .svcNum("01012345678")
                        .svcScrbDt(LocalDate.now())
                        .svcStCd(SvcStCd.AC)
                        .custNum(1)
                        .feeProdId("NA00000001")
                        .build(),
                Svc.builder()
                        .svcCd(SvcCd.I)
                        .svcNum("7132423123")
                        .svcScrbDt(LocalDate.now())
                        .svcStCd(SvcStCd.SP)
                        .custNum(2)
                        .feeProdId("NA00000001")
                        .build(),
                Svc.builder()
                        .svcCd(SvcCd.I)
                        .svcNum("01123231232")
                        .svcScrbDt(LocalDate.now())
                        .svcStCd(SvcStCd.AC)
                        .custNum(3)
                        .feeProdId("NA00000001")
                        .build(),
                Svc.builder()
                        .svcCd(SvcCd.I)
                        .svcNum("7231311231")
                        .svcScrbDt(LocalDate.now())
                        .svcStCd(SvcStCd.TG)
                        .custNum(4)
                        .feeProdId("NA00000001")
                        .build())
                ;

        svcRepository.saveAll(svcs);

        //-- 존재하는 서비스 테스트
        Optional<Svc> fonudSvc = svcRepository.findBySvcNumAndSvcStCd("7132423123", SvcStCd.SP);
        assertThat(fonudSvc.isPresent()).isTrue();

        //-- 서비스번호가 존재하지만 서비스상태가 다른 경우 테스트
        assertThat(svcRepository.findBySvcNumAndSvcStCd("7132423123", SvcStCd.AC).isPresent()).isFalse();
        //-- 서비스번호가 존재하지 않는 경우 테스트
        assertThat(svcRepository.findBySvcNumAndSvcStCd("7132423124", SvcStCd.AC).isPresent()).isFalse();
    }

    @Test
    public void test_신규가입() {
        Svc svc = subscribeSampleSvc();
        assertThat(svc.getFeeProdId()).isEqualTo("NA00000001");
        assertThat(svc.getSvcStHsts()).hasSize(1);
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.AC);
        assertThat(svc.getSvcScrbDt()).isEqualTo(LocalDate.now());
    }

    @Test
    public void test_서비스정지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();
        svc.suspend(now);

        //-- 현재 상태가 SP?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.SP);

        //-- 최종이력의 유효시작일시가 now + 1초 and SP?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh -> ssh.isLastHst() && ssh.getSvcStCd() == SvcStCd.SP && ssh.getEffStaDtm().equals(now.plusSeconds(1)))
        ).isTrue();
    }

    @Test
    public void test_서비스활성화() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        //-- 어제 정지
        svc.suspend(now.minusDays(1));

        //-- 오늘 활성화
        svc.activate(now);

        //-- 현재 상태가 AC?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.AC);

        //-- 최종이력의 유효시작일시가 now + 1초 && AC?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh -> ssh.isLastHst() && ssh.getSvcStCd() == SvcStCd.AC && ssh.getEffStaDtm().equals(now.plusSeconds(1)))
        ).isTrue();
    }

    @Test
    public void test_서비스해지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysLater = now.plusDays(2);

        //-- 이틀 뒤 해지로 가정
        svc.terminate(twoDaysLater);

        //-- 현재 상태가 TG?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.TG);

        //-- 최종이력의 유효시작일시가 now + 1초 && TG?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh ->
                        ssh.isLastHst() &&
                        ssh.getSvcStCd() == SvcStCd.TG &&
                                ssh.getEffStaDtm().equals(twoDaysLater.plusSeconds(1)))
        ).isTrue();
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
