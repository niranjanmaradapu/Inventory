package com.otsi.retail.inventory.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.otsi.retail.inventory.commons.AdjustmentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Adjustments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adjustments extends BaseEntity {

	@Id
	@GeneratedValue
	private Long adjustmentId;

	private String currentBarcodeId;

	private String toBeBarcodeId;

	private String comments;
	
	private Boolean status;
	
	private Long storeId;
	
	@Enumerated(EnumType.STRING)
	private AdjustmentType type;

}
