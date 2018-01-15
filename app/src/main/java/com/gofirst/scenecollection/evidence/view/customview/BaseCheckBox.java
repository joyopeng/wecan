package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/6/12.
 */
public class BaseCheckBox extends LinearLayout implements View.OnClickListener{

    private CheckBox baseCheckbox;
    private String isCheckdValue;

    public BaseCheckBox(Context context) {
        this(context, null);
        initLayout(context);
    }
    public BaseCheckBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }


    private void initLayout(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.base_checkbox, this, true);
        baseCheckbox=(CheckBox)view.findViewById(R.id.base_checkbox);
        baseCheckbox.setOnClickListener(this);
    }

    public void setHide(Boolean isHide){
        if(isHide){
            baseCheckbox.setVisibility(View.GONE);
        }else{
            baseCheckbox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.base_checkbox:
                baseCheckbox.setChecked(true);
            break;
        }
    }

    public void setIsCheckd(Boolean isCheckd){
        if(isCheckd){
            isCheckdValue="1";
        }else{
            isCheckdValue="0";
        }
    }

}
