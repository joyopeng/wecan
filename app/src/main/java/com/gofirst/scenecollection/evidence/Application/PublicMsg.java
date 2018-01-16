package com.gofirst.scenecollection.evidence.Application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Administrator on 2016/5/15.
 */
public class PublicMsg {
    public static final String SHP_SHOW = "SHOWSETTINGLAYOUT";
    // public static String BASEURL ="http://192.168.0.188:8888/EvidenceService/app";
    // public static String BASEURL ="http://192.168.0.156:8080/EvidenceService/app";//taohong
    // public static String BASEURL ="http://192.168.0.16:8080/EvidenceService/app";//zhangwei
    // public static String BASEURL ="http://192.168.0.56:8080/EvidenceService/app";//zhaolong
    //public static String BASEURL ="http://test.geruiedu.com/EvidenceService/app";//苏州http://xjwk.szgaj.cn/EvidenceService/app
    // public static String BASEURL ="http://10.35.142.22:8080/EvidenceService/app";//zhengshi
//   public static String BASEURL ="http://192.168.1.207:9191/EvidenceService/app";//ling
    public static String BASEURL_RELEASE  ="http://172.168.0.199:8080/weapp";//苏州tt
    //  public static String BASEURL ="http://127.0.0.1:6253/EvidenceService/app";//ling
    public static final String BASEURL_TEST  ="http://192.168.191.1:8090/EvidenceService/app";
    public static String BASEURL  ="http://192.168.191.1:8180/EvidenceService/app";
    public static String evidenceDb = "evidence.db";
    public static String belongTo="";//未分类 general key detail other
    //public static String BASEUR`LTEMP ="http://222.92.23.166:12070/EvidenceService/app";//苏州
    // public static String BASEURLTEMP ="http://test.geruiedu.com/EvidenceService/app";//苏州
    public static String BASEURLTEMP  ="http://172.168.0.199:8080/weapp";//苏州tt
    //public static String BASEURLTEMP  ="http://127.0.0.1:6253/EvidenceService/app";//苏州tt
    public static final String UPLOADFILE_PREFRENCE = "uploadfile";
    public static boolean isDebug;
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
