package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @author maxiran
 *         <p/>
 *         用户表
 */
@Table(name = "User")
public class User {
    /**
     * 主键
     */
    private int id;
    private String userName;
    private String token;
    private String prospectPerson;
    private String newUserId;


    public String getNewUserId() {
        return newUserId;
    }

    public void setNewUserId(String newUserId) {
        this.newUserId = newUserId;
    }

    /**
     * 组织编号
     */
    private int organizationId;
    private String password;
    private String userId;

    /**
     * sync data update time
     * */
    private String coredataUpdateTime;
    private String addresslistUpdateTime;
    private String scheduleUpdateTime;

    private String userNameId;
    /**
     * 自动上传时间段
     * */
    private String autoUploadSoltTime;
    private boolean isSupportAutoUpload;

    /*权限设置*/
    private String permissionSetting;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProspectPerson() {
        return prospectPerson;
    }

    public void setProspectPerson(String prospectPerson) {
        this.prospectPerson = prospectPerson;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public String getAutoUploadSoltTime() {
        return autoUploadSoltTime;
    }

    public void setAutoUploadSoltTime(String autoUploadSoltTime) {
        this.autoUploadSoltTime = autoUploadSoltTime;
    }

    public boolean isSupportAutoUpload() {
        return isSupportAutoUpload;
    }

    public void setSupportAutoUpload(boolean supportAutoUpload) {
        isSupportAutoUpload = supportAutoUpload;
    }

    public String getCoredataUpdateTime() {
        return coredataUpdateTime;
    }

    public void setCoredataUpdateTime(String coredataUpdateTime) {
        this.coredataUpdateTime = coredataUpdateTime;
    }

    public String getAddresslistUpdateTime() {
        return addresslistUpdateTime;
    }

    public void setAddresslistUpdateTime(String addresslistUpdateTime) {
        this.addresslistUpdateTime = addresslistUpdateTime;
    }

    public String getScheduleUpdateTime() {
        return scheduleUpdateTime;
    }

    public void setScheduleUpdateTime(String scheduleUpdateTime) {
        this.scheduleUpdateTime = scheduleUpdateTime;
    }

    public String getPermissionSetting() {
        return permissionSetting;
    }

    public void setPermissionSetting(String permissionSetting) {
        this.permissionSetting = permissionSetting;
    }

    public String getUserNameId() {
        return userNameId;
    }

    public void setUserNameId(String userNameId) {
        this.userNameId = userNameId;
    }
}
