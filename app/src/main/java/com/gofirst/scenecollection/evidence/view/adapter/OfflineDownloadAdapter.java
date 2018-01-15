package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.AppOfflineMapPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class
OfflineDownloadAdapter extends BaseAdapter {
    private Context mContext;
    List<AppOfflineMapPackage> mAllDownloadCitys = new ArrayList<>();
    private String DOWNLOAD_MAP_DATA_PATH = "";//Environment.getExternalStorageDirectory() + "/test/";
    public OfflineDownloadAdapter(Context context, List<AppOfflineMapPackage> download) {
        super();
        this.mContext = context;
        this.mAllDownloadCitys = download;
        /*DOWNLOAD_MAP_DATA_PATH = context.getObbDir() + "/map";*/
        DOWNLOAD_MAP_DATA_PATH = context.getObbDir() + "/amp/data/vmap";
    }

    @Override
    public int getCount() {
        return mAllDownloadCitys.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllDownloadCitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AppOfflineMapPackage info = mAllDownloadCitys.get(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.offline_download_list_item,null);
        ViewHolder holder = new ViewHolder();
        holder.itemName = (TextView) convertView.findViewById(R.id.offline_downloaded_citys_title);
        holder.itemState = (TextView)convertView.findViewById(R.id.offline_downloaded_size);
        holder.itemClear = (ImageView) convertView.findViewById(R.id.offline_downloaded_clear);
        holder.itemClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(DOWNLOAD_MAP_DATA_PATH);
                if(!dir.exists()){
                    Toast.makeText(mContext,"目录不存在，请确认！",Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(DOWNLOAD_MAP_DATA_PATH + "/" + info.getMapSpell() + ".dat");
                if(file.exists()) {
                    file.delete();
                }
                EvidenceApplication.db.deleteById(AppOfflineMapPackage.class, info.getId());
                mAllDownloadCitys.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.itemName.setText(info.getAreaName());
        holder.itemState.setText(getMapSize(info.getFileSize()));
        return convertView;
    }

    private class ViewHolder{
        TextView itemName;
        TextView itemState;
        ImageView itemClear;
    }

    private String getMapSize(String bytes){
        String size = "0";
        if(bytes != null && !"".equals(bytes)){
            java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#####0.00");
            size = df.format(Double.valueOf(bytes)/(1024*1024)) + "M";
        }
        return size;
    }
}
