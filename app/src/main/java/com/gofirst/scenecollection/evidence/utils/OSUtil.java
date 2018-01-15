package com.gofirst.scenecollection.evidence.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
  

public class OSUtil {
	public static String getUUid(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	//获取客户端版本类型 
	public static String getAppVersionType(Context context){
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			return appInfo.metaData.get("app_version_type").toString();
			
		} catch (NameNotFoundException e) { 
			e.printStackTrace();
		}
		return null;
	}
	//获取客户端版本类型 
	public static String getAppNetworkType(Context context){
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			return appInfo.metaData.get("app_network_type").toString();
			
		} catch (NameNotFoundException e) { 
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static int getVersionCode(Context context) {
		if(context==null)
			return 0;
		PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
	}
	public static String getDeviceId(Context context) {
		if(context==null)
			return "";
		TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			return telephonemanage.getDeviceId();
		}
		catch(Exception e) {
		}
		return "";
	}
	public static String getBrandAndMode(Context context){
//		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
        return android.os.Build.BRAND+";"+android.os.Build.MODEL;
        
	}
	 
	//隐藏键盘
	public static void hideKeyBoard(Context context){
		((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);   
	}

	//发送短信
	public static void sendSMS(Context context,String number){
		Uri smsToUri = Uri.parse("smsto:"+number);  
		  
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
		  
		context.startActivity(intent);  
	}	
	//发送短信
	public static void sendSMS(Context context,String number,String message){
		Uri smsToUri = Uri.parse("smsto:"+number);  
		  
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
		if(!StringUtil.isNullorEmpty(message)){
			intent.putExtra("sms_body", message);  
		}
		context.startActivity(intent);  
	}
	//静默发送短信
	public static void sendSilentSMS(Context context,String toNumber,String message){
		// 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
		if (message != null) {
		SmsManager sms = SmsManager.getDefault();
		// 如果短信没有超过限制长度，则返回一个长度的List。
		List<String> texts = sms.divideMessage(message);
		for (String text : texts) {
			sms.sendTextMessage(toNumber,  null, text,  null, null);
		}}
	}
	public static boolean appIsInstalled(Context context,String packageName){
		PackageInfo packageInfo;
        try {
            packageInfo =context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
	}
	//获取屏幕高度
	public static DisplayMetrics getDisplayMetrics(Activity context){
		DisplayMetrics  dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);            
		return dm; 
	}
	//获取屏幕高度
	public static int getScreenHeight(Activity context){
		DisplayMetrics  dm = new DisplayMetrics();    
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);            
		return dm.heightPixels; 
	}
	public static int getScreenWidth(Activity context){
		DisplayMetrics  dm = new DisplayMetrics();    
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);            
		return dm.widthPixels; 
	}
	//获取状态栏的高度
	public static int getStatusBarHeight(Activity context){
		Rect frame = new Rect();  
		context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
        return frame.top;  
	}
	
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	}

	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	*/
	public static int px2dip(Context context, float pxValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (pxValue / scale + 0.5f);
	}
    /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    } 
    
    //获取wifi IP地址
    public static String getWifiIpAddress(Context context) {
        try {
        	//获取wifi服务  
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
            //判断wifi是否开启  
//            if (!wifiManager.isWifiEnabled()) {  
//            	wifiManager.setWifiEnabled(true);    
//            }  
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
            int ipAddress = wifiInfo.getIpAddress();   
            return intToIp(ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

  //获取GPRS IP地址
    public static String getGprsIpAddress() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();

                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (!ip.isLoopbackAddress()) {
                        return ip.getHostAddress().toString(); 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



	public static void installApk(Context context,File apk){
		if(apk.exists() && apk.length() > 0){
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setDataAndType(Uri.fromFile(apk),"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}
}
