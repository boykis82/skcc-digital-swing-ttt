package org.caltech.miniswing.productserver.domain;

import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProdRepository extends JpaRepository<Prod, String> {
    List<Prod> findBySvcProdCdOrderByProdId(SvcProdCd svcProdCd);
}
