package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/8/18.
 */
@Table(name="Area")
public class Area{

    /**
     * 主键
     * 机构ID
     **/
    private int id;
    @Id(column = "compartmentId")
    private String compartmentId;

    private String compartmentNo;

    private String compartmentName;

    private String compartmentUpNo;

    private String compartmentLevel;

    private String compartmentType;

    private String deleteFlag;

    private String createUser;

    private String createDatetime;

    private String updateUser;

    private String updateDatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompartmentId() {
        return compartmentId;
    }

    public void setCompartmentId(String compartmentId) {
        this.compartmentId = compartmentId;
    }

    public String getCompartmentNo() {
        return compartmentNo;
    }

    public void setCompartmentNo(String compartmentNo) {
        this.compartmentNo = compartmentNo;
    }

    public String getCompartmentName() {
        return compartmentName;
    }

    public void setCompartmentName(String compartmentName) {
        this.compartmentName = compartmentName;
    }

    public String getCompartmentLevel() {
        return compartmentLevel;
    }

    public void setCompartmentLevel(String compartmentLevel) {
        this.compartmentLevel = compartmentLevel;
    }

    public String getCompartmentUpNo() {
        return compartmentUpNo;
    }

    public void setCompartmentUpNo(String compartmentUpNo) {
        this.compartmentUpNo = compartmentUpNo;
    }

    public String getCompartmentType() {
        return compartmentType;
    }

    public void setCompartmentType(String compartmentType) {
        this.compartmentType = compartmentType;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }



    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }
}
