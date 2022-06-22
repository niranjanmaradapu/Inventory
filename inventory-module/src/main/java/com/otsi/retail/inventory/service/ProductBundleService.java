package com.otsi.retail.inventory.service;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.vo.ProductBundleVo;

@Service
public interface ProductBundleService {

	ProductBundleVo addProductBundle(ProductBundleVo productBundleVo);

	Optional<ProductBundle> getProductBundle(Long id);

	ProductBundleVo updateProductBundle(ProductBundleVo productBundleVo);

	ProductBundleVo deleteProductBundle(Long id);

	Page<ProductBundleVo> getAllProductBundles(LocalDate fromDate, LocalDate toDate, Long id, Long storeId,
			Pageable pageable);

}