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
import com.otsi.retail.inventory.exceptions.DataNotFoundException;
import com.otsi.retail.inventory.exceptions.DuplicateRecordException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.CatalogMapper;
import com.otsi.retail.inventory.model.CatalogEntity;
import com.otsi.retail.inventory.repo.CatalogRepository;
import com.otsi.retail.inventory.vo.CatalogVo;

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
	public CatalogVo saveCatalogDetails(CatalogVo catalog) throws Exception {
		CatalogEntity entity = new CatalogEntity();

		entity = catalogMapper.convertVoToEntity(catalog);
		if (entity == null) {
			throw new DataNotFoundException("Data not found");
		}

		if (catalog.getCUID() == 0) {
			entity.setParent(null);
		} else {
			CatalogEntity centity = this.catalogRepository.getById(catalog.getCUID());
			entity.setParent(centity);
		}

		catalogRepository.save(entity);

		CatalogVo vo = catalogMapper.convertEntityToVo(entity);
		if (entity.getParent() != null) {
			vo.setCUID(entity.getParent().getId());
		} else {
			vo.setCUID(null);

		}
		return vo;

	}

	@Override
	public CatalogVo getCatalogByName(String name) throws Exception {

		Optional<CatalogEntity> names = catalogRepository.findByName(name);

		if (!names.isPresent()) {
			throw new RecordNotFoundException("Given catalog name is not exists");
		} else {

			CatalogVo vo = catalogMapper.convertEntityToVo(names.get());
			return vo;
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
	public List<CatalogVo> getCategories(Long id) {
		Optional<CatalogEntity> entity = catalogRepository.findById(id);
		if (!entity.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "catalog not found for id:" + id);
		}
		List<CatalogEntity> catalogs = catalogRepository.findByParentId(entity.get().getId());
		if (catalogs.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<CatalogVo> catalogsVO = catalogMapper.convertlistEntityToVo(catalogs);
		return catalogsVO;
	}

	@Override
	public List<CatalogVo> getMainCategories() {

		List<CatalogEntity> ent = catalogRepository.findByDescription(Categories.DIVISION);
		if (ent.isEmpty()) {
			throw new RecordNotFoundException("record not exists");
		}

		List<CatalogVo> lvo = catalogMapper.convertlEntityToVo(ent);
		return lvo;
	}

	@Override
	public List<CatalogVo> getAllCategories() {
		List<CatalogEntity> listOfCategories = catalogRepository.findAll();
		if (listOfCategories.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<CatalogVo> catalogList = catalogMapper.convertlEntityToVo(listOfCategories);
		return catalogList;
	}

}
