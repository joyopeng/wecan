package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.UploadedListAdapter;
import com.gofirst.scenecollection.evidence.view.customview.SelectionCategoryPop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class UploadedCaseList extends Activity implements View.OnClickListener,
        SelectionCategoryPop.SelectionPopInterFace, Handler.Callback {

    private final int LAYOUT_NONE_SHOW = -1;
    private final int LAYOUT_SORT_SHOW = 2;
    private int mCurrentLayoutShow = LAYOUT_NONE_SHOW;
    private View mSelectionShowLayout;
    private ListView mSelectionSortList;
    private String mSrotSelected = "默认排序";
    private String[] mSortDatas = {"默认排序", "时间最近", "时间最远"};
    private SortListAdapter mSortListAdapter;
    private HashMap<String, String> mScreenResults = new HashMap<>();
    private SelectionCategoryPop mCategoryPop;
    private ListView upLoadedCaseList;
    private Handler handler;
    private SharePre sharePre;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.uploaded_case_list);
        View titleView = findViewById(R.id.title);
        sharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        ((TextView) titleView.findViewById(R.id.secondary_title_tv)).setText("已上传数据");
        titleView.findViewById(R.id.secondary_back_img).setOnClickListener(this);
        View mLayoutSort = findViewById(R.id.layout_selection_sort);
        mLayoutSort.setOnClickListener(this);
        mSelectionShowLayout = findViewById(R.id.selection_layout);
        mSelectionSortList = (ListView) findViewById(R.id.selection_sort_list);
        mSortListAdapter = new SortListAdapter(this);
        mSelectionSortList.setAdapter(mSortListAdapter);
        mSelectionSortList.setOnItemClickListener(mSortListItemClick);
        View mLayoutScreen = findViewById(R.id.layout_selection_screen);
        mLayoutScreen.setOnClickListener(this);
        upLoadedCaseList = (ListView) findViewById(R.id.uploaded_case_list);
        mSelectionShowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showCategoryGrid(LAYOUT_NONE_SHOW);
                return true;
            }
        });
        handler = new Handler(this);
        getData(null, null);
    }


    private void getData(final String sortField, final String sort, final String... wheres) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CsSceneCases> list = getUploadedDatas(sortField, sort, wheres);
                if (list != null && list.size() > 0) {
                    Message message = handler.obtainMessage();
                    message.obj = list;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondary_back_img:
                finish();
                break;
            case R.id.layout_selection_sort:
                showCategoryGrid(LAYOUT_SORT_SHOW);
                break;
            case R.id.layout_selection_screen:
                showCategoryGrid(LAYOUT_NONE_SHOW);
                if (mCategoryPop == null) {
                    mCategoryPop = new UploadSelectionCategoryPop(this);
                    mCategoryPop.setSelectionPopInterFace(this);
                }
                mCategoryPop.showAtLocation(findViewById(R.id.scene_prospect_root), Gravity.RIGHT, 0, 0);
                break;
        }
    }

    private void showCategoryGrid(int show) {
        if (mCurrentLayoutShow == show) {
            mSelectionShowLayout.setVisibility(View.GONE);
            mCurrentLayoutShow = LAYOUT_NONE_SHOW;
        } else {
            mCurrentLayoutShow = show;
            mSelectionSortList.setVisibility(mCurrentLayoutShow == LAYOUT_SORT_SHOW ? View.VISIBLE : View.GONE);
            mSelectionShowLayout.setVisibility(mCurrentLayoutShow == LAYOUT_NONE_SHOW ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void resetButtonClick(HashMap<String, String> results) {
        getData(null, null);
    }

    @Override
    public void saveButtonClick(HashMap<String, String> results) {
        if (results == null || results.size() == 0) {
            return;
        }
        String date = results.get("date");
        String address = results.get("address");
        String sceneType = results.get("scene_type");
        final StringBuilder sql = new StringBuilder();
        sql.append("isUploaded = '1' and status = '3' and receivePeopleNum = '");
        sql.append(sharePre.getString("userId",""));
        sql.append("'");
        if (!TextUtils.isEmpty(address)){
            sql.append(" and ");
            sql.append("sceneDetail like %");
            sql.append(address);
            sql.append("%");
        }
        if (!TextUtils.isEmpty(sceneType)){
            sql.append(" and ");
            sql.append("caseType like %");
            sql.append(address);
            sql.append("%");
        }
        if (!TextUtils.isEmpty(date)) {
            sql.append(" and ");
            sql.append("uploadTime >= ");
            sql.append("'"+date+"'");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,sql.toString(),"uploadTime asc");
                if (list != null && list.size() > 0) {
                    Message message = handler.obtainMessage();
                    message.obj = list;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null && msg.obj != null) {
            upLoadedCaseList.setAdapter(new UploadedListAdapter((List<CsSceneCases>) msg.obj));
        }
        return false;
    }

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
                    getData(null, null);
                    break;
                case 1:
                    getData("uploadTime", "desc");
                    break;
                case 2:
                    getData("uploadTime", "asc");
                    break;
                default:
                    getData(null, null);
                    break;
            }


        }
    };


    public static class UploadSelectionCategoryPop extends SelectionCategoryPop {

        public UploadSelectionCategoryPop(Activity activity) {
            super(activity, true, true);
            mMainView.findViewById(R.id.employee_tv).setVisibility(View.GONE);
            ((TextView) mMainView.findViewById(R.id.create_date_tv)).setText("上传时间");
            mMainView.findViewById(R.id.selection_scene_employee).setVisibility(View.GONE);
        }
    }

    private List<CsSceneCases> getUploadedDatas(String sortField, String sort, String... wheres) {
        StringBuilder sql = new StringBuilder();
        List<CsSceneCases> list;
        sql.append("isUploaded = '1' and status = '3' and receivePeopleNum = '");
        sql.append(sharePre.getString("userId",""));
        sql.append("'");
        if (wheres != null) {
            for (String where : wheres) {
                sql.append(" and ");
                sql.append(where);
            }
        }
        if (!TextUtils.isEmpty(sortField) && !TextUtils.isEmpty(sort)) {
            list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, sql.toString(), sortField + " " + sort);
        } else {
            list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, sql.toString());
        }
        List<CsSceneCases> bulu = new ArrayList<>();
        //查找对应的已上传补录数据
        for (CsSceneCases sceneCases : list){
            List<UnUploadJson> unUploadJsons = EvidenceApplication.db.findAllByWhere(UnUploadJson.class,
                    "addRec = '1'and uploaded = '1' and caseId = '" + sceneCases.getCaseNo() + "'");
            for (UnUploadJson unUploadJson : unUploadJsons){
                CsSceneCases csSceneCases = new CsSceneCases();
                csSceneCases.setAddRec(true);
                csSceneCases.setSceneDetail(sceneCases.getSceneDetail());
                csSceneCases.setCaseType(sceneCases.getCaseType());
                csSceneCases.setUploadTime(unUploadJson.getIsUploading());
//                list.add(csSceneCases);
                bulu.add(csSceneCases);
            }
        }
        list.addAll(bulu);
        return list;
    }
}
