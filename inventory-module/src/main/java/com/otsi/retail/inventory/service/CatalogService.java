/**
 * 
 */
package com.otsi.retail.inventory.service;

import java.util.List;

import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.vo.CatalogVO;

/**
 * @author Sudheer.Swamy
 *
 */
public interface CatalogService {

	public CatalogVO saveCatalogDetails(CatalogVO catalog) throws Exception;

	public CatalogVO getCatalogByName(String name) throws Exception;

	public void deleteCategoryById(Long id) throws Exception;

	public List<CatalogVO> getCategories(Long id, DomainType domainType);

	public List<CatalogVO> getMainCategories(DomainType domainType);

	public List<CatalogVO> getAllCategories(DomainType domainType);

}
