package com.gofirst.scenecollection.evidence.utils;

import android.os.Build;
import android.util.Log;

import com.gofirst.scenecollection.evidence.Application.PublicMsg;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/16.
 */

public class UpLoadLog {


    public static void UpLoadLogToServicer(final String fileName, String version, String versionName) {
        AjaxParams params = new AjaxParams();

        try {
            params.put("uploadFile", new File(AppPathUtil.getLogPath() + "/logs/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("equipNumber", getPhotoModel());
        params.put("fileName", fileName);
        params.put("versionNo", versionName);
        params.put("versionName", version);
        FinalHttp fh = new FinalHttp();
        fh.post(PublicMsg.BASEURL + "/addFile", params, new AjaxCallBack<Object>() {

            @Override
            public void onLoading(long count, long current) {
                Log.d("upLogs", "" + current + "/" + count);
            }

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.d("upLogs", strMsg + "");
            }

        });
    }

    // 获取当前目录下所有的txt文件
    public static ArrayList<String> GetLogFileName(String fileAbsolutePath) {
        ArrayList<String> arrayList = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为txt结尾
                if (filename.trim().toLowerCase().endsWith(".log")) {
                    arrayList.add(filename);
                }
            }
        }
        return arrayList;
    }

    //獲得手機型號
    public static String getPhotoModel() {
        return Build.MODEL;
        //String brand = android.os.Build.BRAND;//手機品牌
        //String model = Build.MODEL;//手機型號
    }

    public static void DeleteLog(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

    }

}
