package com.gofirst.scenecollection.evidence.model;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/7.
 */
@Table(name = "CasePeople")
public class CasePeople {

    public CasePeople() {
    }

    private String caseId;
    private String peopleType;
    /** 姓名*/
    private String name;
    /** 年龄*/
    private String age;
    /** 性别*/
    private String sex;
    /**身份证号*/
    private String idCard;
    /** 电话号码*/
    private String phoneNum;
    /**单位*/
    private String unit;
    /** 现住地址*/
    private String currentAddress;
    /** 户籍地址*/
    private String registerAddress;
    @Id(column = "id")
    private String id;
    private String caseInfo;
    private String sceneType;
    private String json;
	@JSONField(name = "SCENE_TYPE")
    public String getSceneType() {
        return sceneType;
    }

    public String getCaseInfo() {
        return caseInfo;
    }

    public void setCaseInfo(String caseInfo) {
        this.caseInfo = caseInfo;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
	@JSONField(name = "JSON")   
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
	@JSONField(name = "CASE_ID")   
    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
	@JSONField(name = "ID")   
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
	@JSONField(name = "AGE")   
    public String getAge() {
        return age;
    }


    public void setAge(String age) {
        this.age = age;
    }

	@JSONField(name = "CURRENT_ADDRESS")   
    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

	@JSONField(name = "ID_CARD")  
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
	@JSONField(name = "PEOPLE_TYPE")  
    public String getPeopleType() {
        return peopleType;
    }

    public void setPeopleType(String peopleType) {
        this.peopleType = peopleType;
    }
	@JSONField(name = "PHONE_NUM")  
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
	@JSONField(name = "NAME")  
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
	@JSONField(name = "REGISTER_ADDRESS")  
    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }
	@JSONField(name = "SEX")  
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
	@JSONField(name = "UNIT")  
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
