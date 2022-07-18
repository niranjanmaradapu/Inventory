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
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otsi.retail.inventory.commons.AdjustmentType;
import com.otsi.retail.inventory.commons.Categories;
import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.commons.Generation;
import com.otsi.retail.inventory.commons.NatureOfTransaction;
import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.config.Config;
import com.otsi.retail.inventory.exceptions.InvalidDataException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.gatewayresponse.GateWayResponse;
import com.otsi.retail.inventory.mapper.AdjustmentMapper;
import com.otsi.retail.inventory.mapper.DomainAttributesMapper;
import com.otsi.retail.inventory.mapper.ProductMapper;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.model.CatalogEntity;
import com.otsi.retail.inventory.model.DomainAttributes;
import com.otsi.retail.inventory.model.Product;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.model.ProductBundleAssignmentTextile;
import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.repo.AdjustmentRepository;
import com.otsi.retail.inventory.repo.BundledProductAssignmentRepository;
import com.otsi.retail.inventory.repo.CatalogRepository;
import com.otsi.retail.inventory.repo.DomainAttributesRepository;
import com.otsi.retail.inventory.repo.ProductBundleRepository;
import com.otsi.retail.inventory.repo.ProductRepository;
import com.otsi.retail.inventory.repo.ProductTransactionRepository;
import com.otsi.retail.inventory.util.Constants;
import com.otsi.retail.inventory.util.DateConverters;
import com.otsi.retail.inventory.util.ExcelService;
import com.otsi.retail.inventory.vo.AdjustmentsVO;
import com.otsi.retail.inventory.vo.DomainAttributesVO;
import com.otsi.retail.inventory.vo.DomainTypePropertiesVO;
import com.otsi.retail.inventory.vo.FieldNameVO;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.InvoiceDetailsVO;
import com.otsi.retail.inventory.vo.ProductVO;
import com.otsi.retail.inventory.vo.SearchFilterVo;
import com.otsi.retail.inventory.vo.UserDetailsVo;
import com.otsi.retail.inventory.vo.ValuesVO;

@Component
public class ProductServiceImpl implements ProductService {

	private Logger log = LogManager.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductTransactionRepository productTransactionRepository;

	@Autowired
	private AdjustmentRepository adjustmentRepository;

	@Autowired
	private ProductBundleRepository productBundleRepository;

	@Autowired
	private AdjustmentMapper adjustmentMapper;

	@Autowired
	private BundledProductAssignmentRepository bundledProductAssignmentRepository;

	@PersistenceContext
	EntityManager em;

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private Config config;

	@Autowired
	private ExcelService excelService;

	@Autowired
	private ProductBundleRepository productBundleRepo;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DomainAttributesRepository domainAttributesRepository;

	@Autowired
	private DomainAttributesMapper domainAttributesMapper;

	private static final String PRODUCT_TABLE = "PRODUCT";
	private static final String PRODUCT_PURCHASE_COMMENT = "INSERTED";
	private static final String ADJUSTMENTS_TABLE = "ADJUSTMENT";

