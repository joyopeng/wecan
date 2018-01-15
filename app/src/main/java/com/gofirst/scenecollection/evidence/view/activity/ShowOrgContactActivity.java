package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ContactPersons;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.OrgSearchShowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/24.
 */
public class ShowOrgContactActivity extends Activity  {
    private final String TAG = "ShowOrgContactActivity";
    private ListView mShowListView;
    private OrgSearchShowAdapter mShowAdapter;
    private List<ContactPersons> mShowPersons = new ArrayList<>();
    private String mOrgId = "";
    private SharePre mSharePre;
    private ImageView mBackImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_org_contact_layout);
        mOrgId = getIntent().getStringExtra("org_id");
        mSharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.secondary_title_tv)).setText("单位联系人");
        mBackImg = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mShowListView = (ListView) findViewById(R.id.lv_show_org_contact);
        initData();
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

    private void initData(){
        if(mOrgId == null || "".equals(mOrgId)){
            return;
        }
        StringMap map = new StringMap();
        map.putString("token", mSharePre.getString("token", ""));
        map.putString("org_id",mOrgId);
        map.putString("type","0");
        Netroid.PostHttp("/app/mail", map, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray arrays = null;
                try {
                    arrays = response.getJSONArray("data");
                    JSONObject object = null;
                    int length = arrays.length();
                    mShowPersons.clear();
                    ContactPersons person = null;
                    for(int i = 0;i < length;i++){
                        object = arrays.getJSONObject(i);
                        Log.i(TAG,"obj = " + object);
                        person = new ContactPersons();
                        person.setEmployeeId(object.getString("employeeId"));
                        person.setEmployeeTel(object.getString("employeeTel"));
                        person.setEmployeeNo(object.getString("employeeNo"));
                        person.setEmployeeName(object.getString("employeeName"));
                        person.setEmployeeSex(object.getString("employeeSex"));
                        person.setEmployeeCredno(object.getString("employeeCredno"));
                        person.setEmployeeCredname(object.getString("employeeCredname"));
                        person.setEmployeeNameSpell(object.getString("employeeNameSpell"));
                        person.setEmployeePost(object.getString("employeeNamePost"));
                        mShowPersons.add(person);
                    }
                    mShowAdapter = new OrgSearchShowAdapter(ShowOrgContactActivity.this,false,mShowPersons);
                    mShowListView.setAdapter(mShowAdapter);
                    mShowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(mShowPersons.size() > 0) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ContactDetailActivity");
                                bundle.putSerializable("person", mShowPersons.get(position));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                }catch (JSONException e){
                    Log.e(TAG,"JSONException",e);
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.e(TAG,"pos onError",error);
            }
        });
    }
}
