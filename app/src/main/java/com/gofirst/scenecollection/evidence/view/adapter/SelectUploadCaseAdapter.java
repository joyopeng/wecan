package com.gofirst.scenecollection.evidence.view.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;

import java.util.List;

public class SelectUploadCaseAdapter extends BaseAdapter {

    private List<CsSceneCases> datas;
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();

    public List<CsSceneCases> getDatas() {
        return datas;
    }

    public void setDatas(List<CsSceneCases> datas) {
        this.datas = datas;
    }

    public SparseBooleanArray getmCheckStates() {
        return mCheckStates;
    }

    public void setmCheckStates(SparseBooleanArray mCheckStates) {
        this.mCheckStates = mCheckStates;
    }

    public SelectUploadCaseAdapter(List<CsSceneCases> datas){
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
        final UploadHolder uploadHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_upload_case_item,parent,false);
            uploadHolder = new UploadHolder();
            uploadHolder.type = (TextView) convertView.findViewById(R.id.caseType);
            uploadHolder.address = (TextView) convertView.findViewById(R.id.caseAddress);
            uploadHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_select);
            convertView.setTag(uploadHolder);
        }else {
            uploadHolder = (UploadHolder) convertView.getTag();
        }
        uploadHolder.checkBox.setTag(position);
        CsSceneCases csSceneCases = datas.get(position);
        String type = csSceneCases.getCaseType();
        uploadHolder.type.setText(csSceneCases.isAddRec() ? type + "（补录）" : type);
        uploadHolder.address.setText(csSceneCases.getSceneDetail());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadHolder.checkBox.setChecked(!uploadHolder.checkBox.isChecked());
            }
        });
        uploadHolder.checkBox.setChecked(mCheckStates.get(position,false));
        uploadHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos =(int)buttonView.getTag();
                if(isChecked){
                    mCheckStates.put(pos,true);
                }else{
                    mCheckStates.delete(pos);
                }
            }
        });
        return convertView;
    }

    public static class UploadHolder{
        private CheckBox checkBox;
        private TextView type;
        private TextView address;
    }
}
