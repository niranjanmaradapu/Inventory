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
<<<<<<< HEAD
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
	private String createdBy;
	private Boolean isActive;
	private Boolean isSuperAdmin;
	private Boolean isCustomer;
	private Role role;
	private List<ClientDomains> clientDomians;
=======

	private LocalDate createdDate;

	private LocalDate lastModifiedDate;

	private String createdBy;

	private Role role;

>>>>>>> alpha-release
	private List<UserAv> userAv;

	private List<StoreVo> stores;

	private StoreVo ownerOf;
	private Long modifiedBy;


	private Boolean isActive;

	private Boolean isSuperAdmin;

	private Boolean isCustomer;

	private Long modifiedBy;

}
