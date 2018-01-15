package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.AllQueryFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.AllQueryFragmentAdapter.AllQueryFragmentData;
import com.gofirst.scenecollection.evidence.view.customview.PullUpRefreshLayout;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class AllQueryFragment extends Fragment {




    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private String[] crackedDate=new String[9];


    private ListView allQuery_fragment_listview;
    private PullUpRefreshLayout refreshLayout;
    private AllQueryFragmentAdapter adapter;
    private List<AllQueryFragmentData> list = new ArrayList<AllQueryFragmentData>();
    private SharePre sharePre;
    Context context;
    private int currentCount = 0;
    private int start=0;
    private int end =2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.allquery_fragment, null);
        sharePre=new SharePre(getActivity(),"user_info",Context.MODE_PRIVATE);
        String prospectPerson=sharePre.getString("prospectPerson","");
        allQuery_fragment_listview=(ListView)view.findViewById(R.id.allQuery_fragment_listview);
        adapter = new AllQueryFragmentAdapter(prospectPerson,getActivity(),list);
        allQuery_fragment_listview.setAdapter(adapter);
        refreshLayout = (PullUpRefreshLayout) view.findViewById(R.id.refresh_layout);

 //       getData();
 //       getAllData("/prospects/query");
        if(getNetworkType().equals("0")){
            getData();
        }else{
            getAllData("/prospects/query");
        }
        refreshLayout.setColorSchemeColors(R.color.pull_color);
        refreshLayout.setOnLoadListener(new PullUpRefreshLayout.onLoadListener() {
            @Override
            public void onLoad() {
                Log.d("onRefresh", "上拉");
                if(getNetworkType().equals("0")){
                    getData();
                }else{
                    getAllData("/prospects/query");
                }
            }
        });

        return view;
    }


    public void getData() {
        AllQueryFragmentData allQueryFragmentData;
 //       List<CsSceneCases> resultList=EvidenceApplication.db.findAll(CsSceneCases.class);
///        allCount=resultList.size();
        List<DbModel> resultList=EvidenceApplication.db
                .findDbModelListBySQL("select * from CsSceneCases order by id limit '" + start + "','"+ end +"'");

        Log.d("resultList", "" + resultList.size());
            list.clear();
            for(DbModel dbModel:resultList) {
                /*allQueryFragmentData = new AllQueryFragmentData();
                allQueryFragmentData.setId(resultList.get(j).getId());
                allQueryFragmentData.setInvestigationPlace(resultList.get(j).getSceneDetail());
                allQueryFragmentData.setExposureProcess(resultList.get(j).getExposureProcess());
                allQueryFragmentData.setSceneRegionalism(resultList.get(j).getSceneRegionalism());//sceneRegionalismName
                allQueryFragmentData.setCrackedDate(resultList.get(j).getCrackedDate());
                allQueryFragmentData.setStatus(resultList.get(j).getStatus());*/

                allQueryFragmentData = new AllQueryFragmentData();
                allQueryFragmentData.setId(dbModel.getString("id"));
                allQueryFragmentData.setInvestigationPlace(dbModel.getString("sceneDetail"));
                allQueryFragmentData.setExposureProcess(dbModel.getString("exposureProcess"));
                allQueryFragmentData.setSceneRegionalism(dbModel.getString("sceneRegionalism"));
                allQueryFragmentData.setCrackedDate(dbModel.getString("crackedDate"));
                allQueryFragmentData.setStatus(dbModel.getString("status"));

                list.add(allQueryFragmentData);
        }

    }

    public void onResume(){
        super.onResume();
        if(getNetworkType().equals("0")){
            getData();
        }else{
            getAllData("/prospects/query");
        }
        Toast.makeText(getActivity(),""+getNetworkType(),Toast.LENGTH_SHORT).show();
    }

    public void getAllData(String mathName){
        StringMap params = new StringMap();
        params.putString("areaCode", "11");//
        params.putString("startDate", "");//date类型
        params.putString("endDate", "");
        params.putString("caseType", "");
        params.putString("prospect", "");//curDatestr
        params.putString("status", "");
        params.putString("pageSize", "10");
        params.putString("pageIndex", "1");
        params.putString("token", sharePre.getString("token", ""));
        Log.d("Token", sharePre.getString("token", ""));
        Netroid.PostHttp(mathName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("Allresponse", "" + response);
                list.clear();
                try {
                    JSONArray JsonArray = response.getJSONArray("data");
                    Log.d("JsonArraylength", "" + JsonArray.length());
                    JSONObject JsonObjectdata;
                    AllQueryFragmentData allQueryFragmentData;
                    for (int i = 0; i < JsonArray.length(); i++) {
                        JsonObjectdata = JsonArray.getJSONObject(i).getJSONObject("cases");
                        Log.d("cases", "" + JsonObjectdata);
                        allQueryFragmentData = new AllQueryFragmentData();
                        Log.d("allid", JsonObjectdata.getString("id"));
                        allQueryFragmentData.setId(JsonObjectdata.getString("id"));
                        allQueryFragmentData.setInvestigationPlace(JsonObjectdata.getString("sceneDetail"));
                        allQueryFragmentData.setExposureProcess(JsonObjectdata.getString("exposureProcess"));
                        allQueryFragmentData.setSceneRegionalism(JsonObjectdata.getString("sceneRegionalism"));
                        allQueryFragmentData.setCrackedDate(JsonObjectdata.getString("crackedDate"));
                        allQueryFragmentData.setStatus(JsonObjectdata.getString("status"));
                        list.add(allQueryFragmentData);
                    }
                    adapter.notifyDataSetChanged();

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


    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */

    public static final String NETTYPE_WIFI = "1";
    public static final String NETTYPE_CMWAP = "2";
    public static final String NETTYPE_CMNET = "3";
    public String getNetworkType() {
        String netType = "0";
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if(extraInfo!=null||!extraInfo.equals("")){
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


}

