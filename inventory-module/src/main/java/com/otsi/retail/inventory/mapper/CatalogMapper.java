/**
 * 
 */
package com.otsi.retail.inventory.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.CatalogEntity;
import com.otsi.retail.inventory.vo.CatalogVo;

/**
 * @author Sudheer.Swamy
 *
 */
@Component
public class CatalogMapper {

	public CatalogEntity convertVoToEntity(CatalogVo vo) {

		CatalogEntity entity = new CatalogEntity();
		// entity.setId(vo.getId());
		entity.setName(vo.getName());
		entity.setDescription(vo.getDescription());
		entity.setStatus(vo.getStatus());
		entity.setCatergory(vo.getCategory());

		return entity;

	}

	public List<CatalogVo> convertlistEntityToVo(List<CatalogEntity> pentity) {

		List<CatalogVo> lvo = new ArrayList<CatalogVo>();
		pentity.stream().forEach(x -> {

			CatalogVo vo = new CatalogVo();
			vo.setId(x.getId());
			vo.setName(x.getName());
			vo.setCategory(x.getCatergory());
			vo.setDescription(x.getDescription());
			vo.setStatus(x.getStatus());
			vo.setCUID(x.getParent().getId());
			lvo.add(vo);
		});

		return lvo;

	}

	public CatalogVo convertEntityToVo(CatalogEntity dtos) {
		CatalogVo vo = new CatalogVo();

		vo.setId(dtos.getId());
		vo.setName(dtos.getName());
		vo.setDescription(dtos.getDescription());
		vo.setStatus(dtos.getStatus());
		vo.setCreatedDate(dtos.getCreatedDate());
		vo.setLastModifiedDate(dtos.getLastModifiedDate());
		return vo;

	}

	public List<CatalogVo> convertlEntityToVo(List<CatalogEntity> dtos) {
		return dtos.stream().map(entity -> convertEntityToVo(entity)).collect(Collectors.toList());

	}

}
