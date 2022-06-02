package com.otsi.retail.inventory.vo;

import lombok.Data;

@Data
public class ProductTransactionVO extends BaseEntityVo {
	
	private Long id;
	
	private Long storeId;
	
	private String barcodeId;
	
	private int quantity;
	
	private String natureOfTransaction;
	
	private String effectingTable;
	
	private Long effectingTableId;
	
	private boolean masterFlag;
	
	private String comment;


}
