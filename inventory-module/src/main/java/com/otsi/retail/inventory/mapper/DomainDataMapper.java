package com.otsi.retail.inventory.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.model.Domaindata;
import com.otsi.retail.inventory.vo.DomaindataVo;

@Component
public class DomainDataMapper {

	/*
	 * EntityToVo converts dto to vo
	 * 
	 */

	public DomaindataVo EntityToVo(Domaindata dto) {
		DomaindataVo vo = new DomaindataVo();
		vo.setDomainDataId(dto.getDomainDataId());
		vo.setDuid(dto.getDuid());
		vo.setDomainName(dto.getDomainName());
		vo.setDescription(dto.getDescription());
		vo.setCreationdate(LocalDate.now());
		vo.setLastmodified(LocalDate.now());
		return vo;

	}

	/*
	 * VoToEntity converts vo to dto
	 * 
	 */

	public Domaindata VoToEntity(DomaindataVo vo) {
		Domaindata dto = new Domaindata();
		dto.setDomainDataId(vo.getDomainDataId());
		dto.setDuid(vo.getDuid());
		dto.setDomainName(vo.getDomainName());
		dto.setDescription(vo.getDescription());
		dto.setCreationdate(LocalDate.now());
		dto.setLastmodified(LocalDate.now());
		return dto;

	}

}
