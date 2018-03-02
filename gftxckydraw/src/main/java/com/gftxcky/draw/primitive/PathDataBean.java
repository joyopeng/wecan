package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class PathDataBean implements Serializable {

	private static final long serialVersionUID = -6237702536791798147L;
	private String xystr;
	private String indexstr;
	private String name;
	public String getXYStr() {
		return xystr;
	}
	public void setXYStr(String xystr) {
		this.xystr = xystr;
	}
	public String getIndexStr() {
		return indexstr;
	}
	public void setIndexStr(String indexstr) {
		this.indexstr = indexstr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
}
