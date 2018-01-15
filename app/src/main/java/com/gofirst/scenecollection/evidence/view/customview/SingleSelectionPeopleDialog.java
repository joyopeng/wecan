package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyEmployees;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.SingleSelectionPeopleDialogAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */
public class SingleSelectionPeopleDialog extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name,isRequiredField;
    private String saveKey;
    private boolean isOrg = false;
    private ListView listView;
    private SingleSelectionPeopleDialogAdapter adapter;
    private String[] beans = new String[] { "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13","14","15","16","17","18","19" };
    private boolean viewWithoutToast;
    private TextView viewName;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    private List<String> listId=new ArrayList<>();
    private List<String> listName=new ArrayList<>();
    private String temp;
    private SharePre sharePre;
    private List<HyEmployees> hyEmployeesList;
    private onResultListener listener;

    public SingleSelectionPeopleDialog(Context context) {
        super(context);
        sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        initView();
    }

    public SingleSelectionPeopleDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SingleSelectionPeopleDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
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

         hyEmployeesList = EvidenceApplication.db.findAll(HyEmployees.class);
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
        adapter = new SingleSelectionPeopleDialogAdapter(getContext(), listName,employeeName,text1);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(new SingleSelectionPeopleAdapter(hyEmployeesList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (click2Input instanceof TextView) {
                    String text = hyEmployeesList.get(position).getEmployeeName();
                    ((TextView) click2Input).setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    ((TextView) click2Input).setTag(hyEmployeesList.get(position).getEmployeeNo());
                }
                if (listener != null)
                    listener.onResult(hyEmployeesList.get(position).getEmployeeName());
                popupWindow.dismiss();
            }
        });
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
                    //String text = ((SingleAdapter) listView.getAdapter()).;
                    //click2Input.setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(click2Input, Gravity.BOTTOM, 0, 0);

    }


    public void setListener(onResultListener listener) {
        this.listener = listener;
    }

    public interface onResultListener {
        void onResult(String templateId);
    }


    public void setIsOrg() {
        isOrg = true;
    }

    private void initView(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.normal_edit_text_layout, this, true);
        viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
    }
}
