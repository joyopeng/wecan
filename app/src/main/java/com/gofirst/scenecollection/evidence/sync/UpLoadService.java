package com.gofirst.scenecollection.evidence.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.IMyAidlInterface;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.model.UploadFile;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.DateTimeUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.SelectUploadCase;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.tdtech.devicemanager.DevicePolicyManager;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.gofirst.scenecollection.evidence.Application.EvidenceApplication.latitude;

/**
 * @author maxiran 2016/5/10.
 *         上传文件服务
 */
public class UpLoadService extends Service {

    private SharePre userInfo;
    private RequestQueue requestQueue;
    private MyBinder binder;
    private MyConn conn;
    private int triggerTime = 6 * 1000;
    private int triggerCount;
    private ThreadPoolExecutor taskPoolExecutor;
    private static BufferedWriter bw;
    static FileWriter writer2;
    private SharedPreferences uploadfilerec;

    class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return UpLoadService.class.getSimpleName();
        }
    }

    class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(UpLoadService.this, "远程服务killed", Toast.LENGTH_SHORT).show();
            //开启远程服务
            UpLoadService.this.startService(new Intent(UpLoadService.this, RemoteService.class));
            //绑定远程服务
            UpLoadService.this.bindService(new Intent(UpLoadService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        }
    }

    public UpLoadService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        conn = new MyConn();
        taskPoolExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(128));
        inItRequestQueue();
        userInfo = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        uploadfilerec = getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //每隔十分钟生成一次补录数据
        if (triggerCount % (600 * 1000 / triggerTime) == 0) {
            //生成补录上传数据
            taskPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SelectUploadCase.generateAddUploadJson(userInfo);
                }
            });

        }
        startAlarm();
        boolean manualStart = userInfo.getBoolean("manualStart", false);
        //auto upload, this function will be open later
