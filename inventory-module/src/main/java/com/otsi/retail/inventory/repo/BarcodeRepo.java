package com.otsi.retail.inventory.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.otsi.retail.inventory.model.Barcode;

@Repository
public interface BarcodeRepo extends JpaRepository<Barcode, Long> {

	boolean existsByBarcode(String barcode);

	Optional<Barcode> findByBarcode(String barcode);

	Barcode findByBarcodeId(Long barcodeId);

}