	@Override
	public ProductVO addBarcode(ProductVO productVO) {
		log.debug("debugging save Product:" + productVO);
		if (productVO.getCostPrice() == 0 || productVO.getItemMrp() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cost price and list price are required");
		}
		Product productBarcode = productRepository.findByBarcode(productVO.getBarcode());

		Product product = productMapper.voToEntity(productVO);
		if (productBarcode == null) {
			product.setBarcode(productVO.getBarcode());
		} else if (StringUtils.isNotEmpty(productBarcode.getBarcode())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode already exists");
		} else if (productVO.getBarcode() == null) {
			product.setBarcode("BAR-" + Generation.getSaltString().toString());
		}

		product = productRepository.save(product);
		saveProductTransaction(product, NatureOfTransaction.PURCHASE.getName(), PRODUCT_TABLE, product.getId(),
				PRODUCT_PURCHASE_COMMENT);
		return productMapper.entityToVO(product);
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

		Product product = new Product();
		product.setCostPrice(productOptional.get().getCostPrice());
		product.setEmpId(productOptional.get().getEmpId());
		product.setStatus(ProductStatus.ENABLE);
		product.setName(productOptional.get().getName());
		product.setBarcode(productOptional.get().getBarcode());
		product.setDivision(productOptional.get().getDivision());
		product.setSection(productOptional.get().getSection());
		product.setSubSection(productOptional.get().getSubSection());
		product.setOriginalBarcodeCreatedAt(LocalDate.now());
		product.setCategory(productOptional.get().getCategory());
		product.setBatchNo(productOptional.get().getBatchNo());
		product.setColour(productOptional.get().getColour());
		product.setHsnCode(productOptional.get().getHsnCode());
		product.setItemMrp(productVO.getItemMrp());
		product.setUom(productOptional.get().getUom());
		product.setQty(productVO.getQty());
		product.setStoreId(productOptional.get().getStoreId());
		product.setDomainType(productOptional.get().getDomainType());
		product.setBarcode("REBAR/" + LocalDate.now().getYear() + LocalDate.now().getDayOfMonth() + "/"
				+ Generation.getSaltString());
		if (product.getSellingTypeCode() != null) {
			product.setSellingTypeCode(productOptional.get().getSellingTypeCode());
		}
		product.setParentBarcode(oldProduct.getBarcode());
		product = productRepository.save(product);

		Adjustments adjustments = saveAdjustment(product.getStoreId(), productVO.getEmpId(), product.getBarcode(),
				oldProduct.getBarcode(), "rebar");
		saveProductTransaction(product, NatureOfTransaction.REBARPARENT.getName(), ADJUSTMENTS_TABLE,
				adjustments.getAdjustmentId(), ADJUSTMENTS_TABLE);
		return productMapper.entityToVO(product);
	}

	@Override
	public ProductVO updateQuantity(ProductVO productVO) {
		Optional<Product> productOptional = productRepository.findById(productVO.getId());
		if (!productOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found with id:" + productVO.getId());
		}

		Product productQty = productOptional.get();
		// update quantity only
		productQty.setQty(productVO.getQty());
		Product updateQty = productRepository.save(productQty);
		return productMapper.entityToVO(updateQty);
	}

