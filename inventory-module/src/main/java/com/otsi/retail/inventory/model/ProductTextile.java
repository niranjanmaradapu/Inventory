package com.otsi.retail.inventory.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.otsi.retail.inventory.commons.ProductEnum;
import com.otsi.retail.inventory.commons.ProductStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_textile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTextile extends BaseEntity {
	
	@Id
	@GeneratedValue
	private Long productTextileId;
	private String name;
	private String barcode;
	private Long division;
	private Long section;
	private Long subSection;
	private Long category;
	private String batchNo;
	private String colour;
	private String parentBarcode;
	private float costPrice;
	private float itemMrp;
	private Long empId;
	private Long storeId;
	private Long domainId;
	@ApiModelProperty(notes = "unit of measures of the product")
	private String uom;
	private String hsnCode;
	@Enumerated(EnumType.STRING)
	private ProductEnum sellingTypeCode;
	private LocalDate originalBarcodeCreatedAt;
	private ProductStatus status;
	private int quantity;

}
