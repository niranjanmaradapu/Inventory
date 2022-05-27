package com.otsi.retail.inventory.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otsi.retail.inventory.commons.AdjustmentType;
import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.commons.NatureOfTransaction;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.config.Config;
import com.otsi.retail.inventory.exceptions.InvalidDataException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.mapper.AdjustmentMapper;
import com.otsi.retail.inventory.mapper.ProductTextileMapper;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.model.Product;
import com.otsi.retail.inventory.model.ProductInventory;
import com.otsi.retail.inventory.model.ProductItem;
import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.model.ProductTransactionRe;
import com.otsi.retail.inventory.repo.AdjustmentRepo;
import com.otsi.retail.inventory.repo.ProductInventoryRepo;
import com.otsi.retail.inventory.repo.ProductItemRepo;
import com.otsi.retail.inventory.repo.ProductRepository;
import com.otsi.retail.inventory.repo.ProductTransactionReRepo;
import com.otsi.retail.inventory.repo.ProductTransactionRepo;
import com.otsi.retail.inventory.util.DateConverters;
import com.otsi.retail.inventory.util.ExcelService;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.DomainTypePropertiesVO;
import com.otsi.retail.inventory.vo.FieldNameVO;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductVO;
import com.otsi.retail.inventory.vo.SearchFilterVo;
import com.otsi.retail.inventory.vo.UserDetailsVo;

@Component
public class ProductServiceImpl implements ProductService {

	private Logger log = LogManager.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductTextileMapper productTextileMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductTransactionRepo productTransactionRepository;

	@Autowired
	private AdjustmentRepo adjustmentRepository;

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

	private static final String PRODUCT_TABLE = "PRODUCT";
	private static final String PRODUCT_PURCHASE_COMMENT = "INSERTED";
	private static final String ADJUSTMENTS_TABLE = "ADJUSTMENT";

	@Override
	public ProductVO addBarcode(ProductVO productVO) {
		log.debug("debugging save Product:" + productVO);
		if (productVO.getCostPrice() == 0 || productVO.getItemMrp() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cost price and list price are required");
		}
		Product product = productTextileMapper.VoToEntity(productVO);
		product.setBarcode("BAR-" + getSaltString().toString());
		product = productRepository.save(product);
		saveProductTransaction(product, NatureOfTransaction.PURCHASE.getName(), PRODUCT_TABLE, product.getId(),
				PRODUCT_PURCHASE_COMMENT);
		return productTextileMapper.EntityToVo(product);
	}

	@Override
	public ProductVO updateBarcode(ProductVO productVO) {
		log.debug(" debugging updateBarcode:" + productVO);
		Optional<Product> productOptional = productRepository.findById(productVO.getId());
		if (!productOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found with id:" + productVO.getId());
		}
		// Disable old product and create new product when rebar is done
		Product oldProduct = productOptional.get();
		oldProduct.setStatus(ProductStatus.DISABLE);
		productRepository.save(oldProduct);

		Product product = productTextileMapper.VoToEntity(productVO);
		product.setBarcode(
				"REBAR/" + LocalDate.now().getYear() + LocalDate.now().getDayOfMonth() + "/" + getSaltString());
		product.setParentBarcode(oldProduct.getBarcode());
		product = productRepository.save(product);

		/*
		 * List<ProductTransaction> transact = productTransactionRepository
		 * .findAllByBarcodeId(productOptional.get().getBarcode());
		 * transact.stream().forEach(t -> { if
		 * (t.getEffectingTable().equals("product textile table")) { t =
		 * productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(
		 * productOptional.get().getBarcode(), "product textile table", true);
		 * t.setMasterFlag(false); productTransactionRepository.save(t); } else if
		 * (t.getEffectingTable().equals("Adjustments")) { t =
		 * productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(
		 * productOptional.get().getBarcode(), "Adjustments", true);
		 * t.setMasterFlag(false); productTransactionRepository.save(t); } });
		 */
		Adjustments adjustments = saveAdjustment(product.getStoreId(), productVO.getEmpId(), product.getBarcode(),
				oldProduct.getBarcode(), "rebar");
		saveProductTransaction(product, NatureOfTransaction.REBARPARENT.getName(), ADJUSTMENTS_TABLE,
				adjustments.getAdjustmentId(), ADJUSTMENTS_TABLE);
		return productTextileMapper.EntityToVo(product);
	}

