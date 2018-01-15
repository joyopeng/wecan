package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/9/13.
 */
@Table(name="SceneAlarm")
public class SceneAlarm {
    private String id;
    /**
     * 警情编号
     **/
    private String alarmNo;
    /**
     * 案件类别(字典:AJLBDM 可多选)
     **/
    private String alarmType;
    /**
     * 是否命案，1是，0否
     **/
    private String alarmCategory;
    /**
     * 警情细节
     */
    private String alarmDetail;
    /**
     * 发案区划(对应hy_compartments 单选)
     **/
    private String sceneRegionalism;
    /**
     * 发案区划名称(对应hy_compartments 单选)
     **/
    private String sceneRegionalismName;
    /**
     * 发案地点
     **/
    private String alarmAddress;
    /**
     * 案件发现过程
     **/
    private String exposureProcess;
    /**
     * 备注
     **/
    private String remark;
    /**
     * 报警时间
     */
    private String alarmDatetime;

    /**
     * 接警时间
     */
    private String receiveAlarmTime;

    /**
     * 是否删除
     */
    private String deleteFlag;
    private String hostId;
    private String hostYear;
    private String status;
    private String longs;
    private String lats;
    private String alarmTel;
    /*报警人名字*/
    private String alarmPeople;
    /**
     * 接警人电话
     */
    private String receiveTel;
    /**
     * 接警人id
     */
    private String receiveId;
    /**
     * 接警人姓名
     */
    private String receivePeople;

    private String receptionNo;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmNo() {
        return alarmNo;
    }

    public void setAlarmNo(String alarmNo) {
        this.alarmNo = alarmNo;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmCategory() {
        return alarmCategory;
    }

    public void setAlarmCategory(String alarmCategory) {
        this.alarmCategory = alarmCategory;
    }

    public String getAlarmDetail() {
        return alarmDetail;
    }

    public void setAlarmDetail(String alarmDetail) {
        this.alarmDetail = alarmDetail;
    }

    public String getSceneRegionalism() {
        return sceneRegionalism;
    }

    public void setSceneRegionalism(String sceneRegionalism) {
        this.sceneRegionalism = sceneRegionalism;
    }

    public String getSceneRegionalismName() {
        return sceneRegionalismName;
    }

    public void setSceneRegionalismName(String sceneRegionalismName) {
        this.sceneRegionalismName = sceneRegionalismName;
    }

    public String getAlarmAddress() {
        return alarmAddress;
    }

    public void setAlarmAddress(String alarmAddress) {
        this.alarmAddress = alarmAddress;
    }

    public String getExposureProcess() {
        return exposureProcess;
    }

    public void setExposureProcess(String exposureProcess) {
        this.exposureProcess = exposureProcess;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAlarmDatetime() {
        return alarmDatetime;
    }

    public void setAlarmDatetime(String alarmDatetime) {
        this.alarmDatetime = alarmDatetime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLongs() {
        return longs;
    }

    public void setLongs(String longs) {
        this.longs = longs;
    }

    public String getLats() {
        return lats;
    }

    public void setLats(String lats) {
        this.lats = lats;
    }

    public String getAlarmTel() {
        return alarmTel;
    }

    public void setAlarmTel(String alarmTel) {
        this.alarmTel = alarmTel;
    }

    public String getAlarmPeople() {
        return alarmPeople;
    }

    public void setAlarmPeople(String alarmPeople) {
        this.alarmPeople = alarmPeople;
    }

    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getReceivePeople() {
        return receivePeople;
    }

    public void setReceivePeople(String receivePeople) {
        this.receivePeople = receivePeople;
    }

    public String getReceiveAlarmTime() {
        return receiveAlarmTime;
    }

    public void setReceiveAlarmTime(String receiveAlarmTime) {
        this.receiveAlarmTime = receiveAlarmTime;
    }

    public String getReceptionNo() {
        return receptionNo;
    }

    public void setReceptionNo(String receptionNo) {
        this.receptionNo = receptionNo;
    }
}
