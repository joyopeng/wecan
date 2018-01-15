package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Table;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/22.
 */

@Table(name="FindCsSceneCases")
public class FindCsSceneCases {
    private String id;
    /**
     * 案（事）件受理号
     **/
    private String receptionNo;
    /**
     * 案件编号
     **/
    private String caseNo;
    /**
     * 案件类别(字典:AJLBDM 可多选)
     **/
    private String caseType;
    /*案件类别Id*/
    private String caseTypeId;
    /**
     * 是否命案，1是，0否
     **/
    private String caseCategory;
    /**
     * 按键名称
     */
    private String caseName;
    /**
     * 督办级别
     */
    private String caseLevel;
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
    private String sceneDetail;
    /**
     * 发案开始时间
     **/
    private Date occurrenceDateFrom;
    /**
     * 发案结束时间
     **/
    private Date occurrenceDateTo;
    /**
     * 案件发现过程
     **/
    private String exposureProcess;
    /**
     * 被害人/事主信息
     */
    private String victimInfo;
    /**
     * 原始服务器编号
     */
    private String initServerNo;
    /**
     * 上报时间
     */
    private Date transferDate;
    /**
     * 破案时间
     **/

    private String crackedDate;

    /*勘验ID*/
    private String inquestId;

    /*报案时间*/
    private String reportTime;
    /*报警人*/
    private String alarmPeople;
    /*报警人电话*/
    private String alarmPhone;
    /*接勘人*/
    private String receivePeople;
    /*接勘人警号*/
    private String receivePeopleNum;
    /*勘查开始时间*/
    private String investigationStartTime;
    /*勘查结束时间*/
    private String investigationEndTime;
    /*勘验类型*/
    private String dealType;//0 非简勘   1 简勘

    private String secrecy;
    private String deleteFlag;
    private String reserver1;
    private String reserver2;
    private String reserver3;
    private String reserver4 ;
    private String reserver5 ;
    private String reserver6 ;
    private String reserver7;
    private String reserver8 ;
    private String tempFa1 ;
    private String tempFa2;
    private String gxsk;
    private String qhname;
    private String ifflag;
    private String hostId;
    /*指派方式*/
    private String appointWay;
    /*指派报告单位*/
    private String appointWayUnit;

    /*模板id*/
    private String templateId;

    /*接勘时间*/
    private String receiveCaseTime;



    private String hostYear;
    private String status;

    private Boolean isReceive;
    private String sortListDateTime;   //警情保存报警时间；接勘保存接勘时间



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
    private String alarmCategoryName;
    /**
     * 警情细节
     */
    private String alarmDetail;

    /**
     * 发案地点
     **/
    private String alarmAddress;

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


    private String longs;
    private String lats;
    private String alarmTel;

    /**
     * 接警人电话
     */
    private String receiveTel;
    /**
     * 接警人id
     */
    private String receiveId;
    private String uploadTime;
    private boolean isUploaded;
    private boolean isSpecific;
    private boolean isManualAddCase;

    /**
     * 是否补录
     */
    private boolean addRec;

    public boolean isAddRec() {
        return addRec;
    }

    public void setAddRec(boolean addRec) {
        this.addRec = addRec;
    }

    public boolean isManualAddCase() {
        return isManualAddCase;
    }

    public void setIsManualAddCase(boolean isManualAddCase) {
        this.isManualAddCase = isManualAddCase;
    }

    public boolean isSpecific() {
        return isSpecific;
    }

