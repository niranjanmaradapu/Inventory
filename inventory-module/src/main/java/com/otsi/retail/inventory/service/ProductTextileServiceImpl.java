package com.otsi.retail.inventory.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.otsi.retail.inventory.commons.NatureOfTransaction;
import com.otsi.retail.inventory.commons.ProductStatus;
import com.otsi.retail.inventory.exceptions.ParentBarcodeFoundException;
import com.otsi.retail.inventory.exceptions.RecordNotFoundException;
import com.otsi.retail.inventory.mapper.AdjustmentMapper;
import com.otsi.retail.inventory.mapper.BarcodeTextileMapper;
import com.otsi.retail.inventory.mapper.ProductTextileMapper;
import com.otsi.retail.inventory.model.Adjustments;
import com.otsi.retail.inventory.model.BarcodeTextile;
import com.otsi.retail.inventory.model.ProductInventory;
import com.otsi.retail.inventory.model.ProductItem;
import com.otsi.retail.inventory.model.ProductTextile;
import com.otsi.retail.inventory.model.ProductTransaction;
import com.otsi.retail.inventory.model.ProductTransactionRe;
import com.otsi.retail.inventory.repo.AdjustmentRepo;
import com.otsi.retail.inventory.repo.BarcodeTextileRepo;
import com.otsi.retail.inventory.repo.BarcodeTextileRepoImpl;
import com.otsi.retail.inventory.repo.ProductInventoryRepo;
import com.otsi.retail.inventory.repo.ProductItemRepo;
import com.otsi.retail.inventory.repo.ProductTextileRepo;
import com.otsi.retail.inventory.repo.ProductTextileRepoImpl;
import com.otsi.retail.inventory.repo.ProductTransactionReRepo;
import com.otsi.retail.inventory.repo.ProductTransactionRepo;
import com.otsi.retail.inventory.vo.AdjustmentsVo;
import com.otsi.retail.inventory.vo.BarcodeTextileVo;
import com.otsi.retail.inventory.vo.InventoryUpdateVo;
import com.otsi.retail.inventory.vo.ProductTextileVo;
import com.otsi.retail.inventory.vo.SearchFilterVo;

@Component
public class ProductTextileServiceImpl implements ProductTextileService {

	private Logger log = LogManager.getLogger(ProductTextileServiceImpl.class);

	@Autowired
	private BarcodeTextileMapper barcodeTextileMapper;

	@Autowired
	private ProductTextileMapper productTextileMapper;

	@Autowired
	private ProductTextileRepo productTextileRepo;

	@Autowired
	private BarcodeTextileRepo barcodeTextileRepo;

	@Autowired
	private ProductTransactionRepo productTransactionRepo;

	@Autowired
	private AdjustmentRepo adjustmentRepo;

	@Autowired
	private AdjustmentMapper adjustmentMapper;

	@Autowired
	private ProductTextileRepoImpl productTextileRepoImpl;

	@Autowired
	private BarcodeTextileRepoImpl barcodeTextileRepoImpl;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private ProductInventoryRepo productInventoryRepo;

	@Autowired
	private ProductTransactionReRepo productTransactionReRepo;

