package com.otsi.retail.inventory.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.model.ProductBundle;

@Repository
public interface ProductBundleRepo extends JpaRepository<ProductBundle, Long> {

	List<ProductBundle> findByStatus(Boolean status);

	List<ProductBundle> findByCreatedDateBetweenAndStatusOrderByLastModifiedDateAsc(LocalDate fromDate,
			LocalDate toDate, Boolean status);
	
	List<ProductBundle> findByCreatedDateAndStatus(LocalDate fromDate, Boolean status);

	List<ProductBundle> findByCreatedDateBetweenAndStatus(LocalDateTime atStartOfDay, LocalDateTime endOfDay,
			Boolean status);

	List<ProductBundle> findByCreatedDateAndStatus(String string, Boolean status);

	List<ProductBundle> findAllByStoreIdAndStatus(Long storeId, Boolean status);

	List<ProductBundle> findByCreatedDateBetweenAndIdAndStatusAndStoreIdOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, Long id, Boolean status, Long storeId);

	List<ProductBundle> findByIdAndStatusAndStoreId(Long id, Boolean status, Long storeId);

}
