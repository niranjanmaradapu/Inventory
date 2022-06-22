package com.otsi.retail.inventory.controller;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.model.DomainAttributes;
import com.otsi.retail.inventory.rabbitmq.MQConfig;
import com.otsi.retail.inventory.service.ProductService;
import com.otsi.retail.inventory.util.CommonUtilities;
import com.otsi.retail.inventory.util.Constants;
import com.otsi.retail.inventory.vo.AdjustmentsVO;
import com.otsi.retail.inventory.vo.DomainAttributesVO;
import com.otsi.retail.inventory.vo.DomainTypePropertiesVO;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.InvoiceDetailsVO;
import com.otsi.retail.inventory.vo.ProductVO;
import com.otsi.retail.inventory.vo.SearchFilterVo;
import com.otsi.retail.inventory.vo.ValuesVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author vasavi
 *
 */
@Api(value = "ProductController", description = "REST APIs related to Product Entity!!!!")
@RestController
@RequestMapping("/inventory-management")
public class ProductController {

	private Logger log = LogManager.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	/**
	 * 
	 * @param productVO
	 * @return
	 */
	@ApiOperation(value = "/product", notes = "add product", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@PostMapping("/product")
	public ResponseEntity<?> addBarcode(@RequestBody ProductVO productVO) {
		log.info("Recieved request to addBarcode:" + productVO);
		ProductVO vo = productService.addBarcode(productVO);
		return ResponseEntity.ok(vo);

	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@ApiOperation(value = "/product", notes = "update product", response = ProductVO.class)
	@PutMapping(value = "/product")
	public ResponseEntity<?> updateBarcode(@RequestBody ProductVO vo) {
		log.info("Received Request to updateBarcode :" + vo.toString());
		ProductVO productVO = productService.updateBarcode(vo);
		return ResponseEntity.ok(productVO);

	}

	/**
	 * 
	 * @param qty
	 * @return
	 */
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@ApiOperation(value = "/product-qty", notes = "update qty of the product", response = ProductVO.class)
	@PutMapping(value = "/product-qty")
	public ResponseEntity<?> updateQuantity(@RequestBody ProductVO productVO) {
		log.info("Received Request to updateQuantity :" + productVO);
		ProductVO productQty = productService.updateQuantity(productVO);
		return ResponseEntity.ok(productQty);

	}

	/**
	 * 
	 * @param barcode
	 * @return
	 */
	@ApiOperation(value = "/product", notes = "delete barcode", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@DeleteMapping("/product")
	public ResponseEntity<?> deleteProduct(@RequestParam("id") Long id) {
		log.info("Received Request to deleteBarcode:" + id);
		productService.deleteProduct(id);
		return ResponseEntity.ok(CommonUtilities.buildSuccessResponse(Constants.SUCCESS, Constants.RESULT));

	}

	/**
	 * 
	 * @param barcode
	 * @param storeId
	 * @return
	 */
	@ApiOperation(value = "/barcode", notes = "fetch barcode using storeId", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@GetMapping("/barcode")
	public ResponseEntity<?> getBarcode(@RequestParam("barcode") String barcode,
			@RequestParam("storeId") Long storeId) {
		// ,@RequestHeader(value = "clientId" , required=false) Long clientId) {
		log.info("Received request to getProduct:" + barcode);
		ProductVO product = productService.getBarcode(barcode, storeId);
		return ResponseEntity.ok(product);
	}

	/**
	 * include Hsn and tax values along with product barcode used in billing
	 * 
	 * @param barcode
	 * @param storeId
	 * @return
	 */
	@ApiOperation(value = "/barcode-details", notes = "fetch barcode using storeId", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@GetMapping("/barcode-details")
	public ResponseEntity<?> getBarcodeDetails(@RequestParam("barcode") String barcode,
			@RequestParam("storeId") Long storeId, @RequestHeader(value = "clientId") Long clientId) {
		log.info("Received request to getProduct:" + barcode);
		ProductVO productTaxValues = productService.barcodeDetails(barcode, clientId, storeId);
		return ResponseEntity.ok(productTaxValues);
	}

	/**
	 * 
	 * @param pageable
	 * @param searchFilterVo
	 * @return
	 */
	@ApiOperation(value = "/barcodes/filter", notes = "fetch list of barcodes", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@PostMapping("/barcodes/filter")
	public ResponseEntity<?> getAllBarcodes(Pageable pageable, @RequestBody SearchFilterVo searchFilterVo) {
		Page<ProductVO> products = productService.getAllBarcodes(searchFilterVo, pageable);
		return ResponseEntity.ok(products);
	}

	/**
	 * 
	 * @param request
	 */
	@RabbitListener(queues = MQConfig.inventory_queue)
	@PostMapping("/inventory-update")
	public void inventoryUpdate(@RequestBody List<InventoryUpdateVo> request) {
		String type = Constants.NEW_SALE;
		String referringTable = Constants.ORDER_TABLE;
		productService.inventoryUpdate(request, type, referringTable);
	}

	/**
	 * 
	 * @param request
	 */
	@RabbitListener(queues = "returnslip_queue")
	public void returnslipInventoryUpdate(@RequestBody List<InventoryUpdateVo> request) {
		String type = Constants.RETURN_SLIP;
		String referringTable = Constants.CUSTOMER_TABLE;
		productService.inventoryUpdate(request, type, referringTable);
	}

	/**
	 * 
	 * @param pageable
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "/adjustments/filter", notes = "fetch rebarcodes", response = AdjustmentsVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = AdjustmentsVO.class, responseContainer = "List") })
	@PostMapping("/adjustments/filter")
	public ResponseEntity<Page<?>> getAllAdjustments(Pageable pageable, @RequestBody SearchFilterVo vo) {
		log.info("Recieved request to adjustments:" + vo);
		Page<AdjustmentsVO> rebarProducts = productService.getAdjustments(vo, pageable);
		return ResponseEntity.ok(rebarProducts);
	}

	/**
	 * 
	 * @param productVOs
	 * @param storeId
	 * @return
	 */
	@ApiOperation(value = "/products", notes = "add products", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@PostMapping("/products")
	public ResponseEntity<?> saveProducts(@RequestBody List<ProductVO> productVOs, Long storeId) {
		log.info("Received Request to save products:" + productVOs);
		productService.saveProducts(productVOs, storeId);
		return ResponseEntity.ok(CommonUtilities.buildSuccessResponse(Constants.SUCCESS, Constants.RESULT));
	}

	/**
	 * 
	 * @param enumName
	 * @return
	 */
	@ApiOperation(value = "getValuesFromColumns", notes = "fetch values using column names for the catalog category ", response = ValuesVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@GetMapping("/getValuesFromColumns")
	public GateWayResponse<?> getValuesFromColumns(@RequestParam("enumName") String enumName) {
		log.info("Recieved request to getValuesFromColumns:" + enumName);
		List<ValuesVO> valuesVo = productService.getValuesFromColumns(enumName);
		return new GateWayResponse<>("fetching all " + enumName + " category details sucessfully", valuesVo);
	}

	/**
	 * 
	 * @param enumName
	 * @return
	 */
	@ApiOperation(value = "getValuesFromProductTextileColumns", notes = "fetch values using column names for the textile ", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@GetMapping("/getValuesFromProductTextileColumns")
	public GateWayResponse<?> getValuesFromProductTextileColumns(@RequestParam("enumName") String enumName) {
		log.info("Recieved request to getValuesFromProductTextileColumns:" + enumName);
		List<String> enumVo = productService.getValuesFromProductTextileColumns(enumName);
		return new GateWayResponse<>("fetching all " + enumName + " textile details sucessfully", enumVo);
	}

	/**
	 * 
	 * @param domainId
	 * @return
	 */
	@ApiOperation(value = "getAllColumns", notes = "fetch all columns for the textile ", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@GetMapping("/getAllColumns")
	public GateWayResponse<?> getAllColumns() {
		log.info("Received Request to getAllColumns...");
		List<String> columns = productService.getAllColumns();
		return new GateWayResponse<>("fetching all Column details", columns);

	}

	/**
	 * 
	 * @param vo
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "getBarcodeTextileReports", notes = "fetch barcodes for the textile reports", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@PostMapping("/getBarcodeTextileReports")
	public GateWayResponse<?> getBarcodeTextileReports(@RequestBody SearchFilterVo vo, Pageable pageable) {
		log.info("Recieved request to getBarcodeTextileReports:" + vo);
		Page<ProductVO> allBarcodes = productService.getBarcodeTextileReports(vo, pageable);
		return new GateWayResponse<>("fetching all barcode textile details sucessfully", allBarcodes);
	}

	/**
	 * 
	 * @param barcode
	 * @return
	 */
	@ApiOperation(value = "/barcodes", notes = "fetch barcodes", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "List") })
	@PostMapping("/barcodes")
	public ResponseEntity<?> getBarcodes(@RequestBody List<String> barcode) {
		log.info("Received Request to getBarcodes:" + barcode);
		List<ProductVO> products = productService.getBarcodes(barcode);
		return ResponseEntity.ok(products);

	}

	/**
	 * 
	 * @param parentBarcode
	 * @return
	 */
	@ApiOperation(value = "getTextileParentBarcode", notes = "fetch parentBarcode", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@GetMapping("/getTextileParentBarcode")
	public ResponseEntity<?> getParentBarcode(@RequestParam("parentBarcode") String parentBarcode) {
		log.info("Recieved request to getTextileParentBarcode:" + parentBarcode);
		ProductVO product = productService.getProductByParentBarcode(parentBarcode);
		return ResponseEntity.ok(product);
	}

	/**
	 * 
	 * @param file
	 * @param storeId
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	@PostMapping(path = "/add-bulk-products", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addBulkProducts(@RequestParam("file") MultipartFile file,
			@RequestParam("storeId") Long storeId) throws InstantiationException, IllegalAccessException, IOException {
		productService.addBulkProducts(file, storeId);
		return ResponseEntity.ok(CommonUtilities.buildSuccessResponse(Constants.SUCCESS, Constants.RESULT));
	}

	/**
	 * @param domainType
	 */
	@GetMapping(path = "/properties")
	public ResponseEntity<?> getProperties(@RequestParam("domainType") String domainType) {
		DomainTypePropertiesVO properties = productService.getProperties(domainType);
		return ResponseEntity.ok(properties);
	}

	/**
	 * 
	 * @param barcode
	 * @param storeId
	 * @param clientId
	 * @return
	 */
	@ApiOperation(value = "/scan-barcode", notes = "fetch barcode using storeId", response = ProductVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = ProductVO.class, responseContainer = "Object") })
	@GetMapping("/scan-barcode")
	public ResponseEntity<?> scanBarcode(@RequestParam("barcode") String barcode, @RequestParam("storeId") Long storeId,
			@RequestHeader(value = "clientId") Long clientId) {
		log.info("Received request to getProduct:" + barcode);
		InvoiceDetailsVO invoiceDetailsVO = productService.scanAndFetchbarcodeDetails(barcode, clientId, storeId);
		return ResponseEntity.ok(invoiceDetailsVO);
	}

	@GetMapping(path = "/domain-attributes")
	public ResponseEntity<?> getDomainAttributes(@RequestParam DomainType domainType) {
		List<DomainAttributes> properties = productService.findDomainAttributes(domainType);
		return ResponseEntity.ok(properties);
	}

	@PostMapping(path = "/domain-attributes")
	public ResponseEntity<?> saveDomainAttributes(@RequestBody DomainAttributesVO domainAttributes) {
		if (domainAttributes.getId() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"invalid id property in payload " + domainAttributes.getId());
		}
		DomainAttributesVO domainAttributesVO = productService.saveDomainAttributes(domainAttributes);
		return ResponseEntity.ok(domainAttributesVO);
	}

	@PutMapping(path = "/domain-attributes")
	public ResponseEntity<?> updateDomainAttributes(@RequestBody DomainAttributesVO domainAttributes) {
		if (domainAttributes.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"id property is null in payload " + domainAttributes.getId());
		}
		DomainAttributesVO domainAttributesVO = productService.updateDomainAttributes(domainAttributes);
		return ResponseEntity.ok(domainAttributesVO);
	}

}
