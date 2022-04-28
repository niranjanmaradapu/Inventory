package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.vo.AdjustmentsVo;

@Component
public class AdjustmentMapper {

	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public AdjustmentsVo EntityToVo(Adjustments dto) {
		AdjustmentsVo vo = new AdjustmentsVo();
		vo.setAdjustmentId(dto.getAdjustmentId());
		vo.setCreatedBy(dto.getCreatedBy());
		vo.setCurrentBarcodeId(dto.getCurrentBarcodeId());
		vo.setToBeBarcodeId(dto.getToBeBarcodeId());
		vo.setCreatedDate(dto.getCreatedDate());
		vo.setLastModifiedDate(dto.getLastModifiedDate());
		vo.setComments(dto.getComments());
		return vo;

	}

	/*
	 * to convert list dto's to vo's
	 */

	public List<AdjustmentsVo> EntityToVo(List<Adjustments> dtos) {
		return dtos.stream().map(dto -> EntityToVo(dto)).collect(Collectors.toList());

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public Adjustments VoToEntity(AdjustmentsVo vo) {
		Adjustments dto = new Adjustments();
	    dto.setAdjustmentId(vo.getAdjustmentId());
		//dto.setCreatedBy(vo.getCreatedBy());
		dto.setCurrentBarcodeId(vo.getCurrentBarcodeId());
		dto.setToBeBarcodeId(vo.getToBeBarcodeId());
		dto.setComments(vo.getComments());
		return dto;

	}

	/*
	 * to convert list vo's to dto's
	 */

	public List<Adjustments> VoToEntity(List<AdjustmentsVo> vos) {
		return vos.stream().map(vo -> VoToEntity(vo)).collect(Collectors.toList());

	}

}
