package org.mifosplatform.accounting.costcenter.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by frank on 22/05/18.
 */
public interface CostCenterRepository extends JpaRepository<CostCenter, Long>, JpaSpecificationExecutor<CostCenter> {

	@Query("from CostCenter costCenter where costCenter.nonStaff.id= :nonStaffId")
	List<CostCenter> findByNonStaffId(@Param("nonStaffId") Long nonStaffId);
}
