package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.AppOfflineMapPackage;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.OfflineAllCitysAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.OfflineDownloadAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.OfflinePageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class OffLineMapActivity extends Activity implements View.OnClickListener,OfflineAllCitysAdapter.DownLoadAllCitys {
    private final String TAG = "OffLineMapActivity";
    private String DOWNLOAD_MAP_DATA_PATH = Environment.getExternalStorageDirectory() + "/test/";
    private SharePre mSharePre;
    private List<AppOfflineMapPackage> mAllCitys = new ArrayList<>();
    private OfflineAllCitysAdapter mOfflineAllCitysAdapter;

    private List<AppOfflineMapPackage> mDownloaded = new ArrayList<>();
    private OfflineDownloadAdapter mOfflineDownloadAdapter;

    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mUnDownloadText;
    private TextView mDownloadText;
    private ViewPager mMapViewPager;

    private View mAllCityView;
    private ListView mAllCitysList;
    private ListView mDownloadList;
    private boolean flagSizechange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //MapsInitializer.sdcardDir = getSdCacheDir(this);

        setContentView(R.layout.off_line_map_layout);
        mSharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);

        mTitleTv = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleTv.setText("离线地图包");
        mBackBtn = (ImageView) findViewById(R.id.secondary_back_img);
        mBackBtn.setOnClickListener(this);
        mUnDownloadText = (TextView) findViewById(R.id.offline_map_undownload_btn);
        mUnDownloadText.setOnClickListener(this);
        mDownloadText = (TextView) findViewById(R.id.offline_map_downloaded_btn);
        mDownloadText.setOnClickListener(this);
        mMapViewPager = (ViewPager) findViewById(R.id.map_view_pager);
        mMapViewPager.setOffscreenPageLimit(2);
        getAllCityList();
        initViewpager();
        Log.i(TAG,"onCreate over map list");
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
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secondary_back_img:
                this.onBackPressed();
                break;
            case R.id.offline_map_undownload_btn:
                mMapViewPager.setCurrentItem(0);
                mUnDownloadText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mDownloadText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                break;
            case R.id.offline_map_downloaded_btn:
                mMapViewPager.setCurrentItem(1);
                mUnDownloadText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mDownloadText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                break;
            default:
                break;
        }
    }
    //download all citys methods
    @Override
    public void successDownload(AppOfflineMapPackage info) {
        List<AppOfflineMapPackage> hadDownloads = EvidenceApplication.db.findAllByWhere(AppOfflineMapPackage.class,"id = \"" + info.getId() + "\"");
        if(hadDownloads.size() > 0){
            EvidenceApplication.db.update(info);
        }else {
            EvidenceApplication.db.save(info);
        }
        mDownloaded = EvidenceApplication.db.findAll(AppOfflineMapPackage.class);
        mOfflineDownloadAdapter = new OfflineDownloadAdapter(this,mDownloaded);
        mDownloadList.setAdapter(mOfflineDownloadAdapter);
        mOfflineDownloadAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                flagSizechange = true;
            }
        });
    }

    private void initViewpager(){
        initAllCitys();
        initDownloadedCitys();
//        mMapViewPager.addView(mAllCityView);
//        mMapViewPager.addView(mDownloadList);
        OfflinePageAdapter adapter = new OfflinePageAdapter(mMapViewPager,mAllCityView,mDownloadList);
        mMapViewPager.setAdapter(adapter);
        mMapViewPager.setCurrentItem(0);
        mMapViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"onPageSelected position = " + position);
                if(position == 0){
                    if (flagSizechange)
                        mOfflineAllCitysAdapter.notifyDataSetChanged();
                    mUnDownloadText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                    mDownloadText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                }else{
                    mDownloaded = EvidenceApplication.db.findAll(AppOfflineMapPackage.class);
                    flagSizechange = false;
//                    mOfflineDownloadAdapter.notifyDataSetChanged();
                    mUnDownloadText.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                    mDownloadText.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void getAllCityList(){
        StringMap maps = new StringMap();
        maps.putString("token",mSharePre.getString("token",""));
        Netroid.PostHttp("/appOfflineMapPackage/", maps, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i(TAG,"PostHttp onSuccess response = " + response);
                JSONArray result = null;
                try {
                    if(!response.getBoolean("success")){
                        return;
                    }
                    result = response.getJSONArray("data");
                    int length = result.length();
                    mAllCitys.clear();
                    AppOfflineMapPackage pa = null;
                    JSONObject object = null;
                    for (int i = 0;i < length;i++){
                        pa = new AppOfflineMapPackage();
                        object = result.getJSONObject(i);
                        pa.setId(object.getString("id"));
                        pa.setAreaId(object.getString("areaId"));
                        pa.setAreaName(object.getString("areaName"));
                        pa.setMapSpell(object.getString("mapSpell"));
                        pa.setFileSize(object.getString("fileSize"));
                        pa.setPath(object.getString("path"));
                        pa.setThirdUrl(object.getString("thirdUrl"));
                        pa.setDeleteFlag(object.getString("deleteFlag"));
                        pa.setCreateUser(object.getString("createUser"));
                        //pa.setCreateDatetime((Date) object.get("createDatetime"));
                        pa.setUpdateUser(object.getString("updateUser"));
                       // pa.setUpdateDatetime((Date)object.get("updateDatetime"));
                        pa.setHostId(object.getString("hostId"));
                        pa.setHostYear(object.getString("hostYear"));
                        pa.setVersionCode(object.getString("versionCode"));
                        mAllCitys.add(pa);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initAllCitys();
                initViewpager();
            }

            @Override
            public void onError(NetroidError error) {
                Log.i(TAG,"PostHttp onError error",error);
            }
        });
    }

    private void initAllCitys(){
        mAllCityView = LayoutInflater.from(OffLineMapActivity.this).inflate(R.layout.offline_citys_layout,null);
        mAllCitysList = (ListView)mAllCityView.findViewById(R.id.offline_all_citys_list);
        mOfflineAllCitysAdapter = new OfflineAllCitysAdapter(this,mAllCitys);
        mOfflineAllCitysAdapter.setDownloadListener(this);
        mAllCitysList.setAdapter(mOfflineAllCitysAdapter);

    }

    private void initDownloadedCitys(){
        mDownloaded = EvidenceApplication.db.findAll(AppOfflineMapPackage.class);
        View view = LayoutInflater.from(OffLineMapActivity.this).inflate(R.layout.offline_download_layout,null);
        mDownloadList = (ListView)view.findViewById(R.id.offline_download_root);
        mOfflineDownloadAdapter = new OfflineDownloadAdapter(this,mDownloaded);
        mDownloadList.setAdapter(mOfflineDownloadAdapter);
    }

    /**
     * 获取map 缓存和读取目录
     */
    public static  String getSdCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            java.io.File fExternalStorageDirectory = Environment
                    .getExternalStorageDirectory();
            java.io.File autonaviDir = new java.io.File(
                    fExternalStorageDirectory, "testmap");
            boolean result = false;
            if (!autonaviDir.exists()) {
                result = autonaviDir.mkdir();
            }
            java.io.File minimapDir = new java.io.File(autonaviDir,
                    "offlineMap");
            if (!minimapDir.exists()) {
                result = minimapDir.mkdir();
            }
            return minimapDir.toString() + "/";
        } else {
            return "";
        }
    }
}
