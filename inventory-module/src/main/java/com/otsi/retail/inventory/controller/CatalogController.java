/**
 * 
 */
package com.otsi.retail.inventory.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.otsi.retail.inventory.service.CatalogService;
import com.otsi.retail.inventory.util.Constants;
import com.otsi.retail.inventory.vo.CatalogVO;
import com.otsi.retail.inventory.vo.UomVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Sudheer.Swamy
 *
 */
@Api(value = "CatalogController", description = "REST APIs related to CatalogEntity !!!!")
@RestController
@RequestMapping("/catalog")
public class CatalogController {

	@Autowired
	private CatalogService catalogService;

	private final Logger LOGGER = LogManager.getLogger(CatalogController.class);

	/**
	 * 
	 * @param catalog
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "", notes = "saving catlog", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = CatalogVO.class, responseContainer = "Object") })
	@PostMapping
	public ResponseEntity<?> saveCatalog(@RequestBody CatalogVO catalog) throws Exception {
		CatalogVO catalogVO = catalogService.saveCatalogDetails(catalog);
		return ResponseEntity.ok(catalogVO);

	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "/name", notes = "fetching catalog using name", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomVO.class, responseContainer = "Object") })
	@GetMapping("name/{name}")
	public ResponseEntity<?> getCatalogbyName(@PathVariable("name") String name) throws Exception {
		CatalogVO catalogVO = catalogService.getCatalogByName(name);
		return ResponseEntity.ok(catalogVO);
	}

	/**
	 * 
	 * @return
	 */
	@ApiOperation(value = "/divisions", notes = "fetching list of divisions", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = CatalogVO.class, responseContainer = "List") })
	@GetMapping("/divisions")
	public ResponseEntity<?> getListOfMainCatagories() {
		List<CatalogVO> catalogVO = catalogService.getMainCategories();
		return ResponseEntity.ok(catalogVO);

	}

	/**
	 * |
	 * 
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "category", notes = "fetching categories using id", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = CatalogVO.class, responseContainer = "List") })
	@GetMapping("/category")
	public ResponseEntity<?> getCategories(@RequestParam Long id) {
		List<CatalogVO> catalogVO = catalogService.getCategories(id);
		return ResponseEntity.ok(catalogVO);

	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "/category", notes = "delete category using id", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = CatalogVO.class, responseContainer = "String") })
	@DeleteMapping("/category/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) throws Exception {
		catalogService.deleteCategoryById(id);
		return ResponseEntity.ok(CommonUtilities.buildSuccessResponse(Constants.SUCCESS, Constants.RESULT));

	}

	/**
	 *
	 * @return
	 */
	@ApiOperation(value = "categories", notes = "fetching list of categories", response = CatalogVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = CatalogVO.class, responseContainer = "List") })
	@GetMapping("/categories")
	public ResponseEntity<?> getListOfCategories() {
		List<CatalogVO> catalogs = catalogService.getAllCategories();
		return ResponseEntity.ok(catalogs);

	}

}
