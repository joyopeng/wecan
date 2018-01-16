package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.Area;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.AllNewestStateFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.MoreNewCaseAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.MoreNewCaseAdapter.MoreNewCaseData;
import com.gofirst.scenecollection.evidence.view.customview.PullUpRefreshLayout;
import com.gofirst.scenecollection.evidence.view.customview.SelectionCategoryPop;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class MoreNewCase extends Activity implements View.OnClickListener,SelectionCategoryPop.SelectionPopInterFace{

    private ListView moreNewCaseList;
    private MoreNewCaseAdapter adapter;
    private List<MoreNewCaseData> list = new ArrayList<>();
	private PullUpRefreshLayout refreshLayout;
    private MaterialRefreshLayout materialRefreshLayout;
    private String caseNo = "";
    //search title layout start
    private EditText mSearchContent;
    private ImageView mSearchBtn;
    private TextView title_bar_tv;
    //end

    private View mSelectionLayout;
    private View mLayoutCategory;
    private TextView mBtnCategory;
    private ImageView mImgCategory;
    private View mLayoutSort;
    private TextView mBtnSort;
    private ImageView mImgSort;
    private View mLayoutScreen;
    private TextView mBtnScreen;
    private ImageView mImgScreen;
    private View mSelectionShowLayout;
    //category layout
    private View mSelectionCategoryLayout;
    private Button mCategoryReset;
    private Button mCategorySave;
    private String mBitCategoryValue = "";
    private String mZoneValue = "";
    private GridView mBigCategoryGrid;
    private CategoryAdapter mBigCategoryAdapter;
    private List<CsDicts> mBigCategoryDatas = new ArrayList<>();
    private GridView mZoneGrid;
    private ZoneGridAdapter mZoneAdapter;
    private List<Area> mZoneDatas = new ArrayList<>();
    //sort listview
    private ListView mSelectionSortList;
    private String mSrotSelected = "默认排序";
    private String[] mSortDatas = {"默认排序","时间最近","时间最远"};
    private SortListAdapter mSortListAdapter;

    private HashMap<String,String> mScreenResults = new HashMap<>();
    private final int LAYOUT_NONE_SHOW = -1;
    private final int LAYOUT_CATEGORY_SHOW = 1;
    private final int LAYOUT_SORT_SHOW = 2;
    private int mCurrentLayoutShow = LAYOUT_NONE_SHOW;

    private SelectionCategoryPop mCategoryPop;

    private SharePre mShares;
    private String mOrgId;

    private LinearLayout mSceneNoDetails;
    private TextView mSceneNoDetailsText;

    private int pageNumber=1;
    private int pageSize=10;
    private Boolean pullUpFlag=false;
    private Boolean pullDownFlag=true;

    //本地数据库查询出来的结果
    private List<DbModel> mLocalResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_new_case);
        findViewById(R.id.title_bar_alarm_layout).setVisibility(View.GONE);
        mShares = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        mOrgId = mShares.getString("organizationId","");
        Init();
        //NewestUpdateList("/cases/lasted");
        getData("");
        adapter = new MoreNewCaseAdapter(this,list);
        moreNewCaseList.setAdapter(adapter);
        moreNewCaseList.setOnItemClickListener(mCaseItemClikc);

    }

    @Override
    protected void onPause() {
        if(mCurrentLayoutShow != LAYOUT_NONE_SHOW){
            showCategoryGrid(LAYOUT_NONE_SHOW);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mSelectionShowLayout.getVisibility() == View.VISIBLE){
            showCategoryGrid(LAYOUT_NONE_SHOW);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_selection_category:
                showCategoryGrid(LAYOUT_CATEGORY_SHOW);
                mBitCategoryValue = mScreenResults.get("category_big_type");
                mZoneValue = mScreenResults.get("category_zone");
                mBigCategoryAdapter.setSelectedItem(mBitCategoryValue);
                mBigCategoryAdapter.notifyDataSetChanged();
                mZoneAdapter.setSelectedItem(mZoneValue);
                mZoneAdapter.notifyDataSetChanged();
                break;
            case R.id.layout_selection_sort:
                showCategoryGrid(LAYOUT_SORT_SHOW);
                break;
            case R.id.layout_selection_screen:
                showCategoryGrid(LAYOUT_NONE_SHOW);
                if(mCategoryPop == null){
                    mCategoryPop = new SelectionCategoryPop(this,false,false);
                    mCategoryPop.setSelectionPopInterFace(this);
                }
                mCategoryPop.showAtLocation(MoreNewCase.this.findViewById(R.id.more_new_case_root), Gravity.RIGHT,0,0);
                break;
            case R.id.selection_category_reset_btn:
                mBitCategoryValue = "";
                mBigCategoryAdapter.setSelectedItem(0);
                mBigCategoryAdapter.notifyDataSetChanged();
                mZoneValue = "";
                mZoneAdapter.setSelectedItem(0);
                mZoneAdapter.notifyDataSetChanged();
                mScreenResults.put("category_big_type","");
                mScreenResults.put("category_zone","");
                break;
            case R.id.selection_category_save_btn:
                mScreenResults.put("category_big_type",mBitCategoryValue);
                mScreenResults.put("category_zone",mZoneValue);
                showCategoryGrid(LAYOUT_NONE_SHOW);
                getData(mSearchContent.getText().toString().trim());
                adapter = new MoreNewCaseAdapter(this,list);
                moreNewCaseList.setAdapter(adapter);
                break;
            case R.id.title_bar_search_img:
                String searchString = mSearchContent.getText().toString().trim();
//                getData(searchString);
//                adapter = new MoreNewCaseAdapter(this,list);
//                moreNewCaseList.setAdapter(adapter);
                localSearchClick(searchString);
                break;
        }
    }

    @Override
    public void resetButtonClick(HashMap<String, String> results) {
        Log.i("zhangsh","resetButtonClick");
        mScreenResults.put("screen_date","");
        mScreenResults.put("screen_address","");
        mScreenResults.put("screen_type","");
        mScreenResults.put("screen_employee","");
        getData(mSearchContent.getText().toString().trim());
        adapter = new MoreNewCaseAdapter(this,list);
        moreNewCaseList.setAdapter(adapter);
    }

    @Override
    public void saveButtonClick(HashMap<String, String> results) {
        Log.i("zhangsh","saveButtonClick");
        mScreenResults.put("screen_date",results.get("date"));
        mScreenResults.put("screen_address",results.get("address"));
        mScreenResults.put("screen_type",results.get("scene_type"));
        mScreenResults.put("screen_employee",results.get("employee"));
        getData(mSearchContent.getText().toString().trim());
        adapter = new MoreNewCaseAdapter(this,list);
        moreNewCaseList.setAdapter(adapter);
    }

    private void Init(){
        mSceneNoDetails = (LinearLayout)findViewById(R.id.scene_no_details);
        mSceneNoDetailsText= (TextView)findViewById(R.id.scene_no_details_text);

        title_bar_tv=(TextView) findViewById(R.id.title_bar_tv);
        title_bar_tv.setText("警情");
        findViewById(R.id.title_bar_alarm).setVisibility(View.GONE);
        findViewById(R.id.title_bar_add_layout).setVisibility(View.GONE);
        //search title layout
        mSearchContent = (EditText) findViewById(R.id.title_bar_search_edit);
        mSearchBtn = (ImageView) findViewById(R.id.title_bar_search_img);
        mSearchBtn.setOnClickListener(this);

        moreNewCaseList=(ListView)findViewById(R.id.more_new_case_list);
        //selection start
        mSelectionLayout = findViewById(R.id.layout_selection);
        mLayoutCategory = findViewById(R.id.layout_selection_category);
        mLayoutCategory.setOnClickListener(this);
        mLayoutSort = findViewById(R.id.layout_selection_sort);
        mLayoutSort.setOnClickListener(this);
        mLayoutScreen = findViewById(R.id.layout_selection_screen);
        mLayoutScreen.setOnClickListener(this);
        mImgCategory = (ImageView) findViewById(R.id.img_selection_category);
        mImgSort = (ImageView) findViewById(R.id.img_selection_sort);
        mImgScreen = (ImageView) findViewById(R.id.img_selection_screen);
        mBtnCategory = (TextView) findViewById(R.id.btn_selection_category);
        mBtnSort = (TextView) findViewById(R.id.btn_selection_sort);
        mBtnScreen = (TextView)findViewById(R.id.btn_selection_screen);
        mSelectionShowLayout = findViewById(R.id.selection_layout);
        mSelectionShowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showCategoryGrid(LAYOUT_NONE_SHOW);
                return true;
            }
        });
        //category
        mBigCategoryDatas = EvidenceApplication.db.findAllByWhere(CsDicts.class,"rootKey = \"GASSLAJ\"","dictSort asc");
        List<User> userList=EvidenceApplication.db.findAllByWhere(User.class, "userNameId = '" + mShares.getString("user_id", "") +"'");
        if(userList.size()>0){
            if(userList.get(0).getPermissionSetting()!=null&&userList.get(0).getPermissionSetting().length()>0){
                String orgIdTemp[] = userList.get(0).getPermissionSetting().toString().split(",");
                for(int i =0;i<orgIdTemp.length;i++) {
                    mZoneDatas.addAll(EvidenceApplication.db.findAllByWhere(Area.class, " compartmentNo = (select compartmentNo from HyOrganizations where organizationId = \"" + orgIdTemp[i] + "\") "));
                }
            }else{
                mZoneDatas = EvidenceApplication.db.findAllByWhere(Area.class," compartmentNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId +  "\") or compartmentUpNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId +  "\")");
            }
        }
        //mZoneDatas = EvidenceApplication.db.findAllByWhere(Area.class," compartmentNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId +  "\") or compartmentUpNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId +  "\")");
        if(mZoneDatas.size() > 0) {
            Area area = new Area();
            area.setCompartmentName("不限");
            area.setCompartmentNo("");
            //mZoneDatas.set(0, area);
            mZoneDatas.add(0,area);
        }
        mSelectionCategoryLayout = findViewById(R.id.selection_category_scroll);
        mCategoryReset = (Button) findViewById(R.id.selection_category_reset_btn);
        mCategoryReset.setOnClickListener(this);
        mCategorySave = (Button) findViewById(R.id.selection_category_save_btn);
        mCategorySave.setOnClickListener(this);
        mBigCategoryGrid = (GridView)findViewById(R.id.selection_big_category_grid);
        mBigCategoryAdapter = new CategoryAdapter(this);
        mBigCategoryGrid.setAdapter(mBigCategoryAdapter);
        mZoneGrid = (GridView) findViewById(R.id.selection_zone_grid);
        mZoneAdapter = new ZoneGridAdapter(this);
        mZoneGrid.setAdapter(mZoneAdapter);

        //sort ListView
        mSelectionSortList = (ListView) findViewById(R.id.selection_sort_list);
        mSortListAdapter = new SortListAdapter(this);
        mSelectionSortList.setAdapter(mSortListAdapter);
        mSelectionSortList.setOnItemClickListener(mSortListItemClick);
        //selection end
        initSearchData();
		
		
		moreNewCaseList=(ListView)findViewById(R.id.more_new_case_list);
        //refreshLayout = (PullUpRefreshLayout)findViewById(R.id.refresh_layout);
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
        materialRefreshLayout.setLoadMore(true);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //if (isNetworkAvailable(getActivity())) {
                pullUpFlag=true;
                pullDownFlag=false;
                   NewestUpdateList("/cases/lasted");
                //} else {
                materialRefreshLayout.finishRefresh();
                // Toast.makeText(getActivity(), "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                // }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                pullUpFlag=false;
                pullDownFlag=true;
                //getMoreData();
                NewestUpdateList("/cases/lasted");
                adapter.notifyDataSetChanged();

            }
        });

		// 结束下拉刷新...
		materialRefreshLayout.finishRefresh();

        // 结束上拉刷新...
        materialRefreshLayout.finishRefreshLoadMore();
    }

    public void initSearchData(){
        mScreenResults.put("category_big_type","");
        mScreenResults.put("category_zone","");
        mScreenResults.put("sort","");
        mScreenResults.put("screen_date","");
        mScreenResults.put("screen_address","");
        mScreenResults.put("screen_type","");
        mScreenResults.put("screen_employee","");

    }

    private List<DbModel> getDatas(String search){
        List<DbModel> results = null;
        StringBuilder sql = new StringBuilder();

        StringBuilder where = new StringBuilder();
        String zone = mScreenResults.get("category_zone");
        if(zone != null && !"".equals(zone)){
            where.append(" CsSceneCases.sceneRegionalism = \"" + zone + "\" and");
        }
        //screen
        String date = mScreenResults.get("screen_date");
        if(date != null && !"".equals(date)){
            where.append(" CsSceneCases.alarmDatetime > \"" + date+" 00:00:00" + "\" and CsSceneCases.alarmDatetime < \"" + date+" 59:59:59" + "\"");
            where.append(" and");
        }
        String address = mScreenResults.get("screen_address");
        if(address != null && !"".equals(address)){
            where.append(" CsSceneCases.alarmAddress like \"%" + address + "%\" and");
        }
        /*String type = mScreenResults.get("screen_type");
        if(type != null && !"".equals(type)){
            where.append(" CsSceneCases.alarmType like \"%" + type + "%\" and");
        }*/
        String employee = mScreenResults.get("screen_employee");
        if(employee != null && !"".equals(employee)){
            where.append(" CsSceneCases.alarmPeople like \"%" + employee + "%\"");
        }
        if(!"".equals(search)){
            where.append(" (CsSceneCases.alarmPeople like \"%" + search + "%\" or CsSceneCases.alarmTel like \"%" + search + "%\" or CsSceneCases.alarmAddress like \"%" + search + "%\"" +
                    " or CsSceneCases.receptionNo like '%" + search + "%')");
                    //"%\" or CsSceneCases.alarmType like \"%" + search + "%\" or CsSceneCases.alarmAddress like \"%" + search + "%\")");
        }
        int len = where.lastIndexOf(" and");
        if(len > 0 && where.length() - len <= 4){
            where = new StringBuilder(where.substring(0,len));
        }
        if(where.length() > 0){
            //category
            String bigCategory = mScreenResults.get("category_big_type");
            if(bigCategory != null && !"".equals(bigCategory)){
                sql.append("select CsSceneCases.* from CsSceneCases left join CsDictsConjunction on CsSceneCases.alarmType = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" and ");
            }else{
                sql.append("select CsSceneCases.* from CsSceneCases where ");
            }
            sql.append(where.toString());
        }else{
            String bigCategory = mScreenResults.get("category_big_type");
            if(bigCategory != null && !"".equals(bigCategory)){
                sql.append("select CsSceneCases.* from CsSceneCases left join CsDictsConjunction on CsSceneCases.alarmType = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" ");
            }else{
                sql.append("select CsSceneCases.* from CsSceneCases ");
            }
        }
        //sort
        String order = mScreenResults.get("sort");
        if(order == null || "".equals(order)){
            results = EvidenceApplication.db.findDbModelListBySQL(sql.toString()+ " order by CsSceneCases.alarmDatetime desc" + order);
        }else {
            results = EvidenceApplication.db.findDbModelListBySQL(sql.toString() + " order by CsSceneCases.alarmDatetime " + order);
        }
        return results;
    }

    public void getData(String search){
        MoreNewCaseData moreNewCaseData;
        //List<CsSceneCases> resultList= EvidenceApplication.db.findAll(CsSceneCases.class);
        List<DbModel> resultList = getDatas(search);
        list.clear();
        Log.d("resultList", "" + resultList.size());
        for(int j=0;j<resultList.size();j++) {
            if(resultList.get(j).getString("status").equals("0")) {
                moreNewCaseData = new MoreNewCaseData();

                moreNewCaseData.setId(resultList.get(j).getString("id"));
                moreNewCaseData.setName(resultList.get(j).getString("alarmPeople"));
                moreNewCaseData.setMarkOrPhone(resultList.get(j).getString("alarmTel"));
                moreNewCaseData.setExposureProcess(resultList.get(j).getString("exposureProcess"));
                moreNewCaseData.setState(resultList.get(j).getString("status"));
                //moreNewCaseData.setCrackedDate(resultList.get(j).getString("alarmDatetime"));
                moreNewCaseData.setAlarmDatetime(resultList.get(j).getString("alarmDatetime"));
                moreNewCaseData.setSceneRegionalismName(resultList.get(j).getString("sceneRegionalismName"));
                moreNewCaseData.setAlarmAddress(resultList.get(j).getString("alarmAddress"));
                moreNewCaseData.setRemark(resultList.get(j).getString("remark"));
                moreNewCaseData.setReceptionNo(resultList.get(j).getString("receptionNo"));
                list.add(moreNewCaseData);
            }
        }
        if(list.size()==0){
            mSceneNoDetails.setVisibility(View.VISIBLE);
            mSceneNoDetailsText.setText("未查询到符合条件的信息！");
        }else{
            mSceneNoDetails.setVisibility(View.GONE);
        }
    }

    //////////////////////////****** category gridview *******///////////////////////////////
    //big category
    private class CategoryAdapter extends BaseAdapter{
        private Context mContext;
        private int selected;
        public CategoryAdapter(Context context){
            this.mContext = context;
        }

        public void setSelectedItem(int selected){
            this.selected = selected;
        }

        public void setSelectedItem(String value){
            int lenth = mBigCategoryDatas.size();
            if(value == null){
                this.selected = 0;
                return;
            }
            for (int i = 0;i < lenth;i++){
                String s = mBigCategoryDatas.get(i).getDictKey();
                if(value.equals(s)){
                    this.selected = i;
                    break;
                }else if("".equals(value)){
                    this.selected = 0;
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return mBigCategoryDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mBigCategoryDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_pop_item, null);
            Button button = (Button) convertView.findViewById(R.id.selection_pop_item_tv);
            button.setText("GASSLAJ".equals(mBigCategoryDatas.get(position).getDictKey()) ? "不限" : mBigCategoryDatas.get(position).getDictValue1());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = position;
                    mBitCategoryValue = mBigCategoryDatas.get(position).getDictKey();
                    if("GASSLAJ".equals(mBitCategoryValue)){
                        mBitCategoryValue = "";
                    }
                    notifyDataSetChanged();
                }
            });
            if(selected == position){
                /*button.setBackgroundResource(R.drawable.selection_category_item_selected);
                button.setTextColor(getResources().getColor(R.color.common_btn_bg_normal_color));*/
                button.setBackgroundResource(R.drawable.selection_grid_item_press_bg);
                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_press_color));
            }else{
                /*button.setBackgroundResource(R.drawable.selection_screen_item_normal);
                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_color));*/
                button.setBackgroundResource(R.drawable.selection_grid_item_normal_bg);
                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
            }
            return convertView;
        }
    }

    //zone gridview
    private class ZoneGridAdapter extends BaseAdapter{
        private Context mContext;
        private int selection = 0;
        public ZoneGridAdapter(Context context){
            this.mContext = context;
        }

        public void setSelectedItem(int select){
            this.selection = select;
        }

        public void setSelectedItem(String value){
            int lenth = mZoneDatas.size();
            if(value == null){
                this.selection = 0;
                return;
            }
            for (int i = 0;i < lenth;i++){
                String s = mZoneDatas.get(i).getCompartmentNo();
                if(value.equals(s)){
                    this.selection = i;
                    break;
                }else if("".equals(value)){
                    this.selection = 0;
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return mZoneDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mZoneDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_pop_item,null);
            Button btn = (Button)convertView.findViewById(R.id.selection_pop_item_tv);
            btn.setText(mZoneDatas.get(position).getCompartmentName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection = position;
                    mZoneValue = mZoneDatas.get(position).getCompartmentNo();
                    notifyDataSetChanged();
                }
            });
            if(selection == position){
                /*btn.setBackgroundResource(R.drawable.selection_category_item_selected);
                btn.setTextColor(getResources().getColor(R.color.common_btn_bg_normal_color));*/
                btn.setBackgroundResource(R.drawable.selection_grid_item_press_bg);
                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_press_color));
            }else{
                /*btn.setBackgroundResource(R.drawable.selection_screen_item_normal);
                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_color));*/
                btn.setBackgroundResource(R.drawable.selection_grid_item_normal_bg);
                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
            }
            return convertView;
        }
    }

    private void showCategoryGrid(int show){
        if(mCurrentLayoutShow == show){
            mSelectionShowLayout.setVisibility(View.GONE);
            mCurrentLayoutShow = LAYOUT_NONE_SHOW;
        }else{
            mCurrentLayoutShow = show;
            mSelectionSortList.setVisibility(mCurrentLayoutShow == LAYOUT_SORT_SHOW ? View.VISIBLE : View.GONE);
            mSelectionCategoryLayout.setVisibility(mCurrentLayoutShow == LAYOUT_CATEGORY_SHOW ? View.VISIBLE : View.GONE);
            mSelectionShowLayout.setVisibility(mCurrentLayoutShow == LAYOUT_NONE_SHOW ? View.GONE : View.VISIBLE);
        }
    }

    //////////////////////////****** sort listview *******///////////////////////////////
    private class SortListAdapter extends BaseAdapter{
        private Activity activity;
        public SortListAdapter(Activity activity) {
            super();
            this.activity = activity;
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.selection_sort_list_item,null);
            TextView tv = (TextView) convertView.findViewById(R.id.selection_sort_list_item_tv);
            tv.setText(mSortDatas[position]);
            if(mSrotSelected.equals(mSortDatas[position])){
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_press));
            }else{
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_normal));
            }
            return convertView;
        }
    }

    public AdapterView.OnItemClickListener mSortListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showCategoryGrid(LAYOUT_NONE_SHOW);
            if(mSortDatas[position].equals(mSrotSelected)){
                return;
            }
            mSrotSelected = mSortDatas[position];
            mSortListAdapter.notifyDataSetChanged();
            switch (position){
                case 0:
                    mScreenResults.put("sort","");
                    break;
                case 1:
                       mScreenResults.put("sort", "desc");
                    break;
                case 2:
                    mScreenResults.put("sort","asc");
                    break;
                default:
                    mScreenResults.put("sort","");
                    break;
            }
            getData(mSearchContent.getText().toString().trim());
            adapter = new MoreNewCaseAdapter(MoreNewCase.this,list);
            moreNewCaseList.setAdapter(adapter);
        }
    };

    private void NewestUpdateList(String MethodName){
        StringMap params = new StringMap();

        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("pageNumber", String.valueOf(pageNumber));
        params.putString("pageSize",String.valueOf(pageSize));
        params.putString("token", mShares.getString("token", ""));
        Log.d("pageNumber", pageNumber + "");
        Log.d("pageSize",pageSize+"");

        Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = sDateFormat.format(new java.util.Date());
                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {
                        AllNewestStateFragmentAdapter.AllNewestStateFragmentData allNewestStateFragmentData;
                        ;
                        JSONArray jsonArray = response
                                .getJSONArray("data");
                        JSONObject jsonObjectdata;
                        Log.d("testcomplete", "" + jsonArray);
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjectdata = jsonArray.getJSONObject(i);
                            caseNo = jsonObjectdata.getString("id");
                            Log.d("id", caseNo);
                            if (jsonObjectdata.getString("alarmNo").equals("J3205055616112900201")){
                                Log.d("ss","");
                            }
                            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                    "receptionNo = '" + jsonObjectdata.getString("alarmNo") + "' and isManualAddCase = '1'");
                            if (list != null && list.size() > 0) {
                                CsSceneCases csSceneCases = list.get(0);
                                if (TextUtils.equals(jsonObjectdata.getString("status") , "0")){
                                    String oldId = csSceneCases.getId();
                                    csSceneCases.setId(caseNo);
                                    EvidenceApplication.db.deleteById(CsSceneCases.class, oldId);
                                    EvidenceApplication.db.save(csSceneCases);
                                    updateStatus(caseNo);
                                }
                                continue;
                            }
                            CsSceneCases sceneAlarm = new CsSceneCases();

                            sceneAlarm.setId(jsonObjectdata.getString("id"));
                            if(jsonObjectdata.has("alarmPeople")) {
                                sceneAlarm.setAlarmPeople(jsonObjectdata.getString("alarmPeople"));
                            }
                            sceneAlarm.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
                            sceneAlarm.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
                            sceneAlarm.setSceneRegionalismName(jsonObjectdata.getString("sceneRegionalismName"));
                            sceneAlarm.setAlarmTel(jsonObjectdata.getString("alarmTel"));
                            sceneAlarm.setAlarmDatetime(jsonObjectdata.getString("alarmDatetime"));
                            sceneAlarm.setSortListDateTime(jsonObjectdata.getString("alarmDatetime"));
                            sceneAlarm.setAlarmAddress(jsonObjectdata.getString("alarmAddress"));
                            sceneAlarm.setRemark(jsonObjectdata.getString("remark"));
                            sceneAlarm.setReceptionNo(jsonObjectdata.getString("alarmNo"));
                            sceneAlarm.setAlarmCategory(jsonObjectdata.getString("alarmCategory"));
                            sceneAlarm.setAlarmCategoryName(jsonObjectdata.getString("alarmCategoryName"));
                            if(jsonObjectdata.has("updateUser")) {
                                sceneAlarm.setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                            }

                            sceneAlarm.setStatus(jsonObjectdata.getString("status"));

                            List<CsSceneCases> SceneCaselist = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                    "id = '" + caseNo + "'");
                            if (SceneCaselist.size() == 0) {
                                Log.d("SceneCaselist", "save");
                                if(jsonObjectdata.getString("status")!=null&&
                                        jsonObjectdata.getString("status").equals("0")) {
                                    EvidenceApplication.db.save(sceneAlarm);
                                }

                            } else {
                                Log.d("SceneCaselist", "update");
                               // if (SceneCaselist.get(0).getStatus().equals("0")) {
                               //     EvidenceApplication.db.update(sceneAlarm);
                              //  } else if (SceneCaselist.get(0).getStatus().equals("1")) {
                                if ("0".equals(SceneCaselist.get(0).getStatus())) {
                                    sceneAlarm.setStatus("1");
                                    SceneCaselist.get(0).setStatus(jsonObjectdata.getString("status"));
                                    if (jsonObjectdata.getString("updateUser") != null && jsonObjectdata.has("updateUser")) {
                                        SceneCaselist.get(0).setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                                    }
                                    EvidenceApplication.db.update(SceneCaselist.get(0));
                                }else {

                                }
                              //  }
                            }
                        }
                        /*pageNumber
                        pageSize

                        pullUpFlag=false;
                         pullDownFlag=false;

                        */
                        if(pullDownFlag){
                            if(jsonArray.length()<pageSize){

                            }else{
                                pageNumber=pageNumber+1;
                            }
                            pullUpFlag=false;
                        }
                        if(pullUpFlag){
                            pageNumber=1;
                            pullDownFlag=false;
                        }


                        if(jsonArray.length()>0) {
                            mSceneNoDetails.setVisibility(View.GONE);
                        }else if(jsonArray.length()==0){
                            mSceneNoDetails.setVisibility(View.VISIBLE);
                            mSceneNoDetailsText.setText("暂无最新数据！");
                        }
                        getData(mSearchContent.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                        //materialRefreshLayout.finishRefresh();

                    } else {

                        Toast.makeText(MoreNewCase.this, response
                                        .getJSONArray("data").toString(),
                                Toast.LENGTH_SHORT).show();
                        //refreshLayout.setRefreshing(false);
                    }
                    materialRefreshLayout.finishRefreshLoadMore();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
                mSceneNoDetails.setVisibility(View.VISIBLE);
                mSceneNoDetailsText.setText("加载失败，请检查网络！");
                Utils.stopProgressDialog();
            }
        });

    }

    public void onResume(){
        //getData("");
        //adapter.notifyDataSetChanged();
        if(list.size() == 0) {
            Utils.startProgressDialog(this, "", "请稍后...", false, false);
            NewestUpdateList("/cases/lasted");
        }
        super.onResume();
    }

    /**
     * case list item click
     * */
    private AdapterView.OnItemClickListener mCaseItemClikc = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MoreNewCase.this, NewestStateDetail.class);
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
        }
    };

    private void updateStatus(String caseId){
        StringMap params = new StringMap();

        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("id",caseId);
        params.putString("user",mShares.getString("userId", ""));
        params.putString("token", mShares.getString("token", ""));


        Netroid.PostHttp("/update/staus", params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = sDateFormat.format(new java.util.Date());
                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //   Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
                //    Utils.stopProgressDialog();
            }
        });

    }

    private void localSearchClick(String search){
        Utils.startProgressDialog(this, "", "请稍后...", false, false);
        mLocalResults = getDatas(search);
        int count = mLocalResults.size();
        StringMap params = new StringMap();

        params.putString("userId", mShares.getString("user_id",""));
        String zone = mScreenResults.get("category_zone");
        params.putString("sceneRegionalism", zone == null ? "" : zone);
        String date = mScreenResults.get("screen_date");
        params.putString("alarmDatetime", date == null ? "" : date);
        String address = mScreenResults.get("screen_address");
        params.putString("alarmAddress",address == null ? "" : address);
        params.putString("search", search == null ? "" : search);
        params.putString("alarmCount", String.valueOf(count));

        Netroid.PostHttp("/cases/searchAlarms", params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                //SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                //String date = sDateFormat.format(new java.util.Date());
                Log.d("responsecomplete", "" + response);
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        //JSONArray jsonArray = response.getJSONArray("data");
                        JSONObject jsonObjectdata;
                        Log.d("testcomplete", "" + jsonArray);
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjectdata = jsonArray.getJSONObject(i);
                            caseNo = jsonObjectdata.getString("id");
                            Log.d("id", caseNo);
                            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                    "receptionNo = '" + jsonObjectdata.getString("alarmNo") + "' and isManualAddCase = '1'");
                            if (list != null && list.size() > 0) {
                                CsSceneCases csSceneCases = list.get(0);
                                if (TextUtils.equals(jsonObjectdata.getString("status") , "0")){
                                    String oldId = csSceneCases.getId();
                                    csSceneCases.setId(caseNo);
                                    EvidenceApplication.db.deleteById(CsSceneCases.class, oldId);
                                    EvidenceApplication.db.save(csSceneCases);
                                    updateStatus(caseNo);
                                }
                                continue;
                            }
                            CsSceneCases sceneAlarm = new CsSceneCases();
                            sceneAlarm.setId(jsonObjectdata.getString("id"));
                            if(jsonObjectdata.has("alarmPeople")) {
                                sceneAlarm.setAlarmPeople(jsonObjectdata.getString("alarmPeople"));
                            }
                            sceneAlarm.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
                            sceneAlarm.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
                            sceneAlarm.setSceneRegionalismName(jsonObjectdata.getString("sceneRegionalismName"));
                            sceneAlarm.setAlarmTel(jsonObjectdata.getString("alarmTel"));
                            sceneAlarm.setAlarmDatetime(jsonObjectdata.getString("alarmDatetime"));
                            sceneAlarm.setSortListDateTime(jsonObjectdata.getString("alarmDatetime"));
                            sceneAlarm.setAlarmAddress(jsonObjectdata.getString("alarmAddress"));
                            sceneAlarm.setRemark(jsonObjectdata.getString("remark"));
                            sceneAlarm.setReceptionNo(jsonObjectdata.getString("alarmNo"));
                            sceneAlarm.setAlarmCategory(jsonObjectdata.getString("alarmCategory"));
                            sceneAlarm.setAlarmCategoryName(jsonObjectdata.getString("alarmCategoryName"));
                            if(jsonObjectdata.has("updateUser")) {
                                sceneAlarm.setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                            }
                            sceneAlarm.setStatus(jsonObjectdata.getString("status"));

                            List<CsSceneCases> SceneCaselist = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                    "id = '" + caseNo + "'");
                            SceneCaselist.size();
                            if (SceneCaselist.size() == 0) {
                                Log.d("SceneCaselist", "save");
                                if(jsonObjectdata.getString("status")!=null&&
                                        jsonObjectdata.getString("status").equals("0")) {
                                    EvidenceApplication.db.save(sceneAlarm);
                                }
                            } else {
                                Log.d("SceneCaselist", "update");
                                if ("0".equals(SceneCaselist.get(0).getStatus())) {
                                    sceneAlarm.setStatus("1");
                                    SceneCaselist.get(0).setStatus(jsonObjectdata.getString("status"));
                                    if (jsonObjectdata.getString("updateUser") != null && jsonObjectdata.has("updateUser")) {
                                        SceneCaselist.get(0).setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                                    }
                                    EvidenceApplication.db.update(SceneCaselist.get(0));
                                }else {}
                            }
                        }
                        getData(mSearchContent.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                    } else {
                        getDataFromLocal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getDataFromLocal();
                }
                Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "localSearchClick " + error);
                getDataFromLocal();
                Utils.stopProgressDialog();
            }
        });
    }

    private void getDataFromLocal(){
        int length = mLocalResults.size();
        list.clear();
        if(mLocalResults != null && length > 0){
            MoreNewCaseData moreNewCaseData;
            Log.d("resultList", "getDataFromLocal : " + length);
            DbModel dbModel = null;
            for(int j=0;j < length;j++) {
                if(mLocalResults.get(j).getString("status").equals("0")) {
                    moreNewCaseData = new MoreNewCaseData();
                    dbModel = mLocalResults.get(j);
                    moreNewCaseData.setId(dbModel.getString("id"));
                    moreNewCaseData.setName(dbModel.getString("alarmPeople"));
                    moreNewCaseData.setMarkOrPhone(dbModel.getString("alarmTel"));
                    moreNewCaseData.setExposureProcess(dbModel.getString("exposureProcess"));
                    moreNewCaseData.setState(dbModel.getString("status"));
                    //moreNewCaseData.setCrackedDate(resultList.get(j).getString("alarmDatetime"));
                    moreNewCaseData.setAlarmDatetime(dbModel.getString("alarmDatetime"));
                    moreNewCaseData.setSceneRegionalismName(dbModel.getString("sceneRegionalismName"));
                    moreNewCaseData.setAlarmAddress(dbModel.getString("alarmAddress"));
                    moreNewCaseData.setRemark(dbModel.getString("remark"));
                    moreNewCaseData.setReceptionNo(dbModel.getString("receptionNo"));
                    list.add(moreNewCaseData);
                }
            }
            adapter.notifyDataSetChanged();
            if(list.size()==0){
                mSceneNoDetails.setVisibility(View.VISIBLE);
                mSceneNoDetailsText.setText("未查询到符合条件的信息！");
            }else{
                mSceneNoDetails.setVisibility(View.GONE);
            }
        }
        adapter.notifyDataSetChanged();
        if(list.size()==0){
            mSceneNoDetails.setVisibility(View.VISIBLE);
            mSceneNoDetailsText.setText("未查询到符合条件的信息！");
        }else{
            mSceneNoDetails.setVisibility(View.GONE);
        }
    }
}
