package com.gofirst.scenecollection.evidence.view.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Date extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name;
    private String saveKey;
    private String isRequiredField;
    private boolean viewWithoutToast;
    private BroadcastReceiver receiver;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    public Date(Context context) {
        super(context);
    }

    public Date(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Date(Context context, AttributeSet attrs, int defStyleAttr) {
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
                    DatePickDialog datePickDialog = new DatePickDialog(v.getContext());
                    datePickDialog.datePickDialog(click2Input, name);
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.name = name;
        this.saveKey = saveKey;
        this.isRequiredField = isRequiredField;
        viewName.setText(name);
        click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        ViewUtil.setTextColorAndInputManger(textColor,dataType,click2Input);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("id_card_info".equals(action) || "id_no".equals(action)) {
                    if ("出生日期".equals(name)) {
                        if (!TextUtils.isEmpty(intent.getStringExtra("birthday"))) {
                            click2Input.setText(intent.getStringExtra("birthday"));
                        }
                    }
                } else if ("ic".equals(action)) {
                    if ("出生日期".equals(name)) {
                        click2Input.setText("1982-07-09");
                    }
                }
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter("id_card_info"));
        getContext().registerReceiver(receiver, new IntentFilter("id_no"));
//        getContext().registerReceiver(receiver, new IntentFilter("ic"));
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
    public void setID(String id){

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (receiver != null) {
            getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
