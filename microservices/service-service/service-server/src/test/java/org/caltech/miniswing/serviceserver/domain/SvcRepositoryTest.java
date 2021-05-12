package org.caltech.miniswing.serviceserver.domain;

import org.caltech.miniswing.serviceclient.dto.SvcCd;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest     //-- 표준 repository만 쓸거면 DataJpaTest 써도 되는데 querydsl은 이거 써줘야 하는듯
public class SvcRepositoryTest {
    @Autowired
    SvcRepository svcRepository;

    @Autowired
    SvcRepositorySupport svcRepositorySupport;

    @After
    public void tearDown() {
        svcRepository.deleteAll();
    }

    @Test
    public void test_서비스_생성() {
        Svc savedSvc = svcRepository.save(createSampleSvc());

        assertThat(savedSvc.getSvcMgmtNum()).isNotEqualTo(0);
        assertThat(savedSvc.getCustNum()).isEqualTo(1);
        assertThat(savedSvc.getFeeProdId()).isEqualTo("NA00000001");
    }

    private Svc createSampleSvc() {
        Svc s = Svc.builder()
                .svcCd(SvcCd.C)
                .svcStCd(SvcStCd.AC)
                .svcNum("01012345678")
                .svcScrbDt(LocalDate.now())
                .feeProdId("NA00000001")
                .custNum(1)
                .build();
        s.subscribe(LocalDateTime.of(2021,4,1,1,1,1));
        s.suspend(LocalDateTime.of(2021,4,3,1,1,1));
        s.activate(LocalDateTime.of(2021,4,5,1,1,1));

        return s;
    }

    @Test
    public void 상태이력까지fetchjoin() {
        Svc savedSvc = svcRepository.save(createSampleSvc());
        List<SvcStHst> svcStHsts = svcRepository.findByIdWithSvcStHsts(savedSvc.getId()).get().getSvcStHsts();

        assertThat(svcStHsts).hasSize(3);
        assertThat(svcStHsts.get(0).getSvcStCd()).isEqualTo(SvcStCd.AC);
        assertThat(svcStHsts.get(1).getSvcStCd()).isEqualTo(SvcStCd.SP);
        assertThat(svcStHsts.get(2).getSvcStCd()).isEqualTo(SvcStCd.AC);
    }

    @Test
    public void test_동일명의서비스_조회() {
        List<Long>    svcCusts = Arrays.asList(1L, 1L, 1L, 2L, 3L);
        List<SvcStCd> svcSts   = Arrays.asList(SvcStCd.AC, SvcStCd.SP, SvcStCd.TG, SvcStCd.AC, SvcStCd.TG);
        List<Svc>     svcs     = new ArrayList<>();
        List<String>  feeProds = Arrays.asList("NA00000001", "NA00000002", "NA00000003");
        Random        rand     = new Random();

        for (int i = 0 ; i < svcCusts.size() ; ++i)  {
            svcs.add( Svc.builder()
                    .svcCd(SvcCd.C)
                    .svcStCd( svcSts.get(i) )
                    .svcNum("0101234567" + rand.nextInt(9))
                    .svcScrbDt(LocalDate.now())
                    .feeProdId( feeProds.get(rand.nextInt(3)) )
                    .custNum( svcCusts.get(i) )
                    .build() );
        }
        svcRepository.saveAll(svcs);

        int offset = 0;
        int limit = 2;

        List<Svc> foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 1, false);
        assertThat(foundSvcs).hasSize(2);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 2, false);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 2, true);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 3, true);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 3, false);
        assertThat(foundSvcs).isEmpty();

        //-- paging
        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 1, true);
        assertThat(foundSvcs).hasSize(2);

        offset += limit;
        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, 1, true);
        assertThat(foundSvcs).hasSize(1);

    }

}
