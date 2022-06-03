package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.vo.ProductTransactionVO;

@Component
public class ProductTransactionMapper {


	public ProductTransactionVO entityToVO(ProductTransaction productTransaction) {
		ProductTransactionVO productTransactionVo = new ProductTransactionVO();
		productTransactionVo.setId(productTransaction.getId());
		productTransactionVo.setBarcodeId(productTransaction.getBarcodeId());
		productTransactionVo.setComment(productTransaction.getComment());
		productTransactionVo.setEffectingTable(productTransaction.getEffectingTable());
		productTransactionVo.setEffectingTableId(productTransaction.getEffectingTableId());
		productTransactionVo.setQuantity(productTransaction.getQuantity());
		productTransactionVo.setMasterFlag(productTransaction.isMasterFlag());
		productTransactionVo.setNatureOfTransaction(productTransaction.getNatureOfTransaction());
		productTransactionVo.setCreatedDate(productTransaction.getCreatedDate());
		productTransactionVo.setLastModifiedDate(productTransaction.getLastModifiedDate());
		productTransactionVo.setStoreId(productTransaction.getStoreId());
		return productTransactionVo;

	}


	public List<ProductTransactionVO> entityToVO(List<ProductTransaction> productTransactionList) {
		return productTransactionList.stream().map(productTransaction -> entityToVO(productTransaction)).collect(Collectors.toList());

	}


	public ProductTransaction voToEntity(ProductTransactionVO productTransactionVo) {
		ProductTransaction productTransaction = new ProductTransaction();
		productTransaction.setMasterFlag(true);
		productTransaction.setStoreId(productTransactionVo.getStoreId());
		return productTransaction;

	}

	
	public List<ProductTransaction> voToEntity(List<ProductTransactionVO> productTransactionVoList) {
		return productTransactionVoList.stream().map(productTransactionVo -> voToEntity(productTransactionVo)).collect(Collectors.toList());

	}

}
