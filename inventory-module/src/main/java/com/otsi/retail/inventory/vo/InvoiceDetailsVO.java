package com.otsi.retail.inventory.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class InvoiceDetailsVO {
	
	private List<ProductVO> barcode;

	private int qty;
	
	private String dsNumber;

	private String type;

	private Long mrp;

	private Long promoDisc;

	private Long netAmount;

	private Long salesMan;

	private Date createdDate;

	private Date lastModified;


}
