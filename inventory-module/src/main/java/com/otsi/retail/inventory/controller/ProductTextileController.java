package com.otsi.retail.inventory.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.rabbitmq.MQConfig;
import com.otsi.retail.inventory.service.ProductTextileService;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;
import com.otsi.retail.inventory.vo.SearchFilterVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * @author vasavi
 *
 */
@Api(value = "ProductTextileController", description = "REST APIs related to ProductTextile Entity!!!!")
@RestController
@RequestMapping("/inventoryTextile")
public class ProductTextileController {

	private Logger log = LogManager.getLogger(ProductTextileController.class);

	@Autowired
	private ProductTextileService productTextileService;
	
	
    @ApiOperation(value = "addBarcode_Textile",notes="add product for the textile ", response = ProductTextileVo.class)
    @ApiResponses(value = {
	        @ApiResponse(code = 500, message = "Server error"),
	        @ApiResponse(code = 200, message = "Successful retrieval",
	            response = ProductTextileVo.class, responseContainer = "Object") })
	@PostMapping("/addBarcode_Textile")
	public GateWayResponse<?> addBarcodeTextile(@RequestBody ProductTextileVo textileVo) {
		log.info("Recieved request to saveTextile:" + textileVo);
		String textileSave = productTextileService.addBarcodeTextile(textileVo);
		return new GateWayResponse<>("barcode textile saved successfully", textileSave);

	}
    
    
    @ApiResponses(value = {
	        @ApiResponse(code = 500, message = "Server error"),
	        @ApiResponse(code = 200, message = "Successful retrieval",
	            response = ProductTextileVo.class, responseContainer = "Object") })
    @ApiOperation(value = "updateBarcode_Textile",notes="update product for the textile ", response = ProductTextileVo.class)
	@PutMapping(value = "/updateBarcode_Textile")
	public GateWayResponse<?> updateBarcodeTextile(@RequestBody ProductTextileVo vo) {
		log.info("Received Request to updateBarcodeTextile :" + vo.toString());
		String updateBarcode = productTextileService.updateBarcodeTextile(vo);
		return new GateWayResponse<>("Barcode textile updated successfully", updateBarcode);
	}
    
    
    @ApiOperation(value = "deleteBarcode_Textile",notes="delete barcode for the textile ", response = ProductTextileVo.class)
    @ApiResponses(value = {
	        @ApiResponse(code = 500, message = "Server error"),
	        @ApiResponse(code = 200, message = "Successful retrieval",
	            response = ProductTextileVo.class, responseContainer = "Object") })
	@DeleteMapping("/deleteBarcode_Textile")
	public GateWayResponse<?> deleteBarcodeTextile(@RequestParam("barcode") String barcode) {
		log.info("Received Request to deleteBarcodeTextile:" + barcode);
		String deleteBarcode = productTextileService.deleteBarcodeTextile(barcode);
		return new GateWayResponse<>("Barcode textile deleted successfully", deleteBarcode);

	}
    
    
    @ApiOperation(value = "getBarcodeTextile",notes="fetch barcode using storeId for the textile ", response = ProductTextileVo.class)
    @ApiResponses(value = {
	        @ApiResponse(code = 500, message = "Server error"),
	        @ApiResponse(code = 200, message = "Successful retrieval",
	            response = ProductTextileVo.class, responseContainer = "Object") })
	@GetMapping("/getBarcodeTextile")
	public GateWayResponse<?> getBarcodeTextile(@RequestParam("barcode") String barcode,
			@RequestParam("storeId") Long storeId) {
		log.info("Recieved request to getProductTextile:" + barcode);
		ProductTextileVo textile = productTextileService.getBarcodeTextile(barcode, storeId);
		return new GateWayResponse<>("fetching barcode textile details successfully with barcode", textile);
	}
    
    
    @ApiOperation(value = "getAllBarcodeTextiles", notes = "fetch list of barcodes for the textile ", response = ProductTextileVo.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductTextileVo.class, responseContainer = "List") })
	@PostMapping("/getAllBarcodeTextiles")
	public GateWayResponse<?> getAllBarcodes(Pageable pageable, @RequestBody SearchFilterVo vo) {
		log.info("Recieved request to getAllBarcodes");

		Page<ProductTextileVo> allBarcodes = productTextileService.getAllBarcodes(vo, pageable);
		return new GateWayResponse<>("fetching all barcode textile details sucessfully", allBarcodes);
	}
    

	@RabbitListener(queues = MQConfig.inventory_queue)
	public void inventoryUpdate(@RequestBody List<InventoryUpdateVo> request) {
		productTextileService.inventoryUpdate(request);
	}
	
	
	 @ApiOperation(value = "getAllAdjustments",notes="fetch rebarcodes for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@PostMapping("/getAllAdjustments")
	public GateWayResponse<?> getAllAdjustments(@RequestBody AdjustmentsVo vo) {
		log.info("Recieved request to getAllAdjustments:" + vo);
		List<AdjustmentsVo> allBarcodes = productTextileService.getAllAdjustments(vo);
		return new GateWayResponse<>("fetching all adjusment details sucessfully", allBarcodes);
	}
	 
	 
	 @ApiOperation(value = "saveProductTextileList",notes="add bulk products for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@PostMapping("/saveProductTextileList")
	public GateWayResponse<?> saveProductTextileList(@RequestBody List<ProductTextileVo> barcodeTextileVos,
			Long storeId) {
		log.info("Received Request to saveProductTextileList:" + barcodeTextileVos);
		String saveVoList = productTextileService.saveProductTextileList(barcodeTextileVos, storeId);
		return new GateWayResponse<>("saving list of product textile", saveVoList);

	}
	 
	 
	 @ApiOperation(value = "getValuesFromProductTextileColumns",notes="fetch values using column names for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@GetMapping("/getValuesFromProductTextileColumns")
	public GateWayResponse<?> getValuesFromProductTextileColumns(@RequestParam("enumName") String enumName) {
		log.info("Recieved request to getValuesFromProductTextileColumns:" + enumName);
		List<String> enumVo = productTextileService.getValuesFromProductTextileColumns(enumName);
		return new GateWayResponse<>("fetching all " + enumName + " textile details sucessfully", enumVo);
	}
	 
	 
	 @ApiOperation(value = "getAllColumns",notes="fetch all columns for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@GetMapping("/getAllColumns")
	public GateWayResponse<?> getAllColumns(@RequestParam Long domainId) {
		log.info("Received Request to getAllColumns...");
		List<String> columns = productTextileService.getAllColumns(domainId);
		return new GateWayResponse<>("fetching all Column details", columns);

	}
	 
	 
	@ApiOperation(value = "getBarcodeTextileReports",notes="fetch barcodes for the textile reports", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@PostMapping("/getBarcodeTextileReports")
	public GateWayResponse<?> getBarcodeTextileReports(@RequestBody SearchFilterVo vo) {
		log.info("Recieved request to getBarcodeTextileReports:" + vo);
		List<ProductTextileVo> allBarcodes = productTextileService.getBarcodeTextileReports(vo);
		return new GateWayResponse<>("fetching all barcode textile details sucessfully", allBarcodes);
	}
	
	
	@ApiOperation(value = "getBarcodes",notes="fetch barcodes for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "List") })
	@PostMapping("/getBarcodes")
	public GateWayResponse<?> getBarcodes(@RequestBody List<String> barcode) {
		log.info("Received Request to getBarcodeDetails:" + barcode);
		List<ProductTextileVo> barcodeDetails = productTextileService.getBarcodes(barcode);
		return new GateWayResponse<>("fetching all barcode details", barcodeDetails);

	}
	
	
	@ApiOperation(value = "getTextileParentBarcode",notes="fetch parentBarcode for the textile ", response = ProductTextileVo.class)
	 @ApiResponses(value = {
		        @ApiResponse(code = 500, message = "Server error"),
		        @ApiResponse(code = 200, message = "Successful retrieval",
		            response = ProductTextileVo.class, responseContainer = "Object") })
	@GetMapping("/getTextileParentBarcode")
	public GateWayResponse<?> getTextileParentBarcode(@RequestParam("parentBarcode") String parentBarcode) {
		log.info("Recieved request to getTextileParentBarcode:" + parentBarcode);
		ProductTextileVo textile = productTextileService.getTextileParentBarcode(parentBarcode);
		return new GateWayResponse<>("fetching parent barcode textile details successfully", textile);
	}
	 
	
}
