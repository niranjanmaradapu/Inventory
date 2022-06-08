package com.otsi.retail.inventory.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.vo.AdjustmentsVO;

@Component
public class AdjustmentMapper {

	public AdjustmentsVO entityToVO(Adjustments adjustments) {
		AdjustmentsVO adjustmentsVo = new AdjustmentsVO();
		adjustmentsVo.setAdjustmentId(adjustments.getAdjustmentId());
		adjustmentsVo.setCreatedBy(adjustments.getCreatedBy());
		adjustmentsVo.setCurrentBarcodeId(adjustments.getCurrentBarcodeId());
		adjustmentsVo.setToBeBarcodeId(adjustments.getToBeBarcodeId());
		adjustmentsVo.setCreatedDate(adjustments.getCreatedDate());
		adjustmentsVo.setLastModifiedDate(adjustments.getLastModifiedDate());
		adjustmentsVo.setComments(adjustments.getComments());
		adjustmentsVo.setStoreId(adjustments.getStoreId());
		return adjustmentsVo;

	}

	public List<AdjustmentsVO> entityToVO(List<Adjustments> adjustmentList) {
		return adjustmentList.stream().map(adjustments -> entityToVO(adjustments)).collect(Collectors.toList());

	}

	public Adjustments voToEntity(AdjustmentsVO adjusmentsVo) {
		Adjustments adjusments = new Adjustments();
		adjusments.setAdjustmentId(adjusmentsVo.getAdjustmentId());
		adjusments.setCurrentBarcodeId(adjusmentsVo.getCurrentBarcodeId());
		adjusments.setToBeBarcodeId(adjusmentsVo.getToBeBarcodeId());
		adjusments.setComments(adjusmentsVo.getComments());
		return adjusments;

	}

	public List<Adjustments> voToEntity(List<AdjustmentsVO> adjustmentsVoList) {
		return adjustmentsVoList.stream().map(adjustmentsVo -> voToEntity(adjustmentsVo)).collect(Collectors.toList());

	}

}
