package com.otsi.retail.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.otsi.retail.inventory.model.UomEntity;
import com.otsi.retail.inventory.service.UomService;
import com.otsi.retail.inventory.util.Constants;
import com.otsi.retail.inventory.vo.UomVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Saikiran Kola
 *
 */

@Api(value = "UomController", description = "REST APIs related to Uom Entity!!!!")
@RestController
@RequestMapping("/uom")
public class UomController {

	private Logger log = LogManager.getLogger(UomController.class);

	@Autowired
	private UomService uomService;

	/**
	 * 
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "", notes = "adding unit of measures", response = UomVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomVO.class, responseContainer = "Object") })
	@PostMapping
	public ResponseEntity<?> saveUom(@RequestBody UomVO vo) {
		log.info("Recieved request to saveUom:" + vo);
		UomVO uomVO = uomService.saveUom(vo);
		return ResponseEntity.ok(uomVO);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "", notes = "fetching uom using id ", response = UomEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomEntity.class, responseContainer = "Object") })
	@GetMapping
	public ResponseEntity<?> getUom(@RequestParam Long id) {
		log.info("Recieved request to getUom:" + id);
		Optional<UomEntity> uom = uomService.getUom(id);
		return ResponseEntity.ok(uom);
	}

	/**
	 * 
	 * @return
	 */
	@ApiOperation(value = "/list", notes = "fetching all uoms ", response = UomVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomVO.class, responseContainer = "String") })
	@GetMapping("/list")
	public ResponseEntity<?> getAllUom() {
		log.info("Recieved request to getAllUom");
		List<UomVO> uomList = uomService.getAllUom();
		return ResponseEntity.ok(uomList);
	}

	/**
	 * 
	 * @param uomVo
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "", notes = "updating uom ", response = UomVO.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomVO.class, responseContainer = "String") })
	@PutMapping
	public ResponseEntity<?> updateUom(@RequestBody UomVO uomVo) throws Exception {
		log.info("Recieved request to updateUom:" + uomVo);
		UomVO uom = uomService.updateUom(uomVo);
		return ResponseEntity.ok(uom);

	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "", notes = "deleting uom ", response = UomEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 200, message = "Successful retrieval", response = UomEntity.class, responseContainer = "String") })
	@DeleteMapping
	public ResponseEntity<?> deleteUom(@RequestParam("id") Long id) throws Exception {
		log.info("Recieved request to deleteUom:" + id);
		uomService.deleteUom(id);
		return ResponseEntity.ok(CommonUtilities.buildSuccessResponse(Constants.SUCCESS, Constants.RESULT));

	}

}
