package com.gofirst.scenecollection.evidence.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.customview.ExpandListView;
import com.gofirst.scenecollection.evidence.view.customview.PermissionSetingHorizNavView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */
public class PermissionSetting  {

    private LinearLayout container,oftenConainer,searchContainer,searchShow;
    private List<View> levelItem = new ArrayList<>();
    private String name;
    private String rootKey;
    private TextView outPutText;
    private View allLine,oftenLine,searchLine;
    private PopupWindow popupWindow;
    private SharePre sharePre;
    private ExpandListView allDepartmentList,allDepartmentPeopleList;
    private List<HyOrganizations> allDepartmentData;

    public  HashMap<Integer, Boolean> isSelectedData;
    private Boolean isUpdate=false;



    private View show_gray;

    // private DepartmentNameListAdapter departmentNameAdapter;
    //  private List<DepartmentNameListAdapter.DepartmentNameListData> list = new ArrayList<>();

    private PermissionSetingHorizNavView horizNavView;
    private ArrayList<String> datas;
    private ArrayList<String> datasId;
    private LinearLayout horiznaview_linearLayout;
    private  RecyclerView rec;
    private static final String PSACTION = "violetjack.permissionSet";

    private Boolean departmentRefresh=false;//部门刷新
    private Boolean departmentPeopleRefresh=false;//部门人员刷新
    public GridView selectGridView;

    private List <HyOrganizations> listName;
    private int positionSet=0;
    private String id = "";
    private  List<User> listUser=new ArrayList<>();

    public PermissionSetting(Context context, TextView inputDate, String name, String rootKey) {
        this.name = name;
        this.rootKey = rootKey;
        initView(context, inputDate);
    }