    public void setSpecific(boolean specific) {
        isSpecific = specific;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public FindCsSceneCases() {
    }

    public String getInquestId() {
        return inquestId;
    }

    public void setInquestId(String inquestId) {
        this.inquestId = inquestId;
    }

    public String getAppointWay() {
        return appointWay;
    }

    public void setAppointWay(String appointWay) {
        this.appointWay = appointWay;
    }

    public String getAppointWayUnit() {
        return appointWayUnit;
    }

    public void setAppointWayUnit(String appointWayUnit) {
        this.appointWayUnit = appointWayUnit;
    }

    public String getReceptionNo() {
        return receptionNo;
    }

    public void setReceptionNo(String receptionNo) {
        this.receptionNo = receptionNo;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getAlarmDatetime() {
        return alarmDatetime;
    }

    public void setAlarmDatetime(String alarmDatetime) {
        this.alarmDatetime = alarmDatetime;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getSortListDateTime() {
        return sortListDateTime;
    }

    public void setSortListDateTime(String sortListDateTime) {
        this.sortListDateTime = sortListDateTime;
    }

    public String getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(String caseCategory) {
        this.caseCategory = caseCategory;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCaseLevel() {
        return caseLevel;
    }

    public void setCaseLevel(String caseLevel) {
        this.caseLevel = caseLevel;
    }

    public String getSceneRegionalism() {
        return sceneRegionalism;
    }

    public void setSceneRegionalism(String sceneRegionalism) {
        this.sceneRegionalism = sceneRegionalism;
    }

    public String getSceneDetail() {
        return sceneDetail;
    }

    public void setSceneDetail(String sceneDetail) {
        this.sceneDetail = sceneDetail;
    }

    public Date getOccurrenceDateFrom() {
        return occurrenceDateFrom;
    }

    public void setOccurrenceDateFrom(Date occurrenceDateFrom) {
        this.occurrenceDateFrom = occurrenceDateFrom;
    }

    public String getSceneRegionalismName() {
        return sceneRegionalismName;
    }

    public void setSceneRegionalismName(String sceneRegionalismName) {
        this.sceneRegionalismName = sceneRegionalismName;
    }

    public Date getOccurrenceDateTo() {
        return occurrenceDateTo;
    }

    public void setOccurrenceDateTo(Date occurrenceDateTo) {
        this.occurrenceDateTo = occurrenceDateTo;
    }

    public String getExposureProcess() {
        return exposureProcess;
    }

    public void setExposureProcess(String exposureProcess) {
        this.exposureProcess = exposureProcess;
    }

    public String getVictimInfo() {
        return victimInfo;
    }

    public void setVictimInfo(String victimInfo) {
        this.victimInfo = victimInfo;
    }

    public String getInitServerNo() {
        return initServerNo;
    }

    public void setInitServerNo(String initServerNo) {
        this.initServerNo = initServerNo;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public String getCrackedDate() {
        return crackedDate;
    }

    public void setCrackedDate(String crackedDate) {
        this.crackedDate = crackedDate;
    }

    public String getSecrecy() {
        return secrecy;
    }

    public void setSecrecy(String secrecy) {
        this.secrecy = secrecy;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getReserver1() {
        return reserver1;
    }

    public void setReserver1(String reserver1) {
        this.reserver1 = reserver1;
    }

    public String getReserver2() {
        return reserver2;
    }

    public void setReserver2(String reserver2) {
        this.reserver2 = reserver2;
    }

    public String getReserver3() {
        return reserver3;
    }

    public void setReserver3(String reserver3) {
        this.reserver3 = reserver3;
    }

    public String getReserver4() {
        return reserver4;
    }

    public void setReserver4(String reserver4) {
        this.reserver4 = reserver4;
    }

    public String getReserver5() {
        return reserver5;
    }

    public void setReserver5(String reserver5) {
        this.reserver5 = reserver5;
    }

    public String getReserver6() {
        return reserver6;
    }

    public void setReserver6(String reserver6) {
        this.reserver6 = reserver6;
    }

    public String getReserver7() {
        return reserver7;
    }

    public void setReserver7(String reserver7) {
        this.reserver7 = reserver7;
    }

    public String getReserver8() {
        return reserver8;
    }

    public void setReserver8(String reserver8) {
        this.reserver8 = reserver8;
    }

    public String getTempFa1() {
        return tempFa1;
    }

    public void setTempFa1(String tempFa1) {
        this.tempFa1 = tempFa1;
    }

    public String getTempFa2() {
        return tempFa2;
    }

    public void setTempFa2(String tempFa2) {
        this.tempFa2 = tempFa2;
    }

    public String getGxsk() {
        return gxsk;
    }

    public void setGxsk(String gxsk) {
        this.gxsk = gxsk;
    }

    public String getQhname() {
        return qhname;
    }

    public void setQhname(String qhname) {
        this.qhname = qhname;
    }

    public String getIfflag() {
        return ifflag;
    }

    public void setIfflag(String ifflag) {
        this.ifflag = ifflag;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsReceive(Boolean isReceive) {
        this.isReceive = isReceive;
    }

    public Boolean getIsReceive() {
        return isReceive;
    }


    public String getReceiveCaseTime() {
        return receiveCaseTime;
    }

    public void setReceiveCaseTime(String receiveCaseTime) {
        this.receiveCaseTime = receiveCaseTime;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getAlarmPeople() {
        return alarmPeople;
    }

    public void setAlarmPeople(String alarmPeople) {
        this.alarmPeople = alarmPeople;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getAlarmPhone() {
        return alarmPhone;
    }

    public void setAlarmPhone(String alarmPhone) {
        this.alarmPhone = alarmPhone;
    }

    public String getReceivePeople() {
        return receivePeople;
    }

    public void setReceivePeople(String receivePeople) {
        this.receivePeople = receivePeople;
    }

    public String getReceivePeopleNum() {
        return receivePeopleNum;
    }

    public void setReceivePeopleNum(String receivePeopleNum) {
        this.receivePeopleNum = receivePeopleNum;
    }

    public String getCaseTypeId() {
        return caseTypeId;
    }

    public void setCaseTypeId(String caseTypeId) {
        this.caseTypeId = caseTypeId;
    }

    public String getInvestigationStartTime() {
        return investigationStartTime;
    }

    public void setInvestigationStartTime(String investigationStartTime) {
        this.investigationStartTime = investigationStartTime;
    }

    public String getInvestigationEndTime() {
        return investigationEndTime;
    }

    public void setInvestigationEndTime(String investigationEndTime) {
        this.investigationEndTime = investigationEndTime;
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

    public String getAlarmAddress() {
        return alarmAddress;
    }

    public void setAlarmAddress(String alarmAddress) {
        this.alarmAddress = alarmAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getReceiveAlarmTime() {
        return receiveAlarmTime;
    }

    public void setReceiveAlarmTime(String receiveAlarmTime) {
        this.receiveAlarmTime = receiveAlarmTime;
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

    public String getAlarmCategoryName() {
        return alarmCategoryName;
    }

    public void setAlarmCategoryName(String alarmCategoryName) {
        this.alarmCategoryName = alarmCategoryName;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }
}
