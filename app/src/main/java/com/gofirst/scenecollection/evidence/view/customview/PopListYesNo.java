package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author maxiran 弹出列表
 */
public class PopListYesNo extends LinearLayout implements BaseView {

    protected TextView name;
    protected TextView startPop;
    protected String saveKey;
    protected View view;
    protected String rootKey;
    private String text ,isRequiredField;
    private boolean viewWithoutToast;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    public PopListYesNo(Context context) {
        super(context);
        initLayout(context);
    }

    public PopListYesNo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PopListYesNo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    protected void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout, this, true);
        name = (TextView) view.findViewById(R.id.name);
        startPop = (TextView) view.findViewById(R.id.click_to_input);
    }

    @Override
    public String getText() {
        return (String) startPop.getTag();
    }

    @Override
    public String getViewName() {
        return name.getText().toString();
    }

    @Override
    public void initView(final String mode, final String name, final String text, final String saveKey, String textColor, String dataType, String isRequiredField) {
       startPop.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mode.equals(BaseView.EDIT)) {
                   new YesNoSingleLevel(v.getContext(),name,startPop);
               } else if (!viewWithoutToast){
                   Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
               }
           }
       });
        this.saveKey = saveKey;
        this.name.setText(name);
        this.isRequiredField = isRequiredField;
        this.text = text;
        if (!TextUtils.isEmpty(text)){
            startPop.setTag(text);
            if (text.equals("0"))
                startPop.setText("否");
            if (text.equals("1"))
                startPop.setText("是");
        }else {
            startPop.setText(mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        }

        ViewUtil.setTextColorAndInputManger(textColor,dataType,startPop);
    }

    @Override
    public boolean validate() {
        return !TextUtils.isEmpty((String) startPop.getTag());
    }

    @Override
    public String getSaveKey() {
        return saveKey;
    }

    @Override
    public String getIsRequireField() {
        return isRequiredField;
    }

    @Override
    public void saveName(JSONObject jsonObject) throws JSONException {
        String text1 = startPop.getText().toString();
        if (!TextUtils.isEmpty(text1) && !"点击输入".equals(text1))
            jsonObject.put(saveKey + "_NAME", text1);
    }

    @Override
    public boolean isID() {
        return false;
    }

    @Override
    public void setID(String id) {

    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
    }


}
