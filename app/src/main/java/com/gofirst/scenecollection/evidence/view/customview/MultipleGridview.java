package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.view.adapter.MultipleGridviewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class MultipleGridview extends LinearLayout implements BaseView{

    private TextView name;
    private ProphetGridView gridview;
    private LinearLayout mRootView;
    private String saveKey;
    private MultiChoicePopWindow mMultiChoicePopWindow;
    //private String stringTemp;
    //private SimpleAdapter sim_adapter;
    private List<String> data_list;
    private List<Integer> listPosition=new ArrayList<>();

    private List<String> mSingleDataList;
    private List<String> mMultiDataList;
    private String[] iconName = { "实地勘验", "现场试验", "调查访问", "检验鉴定" };
    private boolean isSelect = false;
    private MultipleGridviewAdapter adapter;


    public MultipleGridview(Context context) {
        super(context);
        initLayout(context);
    }

    public MultipleGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MultipleGridview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_gridview,this,true);
        name = (TextView)view.findViewById(R.id.name);
        gridview = (ProphetGridView)view.findViewById(R.id.gridview);
        mRootView=(LinearLayout)view.findViewById(R.id.mRootView);



        data_list = new ArrayList<String>();
        //获取数据
 //       getData();
       /* String [] from ={"text"};
        int [] to = {R.id.text};*/
     /*   adapter = new MultipleGridviewAdapter(getContext(), data_list);
        //配置适配器
        gridview.setAdapter(adapter);*/
       // gridview.setOnItemClickListener(new ItemClickListener());


        /*gridview.setAdapter(adapter = new MultipleGridviewAdapter(getContext(),data_list,
                new MultipleGridviewAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {
                        Log.d("positionTAO",""+position);
                        if(position>0){
                            listPosition.add(position);
                        }else{
                            for(int i=0;i<listPosition.size();i++){
                                if(listPosition.get(i)==-position)
                                    listPosition.remove(i);
                            }

                        }

                    }}));*/
    }

    class  ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                                View arg1,//The view within the AdapterView that was clicked
                                int arg2,//The position of the view in the adapter
                                long arg3//The row id of the item that was clicked
        ) {
            //在本例中arg2=arg3
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            //显示所选Item的ItemText
            //setTitle((String) item.get("ItemText"));
            Log.d("argtao",arg2+"");
            Log.d("argtao",arg3+"");

           /* if (!isSelect){
                setBackground(getResources().getDrawable(R.drawable.rect_gray));
                //setTextColor(Color.BLACK);
            }else {
                setBackground(getResources().getDrawable(R.drawable.rect_blue));
                //setTextColor(Color.WHITE);
            }*/


            setBackground(getResources().getDrawable(R.drawable.rect_blue));
            Toast.makeText(getContext(), "" + item.get("text"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public String getText() {
        String stringTemp="";
        if(listPosition.size()==0){
            stringTemp= "";
        }else{
            Log.d("listPositiontao",listPosition.size()+"");
            for(int i=0;i<listPosition.size();i++){
                data_list.get(listPosition.get(i));
                stringTemp+=data_list.get(listPosition.get(i))+",";
            }
            stringTemp=stringTemp.substring(0,stringTemp.length()-1);

            //stringTemp=stringTemp.substring(0,stringTemp.length()-1);
        }

        return stringTemp;
    }

    @Override
    public String getViewName() {
        return null;
    }

    @Override
    public void initView(String mode, String name, String text, String saveKey,String textColor,String dataType,String isRe) {

        /*if (mode.equals(BaseView.VIEW)) {
            editText.setFocusable(false);
            editText.setCursorVisible(false);
            editText.setFocusableInTouchMode(false);
        }*/
        this.saveKey = saveKey;
        this.name.setText(name);
        //editText.setText(text != null ? text : "");*/
        data_list = new ArrayList<String>();
        List<CommonExtField> list=EvidenceApplication.db.findAllByWhere(CommonExtField.class,"name = '" + name + "'");
        if(list.size()>0){
            String dictType=list.get(0).getDictType();
            List<CsDicts> listParentKey=EvidenceApplication.db.findAllByWhere(CsDicts.class,"parentKey = '" + dictType + "'");
            if(listParentKey.size()>0){
                for(int i=0;i<listParentKey.size();i++){
                    data_list.add(listParentKey.get(i).getDictValue1());
                }
                gridview.setAdapter(adapter = new MultipleGridviewAdapter(text,getContext(),data_list,
                        new MultipleGridviewAdapter.ClickListener() {
                            @Override
                            public void onClick(int position) {
                                Log.d("positionTAO",""+position);
                                if(position>=0){
                                    listPosition.add(position);
                                }else{
                                    for(int i=0;i<listPosition.size();i++){
                                        if(listPosition.get(i)==-position)
                                        {
                                            listPosition.remove(i);
                                        }
                                    }
                                }
                            }
                        }
                ));
            }
        }


    }

    public List<String> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<iconName.length;i++){
            data_list.add(iconName[i]);
        }

        return data_list;
    }


    @Override
    public boolean validate() {
        return true;
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
}
