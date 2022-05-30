package com.otsi.retail.inventory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.otsi.retail.inventory.commons.Generation;
import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.ProductBundleMapper;
import com.otsi.retail.inventory.mapper.ProductTextileMapper;
import com.otsi.retail.inventory.model.Product;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.model.ProductBundleAssignmentTextile;
import com.otsi.retail.inventory.repo.BundledProductAssignmentRepository;
import com.otsi.retail.inventory.repo.ProductBundleRepo;
import com.otsi.retail.inventory.repo.ProductRepository;
import com.otsi.retail.inventory.util.DateConverters;
import com.otsi.retail.inventory.vo.ProductBundleVo;
import com.otsi.retail.inventory.vo.ProductVO;

@Component
public class ProductBundleServiceImpl implements ProductBundleService {

	private Logger log = LogManager.getLogger(ProductBundleServiceImpl.class);

	@Autowired
	private ProductBundleMapper productBundleMapper;

	@Autowired
	private ProductBundleRepo productBundleRepo;

	@Autowired
	private ProductRepository productTextileRepo;

	@Autowired
	private ProductTextileMapper productTextileMapper;

	@Autowired
	private BundledProductAssignmentRepository bundledProductAssignmentRepository;

	@Transactional
	@Override
	public ProductBundleVo addProductBundle(ProductBundleVo productBundleVo) {
		if (productBundleVo.getName() == null || productBundleVo.getBundleQuantity() == null
				|| productBundleVo.getProductTextiles() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"bundle name,bundle qty and products are required");
		}
		Product product = new Product();
		ProductBundle bundle = productBundleMapper.voToEntity(productBundleVo);
		bundle.setBarcode("BAR-" + Generation.getSaltString().toString());
		List<ProductVO> textiles = productBundleVo.getProductTextiles();
		ProductStatus status = ProductStatus.ENABLE;

		List<ProductBundleAssignmentTextile> bundleList = new ArrayList<ProductBundleAssignmentTextile>();

		textiles.stream().forEach(productTextile -> {
			Product productBarcode = productTextileRepo.findByBarcodeAndStatus(productTextile.getBarcode(), status);
			if (productBarcode != null) {
				productBarcode.setSellingTypeCode(ProductEnum.PRODUCTBUNDLE);
				productTextileRepo.save(productBarcode);
			}

			ProductBundleAssignmentTextile bundledProductAssignment = new ProductBundleAssignmentTextile();
			bundledProductAssignment.setProductBundleId(bundle);
			bundledProductAssignment.setAssignedproductId(productTextileMapper.customVoToEntityMapper(productTextile));
			bundledProductAssignment.setQuantity(productTextile.getQty());
			bundleList.add(bundledProductAssignment);

			if (productTextile.getQty() > 1) {
				double itemMrpCalculation = 0L;
				itemMrpCalculation = textiles.stream().mapToDouble(x -> x.getItemMrp() * x.getQty()).sum();
				float mrp = (float) itemMrpCalculation;
				product.setItemMrp(mrp);
				bundle.setItemMrp(mrp);
			}
		});

		bundledProductAssignmentRepository.saveAll(bundleList);
		// saving product bundle again as individual product

