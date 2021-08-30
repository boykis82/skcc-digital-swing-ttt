package org.skcc.team1.legacy.customerserver.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CustRepository extends JpaRepository<Cust, Long> {
    List<Cust> findByCustNmAndBirthDtOrderByCustRgstDtDesc(String custNm, LocalDate birthDt);

    @Query("SELECT c FROM Cust c ORDER BY c.id DESC")
    List<Cust> findAllDesc();
}
