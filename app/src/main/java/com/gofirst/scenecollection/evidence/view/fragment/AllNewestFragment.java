package com.gofirst.scenecollection.evidence.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.NetState;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.AllNewestStateFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.AllNewestStateFragmentAdapter.AllNewestStateFragmentData;
import com.gofirst.scenecollection.evidence.view.customview.PullUpRefreshLayout;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AllNewestFragment extends Fragment implements View.OnClickListener{

	private ListView allStateListview;
	private AllNewestStateFragmentAdapter adapter;
	private List<AllNewestStateFragmentData> list = new ArrayList<AllNewestStateFragmentData>();
	private String caseNo;
	private SharePre sharePre;
	private PullUpRefreshLayout refreshLayout;
	private boolean isRefreshing = true;
	private boolean noMoreData = false;
	private int singleDisplaySize = 2;
	private int currentCount = 0;
	private Button showNewestCase;
	private String upOrDownFlage="1";
	private int allCount=0;
	private int start=0;
	private int end =20;
	//private MaterialRefreshLayout materialRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("tag", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.all_fragment, null);
		Init(view);
		sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
		String prospectPerson=sharePre.getString("prospectPerson","");
		allStateListview=(ListView)view.findViewById(R.id.allstate_listview);
		refreshLayout = (PullUpRefreshLayout) view.findViewById(R.id.refresh_layout);
		//materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);

		NetState receiver = new NetState();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		/*getActivity().registerReceiver(receiver, filter);*/
		receiver.onReceive(getActivity(), null);

		getData();
		adapter = new AllNewestStateFragmentAdapter(prospectPerson,getActivity(),list);
		allStateListview.setAdapter(adapter);
	/*	materialRefreshLayout.setLoadMore(true);
		materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
			@Override
			public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
				if (isNetworkAvailable(getActivity())) {
					NewestUpdateList("/cases/lasted");
				}else {
					materialRefreshLayout.finishRefresh();
					Toast.makeText(getActivity(), "当前没有可用网络！", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
				//上拉刷新...
				getMoreData();
				adapter.notifyDataSetChanged();

			}
		});*/

		/*// 结束下拉刷新...
		materialRefreshLayout.finishRefresh();

		// 结束上拉刷新...
		materialRefreshLayout.finishRefreshLoadMore();
*/


		refreshLayout.setColorSchemeColors(R.color.pull_color);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				Log.d("onRefresh", "下拉");
				upOrDownFlage="1";
				isRefreshing = false;
				NewestUpdateList("/cases/lasted");
				currentCount=0;
				refreshLayout.onFinishTemporaryDetach();
			}
		});
		refreshLayout.setOnLoadListener(new PullUpRefreshLayout.onLoadListener() {

			@Override
			public void onLoad() {
				Log.d("onRefresh", "上拉");
				if (upOrDownFlage.equals("1")) {
					list.clear();
				}
//				if (!noMoreData) {
//					noMoreData=false;
				List<DbModel> resultList = EvidenceApplication.db
						.findDbModelListBySQL("select * from CsSceneCases order by id limit '" + start + "','" + end + "'");
				/*start = +end;
				end = +end;*/
				int lastCount = currentCount + resultList.size();
				if (lastCount <= allCount) {
					for (DbModel dbModel : resultList) {
						if(dbModel.getString("status").equals("0")) {
							AllNewestStateFragmentData allNewestStateFragmentData;
							allNewestStateFragmentData = new AllNewestStateFragmentData();
							allNewestStateFragmentData.setCaseId(dbModel.getString("id"));
							allNewestStateFragmentData.setInvestigationPlace(dbModel.getString("sceneDetail"));
							allNewestStateFragmentData.setExposureProcess(dbModel.getString("exposureProcess"));
							allNewestStateFragmentData.setSceneRegionalism(dbModel.getString("sceneRegionalism"));
							allNewestStateFragmentData.setCrackedDate(dbModel.getString("crackedDate"));
							allNewestStateFragmentData.setStatus(dbModel.getString("status"));
							list.add(allNewestStateFragmentData);
						}
					}

					adapter.notifyDataSetChanged();
					refreshLayout.setLoading(false);
					upOrDownFlage = "0";
					currentCount = lastCount;
				} else {
					refreshLayout.setLoading(false);
					Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
				}


			}
		});
		//自动刷新
		/*refreshLayout.post(new Runnable() {

			@Override
			public void run() {
				Log.d("onRefresh", "自动");
				refreshLayout.setRefreshing(true);
				NewestUpdateList("/cases/lasted");
			}
		});
//		NewestUpdateList("/cases/lasted");*/
		return view;
	}


	public void getData(){
		AllNewestStateFragmentData allNewestStateFragmentData;
		List<CsSceneCases> resultList=EvidenceApplication.db.findAll(CsSceneCases.class);
		allCount=resultList.size();
		list.clear();
		Log.d("resultList", "" + resultList.size());
		for(int j=0;j<resultList.size();j++) {
			if(resultList.get(j).getStatus().equals("0")) {
				allNewestStateFragmentData = new AllNewestStateFragmentData();
				allNewestStateFragmentData.setCaseId(resultList.get(j).getCaseNo());
				allNewestStateFragmentData.setInvestigationPlace(resultList.get(j).getSceneDetail());
				allNewestStateFragmentData.setExposureProcess(resultList.get(j).getExposureProcess());
				allNewestStateFragmentData.setSceneRegionalism(resultList.get(j).getSceneRegionalism());
				//allNewestStateFragmentData.setCrackedDate(resultList.get(j).getCrackedDate());
				allNewestStateFragmentData.setStatus(resultList.get(j).getStatus());
				list.add(allNewestStateFragmentData);
			}
		}
	}


	private void getMoreData(){
		Log.d("onRefresh", "上拉");
		list.clear();
		List<DbModel> resultList = EvidenceApplication.db
				.findDbModelListBySQL("select * from CsSceneCases limit '" + start + "','" + end + "'");
		start = +end;
		end = +end;
		int lastCount = currentCount + resultList.size();
		if (lastCount <= allCount) {
			for (DbModel dbModel : resultList) {
				if(dbModel.getString("status").equals("0")) {
					AllNewestStateFragmentData allNewestStateFragmentData;
					allNewestStateFragmentData = new AllNewestStateFragmentData();
					allNewestStateFragmentData.setCaseId(dbModel.getString("id"));
					allNewestStateFragmentData.setInvestigationPlace(dbModel.getString("sceneDetail"));
					allNewestStateFragmentData.setExposureProcess(dbModel.getString("exposureProcess"));
					allNewestStateFragmentData.setSceneRegionalism(dbModel.getString("sceneRegionalism"));
					//allNewestStateFragmentData.setCrackedDate(dbModel.getString("crackedDate"));
					allNewestStateFragmentData.setStatus(dbModel.getString("status"));
					list.add(allNewestStateFragmentData);
				}
			}

	//		materialRefreshLayout.finishRefreshLoadMore();
		} else {
	//		materialRefreshLayout.finishRefreshLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
		}
	}


	public void onResume(){
		super.onResume();
		Log.d("OnResume","OnResume_1");
	//	getData();
		adapter.notifyDataSetChanged();
	}



	private void NewestUpdateList(String MethodName){
		StringMap params = new StringMap();

		params.putString("ver", "1");
		params.putString("verName", Netroid.versionName);
		params.putString("deviceId", Netroid.dev_ID);
		params.putString("token",sharePre.getString("token", ""));


		Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
			@Override
			public void onSuccess(JSONObject response) {

				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String date = sDateFormat.format(new java.util.Date());
				Log.d("responsecomplete", "" + response);
				try {
					if (response.getBoolean("success")) {
						AllNewestStateFragmentData allNewestStateFragmentData;
						JSONArray jsonArray =  response.getJSONArray("data");
						JSONObject jsonObjectdata;
						Log.d("testcomplete", "" + jsonArray);
						if (isRefreshing) {
							list.clear();
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							jsonObjectdata = jsonArray.getJSONObject(i);
							caseNo = jsonObjectdata.getString("id");
							Log.d("id", caseNo);

							CsSceneCases csSceneCases = new CsSceneCases();
							csSceneCases.setId(ViewUtil.getUUid());
							csSceneCases.setCaseNo(jsonObjectdata.getString("id"));
							csSceneCases.setSceneDetail(jsonObjectdata.getString("sceneDetail"));
							csSceneCases.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
							csSceneCases.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
							if (jsonObjectdata.has("crackedDate") || jsonObjectdata.getString("crackedDate") == null) {
								csSceneCases.setCrackedDate(date);
							} else {
								csSceneCases.setCrackedDate(jsonObjectdata.getString("crackedDate"));
							}
							csSceneCases.setStatus(jsonObjectdata.getString("status"));
							csSceneCases.setIsReceive(false);

							List<CsSceneCases> SceneCaselist=EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
									"caseNo = '" + caseNo + "'");
							SceneCaselist.size();
							if (SceneCaselist.size()==0) {
								Log.d("SceneCaselist", "save");
								EvidenceApplication.db.save(csSceneCases);

							} else{
								Log.d("SceneCaselist", "update");
								if(SceneCaselist.get(0).getStatus().equals("0")){
									EvidenceApplication.db.update(csSceneCases);
								}else if(SceneCaselist.get(0).getStatus().equals("1")){
									csSceneCases.setStatus("1");
									EvidenceApplication.db.update(csSceneCases);
								}
							}
						}

						getData();
						adapter.notifyDataSetChanged();
						//materialRefreshLayout.finishRefresh();

					} else {

						Toast.makeText(getActivity(), response
										.getJSONArray("data").toString(),
								Toast.LENGTH_SHORT).show();
						//refreshLayout.setRefreshing(false);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(NetroidError error) {
				Log.d("error", "" + error);
			}
		});

	}

	private void Init(View view){
		showNewestCase=(Button)view.findViewById(R.id.show_newest_case);
		showNewestCase.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.show_newest_case:
				NewestUpdateList("/cases/lasted");

				/*String strnew = jsonToString(getActivity(),"dw");
				Log.i("zhangsh","strnew = " + strnew);
				try {
					JSONArray array = new JSONArray(strnew);
					update(array);
				} catch (JSONException e) {
					e.printStackTrace();
				}*/

				break;
		}
	}

	public String jsonToString(Context context, String fileName) {
		String jsonString = null;
		try {

			InputStreamReader inputStreamReader = new InputStreamReader(context
					.getAssets().open(fileName + ".txt"), "UTF-8");//编码自行决定
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			bufferedReader.close();
			inputStreamReader.close();
			jsonString = stringBuilder.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	public void update(JSONArray array){
		try {

			AllNewestStateFragmentData allNewestStateFragmentData;
			JSONArray jsonArray = array;
			JSONObject jsonObjectdata;
			Log.d("testcomplete", "" + jsonArray);
			if (isRefreshing) {
				list.clear();
			}
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date;
			for (int i = 0; i < jsonArray.length(); i++){
				jsonObjectdata = jsonArray.getJSONObject(i);
				caseNo = jsonObjectdata.getString("id");
				Log.d("id", caseNo);
				CsSceneCases csSceneCases = new CsSceneCases();
				//csSceneCases.setCaseNo(jsonObjectdata.getString("id"));
				csSceneCases.setId(ViewUtil.getUUid());
				csSceneCases.setCaseNo(jsonObjectdata.getString("id"));
				csSceneCases.setSceneDetail(jsonObjectdata.getString("sceneDetail"));
				csSceneCases.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
				csSceneCases.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));
				//Log.i("zhangsh","json date time = " + jsonObjectdata.getString("occurrenceDateFrom") + ";current time = " + System.currentTimeMillis());
				if(jsonObjectdata.has("occurrenceDateFrom")){
					String time = jsonObjectdata.getString("occurrenceDateFrom");
					if("".equals(time)){
						csSceneCases.setOccurrenceDateFrom(new Date(System.currentTimeMillis()));
					}else {
						date = new Date(Long.valueOf(time));
						csSceneCases.setOccurrenceDateFrom(date);
					}
				}else{
					csSceneCases.setOccurrenceDateFrom(new Date(System.currentTimeMillis()));
				}
				csSceneCases.setCrackedDate(jsonObjectdata.has("crackedDate")?jsonObjectdata.getString("crackedDate"):null);
				csSceneCases.setStatus(jsonObjectdata.getString("status"));
				csSceneCases.setIsReceive(false);
				List<CsSceneCases> SceneCaselist=EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
						"caseNo = '" + caseNo + "'");
				SceneCaselist.size();
				if (SceneCaselist.size()==0) {
					Log.d("SceneCaselist", "save");
					EvidenceApplication.db.save(csSceneCases);

				} else{
					Log.d("SceneCaselist", "update");
					if(SceneCaselist.get(0).getStatus().equals("0")){
						EvidenceApplication.db.update(csSceneCases);
					}else if(SceneCaselist.get(0).getStatus().equals("1")){
						csSceneCases.setStatus("1");
						EvidenceApplication.db.update(csSceneCases);
					}
				}
			}
			if (!isRefreshing) {
				getData();
				adapter.notifyDataSetChanged();
			} else {
				// 下拉重置当前计数
				getData();
				adapter.notifyDataSetChanged();
				isRefreshing = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}




		/**
		 * 检查当前网络是否可用
		 */

		public boolean isNetworkAvailable(Activity activity)
		{
			Context context = activity.getApplicationContext();
			// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivityManager == null)
			{
				return false;
			}
			else
			{
				// 获取NetworkInfo对象
				NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

				if (networkInfo != null && networkInfo.length > 0)
				{
					for (int i = 0; i < networkInfo.length; i++)
					{
						System.out.println(i + "===状态===" + networkInfo[i].getState());
						System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
						// 判断当前网络状态是否为连接状态
						if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
						{
							return true;
						}
					}
				}
			}
			return false;
		}





}
