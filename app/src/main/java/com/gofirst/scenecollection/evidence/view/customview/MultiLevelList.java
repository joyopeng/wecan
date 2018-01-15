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

public class MultiLevelList extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name;
    private String saveKey,isRequiredField;
    private View view;
    private boolean viewWithoutToast;

    public MultiLevelList(Context context) {
        super(context);
         view = LayoutInflater.from(getContext()).inflate(R.layout.normal_edit_text_layout, this, true);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
    }

    public MultiLevelList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLevelList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        return (String) click2Input.getTag();
    }


    @Override
    public String getViewName() {
        return name;
    }

    @Override
    public void initView(final String mode, final String name, String text, String saveKey,String textColor,String dataType,String isRequiredField) {
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    new MultiLevelListDialog(v.getContext(), click2Input, name);
                }else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.name = name;
        this.saveKey = saveKey;
        this.isRequiredField = isRequiredField;
        viewName.setText(name);
        click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text :  mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        ViewUtil.setTextColorAndInputManger(textColor,dataType,click2Input);
    }

    @Override
    public boolean validate() {
        return !TextUtils.isEmpty((String) click2Input.getTag());
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
        String text = click2Input.getText().toString();
        if (!TextUtils.isEmpty(text) && !"点击输入".equals(text))
            jsonObject.put(saveKey + "_NAME", text);
    }

    @Override
    public boolean isID() {
        return true;
    }

    @Override
    public void setID(String id) {
        click2Input.setTag(id);
    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }
}
