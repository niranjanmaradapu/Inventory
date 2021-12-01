/**
 * 
 */
package com.otsi.retail.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsi.retail.catalog.common.Categories;
import com.otsi.retail.catalog.model.CatalogEntity;
import com.otsi.retail.catalog.vo.CatalogVo;

/**
 * @author Sudheer.Swamy
 *
 */
@Repository
public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

	Optional<CatalogEntity> findById(int i);

	Optional<CatalogEntity> findByName(String name);

	CatalogVo save(CatalogVo update);

	CatalogEntity getById(int cuid);

	List<CatalogEntity> findByParentId(Long id);

	List<CatalogEntity> findByDescription(Categories mainCategory);

	//CatalogEntity findByCategory(Categories catgeory);
}
