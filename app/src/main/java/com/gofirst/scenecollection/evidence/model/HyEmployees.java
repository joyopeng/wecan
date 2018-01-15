package com.gofirst.scenecollection.evidence.model;


import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
@Table(name="HyEmployees")
public class HyEmployees{
		private int id;
	
		/**
		 * 主键
		 * 人员ID
		 **/
		@Id(column = "employeeId")
		private Integer employeeId;
		/**
		 * 警号，唯一，除管理员外，一般用警号
		 **/
		private String employeeNo;
		/**
		 * 人员姓名
		 **/
		private String employeeName;
		/**
		 * 性别：男1/女2
		 **/
		private String employeeSex;
		/**
		 * 出生日期
		 **/
		private String employeeBirth;
		/**
		 * 联系电话
		 **/
		private String employeeTel;
		/**
		 * 邮件地址
		 **/
		private String employeeEmail;
		/**
		 * 证件名称
		 **/
		private String employeeCredname;
		/**
		 * 证件号码
		 **/
		private String employeeCredno;
		/**
		 * 电脑IP
		 **/
		private Integer employeePcIp;
		/**
		 * 排序
		 **/
		private Integer employeeOrderby;
		/**
		 * 部门ID
		 **/
		private Integer orgDeptId;
		/**
		 * 机构ID
		 **/
		private Integer organizationId;
		/**
		 * 备注
		 **/
		private String remark;
		/**
		 * 在职状态
		 **/
		private String employeeStatus;

	/*
	* 刪除標記*/

		private String deleteFlag;
	
		public HyEmployees(){}

		public int getId() {
			return id;
		}

		public void setId(int id) {
		this.id = id;
	}

		public Integer getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(Integer employeeId) {
			this.employeeId = employeeId;
		}

		public String getEmployeeNo() {
			return employeeNo;
		}

		public void setEmployeeNo(String employeeNo) {
			this.employeeNo = employeeNo;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		public String getEmployeeSex() {
			return employeeSex;
		}

		public void setEmployeeSex(String employeeSex) {
			this.employeeSex = employeeSex;
		}

		public String getEmployeeBirth() {
			return employeeBirth;
		}

		public void setEmployeeBirth(String employeeBirth) {
			this.employeeBirth = employeeBirth;
		}

		public String getEmployeeTel() {
			return employeeTel;
		}

		public void setEmployeeTel(String employeeTel) {
			this.employeeTel = employeeTel;
		}

		public String getEmployeeEmail() {
			return employeeEmail;
		}

		public void setEmployeeEmail(String employeeEmail) {
			this.employeeEmail = employeeEmail;
		}

		public String getEmployeeCredname() {
			return employeeCredname;
		}

		public void setEmployeeCredname(String employeeCredname) {
			this.employeeCredname = employeeCredname;
		}

		public String getEmployeeCredno() {
			return employeeCredno;
		}

		public void setEmployeeCredno(String employeeCredno) {
			this.employeeCredno = employeeCredno;
		}

		public Integer getEmployeePcIp() {
			return employeePcIp;
		}

		public void setEmployeePcIp(Integer employeePcIp) {
			this.employeePcIp = employeePcIp;
		}

		public Integer getEmployeeOrderby() {
			return employeeOrderby;
		}

		public void setEmployeeOrderby(Integer employeeOrderby) {
			this.employeeOrderby = employeeOrderby;
		}

		public Integer getOrgDeptId() {
			return orgDeptId;
		}

		public void setOrgDeptId(Integer orgDeptId) {
			this.orgDeptId = orgDeptId;
		}

		public Integer getOrganizationId() {
			return organizationId;
		}

		public void setOrganizationId(Integer organizationId) {
			this.organizationId = organizationId;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getEmployeeStatus() {
			return employeeStatus;
		}

		public void setEmployeeStatus(String employeeStatus) {
			this.employeeStatus = employeeStatus;
		}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}