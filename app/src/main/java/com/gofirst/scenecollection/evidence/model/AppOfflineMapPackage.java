package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

import java.util.Date;



/**
 * APP_OFFLINE_MAP_PACKAGE 实体类 Wed May 18 09:05:40 CST 2016 taohong
 */
@Table(name="AppOfflineMapPackage")
public class AppOfflineMapPackage {
	private String id;
	private String areaId;
	private String areaName;// 地区名称
	private String mapSpell;//地区名称全拼
	private String fileSize;// 文件大小
	private String path;
	private String thirdUrl;
	private String deleteFlag;
	private String createUser;
	private Date createDatetime;
	private String updateUser;
	private Date updateDatetime;
	private String hostId;
	private String hostYear;
	private String versionCode;// 版本号

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaName() {
		return areaName;
	}

	public String getMapSpell() {
		return mapSpell;
	}

	public void setMapSpell(String mapSpell) {
		this.mapSpell = mapSpell;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setThirdUrl(String thirdUrl) {
		this.thirdUrl = thirdUrl;
	}

	public String getThirdUrl() {
		return thirdUrl;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostYear(String hostYear) {
		this.hostYear = hostYear;
	}

	public String getHostYear() {
		return hostYear;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
}
