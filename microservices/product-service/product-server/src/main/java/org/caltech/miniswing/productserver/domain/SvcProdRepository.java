package org.caltech.miniswing.productserver.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SvcProdRepository extends JpaRepository<SvcProd, Long> {
    @Query("FROM SvcProd sp WHERE sp.svcMgmtNum = :svcMgmtNum ORDER BY svcProdCd")
    List<SvcProd> findAllSvcProds(long svcMgmtNum);

    @Query("FROM SvcProd sp WHERE sp.svcMgmtNum = :svcMgmtNum AND sp.termDt IS NULL ORDER BY svcProdCd, effEndDtm DESC")
    List<SvcProd> findActiveSvcProds(long svcMgmtNum);
}
