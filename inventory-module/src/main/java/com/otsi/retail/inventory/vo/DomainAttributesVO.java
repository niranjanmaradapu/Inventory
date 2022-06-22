package com.otsi.retail.inventory.vo;

import java.util.List;

import com.otsi.retail.inventory.commons.DomainType;

import lombok.Data;



@Data
public class DomainAttributesVO  {

	private Long id;
	
	private String name;
	
	private String placeholder;
	
	private String type;
	
	private List<String> values;
	
	private String selectedValue;
	
	private DomainType domainType;
	
}
