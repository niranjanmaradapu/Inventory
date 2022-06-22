package com.otsi.retail.inventory.mapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.model.Product;
import com.otsi.retail.inventory.vo.InvoiceDetailsVO;
import com.otsi.retail.inventory.vo.ProductVO;

@Component
public class ProductMapper {

	public ProductVO entityToVO(Product product) {
		ProductVO productVo = new ProductVO();

		BeanUtils.copyProperties(product, productVo);
		productVo.setEmpId(product.getEmpId());
		productVo.setFromDate(product.getCreatedDate());
		productVo.setToDate(product.getLastModifiedDate());
		productVo.setBarcode(product.getBarcode());
		productVo.setDivision(product.getDivision());
		productVo.setSection(product.getSection());
		productVo.setSubSection(product.getSubSection());
		productVo.setName(product.getName());
		productVo.setStatus(product.getStatus());
		productVo.setCategory(product.getCategory());
		productVo.setBatchNo(product.getBatchNo());
		productVo.setColour(product.getColour());
		productVo.setOriginalBarcodeCreatedAt(product.getOriginalBarcodeCreatedAt());
		productVo.setStoreId(product.getStoreId());
		productVo.setDomainId(product.getDomainId());
		productVo.setSellingTypeCode(product.getSellingTypeCode());
		return productVo;

	}

	public List<ProductVO> entityToVO(List<Product> productList) {
		return productList.stream().map(product -> entityToVO(product)).collect(Collectors.toList());

	}

	public Product customVoToEntityMapper(ProductVO productVo) {
		Product product = new Product();
		product.setQty(productVo.getQty());
		product.setId(productVo.getId());
		return product;

	}

	public Product voToEntity(ProductVO productVo) {
		Product product = new Product();
		BeanUtils.copyProperties(productVo, product);
		product.setCostPrice(productVo.getCostPrice());
		product.setEmpId(productVo.getEmpId());
		product.setStatus(ProductStatus.ENABLE);
		product.setName(productVo.getName());
		product.setBarcode(productVo.getBarcode());
		product.setDivision(productVo.getDivision());
		product.setSection(productVo.getSection());
		product.setSubSection(productVo.getSubSection());
		product.setOriginalBarcodeCreatedAt(LocalDate.now());
		product.setCategory(productVo.getCategory());
		product.setBatchNo(productVo.getBatchNo());
		product.setColour(productVo.getColour());
		product.setStoreId(productVo.getStoreId());
		product.setDomainId(productVo.getDomainId());
		product.setSellingTypeCode(productVo.getSellingTypeCode());
		return product;

	}

	public List<Product> VoToEntity(List<ProductVO> productVoList) {
		return productVoList.stream().map(productVo -> voToEntity(productVo)).collect(Collectors.toList());

	}

	public InvoiceDetailsVO productToInvoiceMapper(ProductVO vo) {
		InvoiceDetailsVO invoiceDetailsVO = new InvoiceDetailsVO();
		ProductVO productVO = new ProductVO();
		productVO.setBarcode(vo.getBarcode());
		productVO.setItemMrp(vo.getItemMrp());
		productVO.setQty(vo.getQty());
		productVO.setSection(vo.getSection());
		productVO.setSubSection(vo.getSubSection());
		productVO.setDivision(vo.getDivision());
		productVO.setHsnCode(vo.getHsnCode());
		productVO.setStoreId(vo.getStoreId());
		// GrossValue is multiple of net value of product and quantity
		if (productVO.getTaxValues() != null) {
			productVO.setTaxValues(vo.getTaxValues());
		}
		invoiceDetailsVO.setBarcode(Arrays.asList(productVO));
		return invoiceDetailsVO;
	}

}
