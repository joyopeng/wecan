package com.gofirst.scenecollection.evidence.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public class UpdateNewCaseService extends Service {

    private final String TAG = "UpdateNewCaseService";
    public static final String SHARE_DOWNLOAD_CASE_TIME_STAMP = "share_download_case_time_stamp";
    private UpdateNewCaseBinder mBinder;
    private SharePre mShares;
    private String mTimeStamp = "";
    private final String MethodName = "/cases/lastedNoToken";
    private int mNumber = 1;
    private final int PAGE_SIZE = 100;
    private String mCaseNo = "";

    private final int TIME_POST_DELAY = 2*60*1000;  // 2分钟
    private final int MSG_POST_CURRENT = 100;
    private final int MSG_POST_DELAY = 101;
    private Handler mDownloadHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_POST_DELAY:

                    break;
                case MSG_POST_CURRENT:

                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mBinder = new UpdateNewCaseBinder();
        mShares = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        String time = mShares.getString(SHARE_DOWNLOAD_CASE_TIME_STAMP,"");
        mTimeStamp = getCurrentTime();
        if("".equals(time)){
            mTimeStamp = mTimeStamp.substring(0,10) + " 00:00:00";
            //mTimeStamp = "2017-05-23 00:00:00";
        }else if(mTimeStamp.substring(0,10).equals(time.substring(0,10))){
            mTimeStamp = time;
        }
        mDownloadHandler.post(mDownloadNewCaseThread);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        mDownloadHandler.removeCallbacks(mDownloadNewCaseThread);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    public class UpdateNewCaseBinder extends Binder{
        public void startThread(){
            mDownloadHandler.post(mDownloadNewCaseThread);
        }

        public void reStartThread(){
            mDownloadHandler.removeCallbacks(mDownloadNewCaseThread);
            mDownloadHandler.postDelayed(mDownloadNewCaseThread,TIME_POST_DELAY);
        }

        public void stopThread(){
            mDownloadHandler.removeCallbacks(mDownloadNewCaseThread);
        }
    }

    public Runnable mDownloadNewCaseThread = new Runnable(){

        @Override
        public void run() {
            StringMap params = new StringMap();
            params.putString("ver", "1");
            params.putString("verName", Netroid.versionName);
            params.putString("deviceId", Netroid.dev_ID);
            params.putString("pageNumber", String.valueOf(mNumber));
            params.putString("pageSize",String.valueOf(PAGE_SIZE));
            //params.putString("token", mShares.getString("token", ""));
            params.putString("timestamp",mTimeStamp);
            params.putString("userId",mShares.getString("user_id",""));
            Log.i(TAG, "pageNumber = " + mNumber + ";pageSize = " + PAGE_SIZE + ";timestamp = " + mTimeStamp + "; user_id = " + mShares.getString("user_id",""));
            final String currentTime = getCurrentTime();
            Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    long time1 = System.currentTimeMillis();
                    Log.i(TAG, "responsecomplete : " + response);
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            JSONObject jsonObjectdata = null;
                            int length = jsonArray.length();
                            //String currentTime = getCurrentTime();
                            if(length == 0){
                                Log.d(TAG, "responsecomplete : length = 0 ; currentTime = " + currentTime);
                                mNumber = 1;
                                mTimeStamp = currentTime;
                                mShares.put(SHARE_DOWNLOAD_CASE_TIME_STAMP,currentTime);
                                mShares.commit();
                                mDownloadHandler.postDelayed(mDownloadNewCaseThread,TIME_POST_DELAY);
                                return;
                            }
                            Log.d(TAG, "testcomplete : length = " + length + ";" + jsonArray);
                            for (int i = 0; i < length; i++) {
                                jsonObjectdata = jsonArray.getJSONObject(i);
                                mCaseNo = jsonObjectdata.getString("id");
                                //SceneAlarm sceneAlarm = new SceneAlarm();
                                //如果本地有相同接警编号并且是用户新增案件则不更新
                                List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                        "receptionNo = '" + jsonObjectdata.getString("alarmNo") + "' and isManualAddCase = '1'");
                                if (list != null && list.size() > 0){
                                    CsSceneCases csSceneCases = list.get(0);
                                    if (TextUtils.equals(jsonObjectdata.getString("status"),"0")){
                                        String oldId = csSceneCases.getId();
                                        csSceneCases.setId(mCaseNo);
                                        EvidenceApplication.db.deleteById(CsSceneCases.class, oldId);
                                        EvidenceApplication.db.save(csSceneCases);
                                        updateStatus(mCaseNo);
                                    }

                                    continue;
                                }

                                CsSceneCases csSceneCase = new CsSceneCases();
                                csSceneCase.setId(mCaseNo);
                                if(jsonObjectdata.has("alarmPeople")) {
                                    csSceneCase.setAlarmPeople(jsonObjectdata.getString("alarmPeople"));
                                }
                                csSceneCase.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
                                csSceneCase.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
                                csSceneCase.setSceneRegionalismName(jsonObjectdata.getString("sceneRegionalismName"));
                                csSceneCase.setAlarmTel(jsonObjectdata.getString("alarmTel"));
                                csSceneCase.setAlarmDatetime(jsonObjectdata.getString("alarmDatetime"));
                                csSceneCase.setSortListDateTime(jsonObjectdata.getString("alarmDatetime"));
                                csSceneCase.setAlarmAddress(jsonObjectdata.getString("alarmAddress"));
                                csSceneCase.setRemark(jsonObjectdata.getString("remark"));
                                csSceneCase.setReceptionNo(jsonObjectdata.getString("alarmNo"));
                                csSceneCase.setAlarmCategory(jsonObjectdata.getString("alarmCategory"));
                                csSceneCase.setAlarmCategoryName(jsonObjectdata.getString("alarmCategoryName"));
                                csSceneCase.setStatus(jsonObjectdata.getString("status"));
                                if(jsonObjectdata.has("updateUser")) {
                                    csSceneCase.setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                                }
                                /*sceneAlarm.setId(jsonObjectdata.getString("id"));
                                if(jsonObjectdata.has("alarmPeople")) {
                                    sceneAlarm.setAlarmPeople(jsonObjectdata.getString("alarmPeople"));
                                }
                                sceneAlarm.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
                                sceneAlarm.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
                                sceneAlarm.setSceneRegionalismName(jsonObjectdata.getString("sceneRegionalismName"));
                                sceneAlarm.setAlarmTel(jsonObjectdata.getString("alarmTel"));
                                sceneAlarm.setAlarmDatetime(jsonObjectdata.getString("alarmDatetime"));
                                sceneAlarm.setAlarmAddress(jsonObjectdata.getString("alarmAddress"));
                                sceneAlarm.setRemark(jsonObjectdata.getString("remark"));
                                sceneAlarm.setReceptionNo(jsonObjectdata.getString("alarmNo"));
                                sceneAlarm.setStatus(jsonObjectdata.getString("status"));

                                List<SceneAlarm> SceneCaselist = EvidenceApplication.db.findAllByWhere(SceneAlarm.class,
                                        "id = '" + mCaseNo + "'");*/
                                List<CsSceneCases> sceneCasesList = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                        "id = '" + mCaseNo + "'");
                                if (sceneCasesList.size() == 0) {
                                    if(jsonObjectdata.getString("status")!=null&&
                                            jsonObjectdata.getString("status").equals("0")) {
                                        EvidenceApplication.db.save(csSceneCase);
                                    }
                                } else {
                                    String status = sceneCasesList.get(0).getStatus();
                                    if ("0".equals(status)) {
                                        EvidenceApplication.db.update(csSceneCase);
                                        sceneCasesList.get(0).setStatus(jsonObjectdata.getString("status"));
                                        if(jsonObjectdata.getString("updateUser")!=null&&jsonObjectdata.has("updateUser")) {
                                            sceneCasesList.get(0).setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                                        }
                                        EvidenceApplication.db.update(sceneCasesList.get(0));
                                    }
                                   // if ("0".equals(status)) {
                                        //EvidenceApplication.db.update(csSceneCase);
                                   // } else  {
                                       // if(!sceneCasesList.get(0).getReceivePeopleNum().toString().equals( mShares.getString("userId",""))){
                                        //sceneCasesList.get(0).setStatus(jsonObjectdata.getString("status"));
                                        //if(jsonObjectdata.getString("updateUser")!=null&&jsonObjectdata.has("updateUser")) {
                                        //    sceneCasesList.get(0).setReceivePeopleNum(jsonObjectdata.getString("updateUser"));
                                        //}
                                       // EvidenceApplication.db.update(sceneCasesList.get(0));
                                      //  }

                                   // }
                                }
                                if(i == length - 1){
                                    if(length == PAGE_SIZE){
                                        mNumber += 1;
                                        mDownloadHandler.post(mDownloadNewCaseThread);
                                    }else{
                                        mNumber = 1;
                                        //mTimeStamp = jsonObjectdata.getString("alarmDatetime");
                                        mTimeStamp = currentTime;
                                        mShares.put(SHARE_DOWNLOAD_CASE_TIME_STAMP,currentTime);
                                        mShares.commit();
                                        mDownloadHandler.postDelayed(mDownloadNewCaseThread,TIME_POST_DELAY);
                                        Intent in = new Intent();
                                        in.setAction("update_new_case_broadcast");
                                        in.putExtra("update","1");
                                        sendBroadcast(in);
                                    }
                                }
                            }
                        }else{
                            mDownloadHandler.postDelayed(mDownloadNewCaseThread,TIME_POST_DELAY);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mDownloadHandler.postDelayed(mDownloadNewCaseThread, TIME_POST_DELAY);
                    }
                    Log.i(TAG,"update new case service host time = " + (System.currentTimeMillis() - time1));
                }

                @Override
                public void onError(NetroidError error) {
                    Log.d("error", "" + error);
                    mDownloadHandler.postDelayed(mDownloadNewCaseThread, TIME_POST_DELAY);
                }
            });
        }
    };

    private String getCurrentTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


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

}
