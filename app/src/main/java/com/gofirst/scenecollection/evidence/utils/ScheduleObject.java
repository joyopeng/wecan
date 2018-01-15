package com.gofirst.scenecollection.evidence.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ScheduleObject {
   /* private int orgId;
    private int empId;
    private String dutyDate;
    private String id;
    private String empName;
    private String deleteFlag;
    private String dutyType;
    private String phone;
    private String beginTime;
    private String endTime;
    private String orgName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getDutyType() {
        return dutyType;
    }

    public void setDutyType(String dutyType) {
        this.dutyType = dutyType;
    }*/

    private String white_start;
    private String white_end;
    private String black_start;
    private String black_end;

    public ArrayList<ScheduleEmployee> getScheduleEmployees() {
        return scheduleEmployees;
    }

    public void setScheduleEmployees(ArrayList<ScheduleEmployee> scheduleEmployees) {
        this.scheduleEmployees = scheduleEmployees;
    }

    private ArrayList<ScheduleEmployee> scheduleEmployees;

    private ArrayList<ScheduleEmployee> whiteScheduleEmployees;

    public ArrayList<ScheduleEmployee> getWhiteScheduleEmployees() {
        return whiteScheduleEmployees;
    }

    public void setWhiteScheduleEmployees(ArrayList<ScheduleEmployee> whiteScheduleEmployees) {
        this.whiteScheduleEmployees = whiteScheduleEmployees;
    }

    public ArrayList<ScheduleEmployee> getBlackScheduleEmployees() {
        return blackScheduleEmployees;
    }

    public void setBlackScheduleEmployees(ArrayList<ScheduleEmployee> blackScheduleEmployees) {
        this.blackScheduleEmployees = blackScheduleEmployees;
    }

    private ArrayList<ScheduleEmployee> blackScheduleEmployees;

    public String getWhite_start() {
        return white_start;
    }

    public void setWhite_start(String white_start) {
        this.white_start = white_start;
    }

    public String getWhite_end() {
        return white_end;
    }

    public void setWhite_end(String white_end) {
        this.white_end = white_end;
    }

    public String getBlack_start() {
        return black_start;
    }

    public void setBlack_start(String black_start) {
        this.black_start = black_start;
    }

    public String getBlack_end() {
        return black_end;
    }

    public void setBlack_end(String black_end) {
        this.black_end = black_end;
    }

    public String getMouth_data() {
        return mouth_data;
    }

    public void setMouth_data(String mouth_data) {
        this.mouth_data = mouth_data;
    }

    public String getMouthData() {
        return mouthData;
    }

    public void setMouthData(String mouthData) {
        this.mouthData = mouthData;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List getWhite() {
        return white;
    }

    public void setWhite(List white) {
        this.white = white;
    }

    public List getBlack() {
        return black;
    }

    public void setBlack(List black) {
        this.black = black;
    }

    private String mouth_data;
    private String mouthData;
    private String week;
    private List white;
    private List black;


}
