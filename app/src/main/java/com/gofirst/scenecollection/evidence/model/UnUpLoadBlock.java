package com.gofirst.scenecollection.evidence.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @author maxiran
 *         用来存放未上传的分块信息
 */
@Table(name = "UnUpLoadBlock")
public class UnUpLoadBlock {

    @Id(column="id")
    private String id;
    /**
     * 分区对应的母文件路径
     */
    private String parentPath;
    /**
     * 本分区的路径
     */
    private String path;
    /**
     * 每个文件的分块数量
     */
    private int blockTotal;
    /**
     * 本分块的编号
     */
    private int blockIndex;

    /**
     * 是否进入上传队列
     */
   private boolean isUploading;

    private boolean isSpec;

    private String caseId;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public boolean isSpec() {
        return isSpec;
    }

    public void setSpec(boolean spec) {
        isSpec = spec;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getBlockTotal() {
        return blockTotal;
    }

    public void setBlockTotal(int blockTotal) {
        this.blockTotal = blockTotal;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }
}
