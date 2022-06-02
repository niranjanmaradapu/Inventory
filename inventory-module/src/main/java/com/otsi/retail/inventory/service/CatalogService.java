/**
 * 
 */
package com.otsi.retail.inventory.service;

import java.util.List;
import com.otsi.retail.inventory.vo.CatalogVO;

/**
 * @author Sudheer.Swamy
 *
 */
public interface CatalogService {

	public CatalogVO saveCatalogDetails(CatalogVO catalog) throws Exception;

	public CatalogVO getCatalogByName(String name) throws Exception;

	//public List<CatalogVo> getAllCatalogs();

	//public CatalogVo updateCatalog(Long id, CatalogVo vo) throws Exception;

	public void deleteCategoryById(Long id) throws Exception;

	public List<CatalogVO> getCategories(Long id);

	public List<CatalogVO> getMainCategories();

	public List<CatalogVO> getAllCategories();
	
	

}
