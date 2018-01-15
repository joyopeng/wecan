package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/11.
 */
@Table(name = "UnUploadJson")
public class UnUploadJson {
    /**待上传的json*/
    @Id(column="id")
    private String id;
    private String json;
    private String caseId;
    private boolean isSpec;
    private String userId;
    private boolean addRec;

    public boolean isAddRec() {
        return addRec;
    }

    public void setAddRec(boolean addRec) {
        this.addRec = addRec;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSpec() {
        return isSpec;
    }

    public void setSpec(boolean spec) {
        isSpec = spec;
    }

    //废弃字段 改为上传完成时间
    private String isUploading;

    private boolean uploaded;

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getIsUploading() {
        return isUploading;
    }

    public void setIsUploading(String isUploading) {
        this.isUploading = isUploading;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
