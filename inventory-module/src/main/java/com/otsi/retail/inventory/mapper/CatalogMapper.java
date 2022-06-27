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

	public CatalogEntity convertVoToEntity(CatalogVO catalogVo) {
		CatalogEntity catalog = new CatalogEntity();
		catalog.setName(catalogVo.getName());
		catalog.setDescription(catalogVo.getDescription());
		catalog.setStatus(catalogVo.getStatus());
		catalog.setCatergory(catalogVo.getCategory());
		return catalog;

	}

	public List<CatalogVO> convertEntityToVO(List<CatalogEntity> catalogList) {
		List<CatalogVO> catalogVoList = new ArrayList<CatalogVO>();
		catalogList.stream().forEach(catalog -> {
			CatalogVO catalogVo = new CatalogVO();
			catalogVo.setId(catalog.getId());
			catalogVo.setName(catalog.getName());
			catalogVo.setCategory(catalog.getCatergory());
			catalogVo.setDescription(catalog.getDescription());
			catalogVo.setStatus(catalog.getStatus());
			catalogVo.setCUID(catalog.getParent().getId());
			catalogVo.setDomainType(catalog.getDomainType());
			catalogVoList.add(catalogVo);
		});
		return catalogVoList;
	}

	public CatalogVO convertEntityToVO(CatalogEntity catalog) {
		CatalogVO catalogVo = new CatalogVO();
		catalogVo.setId(catalog.getId());
		catalogVo.setName(catalog.getName());
		catalogVo.setDescription(catalog.getDescription());
		catalogVo.setStatus(catalog.getStatus());
		catalogVo.setCreatedDate(catalog.getCreatedDate());
		catalogVo.setCategory(catalog.getCatergory());
		catalogVo.setLastModifiedDate(catalog.getLastModifiedDate());
		catalogVo.setDomainType(catalog.getDomainType());
		return catalogVo;

	}

	public List<CatalogVO> convertlEntityToVo(List<CatalogEntity> catalogList) {
		return catalogList.stream().map(catalog -> convertEntityToVO(catalog)).collect(Collectors.toList());

	}

}
