package com.otsi.retail.inventory.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;

import com.otsi.retail.inventory.commons.DomainType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "domain_attributes")
public class DomainAttributes extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	private String placeholder;
	
	private String type;
	
	@Type(type = "com.otsi.retail.inventory.commons.CustomJsonListType")
	@Target(String.class)
	private List<String> values;
	
	private String selectedValue;
	
	@Enumerated(EnumType.STRING)
	private DomainType domainType;
	
}
