package org.caltech.miniswing.productserver.domain;

import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
@Sql({"/prod_unittest.sql"})
public class ProdTest {
    @Autowired
    SvcProdRepository svcProdRepository;

    @Autowired
    ProdRepository prodRepository;

    List<Long> custs;
    List<Prod> prods_p1;
    List<Prod> prods_p2;
    List<Prod> prods_p3;

    @Before
    public void setUp() {
        //-- cust pool
        custs = Arrays.asList(1L, 2L, 3L, 4L);

        //-- prod pool
        prods_p1 = prodRepository.findBySvcProdCdOrderByProdId(SvcProdCd.P1);
        prods_p2 = prodRepository.findBySvcProdCdOrderByProdId(SvcProdCd.P2);
        prods_p3 = prodRepository.findBySvcProdCdOrderByProdId(SvcProdCd.P3);
    }

    @After
    public void tearDown() {
        svcProdRepository.deleteAll();
        prodRepository.deleteAll();
    }

    @Test
    public void test_서비스상태에따른조회() {
        //-- 기본요금제, 부가요금제, 부가서비스 각각 1개씩 가입한 샘플
        long svcMgmtNum = subscribeSampleSvc();
        assertThat(svcProdRepository.findAllSvcProds(svcMgmtNum)).hasSize(3);

        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);
        assertThat(activeSvcProds).hasSize(3);

        SvcProd suplSvcProd = activeSvcProds.stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P3)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );
        suplSvcProd.terminate(LocalDateTime.now(),false);

        assertThat(svcProdRepository.findAllSvcProds(svcMgmtNum)).hasSize(3);
        assertThat(svcProdRepository.findActiveSvcProds(svcMgmtNum)).hasSize(2);
    }

    @Test
    public void test_상품해지() {
        long svcMgmtNum = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        SvcProd termSvcProd = svcProdRepository.findActiveSvcProds(svcMgmtNum).stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P3)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );

        termSvcProd.terminate(now, false);
        assertThat(termSvcProd.getEffEndDtm()).isEqualTo(now);
        assertThat(termSvcProd.getTermDt()).isEqualTo(now.toLocalDate());
    }

    @Test(expected = InvalidInputException.class)
    public void test_상품해지_기본요금제_예외발생() {
        long svcMgmtNum = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        SvcProd termSvcProd = svcProdRepository.findActiveSvcProds(svcMgmtNum).stream()
                .filter(sp -> sp.getSvcProdCd() == SvcProdCd.P1)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") );

        termSvcProd.terminate(now, false);
        fail("여기 오면 안됨");
    }

    @Test
    public void test_부가서비스추가() {
        long svcMgmtNum = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        //-- 다른 부가서비스 가입
        long svcProdId = svcProdRepository.save( SvcProd.createNewSvcProd(svcMgmtNum, prods_p3.get(1).getProdId(), SvcProdCd.P3, now) ).getId();

        //-- 상품 총 4개여야 함
        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(svcMgmtNum);
        assertThat(activeSvcProds).hasSize(4);

        //-- 종료된 부가서비스 없어야 함
        assertThat(activeSvcProds
                .stream()
                .anyMatch( sp -> sp.getEffEndDtm().isBefore(LocalDateTime.of(9999,12,31,23,59,59)) )
        ).isFalse();

        //-- 새 부가서비스 있어야 함
        assertThat(activeSvcProds
                .stream()
                .anyMatch( sp -> sp.getProdId().equals(prods_p3.get(1).getProdId()) )
        ).isTrue();
    }

    //-- 서비스 개통 & 상품 3개 가입한 샘플 서비스
    private long subscribeSampleSvc() {
        long svcMgmtNum = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<SvcProd> svcProds = Arrays.asList(
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p1.get(0).getProdId(), SvcProdCd.P1, now),
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p2.get(0).getProdId(), SvcProdCd.P2, now),
                SvcProd.createNewSvcProd(svcMgmtNum, prods_p3.get(0).getProdId(), SvcProdCd.P3, now)
        );
        svcProdRepository.saveAll(svcProds);
        return svcMgmtNum;
    }


}
