package org.caltech.miniswing.plmserver.domain;

import org.caltech.miniswing.plmclient.dto.SvcProdCd;
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
        prodRepository.saveAll(createManyProds_1());
        prodRepository.saveAll(createManyProds_2());
        prodRepository.saveAll(createManyProds_3());
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


    public static List<Prod> createManyProds_1() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000001").prodNm("표준요금제").svcProdCd(SvcProdCd.P1).description("111").build(),
                Prod.builder().prodId("NA00000002").prodNm("기본요금제").svcProdCd(SvcProdCd.P1).description("111").build(),
                Prod.builder().prodId("NA00000003").prodNm("스페셜").svcProdCd(SvcProdCd.P1).description("111").build()
        );
    }

    public static List<Prod> createManyProds_2() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000004").prodNm("부가요금제1").svcProdCd(SvcProdCd.P2).description("111").build(),
                Prod.builder().prodId("NA00000005").prodNm("부가요금제2").svcProdCd(SvcProdCd.P2).description("111").build()
        );

    }

    public static List<Prod> createManyProds_3() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000006").prodNm("V컬러링").svcProdCd(SvcProdCd.P3).description("111").build(),
                Prod.builder().prodId("NA00000007").prodNm("Wavve").svcProdCd(SvcProdCd.P3).description("111").build(),
                Prod.builder().prodId("NA00000008").prodNm("Flo").svcProdCd(SvcProdCd.P3).description("111").build()
        );

    }
}
