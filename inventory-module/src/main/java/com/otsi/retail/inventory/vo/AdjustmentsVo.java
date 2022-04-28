package com.otsi.retail.inventory.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdjustmentsVo {

	private Long adjustmentId;
	private String currentBarcodeId;
	private String toBeBarcodeId;
	private Long createdBy;
	private String comments;
	private LocalDateTime createdDate;
	private LocalDateTime LastModifiedDate;
	private Long storeId;
}
