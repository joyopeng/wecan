package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ContactPersons;
import com.gofirst.scenecollection.evidence.model.NodeData;
import com.gofirst.scenecollection.evidence.model.OrgNode;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.OrgListViewAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.OrgSearchShowAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.OrgSpecialAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class ContactActivity extends Activity implements View.OnClickListener{

    private final String TAG = "ContactActivity";

    private SharePre mSharePre;
    private final int TYPE_SUPERIOR_ORG = 10;
    private final int TYPE_CURRENT_LEVEL_ORG = 11;
    private final int TYPE_SUBORDINATE_ORG = 12;
    private final int TYPE_LAST_CONTACT = 13;
    private final int TYPE_SEARCH_ORG = 14;
    private int mCurrentType = TYPE_SUPERIOR_ORG;

    private ListView mOrgListView;
    private List<NodeData> mOrgDatas = new ArrayList<NodeData>();
    private OrgSpecialAdapter mListAdpater;

    //index listview
    private ListView mOrgIndexList;
    private ListView mSearchIndexList;

    private ListView mOrgSearchListView;

    private Button mBtnSupper;  //上级单位
    private Button mBtnCurrentLevel;  //本级单位
    private Button mBtnSubordinate;  //下级单位
    private Button mBtnLastContact;  //最近联系人

    private String[] mSearchShowIndexs = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T",
            "U","V","W","X","Y","Z"};
    private View mViewShowSearch;
    private List<ContactPersons> mLastPersons = new ArrayList<>();
    private List<ContactPersons> mSuperPersons = new ArrayList<>();
    private List<ContactPersons> mCurrentLevelPersons = new ArrayList<>();
    private List<ContactPersons> mSearchPersons = new ArrayList<>();
    private OrgSearchShowAdapter mSearchShowAdapter;
    //search title
    private EditText mSearchEdit;
    private ImageView mSearchBtn;
    private View mAlarmBtn;
    private ImageView mBackBtn;
    private View mAddNewCaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_layout);
        mSharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.title_bar_tv)).setText("通讯录");
        //search title
        findViewById(R.id.title_bar_alarm_layout).setVisibility(View.GONE);
        mSearchEdit = (EditText) findViewById(R.id.title_bar_search_edit);
        mSearchBtn = (ImageView) findViewById(R.id.title_bar_search_img);
        mSearchBtn.setOnClickListener(this);
        mAlarmBtn = findViewById(R.id.title_bar_alarm_img);
        mAlarmBtn.setOnClickListener(this);
        mAddNewCaseBtn = findViewById(R.id.title_bar_add_layout);
        mAddNewCaseBtn.setVisibility(View.GONE);
        findViewById(R.id.title_bar_alarm).setVisibility(View.GONE);
        mAddNewCaseBtn.setOnClickListener(this);
        mBackBtn = (ImageView)findViewById(R.id.search_title_back_img);
        mBackBtn.setOnClickListener(this);
        mBackBtn.setVisibility(View.VISIBLE);
        mBtnSupper = (Button) findViewById(R.id.btn_org_supper);
        mBtnSupper.setOnClickListener(this);
        mBtnCurrentLevel = (Button) findViewById(R.id.btn_org_current);
        mBtnCurrentLevel.setOnClickListener(this);
        mBtnSubordinate = (Button) findViewById(R.id.btn_org_subordinate);
        mBtnSubordinate.setOnClickListener(this);
        mBtnLastContact = (Button) findViewById(R.id.btn_org_last_contact);
        mBtnLastContact.setOnClickListener(this);

        mViewShowSearch = findViewById(R.id.org_show_search_layout);
        mViewShowSearch.setVisibility(View.GONE);

        mCurrentType = TYPE_SUPERIOR_ORG;
        mOrgListView = (ListView) findViewById(R.id.org_listview);
        updateOrgDatas(TYPE_SUPERIOR_ORG);
        updateBtnTextColor(mCurrentType);
        mOrgSearchListView = (ListView)findViewById(R.id.org_listview_search);

        //index listview adapter
        IndexAdapter mIndexAdapter = new IndexAdapter(mSearchShowIndexs);
        mOrgIndexList = (ListView)findViewById(R.id.org_index_list);
        mOrgIndexList.setOnItemClickListener(mListIndexListener);
        mOrgIndexList.setAdapter(mIndexAdapter);

        mSearchIndexList = (ListView) findViewById(R.id.search_index_list);
        mSearchIndexList.setOnItemClickListener(mSearchListIndexListener);
        mSearchIndexList.setAdapter(mIndexAdapter);
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
    public void onBackPressed() {
        if(mViewShowSearch != null && mViewShowSearch.getVisibility() == View.VISIBLE){
            mViewShowSearch.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_org_supper:
                //点击上级单位
                if(mCurrentType == TYPE_SUPERIOR_ORG){
                    break;
                }
                mOrgIndexList.setVisibility(View.VISIBLE);
                mCurrentType = TYPE_SUPERIOR_ORG;
                updateOrgDatas(TYPE_SUPERIOR_ORG);
                break;
            case R.id.btn_org_current:
                //点击本单位
                if(mCurrentType == TYPE_CURRENT_LEVEL_ORG){
                    break;
                }
                mOrgIndexList.setVisibility(View.VISIBLE);
                mCurrentType = TYPE_CURRENT_LEVEL_ORG;
                updateOrgDatas(TYPE_CURRENT_LEVEL_ORG);
                break;
            case R.id.btn_org_subordinate:
                //点击下级单位
                if(mCurrentType == TYPE_SUBORDINATE_ORG){
                    break;
                }
                mOrgIndexList.setVisibility(View.GONE);
                mCurrentType = TYPE_SUBORDINATE_ORG;
                updateOrgDatas(TYPE_SUBORDINATE_ORG);
                break;
            case R.id.btn_org_last_contact:
                //点击最近联系人
                if(TYPE_LAST_CONTACT == mCurrentType){
                    break;
                }
                mOrgIndexList.setVisibility(View.GONE);
                mCurrentType = TYPE_LAST_CONTACT;
                mLastPersons.clear();
                Date date = new Date(System.currentTimeMillis());
                int month = date.getMonth();
                int year = date.getYear();
                if(month == 0){
                    date.setYear(date.getYear() - 1);
                    date.setMonth(11);
                }else{
                    date.setMonth(month - 1);
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                mLastPersons = EvidenceApplication.db.findAllByWhere(ContactPersons.class,"lastConnectTime > '" + format.format(date) + "'","lastConnectTime desc");
                mOrgListView.setOnItemClickListener(mOrgListItemClick);
                mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,true,mLastPersons);
                mOrgListView.setAdapter(mSearchShowAdapter);
                //mOrgListView.setOnItemClickListener(mOrgListItemClick);
                break;
            case R.id.title_bar_search_img:
                mViewShowSearch.setVisibility(View.VISIBLE);
                String key = mSearchEdit.getText().toString().trim();
                if(key != null && !"".equals(key)){
                    StringMap maps = new StringMap();
                    maps.putString("token", mSharePre.getString("token", ""));
                    maps.putString("org_id",mSharePre.getString("organizationId",""));
                    maps.putString("keywords",key);
                    Netroid.PostHttp("/app/mail", maps, new Netroid.OnLister<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            object2Persons(mCurrentType,mSearchPersons,response);
                            mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,false,mSearchPersons);
                            mOrgSearchListView.setAdapter(mSearchShowAdapter);
                            mOrgSearchListView.setOnItemClickListener(mOrgSearchListItemClick);
                        }

                        @Override
                        public void onError(NetroidError error) {
                            Log.e(TAG,"search contact onError",error);
                        }
                    });
                }
                updateBtnTextColor(mCurrentType);
                break;
            case R.id.title_bar_alarm_img:
                if(mViewShowSearch != null && mViewShowSearch.getVisibility() == View.VISIBLE){
                    mViewShowSearch.setVisibility(View.GONE);
                }
                Intent alarmIntent = new Intent(ContactActivity.this,MoreNewCase.class);
                startActivity(alarmIntent);
                break;
            case R.id.title_bar_add_layout:
                Intent addNewIntent = new Intent(ContactActivity.this,AddNewCase.class);
                startActivity(addNewIntent);
                break;
            case R.id.search_title_back_img:
                if(mViewShowSearch != null && mViewShowSearch.getVisibility() == View.VISIBLE){
                    mViewShowSearch.setVisibility(View.GONE);
                    break;
                }
                mViewShowSearch.setVisibility(View.GONE);
                mSearchShowAdapter = null;
                mOrgSearchListView.setAdapter(null);
                finish();
                break;
            default:
                break;
        }
        updateBtnTextColor(mCurrentType);
    }

    /**
     * search title click end
     * */

    private void updateOrgDatas(int type){
        StringMap map = new StringMap();
        map.putString("token", mSharePre.getString("token", ""));
        map.putString("org_id",mSharePre.getString("organizationId",""));
        Log.i(TAG,"org id = " + mSharePre.getString("organizationId",""));
        switch (type){
            case TYPE_SUPERIOR_ORG:
                mOrgListView.setOnItemClickListener(mOrgListItemClick);
                if(mSuperPersons.size() > 0){
                    mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,false,mSuperPersons);
                    mOrgListView.setAdapter(mSearchShowAdapter);
                    break;
                }
                map.putString("type","1");
                Netroid.PostHttp("/app/mail", map, new Netroid.OnLister<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        object2Persons(mCurrentType,mSuperPersons,response);
                        mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,false,mSuperPersons);
                        mOrgListView.setAdapter(mSearchShowAdapter);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        Log.e(TAG,"contact onError",error);
                    }
                });
                break;

            case TYPE_CURRENT_LEVEL_ORG:
                mOrgListView.setOnItemClickListener(mOrgListItemClick);
                if(mCurrentLevelPersons.size() > 0){
                    mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,false,mCurrentLevelPersons);
                    mOrgListView.setAdapter(mSearchShowAdapter);
                    break;
                }
                map.putString("type","0");
                Netroid.PostHttp("/app/mail", map, new Netroid.OnLister<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        object2Persons(mCurrentType,mCurrentLevelPersons,response);
                        mSearchShowAdapter = new OrgSearchShowAdapter(ContactActivity.this,false,mCurrentLevelPersons);
                        mOrgListView.setAdapter(mSearchShowAdapter);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        Log.e(TAG,"contact onError",error);
                    }
                });
                break;
            case TYPE_SUBORDINATE_ORG:
                if(mOrgDatas.size() > 0){
                    mOrgListView.setAdapter(mListAdpater);
                    mOrgListView.setOnItemClickListener(mListAdpater.getOnItemClickLintener());
                    break;
                }
                mOrgListView.setAdapter(null);
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
                                //data.setType("");
                                mOrgDatas.add(data);
                            }
                        }catch (JSONException e){
                            Log.e(TAG,"JSONException",e);
                        }
                        //获取单位信息为null，返回不做显示
                        if(mOrgDatas.size() == 0){
                            return;
                        }
                        try {
                            mListAdpater = new OrgSpecialAdapter(mOrgListView,ContactActivity.this,mOrgDatas,1);
                            mListAdpater.setNodeClickListener(nodeClickListener);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }catch (IllegalArgumentException iae){
                            iae.printStackTrace();
                        }
                        mOrgListView.setAdapter(mListAdpater);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        Log.e(TAG,"pos onError",error);
                    }
                });
                break;
        }
    }


    private OrgListViewAdapter.onNodeClickListener nodeClickListener = new OrgListViewAdapter.onNodeClickListener() {
        @Override
        public void onNodeClick(OrgNode node, int position) {
            Log.i(TAG,"onNodeClick node id = " + node.getId() + ";pid = " + node.getpId() + ";name = " +
                    node.getName() + ";type = " + node.getType() + ";isLeaf = " + node.isLeaf() + ";position = " + position);
            Intent intent = new Intent();
            intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ShowOrgContactActivity");
            intent.putExtra("org_id",node.getId());
            startActivity(intent);
        }
    };

    private void updateBtnTextColor(int type){
        int notSelect = getResources().getColor(R.color.text_common_gray_color);
        int select = getResources().getColor(R.color.common_btn_bg_press_color);
        mBtnSupper.setTextColor(type == TYPE_SUPERIOR_ORG ? select : notSelect);
        mBtnCurrentLevel.setTextColor(type == TYPE_CURRENT_LEVEL_ORG ? select : notSelect);
        mBtnSubordinate.setTextColor(type == TYPE_SUBORDINATE_ORG ? select : notSelect);
        mBtnLastContact.setTextColor(type == TYPE_LAST_CONTACT ? select : notSelect);
    }

    private AdapterView.OnItemClickListener mOrgListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (mCurrentType){
                case TYPE_SUPERIOR_ORG:
                    intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ContactDetailActivity");
                    bundle.putSerializable("person",mSuperPersons.get(position));
                    break;
                case TYPE_CURRENT_LEVEL_ORG:
                    intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ContactDetailActivity");
                    bundle.putSerializable("person",mCurrentLevelPersons.get(position));
                    break;
                case TYPE_SUBORDINATE_ORG:
                    //intent.setAction("");
                    //intent.putExtra("org_id","");
                    break;
                case TYPE_LAST_CONTACT:
                    intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ContactDetailActivity");
                    bundle.putSerializable("person",mLastPersons.get(position));
                    break;
                default:

                    break;
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    private AdapterView.OnItemClickListener mOrgSearchListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.setAction("com.gofirst.scenecollection.evidence.view.activity.ContactDetailActivity");
            bundle.putSerializable("person",mSearchPersons.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    /**
     * index listview start
     * */
    private AdapterView.OnItemClickListener mListIndexListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pt = ((OrgSearchShowAdapter)mOrgListView.getAdapter()).getPositonSection(mSearchShowIndexs[position]);
            if(pt != -1){
                //mOrgListView.smoothScrollToPosition(pt);
                mOrgListView.requestFocus();
                mOrgListView.setSelection(pt);
            }
        }
    };

    private AdapterView.OnItemClickListener mSearchListIndexListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pt = mSearchShowAdapter.getPositonSection(mSearchShowIndexs[position]);
            if(pt != -1){
                //mOrgSearchListView.smoothScrollToPosition(pt);
                //mOrgSearchListView.requestFocusFromTouch();
                mOrgSearchListView.requestFocus();
                mOrgSearchListView.setSelection(pt);

            }
        }
    };

    private class IndexAdapter extends BaseAdapter {
        String[] datas = null;
        public IndexAdapter(String[] datas) {
            //super();
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.listview_index_item_layout,parent,false);
            TextView tv = (TextView)convertView.findViewById(R.id.tv_listview_index_item);
            tv.setText(mSearchShowIndexs[position]);
            return convertView;
        }
    }

    /**
     * index listview end
     * */

    /**
     * switch json to list datas
     * */
    private void object2Persons(int type,List<ContactPersons> results,JSONObject jsonObject){
        JSONArray arrays = null;
        try {
            arrays = jsonObject.getJSONArray("data");
            JSONObject object = null;
            int length = arrays.length();
            results.clear();
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
                person.setOrganizationId(object.getInt("organizationId"));
                person.setOrganizationName(object.getString("organizationName"));
                results.add(person);
            }
        }catch (JSONException e){
            Log.e(TAG,"JSONException",e);
        }
    }
}
