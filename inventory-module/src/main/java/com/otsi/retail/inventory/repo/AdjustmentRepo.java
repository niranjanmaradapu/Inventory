package com.otsi.retail.inventory.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.model.Adjustments;

@Repository
public interface AdjustmentRepo extends JpaRepository<Adjustments, Long> {

	Adjustments findByCurrentBarcodeId(String currentBarcodeId);

	// List<Adjustments> findAllByAdjustmentId(Long adjustmentId);

	/*
	 * Page<Adjustments> findByCreatedDateBetweenAndComments(LocalDateTime fromTime,
	 * LocalDateTime fromTime1, String string, Pageable pageable);
	 */
	/*
	 * Page<Adjustments>
	 * findByCreatedDateBetweenAndCommentsOrderByLastModifiedDateAsc(LocalDateTime
	 * fromTime, LocalDateTime toTime, String string, Pageable pageable);
	 */

	Page<Adjustments> findByCreatedDateBetweenAndCurrentBarcodeIdAndCommentsOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String currentBarcodeId, String string, Pageable pageable);

	Page<Adjustments> findByComments(String string, Pageable pageable);

	Page<Adjustments> findByAdjustmentIdInAndComments(List<Long> effectingId, String string, Pageable pageable);

	Page<Adjustments> findByCurrentBarcodeIdAndAdjustmentIdInAndComments(String currentBarcodeId,
			List<Long> effectingId, String string, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdIn(LocalDateTime fromTime,
			LocalDateTime fromTime1, String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdInOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCommentsAndCurrentBarcodeIdIn(String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCurrentBarcodeIdAndComments(String currentBarcodeId, String comments, Pageable pageable);

}
