package com.otsi.retail.inventory.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.vo.ProductBundleVo;

@Service
public interface ProductBundleService {

	ProductBundleVo addProductBundle(ProductBundleVo productBundleVo);

	Optional<ProductBundle> getProductBundle(Long id);

	String updateProductBundle(ProductBundleVo productBundleVo);

	ProductBundleVo deleteProductBundle(Long id);

	List<ProductBundleVo> getAllProductBundles(LocalDate fromDate, LocalDate toDate, Long id, Long storeId);

}
