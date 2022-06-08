package com.otsi.retail.inventory.vo;

import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

@Data
public class LineItemVO {

	private Long lineItemId;

	private Long domainId;

	private Long storeId;

	private String barCode;

	private Long section;
	
	private Long subSection;

	private Long division;

	private Long userId;

	private Float itemPrice;

	private int quantity;

	private Float grossValue;

	private String hsnCode;

	private Float actualValue;

	private Float taxValue;

	private Long cgst;

	private Long sgst;

	private Long discount;

	private Long netValue;

	private LocalDate creationDate;

	private LocalDate lastModified;
	
	private Map taxValues;


}
