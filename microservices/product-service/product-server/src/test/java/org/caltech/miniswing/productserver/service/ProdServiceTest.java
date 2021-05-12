package org.caltech.miniswing.productserver.service;

import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productserver.domain.Prod;
import org.caltech.miniswing.productserver.domain.SvcProd;
import org.caltech.miniswing.productserver.domain.SvcProdRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProdServiceTest {
    @Autowired
    SvcProdRepository svcProdRepository;

    @Autowired
    ProdService prodService;

    @After
    public void tearDown() {
        svcProdRepository.deleteAll();
    }

    @Test(expected = InvalidInputException.class)
    public void test_중복상품가입_예외발생() {
        long svcMgmtNum = subscribeSampleSvc();
        prodService.subscribeProduct(svcMgmtNum, ProdSubscribeRequestDto.builder().prodId("NA00000006").svcProdCd(SvcProdCd.P3).build()).block();
        fail("여기 오면 안됨");
    }

    @Test
    public void test_기본요금제_존재하는상태에서_다른기본요금제_가입() {
        long svcMgmtNum = subscribeSampleSvc();

        //-- 다른 기본요금제 가입
        prodService.subscribeProduct(svcMgmtNum, ProdSubscribeRequestDto.builder().prodId("NA00000002").svcProdCd(SvcProdCd.P1).build()).block();

        List<SvcProd> allSvcProds = svcProdRepository.findAllSvcProds(svcMgmtNum);
        assertThat( svcProdRepository.findAllSvcProds(svcMgmtNum) ).hasSize(4);
        //-- 이전 요금제는 종료
        assertThat( allSvcProds
                .stream()
                .filter(sp -> sp.getProdId().equals("NA00000001"))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("-_-!") )
                .getEffEndDtm()
        ).isBefore(LocalDateTime.of(9999,12,31,23,59,59));

        //-- 이후 요금제는 유지
        assertThat( allSvcProds
                .stream()
                .filter(sp -> sp.getProdId().equals("NA00000002"))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("...") )
                .getEffEndDtm()
        ).isEqualTo(LocalDateTime.of(9999,12,31,23,59,59));
    }

    //-- 서비스 개통 & 상품 3개 가입한 샘플 서비스
    private long subscribeSampleSvc() {
        long svcMgmtNum = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<SvcProd> svcProds = Arrays.asList(
                SvcProd.createNewSvcProd(svcMgmtNum, "NA00000001", SvcProdCd.P1, now),
                SvcProd.createNewSvcProd(svcMgmtNum, "NA00000003", SvcProdCd.P2, now),
                SvcProd.createNewSvcProd(svcMgmtNum, "NA00000006", SvcProdCd.P3, now)
        );
        svcProdRepository.saveAll(svcProds);
        return svcMgmtNum;
    }
}
