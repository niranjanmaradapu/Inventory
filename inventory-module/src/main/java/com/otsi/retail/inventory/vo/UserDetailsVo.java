package com.otsi.retail.inventory.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;


@Data
public class UserDetailsVo {
	private Long userId;
	private String userName;
	private String phoneNumber;
	private String gender;
	private LocalDate createdDate;
	private LocalDate lastModifyedDate;
	private String createdBy;
	private Role role;
	private List<UserAv> userAv;
	private List<StoreVo> stores;
	private StoreVo ownerOf;


}