	@Override
	public void deleteProduct(Long id) {
		Optional<Product> productOptional = productRepository.findById(id);
		if (!productOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No record found with id:" + id);
		}
		Product product = productOptional.get();
		deleteAdjustment(product.getBarcode());
		// saveAndUpdateProductTransaction(product.getBarcode());
		/*
		 * productTransactions.stream().forEach(t -> { if
		 * (t.getComment().equals("newly inserted table")) { t =
		 * productTransactionRepository.findByBarcodeIdAndCommentAndMasterFlag(barcode,
		 * "newly inserted table", true); productTransactionRepository.delete(t); } else
		 * if (t.getComment().equals("Adjustments")) { t =
		 * productTransactionRepository.findByBarcodeIdAndCommentAndMasterFlag(barcode,
		 * "Adjustments", true); productTransactionRepository.delete(t); } });
		 */
		product.setStatus(ProductStatus.DISABLE);
		productRepository.save(product);
	}

	private List<ProductTransaction> saveAndUpdateProductTransaction(String barcode) {
		List<ProductTransaction> transact = new ArrayList<>();
		transact = productTransactionRepository.findAllByBarcodeId(barcode);
		transact.stream().forEach(t -> {
			if (t.getEffectingTable().equals("product textile table")) {
				t = productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(barcode,
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
				ProductTransaction saveTrans = productTransactionRepository.save(prodTrans);

			} else if (t.getEffectingTable().equals("Adjustments")) {
				t = productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(barcode, "Adjustments",
						true);
				ProductTransaction prodTrans = new ProductTransaction();
				prodTrans.setBarcodeId(t.getBarcodeId());
				prodTrans.setStoreId(t.getStoreId());
				prodTrans.setEffectingTableId(t.getEffectingTableId());
				prodTrans.setQuantity(t.getQuantity());
				prodTrans.setNatureOfTransaction(t.getNatureOfTransaction());
				prodTrans.setMasterFlag(false);
				prodTrans.setComment("deleted");
				prodTrans.setEffectingTable(t.getEffectingTable());
				ProductTransaction saveTrans = productTransactionRepository.save(prodTrans);
			}
		});

		return transact;
	}

	private Optional<Adjustments> deleteAdjustment(String barcode) {
		Optional<Adjustments> adjustmentOptional = adjustmentRepository.findByCurrentBarcodeIdAndType(barcode,
				AdjustmentType.REBAR);
		if (adjustmentOptional.isPresent()) {
			Adjustments adjustment = adjustmentOptional.get();
			adjustment.setStatus(false);
			adjustment = adjustmentRepository.save(adjustment);
			return Optional.of(adjustment);
		}
		return Optional.empty();
	}

	/**
	 * get Barcode Details
	 */
	@Override
	public ProductVO getBarcode(String barcode, Long storeId) {
		if (storeId == null) {
			throw new RecordNotFoundException("storeId is missing" + storeId);
		}
		if (barcode.startsWith("BAR") || barcode.startsWith("REBAR")) {
			Product parentProduct = productRepository.findByParentBarcode(barcode);
			if (parentProduct != null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, barcode + " barcode is invalidated");
			}

			Product product = productRepository.findByBarcode(barcode);
			if (product == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No records found for barcode:" + barcode);
			}
			ProductVO productVO = productTextileMapper.EntityToVo(product);
			List<UserDetailsVo> userDetailsVo = new ArrayList<>();
			try {
				userDetailsVo = getUsersForGivenId(Arrays.asList(productVO.getEmpId()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			if (userDetailsVo != null) {
				userDetailsVo.stream().forEach(userDetails -> productVO.setEmpName(userDetails.getUserName()));
			}
			productVO.setQty(product.getQty());
			productVO.setValue(product.getQty() * product.getItemMrp());
			return productVO;
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid barcode entered:" + barcode);
		}
	}

	@Override
	public ProductVO barcodeDetails(String barcode, Long clientId, Long storeId) {
		ProductVO productVO = getBarcode(barcode, storeId);
		if (StringUtils.isNotBlank(productVO.getHsnCode())) {
			try {
				Map taxValues = getHsnDetails(productVO.getHsnCode(), productVO.getItemMrp(), clientId);
				productVO.setTaxValues(taxValues);
			} catch (Exception ex) {
				log.info("exception occured while fetching tax values for barcode {}", barcode);
			}
		}
		return productVO;
	}

	public Map getHsnDetails(String hsnCode, Float itemPrice, Long clientId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("clientId", String.valueOf(clientId));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity requestEntity = new HttpEntity<>(headers);
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(config.getHsnDetailsUrl())
				.queryParam("itemPrice", itemPrice).queryParam("hsnCode", hsnCode).build();
		ResponseEntity<Map> hsnResponse = restTemplate.exchange(builder.toString(), HttpMethod.GET, requestEntity,
				Map.class);
		if (hsnResponse.getStatusCode().equals(HttpStatus.OK) && hsnResponse.hasBody()) {
			return hsnResponse.getBody();
		}
		return MapUtils.EMPTY_SORTED_MAP;
	}

	/**
	 * get Barcode Details by filters
	 */
	@Override
	public Page<ProductVO> getAllBarcodes(SearchFilterVo searchFilterVo, Pageable pageable) {
		Page<Product> barcodeDetails = null;
		ProductStatus status = ProductStatus.ENABLE;

		/*
		 * using dates and storeId
		 */
		if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() == null
				&& StringUtils.isEmpty(searchFilterVo.getBarcode()) && searchFilterVo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
			LocalDateTime fromTimeMax = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getFromDate());
			barcodeDetails = productRepository.findByCreatedDateBetweenAndStatusAndStoreId(fromTime, fromTimeMax,
					status, searchFilterVo.getStoreId(), pageable);
		}

		else if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() != null
				&& StringUtils.isEmpty(searchFilterVo.getBarcode()) && searchFilterVo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getToDate());
			barcodeDetails = productRepository.findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, status, searchFilterVo.getStoreId(), pageable);

		}

		/*
		 * using dates with barcode and storeId
		 */

		else if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() != null
				&& StringUtils.isNotEmpty(searchFilterVo.getBarcode()) && searchFilterVo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getToDate());
			barcodeDetails = productRepository.findByCreatedDateBetweenAndBarcodeAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, searchFilterVo.getBarcode(), searchFilterVo.getStoreId(), pageable);
		}
		/*
		 * using barcode and storeId
		 */
		else if (StringUtils.isNotEmpty(searchFilterVo.getBarcode()) && searchFilterVo.getStoreId() != null) {
			barcodeDetails = productRepository.findByBarcodeAndStoreId(searchFilterVo.getBarcode(),
					searchFilterVo.getStoreId(), pageable);
		} /*
			 * using storeId
			 */
		else if (searchFilterVo.getStoreId() != null) {
			barcodeDetails = productRepository.findByStoreIdAndStatus(searchFilterVo.getStoreId(), status, pageable);
		}

		/*
		 * values with empty string
		 */
		else {
			barcodeDetails = productRepository.findByStatus(status, pageable);
		}

		if (barcodeDetails == null || !barcodeDetails.hasContent()) {
			return Page.empty();
		}

		return barcodeDetails.map(barcode -> mapToVo(barcode));

	}

	private ProductVO mapToVo(Product barcode) {
		ProductVO productTextileVO = productTextileMapper.EntityToVo(barcode);

		List<UserDetailsVo> userDetailsVo = new ArrayList<>();
		try {
			userDetailsVo = getUsersForGivenId(Arrays.asList(productTextileVO.getEmpId()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (userDetailsVo != null) {
			userDetailsVo.stream().forEach(x -> {
				productTextileVO.setEmpName(x.getUserName());
			});
		}
		productTextileVO.setValue(barcode.getQty() * barcode.getItemMrp());
		return productTextileVO;

	}

	@Override
	public void inventoryUpdate(List<InventoryUpdateVo> request) {
		request.stream().forEach(x -> {
			// for textile update
			if (x.getDomainId() == 1) {
				Product barcodeDetails = productRepository.findByBarcode(x.getBarCode());
				if (barcodeDetails == null) {
					log.error("record not found with barcode:" + x.getBarCode());
					throw new RecordNotFoundException("record not found with barcode:" + x.getBarCode());
				}
				List<ProductTransaction> transact = new ArrayList<>();
				transact = productTransactionRepository.findAllByBarcodeId(barcodeDetails.getBarcode());
				transact.stream().forEach(t -> {
					if (t.getEffectingTable().equals("product textile table")) {
						t = productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(
								barcodeDetails.getBarcode(), "product textile table", true);
						t.setQuantity(Math.abs(x.getQuantity() - t.getQuantity()));
						productTransactionRepository.save(t);
					} else if (t.getEffectingTable().equals("Adjustments")) {
						t = productTransactionRepository.findByBarcodeIdAndEffectingTableAndMasterFlag(
								barcodeDetails.getBarcode(), "Adjustments", true);
						t.setQuantity(Math.abs(x.getQuantity() - t.getQuantity()));
						productTransactionRepository.save(t);
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
				ProductTransaction textileUpdate = productTransactionRepository.save(prodTrans);
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
	public Page<AdjustmentsVo> getAdjustments(SearchFilterVo searchFilterVo, Pageable pageable) {
		Page<Adjustments> adjustmentDetails = null;

		if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() == null
				&& StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getFromDate());
			adjustmentDetails = adjustmentRepository.findByCreatedDateBetweenAndTypeOrderByCreatedDateDesc(fromTime,
					toTime, AdjustmentType.REBAR, pageable);
		}
		/*
		 * using dates with storeId
		 */
		else if (searchFilterVo.getFromDate() != null && searchFilterVo.getToDate() != null
				&& StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(searchFilterVo.getFromDate());
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(searchFilterVo.getToDate());
			adjustmentDetails = adjustmentRepository.findByCreatedDateBetweenAndTypeOrderByCreatedDateDesc(fromTime,
					toTime, AdjustmentType.REBAR, pageable);

		}

		/*
		 * using dates and currentBarcodeId and storeId
		 */
		else if (StringUtils.isNotEmpty(searchFilterVo.getCurrentBarcodeId())) {
			Optional<Adjustments> adjustmentOptional = adjustmentRepository
					.findByCurrentBarcodeIdAndType(searchFilterVo.getCurrentBarcodeId(), AdjustmentType.REBAR);
			if (adjustmentOptional.isPresent()) {
				Adjustments adjustment = adjustmentOptional.get();
				List<Adjustments> adjustments = Arrays.asList(adjustment);
				int pageSize = pageable.getPageSize();
				int currentPage = pageable.getPageNumber();
				// adjustmentDetails = new PageImpl<>(adjustments);
				adjustmentDetails = new PageImpl<Adjustments>(adjustments, PageRequest.of(currentPage, pageSize),
						adjustments.size());

			}

		}

		/*
		 * rebars list for specific store
		 */
		else if (StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() != null) {
			adjustmentDetails = adjustmentRepository.findByTypeAndStoreIdOrderByCreatedDateDesc(AdjustmentType.REBAR,
					searchFilterVo.getStoreId(), pageable);
		}

		/*
		 * rebars list for all store
		 */
		else if (StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() == null) {
			adjustmentDetails = adjustmentRepository.findByTypeOrderByCreatedDateDesc(AdjustmentType.REBAR, pageable);
		}

		if (adjustmentDetails.hasContent()) {
			return adjustmentDetails.map(adjustment -> adjustmentMapToVo(adjustment));
		}
		return Page.empty();
	}

	private AdjustmentsVo adjustmentMapToVo(Adjustments adjustment) {
		return adjustmentMapper.EntityToVo(adjustment);
	}

	@Override
	public void saveProducts(List<ProductVO> products, Long storeId) {
		products.stream().forEach(product -> {
			product.setStoreId(storeId);
			addBarcode(product);
		});
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
			columns = productRepository.findAllColumnNames();
		} else {
			columns = productItemRepo.findAllColumnNames();
		}
		return columns;
	}

	@Override
	public Page<ProductVO> getBarcodeTextileReports(SearchFilterVo vo, Pageable pageable) {
		log.debug("debugging getBarcodeTextileReports():" + vo);
		Page<Product> barcodeDetails = null;
		ProductStatus status = ProductStatus.ENABLE;
		List<Product> barStore = productRepository.findByStoreId(vo.getStoreId());
		if (barStore != null) {

			/*
			 * using fromDate and storeId
			 */
			if (vo.getFromDate() != null && (vo.getToDate() == null)
					&& (vo.getBarcode() == null || vo.getBarcode() == "") && vo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime fromTime1 = DateConverters.convertToLocalDateTimeMax(vo.getFromDate());
				barcodeDetails = productRepository.findByCreatedDateBetweenAndStatusAndStoreId(fromTime, fromTime1,
						status, vo.getStoreId(), pageable);
			}

			/*
			 * using dates and storeId
			 */

			else if (vo.getFromDate() != null && vo.getToDate() != null
					&& (vo.getBarcode() == null || vo.getBarcode() == "") && vo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
				barcodeDetails = productRepository
						.findByCreatedDateBetweenAndStatusAndStoreIdOrderByLastModifiedDateAsc(fromTime, toTime, status,
								vo.getStoreId(), pageable);

			}

			/*
			 * using dates with barcode and storeId
			 */

			else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getBarcode() != null
					&& vo.getStoreId() != null) {
				Product barOpt = productRepository.findByBarcode(vo.getBarcode());
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
				if (barOpt != null) {
					barcodeDetails = productRepository
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

				barcodeDetails = productRepository.findByItemMrpBetweenAndStoreIdAndStatus(vo.getItemMrpLessThan(),
						vo.getItemMrpGreaterThan(), vo.getStoreId(), status, pageable);

			}
			/*
			 * using empId
			 */
			else if (vo.getEmpId() != null) {
				barcodeDetails = productRepository.findByEmpIdAndStatusAndStoreId(vo.getEmpId(), status,
						vo.getStoreId(), pageable);
			}
			/*
			 * using barcode and storeId
			 */
			else if (vo.getFromDate() == null && vo.getToDate() == null && (vo.getBarcode() != null)
					&& vo.getStoreId() != null) {
				barcodeDetails = productRepository.findByBarcodeAndStoreId(vo.getBarcode(), vo.getStoreId(), pageable);
			}

			/*
			 * using storeId
			 */
			else if (vo.getStoreId() != null) {
				barcodeDetails = productRepository.findByStoreIdAndStatus(vo.getStoreId(), status, pageable);

			}

			/*
			 * using barcode
			 */
			else if (vo.getFromDate() == null && vo.getToDate() == null && vo.getBarcode() != null
					&& vo.getStoreId() == null && vo.getItemMrpGreaterThan() == 0 && vo.getItemMrpLessThan() == 0
					&& vo.getEmpId() == null) {
				Product textile = productRepository.findByBarcode(vo.getBarcode());
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
	public List<ProductVO> getBarcodes(List<String> barcodes) {
		log.debug("deugging getBarcodeDetails" + barcodes);
		List<ProductVO> productsList = new ArrayList<ProductVO>();
		List<Product> barcodeDetails = productRepository.findByBarcodeIn(barcodes);
		productsList = productTextileMapper.EntityToVo(barcodeDetails);
		return productsList;
	}

	@Override
	public ProductVO getProductByParentBarcode(String parentBarcode) {
		Product product = productRepository.findByBarcode(parentBarcode);
		if (product != null) {
			ProductVO productVO = productTextileMapper.EntityToVo(product);
			productVO.setValue(product.getQty() * product.getItemMrp());
			return productVO;
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No record found with parentBarcode:" + parentBarcode);
	}

	@Override
	public void addBulkProducts(MultipartFile multipartFile, Long storeId)
			throws InstantiationException, IllegalAccessException, IOException {
		List<ProductVO> products = excelService.readExcel(multipartFile.getInputStream(), ProductVO.class);
		if (CollectionUtils.isNotEmpty(products)) {
			products.forEach(product -> {
				product.setStoreId(storeId);
				addBarcode(product);
			});
		}
	}

	@Override
	public DomainTypePropertiesVO getProperties(String domainType) {
		DomainTypePropertiesVO domainTypePropertiesVO = new DomainTypePropertiesVO();
		if (domainType.equals(DomainType.Textile.getName())) {

			List<String> properties = Arrays.asList("category", "division", "section", "subSection", "colour");
			List<Map<String, FieldNameVO>> list = new ArrayList<>();
			properties.forEach(property -> {
				Map<String, FieldNameVO> fieldValueMap = new HashMap<>();
				FieldNameVO fieldNameVO = new FieldNameVO();
				fieldNameVO.setFieldName(property);
				fieldValueMap.put(property, fieldNameVO);
				list.add(fieldValueMap);
			});
			domainTypePropertiesVO.setProperties(list);
			domainTypePropertiesVO.setDomainType(DomainType.Textile.getName());
		}
		return domainTypePropertiesVO;
	}

	private Adjustments saveAdjustment(Long storeId, Long createdBy, String rebarcode, String barcode,
			String comments) {
		Adjustments adjustment = new Adjustments();
		adjustment.setCreatedBy(createdBy);
		adjustment.setCurrentBarcodeId(rebarcode);
		adjustment.setToBeBarcodeId(barcode);
		adjustment.setComments(comments);
		adjustment.setStoreId(storeId);
		adjustment.setType(AdjustmentType.REBAR);
		adjustment = adjustmentRepository.save(adjustment);
		return adjustment;
	}

	private void saveProductTransaction(Product product, String natureOfTransaction, String table,
			Long effectingTableId, String comment) {
		ProductTransaction prodTransaction = new ProductTransaction();
		prodTransaction.setBarcodeId(product.getBarcode());
		prodTransaction.setStoreId(product.getStoreId());
		prodTransaction.setEffectingTableId(effectingTableId);
		prodTransaction.setQuantity(product.getQty());
		prodTransaction.setNatureOfTransaction(natureOfTransaction);
		prodTransaction.setMasterFlag(true);
		prodTransaction.setComment(comment);
		prodTransaction.setEffectingTable(table);
		productTransactionRepository.save(prodTransaction);
	}

	private List<UserDetailsVo> getUsersForGivenId(List<Long> userIds) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		URI uri = UriComponentsBuilder.fromUri(new URI(config.getUserDetails())).build().encode().toUri();
		HttpEntity<List<Long>> request = new HttpEntity<List<Long>>(userIds, headers);
		ResponseEntity<?> usersResponse = restTemplate.exchange(uri, HttpMethod.POST, request, GateWayResponse.class);
		ObjectMapper mapper = new ObjectMapper();
		GateWayResponse<?> gatewayResponse = mapper.convertValue(usersResponse.getBody(), GateWayResponse.class);
		List<UserDetailsVo> bvo = mapper.convertValue(gatewayResponse.getResult(),
				new TypeReference<List<UserDetailsVo>>() {
				});
		return bvo;

	}

	protected String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 6) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

}
