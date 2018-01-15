package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/13.
 */
@Table(name = "ProspectInfo")
public class ProspectInfo {
    @Id(column = "id")
    private String id;
    private String receivedDate;
    private String receivedBy;
    private String assignedBy;
    private String assignedWayT;
    private String assignedWayS;
    private String assignedContent;
    private String dispatchDate;
    private String transferDate;
    private String caseId;
    private String initServerNo;
    private String sceneType;
    private String caseType;
    private String caseGov;
    private String handler;

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getCaseGov() {
        return caseGov;
    }

    public void setCaseGov(String caseGov) {
        this.caseGov = caseGov;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getInitServerNo() {
        return initServerNo;
    }

    public void setInitServerNo(String initServerNo) {
        this.initServerNo = initServerNo;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getAssignedContent() {
        return assignedContent;
    }

    public void setAssignedContent(String assignedContent) {
        this.assignedContent = assignedContent;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getAssignedWayS() {
        return assignedWayS;
    }

    public void setAssignedWayS(String assignedWayS) {
        this.assignedWayS = assignedWayS;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedWayT() {
        return assignedWayT;
    }

    public void setAssignedWayT(String assignedWayT) {
        this.assignedWayT = assignedWayT;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
