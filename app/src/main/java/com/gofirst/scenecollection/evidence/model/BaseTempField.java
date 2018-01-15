package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/6/30.
 */
@Table(name="BaseTempField")
public class BaseTempField {
    @Id(column="id")
    private String id;
    private String sceneName;
    private String viewName;
    private String viewFormat;
    private String hostYear;
    private String viewid;
    private String remark;
    private String maxVerCode;
    private String sceneType;
    private String viewLineNumber;
    private String viewMaxLength;
    private String viewMinor;
    private String dcitParentCode;
    private String hostId;
    private String viewType;
    private String dictType;
    private String minVerCode;
    private String field;
    private String dataType;

    private String name;

    private String viewRequiredFlag;
    private String deleteFlag;
    private String defaultValue;
    private String templateId;
    private String positionSort;
    private String viewShowMode;
    private int viewPositionSort;



    public String getViewMinor() {
        return viewMinor;
    }

    public void setViewMinor(String viewMinor) {
        this.viewMinor = viewMinor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewFormat() {
        return viewFormat;
    }

    public void setViewFormat(String viewFormat) {
        this.viewFormat = viewFormat;
    }

    public String getHostYear() {
        return hostYear;
    }

    public void setHostYear(String hostYear) {
        this.hostYear = hostYear;
    }

    public String getViewid() {
        return viewid;
    }

    public void setViewid(String viewid) {
        this.viewid = viewid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMaxVerCode() {
        return maxVerCode;
    }

    public void setMaxVerCode(String maxVerCode) {
        this.maxVerCode = maxVerCode;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getViewLineNumber() {
        return viewLineNumber;
    }

    public void setViewLineNumber(String viewLineNumber) {
        this.viewLineNumber = viewLineNumber;
    }

    public String getViewMaxLength() {
        return viewMaxLength;
    }

    public void setViewMaxLength(String viewMaxLength) {
        this.viewMaxLength = viewMaxLength;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getMinVerCode() {
        return minVerCode;
    }

    public void setMinVerCode(String minVerCode) {
        this.minVerCode = minVerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewRequiredFlag() {
        return viewRequiredFlag;
    }

    public void setViewRequiredFlag(String viewRequiredFlag) {
        this.viewRequiredFlag = viewRequiredFlag;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public String getDcitParentCode() {
        return dcitParentCode;
    }

    public void setDcitParentCode(String dcitParentCode) {
        this.dcitParentCode = dcitParentCode;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPositionSort() {
        return positionSort;
    }

    public void setPositionSort(String positionSort) {
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
