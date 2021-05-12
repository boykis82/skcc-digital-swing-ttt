package org.caltech.miniswing.serviceserver.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import static org.caltech.miniswing.serviceserver.domain.QSvc.svc;

import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SvcRepositorySupport  {
    private final JPAQueryFactory queryFactory;

    public List<Svc> findByCustAndSvcStCd(int offset,
                                          int limit,
                                          long custNum,
                                          boolean includeTermSvc) {
        QueryResults<Svc> result = queryFactory.selectFrom(svc)
                .where(
                        //-- 동일명의
                        (svc.custNum.eq(custNum)),
                        //-- 해지서비스포함여부에 따라 서비스상태 조건 체크
                        (includeTermSvc ? null : svc.svcStCd.in(SvcStCd.AC, SvcStCd.SP))
                )
                .offset(offset)
                .limit(limit)
                .orderBy( svc.svcStCd.asc(), svc.svcScrbDt.asc() )
                .fetchResults();

        return result.getResults();
    }
}

