package com.gofirst.scenecollection.evidence.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.SceneAlarm;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.AddNewCase;
import com.gofirst.scenecollection.evidence.view.activity.DailyScheduleActivity;
import com.gofirst.scenecollection.evidence.view.activity.MainActivity;
import com.gofirst.scenecollection.evidence.view.activity.MoreNewCase;
import com.gofirst.scenecollection.evidence.view.activity.NewestStateDetail;
import com.gofirst.scenecollection.evidence.view.activity.NvestigatQuery;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.activity.ProspectPreview;
import com.gofirst.scenecollection.evidence.view.adapter.HomePageFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.HomePageFragmentAdapter.HomePageFragmentData;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener,MainActivity.SearchTitleClick, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private LinearLayout addCase,historyInquiry,dayScheduling;
    private ListView homePageList;
    private HomePageFragmentAdapter adapter;
    private List<HomePageFragmentData> list = new ArrayList<>();
    private RelativeLayout more;
    private View mNoSearchDetail;
    private Button mBtnRefreshListViewClick;
    private String mSearcheKey = "";
    private final String TAG = "HomePageFragment";
    private String[] mCateGoryDatas = {"警情信息", "勘验中", "勘验结束"};
    private String[] mSortDatas = { "时间最近", "时间最远","报警类型"};
    private String mSrotSelected = "时间最近";
    private String mCateGorySelected = "警情信息";
    private long mStartTime = 0;
    private SharePre sharePre;
    private ListView mSelectionSortList;
    private SortListAdapter mSortListAdapter;
    private  View mSelectionShowLayout;
    private Dialog mDeleteDialog;
    private String mDelCaseId;
    private String mDelSceneId;

    private BroadcastReceiver mUpdateNewCaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"mUpdateNewCaseReceiver onReceive action = " + intent.getAction());
            if("update_new_case_broadcast".equals(intent.getAction())){
                String data = intent.getStringExtra("update");
                if("1".equals(data)){
                    getAddCaseData(mSearcheKey);
                    //adapter = new HomePageFragmentAdapter(getActivity(),list,HomePageFragment.this);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("tag", "onCreate");
        mStartTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.i(TAG,"onCreat eView start time = " + (System.currentTimeMillis() - mStartTime));
        View view = inflater.inflate(R.layout.homepage, null);
        sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        mNoSearchDetail = view.findViewById(R.id.scene_no_details);
        Init(view);
        adapter = new HomePageFragmentAdapter(getActivity(),list,this);
        homePageList.setAdapter(adapter);
        Log.i(TAG, "onCreateView end time = " + (System.currentTimeMillis() - mStartTime));
        return view;
    }

    private void Init(View view){
        addCase=(LinearLayout)view.findViewById(R.id.add_case);
        historyInquiry=(LinearLayout)view.findViewById(R.id.history_inquiry);
        dayScheduling=(LinearLayout)view.findViewById(R.id.day_scheduling);
        homePageList=(ListView)view.findViewById(R.id.home_page_list);
        homePageList.setOnItemClickListener(this);
        homePageList.setOnItemLongClickListener(this);
        more=(RelativeLayout)view.findViewById(R.id.more);
        addCase.setOnClickListener(this);
        historyInquiry.setOnClickListener(this);
        dayScheduling.setOnClickListener(this);
        more.setOnClickListener(this);
        mSelectionShowLayout = view.findViewById(R.id.selection_layout);
        mSelectionSortList = (ListView) view.findViewById(R.id.selection_sort_list);
        mSelectionSortList.setOnItemClickListener(mSortListItemClick);
        view.findViewById(R.id.layout_selection_category).setOnClickListener(this);
        view.findViewById(R.id.layout_selection_sort).setOnClickListener(this);
        mSelectionShowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSelectionShowLayout.setVisibility(View.GONE);
                return true;
            }
        });
    }


    public AdapterView.OnItemClickListener mSortListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSortListAdapter.isSort)
                mSrotSelected = mSortDatas[position];
            else
                mCateGorySelected = mCateGoryDatas[position];
            mSortListAdapter.notifyDataSetChanged();
            mSelectionShowLayout.setVisibility(View.GONE);
            switch (position) {
                case 0:
                    getWhereData(mSortListAdapter.isSort ? "select * from CsSceneCases order by sortListDateTime desc limit 0,400 ":
                            "select * from CsSceneCases where status = '0' limit 0,400");
                    break;
                case 1:
                    getWhereData(mSortListAdapter.isSort ? "select * from CsSceneCases order by sortListDateTime asc limit 0,400":
                            "select * from CsSceneCases where status = '1' or status = '2' limit 0,100");
                    break;
                case 2:
                    //getWhereData(mSortListAdapter.isSort ? "select * from CsSceneCases order by caseType asc limit 0,400"
                    getWhereData(mSortListAdapter.isSort ? "select * from CsSceneCases order by alarmCategory asc limit 0,400"
                            : "select * from CsSceneCases where status = '3' limit 0,100");
                    break;

            }


        }
    };

    private void getWhereData(String where){
        HomePageFragmentData homePageFragmentData;
        SQLiteDatabase sQLiteDatabase = Utils.getSQLiteDatabase(getActivity());
        Cursor cursor = null;
        try {
            cursor = sQLiteDatabase.rawQuery(where, null);
            list.clear();
            while (cursor.moveToNext()) {
                homePageFragmentData = new HomePageFragmentData();
                String status = cursor.getString(cursor.getColumnIndex("status"));
                if("0".equals(status)) {
                    homePageFragmentData.setId(cursor.getString(cursor.getColumnIndex("id")));
                    homePageFragmentData.setCaseId("");
                    homePageFragmentData.setName(cursor.getString(cursor.getColumnIndex("alarmPeople")));
                    homePageFragmentData.setMarkOrPhone(cursor.getString(cursor.getColumnIndex("alarmTel")));
                    homePageFragmentData.setExposureProcess(cursor.getString(cursor.getColumnIndex("exposureProcess")));
                    homePageFragmentData.setState(status);
                    homePageFragmentData.setAlarmAddress(cursor.getString(cursor.getColumnIndex("alarmAddress")));
                    homePageFragmentData.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                    homePageFragmentData.setAlarmDatetime(cursor.getString(cursor.getColumnIndex("alarmDatetime")));
                    homePageFragmentData.setReceptionNo(cursor.getString(cursor.getColumnIndex("receptionNo")));
                    list.add(homePageFragmentData);
                }else{
                    if(cursor.getString(cursor.getColumnIndex("receivePeopleNum"))!=null&&
                            cursor.getString(cursor.getColumnIndex("receivePeopleNum")).equals(sharePre.getString("userId",""))) {
                        homePageFragmentData.setId(cursor.getString(cursor.getColumnIndex("id")));
                        homePageFragmentData.setCaseId(cursor.getString(cursor.getColumnIndex("caseNo")));
                        homePageFragmentData.setName(cursor.getString(cursor.getColumnIndex("receivePeople")));
                        homePageFragmentData.setMarkOrPhone(cursor.getString(cursor.getColumnIndex("alarmTel")));
                        homePageFragmentData.setExposureProcess(cursor.getString(cursor.getColumnIndex("exposureProcess")));
                        homePageFragmentData.setState(status);
                        homePageFragmentData.setAlarmAddress(cursor.getString(cursor.getColumnIndex("alarmAddress")));
                        homePageFragmentData.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                        homePageFragmentData.setAlarmDatetime(cursor.getString(cursor.getColumnIndex("receiveCaseTime")));
                        homePageFragmentData.setReceptionNo(cursor.getString(cursor.getColumnIndex("receptionNo")));
                        list.add(homePageFragmentData);
                    }
                }
            }

        }catch (Exception e){
            Log.i(TAG,"get case date exception",e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            if(sQLiteDatabase != null) {
                sQLiteDatabase.close();
            }
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void backBtnClick() {

    }

    @Override
    public void searchBtnClick(String key) {

        /*if(key.equals("")){
            Log.d("keytao","123"+key);
            getData();
            getAddCaseData();
            //sortList(list);
            adapter.notifyDataSetChanged();
        }else{
            getLacalData(key);
            //sortList(list);
            adapter.notifyDataSetChanged();
        }*/
        mSearcheKey = key;
        getAddCaseData(mSearcheKey);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_case:
                Intent intent = new Intent(getActivity(), AddNewCase.class);
                startActivity(intent);
                break;
            case R.id.history_inquiry:
                Intent intent1 = new Intent(getActivity(), NvestigatQuery.class);
                startActivity(intent1);
                break;
            case R.id.day_scheduling:
                Intent intent2 = new Intent(getActivity(), DailyScheduleActivity.class);
                startActivity(intent2);
                break;
            case R.id.more:
                Intent intent3 = new Intent(getActivity(), MoreNewCase.class);
                startActivityForResult(intent3, 1);
                break;
            case R.id.layout_selection_category:
               //分类
                mSelectionShowLayout.setVisibility(View.VISIBLE);
                mSortListAdapter = new SortListAdapter(this.getActivity(),false);
                mSelectionSortList.setAdapter(mSortListAdapter);
                break;
            case R.id.layout_selection_sort:
                // 排序
                mSelectionShowLayout.setVisibility(View.VISIBLE);
                mSortListAdapter = new SortListAdapter(this.getActivity(),true);
                mSelectionSortList.setAdapter(mSortListAdapter);
                break;
        }
    }

    public void getData(){
        HomePageFragmentData homePageFragmentData;
        List<SceneAlarm> resultList= EvidenceApplication.db.findAll(SceneAlarm.class,"alarmDatetime desc limit 0,50");

        list.clear();
        Log.d("resultList", "" + resultList.size());
        for(int j=0;j<resultList.size();j++) {
            if(resultList.get(j).getStatus().equals("0")) {
                homePageFragmentData = new HomePageFragmentData();
                //homePageFragmentData.setCaseId(resultList.get(j).getCaseNo());
                homePageFragmentData.setAlarmDatetime(resultList.get(j).getAlarmDatetime());
                homePageFragmentData.setId(resultList.get(j).getId());
                homePageFragmentData.setMarkOrPhone(resultList.get(j).getAlarmTel());
                homePageFragmentData.setName(resultList.get(j).getAlarmPeople());
                homePageFragmentData.setExposureProcess(resultList.get(j).getExposureProcess());
                homePageFragmentData.setAlarmAddress(resultList.get(j).getAlarmAddress());
                homePageFragmentData.setRemark(resultList.get(j).getRemark());
                homePageFragmentData.setState(resultList.get(j).getStatus());
                homePageFragmentData.setReceptionNo(resultList.get(j).getReceptionNo());
                //homePageFragmentData.setCrackedDate(resultList.get(j).getCrackedDate());
                list.add(homePageFragmentData);
            }
        }
    }

    public void getAddCaseData(String condition){
        HomePageFragmentData homePageFragmentData;
        SQLiteDatabase sQLiteDatabase;
        sQLiteDatabase = Utils.getSQLiteDatabase(getActivity());
        String where = "";
        if("".equals(condition)){
            where = "select * from(select * from(" +
                    "select * from CsSceneCases c where c.status = '0' and (deleteFlag = '0' or deleteFlag is null) order by c.sortListDateTime desc limit 0,400) t1 " +
                    "union " +
                    "select * from(select * from CsSceneCases c where c.status != '0' and (deleteFlag = '0' or deleteFlag is null) order by c.sortListDateTime desc limit 0,100) t2) order by sortListDateTime desc";
        }else{
            where = "select * from(select * from(" +
                    "select * from CsSceneCases c where c.status = '0' and (c.exposureProcess like '%" + condition + "%' " +
                    "or c.alarmAddress like '%"+ condition +"%' or c.alarmPeople like '%"+ condition +"%' " +
                    "or c.alarmTel like '%"+ condition +"%') and (deleteFlag = '0' or deleteFlag is null) order by c.reportTime desc limit 0,400) t1 " +
                    "union " +
                    "select * from(select * from CsSceneCases c where c.status != '0' and (c.exposureProcess like '%" + condition + "%' or c.alarmAddress like '%"+ condition +"%' " +
                    "or c.alarmPeople like '%"+ condition +"%' " +
                    "or c.receptionNo like '%"+ condition +"%' " +
                    "or c.alarmTel like '%"+ condition +"%' ) and (deleteFlag = '0' or deleteFlag is null) order by c.sortListDateTime desc limit 0,100) t2) order by sortListDateTime desc";
        }
        Cursor cursor = null;
        try {
            cursor = sQLiteDatabase.rawQuery(where, null);
            list.clear();
            while (cursor.moveToNext()) {
                homePageFragmentData = new HomePageFragmentData();
                String status = cursor.getString(cursor.getColumnIndex("status"));
                if("0".equals(status)) {
                    homePageFragmentData.setId(cursor.getString(cursor.getColumnIndex("id")));
                    homePageFragmentData.setCaseId("");
                    homePageFragmentData.setName(cursor.getString(cursor.getColumnIndex("alarmPeople")));
                    homePageFragmentData.setMarkOrPhone(cursor.getString(cursor.getColumnIndex("alarmTel")));
                    homePageFragmentData.setExposureProcess(cursor.getString(cursor.getColumnIndex("exposureProcess")));
                    homePageFragmentData.setState(status);
                    homePageFragmentData.setAlarmAddress(cursor.getString(cursor.getColumnIndex("alarmAddress")));
                    homePageFragmentData.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                    homePageFragmentData.setAlarmDatetime(cursor.getString(cursor.getColumnIndex("alarmDatetime")));
                    homePageFragmentData.setReceptionNo(cursor.getString(cursor.getColumnIndex("receptionNo")));
                    list.add(homePageFragmentData);
                }else{
                    if(cursor.getString(cursor.getColumnIndex("receivePeopleNum"))!=null&&
                            cursor.getString(cursor.getColumnIndex("receivePeopleNum")).equals(sharePre.getString("userId",""))) {

                        Log.d("taof",cursor.getString(cursor.getColumnIndex("receivePeopleNum"))+"");
                        Log.d("taof",sharePre.getString("userId","")+"");

                        homePageFragmentData.setId(cursor.getString(cursor.getColumnIndex("id")));
                        homePageFragmentData.setCaseId(cursor.getString(cursor.getColumnIndex("caseNo")));
                        homePageFragmentData.setName(cursor.getString(cursor.getColumnIndex("receivePeople")));
                        homePageFragmentData.setMarkOrPhone(cursor.getString(cursor.getColumnIndex("alarmTel")));

                  //      homePageFragmentData.setMarkOrPhone(cursor.getString(cursor.getColumnIndex("receivePeopleNum")));
                        homePageFragmentData.setExposureProcess(cursor.getString(cursor.getColumnIndex("exposureProcess")));
                        homePageFragmentData.setState(status);
                        homePageFragmentData.setAlarmAddress(cursor.getString(cursor.getColumnIndex("alarmAddress")));
                        homePageFragmentData.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                        homePageFragmentData.setAlarmDatetime(cursor.getString(cursor.getColumnIndex("receiveCaseTime")));
                        homePageFragmentData.setReceptionNo(cursor.getString(cursor.getColumnIndex("receptionNo")));
                        list.add(homePageFragmentData);
                    }
                }
                //list.add(homePageFragmentData);
            }

        }catch (Exception e){
            Log.i(TAG,"get case date exception",e);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            if(sQLiteDatabase != null) {
                sQLiteDatabase.close();
            }
        }
        //
        if(list != null && list.size() > 0 ){
            mNoSearchDetail.setVisibility(View.GONE);
        }else{
            mNoSearchDetail.setVisibility(View.VISIBLE);
        }
        //
        adapter.notifyDataSetChanged();
    }
/*private String getClumn(Cursor cursor,String columnName){
    cursor.getString(cursor.getColumnIndex("receivePeopleNum")
}*/

    public void getLacalData(String condition)  {
        Log.d("condition",condition);
        //condition="盗窃";

        List<SceneAlarm> sceneAlarmlistData = EvidenceApplication.db.findAllByWhere(SceneAlarm.class,
                "exposureProcess like '%" + condition + "%' " +
                        "or alarmAddress like '%"+ condition +"%' " +
                        "or alarmPeople like '%"+ condition +"%' " +
                        "or alarmTel like '%"+ condition +"%' ","alarmDatetime desc limit 0,50");
        list.clear();


        HomePageFragmentData homePageFragmentData;
        if(sceneAlarmlistData.size()!=0){
            for (int i = 0; i < sceneAlarmlistData.size(); i++) {
                if(sceneAlarmlistData.get(i).getStatus().equals("0")) {
                    homePageFragmentData = new HomePageFragmentData();
                    homePageFragmentData.setId(sceneAlarmlistData.get(i).getId());
                    homePageFragmentData.setCaseId(sceneAlarmlistData.get(i).getId());
                    homePageFragmentData.setMarkOrPhone(sceneAlarmlistData.get(i).getAlarmTel());
                    homePageFragmentData.setName(sceneAlarmlistData.get(i).getAlarmPeople());
                    homePageFragmentData.setExposureProcess(sceneAlarmlistData.get(i).getExposureProcess());
                    homePageFragmentData.setState(sceneAlarmlistData.get(i).getStatus());
                    homePageFragmentData.setAlarmAddress(sceneAlarmlistData.get(i).getAlarmAddress());
                    homePageFragmentData.setRemark(sceneAlarmlistData.get(i).getRemark());
                    //homePageFragmentData.setCrackedDate(listData.get(i).getCrackedDate());
                    homePageFragmentData.setAlarmDatetime(sceneAlarmlistData.get(i).getAlarmDatetime());
                    homePageFragmentData.setReceptionNo(sceneAlarmlistData.get(i).getReceptionNo());
                    list.add(homePageFragmentData);
                }
            }
        }


        List<CsSceneCases> listData = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                "exposureProcess like '%" + condition + "%' " +
                        "or sceneDetail like '%"+ condition +"%' " +
                        "or caseNo like '%"+ condition +"%' " +
                        "or alarmPhone like '%"+ condition +"%' " +
                        "or alarmPeople like '%"+ condition +"%' ","sortListDateTime desc limit 0,50");

        if(listData.size()!=0){
            for (int i = 0; i < listData.size(); i++) {
                if(listData.get(i).getStatus().equals("1")) {
                    homePageFragmentData = new HomePageFragmentData();

                    homePageFragmentData.setCaseId(listData.get(i).getCaseNo());
                    //homePageFragmentData.setInvestigationPlace(resultList.get(j).getSceneDetail());
                    homePageFragmentData.setName(listData.get(i).getReceivePeople());
                    homePageFragmentData.setExposureProcess(listData.get(i).getExposureProcess());
                    homePageFragmentData.setState(listData.get(i).getStatus());
                    homePageFragmentData.setMarkOrPhone(listData.get(i).getReceivePeopleNum());
                    //homePageFragmentData.setCrackedDate(listData.get(i).getCrackedDate());
                    homePageFragmentData.setAlarmAddress(listData.get(i).getSceneDetail());
                    homePageFragmentData.setRemark(listData.get(i).getCaseType());
                    homePageFragmentData.setAlarmDatetime(listData.get(i).getReceiveCaseTime());
                    homePageFragmentData.setReceptionNo(listData.get(i).getReceptionNo());
                    list.add(homePageFragmentData);
                }
            }
        }

    }

    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume start time = " + (System.currentTimeMillis() - mStartTime));
        //getData();
        getAddCaseData(mSearcheKey);
        //sortList(list);
        adapter.notifyDataSetChanged();
        getActivity().registerReceiver(mUpdateNewCaseReceiver, new IntentFilter("update_new_case_broadcast"));
        Log.i(TAG,"onResume end time = " + (System.currentTimeMillis() - mStartTime));
    }

    @Override
    public void onPause() {
        Log.i(TAG,"onPause time = " + (System.currentTimeMillis() - mStartTime));
        getActivity().unregisterReceiver(mUpdateNewCaseReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy time = " + (System.currentTimeMillis() - mStartTime));
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3:
                 //getData();
                 //getAddCaseData();
                 //adapter.notifyDataSetChanged();
                break;
            case 1:
                //getData();
                //getAddCaseData();
                //adapter.notifyDataSetChanged();
                break;
        }
    }

    //按报警时间（警情）、接勘时间（已接勘）的混合倒序排，最新的放在最上面
    private void sortList(List<HomePageFragmentData> sourceList){
        long time = System.currentTimeMillis();
        int length = sourceList.size();
        if(length == 0){
            if(!EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG){
                Log.d(TAG,"EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG is false;length = 0");
                //Toast.makeText(getActivity(),"正在下载警情信息。。。",Toast.LENGTH_SHORT).show();
                Utils.startTipDialog(getActivity(),"","正在下载警情信息。。。",false,true,false,null);
            }
            return;
        }
        HomePageFragmentData cache = sourceList.get(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = "";
        String endTime = "";
        for(int i = 0;i < length;i++){
            for (int j = i + 1;j < length;j++){
                startTime = sourceList.get(i).getAlarmDatetime();
                endTime = sourceList.get(j).getAlarmDatetime();
                if(startTime == null || endTime == null || "".equals(startTime) || "".equals(endTime)){
                    continue;
                }
                try {
                    if(Utils.compareDate(dateFormat.parse(startTime),dateFormat.parse(endTime)) > 0){
                        cache = sourceList.get(j);
                        sourceList.remove(j);
                        sourceList.add(j,sourceList.get(i));
                        sourceList.remove(i);
                        sourceList.add(i,cache);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(TAG,"sortList host time = " + (System.currentTimeMillis() - time));
    }

    private void showDeleteDialog() {
        TextView delete;
        mDeleteDialog = new Dialog(getActivity(), R.style.FullHeightDialog1);
        mDeleteDialog.setContentView(R.layout.delete_dialog);
        mDeleteDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        delete = (TextView) mDeleteDialog.findViewById(R.id.delete_relativeLayout);

        mDeleteDialog.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CsSceneCases csc = ViewUtil.getCsSceneCasesByCaseId(mDelCaseId);
                csc.setDeleteFlag("1");
                EvidenceApplication.db.update(csc);

                getAddCaseData(mSearcheKey);
                adapter.notifyDataSetChanged();

                updateStatus(mDelSceneId, "0");
                mDeleteDialog.dismiss();
            }
        });

    }

    private void updateStatus(String id, String status){
        Log.e("jiu", "id = " + id + ", status = " + status);

        StringMap params = new StringMap();
        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("id",id);
        params.putString("user",sharePre.getString("userId", ""));
        params.putString("token", sharePre.getString("token", ""));
        params.putString("status", status);

        Netroid.PostHttp("/update/staus", params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("jiu", "pos = " + position);
        HomePageFragmentData data = list.get(position);
        String stu = data.getState();
        if("1".equals(stu) || "2".equals(stu)) {
            mDelCaseId = data.getCaseId();
            mDelSceneId = data.getId();
            showDeleteDialog();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("jiu", "111 pos = " + position);
        HomePageFragmentAdapter.ViewHolder viewHolder = (HomePageFragmentAdapter.ViewHolder) view.getTag();
        HomePageFragmentData data = viewHolder.data;
        String stu = data.getState();
        if("0".equals(stu)){
            List<CsSceneCases> csSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"receptionNo = \"" + data.getReceptionNo() + "\"");
            // if(csSceneCases.size() > 0){
            if(false){
                CsSceneCases sceneCase = csSceneCases.get(0);
                Intent intent = new Intent(getContext(), ProspectPreview.class);

                intent.putExtra("caseId", sceneCase.getCaseNo());
                intent.putExtra("caseInfo", sceneCase.getExposureProcess());
                intent.putExtra("templateId", sceneCase.getTemplateId());
                intent.putExtra("status", "0");
                getContext().startActivity(intent);
            }else {
                Intent intent = new Intent(getContext(), NewestStateDetail.class);
                intent.putExtra("id", data.getId());
                getActivity().startActivityForResult(intent, 3);
            }
            //      context.startActivity(intent);
        }else if("1".equals(stu)||"2".equals(stu)){
            List<CsSceneCases> reslist= EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                    "caseNo = '"+ data.getCaseId() +"'");
            Intent intent = new Intent(getContext(), ProspectPreview.class);

            if(reslist.get(0).getDealType().equals("1")){
                intent.putExtra("dealType", true);
            }

            intent.putExtra("caseId", reslist.get(0).getCaseNo());
            intent.putExtra("caseInfo", reslist.get(0).getExposureProcess());
            intent.putExtra("templateId", reslist.get(0).getTemplateId());
            intent.putExtra("status", "1");
            getActivity().startActivity(intent);
            //setTemplateId(templateId, caseId);
        }else if("3".equals(stu)){
            List<CsSceneCases>reslist= EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                    "caseNo = '"+ list.get(position).getCaseId() +"'");
            Intent intent = new Intent(getContext(), ProspectInterface.class);


            intent.putExtra("caseId", reslist.get(0).getCaseNo());
            intent.putExtra("mode", BaseView.VIEW);
            intent.putExtra("caseInfo", reslist.get(0).getExposureProcess());
            intent.putExtra("templateId", reslist.get(0).getTemplateId());
            initItemFromJson(reslist.get(0).getCaseNo());
            if(withCaseList.size() <=0){
                initItemFromDb(reslist.get(0).getTemplateId());
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("tabList", withCaseList);
            intent.putExtras(bundle);

            getActivity().startActivity(intent);
            //setTemplateId(templateId, caseId);
        }
    }

    //temporary and will delete
    private ArrayList<ProspectPreViewItemData> withCaseList = new ArrayList<>();
    private ArrayList<ProspectPreViewItemData> allList = new ArrayList<>();
    private  void initItemFromJson(String caseId) {
        withCaseList.clear();
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + caseId + "'", "sort asc");
        for (TemplateSort templateSort : list) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(templateSort.getFatherKey());
            data.setName(templateSort.getFatherValue());
            withCaseList.add(data);
        }
        match();
    }

    private void initItemFromDb(String templateId) {
        List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "templateId = '" + templateId + "' and templateType = '0' and templateLevel = '1'", "positionSort asc");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(commonTemplateDetail.getTableName());
            data.setName(commonTemplateDetail.getSceneName());
            withCaseList.add(data);
        }
        match();
    }

    private void match() {
        //与所有item匹配配置
        for (int i = 0; i < withCaseList.size(); i++) {
            ProspectPreViewItemData item = withCaseList.get(i);
            for (ProspectPreViewItemData all : allList) {
                if (item.getField().equals(all.getField())) {
                    withCaseList.remove(i);
                    withCaseList.add(i, all);
                }
            }
        }
    }

    private class SortListAdapter extends BaseAdapter {
        private Activity activity;
        private boolean isSort;
        public SortListAdapter(Activity activity,boolean isSort) {
            super();
            this.activity = activity;
            this.isSort = isSort;
        }

        @Override
        public int getCount() {
            return mSortDatas.length;
        }

        @Override
        public Object getItem(int position) {
            return mSortDatas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.selection_sort_list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.selection_sort_list_item_tv);
            tv.setText(isSort ? mSortDatas[position] : mCateGoryDatas[position]);
            if (isSort ? mSrotSelected.equals(mSortDatas[position]) : mCateGorySelected.equals(mCateGoryDatas[position])) {
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_press));
            } else {
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_normal));
            }
            return convertView;
        }
    }
}