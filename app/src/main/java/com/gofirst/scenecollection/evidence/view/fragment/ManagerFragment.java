package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.DateTimeUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.utils.UpLoadLog;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.ContactActivity;
import com.gofirst.scenecollection.evidence.view.activity.DailyScheduleActivity;
import com.gofirst.scenecollection.evidence.view.activity.InquestFind;
import com.gofirst.scenecollection.evidence.view.activity.OffLineMapActivity;
import com.gofirst.scenecollection.evidence.view.activity.PermissionSetting;
import com.gofirst.scenecollection.evidence.view.activity.SelectUploadCase;
import com.gofirst.scenecollection.evidence.view.activity.SyncDataActivity;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class ManagerFragment extends Fragment implements View.OnClickListener{

    public SharePre mShare;
    private View mDailyView;
    private View mCommuticationView;
    private View mMapView;
    private View mSyncDataView;
    private View mUploadDataView;
    private View mPermissionSetting;
    private View mInquestFind;
    private ImageView mSyncDataRemindImg;

    private boolean iscoredataTime = false;
    private boolean isaddresslistTime = false;
    private boolean isscheduleTime = false;
    private TextView test;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShare = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_fragment_layout,null);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initSyncDataState();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manager_daily_btn:
                Intent daily = new Intent(getContext(),DailyScheduleActivity.class);
                startActivity(daily);
                break;
            case R.id.manager_communication_btn:
                Intent contact = new Intent(getContext(), ContactActivity.class);
                startActivity(contact);
                break;
            case R.id.manager_map_btn:
                Intent mapIntent = new Intent(getContext(), OffLineMapActivity.class);
                startActivity(mapIntent);
                break;
            case R.id.manager_data_sync_btn:
                Intent dataSyncIntent = new Intent(getContext(), SyncDataActivity.class);
                startActivity(dataSyncIntent);
                break;
            case R.id.permission_setting_btn:
                new PermissionSetting(getContext(), test, "权限设置", "");
                break;
            case R.id.upload_data_btn:
                startActivity(new Intent(getActivity(), SelectUploadCase.class));
                break;
            case R.id.log_upload:
                startUploadLog();
                break;
            case R.id.inquest_find:

                Intent inquestFindIntent = new Intent(getContext(), InquestFind.class);
                startActivity(inquestFindIntent);
                break;
        }
    }

    private void startUploadLog(){
        ToastUtil.showShort(getContext(),"开始上传日志...");
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                UpLoadLog.UpLoadLogToServicer((String) msg.obj,Netroid.versionName,Netroid.versionCode);
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = AppPathUtil.getLogPath() + "/logs";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = "Upload_Logs_" + DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_EN_Y_M_D_H_M_S) + ".log";
                File f = new File(path+"/" + fileName);
                if(!f.exists()){
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileWriter writer2 = null;
                try {
                    writer2 = new FileWriter(path+"/"+fileName,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedWriter bw = new BufferedWriter(writer2);
                List<CsSceneCases> list = EvidenceApplication.db.findAll(CsSceneCases.class);
                JSONArray jsonArray = (JSONArray) JSON.toJSON(list);
                SharePre sharePre = new SharePre(getContext(),"user_info",Context.MODE_PRIVATE);
                List<CsSceneCases> caseList = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"(status = '1' or status = '3') and receivePeople = '" + sharePre.getString("prospectPerson","") + "'");
                org.json.JSONArray caseArray = new org.json.JSONArray();
                com.alibaba.fastjson.JSONObject caseObject;
                for (CsSceneCases csSceneCases : caseList){
                    caseObject = (com.alibaba.fastjson.JSONObject) JSON.toJSON(csSceneCases);
                    List<RecordFileInfo> photos = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "caseId = '" + csSceneCases.getCaseNo() + "' and father = 'SCENE_PHOTO'");
                    caseObject.put("SCENE_PHOTO_NUM",photos.size());
                    List<RecordFileInfo> directions = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "caseId = '" + csSceneCases.getCaseNo() + "' and father = 'SCENE_PICTURE$1082'");
                    caseObject.put("SCENE_PICTURE$1082_NUM",directions.size());
                    List<RecordFileInfo> plans = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "caseId = '" + csSceneCases.getCaseNo() + "' and father = 'SCENE_PICTURE$1010'");
                    caseObject.put("SCENE_PICTURE$1010_NUM",plans.size());
                    List<EvidenceExtra> evidenceExtras = EvidenceApplication.db.findAllByWhere(EvidenceExtra.class,
                            "caseId = '" + csSceneCases.getCaseNo() + "'");
                    caseObject.put("EVIDENCE_EXTRA_NUM",evidenceExtras.size());
                    CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(csSceneCases.getCaseNo(), "SCENE_INVESTIGATION_EXT");
                    try {
                        JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());
                        caseObject.put("INVESTIGATION_DATE_TO",jsonObject.get("INVESTIGATION_DATE_TO"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    caseArray.put(caseObject);
                }
                try {
                    bw.append(jsonArray.toString());
                    bw.newLine();
                    bw.append(caseArray.toString());
                    bw.close();
                    writer2.close();
                    Message message = new Message();
                    message.obj = fileName;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void initView(View view){
        mDailyView = view.findViewById(R.id.manager_daily_btn);
        mDailyView.setOnClickListener(this);
        mCommuticationView = view.findViewById(R.id.manager_communication_btn);
        mCommuticationView.setOnClickListener(this);
        mMapView = view.findViewById(R.id.manager_map_btn);
        mMapView.setOnClickListener(this);
        mSyncDataView = view.findViewById(R.id.manager_data_sync_btn);
        mSyncDataView.setOnClickListener(this);
        mSyncDataRemindImg = (ImageView) view.findViewById(R.id.manager_data_sync_remind_img);
        mUploadDataView = view.findViewById(R.id.upload_data_btn);
        mUploadDataView.setOnClickListener(this);
        mPermissionSetting =view.findViewById(R.id.permission_setting_btn);
        mPermissionSetting.setOnClickListener(this);
        test=(TextView)view.findViewById(R.id.test);
        view.findViewById(R.id.log_upload).setOnClickListener(this);

        mInquestFind=view.findViewById(R.id.inquest_find);
        mInquestFind.setOnClickListener(this);
    }

    private void initSyncDataState(){
        StringMap map = new StringMap();
        map.putString("token",mShare.getString("token",""));
        String json = "{\"coredata\":\""+ mShare.getString(Utils.SHARE_SYNC_BASE_DATA_CONDITION,"") +"\",\"addresslist\":\"" + mShare.getString(Utils.SHARE_SYNC_CONTACT_CONDITION,"") +
                "\",\"schedule\":\"" + mShare.getString(Utils.SHARE_SYNC_SCHEDULE_CONDITION,"") + "\"}";
        map.putString("condition",json);
        Netroid.PostHttp("/checkBasedataUpdate", map, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("data");
                    iscoredataTime = "1".equals(result.getString("coredata"));
                    isaddresslistTime = "1".equals(result.getString("addresslist"));
                    isscheduleTime = "1".equals(result.getString("schedule"));
                } catch (JSONException e) {
                    Log.i("zhangsh","onSuccess exception",e);
                    iscoredataTime = false;
                    isaddresslistTime = false;
                    isscheduleTime = false;
                }
                if(iscoredataTime || isaddresslistTime || isscheduleTime) {
                    mSyncDataRemindImg.setVisibility(View.VISIBLE);
                }else{
                    mSyncDataRemindImg.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.i("zhangsh","onError",error);
                iscoredataTime = false;
                isaddresslistTime = false;
                isscheduleTime = false;
                mSyncDataRemindImg.setVisibility(View.INVISIBLE);
            }
        });
    }
}
