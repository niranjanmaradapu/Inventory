package com.otsi.retail.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.ProductInventory;

@Repository
public interface ProductInventoryRepo extends JpaRepository<ProductInventory, Long>{


}
