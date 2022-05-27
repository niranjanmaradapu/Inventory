package com.otsi.retail.inventory.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DomainTypePropertiesVO {

	private Long id;

	private String domainType;

	private List<Map<String, FieldNameVO>> properties;

}
