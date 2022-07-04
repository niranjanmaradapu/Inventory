package com.otsi.retail.inventory.vo;

/**
 * @author vasavi
 *
 */
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.otsi.retail.inventory.commons.DomainType;
import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ProductVO {

	private Long id;
	@ApiModelProperty(value = "product name of the textile product", name = "name", required = true)

	private String name;
	@ApiModelProperty(value = "barcode of the textile product", name = "barcode", required = true)

	private String barcode;
	@ApiModelProperty(value = "division of the textile product", name = "division", required = true)

	private Long division;
	@ApiModelProperty(value = "section of the textile product", name = "section", required = true)

	private Long section;
	@ApiModelProperty(value = "subSection of the textile product", name = "subSection", required = true)

	private Long subSection;
	@ApiModelProperty(value = "category of the textile product", name = "category", required = true)

	private Long category;
	@ApiModelProperty(value = "batchNo of the textile product", name = "batchNo", required = true)

	private String batchNo;
	@ApiModelProperty(value = "colour of the textile product", name = "colour", required = true)

	private String colour;
	@ApiModelProperty(value = "existing barcode of the textile product", name = "parentBarcode")

	private String parentBarcode;
	@ApiModelProperty(value = "costPrice of the textile product", name = "costPrice", required = true)

	private float costPrice;
	@ApiModelProperty(value = "item price of the textile product", name = "itemMrp", required = true)

	private float itemMrp;
	@ApiModelProperty(value = "employee id of the textile product", name = "empId", required = true)

	private Long empId;
	@ApiModelProperty(value = "store id of the textile product", name = "storeId", required = true)

	private Long storeId;
	@ApiModelProperty(value = "domain id of the textile product", name = "domainId", required = true)

	private Long domainId;
	@ApiModelProperty(value = "created date of the textile product", name = "fromDate")
	private LocalDateTime fromDate;

	@ApiModelProperty(value = "last modified date of the textile product", name = "toDate")
	@DateTimeFormat(pattern = "dd-MM-yyyy ")
	private LocalDateTime toDate;

	@ApiModelProperty(value = "unit of measures of the textile product", name = "uom", required = true)
	private String uom;

	@ApiModelProperty(value = "quantity of the textile product", name = "quantity", required = true)
	private int qty;

	@ApiModelProperty(value = "hsncode of the textile product", name = "hsnCode", required = true)
	private String hsnCode;

	@ApiModelProperty(value = "original barcode created date of the textile product", name = "originalBarcodeCreatedAt")
	private LocalDate originalBarcodeCreatedAt;

	@ApiModelProperty(value = "sellingTypeCode means individual product/bundled product", name = "sellingTypeCode")
	private ProductEnum sellingTypeCode;

	@ApiModelProperty(value = "status of the textile product", name = "status")
	private ProductStatus status;

	@ApiModelProperty(value = "qty multiply itemMrp of the textile product", name = "value")
	private float value;

	@ApiModelProperty(value = "emp name  of the textile product", name = "empName")
	private String empName;

	private DomainType domainType;
	
	private Map taxValues;
	
	private List<DomainAttributesVO> metadata;
	
	private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
