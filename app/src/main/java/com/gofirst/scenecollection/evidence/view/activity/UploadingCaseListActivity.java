package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.sync.UpLoadService;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.view.adapter.SelectUploadCaseAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.UploadingListAdapter;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import net.tsz.afinal.db.sqlite.DbModel;

import java.util.ArrayList;
import java.util.List;


public class UploadingCaseListActivity extends Activity implements View.OnClickListener, Handler.Callback {

    private Handler handler;
    private ListView listView;
    private SharePre sharePre;
    private TextView mRightView;
    private List<CsSceneCases> mCsSceneCasesList = new ArrayList<CsSceneCases>();
    private SharedPreferences uploadfilerec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.uploading_case_list);
        View titleView = findViewById(R.id.title);
        sharePre = new SharePre(this, "user_info", MODE_PRIVATE);
        ((TextView) titleView.findViewById(R.id.secondary_title_tv)).setText("正在上传");
        titleView.findViewById(R.id.secondary_back_img).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.uploaded_case_list);
        mRightView = (TextView) findViewById(R.id.secondary_right_tv);
        mRightView.setVisibility(View.VISIBLE);
        mRightView.setText(R.string.refresh);
        mRightView.setOnClickListener(this);
        handler = new Handler(this);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uploadfilerec == null)
            uploadfilerec = getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
        uploadfilerec.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (uploadfilerec == null)
            uploadfilerec = getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
        uploadfilerec.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondary_back_img:
                finish();
                break;
            case R.id.secondary_right_tv:
                refreshData();
                break;
            default:
                break;
        }
    }


    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();
    }

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
                if (mCsSceneCasesList != null && mCsSceneCasesList.size() > 0) {
                    sharePre.put("manualStart", true);
                    sharePre.commit();
                    startService(new Intent(UploadingCaseListActivity.this, UpLoadService.class));
                }
            }
        }).start();
        ToastUtil.showShort(UploadingCaseListActivity.this, R.string.prompt_refresh);
    }

    public void getData() {
        mCsSceneCasesList.clear();
        List<UnUploadJson> list = EvidenceApplication.db.findAllByWhere(UnUploadJson.class,
                "uploaded = '0' and isSpec = '1'");
        for (UnUploadJson unUploadJson : list) {
            List<CsSceneCases> csSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + unUploadJson.getCaseId() + "'");
            //
            CsSceneCases cs = csSceneCases.get(0);
            if(uploadfilerec !=  null) {
                long currentSize = uploadfilerec.getLong(cs.getCaseNo() + "_u", 0);
                long totalSize = uploadfilerec.getLong(cs.getCaseNo(), 0);
                long percent = (currentSize * 100 / totalSize);
                cs.setUploadTime("上传进度 " + String.valueOf(percent) + "%");
            }
            //
            csSceneCases.get(0).setAddRec(unUploadJson.isAddRec());
            mCsSceneCasesList.add(csSceneCases.get(0));
        }
        if (mCsSceneCasesList != null && mCsSceneCasesList.size() > 0) {
            Message message = handler.obtainMessage();
            message.obj = mCsSceneCasesList;
            handler.sendMessage(message);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null && msg.obj != null) {
            listView.setAdapter(new UploadingListAdapter((List<CsSceneCases>) msg.obj));
        }
        return false;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            for (CsSceneCases cases : mCsSceneCasesList) {
                if (key.contains(cases.getCaseNo())) {
                    long currentSize = sharedPreferences.getLong(key, 0);
                    long totalSize = sharedPreferences.getLong(key.replace("_u",""),0);
                    if(totalSize == 0)
                        continue;
                    long percent =  (currentSize*100 / totalSize);
                    if(percent > 100)
                        percent = 100;
                    cases.setUploadTime("上传进度 "+String.valueOf(percent)+"%");
                    ((UploadingListAdapter)(listView.getAdapter())).notifyDataSetChanged();
                    continue;
                }
            }
        }
    };

}
