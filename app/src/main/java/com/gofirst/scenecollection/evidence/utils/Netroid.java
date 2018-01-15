package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 初始化网路请求队列
 */
public class Netroid {

    public static RequestQueue mRequestQueue;

    private static String HTTP_DISK_CACHE_DIR_NAME = "evidence";


    public static String versionCode;

    public static String versionName;

    public static String dev_ID;

    public static BufferedWriter bw;
    FileWriter writer2;


    public static void init(Context ctx) {
        if (mRequestQueue != null)
            throw new IllegalStateException("initialized");
        String USER_AGENT = null;
        try {
            USER_AGENT = "evidence";
            versionCode = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0).versionCode + "";
            versionName = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0).versionName;
            dev_ID = Settings.Secure.getString(ctx.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Network network = new BasicNetwork(new HurlStack(USER_AGENT, null), "UTF-8");
        mRequestQueue = new RequestQueue(network, 4, new DiskCache(new File(
                ctx.getCacheDir(), HTTP_DISK_CACHE_DIR_NAME),
                50 * 1024 * 1024));
        mRequestQueue.start();
    }

    public static void GetHttp(String url, final OnLister lister, String token) {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(PublicMsg.BASEURL + url, null, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    lister.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                lister.onError(error);
            }

        });
        jsonObjectRequest.setForceUpdate(true);
        jsonObjectRequest.setCacheExpireTime(TimeUnit.MINUTES, 0);
        jsonObjectRequest.addHeader("ver", "1");
        jsonObjectRequest.addHeader("verName", versionName);
        jsonObjectRequest.addHeader("deviceId", dev_ID);
        if (token != null) {
            jsonObjectRequest.addHeader("token", token);
        }
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000, 0, 1f));
        mRequestQueue.add(jsonObjectRequest);
    }

    public interface OnLister<T> {
        void onSuccess(T response) throws JSONException;

        void onError(NetroidError error);
    }

    public static class PostByParamsRequest extends StringRequest {

        private Map<String, String> mParams;

        public PostByParamsRequest(String url, Map<String, String> params, Listener<String> listener) {
            super(Method.POST, url, listener);
            mParams = params;
        }

        @Override
        public Map<String, String> getParams() throws AuthFailureError {
            return mParams;
        }
    }

    public static void PostHttp(String url, StringMap params, final OnLister<JSONObject> lister) {

        Log.d("tfUrl",""+url);
        PostByParamsRequest postByParamsRequest = new PostByParamsRequest(PublicMsg.BASEURL + url, params.getStringMap(), new Listener<String>() {

            @Override
            public void onSuccess(String response) {
                try {
                    lister.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                lister.onError(error);
            }
        });

        Log.d("Urltest",""+PublicMsg.BASEURL+url);
        postByParamsRequest.setForceUpdate(true);
        postByParamsRequest.setCacheExpireTime(TimeUnit.MINUTES, 0);
        postByParamsRequest.setRetryPolicy(new DefaultRetryPolicy(3000,1,1f));
        mRequestQueue.add(postByParamsRequest);
    }




}
