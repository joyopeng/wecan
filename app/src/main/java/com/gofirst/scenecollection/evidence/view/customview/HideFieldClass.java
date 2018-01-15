package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class HideFieldClass extends LinearLayout implements BaseView {

    private String saveKey;
    private String caseId;
    private String defaultValue,text,id;

    public HideFieldClass(Context context) {
        super(context);
    }

    public HideFieldClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HideFieldClass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        return "INPUTER_IDS".equals(saveKey) ? id : text;
    }

    @Override
    public String getViewName() {
        return "HIDDEN";
    }

    @Override
    public void initView(String mode, String name, String text, String saveKey,String textColor,String dataType,String isRequire) {
        this.saveKey = saveKey;
        this.text = text;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public String getSaveKey() {
        return saveKey;
    }

    @Override
    public String getIsRequireField() {
        return null;
    }

    @Override
    public void saveName(JSONObject jsonObject) throws JSONException {
        if ("INPUTER_IDS".equals(saveKey))
            jsonObject.put(saveKey + "_NAME",text);
    }

    @Override
    public boolean isID() {
        return "INPUTER_IDS".equals(saveKey);
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {

    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
