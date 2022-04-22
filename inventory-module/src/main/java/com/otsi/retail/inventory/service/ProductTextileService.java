package com.otsi.retail.inventory.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;
import com.otsi.retail.inventory.vo.SearchFilterVo;

@Service
public interface ProductTextileService {

	String addBarcodeTextile(ProductTextileVo textileVo);

	String updateBarcodeTextile(ProductTextileVo textileVo);

	String deleteBarcodeTextile(String barcode);

	ProductTextileVo getBarcodeTextile(String barcode, Long storeId);

	//List<ProductTextileVo> getAllBarcodes(SearchFilterVo vo);

	List<AdjustmentsVo> getAllAdjustments(AdjustmentsVo vo);

	List<String> getAllColumns(Long domainId);

	List<String> getValuesFromProductTextileColumns(String enumName);

	void inventoryUpdate(List<InventoryUpdateVo> request);

	List<ProductTextileVo> getBarcodeTextileReports(SearchFilterVo vo);

	List<ProductTextileVo> getBarcodes(List<String> barcode);

	String saveProductTextileList(List<ProductTextileVo> productTextileVos, Long storeId);

	ProductTextileVo getTextileParentBarcode(String parentBarcode);
	
	Page<ProductTextileVo> getAllBarcodes(SearchFilterVo vo, Pageable pageable);

}
