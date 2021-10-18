package com.otsi.retail.inventory.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.ProductItem;

@Repository
public interface ProductItemRepo extends JpaRepository<ProductItem, Long> {

	Optional<ProductItem> findByProductItemId(Long productItemId);

	boolean findByBarcodeBarcode(String barcode);

	ProductItem findByNameAndUomAndCostPriceAndListPrice(String name, String uom, float costPrice, float listPrice);

	Optional<ProductItem> findByName(String name);

	//Optional<ProductItem> findByBarcodeId(Long barcodeId);

	Optional<ProductItem> findByBarcodeBarcodeId(Long barcodeId);
   
	//ProductItem findByAttr1AndAttr2AndAttr3(String attr1, String attr2, String attr3);


}
