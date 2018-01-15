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

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.view.adapter.MultiChoiclListDialogLightAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */
public class MultipleChoicesLightDialog extends LinearLayout implements BaseView {

    private TextView click2Input;
    private String name;
    private String saveKey,viewRequireFlag;
    private boolean isOrg = false;
    private ListView listView;
    private MultiChoiclListDialogLightAdapter adapter;
    private String[] beans = new String[] { "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13","14","15","16","17","18","19" };

    private List<String> listId=new ArrayList<>();
    private List<String> listName=new ArrayList<>();
    private List<String> listIdLight=new ArrayList<>();
    private List<String> listNameLight=new ArrayList<>();
    private String temp;
    View view;
    String defaultValue = "";

    public MultipleChoicesLightDialog(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout, this, true);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
    }

    public MultipleChoicesLightDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleChoicesLightDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        return (String) click2Input.getTag();
    }

    @Override
    public String getViewName() {
        return "POP_LIST_MULTI_PLAIN";
    }

    @Override
    public void initView(final String mode, final String name, final String text, final String saveKey,String textColor,String dataType,String viewRequireFlag) {
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);

        this.name = name;
        this.saveKey = saveKey;
        viewName.setText(name);

//        getDefaultValue();
        this.viewRequireFlag = viewRequireFlag;
        //click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text : "点击输入");
        click2Input.setText(text != null && !TextUtils.isEmpty(text) ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "未录入");
        ViewUtil.setTextColorAndInputManger(textColor, dataType, click2Input);
        click2Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    clickPop(getContext(), click2Input, name, text);

                }
            }
        });
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
        return viewRequireFlag;
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

    }


    public  void initDataLight(){
        List<CommonExtField> listField=EvidenceApplication.db.findAllByWhere(CommonExtField.class,
                "field = '" + saveKey + "'");

        if(listField.size()>0){
            listField.get(0).getDictType();
        }
        String key=listField.get(0).getDictType();;//"XCKYGZTJDM";
        List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "parentKey ='" + key + "'");
        if(csDicts.size()!=0){
            listId = new ArrayList<String>();
            listName = new ArrayList<String>();
            boolean booleans[] = new boolean[listId.size()];

            for (int i = 0; i < csDicts.size(); i++) {
                String nameId = csDicts.get(i).getDictKey()+"";
                String name = csDicts.get(i).getDictValue1()+"";
                listId.add(nameId);
                listName.add(name);
            }
        }
    }

    public void getDefaultValue(){

        List<CommonExtField> listField=EvidenceApplication.db.findAllByWhere(CommonExtField.class,
                "field = '" + saveKey + "'");

        if(listField.size()>0){
            listField.get(0).getDictType();
        }

        List<CsDicts> list=EvidenceApplication.db.findAllByWhere(CsDicts.class,"parentKey ='"+listField.get(0).getDictType()+"'");
        String nameTemp="";
        defaultValue=(String)click2Input.getTag();
        String [] temp = null;
        temp = defaultValue.split(",");

        for(int i=0;i<temp.length;i++){
            for(int j=0;j<list.size();j++){
                if(temp[i].equals(list.get(j))){
                    click2Input.setText(list.get(j).getDictValue1());
                }
            }
        }
    }

    public void clickPop(Context context, final TextView input,String name,String text ) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.multi_choicl_list_pop, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        final  TextView cancel=(TextView)view.findViewById(R.id.cancel);
        cancel.setText("清空");
        popName.setText(name);

        initDataLight();
        String text1=input.getText().toString();
        listView = (ListView) view.findViewById(R.id.multi_list);
        adapter = new MultiChoiclListDialogLightAdapter(getContext(), listName,defaultValue,text1);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        String id1 = "";
        String nameT1 = "";
        for (int i = 0; i < listId.size(); i++) {

            if (MultiChoiclListDialogLightAdapter.getIsSelected().get(i)) {

                id1 += listId.get(i) + ",";
                nameT1 += listName.get(i) + ",";
            }
        }
        input.setTag(id1.substring(0,id1.length()-1));
        input.setText(TextUtils.isEmpty(nameT1.substring(0, nameT1.length() - 1)) ? "请输入" : nameT1.substring(0, nameT1.length() - 1));


        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    input.setText("点击输入");
                popupWindow.dismiss();
            }
        });



        view.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    MultiChoiclListDialogLightAdapter.getIsSelected().get(1);
                    String id = "";
                    String nameT = "";
                    for (int i = 0; i < listId.size(); i++) {
                        if (MultiChoiclListDialogLightAdapter.getIsSelected().get(i)) {
                            id += listId.get(i) + ",";
                            nameT += listName.get(i) + ",";
                            input.setTag(id.substring(0,id.length()-1));
                            input.setText(TextUtils.isEmpty(nameT.substring(0, nameT.length() - 1)) ? "请输入" : nameT.substring(0, nameT.length() - 1));

                        }
                    }
                    Log.d("testtao", MultiChoiclListDialogLightAdapter.getIsSelected().get(0) + "");
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

    public void setIsOrg() {
        isOrg = true;
    }
}
