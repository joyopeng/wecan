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
import com.gofirst.scenecollection.evidence.view.activity.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;

public class QrCode extends LinearLayout implements BaseView{

    protected TextView name;
    protected TextView startPop;
    protected String saveKey;
    protected View view;
    protected String rootKey,viewName;
    private String isRequireField;
    private boolean viewWithoutToast;
    private BroadcastReceiver receiver;

    public QrCode(Context context) {
        super(context);
        initLayout(context);
    }

    public QrCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public QrCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @Override
    public String getText() {
        return (String) startPop.getText();
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public void initView(final String mode, final String name, String text, final String saveKey, String textColor, String dataType, String isRequiredField) {
        startPop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MipcaActivityCapture.class);
                    intent.putExtra("saveKey",saveKey);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.saveKey = saveKey;
        this.isRequireField = isRequiredField;
        this.name.setText(name);
        viewName = name;
        startPop.setText(text != null && !text.equals("") ? text : mode.equals(BaseView.EDIT) ? "点击扫描" : "无");
        ViewUtil.setTextColorAndInputManger(textColor, dataType, startPop);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("result");
                if (intent.getAction().equals(saveKey) && !TextUtils.isEmpty(result))
                    startPop.setText(result);
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter(saveKey));
    }

    @Override
    public boolean validate() {
        return !"点击扫描".equals(startPop.getText().toString())&&!"无".equals(startPop.getText().toString());
    }

    @Override
    public String getSaveKey() {
        return saveKey;
    }

    @Override
    public String getIsRequireField() {
        return isRequireField;
    }

    @Override
    public void saveName(JSONObject jsonObject) throws JSONException {
    }

    @Override
    public boolean isID() {
        return false;
    }

    @Override
    public void setID(String id) {

    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(receiver);
    }

    protected void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout, this, true);
        name = (TextView) view.findViewById(R.id.name);
        startPop = (TextView) view.findViewById(R.id.click_to_input);
    }
}
