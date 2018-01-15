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

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author maxiran 弹出列表
 */
public class PopList extends LinearLayout implements BaseView {

    protected TextView name;
    protected TextView startPop;
    protected String saveKey;
    protected View view;
    protected String rootKey,viewName;
    private String isRequireField;
    private boolean viewWithoutToast;
    private BroadcastReceiver receiver;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    public PopList(Context context) {
        super(context);
        initLayout(context);
    }

    public PopList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PopList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    protected void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout, this, true);
        name = (TextView) view.findViewById(R.id.name);
        startPop = (TextView) view.findViewById(R.id.click_to_input);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("id_card_info".equals(action) || "id_no".equals(action)) {
                    if ("性别".equals(viewName)) {
                        String gender = intent.getStringExtra("gender");
                        if (!TextUtils.isEmpty(gender)) {
                            if ("男".equals(gender)) {
                                startPop.setText("男");
                                startPop.setTag("1");
                            } else if ("女".equals(gender)) {
                                startPop.setText("女");
                                startPop.setTag("2");
                            }
                        }
                    }
                } else if ("ic".equals(action)) {
                    if ("性别".equals(viewName)) {
                        startPop.setText("男");
                        startPop.setTag("1");
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter("id_card_info"));
        context.registerReceiver(receiver, new IntentFilter("id_no"));
//        context.registerReceiver(receiver, new IntentFilter("ic"));
    }

    @Override
    public String getText() {
        return (String) startPop.getTag();
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public void initView(final String mode, final String name, final String text, final String saveKey, String textColor, String dataType, String isRequiredField) {
        startPop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    Object o = isMultiLevel(rootKey) ? new SpinnerPop(getContext(), startPop, name, rootKey) : new PopListSingleLevel(getContext(), name, rootKey, startPop);
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.saveKey = saveKey;
        this.isRequireField = isRequiredField;
        this.name.setText(name);
        viewName = name;
        startPop.setText(text != null && !text.equals("") ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        ViewUtil.setTextColorAndInputManger(textColor, dataType, startPop);
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
        return isRequireField;
    }

    @Override
    public void saveName(JSONObject jsonObject) throws JSONException {
        String text = startPop.getText().toString();
        if (!TextUtils.isEmpty(text) && !"点击输入".equals(text))
            jsonObject.put(saveKey + "_NAME", text);
    }

    @Override
    public boolean isID() {
        return true;
    }

    @Override
    public void setID(String id) {
        startPop.setTag(id);
    }

    private boolean isMultiLevel(String rootKey) {
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = '" + rootKey + "' and dictLevel = '2'");
        return list != null && list.size() != 0;
    }

    public void setShowView(View view) {
        this.view = view;
    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
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
