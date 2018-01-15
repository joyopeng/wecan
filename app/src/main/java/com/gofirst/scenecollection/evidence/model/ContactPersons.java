package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/17.
 */
@Table(name="ContactPersons")
public class ContactPersons implements Serializable{
    @Id(column = "id")
    private int id;

    /**
     * 主键
     * 人员ID
     **/

    private String employeeId;
    /**
     * 警号，唯一，除管理员外，一般用警号
     **/
    private String employeeNo;
    /**
     * 人员姓名
     **/
    private String employeeName;
    /**
     * 性别：男1/女2
     **/
    private String employeeSex;
    /**
     * 出生日期
     **/
    private String employeeBirth;
    /**
     * 联系电话
     **/
    private String employeeTel;

    /**
     * 证件名称
     **/
    private String employeeCredname;
    /**
     * 证件号码
     **/
    private String employeeCredno;

    /**
     * 部门ID
     **/
    private int orgDeptId;
    /**
     * 机构ID
     **/
    private int organizationId;
    /**
     * 员工职务
     * */
    private String employeePost;
    /**
     * 员工姓名全拼
     * */
    private String employeeNameSpell;
    /**
     * 最后一次联系时间
     * */
    private Date lastConnectTime;
    /**
     * 单位名称
     * */
    private String organizationName;

    public ContactPersons() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeSex() {
        return employeeSex;
    }

    public void setEmployeeSex(String employeeSex) {
        this.employeeSex = employeeSex;
    }

    public String getEmployeeBirth() {
        return employeeBirth;
    }

    public void setEmployeeBirth(String employeeBirth) {
        this.employeeBirth = employeeBirth;
    }

    public String getEmployeeTel() {
        return employeeTel;
    }

    public void setEmployeeTel(String employeeTel) {
        this.employeeTel = employeeTel;
    }

    public String getEmployeeCredname() {
        return employeeCredname;
    }

    public void setEmployeeCredname(String employeeCredname) {
        this.employeeCredname = employeeCredname;
    }

    public String getEmployeeCredno() {
        return employeeCredno;
    }

    public void setEmployeeCredno(String employeeCredno) {
        this.employeeCredno = employeeCredno;
    }

    public int getOrgDeptId() {
        return orgDeptId;
    }

    public void setOrgDeptId(int orgDeptId) {
        this.orgDeptId = orgDeptId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public String getEmployeePost() {
        return employeePost;
    }

    public void setEmployeePost(String employeePost) {
        this.employeePost = employeePost;
    }

    public String getEmployeeNameSpell() {
        return employeeNameSpell;
    }

    public void setEmployeeNameSpell(String employeeNameSpell) {
        this.employeeNameSpell = employeeNameSpell;
    }

    public Date getLastConnectTime() {
        return lastConnectTime;
    }

    public void setLastConnectTime(Date lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
