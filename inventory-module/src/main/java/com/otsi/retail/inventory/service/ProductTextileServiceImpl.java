package com.otsi.retail.inventory.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otsi.retail.inventory.commons.NatureOfTransaction;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.config.Config;
import com.otsi.retail.inventory.exceptions.InvalidBarcodeException;
import com.otsi.retail.inventory.exceptions.InvalidDataException;
import com.otsi.retail.inventory.exceptions.InvalidPriceException;
import com.otsi.retail.inventory.exceptions.ParentBarcodeFoundException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.mapper.AdjustmentMapper;
import com.otsi.retail.inventory.mapper.ProductTextileMapper;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.model.ProductInventory;
import com.otsi.retail.inventory.model.ProductItem;
import com.otsi.retail.inventory.model.ProductTextile;
import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.model.ProductTransactionRe;
import com.otsi.retail.inventory.repo.AdjustmentRepo;
import com.otsi.retail.inventory.repo.ProductInventoryRepo;
import com.otsi.retail.inventory.repo.ProductItemRepo;
import com.otsi.retail.inventory.repo.ProductTextileRepo;
import com.otsi.retail.inventory.repo.ProductTransactionReRepo;
import com.otsi.retail.inventory.repo.ProductTransactionRepo;
import com.otsi.retail.inventory.utils.DateConverters;
import com.otsi.retail.inventory.utils.ExcelService;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;
import com.otsi.retail.inventory.vo.SearchFilterVo;
import com.otsi.retail.inventory.vo.UserDetailsVo;
import org.apache.commons.collections4.CollectionUtils;

@Component
public class ProductTextileServiceImpl implements ProductTextileService {

	private Logger log = LogManager.getLogger(ProductTextileServiceImpl.class);

	@Autowired
	private ProductTextileMapper productTextileMapper;

	@Autowired
	private ProductTextileRepo productTextileRepo;

	@Autowired
	private ProductTransactionRepo productTransactionRepo;

	@Autowired
	private AdjustmentRepo adjustmentRepo;

	@Autowired
	private AdjustmentMapper adjustmentMapper;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private ProductInventoryRepo productInventoryRepo;

	@Autowired
	private ProductTransactionReRepo productTransactionReRepo;

	@PersistenceContext
	EntityManager em;

	@Autowired
	private Config config;

