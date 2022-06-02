/**
 * 
 */
package com.otsi.retail.inventory.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.otsi.retail.inventory.model.CatalogEntity;
import com.otsi.retail.inventory.vo.CatalogVO;

/**
 * @author Sudheer.Swamy
 *
 */
@Component
public class CatalogMapper {

	public CatalogEntity convertVoToEntity(CatalogVO vo) {
		CatalogEntity entity = new CatalogEntity();
		entity.setName(vo.getName());
		entity.setDescription(vo.getDescription());
		entity.setStatus(vo.getStatus());
		entity.setCatergory(vo.getCategory());
		return entity;

	}

	public List<CatalogVO> convertEntityToVO(List<CatalogEntity> entities) {
		List<CatalogVO> vos = new ArrayList<CatalogVO>();
		entities.stream().forEach(x -> {
			CatalogVO vo = new CatalogVO();
			vo.setId(x.getId());
			vo.setName(x.getName());
			vo.setCategory(x.getCatergory());
			vo.setDescription(x.getDescription());
			vo.setStatus(x.getStatus());
			vo.setCUID(x.getParent().getId());
			vos.add(vo);
		});
		return vos;
	}

	public CatalogVO convertEntityToVO(CatalogEntity entities) {
		CatalogVO vo = new CatalogVO();
		vo.setId(entities.getId());
		vo.setName(entities.getName());
		vo.setDescription(entities.getDescription());
		vo.setStatus(entities.getStatus());
		vo.setCreatedDate(entities.getCreatedDate());
		vo.setLastModifiedDate(entities.getLastModifiedDate());
		return vo;

	}

	public List<CatalogVO> convertlEntityToVo(List<CatalogEntity> entities) {
		return entities.stream().map(entity -> convertEntityToVO(entity)).collect(Collectors.toList());

	}

}
