package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.vo.UomVO;

@Component
public class UomMapper {

	public UomVO entityToVO(UomEntity uom) {
		UomVO uomVo = new UomVO();
		uomVo.setId(uom.getId());
		uomVo.setUomName(uom.getUomName());
		return uomVo;

	}

	public List<UomVO> entityToVO(List<UomEntity> uomList) {
		return uomList.stream().map(uom -> entityToVO(uom)).collect(Collectors.toList());

	}

	public UomEntity voToEntity(UomVO uomVo) {
		UomEntity uom = new UomEntity();
		uom.setId(uomVo.getId());
		uom.setUomName(uomVo.getUomName());
		return uom;

	}

	public List<UomEntity> VoToEntity(List<UomVO> uomVoList) {
		return uomVoList.stream().map(uomVo -> voToEntity(uomVo)).collect(Collectors.toList());

	}

}