		product.setBarcode(bundle.getBarcode());
		product.setStoreId(bundle.getStoreId());
		product.setSellingTypeCode(ProductEnum.BUNDLEDPRODUCT);
		product.setQty(bundle.getBundleQuantity());
		productTextileRepo.save(product);
		ProductBundle bundleSave = productBundleRepo.save(bundle);
		productBundleVo = productBundleMapper.entityToVO(bundleSave);
		return productBundleVo;
	}

	@Override
	public Optional<ProductBundle> getProductBundle(Long id) {
		log.debug("debugging getProductBundle:" + id);
		Optional<ProductBundle> productBundle = productBundleRepo.findById(id);
		if (!(productBundle.isPresent())) {
			log.error("bundle  record is not found");
			throw new RecordNotFoundException("bundle record is not found");
		}
		log.info("after fetching product bundle details:" + productBundle.toString());
		return productBundle;
	}

	@Override
	public Page<ProductBundleVo> getAllProductBundles(LocalDate fromDate, LocalDate toDate, Long id, Long storeId,
			Pageable pageable) {
		log.debug("debugging getAllProductBundles:" + fromDate + "and to date:" + toDate + "and id:" + id
				+ "and storeId:" + storeId);
		Page<ProductBundle> bundles = null;
		Boolean status = Boolean.TRUE;

		/*
		 * using dates with storeId
		 */
		if (fromDate != null && toDate != null && id == null && storeId != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(toDate);

			bundles = productBundleRepo.findByCreatedDateBetweenAndStatus(fromTime, toTime, status, pageable);

		}
		/*
		 * using dates and bundle id and storeId
		 */

		else if (fromDate != null && toDate != null && id != null && storeId != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(toDate);
			bundles = productBundleRepo.findByCreatedDateBetweenAndIdAndStatusAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, id, status, storeId, pageable);
		}
		/*
		 * using bundle id and storeId
		 */
		else if (fromDate == null && toDate == null && id != null && storeId != null) {
			bundles = productBundleRepo.findByIdAndStatusAndStoreId(id, status, storeId, pageable);

		}

		/*
		 * using from date
		 */
		if (fromDate != null && storeId != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(fromDate);

			bundles = productBundleRepo.findByCreatedDateBetweenAndStoreIdAndStatus(fromTime, toTime, storeId, status,
					pageable);
		}
		/*
		 * using storeId
		 */
		else if (storeId != null) {
			bundles = productBundleRepo.findAllByStoreIdAndStatus(storeId, status, pageable);
		}

		return bundles.map(bundle -> bundleMapToVo(bundle));
	}

	private ProductBundleVo bundleMapToVo(ProductBundle productBundle) {
		ProductBundleVo productBundleVo = productBundleMapper.entityToVO(productBundle);
		productBundleVo.setProductTextiles(productTextileMapper.EntityToVo(productBundle.getProductTextiles()));
		productBundleVo.getProductTextiles().stream().forEach(product -> {

			Product productBarcode = productTextileRepo.findByBarcodeAndSellingTypeCode(product.getBarcode(),
					ProductEnum.PRODUCTBUNDLE);
			productBundleVo.setValue(productBundleVo.getBundleQuantity() * productBarcode.getItemMrp());
		});

		return productBundleVo;

	}

	@Override
	public String updateProductBundle(ProductBundleVo productBundleVo) {
		log.debug("debugging updateProductBundle:" + productBundleVo);
		Optional<ProductBundle> productBundleOpt = productBundleRepo.findById(productBundleVo.getId());
		if (!productBundleOpt.isPresent()) {
			throw new RecordNotFoundException("bundle data is  not found with id: " + productBundleVo.getId());
		}
		ProductBundle productBundle = productBundleMapper.voToEntity(productBundleVo);
		productBundle.setId(productBundleVo.getId());
		ProductBundle productBundleUpdate = productBundleRepo.save(productBundle);
		log.info("after updating bundle details:" + productBundleUpdate);
		return "after updated bundle successfully:" + productBundleVo.toString();
	}

	@Override
	public ProductBundleVo deleteProductBundle(Long id) {
		log.debug("debugging deleteProductBundle:" + id);
		Optional<ProductBundle> productBundleOpt = productBundleRepo.findById(id);
		if (!(productBundleOpt.isPresent())) {
			throw new RecordNotFoundException("bundle not found with id: " + id);
		}
		productBundleOpt.get().setStatus(Boolean.FALSE);

		// productBundleRepo.delete(productBundleOpt.get());
		log.info("after deleting bundle details:" + id);
		ProductBundleVo productBundleVo = productBundleMapper
				.entityToVO(productBundleRepo.save(productBundleOpt.get()));
		return productBundleVo;
	}

}
