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

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DateTime extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String saveKey,name;
    private String isRequiredField;
    private boolean viewWithoutToast;


    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    public DateTime(Context context) {
        super(context);
    }

    public DateTime(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        return click2Input.getText().toString();
    }


    @Override
    public String getViewName() {
        return name;
    }

    @Override
    public void initView(final String mode, final String name, String text, String saveKey,String textColor,String dataType,String isRequiredField) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.normal_edit_text_layout, this, true);
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
        click2Input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(v.getContext(), getCurrentTime(), name);
                    dateTimePickDialog.dateTimePicKDialog(click2Input);
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.saveKey = saveKey;
        this.isRequiredField = isRequiredField;
        viewName.setText(name);
        this.name = name;
        click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        ViewUtil.setTextColorAndInputManger(textColor,dataType,click2Input);
    }

    @Override
    public boolean validate() {
        return (!"点击输入".equals(click2Input.getText().toString()) && !"无".equals(click2Input.getText().toString()));
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
    public void saveName(JSONObject jsonObject) {

    }

    @Override
    public boolean isID() {
        return false;
    }

    @Override
    public void setID(String id) {

    }

    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

}