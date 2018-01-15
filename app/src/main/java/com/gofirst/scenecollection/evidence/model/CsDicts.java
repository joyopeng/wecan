package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name="CsDicts")
public class CsDicts  {
		private String id;
		/**
		 * 字典级别
		 **/
		@Id(column = "sid")
		private String sid;
		private String dictLevel;
		/**
		 * 字典代码
		 **/
		private String dictKey;
		/**
		 * 字典父节点代码
		 **/
		private String parentKey;
		/**
		 * 字典根节点代码
		 **/
		private String rootKey;
		/**
		 * 字典值1
		 **/
		private String dictValue1;
		/**
		 * 字典值2
		 **/
		private String dictValue2;
		/**
		 * 字典值3
		 **/
		private String dictValue3;
		/**
		 * 叶子节点标志(0非叶节点，1叶子节点)
		 **/
		private String leafFlag;
		/**
		 * 下载标志(0非下载，1下载的字典)
		 **/
		private String downloadFlag;
		/**
		 * 只读标志(0非只读，1只读)
		 **/
		private String readonlyFlag;
		/**
		 * 显示顺序
		 **/
		private Integer dictSort;
		/**
		 * 字典PY输入的编码(提供拼音的字典输入方式用)
		 **/
		private String dictPy;
		/**
		 * 启用标志
		 **/
		private String openFlag;
		/**
		 * 备注
		 **/
		private String remark;



	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getId() {
			return id;
		}

		public void setId(String id) {
		this.id = id;
	}

		public String getDictLevel() {
			return dictLevel;
		}

		public void setDictLevel(String dictLevel) {
			this.dictLevel = dictLevel;
		}

		public String getDictKey() {
			return dictKey;
		}

		public void setDictKey(String dictKey) {
			this.dictKey = dictKey;
		}

		public String getParentKey() {
			return parentKey;
		}

		public void setParentKey(String parentKey) {
			this.parentKey = parentKey;
		}

		public String getRootKey() {
			return rootKey;
		}

		public void setRootKey(String rootKey) {
			this.rootKey = rootKey;
		}

		public String getDictValue1() {
			return dictValue1;
		}

		public void setDictValue1(String dictValue1) {
			this.dictValue1 = dictValue1;
		}

		public String getDictValue2() {
			return dictValue2;
		}

		public void setDictValue2(String dictValue2) {
			this.dictValue2 = dictValue2;
		}

		public String getDictValue3() {
			return dictValue3;
		}

		public void setDictValue3(String dictValue3) {
			this.dictValue3 = dictValue3;
		}

		public String getLeafFlag() {
			return leafFlag;
		}

		public void setLeafFlag(String leafFlag) {
			this.leafFlag = leafFlag;
		}

		public String getDownloadFlag() {
			return downloadFlag;
		}

		public void setDownloadFlag(String downloadFlag) {
			this.downloadFlag = downloadFlag;
		}

		public String getReadonlyFlag() {
			return readonlyFlag;
		}

		public void setReadonlyFlag(String readonlyFlag) {
			this.readonlyFlag = readonlyFlag;
		}

		public Integer getDictSort() {
			return dictSort;
		}

		public void setDictSort(Integer dictSort) {
			this.dictSort = dictSort;
		}

		public String getDictPy() {
			return dictPy;
		}

		public void setDictPy(String dictPy) {
			this.dictPy = dictPy;
		}

		public String getOpenFlag() {
			return openFlag;
		}

		public void setOpenFlag(String openFlag) {
			this.openFlag = openFlag;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
	
}