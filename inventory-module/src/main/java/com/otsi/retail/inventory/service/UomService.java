package com.otsi.retail.inventory.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.vo.UomVO;

@Service
public interface UomService {

	UomVO saveUom(UomVO vo);

	Optional<UomEntity> getUom(Long id);

	List<UomVO> getAllUom();

	UomVO updateUom(UomVO uomVo);

	void deleteUom(Long id);

}
