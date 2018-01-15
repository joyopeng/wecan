package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;


@Table(name="BaseTemp")
public class BaseTemp {
    //private int id;
    private String id;
    private String templateId;
    private String sceneName;
    private String flag;
    private String tableName;
    private String tableField;
    private String templateType;
    private String templateLevel;	//模板等级 0:根模板 1:一级模板 2:二级模板
    private String templateUpName;	//上级表名 仅模板等级为2时有效
    private String positionSort;
    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableField() {
        return tableField;
    }

    public void setTableField(String tableField) {
        this.tableField = tableField;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getTemplateLevel() {
        return templateLevel;
    }

    public void setTemplateLevel(String templateLevel) {
        this.templateLevel = templateLevel;
    }

    public String getTemplateUpName() {
        return templateUpName;
    }

    public void setTemplateUpName(String templateUpName) {
        this.templateUpName = templateUpName;
    }

    public String getPositionSort() {
        return positionSort;
    }

    public void setPositionSort(String positionSort) {
        this.positionSort = positionSort;
    }
}
