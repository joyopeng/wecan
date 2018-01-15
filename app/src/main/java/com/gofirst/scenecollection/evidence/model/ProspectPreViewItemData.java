package com.gofirst.scenecollection.evidence.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ProspectPreViewItemData implements Serializable {

    private Bitmap background;
    private String desc = "";
    private String name;
    private boolean playOrRecord;
    private boolean isEditOrCamera = false;
    private boolean isNeedRec;
    private String field;
    private boolean selected;


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isEditOrCamera() {
        return isEditOrCamera;
    }

    public void setEditOrCamera(boolean editOrCamera) {
        isEditOrCamera = editOrCamera;
    }

    public boolean isPlayOrRecord() {
        return playOrRecord;
    }

    public void setPlayOrRecord(boolean playOrRecord) {
        this.playOrRecord = playOrRecord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public boolean isNeedRec() {
        return isNeedRec;
    }

    public void setNeedRec(boolean needRec) {
        isNeedRec = needRec;
    }
}