	@Override
	public String addBarcodeTextile(BarcodeTextileVo textileVo) {
		log.debug("debugging saveProductTextile:" + textileVo);
		Random ran = new Random();
		BarcodeTextile barTextile = new BarcodeTextile();
		barTextile.setBarcodeTextileId(textileVo.getBarcodeTextileId());
		barTextile.setBarcode("BAR" + ran.nextInt());
		barTextile.setCreationDate(LocalDate.now());
		barTextile.setLastModified(LocalDate.now());
		barTextile.setDivision(textileVo.getDivision());
		barTextile.setSection(textileVo.getSection());
		barTextile.setSubSection(textileVo.getSubSection());
		barTextile.setCategory(textileVo.getCategory());
		barTextile.setBatchNo(textileVo.getBatchNo());
		barTextile.setColour(textileVo.getColour());
		BarcodeTextile barTextileSave = barcodeTextileRepo.save(barTextile);
		ProductTextile textile = new ProductTextile();
		textile.setProductTextileId(textileVo.getProductTextile().getProductTextileId());
		textile.setBarcodeTextile(barTextileSave);
		textile.setCostPrice(textileVo.getProductTextile().getCostPrice());
		textile.setUom(textileVo.getProductTextile().getUom());
		textile.setHsnMasterId(textileVo.getProductTextile().getHsnMasterId());
		textile.setEmpId(textileVo.getProductTextile().getEmpId());
		textile.setItemMrp(textileVo.getProductTextile().getItemMrp());
		textile.setItemCode(textileVo.getProductTextile().getItemCode());
		textile.setCreateForLocation(0);
		textile.setValueAdditionCp(0);
		textile.setStatus(ProductStatus.ENABLE);
		textile.setStoreId(textileVo.getProductTextile().getStoreId());
		textile.setCreatedAt(LocalDate.now());
		textile.setUpdatedAt(LocalDate.now());
		textile.setOriginalBarcodeCreatedAt(LocalDate.now());
		ProductTextile textileSave = productTextileRepo.save(textile);

		ProductTransaction prodTrans = new ProductTransaction();
		prodTrans.setBarcodeId(barTextileSave.getBarcodeTextileId());
		prodTrans.setStoreId(textileSave.getStoreId());
		prodTrans.setEffectingTableId(textileSave.getProductTextileId());
		prodTrans.setQuantity(textileVo.getProductTextile().getQty());
		prodTrans.setCreationDate(LocalDate.now());
		prodTrans.setLastModified(LocalDate.now());
		prodTrans.setNatureOfTransaction(NatureOfTransaction.PURCHASE.getName());
		prodTrans.setMasterFlag(true);
		prodTrans.setComment("newly inserted table");
		prodTrans.setEffectingTable("product textile table");
		ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);
		log.warn("we are checking if textile is saved...");
		log.info("after saving textile details");
		return "barcode textile saved successfully:" + barTextileSave.getBarcodeTextileId();
	}

	@Override
	public String incrementQty(BarcodeTextileVo vo) {
		int qty = vo.getProductTextile().getQty();
		for (int i = 1; i <= qty; i++) {
			addBarcodeTextile(vo);
		}
		return "barcode textile saved successfully";
	}

	@Override
	public ProductTextileVo getProductTextile(Long productTextileId) {
		log.debug("debugging createInventory:" + productTextileId);
		Optional<ProductTextile> textile = productTextileRepo.findById(productTextileId);
		if (!(textile.isPresent())) {
			log.error("textile record is not found");
			throw new RecordNotFoundException("textile record is not found");
		}
		ProductTextileVo vo = productTextileMapper.EntityToVo(textile.get());
		vo.setBarcodeTextileId(
				barcodeTextileMapper.EntityToVo(textile.get().getBarcodeTextile()).getBarcodeTextileId());
		log.warn("we are checking if textile is fetching...");
		log.info("after fetching textile details:" + productTextileId);
		return vo;
	}

	@Override
	public String updateBarcodeTextile(BarcodeTextileVo vo) {
		log.debug(" debugging updateBarcode:" + vo);
		Optional<BarcodeTextile> dto = barcodeTextileRepo.findByBarcodeTextileId(vo.getBarcodeTextileId());
		if (!dto.isPresent()) {
			log.error("Record Not Found");
			throw new RecordNotFoundException("barcode textile record not found");
		}
		Optional<ProductTextile> prodTxt = productTextileRepo
				.findByProductTextileId(dto.get().getProductTextile().getProductTextileId());
		if (!prodTxt.isPresent()) {
			log.error("Record Not Found");
			throw new RecordNotFoundException("product textile record not found");
		}
		BarcodeTextile barTextile1 = dto.get();
		ProductTextile textile1 = barTextile1.getProductTextile();
		textile1.setStatus(ProductStatus.DISABLE);
		productTextileRepo.save(textile1);

		Random ran = new Random();
		BarcodeTextile barTextile = new BarcodeTextile();
		barTextile.setBarcode(
				"REBAR/" + LocalDate.now().getYear() + LocalDate.now().getDayOfMonth() + "/" + ran.nextInt());
		barTextile.setCreationDate(LocalDate.now());
		barTextile.setLastModified(LocalDate.now());
		barTextile.setBatchNo(vo.getBatchNo());
		barTextile.setDivision(vo.getDivision());
		barTextile.setSection(vo.getSection());
		barTextile.setSubSection(vo.getSubSection());
		barTextile.setColour(vo.getColour());
		barTextile.setCategory(vo.getCategory());
		BarcodeTextile barTextileSave = barcodeTextileRepo.save(barTextile);
		ProductTextile textile = new ProductTextile();
		textile.setParentBarcode(barTextile1.getBarcode());
		textile.setStatus(ProductStatus.ENABLE);
		textile.setBarcodeTextile(barTextileSave);
		textile.setCostPrice(vo.getProductTextile().getCostPrice());
		textile.setUom(vo.getProductTextile().getUom());
		textile.setHsnMasterId(vo.getProductTextile().getHsnMasterId());
		textile.setEmpId(vo.getProductTextile().getEmpId());
		textile.setItemMrp(vo.getProductTextile().getItemMrp());
		textile.setItemCode(vo.getProductTextile().getItemCode());
		textile.setCreateForLocation(vo.getProductTextile().getCreateForLocation());
		textile.setValueAdditionCp(vo.getProductTextile().getValueAdditionCp());
		textile.setStoreId(vo.getProductTextile().getStoreId());
		textile.setCreatedAt(LocalDate.now());
		textile.setUpdatedAt(LocalDate.now());
		textile.setOriginalBarcodeCreatedAt(LocalDate.now());
		ProductTextile textileSave = productTextileRepo.save(textile);
		ProductTransaction transact = productTransactionRepo.findByBarcodeId(vo.getBarcodeTextileId());
		transact.setMasterFlag(false);
		productTransactionRepo.save(transact);
		Adjustments ad = new Adjustments();
		ad.setCreatedBy(textileSave.getEmpId());
		ad.setCreationDate(LocalDate.now());
		ad.setCurrentBarcodeId(barTextileSave.getBarcode());
		ad.setToBeBarcodeId(barTextile1.getBarcode());
		ad.setLastModifiedDate(LocalDate.now());
		ad.setComments("rebar");
		Adjustments audSave = adjustmentRepo.save(ad);
		ProductTransaction prodTrans = new ProductTransaction();
		prodTrans.setBarcodeId(barTextileSave.getBarcodeTextileId());
		prodTrans.setStoreId(textileSave.getStoreId());
		prodTrans.setEffectingTableId(audSave.getAdjustmentId());
		prodTrans.setQuantity(vo.getProductTextile().getQty());
		prodTrans.setNatureOfTransaction(NatureOfTransaction.REBARPARENT.getName());
		prodTrans.setCreationDate(LocalDate.now());
		prodTrans.setLastModified(LocalDate.now());
		prodTrans.setMasterFlag(true);
		prodTrans.setComment("Adjustments");
		prodTrans.setEffectingTable("Adjustments");
		ProductTransaction saveTrans = productTransactionRepo.save(prodTrans);
		log.info("Rebarcoding textile updated successfully:" + barTextileSave.getBarcodeTextileId());
		return "Rebarcoding textile updated successfully:" + barTextileSave.getBarcodeTextileId();
	}

	@Override
	public String deleteBarcodeTextile(Long barcodeTextileId) {
		log.debug(" debugging deleteBarcodeTextile:" + barcodeTextileId);
		Optional<BarcodeTextile> barOpt = barcodeTextileRepo.findByBarcodeTextileId(barcodeTextileId);
		if (!barOpt.isPresent()) {
			log.error("barcode textile details not found with id");
			throw new RecordNotFoundException("barcode textile details not found with id: " + barcodeTextileId);
		}
		Optional<ProductTextile> prodOpt = productTextileRepo
				.findByProductTextileId(barOpt.get().getProductTextile().getProductTextileId());
		if (!prodOpt.isPresent()) {
			log.error("product textile details not found with id");
			throw new RecordNotFoundException(
					"product textile details not found with id: " + prodOpt.get().getProductTextileId());
		}
		productTextileRepo.delete(prodOpt.get());
		barcodeTextileRepo.delete(prodOpt.get().getBarcodeTextile());
		log.warn("we are checking if barcode is deleted based on id...");
		log.info("deleted barcode textile succesfully:" + barcodeTextileId);
		return "deleted barcode textile successfully with id:" + barcodeTextileId;

	}

	@Override
	public BarcodeTextileVo getBarcodeTextile(String barcode, Long storeId) {
		log.debug("debugging createInventory:" + barcode);
		List<BarcodeTextile> barStore = barcodeTextileRepo.findAllByProductTextileStoreId(storeId);
		BarcodeTextile textileParent = barcodeTextileRepo.findByProductTextileParentBarcode(barcode);
		if (textileParent != null) {
			log.error("parent barcode record is no more...please try current barcode.");
			throw new ParentBarcodeFoundException("parent barcode record is no more...please enter current barcode.");
		}
		if (barStore != null) {
			BarcodeTextile textile = barcodeTextileRepo.findByBarcode(barcode);
			if (textile == null) {
				log.error("textile record is not found");
				throw new RecordNotFoundException("textile record is not found");
			}
			BarcodeTextileVo vo = barcodeTextileMapper.EntityToVo(textile);
			ProductTransaction transact = productTransactionRepo.findByBarcodeId(vo.getBarcodeTextileId());
			vo.getProductTextile().setQty(transact.getQuantity());
			vo.getProductTextile().setValue(transact.getQuantity() * vo.getProductTextile().getCostPrice());
			return vo;
		} else {
			log.error("No record found with storeId:" + barStore);
			throw new RecordNotFoundException("No record found with storeId:" + barStore);
		}

	}

	@Override
	public List<BarcodeTextileVo> getAllBarcodes(SearchFilterVo vo) {
		log.debug("debugging getAllBarcodes()");
		List<BarcodeTextile> barcodeDetails = new ArrayList<>();
		/*
		 * using dates and storeId
		 */

		if (vo.getFromDate() != null && vo.getToDate() != null && (vo.getBarcode() == null || vo.getBarcode() == "")
				&& vo.getStoreId() != null) {
			List<BarcodeTextile> barStore = barcodeTextileRepo.findAllByProductTextileStoreId(vo.getStoreId());
			if (barStore != null) {
				ProductStatus status = ProductStatus.ENABLE;

				barcodeDetails = barcodeTextileRepo
						.findByCreationDateBetweenAndProductTextileStatusAndProductTextileStoreIdOrderByLastModifiedAsc(
								vo.getFromDate(), vo.getToDate(), status, vo.getStoreId());

				if (barcodeDetails.isEmpty()) {
					log.error("No record found with given information");
					throw new RecordNotFoundException("No record found with given information");
				}
			} else {
				log.error("No record found with storeId:" + barStore);
				throw new RecordNotFoundException("No record found with storeId:" + barStore);
			}
		}

		/*
		 * using dates and barcodeTextileId and storeId
		 */

		else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getBarcodeTextileId() != null
				&& vo.getStoreId() != null) {
			List<BarcodeTextile> barStore = barcodeTextileRepo.findAllByProductTextileStoreId(vo.getStoreId());
			if (barStore != null) {
				Optional<BarcodeTextile> barOpt = barcodeTextileRepo.findByBarcodeTextileId(vo.getBarcodeTextileId());
				if (barOpt.isPresent()) {
					barcodeDetails = barcodeTextileRepo
							.findByCreationDateBetweenAndBarcodeTextileIdAndProductTextileStoreIdOrderByLastModifiedAsc(
									vo.getFromDate(), vo.getToDate(), vo.getBarcodeTextileId(), vo.getStoreId());
				} else {
					log.error("No record found with given barcodeTextileId");
					throw new RecordNotFoundException("No record found with given barcodeTextileId");
				}

			} else {
				throw new RecordNotFoundException("No record found with storeId:" + barStore);
			}
		}
		/*
		 * using dates with barcode and storeId
		 */

		else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getBarcode() != null
				&& vo.getStoreId() != null) {
			List<BarcodeTextile> barStore = barcodeTextileRepo.findAllByProductTextileStoreId(vo.getStoreId());
			if (barStore != null) {
				BarcodeTextile barOpt = barcodeTextileRepo.findByBarcode(vo.getBarcode());
				if (barOpt != null) {
					barcodeDetails = barcodeTextileRepo
							.findByCreationDateBetweenAndBarcodeAndProductTextileStoreIdOrderByLastModifiedAsc(
									vo.getFromDate(), vo.getToDate(), vo.getBarcode(), vo.getStoreId());
				} else {
					log.error("No record found with given barcode");
					throw new RecordNotFoundException("No record found with given barcode");
				}
			} else {
				throw new RecordNotFoundException("No record found with storeId:" + barStore);
			}

		}
		/*
		 * using itemMrp< and itemMrp>
		 */
		else if (vo.getItemMrpLessThan() != 0 && vo.getItemMrpGreaterThan() != 0) {
			List<ProductTextile> prodOpt = productTextileRepo.findByItemMrpBetween(vo.getItemMrpLessThan(),
					vo.getItemMrpGreaterThan());
			List<Long> bars = prodOpt.stream().map(s -> s.getBarcodeTextile().getBarcodeTextileId())
					.collect(Collectors.toList());

			barcodeDetails = barcodeTextileRepo.findByBarcodeTextileIdIn(bars);

			if (barcodeDetails.isEmpty()) {
				log.error("No record found with given information");
				throw new RecordNotFoundException("No record found with given information");
			}
		}
		/*
		 * using empId
		 */
		else if (vo.getEmpId() != null) {

			Optional<ProductTextile> prodOpt = productTextileRepo.findByEmpId(vo.getEmpId());
			if (prodOpt.isPresent()) {
				barcodeDetails = barcodeTextileRepo.findByProductTextileEmpId(prodOpt.get().getEmpId());
			} else {
				log.error("No record found with given empId");
				throw new RecordNotFoundException("No record found with given empId");
			}

		}
		/*
		 * using barcode and storeId
		 */
		else if (vo.getFromDate() == null && vo.getToDate() == null && (!vo.getBarcode().isEmpty())
				&& vo.getStoreId() != null) {
			List<BarcodeTextile> barStore = barcodeTextileRepo.findAllByProductTextileStoreId(vo.getStoreId());
			if (barStore != null) {
				BarcodeTextile textile = barcodeTextileRepo.findByBarcode(vo.getBarcode());
				if (textile == null) {
					log.error("textile record is not found:" + vo.getBarcode());
					throw new RecordNotFoundException("textile record is not found:" + vo.getBarcode());
				}

				barcodeDetails.add(textile);
				List<BarcodeTextileVo> barcodeList = barcodeTextileMapper.EntityToVo(barcodeDetails);
				barcodeList.stream().forEach(v -> {
					ProductTransaction transact = productTransactionRepo.findByBarcodeId(v.getBarcodeTextileId());
					v.getProductTextile().setQty(transact.getQuantity());
					v.getProductTextile().setValue(transact.getQuantity() * v.getProductTextile().getCostPrice());
				});
				return barcodeList;
			} else {
				log.error("No record found with storeId:" + barStore);
				throw new RecordNotFoundException("No record found with storeId:" + barStore);
			}
		}

		else if (vo.getStoreId() != null) {
			ProductStatus status = ProductStatus.ENABLE;
			List<ProductTextile> prodOpt = productTextileRepo.findAllByStoreId(vo.getStoreId());
			if (prodOpt != null) {
				barcodeDetails = barcodeTextileRepo.findByProductTextileStoreIdAndProductTextileStatus(vo.getStoreId(),
						status);
			} else {
				log.error("No record found with given information");
				throw new RecordNotFoundException("No record found with given information");
			}
		}

		/*
		 * values with empty string
		 */
		else if ((vo.getFromDate() == null) && (vo.getToDate() == null) && (vo.getBarcode() == "")
				&& vo.getStoreId() == null) {
			ProductStatus status = ProductStatus.ENABLE;

			List<BarcodeTextile> barcodeTextileList = barcodeTextileRepo.findByProductTextileStatus(status);

			List<BarcodeTextileVo> barcodeList = barcodeTextileMapper.EntityToVo(barcodeTextileList);
			barcodeList.stream().forEach(v -> {
				ProductTransaction transact = productTransactionRepo.findByBarcodeId(v.getBarcodeTextileId());
				v.getProductTextile().setQty(transact.getQuantity());
				v.getProductTextile().setValue(transact.getQuantity() * v.getProductTextile().getCostPrice());
			});
			return barcodeList;
		}

		else if ((vo.getFromDate() == null) && (vo.getToDate() == null) && (vo.getBarcode() != null)
				&& vo.getStoreId() == null && vo.getItemMrpGreaterThan() == 0 && vo.getItemMrpLessThan() == 0
				&& vo.getEmpId() == null) {
			BarcodeTextile textile = barcodeTextileRepo.findByBarcode(vo.getBarcode());
			if (textile == null) {
				log.error("textile record is not found:" + vo.getBarcode());
				throw new RecordNotFoundException("textile record is not found:" + vo.getBarcode());
			}

			barcodeDetails.add(textile);
			List<BarcodeTextileVo> barcodeList = barcodeTextileMapper.EntityToVo(barcodeDetails);
			return barcodeList;
		}

		List<BarcodeTextileVo> barcodeList = barcodeTextileMapper.EntityToVo(barcodeDetails);
		barcodeList.stream().forEach(v -> {

			ProductTransaction transact = productTransactionRepo.findByBarcodeId(v.getBarcodeTextileId());
			v.getProductTextile().setQty(transact.getQuantity());

			v.getProductTextile().setValue(transact.getQuantity() * v.getProductTextile().getCostPrice());
		});

		log.warn("we are checking if barcode textile is fetching...");
		log.info("fetching all barcode textile details");
		return barcodeList;
	}

	@Override
	public void inventoryUpdate(List<InventoryUpdateVo> request) {

		request.stream().forEach(x -> {
			// for textile update
			if (x.getDomainId() == 1) {
				BarcodeTextile barcodeDetails = barcodeTextileRepo.findByBarcode(x.getBarCode());
				if (barcodeDetails == null) {
					log.error("record not found with barcode:" + x.getBarCode());
					throw new RecordNotFoundException("record not found with barcode:" + x.getBarCode());
				}
				ProductTransaction transact = productTransactionRepo
						.findByBarcodeId(barcodeDetails.getBarcodeTextileId());
				transact.setMasterFlag(false);
				transact.setQuantity(Math.abs(x.getQuantity() - transact.getQuantity()));
				productTransactionRepo.save(transact);
				ProductTransaction prodTrans = new ProductTransaction();
				prodTrans.setBarcodeId(barcodeDetails.getBarcodeTextileId());
				prodTrans.setEffectingTableId(x.getLineItemId());
				prodTrans.setQuantity(x.getQuantity());
				prodTrans.setStoreId(x.getStoreId());
				prodTrans.setNatureOfTransaction(NatureOfTransaction.SALE.getName());
				prodTrans.setCreationDate(LocalDate.now());
				prodTrans.setLastModified(LocalDate.now());
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
	public List<AdjustmentsVo> getAllAdjustments(AdjustmentsVo vo) {
		log.info("Received request to getAllAdjustments:" + vo);
		List<Adjustments> adjustmentDetails = new ArrayList<>();

		List<ProductTransaction> transact = productTransactionRepo.findAllByStoreId(vo.getStoreId());

		/*
		 * using dates
		 */
		if (vo.getFromDate() != null && vo.getToDate() != null && (vo.getCurrentBarcodeId() == "")
				&& vo.getStoreId() != null) {
			if (vo.getStoreId() != null) {
				adjustmentDetails = adjustmentRepo.findByCreationDateBetweenOrderByLastModifiedDateAsc(vo.getFromDate(),
						vo.getToDate());

				if (adjustmentDetails.isEmpty()) {
					log.error("No record found with given information");
					throw new RecordNotFoundException("No record found with given information");
				}
			} else {
				log.error("No record found with storeId:" + vo.getStoreId());
				throw new RecordNotFoundException("No record found with storeId");
			}
		}

		/*
		 * using dates and currentBarcodeId and storeId
		 */
		else if (vo.getFromDate() != null && vo.getToDate() != null && vo.getCurrentBarcodeId() != null
				&& vo.getStoreId() != null) {
			Adjustments adjustOpt = adjustmentRepo.findByCurrentBarcodeId(vo.getCurrentBarcodeId());
			if (adjustOpt != null) {
				adjustmentDetails = adjustmentRepo
						.findByCreationDateBetweenAndCurrentBarcodeIdOrderByLastModifiedDateAsc(vo.getFromDate(),
								vo.getToDate(), vo.getCurrentBarcodeId());
			} else {
				log.error("No record found with given currentbarcodeId:" + vo.getCurrentBarcodeId());
				throw new RecordNotFoundException(
						"No record found with given currentbarcodeId:" + vo.getCurrentBarcodeId());
			}
		}

		/*
		 * values with empty string
		 */
		else if (vo.getFromDate() == null && vo.getToDate() == null && vo.getCurrentBarcodeId() == ""
				&& vo.getStoreId() == null) {
			List<Adjustments> adjustDetails1 = adjustmentRepo.findAll();
			List<AdjustmentsVo> adjustDetails = adjustmentMapper.EntityToVo(adjustDetails1);
			return adjustDetails;
		}
		/*
		 * using storeId
		 */
		else if (vo.getFromDate() == null && vo.getToDate() == null && vo.getCurrentBarcodeId() == ""
				&& vo.getStoreId() != null) {
			if (vo.getStoreId() != null) {

				List<Long> effectingId = transact.stream().map(e -> e.getEffectingTableId())
						.collect(Collectors.toList());
				List<Adjustments> adjustmentDetails1 = adjustmentRepo.findByAdjustmentIdIn(effectingId);
				List<AdjustmentsVo> adjustDetailsVo = adjustmentMapper.EntityToVo(adjustmentDetails1);
				adjustDetailsVo.stream().forEach(a -> {
					transact.stream().forEach(t -> {
						a.setStoreId(t.getStoreId());
					});

				});
				return adjustDetailsVo;
			} else {
				log.error("No record found with storeId:" + vo.getStoreId());
				throw new RecordNotFoundException("No record found with storeId:" + vo.getStoreId());
			}
		}

		/*
		 * using currentBarcodeId and storeId
		 */
		else if (vo.getFromDate() == null && vo.getToDate() == null && vo.getCurrentBarcodeId() != null
				&& vo.getStoreId() != null)

		{
			List<ProductTransaction> transac = productTransactionRepo.findAllByStoreId(vo.getStoreId());
			List<Long> effectingId = transac.stream().map(e -> e.getEffectingTableId()).collect(Collectors.toList());
			List<Adjustments> adjustmentDetails1 = adjustmentRepo
					.findByCurrentBarcodeIdAndAdjustmentIdIn(vo.getCurrentBarcodeId(), effectingId);
			List<AdjustmentsVo> adjustList = adjustmentMapper.EntityToVo(adjustmentDetails1);
			adjustList.stream().forEach(a -> {
				transac.stream().forEach(t -> {
					a.setStoreId(t.getStoreId());
				});

			});
			return adjustList;
		} else {
			List<Adjustments> adjustDetails1 = adjustmentRepo.findAll();
			List<AdjustmentsVo> adjustDetails = adjustmentMapper.EntityToVo(adjustDetails1);
			adjustDetails.stream().forEach(a -> {
				transact.stream().forEach(t -> {
					a.setStoreId(t.getStoreId());
				});

			});
			return adjustDetails;
		}

		List<AdjustmentsVo> adjustmentList = adjustmentMapper.EntityToVo(adjustmentDetails);
		adjustmentList.stream().forEach(a -> {
			transact.stream().forEach(t -> {
				a.setStoreId(t.getStoreId());
			});

		});
		log.warn("we are checking if barcode textile is fetching...");
		log.info("fetching all barcode textile details");
		return adjustmentList;

	}

	@Override
	public String saveProductTextileList(List<BarcodeTextileVo> barcodeTextileVos) {
		barcodeTextileVos.stream().forEach(v -> {
			addBarcodeTextile(v);
		});
		log.info("after saving all product textiles...");
		return "saving list of product textile details...";
	}

	@Override
	public List<String> getValuesFromProductTextileColumns(String enumName) {
		List<String> prod = productTextileRepoImpl.getUniqueColumn(enumName);
		log.info("after fetching ValuesFromProductTextileColumns:" + prod);
		return prod;
	}

	@Override
	public List<String> getValuesFromBarcodeTextileColumns(String enumName) {
		List<String> bar = barcodeTextileRepoImpl.getUniqueColumn(enumName);
		log.info("after fetching ValuesFromBarcodeTextileColumns:" + bar);
		return bar;
	}

	@Override
	public List<String> getAllColumns() {
		List<String> columns = barcodeTextileRepo.findAllColumnNames();
		log.info("after fetching all columns:" + columns);
		return columns;
	}

}
