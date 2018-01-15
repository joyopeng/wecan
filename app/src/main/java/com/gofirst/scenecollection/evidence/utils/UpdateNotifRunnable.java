package com.gofirst.scenecollection.evidence.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/8/10.
 */
public class UpdateNotifRunnable implements Runnable {

    public static final String TAG = "AsyncTaskRunnable";
    //主线程的activity
    private Context mContext;
    //notification的状态：更新 or 失败 or 成功
    private int mStatus;
    //notification的下载比例
    private float mSize;
    //管理下拉菜单的通知信息
    private NotificationManager mNotificationManager;
    //下拉菜单的通知信息
    private Notification mNotification;
    //下拉菜单的通知信息的view
    private RemoteViews mRemoteViews;
    //下拉菜单的通知信息的种类id
    private static final int NOTIFICATION_ID = 1;

    //设置比例和数据
    public void setDatas(int status , float size) {
        this.mStatus = status;
        this.mSize = size;
    }

    public UpdateNotifRunnable(Context context){
        this.mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //初始化下拉菜单的通知信息
        mNotification = new Notification();
        mNotification.icon = R.mipmap.app_icon;//设置下载进度的icon
        mNotification.tickerText = mContext.getResources().getString(R.string.app_name); //设置下载进度的title

        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.down_notification);//对于RemoteView的使用，不懂的需要查找google
        mRemoteViews.setImageViewResource(R.id.id_download_icon, R.drawable.ic_launcher);
    }

    @Override
    public void run() {
        switch (mStatus){
            case DownLoadAsync.NOTIFICATION_PROGRESS_UPDATE:
                DecimalFormat format = new DecimalFormat("0.00");//数字格式转换
                String progress = format.format(mSize);
                Log.d(TAG, "the progress of the download " + progress);
                mRemoteViews.setTextViewText(R.id.id_download_textview, "Download completed : " + progress + " %");
                mRemoteViews.setProgressBar(R.id.id_download_progressbar, 100, (int)mSize, false);
                mNotification.contentView = mRemoteViews;
                mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                break;
            case DownLoadAsync.NOTIFICATION_PROGRESS_SUCCEED:
                mRemoteViews.setTextViewText(R.id.id_download_textview, "Download completed ! ");
                mRemoteViews.setProgressBar(R.id.id_download_progressbar, 100, 100, false);
                mNotification.contentView = mRemoteViews;
                mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                mNotificationManager.cancel(NOTIFICATION_ID);
                //Toast.makeText(mContext, "Download completed ! ", Toast.LENGTH_SHORT).show();
                break;
            case DownLoadAsync.NOTIFICATION_PROGRESS_FAILED:
                mNotificationManager.cancel(NOTIFICATION_ID);
                break;
        }
    }
}
