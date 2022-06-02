package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.vo.UomVO;

@Component
public class UomMapper {

	public UomVO entityToVO(UomEntity entity) {
		UomVO vo = new UomVO();
		vo.setId(entity.getId());
		vo.setUomName(entity.getUomName());
		return vo;

	}

	public List<UomVO> entityToVO(List<UomEntity> dtos) {
		return dtos.stream().map(dto -> entityToVO(dto)).collect(Collectors.toList());

	}

	public UomEntity voToEntity(UomVO vo) {
		UomEntity entity = new UomEntity();
		entity.setId(vo.getId());
		entity.setUomName(vo.getUomName());
		return entity;

	}

	public List<UomEntity> VoToEntity(List<UomVO> vos) {
		return vos.stream().map(vo -> voToEntity(vo)).collect(Collectors.toList());

	}

}
