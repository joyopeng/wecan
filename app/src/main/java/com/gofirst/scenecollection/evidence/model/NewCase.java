package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by maxiran on 2016/5/11.
 * 案件表
 */
@Table(name = "NewCase")
public class NewCase {

    private int id;
    private String caseRegion;
    private String caseAddress;
    private String caseType;
    private String caseHandler;
    //指派方式 false(指派)，true(报告)
    private boolean appointType;
    private String caseTime;
    private String handlerPhone;
    private String appointText;

    public NewCase(){}
    //这个构造方法是可以正常使用的，说明id是主键，且是自增长的
    public NewCase(String caseRegion,
                   String caseAddress,
                   String caseType,
                   String caseHandler,
                   boolean appointType,
                   String caseTime,
                   String handlerPhone,
                   String appointText) {

        this.caseRegion = caseRegion;
        this.caseAddress = caseAddress;
        this.caseType=caseType;
        this.caseHandler=caseHandler;
        this.appointType=appointType;
        this.caseTime=caseTime;
        this.handlerPhone=handlerPhone;
        this.appointText=appointText;}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaseRegion() {
        return caseRegion;
    }

    public void setCaseRegion(String caseRegion) {
        this.caseRegion = caseRegion;
    }

    public String getCaseAddress() {
        return caseAddress;
    }

    public void setCaseAddress(String caseAddress) {
        this.caseAddress = caseAddress;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseHandler() {return caseHandler;}
    public void setCaseHandler(String caseHandler) {this.caseHandler = caseHandler;
    }

    public boolean isAppointType() {return appointType;}

    public void setAppointType(boolean appointType) {
        this.appointType = appointType;
    }

    public String getCaseTime() {
        return caseTime;
    }

    public void setCaseTime(String caseTime) {
        this.caseTime = caseTime;
    }

    public String getHandlerPhone() {
        return handlerPhone;
    }

    public void setHandlerPhone(String handlerPhone) {
        this.handlerPhone = handlerPhone;
    }

    public String getAppointText() {
        return appointText;
    }

    public void setAppointText(String appointText) {
        this.appointText = appointText;
    }
}
