package org.caltech.miniswing.productserver.domain;

import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SvcProdTest {
    @Autowired
    SvcProdRepository svcProdRepository;

    List<String> prods_p1 = Arrays.asList("NA00000001", "NA00000002", "NA00000003");
    List<String> prods_p2 = Arrays.asList("NA00000004", "NA00000005");
    List<String> prods_p3 = Arrays.asList("NA00000006", "NA00000007", "NA00000008");

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        svcProdRepository.deleteAll();
    }

    @Test
    public void test_서비스상태에따른조회() {
        //-- 기본요금제, 부가요금제, 부가서비스 각각 1개씩 가입한 샘플
        Svc svc = subscribeSampleSvc();
        assertThat(svcProdRepository.findAllSvcProds(svc.getSvcMgmtNum())).hasSize(3);

        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(svc.getSvcMgmtNum());
        assertThat(activeSvcProds).hasSize(3);

        SvcProd suplSvcProd = activeSvcProds.stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P3)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );
        suplSvcProd.terminate(LocalDateTime.now(),false);
        svcProdRepository.save(suplSvcProd);

        assertThat(svcProdRepository.findAllSvcProds(svc.getSvcMgmtNum())).hasSize(3);
        assertThat(svcProdRepository.findActiveSvcProds(svc.getSvcMgmtNum())).hasSize(2);
    }

    @Test
    public void test_상품해지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        SvcProd termSvcProd = svcProdRepository.findActiveSvcProds(svc.getSvcMgmtNum()).stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P3)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );

        termSvcProd.terminate(now, false);
        assertThat(termSvcProd.getEffEndDtm()).isEqualTo(now);
        assertThat(termSvcProd.getTermDt()).isEqualTo(now.toLocalDate());
    }

    @Test(expected = InvalidInputException.class)
    public void test_상품해지_기본요금제_예외발생() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        SvcProd termSvcProd = svcProdRepository.findActiveSvcProds(svc.getSvcMgmtNum()).stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P1)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );

        termSvcProd.terminate(now, false);
        fail("여기 오면 안됨");
    }

    @Test
    public void test_부가서비스추가() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        //-- 다른 부가서비스 가입
        svcProdRepository.saveAll(
                svc.subscribeProduct(prods_p3.get(1), SvcProdCd.P3)
        );

        //-- 상품 총 4개여야 함
        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(svc.getSvcMgmtNum());
        assertThat(activeSvcProds).hasSize(4);

        //-- 종료된 부가서비스 없어야 함
        assertThat(activeSvcProds).allMatch(SvcProd::isActive);

        //-- 새 부가서비스 있어야 함
        assertThat(activeSvcProds).anyMatch( sp -> sp.getProdId().equals(prods_p3.get(1)) );
    }

    @Test(expected = InvalidInputException.class)
    public void test_중복상품가입_예외발생() {
        Svc svc = subscribeSampleSvc();
        svc.subscribeProduct(prods_p3.get(0), SvcProdCd.P3);
        fail("여기 오면 안됨");
    }

    @Test
    public void test_기본요금제_존재하는상태에서_다른기본요금제_가입() {
        Svc svc = subscribeSampleSvc();

        //-- 다른 기본요금제 가입
        svcProdRepository.saveAll(
                svc.subscribeProduct(prods_p1.get(1), SvcProdCd.P1)
        );

        List<SvcProd> allSvcProds = svcProdRepository.findAllSvcProds(svc.getSvcMgmtNum());
        assertThat(allSvcProds).hasSize(4);

        //-- 이전 요금제는 종료
        assertThat( allSvcProds
                .stream()
                .filter(sp -> sp.getProdId().equals(prods_p1.get(0)))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") )
                .getEffEndDtm()
        ).isBefore(LocalDateTime.of(9999,12,31,23,59,59));

        //-- 이후 요금제는 유지
        assertThat( allSvcProds
                .stream()
                .filter(sp -> sp.getProdId().equals(prods_p1.get(1)))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("...") )
                .getEffEndDtm()
        ).isEqualTo(LocalDateTime.of(9999,12,31,23,59,59));
    }

    @Test
    public void test_서비스개통시_상품가입() {
        Svc svc = Svc.builder()
                .svcMgmtNum(1)
                .svcProds(svcProdRepository.findActiveSvcProds(1))
                .build();
        assertThat(svc.getSvcProds()).isEmpty();

        svcProdRepository.saveAll(
                svc.subscribeProduct(prods_p1.get(1), SvcProdCd.P1)
        );

        assertThat(svcProdRepository.findAllSvcProds(svc.getSvcMgmtNum())).hasSize(1);
    }

    //-- 서비스 개통 & 상품 3개 가입한 샘플 서비스
    private Svc subscribeSampleSvc() {
        long svcMgmtNum = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<SvcProd> svcProds = Arrays.asList(
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p1.get(0), SvcProdCd.P1, now),
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p2.get(0), SvcProdCd.P2, now),
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p3.get(0), SvcProdCd.P3, now)
        );
        svcProdRepository.saveAll(svcProds);
        return Svc.builder()
                .svcMgmtNum(svcMgmtNum)
                .svcProds(svcProds)
                .build();
    }
}
