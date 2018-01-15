package com.gofirst.scenecollection.evidence.model;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/13.
 */
@Table(name = "InquestInfo")
public class InquestInfo {

    @Id(column = "id")
    private String id;
    private String caseId;
    private String INVESTIGATION_DATE_FROM;
    private String INVESTIGATION_DATE_TO;
    private String INVESTIGATION_PLACE;
    private String INVEST_NOTE_ID;
    private String DIRECTOR;
    private String REMNANT_DESC;
    private String SCENE_DISPOSAL;
    private String INVESTIGATOR;
    private String sceneType;
    private String INVESTIGATION_NO;
    private String basicInfo;

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    @JSONField(name = "INVESTIGATION_NO")
    public String getINVESTIGATION_NO() {
        return INVESTIGATION_NO;
    }

    public void setINVESTIGATION_NO(String INVESTIGATION_NO) {
        this.INVESTIGATION_NO = INVESTIGATION_NO;
    }
	
	@JSONField(name = "SCENE_TYPE")
    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
	
	@JSONField(name = "CASE_ID")
    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
    @JSONField(name = "DIRECTOR")
    public String getDIRECTOR() {
        return DIRECTOR;
    }

    public void setDIRECTOR(String DIRECTOR) {
        this.DIRECTOR = DIRECTOR;
    }
	
	 @JSONField(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @JSONField(name = "INVEST_NOTE_ID")
    public String getINVEST_NOTE_ID() {
        return INVEST_NOTE_ID;
    }

    public void setINVEST_NOTE_ID(String INVEST_NOTE_ID) {
        this.INVEST_NOTE_ID = INVEST_NOTE_ID;
    }
    @JSONField(name = "INVESTIGATION_DATE_TO")
    public String getINVESTIGATION_DATE_TO() {
        return INVESTIGATION_DATE_TO;
    }

    public void setINVESTIGATION_DATE_TO(String INVESTIGATION_DATE_TO) {
        this.INVESTIGATION_DATE_TO = INVESTIGATION_DATE_TO;
    }

    @JSONField(name = "INVESTIGATION_DATE_FROM")
    public String getINVESTIGATION_DATE_FROM() {
        return INVESTIGATION_DATE_FROM;
    }

    public void setINVESTIGATION_DATE_FROM(String INVESTIGATION_DATE_FROM) {
        this.INVESTIGATION_DATE_FROM = INVESTIGATION_DATE_FROM;
    }
    @JSONField(name = "INVESTIGATION_PLACE")
    public String getINVESTIGATION_PLACE() {
        return INVESTIGATION_PLACE;
    }

    public void setINVESTIGATION_PLACE(String INVESTIGATION_PLACE) {
        this.INVESTIGATION_PLACE = INVESTIGATION_PLACE;
    }
    @JSONField(name = "INVESTIGATOR")
    public String getINVESTIGATOR() {
        return INVESTIGATOR;
    }

    public void setINVESTIGATOR(String INVESTIGATOR) {
        this.INVESTIGATOR = INVESTIGATOR;
    }
    @JSONField(name = "REMNANT_DESC")
    public String getREMNANT_DESC() {
        return REMNANT_DESC;
    }

    public void setREMNANT_DESC(String REMNANT_DESC) {
        this.REMNANT_DESC = REMNANT_DESC;
    }

    @JSONField(name = "SCENE_DISPOSAL")
    public String getSCENE_DISPOSAL() {
        return SCENE_DISPOSAL;
    }

    public void setSCENE_DISPOSAL(String SCENE_DISPOSAL) {
        this.SCENE_DISPOSAL = SCENE_DISPOSAL;
    }
}
