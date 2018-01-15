package com.gofirst.scenecollection.evidence.sync;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.gofirst.scenecollection.evidence.IMyAidlInterface;

/**
 * Created by Administrator on 2017/3/16.
 */
public class RemoteService extends Service {
    MyConn conn;
    MyBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        conn = new MyConn();
        binder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, " 远程服务started", Toast.LENGTH_SHORT).show();
        this.bindService(new Intent(this, UpLoadService.class), conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return RemoteService.class.getSimpleName();
        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(RemoteService.this, "本地服务killed", Toast.LENGTH_SHORT).show();
            //开启本地服务
            RemoteService.this.startService(new Intent(RemoteService.this, UpLoadService.class));
            //绑定本地服务
            RemoteService.this.bindService(new Intent(RemoteService.this, UpLoadService.class), conn, Context.BIND_IMPORTANT);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //开启本地服务
        RemoteService.this.startService(new Intent(RemoteService.this, UpLoadService.class));
        //绑定本地服务
        RemoteService.this.bindService(new Intent(RemoteService.this, UpLoadService.class), conn, Context.BIND_IMPORTANT);
    }
}