package com.otsi.retail.inventory.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransaction extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	
	private Long storeId;
	
	private String barcodeId;
	
	private Integer quantity;
	
	private String natureOfTransaction;
	
	private String effectingTable;
	
	private Long effectingTableId;
	
	private boolean masterFlag;
	
	private String comment;

}
