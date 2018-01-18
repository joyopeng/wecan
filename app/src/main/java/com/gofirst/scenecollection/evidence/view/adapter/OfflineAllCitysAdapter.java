package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.AppOfflineMapPackage;
import com.gofirst.scenecollection.evidence.utils.DownLoadAsync;
import com.gofirst.scenecollection.evidence.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/20.
 */
public class OfflineAllCitysAdapter extends BaseAdapter {

    private Activity mActivity;
    List<AppOfflineMapPackage> mAllCitys = new ArrayList<>();
    private String DOWNLOAD_MAP_DATA_PATH = "";//Environment.getExternalStorageDirectory() + "/test/";
    private HashMap<String, DownLoadAsync> mDownloadList = new HashMap<>();
    private DownLoadAllCitys downloadListener;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.00");

    public interface DownLoadAllCitys {
        void successDownload(AppOfflineMapPackage map);
    }

    public void setDownloadListener(DownLoadAllCitys linstener) {
        this.downloadListener = linstener;
    }

    public OfflineAllCitysAdapter(Activity activity, List<AppOfflineMapPackage> allCitys) {
        super();
        this.mActivity = activity;
        this.mAllCitys = allCitys;
        // DOWNLOAD_MAP_DATA_PATH = mActivity.getObbDir() + "/map";
        DOWNLOAD_MAP_DATA_PATH = mActivity.getObbDir() + "/amp/data/vmap";
    }

