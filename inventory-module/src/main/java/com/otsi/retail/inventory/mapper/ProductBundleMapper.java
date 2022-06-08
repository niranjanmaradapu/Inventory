package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.vo.ProductBundleVo;

@Component
public class ProductBundleMapper {

	public ProductBundleVo entityToVO(ProductBundle productBundle) {
		ProductBundleVo productBundleVo = new ProductBundleVo();
		productBundleVo.setId(productBundle.getId());
		productBundleVo.setName(productBundle.getName());
		productBundleVo.setDescription(productBundle.getDescription());
		productBundleVo.setDomainId(productBundle.getDomainId());
		productBundleVo.setStoreId(productBundle.getStoreId());
		productBundleVo.setBundleQuantity(productBundle.getBundleQuantity());
		productBundleVo.setStatus(productBundle.getStatus());
		productBundleVo.setFromDate(productBundle.getCreatedDate());
		productBundleVo.setToDate(productBundle.getLastModifiedDate());
		productBundleVo.setBarcode(productBundle.getBarcode());
		productBundleVo.setItemMrp(productBundle.getItemMrp());

		/*
		 * List<ProductVO> listVo = new ArrayList<>();
		 * 
		 * productBundle.getProductTextiles().stream().forEach(product -> { ProductVO
		 * productTextileVo = new ProductVO(); productTextileVo.setId(product.getId());
		 * productTextileVo.setBarcode(product.getBarcode());
		 * productTextileVo.setEmpId(product.getEmpId());
		 * productTextileVo.setParentBarcode(product.getBarcode());
		 * productTextileVo.setFromDate(product.getCreatedDate());
		 * productTextileVo.setToDate(product.getLastModifiedDate());
		 * productTextileVo.setStatus(product.getStatus());
		 * productTextileVo.setName(product.getName());
		 * productTextileVo.setDivision(product.getDivision());
		 * productTextileVo.setSection(product.getSection());
		 * productTextileVo.setSubSection(product.getSubSection());
		 * productTextileVo.setSellingTypeCode(product.getSellingTypeCode());
		 * productTextileVo.setQty(product.getQty());
		 * productTextileVo.setValue(product.getQty() * product.getItemMrp());
		 * productTextileVo.setOriginalBarcodeCreatedAt(product.
		 * getOriginalBarcodeCreatedAt());
		 * productTextileVo.setCategory(product.getCategory());
		 * productTextileVo.setBatchNo(product.getBatchNo());
		 * productTextileVo.setCostPrice(product.getCostPrice());
		 * productTextileVo.setItemMrp(product.getItemMrp());
		 * productTextileVo.setHsnCode(product.getHsnCode());
		 * productTextileVo.setUom(product.getUom());
		 * productTextileVo.setColour(product.getColour());
		 * productTextileVo.setStoreId(product.getStoreId());
		 * productTextileVo.setDomainId(product.getDomainId());
		 * listVo.add(productTextileVo);
		 * 
		 * }); 
		 * productBundleVo.setProductTextiles(listVo);
		 */
		return productBundleVo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<ProductBundleVo> entityToVO(List<ProductBundle> productBundles) {
		return productBundles.stream().map(productBundle -> entityToVO(productBundle)).collect(Collectors.toList());

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public ProductBundle voToEntity(ProductBundleVo productBundleVo) {
		ProductBundle productBundle = new ProductBundle();
		productBundle.setName(productBundleVo.getName());
		productBundle.setDescription(productBundleVo.getDescription());
		productBundle.setDomainId(productBundleVo.getDomainId());
		productBundle.setStoreId(productBundleVo.getStoreId());
		productBundle.setStatus(Boolean.TRUE);
		productBundle.setBundleQuantity(productBundleVo.getBundleQuantity());
		return productBundle;

	}

	/*
	 * to convert list vo's to dto's
	 */

	public List<ProductBundle> voTOEntity(List<ProductBundleVo> productBundleVos) {
		return productBundleVos.stream().map(productBundleVo -> voToEntity(productBundleVo))
				.collect(Collectors.toList());

	}
}
