package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 模块顺序表
 */
@Table(name="TemplateSort")
public class TemplateSort {
    @Id(column="id")
    private String id;

    private String fatherKey;

    private String fatherValue;

    private int sort;

    private String caseId;

    private boolean siNeedRec;

    private String date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSiNeedRec() {
        return siNeedRec;
    }

    public void setSiNeedRec(boolean siNeedRec) {
        this.siNeedRec = siNeedRec;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFatherKey() {
        return fatherKey;
    }

    public void setFatherKey(String fatherKey) {
        this.fatherKey = fatherKey;
    }

    public String getFatherValue() {
        return fatherValue;
    }

    public void setFatherValue(String fatherValue) {
        this.fatherValue = fatherValue;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