	@Override
	public void deleteProduct(Long id) {
		Optional<Product> productOptional = productRepository.findById(id);
		if (!productOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No record found with id:" + id);
		}
		Product product = productOptional.get();
		deleteAdjustment(product.getBarcode());
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

			Product product = productRepository.findByBarcodeAndStoreId(barcode, storeId);
			if (product == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No records found for barcode:" + barcode);
			}

			ProductVO productVO = productMapper.entityToVO(product);
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
	public ProductVO barcodeDetails(String barcode, Long clientId, Long storeId, Boolean isTaxIncluded) {
		ProductVO productVO = getBarcode(barcode, storeId);
		if (StringUtils.isNotBlank(productVO.getHsnCode())) {
			try {
				Map taxValues = getHsnDetails(productVO.getHsnCode(), productVO.getItemMrp(), clientId, isTaxIncluded);
				productVO.setTaxValues(taxValues);
			} catch (Exception ex) {
				log.info("exception occured while fetching tax values for barcode {}", barcode);
			}
		}
		return productVO;
	}

	@Override
	public InvoiceDetailsVO scanAndFetchbarcodeDetails(String barcode, Long clientId, Long storeId,
			Boolean isTaxIncluded) {
		ProductVO productVO = barcodeDetails(barcode, clientId, storeId, isTaxIncluded);
		return productMapper.productToInvoiceMapper(productVO);
	}

	public Map getHsnDetails(String hsnCode, Float itemPrice, Long clientId, Boolean isTaxIncluded) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("clientId", String.valueOf(clientId));
		if (isTaxIncluded != null) {
			headers.add("isTaxIncluded", String.valueOf(isTaxIncluded));
		}
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
			barcodeDetails = productRepository.findByStoreIdAndStatusOrderByCreatedDateDesc(searchFilterVo.getStoreId(),
					status, pageable);
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
		ProductVO productTextileVO = productMapper.entityToVO(barcode);

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
	@Transactional
	public void inventoryUpdate(List<InventoryUpdateVo> request, String type, String referringTable) {
		request.stream().forEach(x -> {
			// for textile update
			Product barcodeDetails = productRepository.findByBarcode(x.getBarCode());
			if (barcodeDetails == null) {
				log.error("record not found with barcode:" + x.getBarCode());
				throw new RecordNotFoundException("record not found with barcode:" + x.getBarCode());
			}
			if (type.equals(Constants.NEW_SALE)) {
				if (barcodeDetails.getSellingTypeCode() != null) {
					if (barcodeDetails.getSellingTypeCode().equals(ProductEnum.BUNDLEDPRODUCT)) {
						ProductBundle bundle = productBundleRepo.findByBarcode(x.getBarCode());

						bundle.setBundleQuantity(Math.abs(x.getQuantity() - bundle.getBundleQuantity()));
						List<ProductBundleAssignmentTextile> bundleAssignment = bundledProductAssignmentRepository
								.findByProductBundleId_Id(bundle.getId());

						bundleAssignment.stream().forEach(assignmentBundle -> {

							bundle.getProductTextiles().stream().forEach(product -> {

								Optional<Product> productQty = productRepository
										.findById(assignmentBundle.getAssignedProductId().getId());
								// update quantity

								productQty.get().setQty(Math.abs(x.getQuantity() - productQty.get().getQty()));
								productRepository.save(productQty.get());

							});

						});
						productBundleRepo.save(bundle);

					} else if (barcodeDetails.getSellingTypeCode().equals(ProductEnum.PRODUCTBUNDLE)) {
						barcodeDetails.setQty(Math.abs(x.getQuantity() - barcodeDetails.getQty()));
						productRepository.save(barcodeDetails);
					}
				} else {
					barcodeDetails.setQty(Math.abs(x.getQuantity() - barcodeDetails.getQty()));
					productRepository.save(barcodeDetails);
				}

			} else if (type.equals(Constants.RETURN_SLIP)) {
				barcodeDetails.setQty(Math.abs(x.getQuantity() + barcodeDetails.getQty()));
			}
			barcodeDetails.setLastModifiedDate(LocalDateTime.now());
			productRepository.save(barcodeDetails);

			ProductTransaction productTransaction = new ProductTransaction();
			productTransaction.setBarcodeId(barcodeDetails.getBarcode());
			productTransaction.setEffectingTableId(x.getLineItemId());
			productTransaction.setQuantity(x.getQuantity());
			productTransaction.setStoreId(x.getStoreId());
			productTransaction.setNatureOfTransaction(NatureOfTransaction.SALE.getName());
			productTransaction.setMasterFlag(true);
			productTransaction.setComment(Constants.SALE);
			productTransaction.setEffectingTable(referringTable);
			productTransactionRepository.save(productTransaction);
			log.info("updated textile successfully from newsale...");
		});

		log.info("updated inventory from newsale successfully..");
	}

	@Override
	public Page<AdjustmentsVO> getAdjustments(SearchFilterVo searchFilterVo, Pageable pageable) {
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
			adjustmentDetails = adjustmentRepository.findByCreatedDateBetweenAndStoreIdAndTypeOrderByCreatedDateDesc(
					fromTime, toTime, searchFilterVo.getStoreId(), AdjustmentType.REBAR, pageable);

		}

		/*
		 * using dates and currentBarcodeId and storeId
		 */
		else if (StringUtils.isNotEmpty(searchFilterVo.getCurrentBarcodeId())) {
			Optional<Adjustments> adjustmentOptional;
			adjustmentOptional = adjustmentRepository
					.findByCurrentBarcodeIdAndType(searchFilterVo.getCurrentBarcodeId(), AdjustmentType.REBAR);
			if (!adjustmentOptional.isPresent()) {
				adjustmentOptional = adjustmentRepository
						.findByToBeBarcodeIdAndType(searchFilterVo.getCurrentBarcodeId(), AdjustmentType.REBAR);
				Adjustments adjustment = adjustmentOptional.get();
				List<Adjustments> adjustments = Arrays.asList(adjustment);
				int pageSize = pageable.getPageSize();
				int currentPage = pageable.getPageNumber();
				adjustmentDetails = new PageImpl<Adjustments>(adjustments, PageRequest.of(currentPage, pageSize),
						adjustments.size());
			} else if (adjustmentOptional.isPresent()) {
				Adjustments adjustment = adjustmentOptional.get();
				List<Adjustments> adjustments = Arrays.asList(adjustment);
				int pageSize = pageable.getPageSize();
				int currentPage = pageable.getPageNumber();
				adjustmentDetails = new PageImpl<Adjustments>(adjustments, PageRequest.of(currentPage, pageSize),
						adjustments.size());
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "barcode was not found");
			}

		}

