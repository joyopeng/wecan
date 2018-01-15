package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyEmployees;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MultipleChoice extends LinearLayout implements BaseView{

    private TextView name;
    private TextView startPop;
    private LinearLayout mRootView;
    private String saveKey;
    private MultiChoicePopWindow mMultiChoicePopWindow;
    private StringBuffer stringBuffer;

    private List<String> mSingleDataList;
    private List<String> mMultiDataList;

    public MultipleChoice(Context context) {
        super(context);
        initLayout(context);
    }

    public MultipleChoice(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MultipleChoice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout,this,true);
        name = (TextView)view.findViewById(R.id.name);
        startPop = (TextView)view.findViewById(R.id.click_to_input);
        mRootView=(LinearLayout)view.findViewById(R.id.mRootView);
    }



    @Override
    public String getText() {
        return startPop.getText().toString();
    }

    @Override
    public String getViewName() {
        return "DATETIME";
    }

    @Override
    public void initView(String mode, String name, String text, String saveKey,String textColor,String dataType,String is) {
        if (mode.equals(BaseView.VIEW)){
            startPop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "不可选择", Toast.LENGTH_SHORT).show();
                }
            });
            startPop.setText("未录入");

        }else {
            startPop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    initData();
                    showMultiChoiceWindow();

                }
            });

        }
        startPop.setText(text==null?"":text);
        this.saveKey = saveKey;
        this.name.setText(name);

    }

    @Override
    public boolean validate() {
        String text = startPop.getText().toString();
        return !text.equals("未录入");
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
        startPop.setTag(id);
    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {

    }

    public void initData() {

        List<HyEmployees> list = EvidenceApplication.db.findAll(HyEmployees.class);
        if(list.size()!=0){
            mMultiDataList = new ArrayList<String>();
            boolean booleans[] = new boolean[list.size()];

            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i).getEmployeeName();
                mMultiDataList.add(name);
            }
            initPopWindow(booleans);
        }
    }


    public void showMultiChoiceWindow() {
        mMultiChoicePopWindow.show(true);

    }

    public void initPopWindow(boolean[] booleans) {

        mMultiChoicePopWindow = new MultiChoicePopWindow(getContext(), mRootView,
                mMultiDataList, booleans);
//        mMultiChoicePopWindow.setTitle("genius multi title");
        mMultiChoicePopWindow.setOnOKButtonListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean[] selItems = mMultiChoicePopWindow.getSelectItem();
                int size = selItems.length;
                stringBuffer = new StringBuffer();
                for (int i = 0; i < size; i++) {
                    if (selItems[i]) {
                        stringBuffer.append(mMultiDataList.get(i) + " ");
                        Toast.makeText(getContext(), mMultiDataList.get(i),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                startPop.setText(stringBuffer);

            }
        });
    }



}
