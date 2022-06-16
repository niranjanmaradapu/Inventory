package com.otsi.retail.inventory.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.model.DomainAttributes;

@Repository
public interface DomainAttributesRepository extends JpaRepository<DomainAttributes, Long> {

	List<DomainAttributes> findByDomainType(DomainType domainType);

}
