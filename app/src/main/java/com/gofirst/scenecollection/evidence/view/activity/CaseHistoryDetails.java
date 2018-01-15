package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class CaseHistoryDetails extends Activity implements View.OnClickListener{

    private TextView mInvestigationText;
    private View mInvestigationImg;
    private TextView mLowCaseText;
    private View mLowCaseImg;
    private TextView mReceptionText;
    private View mReceptionImg;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private ViewPager mViewPager;
    private CaseHistoryViewAdapter mViewPagerAdapter;
    private LinearLayout mHistoryInvestigationView;
    private LinearLayout mHistoryLowCaseView;
    private LinearLayout mHistoryReceptionView;
    private String mCaseId;
    private String mTableName;

    private String mInvestigationConfig;
    private String mInvestigationData;
    private String mLowCaseConfig;
    private String mLowCaseData;
    private String mReceptionConfig;
    private String mReceptionData;
    private List<HistoryCache> mInvestigationList = new ArrayList<>();
    private List<HistoryCache> mLawCaseList = new ArrayList<>();
    private List<HistoryCache> mReceptionList = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    initView();
                    mViewPagerAdapter = new CaseHistoryViewAdapter(mViewPager,mHistoryInvestigationView,mHistoryLowCaseView,mHistoryReceptionView);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.setCurrentItem(0);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.case_history_details_layout);
        mCaseId = getIntent().getStringExtra("case_id");
        mTableName = getIntent().getStringExtra("table_name");
        mInvestigationText = (TextView) findViewById(R.id.case_history_investigation_tab) ;
        mInvestigationText.setOnClickListener(this);
        mInvestigationImg = findViewById(R.id.case_history_investigation_tab_img);
        mLowCaseText = (TextView) findViewById(R.id.case_history_lowcase_tab);
        mLowCaseText.setOnClickListener(this);
        mLowCaseImg = findViewById(R.id.case_history_lowcase_tab_img);
        mReceptionText = (TextView) findViewById(R.id.case_history_reception_tab);
        mReceptionText.setOnClickListener(this);
        mReceptionImg = findViewById(R.id.case_history_reception_tab_img);
        mTitleTv = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleTv.setText("历史数据");
        mBackBtn = (ImageView) findViewById(R.id.secondary_back_img);
        mBackBtn.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.case_history_view_pager);
        initRootView();
        new Thread(mInitRunnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.case_history_investigation_tab:
                mViewPager.setCurrentItem(0);
                mInvestigationText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mLowCaseText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mReceptionText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mInvestigationImg.setVisibility(View.VISIBLE);
                mLowCaseImg.setVisibility(View.INVISIBLE);
                mReceptionImg.setVisibility(View.INVISIBLE);
                break;
            case R.id.case_history_lowcase_tab:
                mViewPager.setCurrentItem(1);
                mInvestigationText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mLowCaseText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mReceptionText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mInvestigationImg.setVisibility(View.INVISIBLE);
                mLowCaseImg.setVisibility(View.VISIBLE);
                mReceptionImg.setVisibility(View.INVISIBLE);
                break;
            case R.id.case_history_reception_tab:
                mViewPager.setCurrentItem(2);
                mInvestigationText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mLowCaseText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mReceptionText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mInvestigationImg.setVisibility(View.INVISIBLE);
                mLowCaseImg.setVisibility(View.INVISIBLE);
                mReceptionImg.setVisibility(View.VISIBLE);
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }

    private class HistoryCache{
        private int order;
        private String field;
        private String fieldName;
        private String data;

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    private class CaseHistoryViewAdapter extends PagerAdapter{

        private ViewPager mAdapterPager;
        private View mAdapterCase;
        private View mAdapterReception;
        private View mAdapterInvestigation;

        public CaseHistoryViewAdapter(ViewPager viewPager,View investigation,View caseView,View reception) {
            super();
            this.mAdapterPager = viewPager;
            this.mAdapterInvestigation = investigation;
            this.mAdapterCase = caseView;
            this.mAdapterReception = reception;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                mAdapterPager.addView(mAdapterInvestigation);
                return mAdapterInvestigation;
            }else if(position == 1) {
                mAdapterPager.addView(mAdapterCase);
                return mAdapterCase;
            }else{
                mAdapterPager.addView(mAdapterReception);
                return mAdapterReception;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position == 0) {
                mAdapterPager.removeView(mAdapterInvestigation);
            }else if(position == 1) {
                mAdapterPager.removeView(mAdapterCase);
            }else{
                mAdapterPager.removeView(mAdapterReception);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }
    }

    private void initData(){
        if(mTableName == null || mCaseId == null || "".equals(mCaseId) || "".equals(mTableName)){
            return;
        }

        SQLiteDatabase database = Utils.getSQLiteDatabase(this);
        Cursor cursor = database.rawQuery("select * from " + mTableName + " where caseId = \"" + mCaseId + "\"",null);
        while (cursor.moveToNext()){
            mInvestigationData = cursor.getString(cursor.getColumnIndex("sceneInvestigationJson"));
            mLowCaseData = cursor.getString(cursor.getColumnIndex("sceneLawCase"));
            mReceptionData = cursor.getString(cursor.getColumnIndex("sceneReceptionDispatch"));
        }
        cursor = database.rawQuery("select * from HistoryConfigDetail where id = \"" + mTableName + "\"",null);
        while (cursor.moveToNext()){
            mInvestigationConfig = cursor.getString(cursor.getColumnIndex("historyInvestigation"));
            mLowCaseConfig = cursor.getString(cursor.getColumnIndex("historyLowCase"));
            mReceptionConfig = cursor.getString(cursor.getColumnIndex("historyReception"));
        }
        cursor.close();
        HistoryCache cache = null;
        try {
            JSONObject mInvestigationDataObject = new JSONObject(mInvestigationData);
            JSONObject mLowCaseDataObject = new JSONObject(mLowCaseData);
            JSONObject mReceptionDataObject = new JSONObject(mReceptionData);
            JSONObject mInvestigationConfigObject = new JSONObject(mInvestigationConfig);
            JSONObject mInvestigationConfigObjectOrder = mInvestigationConfigObject.getJSONObject("order");
            JSONObject mInvestigationConfigObjectDesc = mInvestigationConfigObject.getJSONObject("desc");
            JSONObject mInvestigationConfigObjectConfig = mInvestigationConfigObject.getJSONObject("config");
            JSONObject mLowCaseConfigObject = new JSONObject(mLowCaseConfig);
            JSONObject mLowCaseConfigObjectOrder = mLowCaseConfigObject.getJSONObject("order");
            JSONObject mLowCaseConfigObjectDesc = mLowCaseConfigObject.getJSONObject("desc");
            JSONObject mLowCaseConfigObjectConfig = mLowCaseConfigObject.getJSONObject("config");
            JSONObject mReceptionConfigObject = new JSONObject(mReceptionConfig);
            JSONObject mReceptionConfigObjectOrder = mReceptionConfigObject.getJSONObject("order");
            JSONObject mReceptionConfigObjectDesc = mReceptionConfigObject.getJSONObject("desc");
            JSONObject mReceptionConfigObjectConfig = mReceptionConfigObject.getJSONObject("config");
            Iterator<String> it = mInvestigationConfigObjectOrder.keys();
            while (it.hasNext()){
                String key = it.next();
                String order = mInvestigationConfigObjectOrder.getString(key);
                if(order == null || "".equals(order) || "-1".equals(order)){
                    continue;
                }
                cache = new HistoryCache();
                String filedName = mInvestigationConfigObjectDesc.getString(key);
                if(filedName == null || "".equals(filedName) || "null".equals(filedName)){
                    continue;
                }else {
                    cache.setFieldName(mInvestigationConfigObjectDesc.getString(key));
                }
                cache.setOrder(mInvestigationConfigObjectOrder.getInt(key));
                String config = mInvestigationConfigObjectConfig.getString(key);
                cache.setField(config);
                if(mInvestigationDataObject.has(config)) {
                    cache.setData(mInvestigationDataObject.getString(config));
                }else{
                    continue;
                }
                mInvestigationList.add(cache);
            }
            it = mLowCaseConfigObjectOrder.keys();
            while (it.hasNext()){
                String key = it.next();
                String order = mLowCaseConfigObjectOrder.getString(key);
                if(order == null || "".equals(order) || "-1".equals(order)){
                    continue;
                }
                cache = new HistoryCache();
                String filedName = mLowCaseConfigObjectDesc.getString(key);
                if(filedName == null || "".equals(filedName) || "null".equals(filedName)){
                    continue;
                }else {
                    cache.setFieldName(mLowCaseConfigObjectDesc.getString(key));
                }
                cache.setOrder(mLowCaseConfigObjectOrder.getInt(key));
                String config = mLowCaseConfigObjectConfig.getString(key);
                cache.setField(config);
                if(mLowCaseDataObject.has(config)) {
                    cache.setData(mLowCaseDataObject.getString(config));
                }else{
                    continue;
                }
                mLawCaseList.add(cache);
            }
            it = mReceptionConfigObjectOrder.keys();
            while (it.hasNext()){
                String key = it.next();
                String order = mReceptionConfigObjectOrder.getString(key);
                if(order == null || "".equals(order) || "-1".equals(order)){
                    continue;
                }
                cache = new HistoryCache();
                String filedName = mReceptionConfigObjectDesc.getString(key);
                if(filedName == null || "".equals(filedName) || "null".equals(filedName)){
                    continue;
                }else{
                    cache.setFieldName(mReceptionConfigObjectDesc.getString(key));
                }
                cache.setOrder(mReceptionConfigObjectOrder.getInt(key));
                String config = mReceptionConfigObjectConfig.getString(key);
                cache.setField(config);
                if(mReceptionDataObject.has(config)) {
                    cache.setData(mReceptionDataObject.getString(config));
                }else{
                    continue;
                }
                mReceptionList.add(cache);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortList(mInvestigationList);
        sortList(mLawCaseList);
        sortList(mReceptionList);
    }

    private void initRootView(){
        mHistoryInvestigationView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.case_history_investigation_layout,null);
        mHistoryLowCaseView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.case_history_low_case_layout,null);
        mHistoryReceptionView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.case_history_reception_layout,null);
    }

    private void initView(){
        mHistoryInvestigationView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.case_history_data_investigation_list,null);
        ListView listView = (ListView) mHistoryInvestigationView.findViewById(R.id.case_history_data_investigation_listview);
        listView.setAdapter(new investigationListAdapter(this));
        mHistoryLowCaseView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.case_history_data_lawcase_list,null);
        ListView listView1 = (ListView)mHistoryLowCaseView.findViewById(R.id.case_history_data_lawcase_listview);
        listView1.setAdapter(new lawCaseListAdapter(this));
        mHistoryReceptionView  = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.case_history_data_reception_list,null);
        ListView listView2 = (ListView)mHistoryReceptionView.findViewById(R.id.case_history_data_reception_listview);
        listView2.setAdapter(new receptionListAdapter(this));
        /*int length = mInvestigationList.size();
        for (int i = 0;i < length;i++){
            HistoryCache cache = mInvestigationList.get(i);
            addViewToRootView(mHistoryInvestigationView,cache.getFieldName(),cache.getData());
        }
        length = mLawCaseList.size();
        for(int i = 0;i < length;i++){
            HistoryCache cache = mLawCaseList.get(i);
            addViewToRootView(mHistoryLowCaseView,cache.getFieldName(),cache.getData());
        }

        length = mReceptionList.size();
        for(int i = 0;i < length;i++){
            HistoryCache cache = mReceptionList.get(i);
            addViewToRootView(mHistoryReceptionView,cache.getFieldName(),cache.getData());
        }*/
    }

    private void addViewToRootView(LinearLayout root, String title, String content){
        TextView titleView = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.leftMargin = 48;
        titleParams.gravity = Gravity.CENTER_VERTICAL;
        titleView.setText(title);
        titleView.setLayoutParams(titleParams);
        TextView contentView = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentParams.leftMargin = 96;
        contentParams.gravity = Gravity.CENTER_VERTICAL;
        contentView.setText(content);
        contentView.setLayoutParams(contentParams);
        root.addView(titleView);
        root.addView(contentView);
    }

    private void sortList(List<HistoryCache> sourceList){
        HistoryCache cache = sourceList.get(0);
        int length = sourceList.size();
        for(int i = 0;i < length;i++){
            for (int j = i + 1;j < length;j++){
                if(sourceList.get(i).getOrder() > sourceList.get(j).getOrder()){
                    cache = sourceList.get(j);
                    sourceList.remove(j);
                    sourceList.add(j,sourceList.get(i));
                    sourceList.remove(i);
                    sourceList.add(i,cache);
                }
            }
        }
    }

    private Runnable mInitRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
            mHandler.sendEmptyMessage(0);
        }
    };

    /**
     * investigation list adapter
     * */
    private class investigationListAdapter extends BaseAdapter{
        private Context mCtx;
        public investigationListAdapter(Context context) {
            super();
            this.mCtx = context;
        }

        @Override
        public int getCount() {
            return mInvestigationList.size();
        }

        @Override
        public Object getItem(int position) {
            return mInvestigationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.case_history_data_list_item,null);
            HistoryCache cache = mInvestigationList.get(position);
            TextView title = (TextView)convertView.findViewById(R.id.case_history_data_item_title);
            title.setText(cache.getFieldName());
            TextView content = (TextView) convertView.findViewById(R.id.case_history_data_item_content);
            content.setText(cache.data);
            return convertView;
        }
    }

    /**
     * law case list adapter
     * */
    private class lawCaseListAdapter extends BaseAdapter{
        private Context mCtx;
        public lawCaseListAdapter(Context context) {
            super();
            this.mCtx = context;
        }

        @Override
        public int getCount() {
            return mLawCaseList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLawCaseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.case_history_data_list_item,null);
            HistoryCache cache = mLawCaseList.get(position);
            TextView title = (TextView)convertView.findViewById(R.id.case_history_data_item_title);
            title.setText(cache.getFieldName());
            TextView content = (TextView) convertView.findViewById(R.id.case_history_data_item_content);
            content.setText(cache.data);
            return convertView;
        }
    }

    /**
     * investigation list adapter
     * */
    private class receptionListAdapter extends BaseAdapter{
        private Context mCtx;
        public receptionListAdapter(Context context) {
            super();
            this.mCtx = context;
        }

        @Override
        public int getCount() {
            return mReceptionList.size();
        }

        @Override
        public Object getItem(int position) {
            return mReceptionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.case_history_data_list_item,null);
            HistoryCache cache = mReceptionList.get(position);
            TextView title = (TextView)convertView.findViewById(R.id.case_history_data_item_title);
            title.setText(cache.getFieldName());
            TextView content = (TextView) convertView.findViewById(R.id.case_history_data_item_content);
            content.setText(cache.data);
            return convertView;
        }
    }
}
