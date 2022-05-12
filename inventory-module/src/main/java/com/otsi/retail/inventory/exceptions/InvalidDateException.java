/**
 * 
 */
package com.otsi.retail.inventory.exceptions;

/**
 * @author vasavi
 *
 */
public class InvalidDateException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String msg;

	/**
	 * @param msg
	 */
	public InvalidDateException(String msg) {
		super();
		this.msg = msg;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
