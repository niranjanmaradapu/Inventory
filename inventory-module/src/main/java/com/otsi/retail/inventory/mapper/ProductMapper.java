package com.otsi.retail.inventory.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.model.Product;
import com.otsi.retail.inventory.vo.ProductVO;

@Component
public class ProductMapper {

	public ProductVO entityToVO(Product entity) {
		ProductVO vo = new ProductVO();

		BeanUtils.copyProperties(entity, vo);
		vo.setEmpId(entity.getEmpId());
		vo.setFromDate(entity.getCreatedDate());
		vo.setToDate(entity.getLastModifiedDate());
		vo.setBarcode(entity.getBarcode());
		vo.setDivision(entity.getDivision());
		vo.setSection(entity.getSection());
		vo.setSubSection(entity.getSubSection());
		vo.setName(entity.getName());
		vo.setStatus(entity.getStatus());
		vo.setCategory(entity.getCategory());
		vo.setBatchNo(entity.getBatchNo());
		vo.setColour(entity.getColour());
		vo.setOriginalBarcodeCreatedAt(entity.getOriginalBarcodeCreatedAt());
		vo.setStoreId(entity.getStoreId());
		vo.setDomainId(entity.getDomainId());
		vo.setSellingTypeCode(entity.getSellingTypeCode());
		return vo;

	}


	public List<ProductVO> entityToVO(List<Product> entities) {
		return entities.stream().map(dto -> entityToVO(dto)).collect(Collectors.toList());

	}

	public Product customVoToEntityMapper(ProductVO vo) {
		Product product = new Product();
		product.setQty(vo.getQty());
		product.setId(vo.getId());
		return product;

	}

	public Product voToEntity(ProductVO vo) {
		Product entity = new Product();
		BeanUtils.copyProperties(vo, entity);
		entity.setCostPrice(vo.getCostPrice());
		entity.setEmpId(vo.getEmpId());
		entity.setStatus(ProductStatus.ENABLE);
		entity.setName(vo.getName());
		entity.setBarcode(vo.getBarcode());
		entity.setDivision(vo.getDivision());
		entity.setSection(vo.getSection());
		entity.setSubSection(vo.getSubSection());
		entity.setOriginalBarcodeCreatedAt(LocalDate.now());
		entity.setCategory(vo.getCategory());
		entity.setBatchNo(vo.getBatchNo());
		entity.setColour(vo.getColour());
		entity.setStoreId(vo.getStoreId());
		entity.setDomainId(vo.getDomainId());
		entity.setSellingTypeCode(vo.getSellingTypeCode());
		return entity;

	}


	public List<Product> VoToEntity(List<ProductVO> vos) {
		return vos.stream().map(vo -> voToEntity(vo)).collect(Collectors.toList());

	}

}
