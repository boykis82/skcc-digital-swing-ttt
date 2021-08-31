package org.skcc.team1.tobe.customerserver.domain;



import org.skcc.team1.tobe.customerserver.dto.CustTypCd;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TestDataFactory {
    public static List<Cust> createManyCusts() {
        return Arrays.asList(
            Cust.builder()
                    .custNum(1)
                    .custNm("강인수")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1982,1,1))
                    .build(),
            Cust.builder()
                    .custNum(2)
                    .custNm("김도희")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1992,1,1))
                    .build(),
            Cust.builder()
                    .custNum(3)
                    .custNm("김선혁")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1986,1,1))
                    .build(),
            Cust.builder()
                    .custNum(4)
                    .custNm("김범수")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1996,1,1))
                    .build()
        );
    }
}
