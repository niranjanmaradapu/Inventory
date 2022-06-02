package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.vo.ProductTransactionVO;

@Component
public class ProductTransactionMapper {


	public ProductTransactionVO entityToVO(ProductTransaction entity) {
		ProductTransactionVO vo = new ProductTransactionVO();
		vo.setId(entity.getId());
		vo.setBarcodeId(entity.getBarcodeId());
		vo.setComment(entity.getComment());
		vo.setEffectingTable(entity.getEffectingTable());
		vo.setEffectingTableId(entity.getEffectingTableId());
		vo.setQuantity(entity.getQuantity());
		vo.setMasterFlag(entity.isMasterFlag());
		vo.setNatureOfTransaction(entity.getNatureOfTransaction());
		vo.setCreatedDate(entity.getCreatedDate());
		vo.setLastModifiedDate(entity.getLastModifiedDate());
		vo.setStoreId(entity.getStoreId());
		return vo;

	}


	public List<ProductTransactionVO> entityToVO(List<ProductTransaction> entities) {
		return entities.stream().map(dto -> entityToVO(dto)).collect(Collectors.toList());

	}


	public ProductTransaction voToEntity(ProductTransactionVO vo) {
		ProductTransaction entity = new ProductTransaction();
		entity.setMasterFlag(true);
		entity.setStoreId(vo.getStoreId());
		return entity;

	}

	
	public List<ProductTransaction> voToEntity(List<ProductTransactionVO> vos) {
		return vos.stream().map(vo -> voToEntity(vo)).collect(Collectors.toList());

	}

}
