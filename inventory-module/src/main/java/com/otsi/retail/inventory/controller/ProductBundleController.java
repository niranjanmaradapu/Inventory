package com.otsi.retail.inventory.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.service.ProductBundleService;
import com.otsi.retail.inventory.vo.ProductBundleVo;

@RequestMapping("productBundle")
@RestController
public class ProductBundleController {

	private Logger log = LogManager.getLogger(ProductBundleController.class);

	@Autowired
	private ProductBundleService productBundleService;

	@PostMapping("/add")
	public GateWayResponse<?> addProductBundle(@RequestBody ProductBundleVo productBundleVo) {
		log.info("Recieved request to addProductBundle:" + productBundleVo);
		ProductBundleVo bundleSave = productBundleService.addProductBundle(productBundleVo);
		return new GateWayResponse<>("product bundle saved successfully", "");

	}


	@GetMapping("/")
	public GateWayResponse<?> getProductBundle(@RequestParam Long id) {
		log.info("Recieved request to getProductBundle:" + id);
		Optional<ProductBundle> productBundle = productBundleService.getProductBundle(id);
		return new GateWayResponse<>("fetching Product bundle details successfully with id", productBundle);
	}

	@PostMapping("/all")
	public GateWayResponse<?> getAllProductBundles(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
			@RequestParam(required = false) Long id, @RequestParam(required = false) Long storeId) {
		log.info("Recieved request to getAllProductBundles");
		List<ProductBundleVo> productBundles = productBundleService.getAllProductBundles(fromDate, toDate, id,
				storeId);
		return new GateWayResponse<>("fetching all product bundle details sucessfully", productBundles);

	}

	@PutMapping("/update")
	public GateWayResponse<?> updateProductBundle(@RequestBody ProductBundleVo productBundleVo) throws Exception {
		log.info("Recieved request to updateProductBundle:" + productBundleVo);
		String updateBundle = productBundleService.updateProductBundle(productBundleVo);
		return new GateWayResponse<>("updated product bundle data successfully", "");

	}

	@DeleteMapping("/delete")
	public GateWayResponse<?> deleteProductBundle(@RequestParam("id") Long id) throws Exception {
		log.info("Recieved request to deleteProductBundle:" + id);
		ProductBundleVo deleteBundle = productBundleService.deleteProductBundle(id);
		return new GateWayResponse<>("product bundle deleted successfully", "");

	}

}