    private void initView(final Context context, final TextView inputDate) {
        outPutText = inputDate;
        View view = LayoutInflater.from(context).inflate(R.layout.permission_setting, null);
        sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        selectGridView =(GridView)view.findViewById(R.id.GridView1);
        IntentFilter filter = new IntentFilter(PSACTION);
        context.registerReceiver(receiver, filter);

        allLine = view.findViewById(R.id.all_line);
        oftenLine = view.findViewById(R.id.often_line);
        searchLine= view.findViewById(R.id.search_line);

        container = (LinearLayout) view.findViewById(R.id.container);
        oftenConainer = (LinearLayout) view.findViewById(R.id.often_container);
        searchContainer= (LinearLayout) view.findViewById(R.id.search_container);


        show_gray=(View)view.findViewById(R.id.show_gray);
        InitLayout(view, context, outPutText);



        new AllDepartmentPeopleAdapter(context,getAllDepartmentListData(),listName).setAllListener(new AllDepartmentPeopleAdapter.OnAllListener() {
            @Override
            public void onItemClick(String organizationName, int organizationId, Boolean isSel) {
                //Toast.makeText(context,"tf"+position,Toast.LENGTH_SHORT).show();

                if (isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getOrganizationId().equals(organizationId)) {
                            isUpdate = true;
                            return;
                        }
                    }
                    if (!isUpdate) {
                        listName.addAll(EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                                "organizationId ='" + organizationId + "'"));

                        selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                    } else {
                        Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getOrganizationId().equals(organizationId)) {
                            listName.remove((i--));
                            break;
                        }
                    }
                    selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));

                }
               // oftenList.setAdapter(new OftenContentAdapter(context, getOftenListData(), listName));
                isUpdate = false;
            }
        });



        selectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, "已添加" + position, Toast.LENGTH_SHORT).show();
                listName.remove(position);
                selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                //oftenList.setAdapter(new OftenContentAdapter(context, getOftenListData(), listName));
               // if (datas.size() == 1) {
                    allDepartmentList.setAdapter(new AllDepartmentAdapter(context,getAllDepartmentListData()));
                    //allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));
               // } else {

                    //getDatas(context,orgId,orgIdName);
                //    getDatas(context,datasId.get(position),datas.get(position),false);
                   /* List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                            "organizationId = '" + datasId.get(datas.size() - 1) + "'");
                    if (listAllDepartmentPeople.size() > 0) {
                        departmentPeopleRefresh = true;
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleData.addAll(listAllDepartmentPeople);
                        //allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    } else {
                        allDepartmentPeopleData.clear();
                        //allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    }*/
             //   }
            }
        });


        allDepartmentList = (ExpandListView)view.findViewById(R.id.all_list);
        allDepartmentList.setDividerHeight(0);
        allDepartmentList.setAdapter(new AllDepartmentAdapter(context, getAllDepartmentListData()));



        allDepartmentPeopleList= (ExpandListView)view.findViewById(R.id.allpeople_list);
        allDepartmentPeopleList.setDividerHeight(0);
       // allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));


        // horiznaview_linearLayout.setVisibility(View.GONE);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((TextView)view.findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(name);
        view.findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView finish = (TextView) view.findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
        finish.setVisibility(View.VISIBLE);
        finish.setText("完成");
         listUser=EvidenceApplication.db.findAllByWhere(User.class,"userNameId = '"+sharePre.getString("user_id","")+"'");

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String nameT = "";
                if (listName.size() == 0) {
                    //inputDate.setText("权限设置");
                    if(listUser.size()>0){
                        id="";
                        nameT="";
                        Setpermission(context,"/saveAlarmVisibleSetting");

                        listUser.get(0).setPermissionSetting("");
                        // EvidenceApplication.db.update(listUser.get(0));
                    }
                } else {
                    for (int i = 0; i < listName.size(); i++) {
                        id = listName.get(i).getOrganizationId() + "," + id;
                        nameT = listName.get(i).getOrganizationName() + "," + nameT;
                    }


                    if(listUser.size()>0){
                        Setpermission(context,"/saveAlarmVisibleSetting");
                        listUser.get(0).setPermissionSetting(id.substring(0, id.length() - 1));
                       // EvidenceApplication.db.update(listUser.get(0));
                    }
                    //inputDate.setTag(id.substring(0, id.length() - 1));
                    //inputDate.setText(nameT.substring(0, nameT.length() - 1));
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(inputDate, Gravity.BOTTOM, 0, 0);
    }




    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PSACTION)){
                int position = intent.getIntExtra("position", 0);
                positionSet= intent.getIntExtra("position", 0);
                String orgId = intent.getStringExtra("orgId");
                String orgIdName = intent.getStringExtra("orgIdName");
                ArrayList<String>  datasTemp,datasIdTemp= new ArrayList<>();
                datasTemp = intent.getStringArrayListExtra("datas");
                datasIdTemp = intent.getStringArrayListExtra("datasId");
                //tvResult.setText(result);
                datas.clear();
                datasId.clear();
                if(position==0){
                    allDepartmentList.setAdapter(new AllDepartmentAdapter(context,getAllDepartmentListData()));
                    //allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context,getAllDepartmentPoepleListData(),listName));
                    datas.clear();
                    datasId.clear();
                    datas.add(orgIdName);
                    datasId.add(orgId);
                    horizNavView.setDatas(datas, datasId);
                    horizNavView.initAgeList();


                }else{
                    for(int i=0;i<position+1;i++){
                        datas.add(datasTemp.get(i).toString());
                        datasId.add(datasIdTemp.get(i).toString());
                    }
                    /*String orgId = list.get(position).getOrganizationId().toString();
                    String orgIdName = list.get(position).getOrganizationName().toString();
                    getDatas();*/
                    horizNavView.setDatas(datas, datasId);
                    horizNavView.initAgeList();
                    //getDatas(context,orgId,orgIdName,false);

                    getDatas(context,datasId.get(position),datas.get(position),false);

                }
            }
        }
    };



    private void InitLayout(View view, final Context context,TextView outPutText){


        listName=new ArrayList<>();
        //listName =getAllDepartmentPoepleListData();
        String [] temp = null;
        String [] tempId = null;
        /*if(outPutText.getText().toString().equals("请输入")){
            listName.clear();
        }else {
            temp = outPutText.getText().toString().split(",");
            tempId = outPutText.getTag().toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                listName.addAll(EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                        "employeeId ='" + tempId[i] + "'"));

            }
        }*/

        List<User> listNameTemp = EvidenceApplication.db.findAllByWhere(User.class,
                "userId = '"+sharePre.getString("userId","")+"'");
        if(listNameTemp.size()>0&&listNameTemp.get(0).getPermissionSetting()!=null){
            temp = listNameTemp.get(0).getPermissionSetting().toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                listName.addAll(EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                        "organizationId ='" + temp[i] + "'"));

            }

        }

        // listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
        //         "employeeId ='" + sharePre.getString("userId","") + "'"));

        GetSelectDepartmentPeopleAdapter getSelectDepartmentPeopleAdapter;
        getSelectDepartmentPeopleAdapter =new GetSelectDepartmentPeopleAdapter(listName);
        selectGridView.setAdapter(getSelectDepartmentPeopleAdapter);

        horiznaview_linearLayout= (LinearLayout) view.findViewById(R.id.horiznaview_linearLayout);
        horizNavView = (PermissionSetingHorizNavView) view.findViewById(R.id.custom_rec);
        rec = (RecyclerView) horizNavView.findViewById(R.id.recycler);
        //准备数据
        String orgId = sharePre.getString("organizationId","");
        List<HyOrganizations> list=EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '0'");

        List<HyOrganizations> listOrgId = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationId = '"+list.get(0).getOrganizationId().toString()+"'");
        datas = new ArrayList<String>();
        datasId = new ArrayList<String>();
        //for (int i = 0; i < 5; i++) {
        if(list.size()>0) {
            datas.add(list.get(0).getOrganizationName().toString());
            datasId.add(list.get(0).getOrganizationId().toString());
        }
        // }

        //添加监听器，获取到RecyclerView的宽度以设置item的宽度
        final ViewTreeObserver vt = rec.getViewTreeObserver();
        vt.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rec.getViewTreeObserver().removeOnPreDrawListener(this);
                horizNavView.setDatas(datas, datasId);
                horizNavView.setRecyclerviewWidth(rec.getMeasuredWidth());
                horizNavView.initAgeList();
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message); // 将Message对象发送出去

                return true;
            }
        });

    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    // 在这里可以进行UI操作
                   // horiznaview_linearLayout.setVisibility(View.GONE);
                    break;
                case 3:
                    EvidenceApplication.db.update(listUser.get(0));
                    break;
                default:
                    break;
            }
        }
    };








    private List<HyOrganizations> getAllDepartmentListData(){//allDepartmentList

        allDepartmentData = new ArrayList<>();
        allDepartmentData.clear();
        List<HyOrganizations> list=EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '0'");

       /* List<HyOrganizations> listOrgId = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationId = '"+list.get(0).getOrganizationId().toString()+"'");*/
        allDepartmentData.addAll(EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '" + list.get(0).getOrganizationId() + "'"));
        return allDepartmentData;
    }



    public class AllDepartmentAdapter extends BaseAdapter {

        private List<HyOrganizations> list;
        private int currentLevel;
        private Context context;


        public AllDepartmentAdapter(Context context,List<HyOrganizations> list) {
            this.list = list;
            this.context=context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.goods_desc);
            name.setText(list.get(position).getOrganizationName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positionSet++;
                   /* outPutText.setText(list.get(position).getOrganizationName());
                    outPutText.setTag(list.get(position).getOrganizationName());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();*/
                    String orgId = list.get(position).getOrganizationId().toString();
                    String orgIdName = list.get(position).getOrganizationName().toString();

                    getDatas(context,orgId,orgIdName,true);

                }
            });
            return convertView;
        }
    }



    public class GetSelectDepartmentPeopleAdapter extends BaseAdapter {

        private List<HyOrganizations> list;
        private int currentLevel;

        public GetSelectDepartmentPeopleAdapter(List<HyOrganizations> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_select_department_item, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.tv_device_name);
                name.setText(list.get(position).getOrganizationName());
           /* convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outPutText.setText(list.get(position).getEmployeeName());
                    outPutText.setTag(list.get(position).getEmployeeName());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });*/

            return convertView;
        }
    }


    public static  class AllDepartmentPeopleAdapter extends BaseAdapter {

        private List<HyOrganizations> list;
        private List<HyOrganizations> listName;
        private int currentLevel;
        public static OnAllListener onAllListener;
        private Boolean IsSel=false;

        private Context context;
        // 用来控制CheckBox的选中状况
        private  HashMap<Integer, Boolean> isSelectedData;

        public AllDepartmentPeopleAdapter(Context context,   List<HyOrganizations> list,List<HyOrganizations> listName) {
            this.list = list;
            this.context=context;
            this.listName=listName;
            isSelectedData = new HashMap<Integer, Boolean>();
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {

            for (int i = 0; i < list.size(); i++) {
                // isSelectedData.put(i, true);
                getIsSelected().put(i, false);
                for(int j = 0; j < listName.size(); j++){
                    if(list.get(i).getOrganizationId().equals(listName.get(j).getOrganizationId())){
                        getIsSelected().put(i, true);
                    }
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pop_person_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.tv_device_name);
            final CheckBox  checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            name.setText(list.get(position).getOrganizationName());


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSelectedData.get(position)) {
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_uncheckbox);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onAllListener != null) {
                            //onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getOrganizationName().toString(),
                                    list.get(position).getOrganizationId(),false);
                        }
                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_checkbok);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onAllListener != null) {
                            //onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getOrganizationName().toString(),
                                    list.get(position).getOrganizationId(),true);
                        }
                    }

                }

            });

            checkBox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelectedData.get(position)) {
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        if (onAllListener != null) {
                            //    onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getOrganizationName().toString(),
                                    list.get(position).getOrganizationId(),false);
                        }

                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        if (onAllListener != null) {
                            //    onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getOrganizationName().toString(),
                                    list.get(position).getOrganizationId(),true);
                        }
                    }

                }
            });


            checkBox.setChecked(getIsSelected().get(position));

            return convertView;
        }




        public interface OnAllListener {
            //void onItemClick(int position);
            void onItemClick(String organizationName,int organizationId,Boolean isSel);
        }

        public void setAllListener(OnAllListener onAllListener) {
            this.onAllListener = onAllListener;
        }




        public  HashMap<Integer, Boolean> getIsSelected() {
            return isSelectedData;
        }

        public  void setIsSelected(HashMap<Integer, Boolean> isSelectedData) {
            isSelectedData = isSelectedData;
        }
    }













    private void getDatas(Context context, String orgId,String orgIdName,Boolean isDelete){
        if(positionSet==0){
            List<HyOrganizations> listAllDepartment = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                    "organizationBusiUpId = '" + orgId + "'");
            allDepartmentData.clear();
            allDepartmentData = new ArrayList<>();
            if (listAllDepartment.size() > 0) {
                departmentRefresh = true;
                allDepartmentData.addAll(listAllDepartment);
                allDepartmentList.setAdapter(new AllDepartmentAdapter(context,allDepartmentData));

            } else {
                allDepartmentData.clear();
                allDepartmentList.setAdapter(new AllDepartmentAdapter(context, allDepartmentData));
            }
            if(isDelete) {
                datas.add(orgIdName);
                datasId.add(orgId);
                horizNavView.setDatas(datas, datasId);
                horizNavView.initAgeList();
            }


        }else{
            List<HyOrganizations> listAllDepartment = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                    "organizationBusiUpId = '" + orgId + "'");
            allDepartmentData.clear();
            allDepartmentData = new ArrayList<>();
            if (listAllDepartment.size() > 0) {
                departmentRefresh = true;
                allDepartmentData.addAll(listAllDepartment);
                allDepartmentList.setAdapter(new AllDepartmentPeopleAdapter(context,allDepartmentData,listName));

            } else {
                allDepartmentData.clear();
                allDepartmentList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentData,listName));
            }
            if(isDelete) {
                datas.add(orgIdName);
                datasId.add(orgId);
                horizNavView.setDatas(datas, datasId);
                horizNavView.initAgeList();
            }
        }


    }

    private void Setpermission(final Context context,String MethodName){
        StringMap params = new StringMap();

        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("orgIds",id);
        params.putString("userId",sharePre.getString("user_id",""));
        params.putString("token", sharePre.getString("token", ""));


        Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    if (response.getBoolean("success")) {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message); // 将Message对象发送出去
                        Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, response
                                        .getJSONArray("data").toString(),
                                Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
                        //refreshLayout.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
                }
                Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
                Utils.stopProgressDialog();
                //Toast.makeText(context, "网络错误,设置失败", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }









}




