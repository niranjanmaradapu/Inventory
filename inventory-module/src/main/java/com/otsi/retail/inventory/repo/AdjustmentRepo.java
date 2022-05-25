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

	Page<Adjustments> findByComments(String string, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdIn(LocalDateTime fromTime,
			LocalDateTime toTime, String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdInOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCreatedDateBetweenAndCurrentBarcodeIdAndCommentsOrderByLastModifiedDateAsc(
			LocalDateTime fromTime, LocalDateTime toTime, String currentBarcodeId, String string, Pageable pageable);

	Page<Adjustments> findByCommentsAndCurrentBarcodeIdIn(String string, List<String> barcodes, Pageable pageable);

	Page<Adjustments> findByCurrentBarcodeIdAndComments(String currentBarcodeId, String string, Pageable pageable);

}