    @Override
    public int getCount() {
        return mAllCitys.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllCitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppOfflineMapPackage info = mAllCitys.get(position);
        if (mDownloadList.get(info.getMapSpell()) != null) {
            return convertView;
        }
        convertView = LayoutInflater.from(mActivity).inflate(R.layout.offline_all_citys_list_item, null);
        final ViewHolder holder = new ViewHolder();
        holder.itemName = (TextView) convertView.findViewById(R.id.offline_all_citys_title);
        holder.itemName.setText(info.getAreaName());
        holder.itemState = (TextView) convertView.findViewById(R.id.offline_downloading_show);
        holder.itemPlay = (ImageView) convertView.findViewById(R.id.offline_downlaod_btn);
        holder.itemUpdateStatus = (ImageView) convertView.findViewById(R.id.sync_data_map_img);
        final String size = getMapSize(info.getFileSize());
        List<AppOfflineMapPackage> hasDownload = EvidenceApplication.db.findAllByWhere(AppOfflineMapPackage.class, "id = \"" + info.getId() + "\"");
        if (hasDownload.size() > 0) {
            File temfile = new File(DOWNLOAD_MAP_DATA_PATH + "/" + info.getMapSpell() + ".dat_temp");
            if (temfile.exists()) {
                holder.itemState.setText("已更新下载:" + temfile.length()/(1024*1024)+"M");
            }
            holder.itemState.setText("已完成");
            holder.itemPlay.setBackgroundResource(R.drawable.offline_map_redownload);
            if (Integer.parseInt(hasDownload.get(0).getVersionCode()) < Integer.parseInt(info.getVersionCode())) {
                holder.itemUpdateStatus.setVisibility(View.VISIBLE);
            }
        } else {
            File temfile = new File(DOWNLOAD_MAP_DATA_PATH + "/" + info.getMapSpell() + ".dat_temp");
            if (temfile.exists()) {
                holder.itemState.setText("已下载:" + temfile.length()/(1024*1024)+"M");
            }else {
                holder.itemState.setText(size + "M");
                holder.itemPlay.setBackgroundResource(R.drawable.offline_map_download);
            }
        }

        holder.itemPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadListener.startDownload(info.getPath(),info.getAreaName(),holder.itemState);
                DownLoadAsync sync = mDownloadList.get(info.getMapSpell());
                if (sync == null) {
                    holder.itemState.setText("请稍后");
                    // sync = startDownloadThread(info, info.getPath(),info.getMapSpell(),holder.itemState,holder.itemPlay,size);
                    Map<String, String> param = new HashMap<>();
                    /*param.put("fileUrl",info.getPath());*/
                    param.put("mapPath", info.getPath());
                    /*sync = startDownloadThread(info, info.getPath(),info.getPath(),holder.itemState,holder.itemPlay,size, param);*/
                    sync = startDownloadThread(info, PublicMsg.BASEURL + "/download", info.getPath(), holder.itemState, holder.itemPlay, size, param);
                    mDownloadList.put(info.getMapSpell(), sync);

                } else {
                    Utils.startProgressDialog(mActivity, "", "请稍后...", false, false);
                    holder.itemPlay.setBackgroundResource(R.drawable.offline_map_download);
                    sync.cancel(true);

                    mDownloadList.remove(info.getMapSpell());
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView itemName;
        TextView itemState;
        ImageView itemPlay;
        ImageView itemUpdateStatus;
    }

    private String getMapSize(String bytes) {
        String size = "0";
        if (bytes != null && !"".equals(bytes)) {
            size = mDecimalFormat.format(Double.valueOf(bytes) / (1024 * 1024));
        }
        return size;
    }


    private final DownLoadAsync
    startDownloadThread(final AppOfflineMapPackage info, final String url, final String fileUrl, final TextView updateView, final ImageView play, final String size, Map<String, String> param) {
        play.setBackgroundResource(R.drawable.offline_map_remove);
        DownLoadAsync async = new DownLoadAsync(mActivity, false, false, new Handler(), param);
        updateView.setTextColor(mActivity.getResources().getColor(R.color.offline_map_downloading_text_color));
        async.setListener(new DownLoadAsync.DownLoadAsyncListener() {
            @Override
            public void onDownloadSuccess() {
                updateView.setText("已完成");
                updateView.setTextColor(mActivity.getResources().getColor(R.color.offline_map_normal_text_color));
                play.setBackgroundResource(R.drawable.offline_map_redownload);
                mDownloadList.remove(info.getMapSpell());
                downloadListener.successDownload(info);
            }

            @Override
            public void onDownloadFail() {
//                updateView.setText(size + "M");
                updateView.setTextColor(mActivity.getResources().getColor(R.color.offline_map_normal_text_color));
//                play.setBackgroundResource(R.drawable.offline_map_download);
                updateView.setText("网络异常,请刷新 " + updateView.getText().toString().replace("下载中", ""));
                play.setBackgroundResource(R.drawable.offline_map_redownload);
                mDownloadList.remove(info.getMapSpell());
            }

            @Override
            public void onDonwloadUpdate(int value) {
                Log.i("zhangsh", "onDonwloadUpdate");
                updateView.setText("下载中  " + mDecimalFormat.format(value * Double.valueOf(size) / 100) + "M/" + size + "M");
            }

            @Override
            public void onCancled() {
                Log.i("zhangsh", "onCancled");
//                updateView.setText(size + "M");
                updateView.setTextColor(mActivity.getResources().getColor(R.color.offline_map_normal_text_color));
                List<AppOfflineMapPackage> hasOne = EvidenceApplication.db.findAllByWhere(AppOfflineMapPackage.class, "mapSpell = \"" + info.getMapSpell() + "\"");
                if (hasOne.size() > 0) {
                    play.setBackgroundResource(R.drawable.offline_map_redownload);
                } else {
                    play.setBackgroundResource(R.drawable.offline_map_download);
                }
                Utils.stopProgressDialog();
            }
        });
        File dir = new File(DOWNLOAD_MAP_DATA_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        /*async.execute(url+"/getMapPackage",DOWNLOAD_MAP_DATA_PATH + "/" + info.getMapSpell() + ".dat");*/
        async.execute(url, DOWNLOAD_MAP_DATA_PATH + "/" + info.getMapSpell() + ".dat");
        return async;
    }
}
