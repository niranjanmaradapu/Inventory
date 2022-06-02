package com.otsi.retail.inventory.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.commons.ProductItemAvEnum;
import com.otsi.retail.inventory.model.ProductImage;
import com.otsi.retail.inventory.model.ProductInventory;
import com.otsi.retail.inventory.model.ProductItem;
import com.otsi.retail.inventory.model.ProductItemAv;
import com.otsi.retail.inventory.vo.ProductItemVo;

@Component
public class ProductItemMapper {

	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public ProductItemVo EntityToVo(ProductItem dto) {
		ProductItemVo vo = new ProductItemVo();
		vo.setProductItemId(dto.getProductItemId());
		vo.setCostPrice(dto.getCostPrice());
		vo.setFromDate(dto.getCreationDate());
		vo.setToDate(dto.getLastModifiedDate());
		vo.setDefaultImage(dto.getDefaultImage());
		vo.setStatus(dto.getStatus());
		vo.setStock(dto.getStock());
		vo.setTitle(dto.getTitle());
		vo.setHsnCode(dto.getHsnCode());
		vo.setDiscontinued(dto.getDiscontinued());
		vo.setTyecode(dto.getTyecode());
		vo.setName(dto.getName());
		vo.setEmpId(dto.getEmpId());
		vo.setCostPrice(dto.getCostPrice());
		vo.setBatchNo(dto.getBatchNo());
		float stockIncrementValue = dto.getProductInventory().getStockvalue() * dto.getListPrice();
		vo.setValue(stockIncrementValue);
		vo.setListPrice(dto.getListPrice());
		vo.setUom(dto.getUom());
		vo.setDomainDataId(dto.getDomainDataId());
		vo.setStoreId(dto.getStoreId());
		vo.setBarcodeId(dto.getBarcodeId());
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
		prodInv.setStockvalue(dto.getProductInventory().getStockvalue());
		vo.setStockValue(prodInv.getStockvalue());
		List<ProductItemAv> productAv = dto.getProductItemAvId();
		productAv.stream().forEach(y -> {
			if (y.getName().equalsIgnoreCase(ProductItemAvEnum.COLOR.geteName())) {
				vo.setColour(y.getStringValue());
			}
			if (y.getName().equalsIgnoreCase(ProductItemAvEnum.LENGTH.geteName())) {
				vo.setLength(y.getIntValue());
			}
			if (y.getName().equalsIgnoreCase(ProductItemAvEnum.PRODUCT_VALIDITY.geteName())) {
				vo.setProductValidity(y.getDateValue());
			}

		});
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
		dto.setBarcodeId(vo.getBarcodeId());
		dto.setName(vo.getName());
		dto.setCostPrice(vo.getCostPrice());
		dto.setCreationDate(LocalDate.now());
		dto.setLastModifiedDate(LocalDate.now());
		dto.setDefaultImage(vo.getDefaultImage());
		dto.setListPrice(vo.getListPrice());
		dto.setStatus(1);
		dto.setStock(vo.getStock());
		dto.setEmpId(vo.getEmpId());
		dto.setDiscontinued(vo.getDiscontinued());
		dto.setTitle(vo.getTitle());
		dto.setDiscontinued(vo.getDiscontinued());
		dto.setTyecode(vo.getTyecode());
		dto.setHsnCode(vo.getHsnCode());
		dto.setBatchNo(vo.getBatchNo());
		dto.setUom(vo.getUom());
		dto.setDomainDataId(vo.getDomainDataId());
		dto.setStoreId(vo.getStoreId());
		return dto;

	}
	/*
	 * to convert list vo's to dto's
	 */

	public List<ProductItem> VoToEntity(List<ProductItemVo> vos) {
		return vos.stream().map(vo -> VoToEntity(vo)).collect(Collectors.toList());

	}

	public ProductItem VoToEntityUpdate(ProductItemVo vo, ProductItem dto) {

		dto.setCostPrice(vo.getCostPrice());
		dto.setCreationDate(LocalDate.now());
		dto.setLastModifiedDate(LocalDate.now());
		dto.setDefaultImage(vo.getDefaultImage());
		dto.setListPrice(vo.getListPrice());
		dto.setStatus(vo.getStatus());
		dto.setStock(vo.getStock());
		dto.setEmpId(vo.getEmpId());
		dto.setDiscontinued(vo.getDiscontinued());
		dto.setTitle(vo.getTitle());
		dto.setDiscontinued(vo.getDiscontinued());
		dto.setTyecode(vo.getTyecode());
		dto.setHsnCode(vo.getHsnCode());
		dto.setBatchNo(vo.getBatchNo());
		dto.setUom(vo.getUom());
		dto.setDomainDataId(vo.getDomainDataId());

		dto.setStoreId(vo.getStoreId());
		return dto;
	}

}
