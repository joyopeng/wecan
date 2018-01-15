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
import com.gofirst.scenecollection.evidence.view.adapter.CompleteStateFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.CompleteStateFragmentAdapter.CompleteStateFragmentData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */
public class CompleteNewestStateFragment extends Fragment {

    private ListView completeStateListview;
    private CompleteStateFragmentAdapter adapter;
    private List<CompleteStateFragmentData> list = new ArrayList<CompleteStateFragmentData>();
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
        View view = inflater.inflate(R.layout.complete_fragment, null);
        sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        String prospectPerson=sharePre.getString("prospectPerson","");
        completeStateListview = (ListView) view.findViewById(R.id.completestate_listview);
        adapter = new CompleteStateFragmentAdapter(prospectPerson,getActivity(), list);
        completeStateListview.setAdapter(adapter);
        return view;
    }

    public void getData(){
        CompleteStateFragmentData completeStateFragmentData;
        List<CsSceneCases> resultList= EvidenceApplication.db.findAll(CsSceneCases.class);

        list.clear();
        Log.d("resultList", "" + resultList.size());
        for(int j=0;j<resultList.size();j++) {
            completeStateFragmentData = new CompleteStateFragmentData();
            completeStateFragmentData.setInvestigationPlace(resultList.get(j).getSceneDetail());
            completeStateFragmentData.setExposureProcess(resultList.get(j).getExposureProcess());
            completeStateFragmentData.setSceneRegionalism(resultList.get(j).getSceneRegionalism());//sceneRegionalismName
            completeStateFragmentData.setCrackedDate(resultList.get(j).getCrackedDate());
            completeStateFragmentData.setStatus(resultList.get(j).getStatus());
            completeStateFragmentData.setReportTime(resultList.get(j).getReportTime());
            completeStateFragmentData.setAlarmPeople(resultList.get(j).getAlarmPeople());
            list.add(completeStateFragmentData);
        }
    }

    public void onResume(){
        super.onResume();
        Log.d("OnResume", "OnResume_3");
        getData();
        adapter.notifyDataSetChanged();
    }
        /*Netroid.GetHttp("/cases/lasted", new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {
                        CompleteStateFragmentData completeStateFragmentData;
                        JSONArray jsonArray = response
                                .getJSONArray("data");
                        JSONObject jsonObjectdata;
                        Log.d("testcomplete", "" + jsonArray);
                        list.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjectdata = jsonArray.getJSONObject(i);

                                completeStateFragmentData = new CompleteStateFragmentData();
                                completeStateFragmentData.setInvestigationPlace(jsonObjectdata.getString("sceneDetail"));
                                completeStateFragmentData.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
                                completeStateFragmentData.setSceneRegionalism(jsonObjectdata.getString("sceneRegionalism"));//sceneRegionalismName
                                completeStateFragmentData.setCrackedDate(jsonObjectdata.getString("crackedDate"));
                                completeStateFragmentData.setStatus(jsonObjectdata.getString("status"));
                                list.add(completeStateFragmentData);


                        }

//                        adapter.notifyDataSetChanged();


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
        }, sharePre.getString("token", ""));*/

}