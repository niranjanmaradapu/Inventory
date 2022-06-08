package com.otsi.retail.inventory.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.ProductTransaction;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

	ProductTransaction findByQuantity(int qty);

	ProductTransaction findByEffectingTableId(Long adjustmentId);

	ProductTransaction findByStoreId(Long storeId);

	ProductTransaction findByBarcodeId(String barcode);
	
	ProductTransaction findTopByBarcodeIdAndStoreId(String barcode, Long storeId);

	ProductTransaction findByBarcodeIdAndStoreId(String barcode, Long storeId);

	ProductTransaction findTopByBarcodeId(String barcode);

	ProductTransaction findByBarcodeIdAndEffectingTableAndMasterFlag(String barcode, String string, boolean b);

	List<ProductTransaction> findAllByBarcodeId(String barcode);

	ProductTransaction findByBarcodeIdAndCommentAndMasterFlag(String barcode, String string, boolean b);

	List<ProductTransaction> findAllByStoreId(Long storeId);

	Page<ProductTransaction> findAllByStoreId(Long storeId, Pageable pageable);

	
	
}
