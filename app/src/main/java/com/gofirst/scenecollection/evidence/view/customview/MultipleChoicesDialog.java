package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyEmployees;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.MultiChoiclListDialogAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class MultipleChoicesDialog extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name,isRequiredField;
    private String saveKey;
    private boolean isOrg = false;
    private ListView listView;
    private MultiChoiclListDialogAdapter adapter;
    private String[] beans = new String[] { "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13","14","15","16","17","18","19" };
    private boolean viewWithoutToast;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    private List<String> listId=new ArrayList<>();
    private List<String> listName=new ArrayList<>();
    private String temp;
    private SharePre sharePre;

    public MultipleChoicesDialog(Context context) {
        super(context);
        sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
    }

    public MultipleChoicesDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleChoicesDialog(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void initView(final String mode, final String name, final String text, final String saveKey,String textColor,String dataType,String isRequiredField) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.normal_edit_text_layout, this, true);
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
        click2Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                        clickPop(getContext(),click2Input,name,text);
                }else if (!viewWithoutToast){
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
    }

    @Override
    public boolean validate() {
        return  !TextUtils.isEmpty((String) click2Input.getTag());
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


    public void initData() {
        String orgId=sharePre.getString("organizationId", "");
        List<HyEmployees> hyEmployeesList = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                "organizationId = '"+orgId+"' ");
        if(hyEmployeesList.size()!=0){
            listId = new ArrayList<String>();
            listName = new ArrayList<String>();
            boolean booleans[] = new boolean[listId.size()];

            for (int i = 0; i < hyEmployeesList.size(); i++) {
                if(hyEmployeesList.get(i).getDeleteFlag().toString().equals("0")) {
                    String nameId = hyEmployeesList.get(i).getEmployeeId() + "";
                    String name = hyEmployeesList.get(i).getEmployeeName();
                    listId.add(nameId);
                    listName.add(name);
                }
            }
        }
    }




    public void clickPop(Context context, final TextView input,String name ,String text) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.multi_choicl_list_pop, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        final  TextView cancel=(TextView)view.findViewById(R.id.cancel);
        cancel.setText("清空");
        popName.setText(name);

        initData();
        String employeeName="";
        employeeName = sharePre.getString("prospectPerson","" );
        String employeeNo="";
        employeeNo = sharePre.getString("userId", "");

        listView = (ListView) view.findViewById(R.id.multi_list);
        String text1=input.getText().toString();
        adapter = new MultiChoiclListDialogAdapter(getContext(), listName,employeeName,text1);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    input.setText("点击输入");
                popupWindow.dismiss();
            }
        });
        final String finalEmployeeNo = employeeNo;
        view.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    //MultiChoiclListDialogAdapter.getIsSelected().get(1);
                    String id = "";
                    String nameT = "";

                    for ( int i = 0; i < listId.size(); i++) {
                        if (MultiChoiclListDialogAdapter.getIsSelected().get(i)) {
                            if(listId.get(i).equals(finalEmployeeNo)){
                                id = listId.get(i) + ","+id;
                                nameT = listName.get(i) + ","+nameT;
                            }else {
                                id += listId.get(i) + ",";
                                nameT += listName.get(i) + ",";
                            }
                            input.setTag(id.substring(0, id.length() - 1));
                            input.setText(TextUtils.isEmpty(nameT.substring(0, nameT.length() - 1)) ? "请输入" : nameT.substring(0, nameT.length() - 1));

                        }
                    }
                    Log.d("testtao", MultiChoiclListDialogAdapter.getIsSelected().get(0) + "");
                    //String text = input.getText().toString();
                    //inputDate.setText(TextUtils.isEmpty(text) ? "请输入" : text);
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
