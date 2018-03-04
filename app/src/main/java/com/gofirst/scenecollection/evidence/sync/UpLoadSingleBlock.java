package com.gofirst.scenecollection.evidence.sync;

import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.RequestQueue;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author maxiran
 *         上传单个分块
 */
public class UpLoadSingleBlock {

    private SharedPreferences preferences;

    /**
     * @param unUpLoadBlock 未上传的文件分块表信息
     */
    public UpLoadSingleBlock(SharedPreferences f) {
        preferences = f;
    }

    public void startUpLoadSingleBlock(final UnUpLoadBlock unUpLoadBlock, RequestQueue requestQueue,String userId) {
        Map<String, String> params = new HashMap<>();
        final File parentFile = new File(unUpLoadBlock.getParentPath());
        params.put("userId", userId);
        params.put("uuid", unUpLoadBlock.getId());
        params.put("fileName", parentFile.getName());
        params.put("flag", CreateMD5(parentFile.getName()));
        params.put("blockTotal", "" + unUpLoadBlock.getBlockTotal());
        params.put("blockIndex", unUpLoadBlock.getBlockIndex() + "");
        params.put("content", readFile(unUpLoadBlock));
        String path = parentFile.getParent().replace(AppPathUtil.getDataPath() + "/", "");
        path = path.replace(Environment.getExternalStorageDirectory().getPath() + "/", "");
        params.put("path", path);
        Netroid.PostByParamsRequest upLoadRequest = new Netroid.PostByParamsRequest(PublicMsg.BASEURL + "/uploadFile",
                params, new Listener<String>() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getBoolean("success")) {
                        if (deleteBlock(unUpLoadBlock)) {
                            Log.d("onRunning", new File(unUpLoadBlock.getPath()).getName() + "删除成功  " + response);
                        } else {
                            Log.d("onRunning", new File(unUpLoadBlock.getPath()).getName() + "删除失败  " + response);
                        }
                    } else {
                        Log.d("onRunning", new File(unUpLoadBlock.getPath()).getName() + "上传失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                Log.d("onRunning", "error : {" + error.toString() + "}");
            }


        });
        upLoadRequest.setForceUpdate(true);
        upLoadRequest.setCacheExpireTime(TimeUnit.MINUTES, 0);
        upLoadRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1f));
        requestQueue.add(upLoadRequest);
    }

    private String readFile(UnUpLoadBlock unUpLoadBlock) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        File file = new File(unUpLoadBlock.getPath());
        try {
            byteArrayOutputStream = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int length;
            while (-1 != (length = bufferedInputStream.read(buffer, 0, 1024))) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String CreateMD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 上传成功删除分块表记录以及文件
     */
    private boolean deleteBlock(UnUpLoadBlock unUpLoadBlock) {
        //
        String caseid = unUpLoadBlock.getCaseId();
        File blockfile = new File(unUpLoadBlock.getPath());
        long filesize = 0;
        if (blockfile.exists()) {
            filesize = blockfile.length();
        }
        long currentsize = preferences.getLong(caseid + "_u", 0);
        filesize = currentsize + filesize;
        preferences.edit().putLong(caseid + "_u", filesize).commit();
        //
        EvidenceApplication.db.deleteById(UnUpLoadBlock.class, unUpLoadBlock.getId());
        File blockFile = new File(unUpLoadBlock.getPath());
        return blockFile.delete();
    }

}
