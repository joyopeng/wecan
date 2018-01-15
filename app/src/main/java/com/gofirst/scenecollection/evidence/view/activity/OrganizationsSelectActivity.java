package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.NodeData;
import com.gofirst.scenecollection.evidence.model.OrgNode;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.OrgListViewAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.OrgSpecialAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsh on 2016/8/26.
 */
public class OrganizationsSelectActivity extends Activity implements View.OnClickListener{
    private final String TAG = "OrgSelectActivity";
    private ListView mOrgSelectLV;
    private OrgSpecialAdapter mOrgsAdapter;
    private List<NodeData> mOrgDatas = new ArrayList<NodeData>();

    private SharePre mSharePre;
    private ImageView mBtnBack;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizations_select_layout);
        mSharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        initView();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secondary_back_img:
                finish();
                break;
            default:
                break;
        }
    }

    private void initView(){
        mOrgSelectLV = (ListView) findViewById(R.id.org_select_listview);
        mTitle = (TextView) findViewById(R.id.secondary_title_tv);
        mTitle.setText("单位选择");
        mBtnBack = (ImageView) findViewById(R.id.secondary_back_img);
        mBtnBack.setOnClickListener(this);
    }

    private void initData(){
        StringMap map = new StringMap();
        map.putString("token", mSharePre.getString("token", ""));
        map.putString("org_id",mSharePre.getString("organizationId",""));
        Netroid.PostHttp("/app/queryByOrgId", map, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray arrays = null;
                try {
                    arrays = response.getJSONArray("data");
                    JSONObject object = null;
                    int length = arrays.length();
                    mOrgDatas.clear();
                    NodeData data = null;
                    for(int i = 0;i < length;i++){
                        object = arrays.getJSONObject(i);
                        data = new NodeData();
                        data.setId(object.getString("organizationId"));
                        data.setpId(object.getString("organizationBusiUpId"));
                        data.setName(object.getString("organizationName"));
                        mOrgDatas.add(data);
                    }
                }catch (JSONException e){
                    Log.e(TAG,"JSONException",e);
                }
                try {
                    mOrgsAdapter = new OrgSpecialAdapter(mOrgSelectLV,OrganizationsSelectActivity.this,
                            mOrgDatas,1);
                    mOrgsAdapter.setNodeClickListener(new OrgListViewAdapter.onNodeClickListener() {
                        @Override
                        public void onNodeClick(OrgNode node, int position) {
                            Intent intent = new Intent();
                            intent.putExtra("select_org_id",node.getId());
                            intent.putExtra("select_org_name",node.getName());
                            setResult(1001,intent);
                            finish();
                        }
                    });
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                mOrgSelectLV.setAdapter(mOrgsAdapter);
            }

            @Override
            public void onError(NetroidError error) {
                Log.e(TAG,"pos onError",error);
            }
        });
    }
}
