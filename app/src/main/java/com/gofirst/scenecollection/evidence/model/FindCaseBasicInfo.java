package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2017/7/6.
 */

@Table(name = " FindCaseBasicInfo")
public class FindCaseBasicInfo {



    @Id(column = "id")
    private String id;

    /**
     * 模块类别
     */
    private String father;

    /**
     * 案件編號
     */
    private String caseId;

    /**
     * json存储具体字段
     */
    private String json;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


}
