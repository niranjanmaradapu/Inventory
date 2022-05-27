package com.otsi.retail.inventory.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ProductBundleVo {
	
	private Long id;

	private String name;

	private String description;

	private Boolean status;

	private Long domainId;
	
	private Long storeId;

	private Integer bundleQuantity;

	private List<ProductVO> productTextiles;

	private LocalDateTime fromDate;

	private LocalDateTime toDate;
	
	private Float value;

}
