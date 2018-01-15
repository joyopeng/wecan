package com.gofirst.scenecollection.evidence.view.customview;

import org.json.JSONException;
import org.json.JSONObject;

public interface BaseView {
     String VIEW = "0";
     String EDIT = "1";
     String ADDREC = "2";

    /**
     * 获取组件输入值
     */
    String getText();


    /**
     * 获取组件英文名称
     */
    String getViewName();


    /**
     * 初始化view
     * @param mode 初始化模式
     */
    void initView(String mode,String name,String text,String saveKey,String textColor,String dataType,String isRequiredField);

    /**
     *验证合法性
     */
    boolean validate();

    String getSaveKey();

    String getIsRequireField();

    void saveName(JSONObject jsonObject) throws JSONException;

    boolean isID();

    void setID(String id);

    void setViewWithoutToast(boolean viewWithoutToast);
}
