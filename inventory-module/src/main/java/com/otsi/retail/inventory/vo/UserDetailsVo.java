package com.otsi.retail.inventory.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserDetailsVo {
	private Long id;

	private String userName;

	private String phoneNumber;

	private String gender;

	private LocalDateTime createdDate;

	private LocalDateTime lastModifiedDate;

	private String createdBy;

	private Role role;

	private List<UserAv> userAv;

	private List<StoreVo> stores;

	private StoreVo ownerOf;

	private Boolean isActive;

	private Boolean isSuperAdmin;

	private Boolean isCustomer;

	private Long modifiedBy;

}
