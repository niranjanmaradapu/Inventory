/**
 * 
 */
package com.otsi.retail.inventory.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vasavi
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "product_bundle")
public class ProductBundle extends BaseEntity {
	
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String description;

	private Boolean status;

	private Long domainId;

	private Long storeId;

	private Integer bundleQuantity;

	@ManyToMany
	@JoinTable(name = "product_bundle_assignment_textile", joinColumns = @JoinColumn(name = "product_bundle_id"), inverseJoinColumns = @JoinColumn(name = "assigned_product_id"))
	private List<Product> productTextiles;

}