		/*
		 * rebars list for specific store
		 */
		else if (StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() != null) {
			adjustmentDetails = adjustmentRepository.findByTypeAndStoreIdAndStatusOrderByCreatedDateDesc(
					AdjustmentType.REBAR, searchFilterVo.getStoreId(), Boolean.TRUE, pageable);
		}

		/*
		 * rebars list for all store
		 */
		else if (StringUtils.isEmpty(searchFilterVo.getCurrentBarcodeId()) && searchFilterVo.getStoreId() == null) {
			adjustmentDetails = adjustmentRepository.findByTypeAndStatusOrderByCreatedDateDesc(AdjustmentType.REBAR,
					Boolean.TRUE, pageable);
		}

		if (adjustmentDetails.hasContent()) {
			return adjustmentDetails.map(adjustment -> adjustmentMapToVO(adjustment));
		}
		return Page.empty();
	}

	private AdjustmentsVO adjustmentMapToVO(Adjustments adjustment) {
		return adjustmentMapper.entityToVO(adjustment);
	}

	@Override
	public void saveProducts(List<ProductVO> products, Long storeId) {
		products.stream().forEach(product -> {
			product.setStoreId(storeId);
			addBarcode(product);
		});
	}

	@Override
	public List<ValuesVO> getValuesFromColumns(String enumName) {

		List<ValuesVO> valuesVoList = new ArrayList<>();
		List<CatalogEntity> catalogList = null;
		List<String> productList = null;

		if (enumName.equalsIgnoreCase("SECTION") || enumName.equalsIgnoreCase("SUBSECTION")
				|| enumName.equalsIgnoreCase("DIVISION") || enumName.equalsIgnoreCase("batchno")
				|| enumName.equalsIgnoreCase("uom")) {
			if (enumName.equalsIgnoreCase("DIVISION"))
				catalogList = catalogRepository.findByDescription(Categories.DIVISION);
			if (enumName.equalsIgnoreCase("SECTION"))
				catalogList = catalogRepository.findByDescription(Categories.SECTION);
			if (enumName.equalsIgnoreCase("SUBSECTION"))
				catalogList = catalogRepository.findByDescription(Categories.SUB_SECTION);
			if (enumName.equalsIgnoreCase("uom"))
				productList = productRepository.findByUom(enumName);
			if (enumName.equalsIgnoreCase("batchno"))
				productList = productRepository.findByBatchNo(enumName);
		}
		if (CollectionUtils.isNotEmpty(catalogList)) {
			catalogList.stream().forEach(catalog -> {
				ValuesVO valuesVO = new ValuesVO();
				valuesVO.setId(catalog.getId());
				valuesVO.setName(catalog.getName());
				valuesVoList.add(valuesVO);
			});
		} else if (CollectionUtils.isNotEmpty(productList)) {
			productList.stream().forEach(product -> {
				ValuesVO valuesVO = new ValuesVO();
				valuesVO.setName(product);
				valuesVoList.add(valuesVO);
			});
		}

		return valuesVoList;
	}

	@Override
	public List<String> getValuesFromProductTextileColumns(String enumName) {

		try {
			String query = null;
			String underscore = "_";
			if (enumName.equalsIgnoreCase("batchno") || enumName.equalsIgnoreCase("costprice")
					|| enumName.equalsIgnoreCase("mrp") || enumName.equalsIgnoreCase("uom")) {
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
				} else if (enumName.equalsIgnoreCase("uom")) {
					enumName = "uom";
				}
				query = "select p." + enumName + " from  product p group by  p." + enumName;
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
	public List<String> getAllColumns() {
		List<String> columns = new ArrayList<>();
		columns = productRepository.findAllColumnNames();
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
			 * using dates with empId and storeId
			 */
			else if (vo.getFromDate() != null && (vo.getToDate() != null) && ObjectUtils.isNotEmpty(vo.getEmpId())
					&& vo.getStoreId() != null) {
				LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(vo.getFromDate());
				LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(vo.getToDate());
				barcodeDetails = productRepository.findByCreatedDateBetweenAndEmpIdAndStatusAndStoreId(fromTime, toTime,
						vo.getEmpId(), status, vo.getStoreId(), pageable);
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
				barcodeDetails = productRepository.findByStoreIdAndStatusOrderByCreatedDateDesc(vo.getStoreId(), status,
						pageable);

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
		productsList = productMapper.entityToVO(barcodeDetails);
		return productsList;
	}

	@Override
	public ProductVO getProductByParentBarcode(String parentBarcode) {
		Product product = productRepository.findByBarcode(parentBarcode);
		if (product != null) {
			ProductVO productVO = productMapper.entityToVO(product);
			productVO.setValue(product.getQty() * product.getItemMrp());
			return productVO;
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No record found with parentBarcode:" + parentBarcode);
	}

	@Override
	public void addBulkProducts(MultipartFile multipartFile, Long storeId) throws InstantiationException,
			IllegalAccessException, IOException, InterruptedException, ExecutionException {
		List<ProductVO> products = excelService.readExcel(multipartFile.getInputStream(), ProductVO.class);
		if (CollectionUtils.isNotEmpty(products)) {
			products.forEach(product -> {
				product.setStoreId(storeId);
				ProductVO productVO = addBarcode(product);
				System.out.println("barcode:" + productVO.getBarcode());
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
		adjustment.setStatus(Boolean.TRUE);
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

	@Override
	public List<DomainAttributes> findDomainAttributes(DomainType domainType) {
		return domainAttributesRepository.findByDomainType(domainType);
	}

	@Override
	public DomainAttributesVO saveDomainAttributes(DomainAttributesVO domainAttributesVO) {
		DomainAttributes entity = domainAttributesMapper.toEntity(domainAttributesVO, null);
		entity = domainAttributesRepository.save(entity);
		return domainAttributesMapper.toVO(entity);
	}

	@Override
	public DomainAttributesVO updateDomainAttributes(DomainAttributesVO domainAttributesVO) {
		Optional<DomainAttributes> domainAttributesOptional = domainAttributesRepository
				.findById(domainAttributesVO.getId());
		if (domainAttributesOptional.isPresent()) {
			DomainAttributes domainAttributes = domainAttributesOptional.get();
			DomainAttributes entity = domainAttributesMapper.toEntity(domainAttributesVO, domainAttributes);
			entity = domainAttributesRepository.save(entity);
			return domainAttributesMapper.toVO(entity);

		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"no data found for id " + domainAttributesVO.getId());
		}
	}

}
