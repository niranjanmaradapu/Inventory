package com.otsi.retail.inventory.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_bundle_assignment_textile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBundleAssignmentTextile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productBundleAssignmentTextileId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_bundle_id")
	private ProductBundle productBundleId;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_product_id")
	private ProductTextile assignedproductId;

	@Column(name = "quantity")
	private Integer quantity;

}
