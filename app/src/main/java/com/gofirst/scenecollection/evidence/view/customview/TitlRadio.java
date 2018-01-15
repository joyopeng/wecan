package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;

import org.json.JSONObject;

import java.util.List;

/**
 * 平铺单选框IOS风格
 */
public class TitlRadio extends LinearLayout implements BaseView {

    private String parentKey;
    private TextView name;
    private SegmentedGroup radioGroup;
    private String text;
    private String saveKey;
    private String textCode;

    public TitlRadio(Context context) {
        super(context);
        initLayout();
    }

    public TitlRadio(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public TitlRadio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    @Override
    public String getText() {
        return text != null ? text : "";
    }

    @Override
    public String getViewName() {
        return "TITL_RADIO";
    }

    @Override
    public void initView(String mode, String name, String text, String saveKey,String textColor,String dataType,String s) {
        this.name.setText(name);
        this.saveKey = saveKey;
        List<CsDicts> radios = getRadio();
        for (int i = 0; i < radios.size(); i++) {
            CsDicts csDicts = radios.get(i);
            LayoutInflater.from(getContext()).inflate(R.layout.single_radio, radioGroup, true);
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            /*radioButton.setId(csDicts.getId());*/
            radioButton.setTag(csDicts.getDictKey());
            radioButton.setText(csDicts.getDictValue1());
        }
        radioGroup.updateBackground();
        if (mode.equals(BaseView.EDIT))
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    TitlRadio.this.text = radioButton.getText().toString();
                    textCode = (String) radioButton.getTag();
                }
            });
        ViewUtil.radioGroupSetCheckByValue(radioGroup, text);
    }

    @Override
    public boolean validate() {
        return text != null && !text.equals("");
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
    public void saveName(JSONObject jsonObject) {

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

    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    protected void initLayout() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.titl_radio, this, true);
        name = (TextView) view.findViewById(R.id.name);
        radioGroup = (SegmentedGroup) view.findViewById(R.id.radio_group);
    }

    private List<CsDicts> getRadio() {
        return EvidenceApplication.db.findAllByWhere(CsDicts.class, "parentKey = '" + parentKey + "' and rootKey = '" + parentKey + "'");
    }

    public String getTextCode() {
        return textCode;
    }


}
