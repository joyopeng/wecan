package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.MultiChoiclListDialogAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class MultiChoiclListDialog extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name;
    private String saveKey,isRequireField;
    private boolean isOrg = false;
    private ListView listView;
    private MultiChoiclListDialogAdapter adapter;
    private String[] beans = new String[] { "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13","14","15","16","17","18","19" };

    private List<String> list=new ArrayList<>();

    public MultiChoiclListDialog(Context context) {
        super(context);
    }

    public MultiChoiclListDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiChoiclListDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        String text = click2Input.getText().toString();
        if (text.equals("") || text.equals("点击输入")) {
            Toast.makeText(getContext(), "请填写" + name, Toast.LENGTH_SHORT).show();
        }
        return text.equals("点击输入") ? "" : text;
    }

    @Override
    public String getViewName() {
        return name;
    }

    @Override
    public void initView(final String mode, final String name, String text, String saveKey,String textColor,String dataType,String isRequireField) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.normal_edit_text_layout, this, true);
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
        click2Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    if (isOrg) {
                        new OrgDialog(v.getContext(), click2Input, name);
                    } else {
                        click2Pop();
                    }
                }
            }
        });
        this.name = name;
        this.saveKey = saveKey;
        this.isRequireField = isRequireField;
        viewName.setText(name);
        click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text : "点击输入");
    }

    @Override
    public boolean validate() {
        String text = click2Input.getText().toString();
        return !text.equals("") && !text.equals("点击输入");
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


    public void click2Pop() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.text_pop, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        final EditText input = (EditText) view.findViewById(R.id.text_input);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    String text = input.getText().toString();
                    click2Input.setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(this, Gravity.BOTTOM, 0, 0);
    }


}
