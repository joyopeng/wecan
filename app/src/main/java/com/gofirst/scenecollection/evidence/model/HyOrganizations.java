package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name="HyOrganizations")
public class HyOrganizations  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7210248768007789556L;
	
		private int id;
	/**
		 * 主键
		 * 机构ID
		 **/
		@Id(column = "organizationId")
		private Integer organizationId;
		/**
		 * 机构代号
		 **/
		private String organizationNo;
		/**
		 * 机构标识
		 **/
		private String organizationLogo;
		/**
		 * 机构简称
		 **/
		private String organizationName;
		/**
		 * 机构全称
		 **/
		private String organizationCname;
		/**
		 * 上级行政机构ID，无上级为0
		 **/
		private Integer  organizationUpId;
		/**
		 * 上级业务机构ID，无上级为0
		 **/
		private Integer  organizationBusiUpId;
		/**
		 * 所在区划，见区划表
HY_COMPARTMENTS

		 **/
		private String compartmentNo;
		/**
		 * 机构电话
		 **/
		private String organizationTel;
		/**
		 * 机构值班电话
		 **/
		private String organizationTelWatch;
		/**
		 * 机构传真
		 **/
		private String organizationFax;
		/**
		 * 机构地址
		 **/
		private String organizationAddr;
		/**
		 * 机构邮编
		 **/
		private String organizationZip;
		/**
		 * 机构URL
		 **/
		private String organizationUrl;
		/**
		 * 排序
		 **/
		private Long organizationOrderby;
		/**
		 * 机构性质：非空,0系统机构1外部机构
		 **/
		private String organizationType;
		/**
		 * 
		 **/
		private String remark;


		public int getId() {
			return id;
		}

		public void setId(int id) {
		this.id = id;
	}
	
		public HyOrganizations(){}

		public Integer  getOrganizationId() {
			return organizationId;
		}

		public void setOrganizationId(Integer  organizationId) {
			this.organizationId = organizationId;
		}

		public String getOrganizationNo() {
			return organizationNo;
		}

		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}

		public String getOrganizationLogo() {
			return organizationLogo;
		}

		public void setOrganizationLogo(String organizationLogo) {
			this.organizationLogo = organizationLogo;
		}

		public String getOrganizationName() {
			return organizationName;
		}

		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}

		public String getOrganizationCname() {
			return organizationCname;
		}

		public void setOrganizationCname(String organizationCname) {
			this.organizationCname = organizationCname;
		}

		public Integer  getOrganizationUpId() {
			return organizationUpId;
		}

		public void setOrganizationUpId(Integer  organizationUpId) {
			this.organizationUpId = organizationUpId;
		}

		public Integer  getOrganizationBusiUpId() {
			return organizationBusiUpId;
		}

		public void setOrganizationBusiUpId(Integer  organizationBusiUpId) {
			this.organizationBusiUpId = organizationBusiUpId;
		}

		public String getCompartmentNo() {
			return compartmentNo;
		}

		public void setCompartmentNo(String compartmentNo) {
			this.compartmentNo = compartmentNo;
		}

		public String getOrganizationTel() {
			return organizationTel;
		}

		public void setOrganizationTel(String organizationTel) {
			this.organizationTel = organizationTel;
		}

		public String getOrganizationTelWatch() {
			return organizationTelWatch;
		}

		public void setOrganizationTelWatch(String organizationTelWatch) {
			this.organizationTelWatch = organizationTelWatch;
		}

		public String getOrganizationFax() {
			return organizationFax;
		}

		public void setOrganizationFax(String organizationFax) {
			this.organizationFax = organizationFax;
		}

		public String getOrganizationAddr() {
			return organizationAddr;
		}

		public void setOrganizationAddr(String organizationAddr) {
			this.organizationAddr = organizationAddr;
		}

		public String getOrganizationZip() {
			return organizationZip;
		}

		public void setOrganizationZip(String organizationZip) {
			this.organizationZip = organizationZip;
		}

		public String getOrganizationUrl() {
			return organizationUrl;
		}

		public void setOrganizationUrl(String organizationUrl) {
			this.organizationUrl = organizationUrl;
		}

		public Long getOrganizationOrderby() {
			return organizationOrderby;
		}

		public void setOrganizationOrderby(Long organizationOrderby) {
			this.organizationOrderby = organizationOrderby;
		}

		public String getOrganizationType() {
			return organizationType;
		}

		public void setOrganizationType(String organizationType) {
			this.organizationType = organizationType;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
	
}