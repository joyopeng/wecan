package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

import java.util.Date;

/**
    * COMMON_EXT_FIELD 实体类
    * Wed May 18 09:05:40 CST 2016 taofa
    */ 
@Table(name = "CommonExtField")
public class CommonExtField {
	private String id;
	private String name;
	private String viewName;
	private String sceneType;
	private String sceneName;
	private String field;
	private String dataType;
	private String dictType;
	private String dcitParentCode;
	private String defaultValue;
	private String viewType;
	private String viewRequiredFlag;
	private String viewFormat;
	private Long viewMaxLength;
	private Long viewLineNumber;
	private String deleteFlag;
	private String createUser;
	private Date createDatetime;
	private String updateUser;
	private Date updateDatetime;
	private String hostId;
	private String hostYear;
	private Long minVerCode;
	private Long maxVerCode;
    private String viewid;
	private String remark;
	private String templateId;
	private int positionSort;
	private String viewMinor;
	private String viewShowMode;
	private int viewPositionSort;

	public String getViewMinor() {
		return viewMinor;
	}

	public void setViewMinor(String viewMinor) {
		this.viewMinor = viewMinor;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getViewId() {
		return viewid;
	}

	public void setViewId(String viewId) {
		this.viewid = viewId;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public String getHostYear() {
		return hostYear;
	}

	public void setHostYear(String hostYear) {
		this.hostYear = hostYear;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getMaxVerCode() {
		return maxVerCode;
	}

	public void setMaxVerCode(Long maxVerCode) {
		this.maxVerCode = maxVerCode;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getDcitParentCode() {
		return dcitParentCode;
	}

	public void setDcitParentCode(String dcitParentCode) {
		this.dcitParentCode = dcitParentCode;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Long getMinVerCode() {
		return minVerCode;
	}

	public void setMinVerCode(Long minVerCode) {
		this.minVerCode = minVerCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getSceneType() {
		return sceneType;
	}

	public void setSceneType(String sceneType) {
		this.sceneType = sceneType;
	}

	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getViewFormat() {
		return viewFormat;
	}

	public void setViewFormat(String viewFormat) {
		this.viewFormat = viewFormat;
	}

	public Long getViewLineNumber() {
		return viewLineNumber;
	}

	public void setViewLineNumber(Long viewLineNumber) {
		this.viewLineNumber = viewLineNumber;
	}

	public Long getViewMaxLength() {
		return viewMaxLength;
	}

	public void setViewMaxLength(Long viewMaxLength) {
		this.viewMaxLength = viewMaxLength;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewRequiredFlag() {
		return viewRequiredFlag;
	}

	public void setViewRequiredFlag(String viewRequiredFlag) {
		this.viewRequiredFlag = viewRequiredFlag;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getViewid() {
		return viewid;
	}

	public void setViewid(String viewid) {
		this.viewid = viewid;
	}

	public int getPositionSort() {
		return positionSort;
	}

	public void setPositionSort(int positionSort) {
		this.positionSort = positionSort;
	}

	public String getViewShowMode() {
		return viewShowMode;
	}

	public void setViewShowMode(String viewShowMode) {
		this.viewShowMode = viewShowMode;
	}

	public int getViewPositionSort() {
		return viewPositionSort;
	}

	public void setViewPositionSort(int viewPositionSort) {
		this.viewPositionSort = viewPositionSort;
	}
}

