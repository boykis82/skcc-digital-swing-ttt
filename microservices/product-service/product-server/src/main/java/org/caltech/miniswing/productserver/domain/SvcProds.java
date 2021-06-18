package org.caltech.miniswing.productserver.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.caltech.miniswing.exception.DataIntegrityViolationException;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SvcProds {
    private long svcMgmtNum;
    private List<SvcProd> svcProds;

    @Builder
    public SvcProds(long svcMgmtNum, List<SvcProd> svcProds) {
        this.svcMgmtNum = svcMgmtNum;
        this.svcProds = svcProds;
    }

    public List<SvcProd> subscribeProduct(String prodId, SvcProdCd svcProdCd) {
        LocalDateTime now = LocalDateTime.now();

        List<SvcProd> subTermSvcProds = new ArrayList<>();

        //-- 동일한 상품 가입되어 있으면 예외 처리
        if ( svcProds.stream().anyMatch(svcProd -> svcProd.isSubscribingProd(prodId)) ) {
            throw new InvalidInputException(String.format("동일한 상풍 가입 못함! svc_mgmt_num = [%d], prod_id = [%s]", svcMgmtNum, prodId));
        }
        //-- svc_prod_Cd가 1,2 면 기존 이력 종료해야 함
        if (svcProdCd == SvcProdCd.P1 || svcProdCd == SvcProdCd.P2) {
            //-- svcProd에서 svcProdCd가 1,2인 거 찾아서 이력 종료 후 가입
            List<SvcProd> prevSvcProds = svcProds.stream()
                    .filter( svcProd -> svcProd.getSvcProdCd() == svcProdCd && svcProd.isActive() )
                    .collect(Collectors.toList());

            //-- 살아있는 기본요금제나 부가요금제가 1개 초과면 데이터 정합성 오류
            if (prevSvcProds.size() > 1) {
                throw new DataIntegrityViolationException(String.format("사용중인 기본요금제/부가요금제가 여러 개임! svc_mgmt_num = [%d], svc_prod_Cd = [%s]", svcMgmtNum, svcProdCd.getValue()));
            }
            //-- 살아있는 기본요금제나 부가요금제 있으면 종료
            else if (prevSvcProds.size() == 1) {
                SvcProd beforeProd = prevSvcProds.get(0);
                beforeProd.terminate(now.minusSeconds(1), true);
                subTermSvcProds.add(beforeProd);
            }
        }
        SvcProd newScrbProd = SvcProd.createNewSvcProd(svcMgmtNum, prodId, svcProdCd, now);
        subTermSvcProds.add(newScrbProd);

        return subTermSvcProds;
    }

    public List<SvcProd> terminateProduct(long svcProdId) {
        SvcProd termSvcProd = svcProds.stream().filter(sp -> sp.getId() == svcProdId)
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(String.format("존재하지 않는 svc_prod_id ! [%d]", svcProdId)));

        termSvcProd.terminate(LocalDateTime.now(), false);

        return Collections.singletonList(termSvcProd);
    }

    public List<SvcProd> terminateAllProducts() {
        LocalDateTime now = LocalDateTime.now();
        svcProds.forEach(sp -> sp.terminate(now, true));
        return svcProds;
    }
}
