package org.caltech.miniswing.plmserver.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProdRepository extends JpaRepository<Prod, String> {
    //@Query("FROM Prod p WHERE p.prodNm LIKE %:prodNm% ORDER BY p.prodId")
    List<Prod> findByProdNmContainingOrderByProdId(String prodNm);

    //@Query("FROM Prod p WHERE p.prodId IN :prodIds ORDER BY p.prodId")
    List<Prod> findByProdIdInOrderByProdId(Collection<String> prodIds);

}
