package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/12.
 */
@Table(name="WorkSuggestionInfo")
public class WorkSuggestionInfo {
    int id;

/*    *//*侦查方向*//*
    private String investigationDirection;
    *//*侦查方向录音路径*//*
    private String investigationDirectionPath;

    *//*现场保留*//*
    private String sceneDispose;
    *//*现场保留录音路径*//*
    private String sceneDisposePath;

    *//*物证保管*//*
    private String evidenceSave;
    *//*物证保管录音路径*//*
    private String evidenceSavePath;

    *//*技术防范*//*
    private String technicalProtection;
    *//*技术防范录音路径*//*
    private String technicalProtectionPath;

    *//*其他*//*
    private String other;
    *//*其他录音路径*//*
    private String otherPath;*/

    private String caseId;
    private String sceneType;
    private String dataType;
    private String data;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
