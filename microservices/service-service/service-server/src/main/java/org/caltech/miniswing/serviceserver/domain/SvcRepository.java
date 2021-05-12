package org.caltech.miniswing.serviceserver.domain;

import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SvcRepository extends JpaRepository<Svc, Long> {
    Optional<Svc> findBySvcNumAndSvcStCd(String svcNum, SvcStCd svcStCd);

    @Query("SELECT DISTINCT s FROM Svc s join fetch s.svcStHsts WHERE s.id = :id")
    Optional<Svc> findByIdWithSvcStHsts(Long id);
}
