package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.Area;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.CaseHistory;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.FindCsSceneCases;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.SceneProspectListAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.SelectionCategoryPop;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */
public class InquestFind extends Activity implements View.OnClickListener, MainActivity.SearchTitleClick, SelectionCategoryPop.SelectionPopInterFace {

    private final String TAG = "SceneProspectFragment";
    private String mEmployeeName;
    private String mEmployeeId;

    private View mFragmentView;
    //private PullUpRefreshLayout mPullUpRefreshLayout;
    private MaterialRefreshLayout mPullUpRefreshLayout;
    private ListView mSceneProspectListView;
    private List<DbModel> mDataLists = new ArrayList<>();
    //历史数据添加 start
    private List<CaseHistory> mHistoryList = new ArrayList<>();
    //历史数据添加 end
    private SceneProspectListAdapter mAdapter;
    private View mSceneNoDetails; //20161125

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
    private Button mIsEmployeeAll;
    private Button mIsEmployeeYes;
    private Button mIsEmployeeNo;
    private Button mIsOverAll;
    private Button mIsOverYes;
    private Button mIsOverNo;
    private Button mCategoryReset;
    private Button mCategorySave;
    private String mBitCategoryValue = "";
    private String mZoneValue = "";
    private String mIsEmployee = "";
    private String mIsOver = "";
    private GridView mBigCategoryGrid;
    private CategoryAdapter mBigCategoryAdapter;
    private List<CsDicts> mBigCategoryDatas = new ArrayList<>();
    private GridView mZoneGrid;
    private ZoneGridAdapter mZoneAdapter;
    private List<Area> mZoneDatas = new ArrayList<>();
    //sort listview
    private ListView mSelectionSortList;
    private String mSrotSelected = "默认排序";
    private String[] mSortDatas = {"默认排序", "时间最近", "时间最远"};
    private SortListAdapter mSortListAdapter;

    private HashMap<String, String> mScreenResults = new HashMap<>();
    private final int LAYOUT_NONE_SHOW = -1;
    private final int LAYOUT_CATEGORY_SHOW = 1;
    private final int LAYOUT_SORT_SHOW = 2;
    private int mCurrentLayoutShow = LAYOUT_NONE_SHOW;

    private SelectionCategoryPop mCategoryPop;

    private SharePre mShares;
    private String mOrgId;
    //search title layout
    EditText mSearchEdit;

    //历史数据分页加载条数
    private final int COUNT_HISTORY_PAGING = 10;

    private long mStartTime = 0;

    //勘验结束
    private ImageView secondary_back_img,title_bar_search_img;
    private TextView secondary_title_tv;
    //private EditText title_bar_search_edit;
    private String templateIdTemp="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_inquest);

