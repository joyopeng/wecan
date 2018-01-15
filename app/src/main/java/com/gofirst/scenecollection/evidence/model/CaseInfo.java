package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/7.
 */
@Table(name = "CaseInfo")
public class CaseInfo {
    @Id(column = "id")
    private String id;
    /** 案发时间*/
    private String caseHappenTime;
    /** 结束时间*/
    private String caseEndTime;
    /** true为刑案*/
    private String isNormal;
    /** true为命案*/
    private String isHomicide;
    /** 翻动程度*/
    private String flipLevel;
    /**案件id*/
    private String caseId;
    private String sceneType;
    private String json;

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


    public String getCaseEndTime() {
        return caseEndTime;
    }

    public void setCaseEndTime(String caseEndTime) {
        this.caseEndTime = caseEndTime;
    }

    public String getCaseHappenTime() {
        return caseHappenTime;
    }

    public void setCaseHappenTime(String caseHappenTime) {
        this.caseHappenTime = caseHappenTime;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getFlipLevel() {
        return flipLevel;
    }

    public void setFlipLevel(String flipLevel) {
        this.flipLevel = flipLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsHomicide() {
        return isHomicide;
    }

    public void setIsHomicide(String isHomicide) {
        this.isHomicide = isHomicide;
    }

    public String getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(String isNormal) {
        this.isNormal = isNormal;
    }
}
