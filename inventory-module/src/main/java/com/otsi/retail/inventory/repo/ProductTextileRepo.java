package com.otsi.retail.inventory.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.model.ProductTextile;
import com.otsi.retail.inventory.vo.ProductTextileVo;

@Repository
public interface ProductTextileRepo extends JpaRepository<ProductTextile, Long> {

	Optional<ProductTextile> findByProductTextileId(Long productTextileId);

	List<ProductTextile> findByEmpId(String empId);

	List<ProductTextile> findByItemMrpBetweenAndStoreId(float itemMrpLessThan, float itemMrpGreaterThan, Long storeId);

	List<ProductTextile> findByStoreId(Long storeId);

	List<ProductTextile> findByStatus(ProductStatus status);

	List<ProductTextile> findAllByStoreId(Long storeId);

	ProductTextile findByBarcode(String barcode);

	List<ProductTextile> findByStoreIdAndBarcode(Long storeId, String barcode);

	List<ProductTextile> findByCreationDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(LocalDate fromDate,
			LocalDate toDate, ProductStatus status, Long storeId);

	List<ProductTextile> findByCreationDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(LocalDate fromDate,
			LocalDate toDate, String barcode, Long storeId);

	List<ProductTextile> findByBarcodeAndStoreId(String barcode, Long storeId);

	List<ProductTextile> findByStoreIdAndStatus(Long storeId, ProductStatus status);

	ProductTextile findByParentBarcode(String barcode);

	List<ProductTextile> findByItemMrpBetweenAndStoreIdAndStatus(float itemMrpLessThan, float itemMrpGreaterThan,
			Long storeId, ProductStatus status);

	List<ProductTextile> findByEmpIdAndStatus(String empId, ProductStatus status);

	List<ProductTextile> findByBarcodeIn(List<String> barcode);

	@Query(value = "select column_name from information_schema.columns where table_name ='product_textile'", nativeQuery = true)
	List<String> findAllColumnNames();

	List<ProductTextile> findByEmpIdAndStatusAndStoreId(String empId, ProductStatus status, Long storeId);

	List<ProductTextile> findByCreationDateAndStatusAndStoreId(LocalDate fromDate, ProductStatus status,
			Long storeId);

	ProductTextile findByBarcodeAndStatus(String barcode, ProductStatus status);

	ProductTextile findByBarcodeAndSellingTypeCode(String barcode, ProductEnum productbundle);

	Page<ProductTextile> findByCreationDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(LocalDate fromDate,
			LocalDate toDate, ProductStatus status, Long storeId, Pageable pageable);

	Page<ProductTextile> findByCreationDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(LocalDate fromDate,
			LocalDate toDate, String barcode, Long storeId, Pageable pageable);

	Page<ProductTextile> findByBarcodeAndStoreId(String barcode, Long storeId, Pageable pageable);

	Page<ProductTextile> findByStoreIdAndStatus(Long storeId, ProductStatus status, Pageable pageable);

	Page<ProductTextile> findByStatus(ProductStatus status, Pageable pageable);

}
