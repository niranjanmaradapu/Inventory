package com.otsi.retail.inventory.repo;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.ProductBundle;

@Repository
public interface ProductBundleRepository extends JpaRepository<ProductBundle, Long> {

	List<ProductBundle> findByStatus(Boolean status);

	Page<ProductBundle> findByCreatedDateBetweenAndStatus(LocalDateTime fromTime, LocalDateTime toTime, Boolean status,
			Pageable pageable);

	Page<ProductBundle> findAllByStoreIdAndStatus(Long storeId, Boolean status, Pageable pageable);

	Page<ProductBundle> findByCreatedDateBetweenAndStoreIdAndStatus(LocalDateTime fromTime, LocalDateTime toTime,
			Long storeId, Boolean status, Pageable pageable);

	Page<ProductBundle> findByIdAndStatusAndStoreId(Long id, Boolean status, Long storeId, Pageable pageable);

	Page<ProductBundle> findByCreatedDateBetweenAndIdAndStatusAndStoreIdOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, Long id, Boolean status, Long storeId, Pageable pageable);

	ProductBundle findByBarcode(String barCode);

}
