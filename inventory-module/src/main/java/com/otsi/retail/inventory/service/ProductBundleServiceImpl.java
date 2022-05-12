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
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.ProductBundleMapper;
import com.otsi.retail.inventory.mapper.ProductTextileMapper;
import com.otsi.retail.inventory.model.ProductBundle;
import com.otsi.retail.inventory.model.ProductTextile;
import com.otsi.retail.inventory.repo.ProductBundleRepo;
import com.otsi.retail.inventory.repo.ProductTextileRepo;
import com.otsi.retail.inventory.utils.DateConverters;
import com.otsi.retail.inventory.vo.ProductBundleVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;

@Component
public class ProductBundleServiceImpl implements ProductBundleService {

	private Logger log = LogManager.getLogger(ProductBundleServiceImpl.class);

	@Autowired
	private ProductBundleMapper productBundleMapper;

	@Autowired
	private ProductBundleRepo productBundleRepo;

	@Autowired
	private ProductTextileRepo productTextileRepo;

	@Autowired
	private ProductTextileMapper productTextileMapper;

	@Transactional
	@Override
	public ProductBundleVo addProductBundle(ProductBundleVo productBundleVo) {
		ProductBundle bundle = productBundleMapper.VoToEntity(productBundleVo);
		List<ProductTextileVo> textiles = productBundleVo.getProductTextiles();
		ProductStatus status = ProductStatus.ENABLE;
		textiles.stream().forEach(productTextile -> {
			ProductTextile textileBarcode = productTextileRepo.findByBarcodeAndStatus(productTextile.getBarcode(),
					status);
			if (textileBarcode != null) {
				productTextile.setSellingTypeCode(ProductEnum.PRODUCTBUNDLE);
			}
		});
		bundle.setProductTextiles(productTextileMapper.VoToEntity(textiles));
		productBundleVo = productBundleMapper.EntityToVo(productBundleRepo.save(bundle));
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
	public List<ProductBundleVo> getAllProductBundles(LocalDate fromDate, LocalDate toDate, Long id, Long storeId) {
		log.debug("debugging getAllProductBundles:" + fromDate + "and to date:" + toDate + "and id:" + id
				+ "and storeId:" + storeId);
		List<ProductBundle> bundles = new ArrayList<>();
		Boolean status = Boolean.TRUE;

		/*
		 * using dates with storeId
		 */
		if (fromDate != null && toDate != null && id == null && storeId != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(toDate);

			bundles = productBundleRepo.findByCreatedDateBetweenAndStatus(fromTime, toTime, status);

		}
		/*
		 * using dates and bundle id and storeId
		 */

		else if (fromDate != null && toDate != null && id != null && storeId != null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(toDate);
			bundles = productBundleRepo.findByCreatedDateBetweenAndIdAndStatusAndStoreIdOrderByLastModifiedDateAsc(
					fromTime, toTime, id, status, storeId);
		}
		/*
		 * using bundle id and storeId
		 */
		else if (fromDate == null && toDate == null && id != null && storeId != null) {
			bundles = productBundleRepo.findByIdAndStatusAndStoreId(id,status, storeId);

		}
		/*
		 * using storeId
		 */
		else if (storeId != null) {
			bundles = productBundleRepo.findAllByStoreIdAndStatus(storeId, status);
		}
		
		/*
		 * using from date
		 */
		if (fromDate != null && toDate==null && id==null && storeId==null) {
			LocalDateTime fromTime = DateConverters.convertLocalDateToLocalDateTime(fromDate);
			LocalDateTime toTime = DateConverters.convertToLocalDateTimeMax(fromDate);

			bundles = productBundleRepo.findByCreatedDateBetweenAndStatus(fromTime, toTime, status);
		}

		
		List<ProductBundleVo> productBundleVo = productBundleMapper.EntityToVo(bundles);

		productBundleVo.stream().forEach(bundleVo -> {

			bundleVo.getProductTextiles().stream().forEach(product -> {
				ProductTextile productTextile = productTextileRepo.findByBarcodeAndSellingTypeCode(product.getBarcode(),
						ProductEnum.PRODUCTBUNDLE);
				bundleVo.setValue(bundleVo.getBundleQuantity() * productTextile.getItemMrp());
			});

		});
		log.info("after fetching all bundle details:" + productBundleVo.toString());
		return productBundleVo;
	}

	@Override
	public String updateProductBundle(ProductBundleVo productBundleVo) {
		log.debug("debugging updateProductBundle:" + productBundleVo);
		Optional<ProductBundle> productBundleOpt = productBundleRepo.findById(productBundleVo.getId());
		if (!productBundleOpt.isPresent()) {
			throw new RecordNotFoundException("bundle data is  not found with id: " + productBundleVo.getId());
		}
		ProductBundle productBundle = productBundleMapper.VoToEntity(productBundleVo);
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
				.EntityToVo(productBundleRepo.save(productBundleOpt.get()));
		return productBundleVo;
	}

}
