package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archermind.policesdk.PoliceService;
import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.sync.ParseAssertService;
import com.gofirst.scenecollection.evidence.sync.UpLoadService;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.DateTimeUtil;
import com.gofirst.scenecollection.evidence.utils.DownLoadAsync;
import com.gofirst.scenecollection.evidence.utils.NetUtilPing;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringUtil;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.utils.UpLoadLog;
import com.gofirst.scenecollection.evidence.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gofirst.scenecollection.evidence.Application.PublicMsg.isDebug;

/**
 * @author maxiran
 */
public class Splash extends Activity {
    //lijiuxiang
    //111
    private String localPath = AppPathUtil.getCachePath() + "/" + OSUtil.getUUid() + ".apk";

    private LinearLayout llloading;
    boolean isGotoLogin = false;
    SharePre sharePre;
    private BufferedWriter bw;
    FileWriter writer2;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(1);
        setContentView(R.layout.splash_layout);
        sharePre = new SharePre(Splash.this, "user_info", Context.MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
        ImageView imageView = (ImageView) findViewById(R.id.welcome);
        llloading = (LinearLayout) findViewById(R.id.llloading);

        //清空缓存文件夹
        AppPathUtil.deleteAllFiles(new File(AppPathUtil.getCachePath()));
//        if (StringUtil.isNullorEmpty(sharePre.getString("token", ""))) {
//            startService(new Intent(this, ParseAssertService.class));
//        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                gotoLogin();
            }
        }, 2000);
        getLastVersion();
        try {
            initWriter();
            writerInfo("splash onCreate start");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
//        alphaAnimation.cancel();
        if (bw != null && writer2 != null)
            try {
                bw.close();
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onDestroy();
    }

    private void getLastVersion() {
        llloading.setVisibility(View.VISIBLE);
        Netroid.GetHttp("/ver/lasted", new Netroid.OnLister<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    llloading.setVisibility(View.GONE);
                    boolean isSuccess = response.getBoolean("success");
                    if (isSuccess) {
                        final JSONObject data = response.getJSONObject("data");
                        if (data == null || data.toString().equals("")) {
                            gotoLogin();
                            return;
                        }
                        String verCode = (data.has("versionCode") ? data.getString("versionCode") : "");
                        String versionName = (data.has("versionName") ? data.getString("versionName") : "");
                        String publish = (data.has("publish") ? data.getString("publish") : "");
                        String levell = (data.has("levell") ? data.getString("levell") : "");
                        final String path = (data.has("path") ? data.getString("path") : "");
                        String fileUrl = (data.has("path") ? data.getString("path") : "");
                        String updateLog = (data.has("updateLog") ? data.getString("updateLog") : "");
                        if (!"1".equals(publish)) {
                            gotoLogin();
                            return;
                        }
                        if (verCode == null || "".equals(verCode) || versionName == null || "".equals(versionName)
                                || path == null || "".equals(path)) {
                            gotoLogin();
                            return;
                        }
                        if (Integer.parseInt(verCode) <= OSUtil.getVersionCode(Splash.this)) {
                            gotoLogin();
                            return;
                        }
                        //{"versionCode":1,"hostId":"","versionName":"1.0.1",
                        // "id":"fd559ebe8abf4334ad16c36178f9c869","orgId":"1","createUser":"","levell":"0","thirdUrl":"",
                        // "createDatetime":"2016-08-22 16:06:04","path":"app\/2016\/08\/22\/a92246da-92fd-4a40-ac34-a6dea0a913bd.jpg",
                        // "updateLog":"第一版","publish":"1","deleteFlag":"0","updateUser":""}


                        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                        final boolean isImportant = ("1".equals(levell));
                        builder.setTitle(isImportant ? "重要更新" : "一般更新");
                        View view = LayoutInflater.from(Splash.this).inflate(R.layout.log_text, null);
                        builder.setView(view);
                        builder.setCancelable(false);
                        TextView log = (TextView) view.findViewById(R.id.update_log);
                        log.setText(updateLog);
                        builder.setPositiveButton("安装",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //downloadApk(path);
                                        //downloadApk(path, localPath);
                                        Map<String, String> param = new HashMap<String, String>();
                                        //       param.put("fileUrl", path);
                                        param.put("isApk", "apk");
                                        /*downloadApk(path, localPath, param);*/

                                        param.put("mapPath", path);
                                        downloadApk(PublicMsg.BASEURL + "/download", localPath, param);

                                    }
                                });

                        builder.setNegativeButton(isImportant ? "退出" : "忽略",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (!isImportant) {
                                            gotoLogin();
                                        } else {
                                            finish();
                                        }
                                    }

                                });
                        AlertDialog adlg = builder.create();
                        adlg.show();
                    } else {
                        gotoLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    gotoLogin();
                }

            }

            @Override
            public void onError(NetroidError error) {
//                if (dialog.isShowing())
//                    dialog.dismiss();
                gotoLogin();
            }

        }, null);
    }

    private void gotoLogin() {
        if (!isGotoLogin) {
            isGotoLogin = true;
            return;
        }
        writerInfo("start get PoliceAccount");
        PoliceService ps = new PoliceService(this);
        final String user = ps.getUserAccount();
        writerInfo("start get PoliceAccount is " + user);
        ToastUtil.show(this, "获取到用户信息为" + user, Toast.LENGTH_SHORT);
        writerInfo("request loginNoPassword ");
        Netroid.GetHttp("/loginNoPasswordByGet?account=" + user, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                boolean isSuccess = false;
                try {
                    isSuccess = response.getBoolean("success");
                    writerInfo("request loginNoPassword success " + response.toString());
                    if (isSuccess) {
                        Log.i("zhangsh", "reponse = " + response);
                        JSONObject data = response.getJSONObject("data");
                        sharePre.put("token", data.getString("token"));
                        sharePre.put("user_name", user);
                        sharePre.put("organizationId", data.getString("organizationId"));
                        //add on
                        JSONObject userData = data.getJSONObject("user");
                        JSONObject hyEmployees = data.getJSONObject("hyEmployees");
                        JSONObject hyOrganizations = data.getJSONObject("hyOrganizations");
                        JSONObject hyCompartments = null;
                        if (!data.isNull("hyCompartments")) {
                            hyCompartments = data.getJSONObject("hyCompartments");
                        }
                        String alarmViewPurview = data.getString("alarmViewPurview");

                        sharePre.put("userId", userData.getString("employeeId"));
                        sharePre.put("user_id", userData.getString("userId"));
                        sharePre.put("organizationId", userData.getString("organizationId"));
                        sharePre.put("prospectPerson", hyEmployees.getString("employeeName"));
                        sharePre.put("employeeNo", hyEmployees.getString("employeeNo"));
                        sharePre.put("organizationCname", hyOrganizations.getString("organizationCname"));
                        if (hyCompartments != null) {
                            sharePre.put("compartmentNo", hyCompartments.getString("compartmentNo"));
                            sharePre.put("compartmentName", hyCompartments.getString("compartmentName"));
                        }

                        sharePre.put("alarmViewPurview", data.getString("alarmViewPurview"));
                        writerInfo("start saveUser");
                        saveUser();
                        //add off
                        sharePre.commit();
                        writerInfo("start getLogFile");
                        ArrayList<String> arrayList = UpLoadLog.GetLogFileName(AppPathUtil.getLogPath() + "/logs");
                        Log.d("UpLoadLog", "" + arrayList.size());
                        sharePre.put("lastLoginTime", System.currentTimeMillis());
                        sharePre.commit();
                        writerInfo("start go to MainActivity");
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                    } else {
//                    Toast.makeText(Splash.this, "用户不存在请手动登录", Toast.LENGTH_SHORT).show();
                        if (isDebug) {
                            startActivity(new Intent(Splash.this, Login.class));
                        } else {
                            startActivity(new Intent(Splash.this, NoAccountActivity.class));
                        }
                        finish();
                    }
                } catch (JSONException e) {
                    writerInfo("request loginNoPassword JSONException !" + e.toString());
                }
            }

            @Override
            public void onError(NetroidError error) {
                Toast.makeText(Splash.this, "网络超时尝试离线登录", Toast.LENGTH_SHORT).show();
                if (error != null) {
                    writerInfo("request loginNoPassword netWorkError !" + error.getMessage());
                    writerInfo("request loginNoPassword netWorkError cause: " + error.toString());
                }
                if (StringUtil.isNullorEmpty(error.getMessage())) {
                    new Thread() {
                        @Override
                        public void run() {
                            String domain;
                            if(isDebug){
                                domain = "192.168.191.1";
                            }else {
                                domain = "172.168.0.199";
                            }
                            NetUtilPing.ping(domain, 4, fileName);
                        }
                    }.start();
                }
                if (isExistUser()) {
                    User user = EvidenceApplication.db.findAllByWhere(User.class, "userName = '" + new PoliceService(Splash.this).getUserAccount() + "'").get(0);
                    sharePre.put("token", user.getToken());
                    sharePre.put("user_name", user.getUserName());
                    sharePre.put("organizationId", user.getOrganizationId() + "");
                    sharePre.put("userId", user.getUserId());
                    sharePre.put("prospectPerson", user.getProspectPerson());
                    sharePre.put("lastLoginTime", System.currentTimeMillis());
                    sharePre.commit();
                    startActivity(new Intent(Splash.this, MainActivity.class));
//                    finish();
                    Toast.makeText(Splash.this, "离线登录", Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(Splash.this, "不是已登录用户", Toast.LENGTH_SHORT).show();
                    if (isDebug) {
                        startActivity(new Intent(Splash.this, Login.class));
                    } else {
                        startActivity(new Intent(Splash.this, NoAccountActivity.class));
                    }
                    finish();
                }
            }
        }, null);
    }

    private boolean isExistUser() {
        List<User> userList = EvidenceApplication.db.findAllByWhere(User.class, "userName = '" + new PoliceService(Splash.this).getUserAccount() + "'");
        return userList != null && userList.size() != 0 && !TextUtils.isEmpty(userList.get(0).getPassword());
    }

    private void saveUser() {
        User user = getUser();
        user.setUserName(new PoliceService(Splash.this).getUserAccount());
        //         user.setPassword(passEditText.getText().toString());
        user.setToken(sharePre.getString("token", ""));
        String orId = sharePre.getString("organizationId", "");
        user.setOrganizationId(Integer.valueOf(TextUtils.isEmpty(orId) ? "1" : orId));
        user.setProspectPerson(sharePre.getString("prospectPerson", ""));
        user.setUserId(sharePre.getString("userId", ""));
        user.setUserNameId(sharePre.getString("user_id", ""));
        //user.setPermissionSetting(sharePre.getString("alarmViewPurview", ""));
        if (user.getId() != 0) {
            EvidenceApplication.db.update(user);
        } else {
            EvidenceApplication.db.save(user);
        }
    }

    private User getUser() {
        List<User> list = EvidenceApplication.db.findAllByWhere(User.class, "userId = '" + sharePre.getString("userId", "") + "'");
        if (list != null && list.size() != 0)
            return list.get(0);
        return new User();

    }
    /*private void downloadApk(String path){
        FinalHttp fh = new FinalHttp();
        fh.download(path,localPath, new AjaxCallBack<File>() {
                    @Override
                    public void onStart() {
                        Utils.startProgressDialog(Splash.this,"",String.format("开始下载..."),false,false);
                        super.onStart();
                    }

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLoading(long count, long current) {
                        super.onLoading(count, current);
                        int progress = 0;
                        if (current != count && current != 0) {
                            progress = (int) (current / (float) count * 100);
                        } else {
                            progress = 100;
                        }
                        Utils.updateProgressDialog(Splash.this,String.format("正在下载中...%s%%", progress));
                    }

                    @Override
                    public void onSuccess(File t) {
                        super.onSuccess(t);
                        Utils.stopProgressDialog();
                        ToastUtil.showShort(Splash.this, "下载文件完成!");
                        OSUtil.installApk(Splash.this,new File(localPath));
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        super.onFailure(t, errorNo, strMsg);

                        Utils.stopProgressDialog();
                        ToastUtil.showShort(Splash.this, "下载失败!");
                        getLastVersion();
                    }
                });
    }*/

    private void downloadApk(final String path, final String savePath, Map<String, String> param) {
        DownLoadAsync async = new DownLoadAsync(this, true, false, new Handler(), param);
        async.setListener(new DownLoadAsync.DownLoadAsyncListener() {
            @Override
            public void onDownloadSuccess() {
                File file = new File(savePath);
                installApk(file);
                Splash.this.finish();
            }

            @Override
            public void onDownloadFail() {
                Toast.makeText(Splash.this, "下载版本失败，请确认是否正常连接！", Toast.LENGTH_SHORT).show();
                gotoLogin();
            }

            @Override
            public void onDonwloadUpdate(int value) {

            }

            @Override
            public void onCancled() {
                Toast.makeText(Splash.this, "取消下载！", Toast.LENGTH_SHORT).show();
                gotoLogin();
            }
        });
        async.execute(path, savePath);
    }

    private void installApk(File apk) {
        if (apk.exists() && apk.length() > 0) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

    private void initWriter() throws IOException {
        String path = AppPathUtil.getLogPath() + "/logs";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fileName = "SplashLogs_" + DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_EN_Y_M_D_H_M_S) + ".log";
        File f = new File(path + "/" + fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        writer2 = new FileWriter(path + "/" + fileName, true);
        bw = new BufferedWriter(writer2);
    }

    private void writerInfo(String s) {
        try {
            if (bw != null) {
                bw.append(DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_EN_Y_M_D_H_M_S) + ":" + s);
                bw.newLine();
            }
            Log.v("Splash", s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
