package com.otsi.retail.inventory.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.repo.ProductTransactionRepo;
import com.otsi.retail.inventory.vo.ProductBundleVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;

@Component
public class ProductBundleMapper {

	@Autowired
	private ProductTransactionRepo productTransactionRepo;

	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public ProductBundleVo EntityToVo(ProductBundle productBundle) {
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
		productBundleVo.setItemMrp(productBundle.getItemMrp());

		/*
		 * List<ProductTextileVo> listVo = new ArrayList<>();
		 * 
		 * productBundle.getProductTextiles().stream().forEach(p -> { ProductTextileVo
		 * productTextileVo = new ProductTextileVo();
		 * productTextileVo.setProductTextileId(p.getProductTextileId());
		 * productTextileVo.setBarcode(p.getBarcode());
		 * productTextileVo.setEmpId(p.getEmpId());
		 * productTextileVo.setParentBarcode(p.getBarcode());
		 * productTextileVo.setQty(p.getQuantity());
		 * productTextileVo.setFromDate(p.getCreatedDate());
		 * productTextileVo.setToDate(p.getLastModifiedDate());
		 * productTextileVo.setStatus(p.getStatus());
		 * productTextileVo.setName(p.getName());
		 * productTextileVo.setDivision(p.getDivision());
		 * productTextileVo.setSection(p.getSection());
		 * productTextileVo.setSubSection(p.getSubSection());
		 * productTextileVo.setSellingTypeCode(p.getSellingTypeCode());
		 * List<ProductTransaction> transact = new ArrayList<>(); transact =
		 * productTransactionRepo.findAllByBarcodeId(p.getBarcode());
		 * transact.stream().forEach(t -> { if
		 * (t.getEffectingTable().equals("product textile table")) { t =
		 * productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(t.
		 * getBarcodeId(), "product textile table", true);
		 * productTextileVo.setQty(t.getQuantity());
		 * 
		 * productTextileVo.setValue(t.getQuantity() * p.getItemMrp()); } else if
		 * (t.getEffectingTable().equals("Adjustments")) { t =
		 * productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(t.
		 * getBarcodeId(), "Adjustments", true);
		 * productTextileVo.setQty(t.getQuantity());
		 * 
		 * productTextileVo.setValue(t.getQuantity() * p.getItemMrp()); } });
		 * productTextileVo.setOriginalBarcodeCreatedAt(p.getOriginalBarcodeCreatedAt())
		 * ; productTextileVo.setCategory(p.getCategory());
		 * productTextileVo.setBatchNo(p.getBatchNo());
		 * productTextileVo.setCostPrice(p.getCostPrice());
		 * productTextileVo.setItemMrp(p.getItemMrp());
		 * productTextileVo.setHsnCode(p.getHsnCode());
		 * productTextileVo.setUom(p.getUom());
		 * productTextileVo.setColour(p.getColour());
		 * productTextileVo.setStoreId(p.getStoreId());
		 * productTextileVo.setDomainId(p.getDomainId()); listVo.add(productTextileVo);
		 * 
		 * }); productBundleVo.setProductTextiles(listVo);
		 */
		return productBundleVo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<ProductBundleVo> EntityToVo(List<ProductBundle> productBundles) {
		return productBundles.stream().map(productBundle -> EntityToVo(productBundle)).collect(Collectors.toList());

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public ProductBundle VoToEntity(ProductBundleVo productBundleVo) {
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

	public List<ProductBundle> VoToEntity(List<ProductBundleVo> productBundleVos) {
		return productBundleVos.stream().map(productBundleVo -> VoToEntity(productBundleVo))
				.collect(Collectors.toList());

	}

}
