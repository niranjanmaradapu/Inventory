package com.otsi.retail.inventory.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.ProductImage;
import com.otsi.retail.inventory.model.ProductInventory;
import com.otsi.retail.inventory.model.ProductItem;
import com.otsi.retail.inventory.vo.ProductItemVo;

@Component
public class InventoryMapper {

	@Autowired
	private DomainDataMapper domainDataMapper;

	@Autowired
	private StoreMapper storeMapper;

	@Autowired
	private BarcodeMapper barcodeeMapper;

	public ProductItemVo EntityToVo(ProductItem dto) {
		ProductItemVo vo = new ProductItemVo();
		vo.setProductItemId(dto.getProductItemId());
		vo.setPuid(dto.getPuid());
		vo.setCostPrice(dto.getCostPrice());
		vo.setDefaultImage(dto.getDefaultImage());
		vo.setListPrice(dto.getListPrice());
		vo.setStatus(dto.getStatus());
		vo.setStock(dto.getStock());
		vo.setTitle(dto.getTitle());
		vo.setTyecode(dto.getTyecode());
		vo.setCostPrice(dto.getCostPrice());
		vo.setUom(dto.getUom());
		vo.setDomainData(domainDataMapper.EntityToVo(dto.getDomainData()));
		vo.setStore(storeMapper.EntityToVo(dto.getStore()));
		vo.setBarcode(barcodeeMapper.EntityToVo(dto.getBarcode()));
		List<ProductImage> listImages = new ArrayList<>();
		List<ProductImage> productImage = dto.getProductImage();
		productImage.forEach(x -> {
			ProductImage image = new ProductImage();
			image.setProductImageId(x.getProductImageId());
			image.setImage(x.getImage());
			image.setPIUID(x.getPIUID());
			image.setCreationDate(LocalDate.now());
			image.setLastModified(LocalDate.now());
			listImages.add(image);

		});
		vo.setProductImage(listImages);
		ProductInventory prodInv = new ProductInventory();
		prodInv.setProductInventoryId(dto.getProductInventory().getProductInventoryId());
		prodInv.setCreationDate(LocalDate.now());
		prodInv.setLastModified(LocalDate.now());
		prodInv.setStockvalue(dto.getProductInventory().getStockvalue());
		vo.setProductInventory(prodInv );
		return vo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<ProductItemVo> EntityToVo(List<ProductItem> dtos) {
		return dtos.stream().map(dto -> EntityToVo(dto)).collect(Collectors.toList());

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public ProductItem VoToEntity(ProductItemVo vo) {
		ProductItem dto = new ProductItem();
		dto.setProductItemId(vo.getProductItemId());
		dto.setPuid(vo.getPuid());
		dto.setCostPrice(vo.getCostPrice());
		dto.setDefaultImage(vo.getDefaultImage());
		dto.setListPrice(vo.getListPrice());
		dto.setStatus(vo.getStatus());
		dto.setStock(vo.getStock());
		dto.setTitle(vo.getTitle());
		dto.setTyecode(vo.getTyecode());
		dto.setDomainData(domainDataMapper.VoToEntity(vo.getDomainData()));
		dto.setStore(storeMapper.VoToEntity(vo.getStore()));
		dto.setBarcode(barcodeeMapper.VoToEntity(vo.getBarcode()));
		dto.setUom(vo.getUom());
		return dto;

	}
	/*
	 * to convert list vo's to dto's
	 */

	public List<ProductItem> VoToEntity(List<ProductItemVo> vos) {
		return vos.stream().map(vo -> VoToEntity(vo)).collect(Collectors.toList());

	}

}
