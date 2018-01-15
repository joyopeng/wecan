package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ContactPersons;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/17.
 */
public class ContactDetailActivity extends Activity implements View.OnClickListener{
    private final String TAG = "ContactDetailActivity";
    private TextView mActivityTitle;
    private ImageView mBackImg;
    private TextView mPicImg;
    private TextView mNameTxt;
    private TextView mDepartTxt;
    private TextView mPositionTxt;
    private TextView mAlarmTxt;  //警号
    private TextView mPhoneTxt;
    private ImageView mCallImg;
    private ContactPersons mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_layout);
        mPerson = (ContactPersons) getIntent().getSerializableExtra("person");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_contact_detail_dial:
                String num = mPhoneTxt.getText().toString();
                if(!"".equals(num)) {
                    mPerson.setLastConnectTime(getCurrentDate());
                    try {
                        EvidenceApplication.db.save(mPerson);
                    }catch (Exception e){
                        Log.i("zhangsh","onClikc exception ",e);
                    }

                    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + num));
                    startActivity(intent);
                }
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }

    private void initView(){
        ((TextView)findViewById(R.id.secondary_title_tv)).setText("联系人信息");
        mBackImg = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImg.setOnClickListener(this);
        mPicImg = (TextView) findViewById(R.id.img_contact_detail_pic);
        mNameTxt = (TextView) findViewById(R.id.txt_contact_detail_name);
        mDepartTxt = (TextView) findViewById(R.id.txt_contact_detail_depart);
        mAlarmTxt = (TextView) findViewById(R.id.txt_contact_detail_alarm);
        mPositionTxt = (TextView) findViewById(R.id.txt_contact_detail_position);
        mPhoneTxt = (TextView) findViewById(R.id.txt_contact_detail_phone);
        mCallImg = (ImageView) findViewById(R.id.img_contact_detail_dial);
        mCallImg.setOnClickListener(this);
        if(mPerson != null){
            try {
                String name = mPerson.getEmployeeName();
                if(name != null && name.length() > 2){
                    int length = name.length();
                    mPicImg.setText(name.substring(length - 2,length));
                }else{
                    mPicImg.setText(name);
                }
                mNameTxt.setText(name);
                mPhoneTxt.setText(mPerson.getEmployeeTel());
                mAlarmTxt.setText(mPerson.getEmployeeNo());
                //mDepartTxt.setText("" + mPerson.getOrgDeptId());
                String orgName = mPerson.getOrganizationName();
                mDepartTxt.setText(orgName == null ? "" : orgName);
                mPositionTxt.setText(mPerson.getEmployeePost());
            }catch (NullPointerException ex){
                mPicImg.setText("无");
                mNameTxt.setText("姓名");
                mPhoneTxt.setText("电话");
                mAlarmTxt.setText("警号");
                mDepartTxt.setText("部门");
                mPositionTxt.setText("职务");
            }
        }
    }

    private Date getCurrentDate(){
        return new Date(System.currentTimeMillis());
    }
}
