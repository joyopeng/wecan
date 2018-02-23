package com.gofirst.scenecollection.evidence.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.utils.UpLoadLog;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.UserAdapter;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.cybertech.pdk.widget.AccountMappingDialogFragment;

/**
 * @author maxiran
 */
public  class Login extends FragmentActivity implements View.OnClickListener,
        Netroid.OnLister<JSONObject>, UserAdapter.OnUserListener ,AccountMappingDialogFragment.AccountMappding {

    private ImageView switch_btn, login_set;
    private boolean isRemeberPass = true, isExp;
    private SharePre sharePre;
    private ImageView expIcon;
    private EditText nameEditText, passEditText;
    private PopupWindow popupWindow;
    private final long LOGIN_TIMEOUT = 2 * 60 * 60 * 1000;
    private JSONObject deviceInfoJson;
    private String[] cloumNames =
            {"userId","machineCode","longitude","latitude","userName","userOrg","userOrgName","modularVersionNo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.splash_login_layout);
        Button log_btn = (Button) findViewById(R.id.login_btn);
        log_btn.setOnClickListener(this);
        switch_btn = (ImageView) findViewById(R.id.switch_btn);
        switch_btn.setOnClickListener(this);
        login_set = (ImageView) findViewById(R.id.login_set);
        login_set.setOnClickListener(this);
        sharePre = new SharePre(Login.this, "user_info", Context.MODE_WORLD_READABLE |MODE_MULTI_PROCESS);
        expIcon = (ImageView) findViewById(R.id.exp_icon);
        if (EvidenceApplication.db.findAll(User.class) != null && EvidenceApplication.db.findAll(User.class).size() < 2)
            expIcon.setVisibility(View.INVISIBLE);
        expIcon.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.user_name);
        passEditText = (EditText) findViewById(R.id.password);
        String userName = getIntent().getStringExtra("userName");
        User lastUser = getLastUser(userName);
        nameEditText.setText(lastUser != null && lastUser.getUserName() != null ? lastUser.getUserName() : "");
        passEditText.setText(lastUser != null && lastUser.getPassword() != null ? lastUser.getPassword() : "");
        long lastLoginTime = sharePre.getLong("lastLoginTime",System.currentTimeMillis());
        if (System.currentTimeMillis() - lastLoginTime > LOGIN_TIMEOUT){
            passEditText.setText("");
            ToastUtil.showShort(this,"登录过期重新填写密码");
        }
        try {
             deviceInfoJson = Utils.getDeviceInfoJsonObject();
            if(!deviceInfoJson.has("machineCode")){
                deviceInfoJson.put("machineCode", ViewUtil.getUUid());
            }
            deviceInfoJson.put("modularVersionNo",Netroid.versionName);
            Log.d("save",deviceInfoJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doMapping(String token, CharSequence account, CharSequence pwd) {
        //请求服务器绑定
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (TextUtils.isEmpty(passEditText.getText().toString()) || TextUtils.isEmpty(nameEditText.getText().toString())) {
                    Toast.makeText(this, "请填写用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringMap params = new StringMap();
                params.putString("pwd", passEditText.getText().toString().trim());
                Utils.startProgressDialog(Login.this, "", "", false, false);
                Netroid.PostHttp("/" + nameEditText.getText().toString().trim() + "/token", params, this);
              /* //请求绑定
                AccountMappingDialogFragment.newInstance(getIntent().getStringExtra("token"), nameEditText.getText().toString(),
                        passEditText.getText().toString(), "现勘").show(getSupportFragmentManager(), "evidence");*/
                break;

            case R.id.switch_btn:
                isRemeberPass = !isRemeberPass;
                switch_btn.setImageResource(isRemeberPass ? R.drawable.reme_pass : R.drawable.see_pass);
                break;

            case R.id.exp_icon:
                if (EvidenceApplication.db.findAll(User.class).size() != 0) {
                    expIcon.setImageResource(isExp ? R.drawable.down_arrow : R.drawable.up_arrow);
                    if (isExp) {
                        popupWindow.dismiss();
                    } else {
                        initPop();
                        popupWindow.showAsDropDown(nameEditText);
                    }
                    isExp = !isExp;
                }
                break;
            case R.id.login_set:
                startActivity(new Intent(Login.this, LoginIpSet.class));
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject response) {
        Utils.stopProgressDialog();
        try {
            boolean isSuccess = response.getBoolean("success");
            if (isSuccess) {
                if (TextUtils.equals("888888",passEditText.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("密码提示");
                    builder.setMessage("修改过于简单密码以获得更高的安全性");
                    builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent();
                            settingsIntent.setClass(Login.this,ChangePassword.class);
                            settingsIntent.putExtra("userName",nameEditText.getText().toString());
                            startActivity(settingsIntent);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else {
                    Log.i("zhangsh", "reponse = " + response);
                    JSONObject data = response.getJSONObject("data");
                    sharePre.put("token", data.getString("token"));
                    sharePre.put("user_name", nameEditText.getText().toString());
                    sharePre.put("organizationId", data.getString("organizationId"));
                    //add on
                    JSONObject userData = data.getJSONObject("user");
                    JSONObject hyEmployees = data.getJSONObject("hyEmployees");
                    JSONObject hyOrganizations = data.getJSONObject("hyOrganizations");
                    JSONObject hyCompartments = null;
                    if (!data.isNull("hyCompartments")) {
                        hyCompartments = data.getJSONObject("hyCompartments");
                    }
                    String alarmViewPurview = data.getString("alarmViewPurview");

                    sharePre.put("userId", userData.getString("employeeId"));
                    sharePre.put("user_id", userData.getString("userId"));
                    sharePre.put("organizationId", userData.getString("organizationId"));
                    sharePre.put("prospectPerson", hyEmployees.getString("employeeName"));
                    sharePre.put("employeeNo", hyEmployees.getString("employeeNo"));
                    sharePre.put("organizationCname", hyOrganizations.getString("organizationCname"));
                    if (hyCompartments != null) {
                        sharePre.put("compartmentNo", hyCompartments.getString("compartmentNo"));
                        sharePre.put("compartmentName", hyCompartments.getString("compartmentName"));
                    }

                    sharePre.put("alarmViewPurview", data.getString("alarmViewPurview"));

                    //add off
                    sharePre.commit();
                    saveUser(data, userData);

                    ArrayList<String> arrayList = UpLoadLog.GetLogFileName(AppPathUtil.getLogPath() + "/logs");
                    Log.d("UpLoadLog", "" + arrayList.size());
                    if (arrayList.size() > 0) {
                        Message message = new Message();
                        message.what = 1;
                        //mHandler.sendMessage(message);
                    }
//                universalLoadingView.stopLoading();
                    sharePre.put("lastLoginTime", System.currentTimeMillis());
                    sharePre.commit();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(Login.this, response.getString("message"), Toast.LENGTH_SHORT).show();
//                universalLoadingView.stopLoading();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(NetroidError error) {
        Utils.stopProgressDialog();
        Toast.makeText(Login.this, "网络超时尝试离线登录", Toast.LENGTH_SHORT).show();
        if (isExistUser()) {
            User user = EvidenceApplication.db.findAllByWhere(User.class,
                    "userName = '" + nameEditText.getText().toString() + "' ").get(0);
            sharePre.put("token", user.getToken());
            sharePre.put("user_name", user.getUserName());
            sharePre.put("organizationId", user.getOrganizationId() + "");
            sharePre.put("user_id", user.getNewUserId());
            sharePre.put("prospectPerson", user.getProspectPerson());
            sharePre.put("lastLoginTime",System.currentTimeMillis());
            sharePre.commit();
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
            Toast.makeText(Login.this, "离线登录",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Login.this, "不是已登录用户", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPop() {
        View view = LayoutInflater.from(Login.this).inflate(R.layout.remember_user_pop, null);
        popupWindow = new PopupWindow(view, nameEditText.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        ListView listView = (ListView) view.findViewById(R.id.user_list);
        listView.setAdapter(new UserAdapter(listRememberedUser(), this));
    }


    @Override
    protected void onDestroy() {
        String userName = nameEditText.getText().toString();
        if (deviceInfoJson != null){
            try {
                deviceInfoJson.put(cloumNames[0], sharePre.getString("user_id", ""));
                deviceInfoJson.put(cloumNames[2], sharePre.getString("longitude", ""));
                deviceInfoJson.put(cloumNames[3], sharePre.getString("latitude", ""));
                deviceInfoJson.put(cloumNames[4], sharePre.getString("user_name", ""));
                deviceInfoJson.put(cloumNames[5], sharePre.getString("organizationId", ""));
                deviceInfoJson.put(cloumNames[6], URLEncoder.encode(sharePre.getString("organizationCname", "")));
                Utils.saveDeviceInfo(deviceInfoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!isRemeberPass && !TextUtils.isEmpty(userName))
            deletePassword(userName);
        super.onDestroy();
    }

    private void deletePassword(String name) {
        List<User> list = EvidenceApplication.db.findAllByWhere(User.class, "userName = '" + name + "'");
        if (list != null && list.size() != 0) {
            list.get(0).setPassword("");
            EvidenceApplication.db.update(list.get(0));
        }
    }

    private void saveUser(JSONObject data,JSONObject userData) throws JSONException {
        if (isRemeberPass) {
            JSONObject hyEmployees = data.getJSONObject("hyEmployees");
            User user = getUser();
            user.setUserName(nameEditText.getText().toString());
            user.setPassword(passEditText.getText().toString());
            user.setToken(data.getString("token"));
            String orId = userData.getString("organizationId");
            user.setUserNameId(userData.getString("userId"));
            user.setToken(sharePre.getString("token", ""));
            user.setNewUserId(sharePre.getString("user_id", ""));
            user.setOrganizationId(Integer.valueOf(TextUtils.isEmpty(orId) ? "1" : orId));
            user.setProspectPerson( hyEmployees.getString("employeeName"));
            user.setUserId(userData.getString("employeeId"));
            //user.setPermissionSetting(sharePre.getString("alarmViewPurview", ""));
            if (user.getId() != 0) {
                EvidenceApplication.db.update(user);
            }else {
                EvidenceApplication.db.save(user);
            }
        }
    }

    private User getUser() {
        List<User> list = EvidenceApplication.db.findAllByWhere(User.class, "userNameId = '" + sharePre.getString("user_id", "") + "'");
        if (list != null && list.size() != 0)
            return list.get(0);
        return new User();

    }

    private boolean isExistUser() {
        List<User> userList = EvidenceApplication.db.findAllByWhere(User.class,
                "userName = '" + nameEditText.getText().toString() + "' ");
        return userList != null && userList.size() != 0 && !TextUtils.isEmpty(userList.get(0).getPassword());
    }

    private List<User> listRememberedUser() {
        return EvidenceApplication.db.findAll(User.class);
    }


    @Override
    public void OnRemoveUser(String userName, boolean isLast) {
        EvidenceApplication.db.deleteByWhere(User.class, "userName = '" + userName + "'");
        if (isLast) {
            popupWindow.dismiss();
            expIcon.setVisibility(View.INVISIBLE);
            nameEditText.setText("");
            passEditText.setText("");
        }
    }

    @Override
    public void OnUserClick(String userName, String pass) {
        nameEditText.setText(userName);
        passEditText.setText(pass);
        popupWindow.dismiss();
        isExp = !isExp;
        expIcon.setImageResource(R.drawable.down_arrow);
    }


    private User getLastUser(String userName) {
        List<User> list = EvidenceApplication.db.findAllByWhere(User.class, "userName = '" + (TextUtils.isEmpty(userName) ?
                sharePre.getString("user_name","") : userName) + "'");
        return list!= null && list.size() != 0 ? list.get(0) : null;
    }

    @Override
    protected void onResume() {
        super.onResume();
       // if (sharePre.getString("ip", "").equals("")) {
            //PublicMsg.BASEURL=PublicMsg.BASEURLTEMP;
       // } else {
       //     PublicMsg.BASEURL = sharePre.getString("ip", "");
       // }
    }


    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    Thread thread=new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            File file = new File(AppPathUtil.getLogPath() + "/logs");
                            if(!file.exists()){
                                file.mkdirs();
                            }
                            ArrayList<String> arrayList= UpLoadLog.GetLogFileName(AppPathUtil.getLogPath() + "/logs");
                            Log.d("UpLoadLog",""+arrayList.size());
                            for(int i =0;i<arrayList.size();i++){
                                 UpLoadLog.UpLoadLogToServicer(arrayList.get(i), getVersion(),getVersionName());
                                //UpLoadLog.UpLoad(arrayList.get(i),getVersion());
                            }
                        }

                    });
                    thread.start();

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String getVersion(){
        String result = "V1.01";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(),0);
            result = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  result;
    }

    private String getVersionName(){
        String result = "1";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(),0);
            result = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  result;
    }


}
