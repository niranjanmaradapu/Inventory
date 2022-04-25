package com.otsi.retail.inventory.vo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdjustmentsVo {

	private Long adjustmentId;
	private String currentBarcodeId;
	private String toBeBarcodeId;
	private Long createdBy;
	private String comments;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Long storeId;
}
