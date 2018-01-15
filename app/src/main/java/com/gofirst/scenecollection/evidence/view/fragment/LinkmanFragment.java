package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.gofirst.scenecollection.evidence.view.activity.MainActivity;
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

public class LinkmanFragment extends Fragment implements View.OnClickListener,MainActivity.SearchTitleClick{

	private final String TAG = "LinkmanFragment";

	private final int TYPE_SUPERIOR_ORG = 10;
	private final int TYPE_CURRENT_LEVEL_ORG = 11;
	private final int TYPE_SUBORDINATE_ORG = 12;
	private final int TYPE_LAST_CONTACT = 13;
	private final int TYPE_SEARCH_ORG = 14;
	private int mCurrentType = TYPE_SUPERIOR_ORG;
	private SharePre mSharePre;

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

	private final int MSG_ACTION_GETCONTACTS_VOER = 110;
	private final int MSG_ACTION_GETORG_OVER  = 111;
	private final int MSG_ACTION_GETDATA_FAIL = 117;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_ACTION_GETCONTACTS_VOER:
					break;
				case MSG_ACTION_GETORG_OVER:
					break;
				case MSG_ACTION_GETDATA_FAIL:

					break;
			}
		}
	} ;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
		Log.i(TAG,"onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		Log.i(TAG,"onCreateView");
		View view = inflater.inflate(R.layout.contact_layout, container, false);

		mBtnSupper = (Button) view.findViewById(R.id.btn_org_supper);
		mBtnSupper.setOnClickListener(this);
		mBtnCurrentLevel = (Button) view.findViewById(R.id.btn_org_current);
		mBtnCurrentLevel.setOnClickListener(this);
		mBtnSubordinate = (Button) view.findViewById(R.id.btn_org_subordinate);
		mBtnSubordinate.setOnClickListener(this);
		mBtnLastContact = (Button) view.findViewById(R.id.btn_org_last_contact);
		mBtnLastContact.setOnClickListener(this);

		mViewShowSearch = view.findViewById(R.id.org_show_search_layout);
		mViewShowSearch.setVisibility(View.GONE);

		mCurrentType = TYPE_SUPERIOR_ORG;
		mOrgListView = (ListView) view.findViewById(R.id.org_listview);
		updateOrgDatas(TYPE_SUPERIOR_ORG);
		updateBtnTextColor(mCurrentType);
		mOrgSearchListView = (ListView)view.findViewById(R.id.org_listview_search);

		//index listview adapter
		IndexAdapter mIndexAdapter = new IndexAdapter(mSearchShowIndexs);
		mOrgIndexList = (ListView)view.findViewById(R.id.org_index_list);
		mOrgIndexList.setOnItemClickListener(mListIndexListener);
		mOrgIndexList.setAdapter(mIndexAdapter);

		mSearchIndexList = (ListView) view.findViewById(R.id.search_index_list);
		mSearchIndexList.setOnItemClickListener(mSearchListIndexListener);
		mSearchIndexList.setAdapter(mIndexAdapter);
		return view;
	}

	@Override
	public void onPause() {
		Log.i(TAG,"onPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG,"onDestroy");
		super.onDestroy();
	}

	/**
	 * search title click start
	 * */
	@Override
	public void searchBtnClick(String key) {
		((MainActivity)getActivity()).showSearchBackBtn();
		mViewShowSearch.setVisibility(View.VISIBLE);
		if(key != null && !"".equals(key)){
			StringMap maps = new StringMap();
			maps.putString("token", mSharePre.getString("token", ""));
			maps.putString("org_id",mSharePre.getString("organizationId",""));
			maps.putString("keywords",key);
			Netroid.PostHttp("/app/mail", maps, new Netroid.OnLister<JSONObject>() {
				@Override
				public void onSuccess(JSONObject response) {
					object2Persons(mCurrentType,mSearchPersons,response);
					mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),false,mSearchPersons);
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
	}

	@Override
	public void backBtnClick() {
		if(mViewShowSearch != null && mViewShowSearch.getVisibility() == View.GONE){
			return;
		}
		mViewShowSearch.setVisibility(View.GONE);
		mSearchShowAdapter = null;
		mOrgSearchListView.setAdapter(null);
		((MainActivity)getActivity()).hideSearchBackBtn();
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
					mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),false,mSuperPersons);
					mOrgListView.setAdapter(mSearchShowAdapter);
					break;
				}
				map.putString("type","1");
				Netroid.PostHttp("/app/mail", map, new Netroid.OnLister<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {
						object2Persons(mCurrentType,mSuperPersons,response);
						mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),false,mSuperPersons);
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
					mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),false,mCurrentLevelPersons);
					mOrgListView.setAdapter(mSearchShowAdapter);
					break;
				}
				map.putString("type","0");
				Netroid.PostHttp("/app/mail", map, new Netroid.OnLister<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {
						object2Persons(mCurrentType,mCurrentLevelPersons,response);
						mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),false,mCurrentLevelPersons);
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
							mListAdpater = new OrgSpecialAdapter(mOrgListView,getActivity(),mOrgDatas,1);
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
				mSearchShowAdapter = new OrgSearchShowAdapter(getActivity(),true,mLastPersons);
				mOrgListView.setAdapter(mSearchShowAdapter);
				//mOrgListView.setOnItemClickListener(mOrgListItemClick);
				break;
			default:
				break;
		}
		updateBtnTextColor(mCurrentType);
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
		int notSelect = getResources().getColor(R.color.black_semi_transparent);
		mBtnSupper.setTextColor(type == TYPE_SUPERIOR_ORG ? Color.BLUE : notSelect);
		mBtnCurrentLevel.setTextColor(type == TYPE_CURRENT_LEVEL_ORG ? Color.BLUE : notSelect);
		mBtnSubordinate.setTextColor(type == TYPE_SUBORDINATE_ORG ? Color.BLUE : notSelect);
		mBtnLastContact.setTextColor(type == TYPE_LAST_CONTACT ? Color.BLUE : notSelect);
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
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_index_item_layout,parent,false);
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
