package com.gofirst.scenecollection.evidence.model;

//import net.tsz.afinal.annotation.sqlite.Id;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
    * COMMON_TEMPLATE 实体类
    * Fri May 27 15:54:06 CST 2016 taofa
    */ 
@Table(name="CommonTemplate")
public class CommonTemplate  {
	private String id;
		@Id(column = "sid")
	    private String sid;
		private String name;
		private String key;
		private String remark;
		private String shareFlag;
		private String orgId;
		private String caseTypeCode;
		private String content;
		private String deleteFlag;
		private String createUser;
		private String createDatetime;
		private String updateUser;
		private String updateDatetime;
		private String hostId;
		private String hostYear;
		private Long minVerCode;
		private Long maxVerCode;
		private String dealType;
//	    private String field;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSid(String sid){
	this.sid=sid;
	}
	public String getSid(){
		return sid;
	}
	public void setName(String name){
	this.name=name;
	}
	public String getName(){
		return name;
	}
	public void setKey(String key){
	this.key=key;
	}
	public String getKey(){
		return key;
	}
	public void setRemark(String remark){
	this.remark=remark;
	}
	public String getRemark(){
		return remark;
	}
	public void setShareFlag(String shareFlag){
	this.shareFlag=shareFlag;
	}
	public String getShareFlag(){
		return shareFlag;
	}
	public void setOrgId(String orgId){
	this.orgId=orgId;
	}
	public String getOrgId(){
		return orgId;
	}
	public void setCaseTypeCode(String caseTypeCode){
	this.caseTypeCode=caseTypeCode;
	}
	public String getCaseTypeCode(){
		return caseTypeCode;
	}
	public void setContent(String content){
	this.content=content;
	}
	public String getContent(){
		return content;
	}
	public void setDeleteFlag(String deleteFlag){
	this.deleteFlag=deleteFlag;
	}
	public String getDeleteFlag(){
		return deleteFlag;
	}
	public void setCreateUser(String createUser){
	this.createUser=createUser;
	}
	public String getCreateUser(){
		return createUser;
	}
	public void setCreateDatetime(String createDatetime){
	this.createDatetime=createDatetime;
	}
	public String getCreateDatetime(){
		return createDatetime;
	}
	public void setUpdateUser(String updateUser){
	this.updateUser=updateUser;
	}
	public String getUpdateUser(){
		return updateUser;
	}
	public void setUpdateDatetime(String updateDatetime){
	this.updateDatetime=updateDatetime;
	}
	public String getUpdateDatetime(){
		return updateDatetime;
	}
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
	public void setMinVerCode(Long minVerCode){
	this.minVerCode=minVerCode;
	}
	public Long getMinVerCode(){
		return minVerCode;
	}
	public void setMaxVerCode(Long maxVerCode){
	this.maxVerCode=maxVerCode;
	}
	public Long getMaxVerCode(){
		return maxVerCode;
	}
	public void setDealType(String dealType){
	this.dealType=dealType;
	}
	public String getDealType(){
		return dealType;
	}

   /* public void setField(String field){
	   this.field=field;
   }
    public String getField(){
		   return field;
	   }*/
}

