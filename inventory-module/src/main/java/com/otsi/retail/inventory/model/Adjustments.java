package com.otsi.retail.inventory.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

}