//                if (isUploadTime() && taskPoolExecutor.getActiveCount() < 3) {
//                   taskPoolExecutor.execute(new Runnable() {
//                       @Override
//                       public void run() {
//                           startUpload();
//                       }
//                   });
//                }
        if (manualStart) {
            if (!isSpecificFileUpload() && taskPoolExecutor.getActiveCount() < 3) {
                taskPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        startSpecificUpload();
                    }
                });
            }
        }
        if (manualStart) {
            if (isSpecificFileUpload()) {
                userInfo.put("manualStart", false);
                userInfo.commit();
            }
        }
        this.bindService(new Intent(UpLoadService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //开启远程服务
        UpLoadService.this.startService(new Intent(UpLoadService.this, RemoteService.class));
        //绑定远程服务
        UpLoadService.this.bindService(new Intent(UpLoadService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        super.onDestroy();
    }

//    private void monitorDeviceInfo(){
//        Map<String, String> param = new HashMap<>();
//        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        param.put("serialNo","");
//        param.put("mobileModel",android.os.Build.MODEL);
//        param.put("userId",userInfo.getString("user_id",""));
//        param.put("tel",mTm.getLine1Number());
//        param.put("machineCode",mTm.getDeviceId());
//        param.put("deviceNo","");
//        param.put("grantDatetime","");
//        param.put("sysNo",android.os.Build.VERSION.RELEASE);
//        param.put("longitude",EvidenceApplication.longitude+"");
//        param.put("latitude", latitude+"");
//        param.put("modularList","");
//        UnUploadSingleJson.PostByParamsRequest jsonObjectRequest =
//                new UnUploadSingleJson.PostByParamsRequest(PublicMsg.BASEURL + "/deviceInfo/beating",param,new Listener<String>(){
//
//
//            @Override
//            public void onSuccess(String response) {
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(response);
//                    boolean isSuccess = jsonObject.getBoolean("success");
//                    if (isSuccess){
//                       String id = jsonObject.getString("data");
//                       /*
//                       -1：保存定位信息异常
//                       -2：保存设备记录异常
//                        1：正常
//                        2：已挂失
//                        */
//                        switch (id){
//                            case "-1":
//                                break;
//                            case "1":
//                                break;
//                            case "-2":
//                                break;
//                            case "2":
//                                DevicePolicyManager m  = DevicePolicyManager.getInstance(UpLoadService.this.getApplicationContext());
//                                com.tdtech.devicemanager.SecurityPolicy securityPolicy = m.getSecurityPolicy();
//                                securityPolicy.setFactoryReset(true);
//                                break;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(NetroidError error) {
//
//            }
//        });
//        jsonObjectRequest.setForceUpdate(true);
//        jsonObjectRequest.setCacheExpireTime(TimeUnit.MINUTES, 0);
//        jsonObjectRequest.addHeader("ver", "1");
//        jsonObjectRequest.addHeader("verName", Netroid.versionName);
//        jsonObjectRequest.addHeader("deviceId", Netroid.dev_ID);
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000,1,1f));
//        requestQueue.add(jsonObjectRequest);
//    }

    private void startSpecificUpload() {
        //分割指定文件 256kb 一份
        List<CsSceneCases> caseList = EvidenceApplication.
                db.findAllByWhere(CsSceneCases.class, "status = '3'and isSpecific = '1'");

        Log.d("onRunning", "onStartSpecific");
        writerInfo("start init createBlocking");
        for (UploadFile recordFileInfo : getFiles(caseList)) {
            writerInfo("start " + recordFileInfo.getFilePath() + "createBlocking");
            new CreateFileBlocks().startCreateBlocks(recordFileInfo, 256 * 1024, true, recordFileInfo.getCaseId());
        }
        //上传指定媒体文件
        String sql = "select * from UnUpLoadBlock where isSpec = '1'";
        List<DbModel> dbModels = EvidenceApplication.db.findDbModelListBySQL(sql);
        int count = dbModels.size();
        int index = 0;
        if (count > 2) {
            Random random = new Random();
            index = random.nextInt(count);
        }
        String specSql = "select * from UnUpLoadBlock where isSpec = '1' order by id limit " + index + ",2";
        Log.d("onRunning", specSql);
        for (UnUpLoadBlock unUpLoadBlock : getUnLoadBlock(specSql)) {
            new UpLoadSingleBlock(uploadfilerec).startUpLoadSingleBlock(unUpLoadBlock, requestQueue);
        }
        //上传指定文本
        String specJsonSql = "select * from UnUploadJson where isSpec = '1'and uploaded = '0'";
        for (UnUploadJson unUploadJson : getUnUploadJson(specJsonSql)) {
            if (isThisCaseAllFileUpload(unUploadJson.getCaseId())) {
                new UnUploadSingleJson().startUploadSingleJson(unUploadJson, getApplicationContext(), requestQueue);
                break;
            } else {
                writerInfo("caseId = " + unUploadJson.getCaseId() + "still has block no upload can not upload json file");
            }
        }
    }


    private boolean isSpecificFileUpload() {
        List<UnUploadJson> list = getUnUploadJson("select * from UnUploadJson where isSpec = '1' and uploaded = '0'");
        return list == null || list.size() == 0;
    }

    private void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + triggerTime;
        triggerCount = triggerAtTime < Integer.MAX_VALUE ? (triggerCount + 1) : 0;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        try {
            JSONObject jsonObject = Utils.getDeviceInfoJsonObject();
            jsonObject.put("latitude", latitude + "");
            jsonObject.put("longitude", latitude + "");
            Utils.saveDeviceInfo(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<UnUpLoadBlock> getUnLoadBlock(String sql) {
        List<UnUpLoadBlock> list = new ArrayList<>();
        List<DbModel> dbModels = EvidenceApplication.db.findDbModelListBySQL(sql);
        for (DbModel dbModel : dbModels) {
            UnUpLoadBlock block = new UnUpLoadBlock();
            block.setBlockTotal(dbModel.getInt("blockTotal"));
            block.setParentPath(dbModel.getString("parentPath"));
            block.setBlockIndex(dbModel.getInt("blockIndex"));
            block.setPath(dbModel.getString("path"));
            block.setId(dbModel.getString("id"));
            block.setUploading(dbModel.getBoolean("isUploading"));
            block.setCaseId(dbModel.getString("caseId"));
            list.add(block);
        }
        return list;
    }

    private List<UnUploadJson> getUnUploadJson(String sql) {
        List<UnUploadJson> list = new ArrayList<>();
        List<DbModel> dbModels = EvidenceApplication.db.findDbModelListBySQL(sql);
        for (DbModel dbModel : dbModels) {
            UnUploadJson unUploadJson = new UnUploadJson();
            unUploadJson.setId(dbModel.getString("id"));
            unUploadJson.setCaseId(dbModel.getString("caseId"));
            unUploadJson.setJson(dbModel.getString("json"));
            unUploadJson.setUploaded(TextUtils.equals("1", dbModel.getString("uploaded")));
            unUploadJson.setAddRec(TextUtils.equals("1", dbModel.getString("addRec")));
            unUploadJson.setSpec(TextUtils.equals("1", dbModel.getString("isSpec")));
            list.add(unUploadJson);
        }
        return list;
    }

//    private void startUpload() {
//        //分割文件 256kb 一份
//        List<CsSceneCases> caseList = getAllFinishCase();
//        for (UploadFile recordFileInfo : getFiles(caseList)) {
//            new CreateFileBlocks().startCreateBlocks(recordFileInfo, 256 * 1024,false,recordFileInfo.getCaseId());
//        }
//        //上传媒体文件
//        for (UnUpLoadBlock unUpLoadBlock : getUnLoadBlock("select * from UnUpLoadBlock order by id limit 0,2")) {
//            new UpLoadSingleBlock().startUpLoadSingleBlock(unUpLoadBlock,requestQueue);
//        }
//        //上传文本
//        for (UnUploadJson unUploadJson : getUnUploadJson("select * from UnUploadJson where uploaded = '0' limit 0,1")) {
//            if (isThisCaseAllFileUpload(unUploadJson.getCaseId()))
//                new UnUploadSingleJson().startUploadSingleJson(unUploadJson, getApplicationContext(),requestQueue);
//        }
//    }
//
//    private boolean isUploadTime() {
//        List<User> userList = EvidenceApplication.db.findAllByWhere(User.class, "userId = '" + userInfo.getString("userId", "") + "'");
//        if (userList == null || userList.size() == 0 || (userList.get(0).getAutoUploadSoltTime() == null || userList.get(0).getAutoUploadSoltTime().equals("")))
//            return false;
//        String[] uploadTimes = userList.get(0).getAutoUploadSoltTime().split(";");
//        for (String uploadTime : uploadTimes) {
//            String[] time = uploadTime.split("-");
//            if (isBetweenTime(time[0], time[1]))
//                return true;
//        }
//        return false;
//    }

    private List<UploadFile> getFiles(List<CsSceneCases> caseList) {
        List<UploadFile> fileInfoList = new ArrayList<>();
        for (CsSceneCases csSceneCases : caseList) {
            List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + csSceneCases.getCaseNo() + "' and (hasBlock = '0' or hasBlock is null) limit 0,1");
            for (RecordFileInfo recordFileInfo : list) {
                if (!checkRecAlreadyHasUploadFile(recordFileInfo.getId())) {
                    if (!TextUtils.isEmpty(recordFileInfo.getFilePath()) && recordFileInfo.getFilePath().contains("xckydb")) {
                        fileInfoList.add(createUploadFile(recordFileInfo.getId(), Environment.getExternalStorageDirectory().getPath() + "/" + recordFileInfo.getFilePath(), recordFileInfo.getCaseId()));
                    } else {
                        if (!TextUtils.isEmpty(recordFileInfo.getFilePath()))
                            fileInfoList.add(createUploadFile(recordFileInfo.getId(), AppPathUtil.getDataPath() + "/" + recordFileInfo.getFilePath(), recordFileInfo.getCaseId()));
                        if (!TextUtils.isEmpty(recordFileInfo.getContractionsFilePath()))
                            fileInfoList.add(createUploadFile(recordFileInfo.getId(), AppPathUtil.getDataPath() + "/" + recordFileInfo.getContractionsFilePath(), recordFileInfo.getCaseId()));
                        if (!TextUtils.isEmpty(recordFileInfo.getTwoHundredFilePath()))
                            fileInfoList.add(createUploadFile(recordFileInfo.getId(), AppPathUtil.getDataPath() + "/" + recordFileInfo.getTwoHundredFilePath(), recordFileInfo.getCaseId()));
                    }
                } else {
                    fileInfoList.addAll(getUnUploadFile(recordFileInfo.getId()));
                }
            }
        }
        writerInfo("ready to create block file size is " + fileInfoList.size());
        return fileInfoList;
    }

    /**
     * 判断当前时间是否在指定时间间隔（可以跨日）
     */
    private boolean isBetweenTime(String startTime, String endTime) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        String startTimeHour = startTime.split(":")[0];
        String endTimeHour = endTime.split(":")[0];
        String startTimeMinute = startTime.split(":")[1];
        String endTimeMinute = endTime.split(":")[1];
        int currentMinutes = currentHour * 60 + currentMinute;
        boolean isAfterStart = currentMinutes > Integer.parseInt(startTimeHour) * 60 + Integer.parseInt(startTimeMinute);
        boolean isBeforeEnd = currentMinutes < Integer.parseInt(endTimeHour) * 60 + Integer.parseInt(endTimeMinute);
        return Integer.parseInt(startTimeHour) > Integer.parseInt(endTimeHour) ? isAfterStart || isBeforeEnd : isAfterStart && isBeforeEnd;
    }


    private List<CsSceneCases> getAllFinishCase() {
        return EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "status = '3'");
    }

    private boolean checkRecAlreadyHasUploadFile(String parentId) {
        List<UploadFile> list = EvidenceApplication.db.findAllByWhere(UploadFile.class, "parentId = '" + parentId + "'");
        return list != null && list.size() != 0;
    }

    private UploadFile createUploadFile(String parentId, String filePath, String caseId) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(ViewUtil.getUUid());
        uploadFile.setFilePath(filePath);
        uploadFile.setParentId(parentId);
        uploadFile.setCaseId(caseId);
        /*EvidenceApplication.db.save(uploadFile);*/
        return uploadFile;
    }

    private List<UploadFile> getUnUploadFile(String parentId) {
        return EvidenceApplication.db.findAllByWhere(UploadFile.class, "parentId = '" + parentId + "' and isUpload = '0'");
    }

    private boolean isThisCaseAllFileUpload(String caseId) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'");
        for (RecordFileInfo recordFileInfo : list) {
            //未产生分块
            if (!recordFileInfo.isHasBlock()) {
                Log.d("onRun", "noBlock");
                return false;
            }
            //产生分块但未上传完毕
            if (!isThisRecAllUpload(recordFileInfo.getId())) {
                Log.d("onRun", "noBlockUploadFinish");
                return false;
            }
        }
        return true;
    }


    private boolean isThisRecAllUpload(String parentId) {
        //如果上传完必有 uploadFile
        List<UploadFile> list = EvidenceApplication.db.findAllByWhere(UploadFile.class, "parentId = '" + parentId + "'");
        if (list == null || list.size() == 0) {
            Log.d("onRun", "noUploadFile");
            return false;
        }

        for (UploadFile uploadFile : list) {
            // isUpload()标记是否产生上传分块
            if (!uploadFile.isUpload()) {
                Log.d("onRun", "UploadFileNoUpload");
                return false;
            }

            // 分块上传完毕会自动删除，若存在没有上传完
            if (checkFileHasBlocks(uploadFile.getFilePath())) {
                Log.d("onRun", "UploadFileHasBlock");
                return false;
            }
        }
        return true;
    }

    private boolean checkFileHasBlocks(String parentPath) {
        List<UnUpLoadBlock> list = EvidenceApplication.db.findAllByWhere(UnUpLoadBlock.class,
                "parentPath = '" + parentPath + "'");
        return list != null && list.size() != 0;
    }


    private void inItRequestQueue() {
        if (requestQueue != null)
            throw new IllegalStateException("initialized");
        Network network = new BasicNetwork(new HurlStack("evidence", null), "UTF-8");
        requestQueue = new RequestQueue(network, 4, new DiskCache(new File(
                this.getCacheDir(), "evidence_block"),
                50 * 1024 * 1024));
        requestQueue.start();
    }

    private static void initWriter() throws IOException {
        String path = AppPathUtil.getLogPath() + "/logs";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "UploadServiceLogs_" + DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_CN_Y_M_D) + ".log";
        File f = new File(path + "/" + fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        writer2 = new FileWriter(path + "/" + fileName, true);
        bw = new BufferedWriter(writer2);
    }

    public static void writerInfo(String s) {
        try {
            initWriter();
            if (bw != null) {
                bw.append(DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_EN_Y_M_D_H_M_S) + ":" + s);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bw != null && writer2 != null)
            try {
                bw.close();
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
