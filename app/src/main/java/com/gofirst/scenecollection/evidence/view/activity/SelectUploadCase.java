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
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.sync.UpLoadService;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.view.adapter.SelectUploadCaseAdapter;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import net.tsz.afinal.db.sqlite.DbModel;

import java.io.File;
import java.util.List;


public class SelectUploadCase extends Activity implements View.OnClickListener,Handler.Callback{

    private Handler handler;
    private ListView listView;
    private SharePre sharePre;
    private SharedPreferences uploadfilerec;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_upload_case);
        View titleView = findViewById(R.id.title);
        sharePre = new SharePre(this,"user_info",MODE_PRIVATE);
        ((TextView) titleView.findViewById(R.id.secondary_title_tv)).setText("手动上传");
        titleView.findViewById(R.id.secondary_back_img).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.uploaded_case_list);
        findViewById(R.id.upload_btn).setOnClickListener(this);
        handler = new Handler(this);
        uploadfilerec = getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondary_back_img:
                finish();
                break;
            case R.id.upload_btn:
                if (!startUpload()) {
                    ToastUtil.show(this, "请重新选择案件", Toast.LENGTH_SHORT);
                }else {
                    ToastUtil.show(this, "开始上传", Toast.LENGTH_SHORT);
                    sharePre.put("manualStart",true);
                    sharePre.commit();
                    finish();
                }
                break;
        }
    }

    private boolean startUpload(){
        boolean isSuccess = false;
        SelectUploadCaseAdapter selectUploadCaseAdapter = (SelectUploadCaseAdapter)listView.getAdapter();
        if (selectUploadCaseAdapter == null)
            return false;
        final List<CsSceneCases> datas = selectUploadCaseAdapter.getDatas();
        final SparseBooleanArray mCheckStates = selectUploadCaseAdapter.getmCheckStates();
                for (int i = 0 ; i < datas.size(); i++){
                    if (mCheckStates.get(i)){
                        if (datas.get(i).isAddRec() && !datas.get(i).isUploaded()){
                            ToastUtil.showShort(SelectUploadCase.this,"补录必须在对应案件上传后才能上传");
                            continue;
                        }
                        CsSceneCases csSceneCases = datas.get(i);
                        // 设置json优先上传
                        //如果有补录而且主案件已经上传完则上传补录否则上传案件
                        if (datas.get(i).isAddRec() && datas.get(i).isUploaded()){
                            UnUploadJson unUploadJson = EvidenceApplication.db.findById(datas.get(i).getReserver1(),UnUploadJson.class);
                            unUploadJson.setSpec(true);
                            EvidenceApplication.db.update(unUploadJson);
                            //
                            long filesize =0;
                            List<RecordFileInfo> list = EvidenceApplication.db.
                                    findAllByWhere(RecordFileInfo.class, "caseId = '" + datas.get(i).getCaseNo() +"' and isUpload = '0' and addRec = '1'");
                            for(RecordFileInfo r:list){
                                if(!TextUtils.isEmpty(r.getFilePath())){
                                    File f = new File(AppPathUtil.getDataPath()+"/"+r.getFilePath());
                                    if(f.exists() && f.isFile())
                                        filesize = filesize +f.length();
                                }
                                if(!TextUtils.isEmpty(r.getTwoHundredFilePath())){
                                    File f = new File(AppPathUtil.getDataPath()+"/"+r.getTwoHundredFilePath());
                                    if(f.exists() && f.isFile())
                                        filesize = filesize +f.length();
                                }
                                if(!TextUtils.isEmpty(r.getContractionsFilePath())){
                                    File f = new File(AppPathUtil.getDataPath()+"/"+r.getContractionsFilePath());
                                    if(f.exists() && f.isFile())
                                        filesize = filesize +f.length();
                                }
                                uploadfilerec.edit().putLong(datas.get(i).getCaseNo(), filesize).commit();
                                //
                            }
                        }else {
                            List<UnUploadJson> list = EvidenceApplication.db.
                                    findAllByWhere(UnUploadJson.class, "caseId = '" + csSceneCases.getCaseNo() + "'");
                            if (list != null && list.size() > 0) {
                                UnUploadJson unUploadJson = list.get(0);
                                unUploadJson.setSpec(true);
                                EvidenceApplication.db.update(unUploadJson);
                            }
                            csSceneCases.setSpecific(true);
                            csSceneCases.setAddRec(false);
                            EvidenceApplication.db.update(csSceneCases);
                        }
                        // 设置分片优先上传
                        List<UnUpLoadBlock> unUpLoadBlocks = EvidenceApplication.db.
                                findAllByWhere(UnUpLoadBlock.class,"caseId = '" + csSceneCases.getCaseNo() + "'");
                        if (unUpLoadBlocks != null && unUpLoadBlocks.size() > 0){
                            UnUpLoadBlock unUpLoadBlock = unUpLoadBlocks.get(0);
                            unUpLoadBlock.setSpec(true);
                            EvidenceApplication.db.update(unUpLoadBlock);
                        }
                        isSuccess = true;
                    }
                }
        startService(new Intent(this, UpLoadService.class));
        return isSuccess;
    }
    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null && msg.obj != null) {
            listView.setAdapter(new SelectUploadCaseAdapter((List<CsSceneCases>) msg.obj));
            findViewById(R.id.upload_btn).setVisibility(View.VISIBLE);
        }
        return false;
    }

    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CsSceneCases> list = EvidenceApplication.
                        db.findAllByWhere(CsSceneCases.class,"isUploaded = '0' and status = '3' " +
                                "and isSpecific = '0' and receivePeopleNum = '"
                                + sharePre.getString("userId","") + "'","uploadTime asc");
                generateAddUploadJson(sharePre);
                //查找未上传的补录数据
                List<UnUploadJson> unUploadJsons = EvidenceApplication.db.findAllByWhere(UnUploadJson.class,"addRec = '1'and uploaded = '0' and isSpec = '0'");
                for (UnUploadJson unUploadJson : unUploadJsons){
                    //查找出对应的案件信息
                    DbModel dbModel = EvidenceApplication.db.findDbModelBySQL("select id,caseType,isUploaded,addRec,sceneDetail,caseNo from CsSceneCases where caseNo = '" + unUploadJson.getCaseId()+"'");
                    CsSceneCases csSceneCases = new CsSceneCases();
                    csSceneCases.setAddRec(true);
                    csSceneCases.setSceneDetail(dbModel.getString("sceneDetail"));
                    csSceneCases.setUploaded(TextUtils.equals("1",dbModel.getString("isUploaded")));
                    //保留字段1：记录对应补录数据主键
                    csSceneCases.setReserver1(unUploadJson.getId());
                    csSceneCases.setCaseType(dbModel.getString("caseType"));
                    csSceneCases.setId(dbModel.getString("id"));
                    csSceneCases.setCaseNo(dbModel.getString("caseNo"));
                    list.add(csSceneCases);
                }
                if (list != null && list.size() > 0){
                    Message message = handler.obtainMessage();
                    message.obj = list;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private static UnUploadJson getAddRecUnUploadJson(String caseId,SharePre sharePre) {
            UnUploadJson unUploadJson = new UnUploadJson();
            unUploadJson.setId(ViewUtil.getUUid());
            unUploadJson.setCaseId(caseId);
            unUploadJson.setUserId(sharePre.getString("user_id",""));
            unUploadJson.setAddRec(true);
            EvidenceApplication.db.save(unUploadJson);
        return unUploadJson;
    }

    public static void generateAddUploadJson(SharePre sharePre){
        List<DbModel> addRecList = EvidenceApplication.db.findDbModelListBySQL("select caseId from DataTemp where addRec = '1' and uploaded = '0' group by caseId");
        //产生补录上传数据
        for (DbModel dbMode : addRecList){
            List<CsSceneCases> addRecCsSceneCaseList =  EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"status = '3' and receivePeopleNum = '"
                    + sharePre.getString("userId","") + "' and caseNo = '" + dbMode.getString("caseId") + "'");
            // 此循环大小只有1次
            for (CsSceneCases cases : addRecCsSceneCaseList){
                cases.setAddRec(true);
                //加入上传列表
                // 只要出现补录的数据就产生一个新的上传json
                UnUploadJson unUploadJson = getAddRecUnUploadJson(cases.getCaseNo(),sharePre);
                List<DataTemp> dataTemps = EvidenceApplication.db.findAllByWhere(DataTemp.class,
                        "caseId = '" + cases.getCaseNo() + "' and addRec = '1'and uploaded = '0'");
                unUploadJson.setJson(JSON.toJSONString(dataTemps));
                EvidenceApplication.db.update(unUploadJson);
                //设置此数据已经加入上传列表 uploaded = 1;
                for (DataTemp dataTemp : dataTemps){
                    dataTemp.setUploaded(true);
                    EvidenceApplication.db.update(dataTemp);
                }
            }
        }
    }
}
