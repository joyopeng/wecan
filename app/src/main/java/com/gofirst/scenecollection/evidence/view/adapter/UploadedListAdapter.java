package com.gofirst.scenecollection.evidence.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class UploadedListAdapter extends BaseAdapter {

    private List<CsSceneCases> datas;

    public UploadedListAdapter(List<CsSceneCases> datas){
        this.datas = datas;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UploadHolder uploadHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_file_list_item,parent,false);
            uploadHolder = new UploadHolder();
            uploadHolder.type = (TextView) convertView.findViewById(R.id.caseType);
            uploadHolder.address = (TextView) convertView.findViewById(R.id.caseAddress);
            uploadHolder.time = (TextView) convertView.findViewById(R.id.uploadTime);
            convertView.setTag(uploadHolder);
        }else {
            uploadHolder = (UploadHolder) convertView.getTag();
        }
        CsSceneCases csSceneCases = datas.get(position);
        String type = csSceneCases.getCaseType();
        uploadHolder.type.setText(csSceneCases.isAddRec() ? type + "（补录）" : type);
        uploadHolder.time.setText(csSceneCases.getUploadTime());
        uploadHolder.address.setText(csSceneCases.getSceneDetail());
        return convertView;
    }

    private static class UploadHolder{
        private TextView type;
        private TextView address;
        private TextView time;
    }
}
