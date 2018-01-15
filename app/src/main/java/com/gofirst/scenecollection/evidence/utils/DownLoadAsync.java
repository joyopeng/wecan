package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.duowan.mobile.netroid.Request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public class DownLoadAsync extends AsyncTask<String,Integer,Boolean> {
    private final String TAG = "DownLoadAsync";

    public static final String TEST_URL = "http://images.cnitblog.com/i/169207/201408/112229149526951.png";
    public static final String TEST_SAVE_PATH = Environment.getExternalStorageDirectory() + "/Pictures/img2016.png";

    private Context mContext;
    private boolean mShowProgress = false;
    private boolean mShowNotif = false;
    private long mFileSize = 0; //所下载信息大小
    private Handler mHandler = null;
    private UpdateNotifRunnable mNotifRunnable = null;
    private DownLoadAsyncListener listener = null;
    private Map<String,String> param ;

    public static final int NOTIFICATION_PROGRESS_UPDATE = 0x10;//用于更新下载进度的标志
    public static final int NOTIFICATION_PROGRESS_SUCCEED = 0x11;//表示下载成功
    public static final int NOTIFICATION_PROGRESS_FAILED = 0x12;//表示下载失败


    public interface DownLoadAsyncListener{
        public void onDownloadSuccess();
        public void onDownloadFail();
        public void onDonwloadUpdate(int value);
        public void onCancled();
    }

    public DownLoadAsync(Context context, boolean showProgress,boolean showNotif, Handler handler ,Map<String,String> param) {
        super();
        this.mContext = context;
        this.mShowProgress = showProgress;
        this.mShowNotif = showNotif;
        this.mHandler = handler;
        this.param=param;
        mNotifRunnable = new UpdateNotifRunnable(mContext);
    }
    public DownLoadAsync(Context context, boolean showProgress,boolean showNotif, Handler handler  ) {
        super();
        this.mContext = context;
        this.mShowProgress = showProgress;
        this.mShowNotif = showNotif;
        this.mHandler = handler;
        mNotifRunnable = new UpdateNotifRunnable(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadStart();
       // Log.i(TAG,"onPreExecute");
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            downloadSuccess();
        }else{
            downloadFail();
        }
        super.onPostExecute(result);
       // Log.i(TAG,"onPostExecute result = " + result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        downloadUpdate(values[0]);
       // Log.i(TAG,"onProgressUpdate value = " + values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        listener.onCancled();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.i(TAG, "doInBackground");
        String downloadPath = params[0];
        String savePath = params[1];
        Log.i(TAG,"zhangsh url = " + downloadPath + ";path = " + savePath);
        if(downloadPath == null || savePath == null || "".equals(downloadPath) || "".equals(savePath)){
            return false;
        }
        //boolean sucess = downloadGet(downloadPath,savePath);
        boolean sucess = downloadGet(downloadPath,savePath,param);
        return sucess;
    }

//    private volatile int invokeCount;
    private boolean downloadGet(String urlPath,String savePath,Map<String,String> param){
        boolean result = false;
        try {
            URL url = null;
            HttpURLConnection conn = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                File file = new File(savePath);
                /* 未加容错，因此这里创建文件的上一级目录必须存在 */
                if (!file.exists()) {
                    file.createNewFile();
                }
                url = new URL(urlPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.setConnectTimeout(18 * 1000);
                conn.setReadTimeout(1000);
                conn.setRequestMethod("GET");
                byte[] bytes = Request.encodeParameters(param, "UTF-8");
                conn.setDoOutput(true);
                conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                if(file.length() > 0)
                conn.setRequestProperty("Range", String.valueOf(file.length()));
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(bytes);
                out.close();
                outputStream = new FileOutputStream(file,true);
                inputStream = conn.getInputStream();
                long totalLength = conn.getContentLength();
                if(totalLength == 0)
                    return result;
                long currentLenth = file.length();
                byte[] data = new byte[2048];
                int lt = 0;
                while (!isCancelled() && currentLenth < totalLength && ((lt = inputStream.read(data)) != -1)) {
                    currentLenth += lt;
                    int progress = (int) (100 * (float) currentLenth / totalLength);
                    publishProgress(progress);
                    outputStream.write(data, 0, lt);
                }
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }
            result = true;
        }catch (MalformedURLException urlException){
            Log.i(TAG,"MalformedURLException",urlException);
        }
        catch (IOException io){
            Log.i(TAG,"IOException",io);
//            if(invokeCount++ > 3)
//                return false;
//            result =downloadGet(urlPath,savePath,param);
        }
        catch (Exception e){
            Log.i(TAG,"zhangsh exception = "  + e);
        }
        return result;
    }

    private boolean downloadGet(String urlPath,String savePath){
        boolean result = false;
        try {
            URL url = null;
            HttpURLConnection conn = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                url = new URL(urlPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.setConnectTimeout(30 * 1000);
                File file = new File(savePath);
                conn.addRequestProperty("keep-alive","true");
                /* 未加容错，因此这里创建文件的上一级目录必须存在 */
                if (!file.exists()) {
                    file.createNewFile();
                }
                outputStream = new FileOutputStream(file);
                inputStream = conn.getInputStream();
                long totalLength = conn.getContentLength();

                long currentLenth = 0;
                byte[] data = new byte[2048];
                int lt = 0;
                while (!isCancelled() && ((lt = inputStream.read(data)) != -1)) {
                    currentLenth += lt;
                    int progress = (int) (100 * (float) currentLenth / totalLength);
                    publishProgress(progress);
                    outputStream.write(data, 0, lt);
                }
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }
            result = true;
        }catch (MalformedURLException urlException){
            Log.i(TAG,"MalformedURLException",urlException);
        }catch (IOException io){
            Log.i(TAG,"IOException",io);
        }catch (Exception e){
            Log.i(TAG,"zhangsh exception = "  + e);
        }
        return result;
    }

    private boolean downloadPost(String url,String savePath){
        return false;
    }

    private void downloadStart(){
        if(mShowProgress){
            Utils.startProgressDialog(mContext,"","下载中...",mShowProgress,false);
        }
        if(mShowNotif){
            mNotifRunnable.setDatas(NOTIFICATION_PROGRESS_UPDATE,0);
            mHandler.post(mNotifRunnable);
        }
    }

    private void downloadFail(){
        if(mShowProgress){
            Utils.stopProgressDialog();
        }
        if(mShowNotif){
            mNotifRunnable.setDatas(NOTIFICATION_PROGRESS_FAILED,0);
            mHandler.post(mNotifRunnable);
        }
        if(listener != null){
            listener.onDownloadFail();
        }
    }

    private void downloadSuccess(){
        if(mShowProgress){
            Utils.stopProgressDialog();
        }
        if(mShowNotif){
            mNotifRunnable.setDatas(NOTIFICATION_PROGRESS_SUCCEED,0);
            mHandler.post(mNotifRunnable);
        }
        if(listener != null){
            listener.onDownloadSuccess();
        }
    }

    private void downloadUpdate(int value){
        if(mShowProgress){
            Utils.updateDialogProgress(value);
        }
        if(mShowNotif){
            mNotifRunnable.setDatas(NOTIFICATION_PROGRESS_UPDATE,value);
            mHandler.post(mNotifRunnable);
        }
        if(listener != null){
            listener.onDonwloadUpdate(value);
        }
    }

    public void setListener(DownLoadAsyncListener listener){
        this.listener = listener;
    }
}
