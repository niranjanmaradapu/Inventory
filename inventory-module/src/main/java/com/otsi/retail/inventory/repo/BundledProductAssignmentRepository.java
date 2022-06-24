package com.otsi.retail.inventory.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.model.ProductBundleAssignmentTextile;
import com.otsi.retail.inventory.vo.ProductVO;

@Repository
public interface BundledProductAssignmentRepository extends JpaRepository<ProductBundleAssignmentTextile, Long> {

	ProductBundleAssignmentTextile findByProductBundleId(Long id);

	List<ProductBundleAssignmentTextile> findByProductBundleId_Id(Long id);

	ProductBundleAssignmentTextile findByProductBundleAssignmentTextileId(Long productBundleAssignmentTextileId);

	ProductBundleAssignmentTextile findAllByProductBundleId_Id(Long id);

	List<ProductBundleAssignmentTextile> findByProductBundleId_IdAndAssignedProductId_Id(Long id, Long id2);

	List<ProductBundleAssignmentTextile> findByAssignedProductId_Id(Long id);

}
