package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/9/9.
 */
@Table(name = "CsDictsConjunction")
public class CsDictsConjunction {

    private String id;
    private String dictKeyFrom;
    private String parentKeyFrom;
    private String rootKeyFrom;
    private String deleteFlag;
    private String hostId;
    private String hostYear;
    private String dictKeyTo;
    private String parentKeyTo;
    private String rootKeyTo;
    private String createUser;
    private String createDatetime;
    private String updateUser;
    private String updateDatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDictKeyFrom() {
        return dictKeyFrom;
    }

    public void setDictKeyFrom(String dictKeyFrom) {
        this.dictKeyFrom = dictKeyFrom;
    }

    public String getParentKeyFrom() {
        return parentKeyFrom;
    }

    public void setParentKeyFrom(String parentKeyFrom) {
        this.parentKeyFrom = parentKeyFrom;
    }

    public String getRootKeyFrom() {
        return rootKeyFrom;
    }

    public void setRootKeyFrom(String rootKeyFrom) {
        this.rootKeyFrom = rootKeyFrom;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostYear() {
        return hostYear;
    }

    public void setHostYear(String hostYear) {
        this.hostYear = hostYear;
    }

    public String getDictKeyTo() {
        return dictKeyTo;
    }

    public void setDictKeyTo(String dictKeyTo) {
        this.dictKeyTo = dictKeyTo;
    }

    public String getParentKeyTo() {
        return parentKeyTo;
    }

    public void setParentKeyTo(String parentKeyTo) {
        this.parentKeyTo = parentKeyTo;
    }

    public String getRootKeyTo() {
        return rootKeyTo;
    }

    public void setRootKeyTo(String rootKeyTo) {
        this.rootKeyTo = rootKeyTo;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }
}
