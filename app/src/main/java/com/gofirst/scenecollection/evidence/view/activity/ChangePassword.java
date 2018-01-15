package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ChangePassword extends Activity implements View.OnClickListener {
        private TextView mTitleText;
        private ImageView mBackImg;
        private EditText oldPassword, newPassword, confirmPassword;
        private Button changeBtn;
        private SharePre sharePre;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        Init();
    }

    public void Init() {
        mTitleText = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleText.setText("修改密码");
        mBackImg = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImg.setOnClickListener(this);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.new_password);
        changeBtn = (Button) findViewById(R.id.change_btn);
        sharePre = new SharePre(ChangePassword.this, "user_info", Context.MODE_PRIVATE);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        changeBtn.setOnClickListener(this);

    }

    ///app/{account}/pwd
    private void changPwd(String oldPwd,String newPwd) {
        String account = getIntent().getStringExtra("userName");
        if (TextUtils.isEmpty(account))
            account = sharePre.getString("user_name", "");
        if("".equals(account)){
            Toast.makeText(ChangePassword.this, "账户为空，请确认！", Toast.LENGTH_SHORT).show();
            return;
        }
        StringMap params = new StringMap();
        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("token", sharePre.getString("token", ""));
        params.putString("oldPwd", oldPwd);//
        params.putString("newPwd", newPwd);
        final String finalAccount = account;
        Netroid.PostHttp("/" + account + "/pwd", params, new Netroid.OnLister<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean isSuccess = response.getBoolean("success");
                    Log.d("isSuccess", "" + isSuccess);
                    if(isSuccess){
                        Toast.makeText(ChangePassword.this,response.getString("message"),Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(ChangePassword.this,Login.class);
                        intent.putExtra("userName",finalAccount);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ChangePassword.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    updateUserPwd(finalAccount);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangePassword.this, "网络连接异常，请确认！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(NetroidError error) {
                Toast.makeText(ChangePassword.this, "网络连接异常，请确认！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_btn:
                String oldPwd = oldPassword.getText().toString().trim();
                String newPwd = newPassword.getText().toString().trim();
                String confirPwd = confirmPassword.getText().toString().trim();
                if("".equals(oldPwd) || "".equals(newPwd) || "".equals(confirPwd)){
                    Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(!newPwd.equals(confirPwd)){
                    Toast.makeText(ChangePassword.this, "新密码不一致", Toast.LENGTH_SHORT).show();
                }else if(newPwd.length() < 6 || confirPwd.length() < 6){
                    Toast.makeText(ChangePassword.this, "新密码不能少于6位", Toast.LENGTH_SHORT).show();
                }else{
                    changPwd(oldPwd,newPwd);
                }
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }

    private void updateUserPwd(String userName){
        List<User> users = EvidenceApplication.db.findAllByWhere(User.class,"userName = '" + userName + "'");
        if (users == null || users.size() == 0)
            return;
        User user = users.get(0);
        user.setPassword("");
        EvidenceApplication.db.update(user);
    }
}
