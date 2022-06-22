package com.otsi.retail.inventory.mapper;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.DomainAttributes;
import com.otsi.retail.inventory.vo.DomainAttributesVO;

@Component
public class DomainAttributesMapper {

	public DomainAttributes toEntity(DomainAttributesVO vo , DomainAttributes entity) {
		if(entity == null) {
		 entity = new DomainAttributes();
		}
		entity.setName(vo.getName());
		entity.setDomainType(vo.getDomainType());
		entity.setPlaceholder(vo.getPlaceholder());
		entity.setSelectedValue(vo.getPlaceholder());
		entity.setType(vo.getType());
		entity.setValues(vo.getValues());
		return entity;
	}
	
	
	public DomainAttributesVO toVO(DomainAttributes entity) {
		DomainAttributesVO vo = new DomainAttributesVO();
		vo.setName(entity.getName());
		vo.setDomainType(entity.getDomainType());
		vo.setPlaceholder(entity.getPlaceholder());
		vo.setSelectedValue(entity.getPlaceholder());
		vo.setType(entity.getType());
		vo.setValues(entity.getValues());
		return vo;
	}

}
