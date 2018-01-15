package com.gofirst.scenecollection.evidence.model;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/16.
 */
@Table(name="PicturesData")
public class PicturesData {

    @Id(column = "id")
    private String id;
    private String MAIN_ID;
    private String SCENE_TYPE;
    private String ATTACHMENT_ID ;

    private String type;
    private String RANG_FLAG;

    private String photoTypeName;
    private String photoId;
    private String photoType;
    private String photoName;
    /** 原型图片文件路径 */
    private String  filePath;

    /** 200K文件路径 */
    private String twoHundredFilePath;
    /** 缩略图文件路径 */
    private String contractionsFilePath;
    /*照片说明*/
    private String description;


    @JSONField(name="ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name="MAIN_ID")
    public String getMAIN_ID() {
        return MAIN_ID;
    }

    public void setMAIN_ID(String MAIN_ID) {
        this.MAIN_ID = MAIN_ID;
    }


    @JSONField(name="SCENE_TYPE")
    public String getSCENE_TYPE() {
        return SCENE_TYPE;
    }

    public void setSCENE_TYPE(String SCENE_TYPE) {
        this.SCENE_TYPE = SCENE_TYPE;
    }


    @JSONField(name="ATTACHMENT_ID")
    public String getATTACHMENT_ID() {
        return ATTACHMENT_ID;
    }

    public void setATTACHMENT_ID(String ATTACHMENT_ID) {
        this.ATTACHMENT_ID = ATTACHMENT_ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRANG_FLAG() {
        return RANG_FLAG;
    }



    public void setRANG_FLAG(String RANG_FLAG) {
        this.RANG_FLAG = RANG_FLAG;
    }

    public String getPhotoTypeName() {
        return photoTypeName;
    }

    public void setPhotoTypeName(String photoTypeName) {
        this.photoTypeName = photoTypeName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTwoHundredFilePath() {
        return twoHundredFilePath;
    }

    public void setTwoHundredFilePath(String twoHundredFilePath) {
        this.twoHundredFilePath = twoHundredFilePath;
    }

    public String getContractionsFilePath() {
        return contractionsFilePath;
    }

    public void setContractionsFilePath(String contractionsFilePath) {
        this.contractionsFilePath = contractionsFilePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
