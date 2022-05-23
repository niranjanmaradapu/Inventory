package com.otsi.retail.inventory.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;


@Data
public class UserDetailsVo {
	private Long userId;
	private String userName;
	private String phoneNumber;
	private String gender;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
	private String createdBy;
	private Boolean isActive;
	private Boolean isSuperAdmin;
	private Boolean isCustomer;
	private Role role;
	private List<ClientDomains> clientDomians;
	private List<UserAv> userAv;
	private List<StoreVo> stores;
	private StoreVo ownerOf;
	private Long modifiedBy;



}
