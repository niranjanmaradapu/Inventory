/**
 * 
 */
package com.otsi.retail.inventory.vo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

/**
 * @author vasavi
 *
 */
@Data
@MappedSuperclass
public class BaseEntityVo {
	
	@Column(name = "id")
	private Long id;

	@Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

}
