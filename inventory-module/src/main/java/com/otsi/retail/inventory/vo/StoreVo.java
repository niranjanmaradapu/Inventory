package com.otsi.retail.inventory.vo;

import java.time.LocalDate;

import lombok.Data;
@Data
public class StoreVo {
	private long id;
	private String name;
	private long stateId;
	private String stateCode;
	private long districtId;
	private String cityId;
	private String area;
	private String address;
	private String phoneNumber;
	private LocalDate createdDate;
	private LocalDate lastModifyedDate;
	private String createdBy;
	private UserDetailsVo storeOwner;
	private long domainId;
	private String gstNumber;
	private long clientId;
}
