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
public class ProductTextileMapper {

	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public ProductVO EntityToVo(Product dto) {
		ProductVO vo = new ProductVO();

		BeanUtils.copyProperties(dto, vo);
		vo.setEmpId(dto.getEmpId());
		vo.setFromDate(dto.getCreatedDate());
		vo.setToDate(dto.getLastModifiedDate());
		vo.setBarcode(dto.getBarcode());
		vo.setDivision(dto.getDivision());
		vo.setSection(dto.getSection());
		vo.setSubSection(dto.getSubSection());
		vo.setName(dto.getName());
		vo.setStatus(dto.getStatus());
		vo.setCategory(dto.getCategory());
		vo.setBatchNo(dto.getBatchNo());
		vo.setColour(dto.getColour());
		vo.setOriginalBarcodeCreatedAt(dto.getOriginalBarcodeCreatedAt());
		vo.setStoreId(dto.getStoreId());
		vo.setDomainId(dto.getDomainId());
		vo.setSellingTypeCode(dto.getSellingTypeCode());
		return vo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<ProductVO> EntityToVo(List<Product> dtos) {
		return dtos.stream().map(dto -> EntityToVo(dto)).collect(Collectors.toList());

	}

	public Product customVoToEntityMapper(ProductVO productVo) {
		Product product = new Product();
		product.setQty(productVo.getQty());
		product.setId(productVo.getId());
		return product;

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public Product VoToEntity(ProductVO vo) {
		Product dto = new Product();
		BeanUtils.copyProperties(vo, dto);
		dto.setCostPrice(vo.getCostPrice());
		dto.setEmpId(vo.getEmpId());
		dto.setStatus(ProductStatus.ENABLE);
		dto.setName(vo.getName());
		dto.setBarcode(vo.getBarcode());
		dto.setDivision(vo.getDivision());
		dto.setSection(vo.getSection());
		dto.setSubSection(vo.getSubSection());
		dto.setOriginalBarcodeCreatedAt(LocalDate.now());
		dto.setCategory(vo.getCategory());
		dto.setBatchNo(vo.getBatchNo());
		dto.setColour(vo.getColour());
		dto.setStoreId(vo.getStoreId());
		dto.setDomainId(vo.getDomainId());
		dto.setSellingTypeCode(vo.getSellingTypeCode());
		return dto;

	}

	/*
	 * to convert list vo's to dto's
	 */

	public List<Product> VoToEntity(List<ProductVO> vos) {
		return vos.stream().map(vo -> VoToEntity(vo)).collect(Collectors.toList());

	}

}
