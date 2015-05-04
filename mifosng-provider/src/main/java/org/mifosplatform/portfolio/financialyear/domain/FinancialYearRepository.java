/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {

    FinancialYear findById(Long id);

    @Modifying
    @Query("update FinancialYear fy set fy.current = ?1 WHERE fy.id <> ?2")
    int setAllCurrent(boolean current, long id);

    @Modifying
    @Query("update FinancialYear fy set fy.current = ?1")
    int setAllCurrent(boolean current);

    @Query("SELECT fy FROM FinancialYear fy ORDER BY fy.endDate DESC")
    List<FinancialYear> getLast();

    @Query("SELECT fy FROM FinancialYear fy WHERE fy.endDate > ?1 ORDER BY fy.endDate DESC")
    List<FinancialYear> getLast(Date now);

    @Query("SELECT COUNT(fy) FROM FinancialYear fy WHERE  ?1 >= fy.startDate AND  ?1 <= fy.endDate AND fy.current=true AND fy.closed=false")
    int countActiveFinancialYearFor(Date d);

    @Query("SELECT fy.closed FROM FinancialYear fy WHERE  ?1 >= fy.startDate AND  ?1 <= fy.endDate")
    Boolean isFinancialYearClosed(Date d);
}
