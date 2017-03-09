package com.ebao.pojo;

/**
 * key store node-key name
 * isCore is for 1st file
 * isDc is for 2nd file
 * @author br
 *
 */
public class KeyResult {

	private String key;
	private boolean isCore;
	private boolean isDc;

	public KeyResult() {
		super();
	}

	public KeyResult(String key, boolean isCore, boolean isDc) {
		super();
		this.key = key;
		this.isCore = isCore;
		this.isDc = isDc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isCore() {
		return isCore;
	}

	public void setCore(boolean isCore) {
		this.isCore = isCore;
	}

	public boolean isDc() {
		return isDc;
	}

	public void setDc(boolean isDc) {
		this.isDc = isDc;
	}

}
