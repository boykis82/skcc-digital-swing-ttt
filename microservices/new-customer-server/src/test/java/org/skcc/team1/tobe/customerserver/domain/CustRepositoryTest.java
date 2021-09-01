package org.skcc.team1.tobe.customerserver.domain;


import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skcc.team1.tobe.customerserver.dto.CustTypCd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustRepositoryTest {
    @Autowired
    CustRepository custRepository;

    @After
    public void tearDown() {
        custRepository.deleteAll();
    }

    @Test
    public void test_저장_그냥jpatest용() {
        Cust c = Cust.builder()
                .custNum(1)
                .custNm("강인수")
                .custTypCd(CustTypCd.C01)
                .custRgstDt(LocalDate.now())
                .build();

        Cust savedCust = custRepository.save(c);
        assertThat(savedCust.getCustNum()).isNotEqualTo(0);
    }

    @Ignore
    public void test_이름과생일로고객가져오기() {
        custRepository.saveAll( TestDataFactory.createManyCusts() );
        assertThat(custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc("강인수", LocalDate.of(1982,1,1))).hasSize(1);
        assertThat(custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc("강인수", LocalDate.of(1983,1,1))).hasSize(0);
        assertThat(custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc("이수경", LocalDate.of(1980,1,1))).hasSize(0);
    }
}
