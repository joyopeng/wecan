package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import java.util.Date;

/**
 * 用来索引录音录像文件
 * @author maxiran
 */
@Table(name="RecordFileInfo")
public class RecordFileInfo {
    @Id(column = "id")
    private String id;
    /** 文件类型：audio video png*/
    private String fileType;

    /** 照片类型：blind/scene/add */
    private String pictureType;
    /*照片保存时间*/
    private String saveTime;
    /*照片归属*/
    //private String belongTo;
    /*照片命名*/
    private String pictureaName;

    /** 原型图片文件路径 */
    private String  filePath;

    /** 200K文件路径 */
    private String twoHundredFilePath;
    /** 缩略图文件路径 */
    private String contractionsFilePath;

    /** 文件生成时间 */
    private Date fileDate;
    /** 文件标识 如天气录音等 */
    private String child;
    /** 文件上级 如现场环境等 */
    private String father;
    /** 案件编号 */
    private String caseId;

    private String section;
    private String investigationId;
    private String mainId;

    private String content;
    private String deleteFlag;
    private String sceneType;
    private String attachmentId;
    /*方向*/
    private String direction;
    private String refKeyId;
    private boolean isUpload;

    private String photoTypeName;
    private String photoId;
    private String photoType;
    private String photoName;

    private String type;

    private String receptionNo;
    private String description;//照片说明
    private String isMarked; //是否已标记
    private boolean addRec;
    private boolean hasBlock;


    public boolean isHasBlock() {
        return hasBlock;
    }

    public void setHasBlock(boolean hasBlock) {
        this.hasBlock = hasBlock;
    }

    public boolean isAddRec() {
        return addRec;
    }

    public void setAddRec(boolean addRec) {
        this.addRec = addRec;
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

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getFileDate() {
        return fileDate;
    }

    public void setFileDate(Date fileDate) {
        this.fileDate = fileDate;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }


    public String getPictureType() {return pictureType;}

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public String getsaveTime() {return saveTime;}

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPictureaName(){return pictureaName;}
    public void setPictureaName(String pictureaName){this.pictureaName = pictureaName;}

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


    public String getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(String investigationId) {
        this.investigationId = investigationId;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRefKeyId() {
        return refKeyId;
    }

    public void setRefKeyId(String refKeyId) {
        this.refKeyId = refKeyId;
    }

    public String getPhotoTypeName() {
        return photoTypeName;
    }

    public void setPhotoTypeName(String photoTypeName) {
        this.photoTypeName = photoTypeName;
    }

    public String getReceptionNo() {
        return receptionNo;
    }

    public void setReceptionNo(String receptionNo) {
        this.receptionNo = receptionNo;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsMarked() {
        return isMarked;
    }

    public void setIsMarked(String isMarked) {
        this.isMarked = isMarked;
    }
}
