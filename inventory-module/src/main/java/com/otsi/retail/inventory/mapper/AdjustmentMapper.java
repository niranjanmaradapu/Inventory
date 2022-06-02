package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.vo.AdjustmentsVO;

@Component
public class AdjustmentMapper {

	public AdjustmentsVO entityToVO(Adjustments entity) {
		AdjustmentsVO vo = new AdjustmentsVO();
		vo.setAdjustmentId(entity.getAdjustmentId());
		vo.setCreatedBy(entity.getCreatedBy());
		vo.setCurrentBarcodeId(entity.getCurrentBarcodeId());
		vo.setToBeBarcodeId(entity.getToBeBarcodeId());
		vo.setCreatedDate(entity.getCreatedDate());
		vo.setLastModifiedDate(entity.getLastModifiedDate());
		vo.setComments(entity.getComments());
		vo.setStoreId(entity.getStoreId());
		return vo;

	}

	public List<AdjustmentsVO> entityToVO(List<Adjustments> entities) {
		return entities.stream().map(entity -> entityToVO(entity)).collect(Collectors.toList());

	}

	public Adjustments voToEntity(AdjustmentsVO vo) {
		Adjustments entity = new Adjustments();
		entity.setAdjustmentId(vo.getAdjustmentId());
		entity.setCurrentBarcodeId(vo.getCurrentBarcodeId());
		entity.setToBeBarcodeId(vo.getToBeBarcodeId());
		entity.setComments(vo.getComments());
		return entity;

	}

	public List<Adjustments> voToEntity(List<AdjustmentsVO> vos) {
		return vos.stream().map(vo -> voToEntity(vo)).collect(Collectors.toList());

	}

}
