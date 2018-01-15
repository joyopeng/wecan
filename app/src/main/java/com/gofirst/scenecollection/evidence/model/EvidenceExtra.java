package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @author maxiran
 *         物证表
 */
@Table(name = "EvidenceExtra")
public class EvidenceExtra implements Cloneable {

    /**
     * uuid与section保持一致
     */
    @Id(column = "id")
    private String id;
    /**
     * 外键关联
     */
    private String section;
    /**
     * 物证类别
     */
    private String father;

    /**
     * 案件編號
     */
    private String caseId;


    private String json;


    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public Object clone() {
        EvidenceExtra o = null;
        try {
            o = (EvidenceExtra) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