	@Autowired
	private ExcelService excelService;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String addBarcodeTextile(ProductTextileVo textileVo) {
		log.debug("debugging saveProductTextile:" + textileVo);
		if (textileVo.getCostPrice() == 0 || textileVo.getItemMrp() == 0) {
			throw new InvalidPriceException("price is greater thean zero");
		}
		ProductTextile prodTextile = productTextileMapper.VoToEntity(textileVo);
		prodTextile.setBarcode("BAR-" + getSaltString().toString());
		ProductTextile textileSave = productTextileRepo.save(prodTextile);
		ProductTransaction prodTrans = new ProductTransaction();
		prodTrans.setBarcodeId(textileSave.getBarcode());
		prodTrans.setStoreId(textileSave.getStoreId());
		prodTrans.setEffectingTableId(textileSave.getProductTextileId());
		prodTrans.setQuantity(textileVo.getQty());
		prodTrans.setNatureOfTransaction(NatureOfTransaction.PURCHASE.getName());
		prodTrans.setMasterFlag(true);
		prodTrans.setComment("newly inserted table");
		prodTrans.setEffectingTable("product textile table");
		ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);
		log.warn("we are checking if textile is saved...");
		log.info("after saving textile details");
		return "barcode textile saved successfully:" + textileSave.getBarcode();
	}

	protected String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 6) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

	@Override
	public String updateBarcodeTextile(ProductTextileVo textileVo) {
		log.debug(" debugging updateBarcode:" + textileVo);
		Optional<ProductTextile> dto = productTextileRepo.findById(textileVo.getProductTextileId());
		if (!dto.isPresent()) {
			log.error("Record Not Found");
			throw new RecordNotFoundException("product textile record not found");
		}
		ProductTextile prodTextileUpdate = dto.get();
		prodTextileUpdate.setStatus(ProductStatus.DISABLE);
		productTextileRepo.save(prodTextileUpdate);
		ProductTextile productTextile = new ProductTextile();
		productTextile.setBarcode(
				"REBAR/" + LocalDate.now().getYear() + LocalDate.now().getDayOfMonth() + "/" + getSaltString());

		productTextile.setEmpId(textileVo.getEmpId());
		productTextile.setParentBarcode(dto.get().getBarcode());
		productTextile.setStatus(ProductStatus.ENABLE);
		productTextile.setName(textileVo.getName());
		productTextile.setDivision(textileVo.getDivision());
		productTextile.setSection(textileVo.getSection());
		productTextile.setSubSection(textileVo.getSubSection());
		productTextile.setOriginalBarcodeCreatedAt(LocalDate.now());
		productTextile.setCategory(textileVo.getCategory());
		productTextile.setBatchNo(textileVo.getBatchNo());
		productTextile.setCostPrice(textileVo.getCostPrice());
		productTextile.setItemMrp(textileVo.getItemMrp());
		productTextile.setHsnCode(textileVo.getHsnCode());
		productTextile.setUom(textileVo.getUom());
		productTextile.setColour(textileVo.getColour());
		productTextile.setStoreId(textileVo.getStoreId());
		productTextile.setDomainId(textileVo.getDomainId());
		ProductTextile textileSave = productTextileRepo.save(productTextile);
		List<ProductTransaction> transact = new ArrayList<>();
		transact = productTransactionRepo.findAllByBarcodeId(dto.get().getBarcode());
		transact.stream().forEach(t -> {
			if (t.getEffectingTable().equals("product textile table")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(dto.get().getBarcode(),
						"product textile table", true);
				t.setMasterFlag(false);
				productTransactionRepo.save(t);
			} else if (t.getEffectingTable().equals("Adjustments")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(dto.get().getBarcode(),
						"Adjustments", true);
				t.setMasterFlag(false);
				productTransactionRepo.save(t);
			}
		});
		Adjustments ad = new Adjustments();

		ad.setCreatedBy(textileVo.getEmpId());
		ad.setCurrentBarcodeId(textileSave.getBarcode());
		ad.setToBeBarcodeId(prodTextileUpdate.getBarcode());
		ad.setComments("rebar");
		Adjustments audSave = adjustmentRepo.save(ad);
		ProductTransaction prodTrans = new ProductTransaction();
		prodTrans.setBarcodeId(textileSave.getBarcode());
		prodTrans.setStoreId(textileSave.getStoreId());
		prodTrans.setEffectingTableId(audSave.getAdjustmentId());
		prodTrans.setQuantity(textileVo.getQty());
		prodTrans.setNatureOfTransaction(NatureOfTransaction.REBARPARENT.getName());
		prodTrans.setMasterFlag(true);
		prodTrans.setComment("Adjustments");
		prodTrans.setEffectingTable("Adjustments");
		ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);
		log.info("Rebarcoding textile updated successfully:" + textileSave.getBarcode());
		return "Rebarcoding textile updated successfully:" + textileSave.getBarcode();
	}

	@Override
	public String deleteBarcodeTextile(String barcode) {
		log.debug(" debugging deleteBarcodeTextile:" + barcode);
		ProductTextile prodOpt = productTextileRepo.findByBarcode(barcode);
		if (prodOpt == null) {
			log.error("product textile details not found with id");
			throw new RecordNotFoundException("product textile details not found with id: " + barcode);
		}

		saveAndUpdateAdjustments(prodOpt.getBarcode());
		saveAndUpdateProductTransaction(prodOpt.getBarcode());
		List<ProductTransaction> transact = new ArrayList<>();
		transact = productTransactionRepo.findAllByBarcodeId(barcode);
		transact.stream().forEach(t -> {
			if (t.getComment().equals("newly inserted table")) {
				t = productTransactionRepo.findByBarcodeIdAndCommentAndMasterFlag(barcode, "newly inserted table",
						true);
				productTransactionRepo.delete(t);
			} else if (t.getComment().equals("Adjustments")) {
				t = productTransactionRepo.findByBarcodeIdAndCommentAndMasterFlag(barcode, "Adjustments", true);
				productTransactionRepo.delete(t);
			}
		});
		prodOpt.setStatus(ProductStatus.DISABLE);
		productTextileRepo.save(prodOpt);
		// productTextileRepo.delete(prodOpt);
		log.warn("we are checking if barcode is deleted based on id...");
		log.info("deleted barcode textile succesfully:" + barcode);
		return "deleted barcode textile successfully with id:" + barcode;

	}

	private List<ProductTransaction> saveAndUpdateProductTransaction(String barcode) {
		List<ProductTransaction> transact = new ArrayList<>();
		transact = productTransactionRepo.findAllByBarcodeId(barcode);
		transact.stream().forEach(t -> {
			if (t.getEffectingTable().equals("product textile table")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(barcode,
						"product textile table", true);
				ProductTransaction prodTrans = new ProductTransaction();
				prodTrans.setBarcodeId(t.getBarcodeId());
				prodTrans.setStoreId(t.getStoreId());
				prodTrans.setEffectingTableId(t.getEffectingTableId());
				prodTrans.setQuantity(t.getQuantity());
				prodTrans.setNatureOfTransaction(t.getNatureOfTransaction());
				prodTrans.setMasterFlag(false);
				prodTrans.setComment("deleted");
				prodTrans.setEffectingTable(t.getEffectingTable());
				ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);

			} else if (t.getEffectingTable().equals("Adjustments")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(barcode, "Adjustments", true);
				ProductTransaction prodTrans = new ProductTransaction();
				prodTrans.setBarcodeId(t.getBarcodeId());
				prodTrans.setStoreId(t.getStoreId());
				prodTrans.setEffectingTableId(t.getEffectingTableId());
				prodTrans.setQuantity(t.getQuantity());
				prodTrans.setNatureOfTransaction(t.getNatureOfTransaction());
				prodTrans.setMasterFlag(false);
				prodTrans.setComment("deleted");
				prodTrans.setEffectingTable(t.getEffectingTable());
				ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);
			}
		});

		return transact;
	}

	private Adjustments saveAndUpdateAdjustments(String barcode) {
		Adjustments adjustUpdate = adjustmentRepo.findByCurrentBarcodeId(barcode);
		adjustUpdate.setComments("deleted");
		return adjustmentRepo.save(adjustUpdate);
	}

	public List<UserDetailsVo> getUsersForGivenId(List<Long> userIds) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		URI uri = UriComponentsBuilder.fromUri(new URI(config.getUserDetails())).build().encode().toUri();

		HttpEntity<List<Long>> request = new HttpEntity<List<Long>>(userIds, headers);

		ResponseEntity<?> usersResponse = restTemplate.exchange(uri, HttpMethod.POST, request, GateWayResponse.class);

		System.out.println("Received Request to getUserDetails:" + usersResponse);
		ObjectMapper mapper = new ObjectMapper();

		GateWayResponse<?> gatewayResponse = mapper.convertValue(usersResponse.getBody(), GateWayResponse.class);

		List<UserDetailsVo> bvo = mapper.convertValue(gatewayResponse.getResult(),
				new TypeReference<List<UserDetailsVo>>() {
				});
		return bvo;

	}

	@Override
	public ProductTextileVo getBarcodeTextile(String barcode, Long storeId) {

		if (storeId == null) {
			log.error("textile record is not found with storeId:" + storeId);
			throw new RecordNotFoundException("textile record is not found with storeId:" + storeId);
		}
		if (barcode.startsWith("BAR") || barcode.startsWith("REBAR")) {
			List<ProductTextile> prodTextileStore = productTextileRepo.findByStoreId(storeId);
			ProductTextile textileParent = productTextileRepo.findByParentBarcode(barcode);
			if (textileParent != null) {
				log.error("parent barcode record is no more...please try current barcode.");
				throw new ParentBarcodeFoundException(
						"parent barcode record is no more...please enter current barcode.");
			}
			if (prodTextileStore != null) {
				ProductTextile textile = productTextileRepo.findByBarcode(barcode);
				if (textile == null) {
					log.error("barcode record was not found");
					throw new RecordNotFoundException("barcode record was not found:" + barcode);
				}
				ProductTextileVo vo = productTextileMapper.EntityToVo(textile);
				List<ProductTextile> empIds = productTextileRepo.findAllByEmpId(vo.getEmpId());
				List<Long> userIds = empIds.stream().map(x -> x.getEmpId()).distinct().collect(Collectors.toList());
				List<UserDetailsVo> userDetailsVo = new ArrayList<>();
				try {
					userDetailsVo = getUsersForGivenId(userIds);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				// Map<String, Integer> uvs = userDetailsVo.map.forEach((key, value) ->
				// productTextileVO.setEmpName(x.getUserName()));
				if (userDetailsVo != null) {
					userDetailsVo.stream().forEach(x -> vo.setEmpName(x.getUserName()));
				}
				List<ProductTransaction> transact = new ArrayList<>();
				transact = productTransactionRepo.findAllByBarcodeId(vo.getBarcode());
				transact.stream().forEach(t -> {
					if (t.getEffectingTable().equals("product textile table")) {
						t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(vo.getBarcode(),
								"product textile table", true);
						vo.setQty(t.getQuantity());

						vo.setValue(t.getQuantity() * vo.getItemMrp());
					} else if (t.getEffectingTable().equals("Adjustments")) {
						t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(vo.getBarcode(),
								"Adjustments", true);
						vo.setQty(t.getQuantity());

						vo.setValue(t.getQuantity() * vo.getItemMrp());
					}
				});
				return vo;
			} else {
				throw new RecordNotFoundException("No record found with storeId:" + prodTextileStore);
			}
		} else {
			throw new InvalidBarcodeException("barcode was invalid" + barcode);
		}
	}

	@Override
	public Page<ProductTextileVo> getAllBarcodes(SearchFilterVo vo, Pageable pageable) {
		log.debug("debugging getAllBarcodes()");
		Page<ProductTextile> barcodeDetails = null;
		ProductStatus status = ProductStatus.ENABLE;

		/*
		 * using dates and storeId
		 */
		if (vo.getFromDate() != null && (vo.getToDate() == null) && (vo.getBarcode() == null || vo.getBarcode() == "")
				&& vo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
			LocalDateTime fromTime1 = DateConverters.convertToLocalDateTimeMax(vo.getFromDate());
			barcodeDetails = productTextileRepo.findByCreatedDateBetweenAndStatusAndStoreId(fromTime, fromTime1, status,
					vo.getStoreId(), pageable);
		}

		else if (vo.getFromDate() != null && vo.getToDate() != null
				&& (vo.getBarcode() == null || vo.getBarcode() == "") && vo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
			barcodeDetails = productTextileRepo.findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, status, vo.getStoreId(), pageable);

		}

		/*
		 * using dates with barcode and storeId
		 */

		else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getBarcode() != null
				&& vo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
			barcodeDetails = productTextileRepo.findByCreatedDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, vo.getBarcode(), vo.getStoreId(), pageable);
		}
		/*
		 * using barcode and storeId
		 */
		else if (vo.getFromDate() == null && vo.getToDate() == null && (!vo.getBarcode().isEmpty())
				&& vo.getStoreId() != null) {
			barcodeDetails = productTextileRepo.findByBarcodeAndStoreId(vo.getBarcode(), vo.getStoreId(), pageable);
		} /*
			 * using storeId
			 */
		else if (vo.getStoreId() != null) {
			barcodeDetails = productTextileRepo.findByStoreIdAndStatus(vo.getStoreId(), status, pageable);
		}

		/*
		 * values with empty string
		 */
		else if ((vo.getFromDate() == null) && (vo.getToDate() == null) && (vo.getBarcode() == "")
				&& vo.getStoreId() == null) {
			barcodeDetails = productTextileRepo.findByStatus(status, pageable);
		}

		if (barcodeDetails == null || !barcodeDetails.hasContent()) {
			log.error("No record found with given information");
			throw new RecordNotFoundException("No record found with given information");
		}

		log.warn("we are checking if barcode textile is fetching...");
		log.info("fetching all barcode textile details");
		return barcodeDetails.map(barcode -> mapToVo(barcode));

	}

	private ProductTextileVo mapToVo(ProductTextile barcode) {
		ProductTextileVo productTextileVO = productTextileMapper.EntityToVo(barcode);

		List<ProductTextile> empIds = productTextileRepo.findAllByEmpId(productTextileVO.getEmpId());
		List<Long> userIds = empIds.stream().map(x -> x.getEmpId()).distinct().collect(Collectors.toList());
		List<UserDetailsVo> userDetailsVo = new ArrayList<>();
		try {
			userDetailsVo = getUsersForGivenId(userIds);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} // Map<String, Integer> uvs = //
		/*
		 * userDetailsVo.map.forEach((key, value) -> // //
		 * productTextileVO.setEmpName(x.getUserName()));
		 * 
		 * Map<String, Integer> u = new HashMap<>(); userDetailsVo.stream().forEach(x ->
		 * { u.add(x.getUserName(), x.getUserId()); });
		 */
		if (userDetailsVo != null) {
			userDetailsVo.stream().forEach(x -> {
				productTextileVO.setEmpName(x.getUserName());
			});
		}
		List<ProductTransaction> transact = new ArrayList<>();
		transact = productTransactionRepo.findAllByBarcodeId(barcode.getBarcode());
		transact.stream().forEach(t -> {
			if (t.getEffectingTable().equals("product textile table")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(barcode.getBarcode(),
						"product textile table", true);
				productTextileVO.setQty(t.getQuantity());

				productTextileVO.setValue(t.getQuantity() * barcode.getItemMrp());
			} else if (t.getEffectingTable().equals("Adjustments")) {
				t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(productTextileVO.getBarcode(),
						"Adjustments", true);
				productTextileVO.setQty(t.getQuantity());

				productTextileVO.setValue(t.getQuantity() * productTextileVO.getItemMrp());
			}

		});
		return productTextileVO;

	}

	@Override
	public void inventoryUpdate(List<InventoryUpdateVo> request) {
		request.stream().forEach(x -> {
			// for textile update
			if (x.getDomainId() == 1) {
				ProductTextile barcodeDetails = productTextileRepo.findByBarcode(x.getBarCode());
				if (barcodeDetails == null) {
					log.error("record not found with barcode:" + x.getBarCode());
					throw new RecordNotFoundException("record not found with barcode:" + x.getBarCode());
				}
				List<ProductTransaction> transact = new ArrayList<>();
				transact = productTransactionRepo.findAllByBarcodeId(barcodeDetails.getBarcode());
				transact.stream().forEach(t -> {
					if (t.getEffectingTable().equals("product textile table")) {
						t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(
								barcodeDetails.getBarcode(), "product textile table", true);
						t.setQuantity(Math.abs(x.getQuantity() - t.getQuantity()));
						productTransactionRepo.save(t);
					} else if (t.getEffectingTable().equals("Adjustments")) {
						t = productTransactionRepo.findByBarcodeIdAndEffectingTableAndMasterFlag(
								barcodeDetails.getBarcode(), "Adjustments", true);
						t.setQuantity(Math.abs(x.getQuantity() - t.getQuantity()));
						productTransactionRepo.save(t);
					}
				});
				ProductTransaction prodTrans = new ProductTransaction();
				prodTrans.setBarcodeId(barcodeDetails.getBarcode());
				prodTrans.setEffectingTableId(x.getLineItemId());
				prodTrans.setQuantity(x.getQuantity());
				prodTrans.setStoreId(x.getStoreId());
				prodTrans.setNatureOfTransaction(NatureOfTransaction.SALE.getName());
				prodTrans.setMasterFlag(true);
				prodTrans.setComment("sale");
				prodTrans.setEffectingTable("order table");
				ProductTransaction textileUpdate = productTransactionRepo.save(prodTrans);
				log.info("updated textile successfully from newsale...");

			} else {
				// for retail update
				ProductItem barOpt = productItemRepo.findByBarcodeId(x.getBarCode());
				if (barOpt == null) {
					log.error("record not found with barcode:" + x.getBarCode());
					throw new RecordNotFoundException("record not found with barcode:" + x.getBarCode());
				}
				Optional<ProductItem> prodOpt = productItemRepo.findByProductItemId(barOpt.getProductItemId());
				ProductInventory item = prodOpt.get().getProductInventory();
				if (item == null) {
					throw new RecordNotFoundException("product inventory is not found");
				}
				ProductItem item1 = prodOpt.get();
				Optional<ProductInventory> prodOp = productInventoryRepo.findByProductItem(item1);
				ProductInventory prodInvUpdate = prodOp.get();
				prodInvUpdate.setLastModified(LocalDate.now());
				prodInvUpdate.setProductItem(item1);
				prodInvUpdate.setStockvalue(Math.abs(barOpt.getProductInventory().getStockvalue() - x.getQuantity()));
				productInventoryRepo.save(prodInvUpdate);

				ProductTransactionRe prodTransRe = new ProductTransactionRe();
				prodTransRe.setBarcodeId(barOpt.getBarcodeId());
				prodTransRe.setEffectingTableId(x.getLineItemId());
				prodTransRe.setQuantity(x.getQuantity());
				prodTransRe.setStoreId(x.getStoreId());
				prodTransRe.setNatureOfTransaction(NatureOfTransaction.SALE.getName());
				prodTransRe.setCreationDate(LocalDate.now());
				prodTransRe.setLastModified(LocalDate.now());
				prodTransRe.setMasterFlag(true);
				prodTransRe.setComment("sale");
				prodTransRe.setEffectingTable("order table");
				ProductTransactionRe retailUpdate = productTransactionReRepo.save(prodTransRe);
				log.info("updated retail successfully from newsale....");
			}
		});

		log.info("updated inventory from newsale successfully..");
	}

	@Override
	public Page<AdjustmentsVo> getAllAdjustments(SearchFilterVo searchFilterVo, Pageable pageable) {
		log.info("Received request to getAllAdjustments:" + searchFilterVo);
		Page<Adjustments> adjustmentDetails = null;
		// Page<ProductTransaction> transactStore =
		// productTransactionRepo.findAllByStoreId(searchFilterVo.getStoreId(),
		// pageable);

		Page<ProductTextile> productTextile = productTextileRepo.findByStoreId(searchFilterVo.getStoreId(), pageable);

		if (productTextile != null) {
			List<String> barcodes = productTextile.stream().map(barcode -> barcode.getBarcode())
					.collect(Collectors.toList());
			if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() == null
					&& StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId())
					&& searchFilterVo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getFromDate());
				adjustmentDetails = adjustmentRepo.findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdIn(fromTime,
						toTime, "rebar", barcodes, pageable);
			}
			/*
			 * using dates with storeId
			 */
			else if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() != null
					&& (searchFilterVo.getCurrentBarcodeId() == "") && searchFilterVo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getToDate());
				adjustmentDetails = adjustmentRepo
						.findByCreatedDateBetweenAndCommentsAndCurrentBarcodeIdInOrderByLastModifiedDateAsc(fromTime,
								toTime, "rebar", barcodes, pageable);

			}

			/*
			 * using dates and currentBarcodeId and storeId
			 */
			else if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() != null
					&& searchFilterVo.getCurrentBarcodeId() != null && searchFilterVo.getStoreId() != null) {
				Adjustments adjustOpt = adjustmentRepo.findByCurrentBarcodeId(searchFilterVo.getCurrentBarcodeId());
				if (adjustOpt != null) {
					LocalDateTime fromTime = DateConverters
							.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
					LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getToDate());
					adjustmentDetails = adjustmentRepo
							.findByCreatedDateBetweenAndCurrentBarcodeIdAndCommentsOrderByLastModifiedDateAsc(fromTime,
									toTime, searchFilterVo.getCurrentBarcodeId(), "rebar", pageable);
				} else {
					log.error("No record found with given currentbarcodeId:" + searchFilterVo.getCurrentBarcodeId());
					throw new RecordNotFoundException(
							"No record found with given currentbarcodeId:" + searchFilterVo.getCurrentBarcodeId());
				}
			}

			/*
			 * values with empty string
			 */
			else if (searchFilterVo.getFromDate() == null && searchFilterVo.getToDate() == null
					&& searchFilterVo.getCurrentBarcodeId() == "" && searchFilterVo.getStoreId() == null) {
				adjustmentDetails = adjustmentRepo.findByComments("rebar", pageable);
			}
			/*
			 * using storeId
			 */
			else if (searchFilterVo.getFromDate() == null && searchFilterVo.getToDate() == null
					&& StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId())
					&& searchFilterVo.getStoreId() != null) {
				adjustmentDetails = adjustmentRepo.findByCommentsAndCurrentBarcodeIdIn("rebar", barcodes, pageable);
			}

			/*
			 * using currentBarcodeId and storeId
			 */
			else if (searchFilterVo.getFromDate() == null && searchFilterVo.getToDate() == null
					&& searchFilterVo.getCurrentBarcodeId() != null && searchFilterVo.getStoreId() != null) {

				adjustmentDetails = adjustmentRepo
						.findByCurrentBarcodeIdAndComments(searchFilterVo.getCurrentBarcodeId(), "rebar", pageable);
			}

			if (adjustmentDetails.isEmpty()) {
				log.error("No record found with given information");
				throw new RecordNotFoundException("No record found with given information");
			}
		}

		if (adjustmentDetails.hasContent()) {
			return adjustmentDetails.map(adjustment -> adjustmentMapToVo(adjustment));
		}
		return Page.empty();
	}

	private AdjustmentsVo adjustmentMapToVo(Adjustments adjustment) {

		AdjustmentsVo adjustmentsVo = adjustmentMapper.EntityToVo(adjustment);
		ProductTextile productTextile = productTextileRepo.findByBarcode(adjustmentsVo.getCurrentBarcodeId());
		adjustmentsVo.setStoreId(productTextile.getStoreId());
		log.info("fetching all adjustment details..:" + adjustmentsVo);
		return adjustmentsVo;

	}

	@Override
	public String saveProductTextileList(List<ProductTextileVo> productTextileVos, Long storeId) {
		productTextileVos.stream().forEach(v -> {
			v.setStoreId(storeId);
			addBarcodeTextile(v);
		});
		log.info("after saving all product textiles...");
		return "saving list of product textile details...";
	}

	@Override
	public List<String> getValuesFromProductTextileColumns(String enumName) {

		try {
			String query = null;
			String underscore = "_";

			if (enumName.equalsIgnoreCase("SECTION") || enumName.equalsIgnoreCase("SUBSECTION")
					|| enumName.equalsIgnoreCase("DIVISION")) {
				if (enumName.equalsIgnoreCase("SUBSECTION")) {
					enumName = enumName.isEmpty() ? enumName
							: enumName.substring(0, 3).toUpperCase() + underscore + enumName.substring(3).toUpperCase();
				} else if (enumName.equalsIgnoreCase("SECTION") || enumName.equalsIgnoreCase("DIVISION")) {
					enumName = enumName.isEmpty() ? enumName
							: Character.toUpperCase(enumName.charAt(0)) + enumName.substring(1).toUpperCase();
				}

				query = "select c.name from  catalog_categories c where c.description= '" + enumName + "'";
			}

			else if (enumName.equalsIgnoreCase("batchno") || enumName.equalsIgnoreCase("costprice")
					|| enumName.equalsIgnoreCase("mrp")) {
				if (enumName.equalsIgnoreCase("batchno")) {
					enumName = enumName.isEmpty() ? enumName
							: enumName.substring(0, 5).toLowerCase() + underscore + enumName.substring(5).toLowerCase();
				} else if (enumName.equalsIgnoreCase("costprice")) {
					enumName = enumName.isEmpty() ? enumName
							: enumName.substring(0, 4).toLowerCase() + underscore + enumName.substring(4).toLowerCase();
				} else if (enumName.equalsIgnoreCase("mrp")) {
					enumName = "itemmrp";
					enumName = enumName.isEmpty() ? enumName
							: enumName.substring(0, 4).toLowerCase() + underscore + enumName.substring(4).toLowerCase();
				}
				query = "select p." + enumName + " from  product_textile p group by  p." + enumName;
			} else if (enumName.equalsIgnoreCase("Dcode") || enumName.equalsIgnoreCase("StyleCode")
					|| enumName.equalsIgnoreCase("SubSectionId") || enumName.equalsIgnoreCase("DiscountType")) {
				return Collections.emptyList();
			}
			return em.createNativeQuery(query).getResultList();

		} catch (Exception ex) {
			log.error("data is not correct");
			throw new InvalidDataException("data is not correct");
		} finally {

			if (em.isOpen()) {
				em.close();
			}

		}
	}

	@Override
	public List<String> getAllColumns(Long domainId) {
		List<String> columns = new ArrayList<>();
		if (domainId == 1) {
			columns = productTextileRepo.findAllColumnNames();
		} else {
			columns = productItemRepo.findAllColumnNames();
		}
		log.info("after fetching all columns:" + columns);
		return columns;
	}

	@Override
	public Page<ProductTextileVo> getBarcodeTextileReports(SearchFilterVo vo, Pageable pageable) {
		log.debug("debugging getBarcodeTextileReports():" + vo);
		Page<ProductTextile> barcodeDetails = null;
		ProductStatus status = ProductStatus.ENABLE;
		List<ProductTextile> barStore = productTextileRepo.findByStoreId(vo.getStoreId());
		if (barStore != null) {

			/*
			 * using fromDate and storeId
			 */
			if (vo.getFromDate() != null && (vo.getToDate() == null)
					&& (vo.getBarcode() == null || vo.getBarcode() == "") && vo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime fromTime1 = DateConverters.convertToLocalDateTimeMax(vo.getFromDate());
				barcodeDetails = productTextileRepo.findByCreatedDateBetweenAndStatusAndStoreId(fromTime, fromTime1,
						status, vo.getStoreId(), pageable);
			}

			/*
			 * using dates and storeId
			 */

			else if (vo.getFromDate() != null && vo.getToDate() != null
					&& (vo.getBarcode() == null || vo.getBarcode() == "") && vo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
				barcodeDetails = productTextileRepo
						.findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(fromTime, toTime, status,
								vo.getStoreId(), pageable);

			}

			/*
			 * using dates with barcode and storeId
			 */

			else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getBarcode() != null
					&& vo.getStoreId() != null) {
				ProductTextile barOpt = productTextileRepo.findByBarcode(vo.getBarcode());
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
				if (barOpt != null) {
					barcodeDetails = productTextileRepo
							.findByCreatedDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(fromTime, toTime,
									vo.getBarcode(), vo.getStoreId(), pageable);
				} else {
					log.error("No record found with given barcode");
					throw new RecordNotFoundException("No record found with given barcode");
				}
			}

			/*
			 * using itemMrp< and itemMrp>
			 */
			else if (vo.getItemMrpLessThan() != 0 && vo.getItemMrpGreaterThan() != 0 && vo.getStoreId() != null) {

				barcodeDetails = productTextileRepo.findByItemMrpBetweenAndStoreIdAndStatus(vo.getItemMrpLessThan(),
						vo.getItemMrpGreaterThan(), vo.getStoreId(), status, pageable);

			}
			/*
			 * using empId
			 */
			else if (vo.getEmpId() != null) {
				barcodeDetails = productTextileRepo.findByEmpIdAndStatusAndStoreId(vo.getEmpId(), status,
						vo.getStoreId(), pageable);
			}
			/*
			 * using barcode and storeId
			 */
			else if (vo.getFromDate() == null && vo.getToDate() == null && (vo.getBarcode() != null)
					&& vo.getStoreId() != null) {
				barcodeDetails = productTextileRepo.findByBarcodeAndStoreId(vo.getBarcode(), vo.getStoreId(), pageable);
			}

			/*
			 * using storeId
			 */
			else if (vo.getStoreId() != null) {
				barcodeDetails = productTextileRepo.findByStoreIdAndStatus(vo.getStoreId(), status, pageable);

			}

			/*
			 * using barcode
			 */
			else if ((vo.getFromDate() == null) && (vo.getToDate() == null) && (vo.getBarcode() != null)
					&& vo.getStoreId() == null && vo.getItemMrpGreaterThan() == 0 && vo.getItemMrpLessThan() == 0
					&& vo.getEmpId() == null) {
				ProductTextile textile = productTextileRepo.findByBarcode(vo.getBarcode());
				barcodeDetails.and(textile);
			}
			if (barcodeDetails.isEmpty()) {
				log.error("No record found with given information");
				throw new RecordNotFoundException("No record found with given information");
			}
		} else {
			throw new RecordNotFoundException("textile store record is not found");
		}

		return barcodeDetails.map(barcodeReport -> mapToVo(barcodeReport));
	}

	@Override
	public List<ProductTextileVo> getBarcodes(List<String> barcode) {
		log.debug("deugging getBarcodeDetails" + barcode);
		List<ProductTextileVo> vo = new ArrayList<ProductTextileVo>();
		List<ProductTextile> barcodeDetails = productTextileRepo.findByBarcodeIn(barcode);
		vo = productTextileMapper.EntityToVo(barcodeDetails);
		return vo;
	}

	@Override
	public ProductTextileVo getTextileParentBarcode(String parentBarcode) {
		ProductTextile textileParent = productTextileRepo.findByBarcode(parentBarcode);
		if (textileParent != null) {
			ProductTextileVo vo = productTextileMapper.EntityToVo(textileParent);
			ProductTransaction transact = productTransactionRepo.findByBarcodeIdAndStoreId(textileParent.getBarcode(),
					textileParent.getStoreId());
			if (transact == null) {
				log.error("textile record is not found");
				throw new RecordNotFoundException("textile record is not found");
			}
			vo.setQty(transact.getQuantity());
			vo.setValue(transact.getQuantity() * vo.getItemMrp());
			return vo;
		} else
			throw new RecordNotFoundException("No record found with parentBarcode:" + parentBarcode);
	}

	@Override
	public void addBulkProducts(MultipartFile multipartFile, Long storeId)
			throws InstantiationException, IllegalAccessException, IOException {
		List<ProductTextileVo> products = excelService.readExcel(multipartFile.getInputStream(),
				ProductTextileVo.class);
		if (CollectionUtils.isNotEmpty(products)) {
			products.forEach(product -> {
				product.setStoreId(storeId);
				addBarcodeTextile(product);
			});
		}
	}

}
