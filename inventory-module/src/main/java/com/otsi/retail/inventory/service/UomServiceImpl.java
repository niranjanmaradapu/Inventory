package com.otsi.retail.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.UomMapper;
import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.repo.UomRepository;
import com.otsi.retail.inventory.vo.UomVO;

@Component
public class UomServiceImpl implements UomService {

	private Logger log = LogManager.getLogger(UomServiceImpl.class);

	@Autowired
	private UomRepository uomRepository;

	@Autowired
	private UomMapper uomMapper;

	@Override
	public UomVO saveUom(UomVO vo) {
		UomEntity uom = uomMapper.voToEntity(vo);
		UomEntity uomSave = uomRepository.save(uom);
		return uomMapper.entityToVO(uomSave);
	}

	@Override
	public Optional<UomEntity> getUom(Long id) {
		Optional<UomEntity> uomOptional = uomRepository.findById(id);
		if (uomOptional.isPresent()) {
			return uomOptional;
		}
		return Optional.empty();
	}

	@Override
	public List<UomVO> getAllUom() {
		List<UomVO> uoms = new ArrayList<>();
		List<UomEntity> uom = uomRepository.findAll();
		uom.stream().forEach(um -> {
			UomVO uomVo = uomMapper.entityToVO(um);
			uoms.add(uomVo);
		});
		return uoms;
	}

	@Override
	public UomVO updateUom(UomVO uomVo) {
		Optional<UomEntity> uomOpt = uomRepository.findById(uomVo.getId());
		if (!uomOpt.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid uom id:" + uomVo.getId());
		}
		UomEntity uom = uomMapper.voToEntity(uomVo);
		uom.setId(uomVo.getId());
		uom = uomRepository.save(uom);
		return uomMapper.entityToVO(uom);
	}

	@Override
	public void deleteUom(Long id) {
		Optional<UomEntity> uomOptional = uomRepository.findById(id);
		if (!(uomOptional.isPresent())) {
			throw new RecordNotFoundException("invalid uom id:" + id);
		}
		uomRepository.delete(uomOptional.get());
	}

}
