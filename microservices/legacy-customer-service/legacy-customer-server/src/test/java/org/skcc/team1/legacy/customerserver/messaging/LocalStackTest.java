package org.skcc.team1.legacy.customerserver.messaging;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.skcc.team1.legacy.customerserver.domain.Cust;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.skcc.team1.legacy.customerclient.dto.CustTypCd.C01;

/*
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {"eureka.client.enabled=false"}
)*/
@Ignore
public class LocalStackTest {

    @Autowired
    CustomerMessagePublisher customerMessagePublisher;

    @Test
    public void test1() {
        CustResponseDto custResponseDto = CustResponseDto.builder()
                .custNm("강인수")
                .custTypCd(C01)
                .custRgstDt(LocalDate.of(2021,12,1))
                .custNum(1)
                .birthDt(LocalDate.of(1981,1,1))
                .build();

        customerMessagePublisher.sendCustomerCreatedEvent(custResponseDto);
    }

}
