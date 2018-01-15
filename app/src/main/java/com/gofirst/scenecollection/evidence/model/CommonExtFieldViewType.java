package com.gofirst.scenecollection.evidence.model;
/**
    * COMMON_EXT_FIELD_VIEW_TYPE 实体类
    * Wed May 18 09:05:40 CST 2016 taofa
    */ 


public class CommonExtFieldViewType{
	private String viewid;
	private String viewname;
	private String remark;
	private String deleteFlag;
//	private String createUser;
//	private Date createDatetime;
//	private String updateUser;
//	private Date updateDatetime;
	private String hostId;
	private String hostYear;
	
	public String getViewid() {
		return viewid;
	}
	public void setViewid(String viewid) {
		this.viewid = viewid;
	}
	public String getViewname() {
		return viewname;
	}
	public void setViewname(String viewname) {
		this.viewname = viewname;
	}
	public void setRemark(String remark){
	this.remark=remark;
	}
	public String getRemark(){
		return remark;
	}
	public void setDeleteFlag(String deleteFlag){
	this.deleteFlag=deleteFlag;
	}
	public String getDeleteFlag(){
		return deleteFlag;
	}
//	public void setCreateUser(String createUser){
//	this.createUser=createUser;
//	}
//	public String getCreateUser(){
//		return createUser;
//	}
//	public void setCreateDatetime(Date createDatetime){
//	this.createDatetime=createDatetime;
//	}
//	public Date getCreateDatetime(){
//		return createDatetime;
//	}
//	public void setUpdateUser(String updateUser){
//	this.updateUser=updateUser;
//	}
//	public String getUpdateUser(){
//		return updateUser;
//	}
//	public void setUpdateDatetime(Date updateDatetime){
//	this.updateDatetime=updateDatetime;
//	}
//	public Date getUpdateDatetime(){
//		return updateDatetime;
//	}
	public void setHostId(String hostId){
	this.hostId=hostId;
	}
	public String getHostId(){
		return hostId;
	}
	public void setHostYear(String hostYear){
	this.hostYear=hostYear;
	}
	public String getHostYear(){
		return hostYear;
	}
}

