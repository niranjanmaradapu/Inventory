package com.otsi.retail.inventory.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.model.ProductBundleAssignmentTextile;

@Repository
public interface BundledProductAssignmentRepository extends JpaRepository<ProductBundleAssignmentTextile, Long> {

	ProductBundleAssignmentTextile findByProductBundleId(Long id);


	List<ProductBundleAssignmentTextile> findByProductBundleId_Id(Long id);

}
