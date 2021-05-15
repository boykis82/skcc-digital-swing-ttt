package org.caltech.miniswing.plmserver.test;

import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.plmserver.domain.Prod;

import java.util.Arrays;
import java.util.List;

public class ProdFactory {


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
