/**
 * 
 */
package com.otsi.retail.inventory.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.otsi.retail.inventory.commons.Categories;
import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.exceptions.DataNotFoundException;
import com.otsi.retail.inventory.exceptions.DuplicateRecordException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.CatalogMapper;
import com.otsi.retail.inventory.model.CatalogEntity;
import com.otsi.retail.inventory.repo.CatalogRepository;
import com.otsi.retail.inventory.vo.CatalogVO;

/**
 * @author Sudheer.Swamy
 *
 */
@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private CatalogMapper catalogMapper;

	@Override
	public CatalogVO saveCatalogDetails(CatalogVO catalogVo) throws Exception {
		CatalogEntity catalog = new CatalogEntity();

		catalog = catalogMapper.convertVoToEntity(catalogVo);
		if (catalog == null) {
			throw new DataNotFoundException("Data not found");
		}

		if (catalogVo.getCUID() == 0) {
			catalog.setParent(null);
		} else {
			CatalogEntity centity = this.catalogRepository.getById(catalogVo.getCUID());
			catalog.setParent(centity);
		}

		catalogRepository.save(catalog);

		CatalogVO catalogVO = catalogMapper.convertEntityToVO(catalog);
		if (catalog.getParent() != null) {
			catalogVO.setCUID(catalog.getParent().getId());
		} else {
			catalogVO.setCUID(null);

		}
		return catalogVO;

	}

	@Override
	public CatalogVO getCatalogByName(String name) throws Exception {

		Optional<CatalogEntity> names = catalogRepository.findByName(name);

		if (!names.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given catalog name is not exists");
		} else {
			CatalogVO catalogVO = catalogMapper.convertEntityToVO(names.get());
			return catalogVO;
		}

	}

	@Override
	public void deleteCategoryById(Long id) throws Exception {

		if (catalogRepository.findById(id).isPresent()) {
			if (catalogRepository.findByParentId(id).isEmpty()) {
				catalogRepository.deleteById(id);
			} else {
				throw new DuplicateRecordException(
						"Failed to delete,  Please delete child categories associated with this category");
			}

		} else

			throw new RecordNotFoundException("record Not found");
	}

	@Override
	public List<CatalogVO> getCategories(Long id, DomainType domainType) {
		Optional<CatalogEntity> catalog = catalogRepository.findById(id);
		if (!catalog.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "catalog not found for id:" + id);
		}
		List<CatalogEntity> catalogs = catalogRepository.findByParentIdAndDomainType(catalog.get().getId(), domainType);
		if (catalogs.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<CatalogVO> catalogsVO = catalogMapper.convertEntityToVO(catalogs);
		return catalogsVO;
	}

	@Override
	public List<CatalogVO> getMainCategories(DomainType domainType) {

		List<CatalogEntity> catalogList = catalogRepository.findByDescriptionAndDomainType(Categories.DIVISION,domainType);
		if (catalogList.isEmpty()) {
			throw new RecordNotFoundException("record not exists");
		}

		List<CatalogVO> catalogVoList = catalogMapper.convertlEntityToVo(catalogList);
		return catalogVoList;
	}

	@Override
	public List<CatalogVO> getAllCategories(DomainType domainType) {
		List<CatalogEntity> listOfCategories = catalogRepository.findByDomainType(domainType);
		if (listOfCategories.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<CatalogVO> catalogVoList = catalogMapper.convertlEntityToVo(listOfCategories);
		return catalogVoList;
	}

}
