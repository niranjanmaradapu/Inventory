package com.otsi.retail.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.ProductBundleAssignmentTextile;

@Repository
public interface BundledProductAssignmentRepository extends JpaRepository<ProductBundleAssignmentTextile, Long> {

	ProductBundleAssignmentTextile findByProductBundleId(Long id);

}
