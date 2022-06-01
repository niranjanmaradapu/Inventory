package com.otsi.retail.inventory.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.DomainTypePropertiesVO;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductVO;
import com.otsi.retail.inventory.vo.SearchFilterVo;

@Service
public interface ProductService {

	ProductVO addBarcode(ProductVO textileVo);

	ProductVO updateBarcode(ProductVO textileVo);

	void deleteProduct(Long id);

	ProductVO getBarcode(String barcode, Long storeId);

	List<String> getAllColumns();

	List<String> getValuesFromProductTextileColumns(String enumName);

	void inventoryUpdate(List<InventoryUpdateVo> request, String type, String referringTable);

	List<ProductVO> getBarcodes(List<String> barcode);

	void saveProducts(List<ProductVO> productTextileVos, Long storeId);

	ProductVO getProductByParentBarcode(String parentBarcode);
	
	Page<ProductVO> getAllBarcodes(SearchFilterVo vo, Pageable pageable);

	Page<AdjustmentsVo> getAdjustments(SearchFilterVo vo, Pageable pageable);

	Page<ProductVO> getBarcodeTextileReports(SearchFilterVo vo, Pageable pageable);

	void addBulkProducts(MultipartFile multipartFile, Long storeId)
			throws InstantiationException, IllegalAccessException, IOException;

	DomainTypePropertiesVO getProperties(String domainType);

	ProductVO barcodeDetails(String barcode, Long clientId, Long storeId);

	

}
