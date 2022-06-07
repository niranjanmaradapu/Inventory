package com.otsi.retail.inventory.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class InvoiceDetailsVO {
	
	private Long storeId;

	private int qty;
	
	private String type;

	private Long mrp;

	private Long promoDisc;

	private Long netAmount;

	private Long salesMan;

	private LocalDate createdDate;

	private LocalDate lastModifiedDate;
	
	private List<LineItemVO> lineItems;


}
