package org.caltech.miniswing.plmserver.domain;

import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.plmserver.test.ProdFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest     //-- 표준 repository만 쓸거면 DataJpaTest 써도 되는데 querydsl은 이거 써줘야 하는듯
public class ProdRepositoryTest {
    @Autowired
    ProdRepository prodRepository;

    @Before
    public void setUp() {
        prodRepository.saveAll(ProdFactory.createManyProds_1());
        prodRepository.saveAll(ProdFactory.createManyProds_2());
        prodRepository.saveAll(ProdFactory.createManyProds_3());
    }

    @After
    public void tearDown() {
        prodRepository.deleteAll();
    }

    @Test
    public void test_상품ID로조회() {
        Optional<Prod> p = prodRepository.findById("NA00000001");
        assertThat(p.isPresent()).isTrue();
        assertThat(p.get().getProdNm()).isEqualTo("표준요금제");
    }

    @Test
    public void test_상품명like로조회() {
        List<Prod> prods = prodRepository.findByProdNmContainingOrderByProdId("가요");
        assertThat(prods).hasSize(2);
        assertThat(prods.get(0).getProdNm()).isEqualTo("부가요금제1");
        assertThat(prods.get(1).getProdNm()).isEqualTo("부가요금제2");
    }

    @Test
    public void test_상품ID여러개로조회() {
        List<Prod> prods = prodRepository.findByProdIdInOrderByProdId(Arrays.asList("NA00000001", "NA00000003", "NA00000007", "NA00000008"));
        assertThat(prods).hasSize(4);
        assertThat(prods.get(0).getProdId()).isEqualTo("NA00000001");
        assertThat(prods.get(0).getProdNm()).isEqualTo("표준요금제");
        assertThat(prods.get(1).getProdId()).isEqualTo("NA00000003");
        assertThat(prods.get(1).getProdNm()).isEqualTo("스페셜");
        assertThat(prods.get(2).getProdId()).isEqualTo("NA00000007");
        assertThat(prods.get(2).getProdNm()).isEqualTo("Wavve");
        assertThat(prods.get(3).getProdId()).isEqualTo("NA00000008");
        assertThat(prods.get(3).getProdNm()).isEqualTo("Flo");
    }

}
