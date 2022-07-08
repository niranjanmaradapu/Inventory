package com.otsi.retail.inventory.vo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.otsi.retail.inventory.commons.ProductStatus;

import lombok.Data;

@Data
public class SearchFilterVo {

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate fromDate;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate toDate;

	private Long empId;

	private float itemMrpLessThan;

	private float itemMrpGreaterThan;

	private Long storeId;

	private String barcode;

	private ProductStatus status;

	private String currentBarcodeId;

}
