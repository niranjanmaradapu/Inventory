package com.otsi.retail.inventory.commons;

public enum ProductEnum {

	None(0L, "none"), INDIVIDUALPRODUCT(1L, "individual product"), PRODUCTBUNDLE(2L, "product bundle");

	private Long id;
	private String name;

	/**
	 * @param id
	 * @param name
	 */
	private ProductEnum(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
