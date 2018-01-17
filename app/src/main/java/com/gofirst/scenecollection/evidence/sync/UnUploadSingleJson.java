package com.gofirst.scenecollection.evidence.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.request.StringRequest;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author maxiran
 */
public class UnUploadSingleJson {

    public void startUploadSingleJson(final UnUploadJson unUploadJson, final Context context, RequestQueue requestQueue) {
        SharePre sharePre = new SharePre(context, "user_info", MODE_PRIVATE);
        final String json = unUploadJson.getJson();
        Map<String, String> param = new HashMap<>();
        param.put("data", json);
        String userId = unUploadJson.getUserId();
        param.put("userId", TextUtils.isEmpty(userId) ? sharePre.getString("user_id","") : userId);
        param.put("uuid", unUploadJson.getId());
        Log.d("upJsonStart :",ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        if (unUploadJson.isAddRec()){
            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"caseNo = '" + unUploadJson.getCaseId() + "'");
            if (list != null && list.size() > 0){
                CsSceneCases csSceneCases = list.get(0);
                if (!csSceneCases.isUploaded())
                    return;
            }
        }
        String method = unUploadJson.isAddRec() ? "prospectByTab" : "prospect";
        PostByParamsRequest jsonObjectRequest = new PostByParamsRequest(PublicMsg.BASEURL + "/" +method + "/" + getIvestId(unUploadJson.getCaseId(),"SCENE_INVESTIGATION_EXT"), param, new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    String time = ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss");
                    Log.d("onRunning_  "+ time,jsonObject.toString());
                    if (isSuccess){
                            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"caseNo = '" + unUploadJson.getCaseId() + "'");
                            if (list != null && list.size() > 0){
                                CsSceneCases csSceneCases = list.get(0);
                                csSceneCases.setUploaded(true);
                                csSceneCases.setUploadTime(time);
                                EvidenceApplication.db.update(csSceneCases);
                        }
                        //
                        SharedPreferences uploadfilerec = context.getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
                        uploadfilerec.edit().remove(unUploadJson.getCaseId() + "_u").remove(unUploadJson.getCaseId()).commit();
                        //
                        unUploadJson.setUploaded(true);
                        unUploadJson.setIsUploading(time);
                        EvidenceApplication.db.update(unUploadJson);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        jsonObjectRequest.setForceUpdate(true);
        jsonObjectRequest.setCacheExpireTime(TimeUnit.MINUTES, 0);
        jsonObjectRequest.addHeader("ver", "1");
        jsonObjectRequest.addHeader("verName", Netroid.versionName);
        jsonObjectRequest.addHeader("deviceId", Netroid.dev_ID);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000,1,1f));
        requestQueue.add(jsonObjectRequest);
    }

    public static class PostByParamsRequest extends StringRequest {
        private Map<String, String>  mParams;

        public PostByParamsRequest(String url, Map<String, String> params, Listener<String> listener) {
            super(Method.POST, url, listener);
            mParams = params;
        }

        @Override
        public Map<String, String> getParams() throws AuthFailureError {
            return mParams;
        }
    }

       public static String getIvestId(String caseId ,String father){
        CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId,father);
        return ViewUtil.safeGetJsonValue("ID",caseBasicInfo.getJson());
    }
}
