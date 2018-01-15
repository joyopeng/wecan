package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.DownLoadAsync;
import com.gofirst.scenecollection.evidence.utils.Netroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/30.
 */
public class CheckUpdate extends Activity implements View.OnClickListener{

    private View mNewVersion;
    private TextView mTitleText;
    private ImageView mBackImg;
    private TextView mVersionText;
    private TextView mNewVersionText;
    private int mCurrentVersionCode = -1;
    private int mNewVersionCode = -1;

    private final String VERSION_INTERFACE = "/ver/lasted";
    private String mDownloadUrl = "";
    private String fileUrl="";
    //private final String DOWNLOAD_FILE_PATH = Environment.getExternalStorageDirectory() + "/";
    private final String DOWNLOAD_FILE_PATH = AppPathUtil.getCachePath()+"/";

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_update);
        Init();
    }

    public void Init(){
        mNewVersion = findViewById(R.id.new_version_layout);
        mNewVersion.setOnClickListener(this);
        mTitleText = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleText.setText("关于");
        mBackImg = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImg.setOnClickListener(this);
        mVersionText = (TextView) findViewById(R.id.now_version_tv);
        mVersionText.setText(getVersion() + " for Android");
        mNewVersionText = (TextView) findViewById(R.id.new_version_tv);
        getLastVersion();
    }

    private void getLastVersion(){
        Netroid.GetHttp(VERSION_INTERFACE, new Netroid.OnLister<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean isSuccess = response.getBoolean("success");
                    if (isSuccess) {
                        final JSONObject data = response.getJSONObject("data");
                        Log.d("updateLog",response+"");
                        if (!data.toString().equals("")) {
                            mNewVersionCode = data.getInt("versionCode");
                            if(mNewVersionCode > mCurrentVersionCode) {
                                mNewVersionText.setText("新版本可用 - " + data.getString("versionName"));
                                mDownloadUrl = data.getString("path");
                                fileUrl = (data.has("path")?data.getString("path"):"");
                            }else{
                                mNewVersionText.setText("当前版本已是最新版本！");
                            }
                        } else {
                            mNewVersionText.setText("检测不到最新版本！");
                        }
                    } else {
                        mNewVersionText.setText("检测不到最新版本！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mNewVersionText.setText("检测不到最新版本！");
                }

            }

            @Override
            public void onError(NetroidError error) {
                mNewVersionText.setText("检测不到最新版本！");
            }
        }, null);
    }

    private String getVersion(){
        String result = "V1.01";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(),0);
            result = info.versionName;
            mCurrentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  result;
    }

    private void downloadApk(final String path,final String savePath,Map<String,String> param){
        DownLoadAsync async = new DownLoadAsync(this,true,false,new Handler(),param);
        async.setListener(new DownLoadAsync.DownLoadAsyncListener() {
            @Override
            public void onDownloadSuccess() {
                File file = new File(savePath);
                installApk(file);
            }

            @Override
            public void onDownloadFail() {
                Toast.makeText(CheckUpdate.this,"下载版本失败，请确认是否正常连接！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDonwloadUpdate(int value) {

            }

            @Override
            public void onCancled() {

            }
        });
        async.execute(path,savePath);
    }

    private void installApk(File apk){
        if(apk.exists() && apk.length() > 0){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(apk),"application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_version_layout:
                if(!"".equals(mDownloadUrl)){
                    String savePath = DOWNLOAD_FILE_PATH + mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/") + 1);
                    Map<String, String> param = new HashMap<>();
          //          param.put("fileUrl", fileUrl);
                    param.put("isApk", "apk");
 /*                   downloadApk(PublicMsg.BASEURL+"/downloadFile", savePath, param);*/
                    param.put("mapPath", mDownloadUrl);
                    downloadApk(PublicMsg.BASEURL + "/download", savePath, param);
//                    downloadApk(mDownloadUrl,savePath,param);
//                  downloadApk(mDownloadUrl,savePath);
                }
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }
}
