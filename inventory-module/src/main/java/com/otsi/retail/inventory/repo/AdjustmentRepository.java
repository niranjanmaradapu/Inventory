package com.otsi.retail.inventory.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.commons.AdjustmentType;
import com.otsi.retail.inventory.model.Adjustments;

@Repository
public interface AdjustmentRepository extends JpaRepository<Adjustments, Long> {

	Optional<Adjustments> findByCurrentBarcodeIdAndType(String currentBarcodeId , AdjustmentType type);
	
	Page<Adjustments> findByCurrentBarcodeIdAndTypeAndStoreId(String currentBarcodeId , AdjustmentType type , Long storeId , Pageable page);


	Page<Adjustments> findByCreatedDateBetweenAndCurrentBarcodeIdAndTypeOrderByCreatedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String currentBarcodeId, AdjustmentType type, Pageable pageable);


	Page<Adjustments> findByAdjustmentIdInAndComments(List<Long> effectingId, String string, Pageable pageable);

	Page<Adjustments> findByCurrentBarcodeIdAndAdjustmentIdInAndComments(String currentBarcodeId,
			List<Long> effectingId, String string, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndTypeOrderByCreatedDateDesc(LocalDateTime fromTime,
			LocalDateTime fromTime1, AdjustmentType type, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdInOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByTypeOrderByCreatedDateDesc(AdjustmentType string, Pageable pageable);
	
	Page<Adjustments> findByTypeAndStoreIdOrderByCreatedDateDesc(AdjustmentType string, Long storeId, Pageable pageable);

	Page<Adjustments> findByCurrentBarcodeIdAndComments(String currentBarcodeId, String comments, Pageable pageable);

	Optional<Adjustments> findByToBeBarcodeIdAndType(String currentBarcodeId, AdjustmentType rebar);

}
