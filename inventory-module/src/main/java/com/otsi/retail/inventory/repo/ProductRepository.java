package com.otsi.retail.inventory.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findById(Long productTextileId);

	List<Product> findByEmpId(String empId);

	List<Product> findByItemMrpBetweenAndStoreId(float itemMrpLessThan, float itemMrpGreaterThan, Long storeId);

	List<Product> findByStoreId(Long storeId);

	List<Product> findByStatus(ProductStatus status);

	List<Product> findAllByStoreId(Long storeId);

	Product findByBarcode(String barcode);

	Product findByBarcodeAndStoreId(String barcode, Long storeId);

	List<Product> findByStoreIdAndStatus(Long storeId, ProductStatus status);

	Product findByParentBarcode(String barcode);

	List<Product> findByItemMrpBetweenAndStoreIdAndStatus(float itemMrpLessThan, float itemMrpGreaterThan, Long storeId,
			ProductStatus status);

	List<Product> findByEmpIdAndStatus(String empId, ProductStatus status);

	List<Product> findByBarcodeIn(List<String> barcode);

	@Query(value = "select column_name from information_schema.columns where table_name ='product'", nativeQuery = true)
	List<String> findAllColumnNames();

	List<Product> findByEmpIdAndStatusAndStoreId(String empId, ProductStatus status, Long storeId);

	Product findByBarcodeAndStatus(String barcode, ProductStatus status);

	Product findByBarcodeAndSellingTypeCode(String barcode, ProductEnum productbundle);

	List<Product> findByCreatedDateBetweenAndStatusAndStoreId(LocalDateTime fromTime, LocalDateTime fromTime1,
			ProductStatus status, Long storeId);

	List<Product> findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(LocalDateTime fromTime,
			LocalDateTime toTime, ProductStatus status, Long storeId);

	List<Product> findByCreatedDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(LocalDateTime fromTime,
			LocalDateTime toTime, String barcode, Long storeId);

	Page<Product> findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(LocalDateTime fromTime,
			LocalDateTime toTime, ProductStatus status, Long storeId, Pageable pageable);

	Page<Product> findByCreatedDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(LocalDateTime fromTime,
			LocalDateTime toTime, String barcode, Long storeId, Pageable pageable);

	Page<Product> findByBarcodeAndStoreId(String barcode, Long storeId, Pageable pageable);

	Page<Product> findByStatus(ProductStatus status, Pageable pageable);

	Page<Product> findByCreatedDateBetweenAndStatusAndStoreId(LocalDateTime fromTime, LocalDateTime fromTime1,
			ProductStatus status, Long storeId, Pageable pageable);

	List<Product> findAllByEmpId(Long empId);

	Page<Product> findByItemMrpBetweenAndStoreIdAndStatus(float itemMrpLessThan, float itemMrpGreaterThan, Long storeId,
			ProductStatus status, Pageable pageable);

	Page<Product> findByEmpIdAndStatusAndStoreId(Long empId, ProductStatus status, Long storeId, Pageable pageable);

	Page<Product> findByStoreIdOrderByCreatedDateDesc(Long storeId, Pageable pageable);

	Page<Product> findByStoreIdAndStatusOrderByCreatedDateDesc(Long storeId, ProductStatus status, Pageable pageable);

	Product findById(Product assignedproductId);

	@Query(value = "select p.uom from  product p WHERE p.uom IS NOT NULL group by  p.uom", nativeQuery = true)
	List<String> findByUom(String enumName);

	@Query(value = "select p.batch_no from  product p  WHERE p.batch_no IS NOT NULL group by  p.batch_no", nativeQuery = true)
	List<String> findByBatchNo(String enumName);

	Product findByBarcodeAndStoreIdAndStatus(String barcode, Long storeId, ProductStatus enable);

}
