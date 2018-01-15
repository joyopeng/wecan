package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.activity.ShowEvidenceExtra;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class AddEvidenceViewAdapter extends BaseAdapter {

    private List<RecordFileInfo> list;
    private String id;
    private String templateId;
    private String mode;
    private EvidenceExtra evidenceExtra;

    public AddEvidenceViewAdapter(String id,String templateId,String mode,EvidenceExtra evidenceExtra) {
        this.id = id;
        this.templateId = templateId;
        this.mode = mode;
        this.evidenceExtra = evidenceExtra;
        getData();
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
        ImageView scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
        scene_photo.setImageBitmap(BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + list.get(position).getContractionsFilePath()));
        scene_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowEvidenceExtra.class)
                        .putExtra("id", id)
                        .putExtra("position", position);
                intent.putExtra("id", id);
                intent.putExtra("templateId", templateId);
                intent.putExtra("mode", mode);
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    public void getData() {
        list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "' and fileType = 'png'");
    }
}
