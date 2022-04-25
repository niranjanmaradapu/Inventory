package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.vo.ProductTransactionVo;

@Component
public class ProductTransactionMapper {


	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public ProductTransactionVo EntityToVo(ProductTransaction dto) {
		ProductTransactionVo vo = new ProductTransactionVo();
		vo.setProductTransactionId(dto.getProductTransactionId());
		vo.setBarcodeId(dto.getBarcodeId());
		vo.setComment(dto.getComment());
		vo.setEffectingTable(dto.getEffectingTable());
		vo.setEffectingTableId(dto.getEffectingTableId());
		vo.setQuantity(dto.getQuantity());
		vo.setMasterFlag(dto.isMasterFlag());
		vo.setNatureOfTransaction(dto.getNatureOfTransaction());
		vo.setCreatedDate(dto.getCreatedDate());
		vo.setLastModifiedDate(dto.getLastModifiedDate());
		vo.setStoreId(dto.getStoreId());
		return vo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<ProductTransactionVo> EntityToVo(List<ProductTransaction> dtos) {
		return dtos.stream().map(dto -> EntityToVo(dto)).collect(Collectors.toList());

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public ProductTransaction VoToEntity(ProductTransactionVo vo) {
		ProductTransaction dto = new ProductTransaction();
		dto.setMasterFlag(true);
		dto.setStoreId(vo.getStoreId());
		return dto;

	}

	/*
	 * to convert list vo's to dto's
	 */

	public List<ProductTransaction> VoToEntity(List<ProductTransactionVo> vos) {
		return vos.stream().map(vo -> VoToEntity(vo)).collect(Collectors.toList());

	}

}
