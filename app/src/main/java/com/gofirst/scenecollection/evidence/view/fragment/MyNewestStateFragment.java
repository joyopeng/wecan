package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.MyNewestStateFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.MyNewestStateFragmentAdapter.MyNewestStateFragmentData;

import java.util.ArrayList;
import java.util.List;

public class MyNewestStateFragment extends Fragment{

	private ListView myStateListview;
	private MyNewestStateFragmentAdapter adapter;
	private List<MyNewestStateFragmentData> list = new ArrayList<MyNewestStateFragmentData>();
	private SharePre sharePre;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("tag", "onCreate");
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.my_fragment, null);
		sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
		String prospectPerson=sharePre.getString("prospectPerson","");
		getData();
		myStateListview=(ListView)view.findViewById(R.id.mystate_listview);
		adapter = new MyNewestStateFragmentAdapter(prospectPerson,getActivity(),list);
		myStateListview.setAdapter(adapter);
		return view;
	}


	public void getData(){
		MyNewestStateFragmentAdapter.MyNewestStateFragmentData myNewestStateFragmentData;
		List<CsSceneCases> resultList= EvidenceApplication.db.findAll(CsSceneCases.class);

		list.clear();
		Log.d("resultList", "" + resultList.size());
		for(int j=0;j<resultList.size();j++) {
			if(resultList.get(j).getStatus().equals("1")) {
				myNewestStateFragmentData = new MyNewestStateFragmentData();
				myNewestStateFragmentData.setInvestigationPlace(resultList.get(j).getSceneDetail());
				myNewestStateFragmentData.setExposureProcess(resultList.get(j).getExposureProcess());
				myNewestStateFragmentData.setSceneRegionalism(resultList.get(j).getSceneRegionalism());//sceneRegionalismName
				myNewestStateFragmentData.setCrackedDate(resultList.get(j).getCrackedDate());
				myNewestStateFragmentData.setStatus(resultList.get(j).getStatus());
				myNewestStateFragmentData.setReportTime(resultList.get(j).getReportTime());
				myNewestStateFragmentData.setAlarmPeople(resultList.get(j).getAlarmPeople());
				list.add(myNewestStateFragmentData);
			}
		}
	}

	public void onResume(){
		super.onResume();
		Log.d("OnResume", "OnResume_2");
		/*getData();
		adap*//*ter.notifyDataSetChanged();*/
	}



	/*Netroid.GetHttp("/cases/lasted", new Netroid.OnLister<JSONObject>() {

		@Override
		public void onSuccess(JSONObject response) {
			Log.d("response", "" + response);
			try {
				if (response.getBoolean("success")) {
					MyNewestStateFragmentData myNewestStateFragmentData;
					JSONArray jsonArray = response
							.getJSONArray("data");
					JSONObject jsonObjectdata;
					Log.d("jsonArray12131", "" + jsonArray);
					list.clear();

					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObjectdata = jsonArray.getJSONObject(i);

						myNewestStateFragmentData = new MyNewestStateFragmentData();
						myNewestStateFragmentData.setInvestigationPlace(jsonObjectdata.getString("sceneDetail"));
						myNewestStateFragmentData.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
						myNewestStateFragmentData.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));//sceneRegionalismName
						myNewestStateFragmentData.setCrackedDate(jsonObjectdata.getString("crackedDate"));
						myNewestStateFragmentData.setStatus(jsonObjectdata.getString("status"));
						list.add(myNewestStateFragmentData);
					}

					adapter.notifyDataSetChanged();


				} else {

					Toast.makeText(getActivity(), response
									.getJSONArray("data").toString(),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(NetroidError error) {
			Log.d("error", "" + error);
		}
	},sharePre.getString("token", ""));*/



}