        mStartTime = System.currentTimeMillis();
        mShares = new SharePre(InquestFind.this, "user_info", Context.MODE_PRIVATE);
        mEmployeeName = mShares.getString("prospectPerson", "");
        mEmployeeId = mShares.getString("userId", "");
        mOrgId = mShares.getString("organizationId", "");
        Log.i(TAG, "onCreate end time = " + (System.currentTimeMillis() - mStartTime) + ";mStart time = " + mStartTime);
        List<CommonTemplate> commonTemplateList = EvidenceApplication.db.findAllByWhere(CommonTemplate.class,
                "caseTypeCode = '-1' ");
        if(commonTemplateList.size()>0){
            templateIdTemp = commonTemplateList.get(0).getSid().toString();
        }
        initView();
        mPullUpRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.scene_prospect_list_layout);
        mSceneProspectListView = (ListView) findViewById(R.id.scene_prospect_list);
        mSearchEdit = (EditText) findViewById(R.id.title_bar_search_edit);
        //will be delete
        getAllData();
        mPullUpRefreshLayout.setLoadMore(true);
        mPullUpRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mPullUpRefreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                Log.i("zhangsh", "onLoad");
                //initListData(true, false);

                if (mDataLists.size() + mHistoryList.size() > mDataLists.size() + mHistoryList.size()) {
                    mSceneProspectListView.setSelection(mDataLists.size() + mHistoryList.size() - COUNT_HISTORY_PAGING);
                } else {
                    mSceneProspectListView.setSelection(mDataLists.size() + mHistoryList.size());
                }
                mPullUpRefreshLayout.finishRefreshLoadMore();
            }
        });

    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "onCreateView start time = " + (System.currentTimeMillis() - mStartTime));
        View view = inflater.inflate(R.layout.scene_prospect_list, null);
        initView(view);
        mPullUpRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.scene_prospect_list_layout);
        mSceneProspectListView = (ListView) view.findViewById(R.id.scene_prospect_list);
        mSearchEdit = (EditText) getActivity().findViewById(R.id.title_bar_search_edit);
        //will be delete
        getAllData();
        mPullUpRefreshLayout.setLoadMore(true);
        mPullUpRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mPullUpRefreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                Log.i("zhangsh", "onLoad");
                initListData(true, false);

                if (mDataLists.size() + mHistoryList.size() > mDataLists.size() + mHistoryList.size()) {
                    mSceneProspectListView.setSelection(mDataLists.size() + mHistoryList.size() - COUNT_HISTORY_PAGING);
                } else {
                    mSceneProspectListView.setSelection(mDataLists.size() + mHistoryList.size());
                }
                mPullUpRefreshLayout.finishRefreshLoadMore();
            }
        });
        Log.i(TAG, "onCreateView end time = " + (System.currentTimeMillis() - mStartTime));
        return view;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume start time = " + (System.currentTimeMillis() - mStartTime));
        //initListData(true, true);
        Log.i(TAG, "onResume end time = " + (System.currentTimeMillis() - mStartTime));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void resetButtonClick(HashMap<String, String> results) {
        Log.i("zhangsh", "resetButtonClick");
        mScreenResults.put("screen_date", "");
        mScreenResults.put("screen_address", "");
        mScreenResults.put("screen_type", "");
        mScreenResults.put("screen_employee", "");
      //  initListData(true, true);
    }

    @Override
    public void saveButtonClick(HashMap<String, String> results) {
        Log.i("zhangsh", "saveButtonClick");
        mScreenResults.put("screen_date", results.get("date"));
        mScreenResults.put("screen_address", results.get("address"));
        mScreenResults.put("screen_type", results.get("scene_type"));
        mScreenResults.put("screen_employee", results.get("employee"));
       // initListData(true, true);
    }

    /**
     * search title click start
     */
    @Override
    public void searchBtnClick(String key) {
            /*if(mSearchEdit != null) {
                mDataLists = getDatas(mSearchEdit.getText().toString().toString());
            }else{
                mDataLists = getDatas("");
            }
            mAdapter.notifyDataSetChanged();*/
    //    initListData(true, true);
    }

    @Override
    public void backBtnClick() {

    }

    /**
     * search title click end
     */

    private void initView() {
        mSearchEdit=(EditText) findViewById(R.id.title_bar_search_edit);
        secondary_back_img=(ImageView) findViewById(R.id.secondary_back_img);
        title_bar_search_img=(ImageView) findViewById(R.id.title_bar_search_img);
        secondary_title_tv=(TextView) findViewById(R.id.secondary_title_tv);
        secondary_title_tv.setText("勘验查询");
        secondary_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_bar_search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFindList("/history/investigateList");
                initListData(true, true);
            }
        });



        mSceneNoDetails = findViewById(R.id.scene_no_details);
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
        mBtnScreen = (TextView) findViewById(R.id.btn_selection_screen);
        mSelectionShowLayout = findViewById(R.id.selection_layout);
        mSelectionShowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showCategoryGrid(LAYOUT_NONE_SHOW);
                return true;
            }
        });
        //category
        mBigCategoryDatas = EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = \"GASSLAJ\"", "dictSort asc");
        mZoneDatas = EvidenceApplication.db.findAllByWhere(Area.class, " compartmentNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId + "\") or compartmentUpNo = (select compartmentNo from HyOrganizations where organizationId = \"" + mOrgId + "\")");
        if (mZoneDatas.size() != 0) {
            Area area = new Area();
            area.setCompartmentName("不限");
            area.setCompartmentNo("");
            mZoneDatas.add(0, area);
        }
        mSelectionCategoryLayout = findViewById(R.id.selection_category_scroll);
        mIsEmployeeAll = (Button) findViewById(R.id.selection_is_employee_all_btn);
        mIsEmployeeAll.setOnClickListener(this);
        mIsEmployeeYes = (Button) findViewById(R.id.selection_is_employee_yes_btn);
        mIsEmployeeYes.setOnClickListener(this);
        mIsEmployeeNo = (Button) findViewById(R.id.selection_is_employee_no_btn);
        mIsEmployeeNo.setOnClickListener(this);
        mIsOverAll = (Button) findViewById(R.id.selection_is_over_all_btn);
        mIsOverAll.setOnClickListener(this);
        mIsOverYes = (Button) findViewById(R.id.selection_is_over_yes_btn);
        mIsOverYes.setOnClickListener(this);
        mIsOverNo = (Button) findViewById(R.id.selection_is_over_no_btn);
        mIsOverNo.setOnClickListener(this);
        mCategoryReset = (Button) findViewById(R.id.selection_category_reset_btn);
        mCategoryReset.setOnClickListener(this);
        mCategorySave = (Button) findViewById(R.id.selection_category_save_btn);
        mCategorySave.setOnClickListener(this);
        mBigCategoryGrid = (GridView) findViewById(R.id.selection_big_category_grid);
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
        updateIsEmployeeBg(mIsEmployee);
        updateIsOverBg(mIsOver);

       // initListData(true, true);

    }

    public void initSearchData() {
        mScreenResults.put("category_big_type", "");
        mScreenResults.put("category_zone", "");
        mScreenResults.put("category_is_employee", "");
        mScreenResults.put("category_is_over", "");
        mScreenResults.put("sort", "");
        mScreenResults.put("screen_date", "");
        mScreenResults.put("screen_address", "");
        mScreenResults.put("screen_type", "");
        mScreenResults.put("screen_employee", "");
    }

    private List<DbModel> getDatas(String s) {
        List<DbModel> results = null;
        StringBuilder sql = new StringBuilder();

        StringBuilder where = new StringBuilder();
        String zone = mScreenResults.get("category_zone");
        if (zone != null && !"".equals(zone)) {
            where.append(" FindCsSceneCases.sceneRegionalism = \"" + zone + "\" and");
        }
        String isEmployee = mScreenResults.get("category_is_employee");
            /*if(isEmployee != null && "yes".equals(isEmployee)){
                where.append(" FindCsSceneCases.receivePeople like \"%" + mEmployeeName + "%\" and");
            }else if(isEmployee != null && "no".equals(isEmployee)){
                where.append(" FindCsSceneCases.receivePeople not like \"%" + mEmployeeName + "%\" and");
            }*/
            /*if(isEmployee != null && "yes".equals(isEmployee)){
                where.append(" FindCsSceneCases.receivePeopleNum like \"%" + mEmployeeId + "%\" and");
            }else if(isEmployee != null && "no".equals(isEmployee)){
                where.append(" FindCsSceneCases.receivePeopleNum not like \"%" + mEmployeeId + "%\" and");
            }*/
        where.append(" FindCsSceneCases.receivePeopleNum = \"" + mEmployeeId + "\" and");
        String isOver = mScreenResults.get("category_is_over");
        if (isOver != null && "yes".equals(isOver)) {
            where.append(" FindCsSceneCases.status = \"3\" and");
        } else if (isOver != null && "no".equals(isOver)) {
            where.append(" FindCsSceneCases.status != \"3\" and");
        }
        //screen
        String date = mScreenResults.get("screen_date");
        if (date != null && !"".equals(date)) {
            where.append(" FindCsSceneCases.receiveCaseTime > \"" + date + "\" and");
        }
        String address = mScreenResults.get("screen_address");
        if (address != null && !"".equals(address)) {
            where.append(" FindCsSceneCases.sceneDetail like \"%" + address + "%\" and");
        }
        String type = mScreenResults.get("screen_type");
        if (type != null && !"".equals(type)) {
            where.append(" FindCsSceneCases.caseType like \"%" + type + "%\" and");
        }
        String employee = mScreenResults.get("screen_employee");
        if (employee != null && !"".equals(employee)) {
            where.append(" FindCsSceneCases.receivePeople like \"%" + employee + "%\"");
        }
        if (!"".equals(s)) {
            where.append("(FindCsSceneCases.receivePeople like \"%" + s +
                    "%\" or FindCsSceneCases.caseType like \"%" + s + "%\" or FindCsSceneCases.sceneDetail like \"%" + s + "%\"" +
                    " or FindCsSceneCases.receptionNo like '%" + s + "%')");
        }
        int len = where.lastIndexOf(" and");
        if (len > 0 && where.length() - len <= 4) {
            where = new StringBuilder(where.substring(0, len));
        }
        if (where.length() > 0) {
            //category
            String bigCategory = mScreenResults.get("category_big_type");
            if (bigCategory != null && !"".equals(bigCategory)) {
                sql.append("select FindCsSceneCases.* from FindCsSceneCases left join CsDictsConjunction on FindCsSceneCases.caseTypeId = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" and FindCsSceneCases.status != '0' and ");
            } else {
                sql.append("select FindCsSceneCases.* from FindCsSceneCases where FindCsSceneCases.status != '0' and ");
            }
            sql.append(where.toString());
        } else {
            String bigCategory = mScreenResults.get("category_big_type");
            if (bigCategory != null && !"".equals(bigCategory)) {
                sql.append("select FindCsSceneCases.* from FindCsSceneCases left join CsDictsConjunction on FindCsSceneCases.caseTypeId = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" and FindCsSceneCases.status != '0'");
            } else {
                sql.append("select FindCsSceneCases.* from FindCsSceneCases where FindCsSceneCases.status != '0'");
            }
        }
        //sort
        String order = mScreenResults.get("sort");
        if (order == null || "".equals(order)) {
            results = EvidenceApplication.db.findDbModelListBySQL(sql.toString() + "order by receiveCaseTime desc");
        } else {
            results = EvidenceApplication.db.findDbModelListBySQL(sql.toString() + "order by receiveCaseTime " + order);
        }
        //return results;
        results = EvidenceApplication.db.findDbModelListBySQL(" select FindCsSceneCases.* from FindCsSceneCases where 1= 1");
        return  results;
    }

    /**
     * 历史数据添加 查找历史数据
     */
    private List<CaseHistory> getHistoryDatas(String query, boolean needClear) {
        List<CaseHistory> results = new ArrayList<>();
        if (needClear) {
            mHistoryList.clear();
        }
        String isOver = mScreenResults.get("category_is_over");
        if (!"".equals(isOver) && !"yes".equals(isOver)) {
            return results;
        }
        String tableName = "HistoryCase";
        // search sql start
        StringBuilder sql = new StringBuilder();
        StringBuilder where = new StringBuilder();
        String zone = mScreenResults.get("category_zone");
        if (zone != null && !"".equals(zone)) {
            where.append(" HistoryCase.sceneRegionalism = \"" + zone + "\" and");
        }
        String isEmployee = mScreenResults.get("category_is_employee");
            /*if(isEmployee != null && "yes".equals(isEmployee)){
                where.append(" HistoryCase.investigator like \"%" + mEmployeeName + "%\" and");
            }else if(isEmployee != null && "no".equals(isEmployee)){
                where.append(" HistoryCase.investigator not like \"%" + mEmployeeName + "%\" and");
            }*/
        if (isEmployee != null && "yes".equals(isEmployee)) {
            where.append(" HistoryCase.investigatorIds like \"%" + mEmployeeId + "%\" and");
        } else if (isEmployee != null && "no".equals(isEmployee)) {
            where.append(" HistoryCase.investigatorIds not like \"%" + mEmployeeId + "%\" and");
        }
        String date = mScreenResults.get("screen_date");
        if (date != null && !"".equals(date)) {
            where.append(" HistoryCase.investigationDateFrom > \"" + date + "\" and");
        }
        String address = mScreenResults.get("screen_address");
        if (address != null && !"".equals(address)) {
            where.append(" HistoryCase.sceneDetail like \"%" + address + "%\" and");
        }
        String type = mScreenResults.get("screen_type");
        if (type != null && !"".equals(type)) {
            where.append(" HistoryCase.caseTypeName like \"%" + type + "%\" and");
        }
        String employee = mScreenResults.get("screen_employee");
        if (employee != null && !"".equals(employee)) {
            where.append(" HistoryCase.investigator like \"%" + employee + "%\"");
        }
        if (!"".equals(query)) {
            where.append("(HistoryCase.investigator like \"%" + query +
                    "%\" or HistoryCase.caseTypeName like \"%" + query + "%\" or HistoryCase.sceneDetail like \"%" + query + "%\")");
        }
        int len = where.lastIndexOf(" and");
        if (len > 0 && where.length() - len <= 4) {
            where = new StringBuilder(where.substring(0, len));
        }
        if (where.length() > 0) {
            //category
            String bigCategory = mScreenResults.get("category_big_type");
            if (bigCategory != null && !"".equals(bigCategory)) {
                sql.append("select HistoryCase.* from HistoryCase left join CsDictsConjunction on HistoryCase.caseType = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" and ");
            } else {
                sql.append("select HistoryCase.* from HistoryCase where ");
            }
            sql.append(where.toString());
        } else {
            String bigCategory = mScreenResults.get("category_big_type");
            if (bigCategory != null && !"".equals(bigCategory)) {
                sql.append("select HistoryCase.* from HistoryCase left join CsDictsConjunction on HistoryCase.caseType = CsDictsConjunction.dictKeyTo where CsDictsConjunction.dictKeyFrom = \"" + bigCategory + "\" ");
            } else {
                sql.append("select HistoryCase.* from HistoryCase ");
            }
        }
        String order = mScreenResults.get("sort");
        SQLiteDatabase database = Utils.getSQLiteDatabase(this);
        Utils.createHistoryTable(InquestFind.this, tableName);
        Cursor cursor = null;
        if (order == null || "".equals(order)) {
            cursor = database.rawQuery(sql.toString() + " order by investigationDateFrom desc limit " + mHistoryList.size() + "," + COUNT_HISTORY_PAGING, null);
        } else {
            cursor = database.rawQuery(sql.toString() + " order by investigationDateFrom " + order + " limit " + mHistoryList.size() + "," + COUNT_HISTORY_PAGING, null);
        }
        // search sql end
        CaseHistory history = null;
        while (cursor.moveToNext()) {
            history = new CaseHistory();
            history.setCaseID(cursor.getString(cursor.getColumnIndex("caseID")));
            history.setCaseNo(cursor.getString(cursor.getColumnIndex("caseNo")));
            history.setCaseName(cursor.getString(cursor.getColumnIndex("caseName")));
            history.setInvestigator(cursor.getString(cursor.getColumnIndex("investigator")));
            history.setInvestigatorIds(cursor.getString(cursor.getColumnIndex("investigatorIds")));
            history.setReceivedDate(cursor.getString(cursor.getColumnIndex("receivedDate")));
            history.setInvestigationDateFrom(cursor.getString(cursor.getColumnIndex("investigationDateFrom")));
            history.setInvestigationDateTo(cursor.getString(cursor.getColumnIndex("investigationDateTo")));
            history.setOccurrenceDateFrom(cursor.getString(cursor.getColumnIndex("occurrenceDateFrom")));
            history.setOccurrenceDateTo(cursor.getString(cursor.getColumnIndex("occurrenceDateTo")));
            history.setSceneDetail(cursor.getString(cursor.getColumnIndex("sceneDetail")));
            history.setCaseType(cursor.getString(cursor.getColumnIndex("caseType")));
            history.setCaseTypeName(cursor.getString(cursor.getColumnIndex("caseTypeName")));
            history.setCaseCategory(cursor.getString(cursor.getColumnIndex("caseCategory")));
            history.setSceneRegionalism(cursor.getString(cursor.getColumnIndex("sceneRegionalism")));
            history.setSceneRegionalismName(cursor.getString(cursor.getColumnIndex("sceneRegionalismName")));
            history.setSceneInvestigationJson(cursor.getString(cursor.getColumnIndex("sceneInvestigationJson")));
            history.setSceneLawCase(cursor.getString(cursor.getColumnIndex("sceneLawCase")));
            history.setSceneReceptionDispatch(cursor.getString(cursor.getColumnIndex("sceneReceptionDispatch")));
            history.setExposureProcess(cursor.getString(cursor.getColumnIndex("exposureProcess")));
            mHistoryList.add(history);
        }
        return results;
    }

    private void initListData(boolean needSearch, boolean needClearHistoryDatas) {
        String s = "";
        if (mSearchEdit != null) {
            s = mSearchEdit.getText().toString().trim();
        }
        mDataLists = getDatas(s);
        if (needSearch) {
            getHistoryDatas(s, needClearHistoryDatas);
        }
        //mAdapter = new SceneProspectListAdapter(mEmployeeName, mDataLists,mEmployeeId);
        mAdapter = new SceneProspectListAdapter(mEmployeeName, mDataLists, mHistoryList, mEmployeeId, new SceneProspectListAdapter.OnAdditionalRecordingListener() {
            @Override
            public void onAdditionalRecording(int position) {
                DbModel caseInfo = mDataLists.get(position);
                Intent intent = new Intent(InquestFind.this, ProspectInterface.class);
                intent.putExtra("caseId", caseInfo.getString("caseNo"));
                intent.putExtra("mode", BaseView.VIEW);
                intent.putExtra(BaseView.ADDREC, true);
                intent.putExtra("caseInfo", caseInfo.getString("exposureProcess"));
                intent.putExtra("templateId", caseInfo.getString("templateId"));
                initItemFromJson(caseInfo.getString("caseNo"));
                if (withCaseList.size() <= 0) {
                    initItemFromDb(caseInfo.getString("templateId"));
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("tabList", withCaseList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mSceneProspectListView.setAdapter(mAdapter);
        mSceneProspectListView.setOnItemClickListener(mSceneListItemClick);
        if (mDataLists.size() + mHistoryList.size() == 0) {
            mSceneNoDetails.setVisibility(View.VISIBLE);
        } else {
            mSceneNoDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "Onclick v id = " + v.getId());
        switch (v.getId()) {
            case R.id.layout_selection_category:
                showCategoryGrid(LAYOUT_CATEGORY_SHOW);
                mIsEmployee = mScreenResults.get("category_is_employee");
                mIsOver = mScreenResults.get("category_is_over");
                mBitCategoryValue = mScreenResults.get("category_big_type");
                mZoneValue = mScreenResults.get("category_zone");
                mBigCategoryAdapter.setSelectedItem(mBitCategoryValue);
                mBigCategoryAdapter.notifyDataSetChanged();
                mZoneAdapter.setSelectedItem(mZoneValue);
                mZoneAdapter.notifyDataSetChanged();
                updateIsEmployeeBg(mIsEmployee);
                updateIsOverBg(mIsOver);
                break;
            case R.id.layout_selection_sort:
                showCategoryGrid(LAYOUT_SORT_SHOW);
                break;
            case R.id.layout_selection_screen:
                showCategoryGrid(LAYOUT_NONE_SHOW);
                if (mCategoryPop == null) {
                    mCategoryPop = new SelectionCategoryPop(InquestFind.this, true, true);
                    mCategoryPop.setSelectionPopInterFace(this);
                }
                mCategoryPop.showAtLocation(findViewById(R.id.scene_prospect_root), Gravity.RIGHT, 0, 0);
                break;
            case R.id.selection_is_employee_all_btn:
                mIsEmployee = "";
                updateIsEmployeeBg(mIsEmployee);
                break;
            case R.id.selection_is_employee_yes_btn:
                mIsEmployee = "yes";
                updateIsEmployeeBg(mIsEmployee);
                break;
            case R.id.selection_is_employee_no_btn:
                mIsEmployee = "no";
                updateIsEmployeeBg(mIsEmployee);
                break;
            case R.id.selection_is_over_all_btn:
                mIsOver = "";
                updateIsOverBg(mIsOver);
                break;
            case R.id.selection_is_over_yes_btn:
                mIsOver = "yes";
                updateIsOverBg(mIsOver);
                break;
            case R.id.selection_is_over_no_btn:
                mIsOver = "no";
                updateIsOverBg(mIsOver);
                break;
            case R.id.selection_category_reset_btn:
                mIsEmployee = "";
                mIsOver = "";
                mBitCategoryValue = "";
                mBigCategoryAdapter.setSelectedItem(0);
                mBigCategoryAdapter.notifyDataSetChanged();
                mZoneValue = "";
                mZoneAdapter.setSelectedItem(0);
                mZoneAdapter.notifyDataSetChanged();
                mScreenResults.put("category_big_type", "");
                mScreenResults.put("category_zone", "");
                mScreenResults.put("category_is_employee", "");
                mScreenResults.put("category_is_over", "");
                updateIsEmployeeBg(mIsEmployee);
                updateIsOverBg(mIsOver);
                initListData(true, true);
                break;
            case R.id.selection_category_save_btn:
                mScreenResults.put("category_big_type", mBitCategoryValue);
                mScreenResults.put("category_zone", mZoneValue);
                mScreenResults.put("category_is_employee", mIsEmployee);
                mScreenResults.put("category_is_over", mIsOver);
                showCategoryGrid(LAYOUT_NONE_SHOW);
                initListData(true, true);
                break;
        }
    }

    //////////////////////////****** category gridview *******///////////////////////////////
    //big category
    private class CategoryAdapter extends BaseAdapter {
        private Context mContext;
        private int selected;

        public CategoryAdapter(Context context) {
            this.mContext = context;
        }

        public void setSelectedItem(int selected) {
            this.selected = selected;
        }

        public void setSelectedItem(String value) {
            int lenth = mBigCategoryDatas.size();
            if (value == null) {
                this.selected = 0;
                return;
            }
            for (int i = 0; i < lenth; i++) {
                String s = mBigCategoryDatas.get(i).getDictKey();
                if (value.equals(s)) {
                    this.selected = i;
                    break;
                } else if ("".equals(value)) {
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
                    if ("GASSLAJ".equals(mBitCategoryValue)) {
                        mBitCategoryValue = "";
                    }
                    notifyDataSetChanged();
                }
            });
            if (selected == position) {
                //button.setBackgroundResource(R.drawable.selection_category_item_selected);
                //button.setTextColor(getResources().getColor(R.color.common_btn_bg_normal_color));
                button.setBackgroundResource(R.drawable.selection_grid_item_press_bg);
                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_press_color));
            } else {
                //                button.setBackgroundResource(R.drawable.selection_screen_item_normal);
                //                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_color));
                button.setBackgroundResource(R.drawable.selection_grid_item_normal_bg);
                button.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
            }
            return convertView;
        }
    }

    //zone gridview
    private class ZoneGridAdapter extends BaseAdapter {
        private Context mContext;
        private int selection = 0;

        public ZoneGridAdapter(Context context) {
            this.mContext = context;
        }

        public void setSelectedItem(int select) {
            this.selection = select;
        }

        public void setSelectedItem(String value) {
            int lenth = mZoneDatas.size();
            if (value == null) {
                this.selection = 0;
                return;
            }
            for (int i = 0; i < lenth; i++) {
                String s = mZoneDatas.get(i).getCompartmentNo();
                if (value.equals(s)) {
                    this.selection = i;
                    break;
                } else if ("".equals(value)) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_pop_item, null);
            Button btn = (Button) convertView.findViewById(R.id.selection_pop_item_tv);
            btn.setText(mZoneDatas.get(position).getCompartmentName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection = position;
                    mZoneValue = mZoneDatas.get(position).getCompartmentNo();
                    notifyDataSetChanged();
                }
            });
            if (selection == position) {
                //                btn.setBackgroundResource(R.drawable.selection_category_item_selected);
                //                btn.setTextColor(getResources().getColor(R.color.common_btn_bg_normal_color));
                btn.setBackgroundResource(R.drawable.selection_grid_item_press_bg);
                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_press_color));
            } else {
                //                btn.setBackgroundResource(R.drawable.selection_screen_item_normal);
                //                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_color));
                btn.setBackgroundResource(R.drawable.selection_grid_item_normal_bg);
                btn.setTextColor(getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
            }
            return convertView;
        }
    }

    private void updateIsEmployeeBg(String value) {
            /*mIsEmployeeAll.setBackgroundResource("".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsEmployeeAll.setTextColor("".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));
            mIsEmployeeYes.setBackgroundResource("yes".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsEmployeeYes.setTextColor("yes".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));
            mIsEmployeeNo.setBackgroundResource("no".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsEmployeeNo.setTextColor("no".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));*/
        mIsEmployeeAll.setBackgroundResource("".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsEmployeeAll.setTextColor("".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
        mIsEmployeeYes.setBackgroundResource("yes".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsEmployeeYes.setTextColor("yes".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
        mIsEmployeeNo.setBackgroundResource("no".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsEmployeeNo.setTextColor("no".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
    }

    private void updateIsOverBg(String value) {
            /*mIsOverAll.setBackgroundResource("".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsOverAll.setTextColor("".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));
            mIsOverYes.setBackgroundResource("yes".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsOverYes.setTextColor("yes".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));
            mIsOverNo.setBackgroundResource("no".equals(value) ? R.drawable.selection_category_item_selected : R.drawable.selection_category_item_normal);
            mIsOverNo.setTextColor("no".equals(value) ? getResources().getColor(R.color.common_btn_bg_normal_color) : getResources().getColor(R.color.selection_category_grid_item_text_color));*/
        mIsOverAll.setBackgroundResource("".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsOverAll.setTextColor("".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
        mIsOverYes.setBackgroundResource("yes".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsOverYes.setTextColor("yes".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
        mIsOverNo.setBackgroundResource("no".equals(value) ? R.drawable.selection_grid_item_press_bg : R.drawable.selection_grid_item_normal_bg);
        mIsOverNo.setTextColor("no".equals(value) ? getResources().getColor(R.color.selection_category_grid_item_text_press_color) : getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
    }

    private void showCategoryGrid(int show) {
        if (mCurrentLayoutShow == show) {
            mSelectionShowLayout.setVisibility(View.GONE);
            mCurrentLayoutShow = LAYOUT_NONE_SHOW;
        } else {
            mCurrentLayoutShow = show;
            mSelectionSortList.setVisibility(mCurrentLayoutShow == LAYOUT_SORT_SHOW ? View.VISIBLE : View.GONE);
            mSelectionCategoryLayout.setVisibility(mCurrentLayoutShow == LAYOUT_CATEGORY_SHOW ? View.VISIBLE : View.GONE);
            mSelectionShowLayout.setVisibility(mCurrentLayoutShow == LAYOUT_NONE_SHOW ? View.GONE : View.VISIBLE);
        }
    }

    //////////////////////////****** sort listview *******///////////////////////////////
    private class SortListAdapter extends BaseAdapter {
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.selection_sort_list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.selection_sort_list_item_tv);
            tv.setText(mSortDatas[position]);
            if (mSrotSelected.equals(mSortDatas[position])) {
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_press));
            } else {
                tv.setTextColor(getResources().getColor(R.color.selection_sort_list_item_text_normal));
            }
            return convertView;
        }
    }

    public AdapterView.OnItemClickListener mSortListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showCategoryGrid(LAYOUT_NONE_SHOW);
            mSrotSelected = mSortDatas[position];
            mSortListAdapter.notifyDataSetChanged();
            switch (position) {
                case 0:
                    mScreenResults.put("sort", "");
                    break;
                case 1:
                    mScreenResults.put("sort", "desc");
                    break;
                case 2:
                    mScreenResults.put("sort", "asc");
                    break;
                default:
                    mScreenResults.put("sort", "");
                    break;
            }

            initListData(true, true);
        }
    };

    /***
     * Scene prospect list item click
     */
    private AdapterView.OnItemClickListener mSceneListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int len = mDataLists.size();
            if (position >= len) {
                int[] time = Utils.getAppointDate(1);
                final CaseHistory history = mHistoryList.get(position - len);
                Intent intent = new Intent(InquestFind.this, CaseHistoryDetails.class);
                intent.putExtra("case_id", history.getCaseID());
                intent.putExtra("table_name", "HistoryCase");
                startActivity(intent);
                return;
            }
            final DbModel caseInfo = mDataLists.get(position);
            boolean prospectState = getProspectState(caseInfo.getString("caseNo"));
            if (prospectState) {
                String status = caseInfo.getString("status");
                if ("0".equals(status) || "1".equals(status) || "2".equals(status)) {
                    Intent intent = new Intent(InquestFind.this, ProspectPreview.class);
                    intent.putExtra("caseId", caseInfo.getString("caseNo"));
                    intent.putExtra("caseInfo", caseInfo.getString("exposureProcess"));
                    intent.putExtra("templateId", caseInfo.getString("templateId"));
                    intent.putExtra("status", status);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(InquestFind.this, ProspectInterface.class);
                    intent.putExtra("caseId", caseInfo.getString("caseNo"));
                    intent.putExtra("mode", BaseView.VIEW);
                    intent.putExtra("caseInfo", caseInfo.getString("exposureProcess"));
                    intent.putExtra("templateId", caseInfo.getString("templateId"));
                    initItemFromJson(caseInfo.getString("caseNo"));
                    if (withCaseList.size() <= 0) {
                        initItemFromDb(caseInfo.getString("templateId"));
                    }
                    intent.putExtra("findType", "find");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tabList", withCaseList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            } else {

                Intent intent = new Intent(InquestFind.this, ProspectInterface.class);
                intent.putExtra("caseId", caseInfo.getString("caseNo"));
                intent.putExtra("mode", BaseView.VIEW);
                intent.putExtra("caseInfo", caseInfo.getString("exposureProcess"));
                intent.putExtra("templateId", caseInfo.getString("templateId"));
                initItemFromJson(caseInfo.getString("caseNo"));
                if (withCaseList.size() <= 0) {
                    initItemFromDb(caseInfo.getString("templateId"));
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("tabList", withCaseList);
                intent.putExtras(bundle);
                startActivity(intent);
                   /* PopListSingleLevel popListSingleLevel = new PopListSingleLevel(InquestFind.this, "案件类型", view, getCaseTemplates(InquestFind.this));
                    popListSingleLevel.setListener(new PopListSingleLevel.onResultListener() {
                        @Override
                        public void onResult(String templateId) {
                            Intent intent = new Intent(InquestFind.this, ProspectPreview.class);
                            intent.putExtra("caseId", caseInfo.getString("caseNo"));
                            intent.putExtra("caseInfo", caseInfo.getString("exposureProcess"));
                            intent.putExtra("templateId", templateId);
                            startActivity(intent);
                            setTemplateId(templateId, caseInfo.getString("caseNo"));
                        }
                    });*/

            }
        }
    };


    private boolean getProspectState(String caseId) {
        List<FindCsSceneCases> list = EvidenceApplication.db.findAllByWhere(FindCsSceneCases.class, "caseNo = '" + caseId + "'");
        return list != null && list.size() != 0 && list.get(0).getTemplateId() != null;
    }

    private List<CsDicts> getCaseTemplates(Context context) {
        SharePre userInfo = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String orgId = userInfo.getString("organizationId", "");
        List<CommonTemplate> commonTemplates = EvidenceApplication.db.findAllByWhere(CommonTemplate.class, "orgId = '" + orgId + "'");
        List<CsDicts> caseTemplates = new ArrayList<>();
        for (CommonTemplate commonTemplate : commonTemplates) {
            CsDicts template = new CsDicts();
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "dictKey = '" +
                    commonTemplate.getCaseTypeCode() + "'" + " and " + "rootKey = 'AJLBDM'");
            for (CsDicts csDict : csDicts) {
                template.setDictValue1(csDict.getDictValue1());
            }
            template.setDictValue2(commonTemplate.getSid());
            caseTemplates.add(template);
        }
        return caseTemplates;
    }

    private void setTemplateId(String templateId, String caseId) {
        List<FindCsSceneCases> list = EvidenceApplication.db.findAllByWhere(FindCsSceneCases.class, "caseNo = '" + caseId + "'");
        if (list != null && list.size() != 0) {
            FindCsSceneCases findCsSceneCases = list.get(0);
            findCsSceneCases.setTemplateId(templateId);
            EvidenceApplication.db.update(findCsSceneCases);
        }
    }


    //temporary and will delete
    private ArrayList<ProspectPreViewItemData> withCaseList = new ArrayList<>();
    private ArrayList<ProspectPreViewItemData> allList = new ArrayList<>();

    private void initItemFromJson(String caseId) {
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

    /**
     * 获取所有的模块
     */
    private void getAllData() {
        List<BaseTemp> baseTemps = EvidenceApplication.db.findAllByWhere(BaseTemp.class, "templateType = '0'");
        for (BaseTemp baseTemp : baseTemps) {
            String tableName = baseTemp.getTableName();
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setName(baseTemp.getSceneName());
            data.setField(tableName);
            //配置描述信息
            allList.add(data);
        }
        getALLConfig();
    }


    private void getALLConfig() {
        for (ProspectPreViewItemData data : allList) {
            String id = data.getField();
            if ("SCENE_PHOTO".equals(id))
                data.setEditOrCamera(true);
        }
        //获取是否录音
        List<CommonTemplateDetail> commonTemplateDetails1 = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "tableName = 'SCENE_TOP_RECORD' and templateLevel = '0'");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails1) {
            List<CommonExtField> photoItems = EvidenceApplication.db.findAllByWhere(CommonExtField.class, "templateId = '" + commonTemplateDetail.getTemplateId() + "'" + " and sceneType = 'SCENE_TOP_RECORD'");
            builderConfig(photoItems, "setNeedRec", boolean.class);
        }
    }

    /**
     * 配置模块
     *
     * @param configItems 配置信息表
     * @param methodName  配置方法名
     * @param paramClass  配置参数类
     */
    @SuppressWarnings("ALL")
    private <T> void builderConfig(List<CommonExtField> configItems, String methodName, Class<T> paramClass) {
        for (CommonExtField commonExtField : configItems) {
            String itemId = commonExtField.getField();
            for (ProspectPreViewItemData data : allList) {
                if (data.getField().equals(itemId)) {
                    Class itemClass = data.getClass();
                    Method method = null;
                    try {
                        method = itemClass.getDeclaredMethod(methodName, paramClass);
                        method.invoke(data, true);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    private void getFindList(String MethodName){
        StringMap params = new StringMap();
        String alarmNo = mSearchEdit.getText().toString();
        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("token", mShares.getString("token", ""));
        params.putString("alarmNo",alarmNo);

        Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {


                //Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {

                       String response1=  response.getString("data");
                        JSONObject jSONObject = new JSONObject(response1);
                        Log.d("response1", response1 + "");

                        String data1 = jSONObject.getString("data");
                        JSONArray jsonArray =new JSONArray(data1);
                        Log.d("jsonArray",jsonArray+"");

                        JSONObject jsonObjectdata;
                        FindCsSceneCases findCsSceneCases = new FindCsSceneCases();
                        for (int i = 0; i < jsonArray.length(); i++) {
                             jsonObjectdata = jsonArray.getJSONObject(i);

                            Log.d("jsonObjectdata", jsonObjectdata + "");
                            //refreshLayout.setRefreshing(false);
                            findCsSceneCases.setExposureProcess(jsonObjectdata.getString("EXPOSURE_PROCESS"));
                            String id = jsonObjectdata.getString("CASE_ID");
                            findCsSceneCases.setId(id);
                            findCsSceneCases.setReceivePeople(jsonObjectdata.getString("INVESTIGATOR"));
                            findCsSceneCases.setReceptionNo(jsonObjectdata.getString("RECEPTION_NO"));
                            findCsSceneCases.setCaseTypeId(jsonObjectdata.getString("CASE_TYPE"));
                            findCsSceneCases.setAlarmAddress(jsonObjectdata.getString("SCENE_DETAIL"));
                            findCsSceneCases.setCaseType(jsonObjectdata.getString("CASE_TYPE_NAME"));
                            findCsSceneCases.setReceiveCaseTime(jsonObjectdata.getString("RECEIVED_DATE"));
                            findCsSceneCases.setCaseNo(jsonObjectdata.getString("INVESTIGATION_ID"));
                            findCsSceneCases.setTemplateId(templateIdTemp);
                            findCsSceneCases.setStatus("3");
                            List<FindCsSceneCases> findCsSceneCasesList =EvidenceApplication.db.findAllByWhere(FindCsSceneCases.class,
                                    "id = '"+id+"'");
                            if(findCsSceneCasesList.size()>0){
                                EvidenceApplication.db.update(findCsSceneCasesList.get(0));
                            }else{
                                EvidenceApplication.db.save(findCsSceneCases);
                            }
                           // EvidenceApplication.db.save(findCsSceneCases);
                        }
                    }
                   // materialRefreshLayout.finishRefreshLoadMore();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
             //   mSceneNoDetails.setVisibility(View.VISIBLE);
               // mSceneNoDetailsText.setText("加载失败，请检查网络！");
                Utils.stopProgressDialog();
            }
        });

    }



}
