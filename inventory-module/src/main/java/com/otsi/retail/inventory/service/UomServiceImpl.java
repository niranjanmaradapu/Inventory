package com.otsi.retail.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.UomMapper;
import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.repo.UomRepo;
import com.otsi.retail.inventory.vo.UomVo;

@Component
public class UomServiceImpl implements UomService {

	private Logger log = LogManager.getLogger(UomServiceImpl.class);

	@Autowired
	private UomRepo uomRepo;

	@Autowired
	private UomMapper uomMapper;

	@Override
	public UomVo saveUom(UomVo vo) {
		log.debug("debugging saveUom:" + vo);
		UomEntity uom = uomMapper.VoToEntity(vo);
		UomEntity uomSave = uomRepo.save(uom);
		UomVo um = uomMapper.EntityToVo(uomSave);
		log.info("after saving uom details:" + vo.toString());
		return um;
	}

	@Override
	public Optional<UomEntity> getUom(Long id) {
		log.debug("debugging getUom:" + id);
		Optional<UomEntity> vo = uomRepo.findById(id);
		if (!(vo.isPresent())) {
			log.error("uom  record is not found");
			throw new RecordNotFoundException("uom record is not found");
		}
		log.warn("we are checking if uom is fetching by id...");
		log.info("after fetching uom details:" + vo.toString());
		return vo;
	}

	@Override
	public List<UomVo> getAllUom() {
		log.debug("debugging getAllUom");
		List<UomVo> uoms = new ArrayList<>();
		List<UomEntity> uom = uomRepo.findAll();
		uom.stream().forEach(um -> {
			UomVo uomVo = uomMapper.EntityToVo(um);
			uoms.add(uomVo);
		});
		log.warn("we are checking if all uoms are fetching...");
		log.info("after fetching all uoms details:" + uoms.toString());
		return uoms;
	}

	@Override
	public String updateUom(UomVo uomVo) {
		log.debug("debugging updateUom:" + uomVo);
		Optional<UomEntity> uomOpt = uomRepo.findById(uomVo.getId());
		if (!uomOpt.isPresent()) {
			throw new RecordNotFoundException("uom data is  not found with id: " + uomVo.getId());
		}
		UomEntity uom = uomMapper.VoToEntity(uomVo);
		uom.setId(uomVo.getId());
		UomEntity uomUpdate = uomRepo.save(uom);
		log.warn("we are checking if uom is updated...");
		log.info("after updating uom details:" + uomUpdate);
		return "after updated uom successfully:" + uomVo.toString();
	}

	@Override
	public String deleteUom(Long id) {
		log.debug("debugging deleteUom:" + id);
		Optional<UomEntity> uomOpt = uomRepo.findById(id);
		if (!(uomOpt.isPresent())) {
			throw new RecordNotFoundException("uom not found with id: " + id);
		}
		uomRepo.delete(uomOpt.get());
		log.warn("we are checking if uom is deleted...");
		log.info("after deleting uom details:" + id);
		return "uom data deleted succesfully: " + id;
	}

}
