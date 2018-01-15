package com.gofirst.scenecollection.evidence.view.easytagdragview.bean;

public class SimpleTitleTip implements Tip {
    private int id;
    private String tip;
    private String fieldId;
    private boolean isNeedRec;

    public boolean isNeedRec() {
        return isNeedRec;
    }

    public void setNeedRec(boolean needRec) {
        isNeedRec = needRec;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "tip:"+ tip;
    }
 
    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}

