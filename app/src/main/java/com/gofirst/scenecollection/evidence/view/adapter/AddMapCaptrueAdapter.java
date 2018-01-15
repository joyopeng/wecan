package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.view.activity.EvidencePhotoExplorer;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.List;

/**
 * Created by maxiran on 2016/5/10.
 */
public class AddMapCaptrueAdapter extends BaseAdapter {

    private List<RecordFileInfo> bitmaps;
    private takeMapCapture takeMapCapture;
    private String mode;

    public AddMapCaptrueAdapter(List<RecordFileInfo> videoFiles, takeMapCapture takeMapCapture) {
        this.bitmaps = videoFiles;
        this.takeMapCapture = takeMapCapture;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        //return mode != null && mode.equals(BaseView.VIEW) ? bitmaps.size() : bitmaps.size() + 1;
        return bitmaps.size();
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
//        return mode != null && mode.equals(BaseView.VIEW) ? getConvertView(position + 1,convertView,parent) :  position != 0 ? getConvertView(position,convertView,parent) :
//                getAddConvertView(position,convertView,parent);
        return getConvertView(position,convertView,parent);
    }

    private View getConvertView(final int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_thumbnail, parent, false);
        //((TextView) convertView.findViewById(R.id.video_name)).setText("地图" + (position));
        ((TextView) convertView.findViewById(R.id.video_name)).setText("地图" + (position + 1));
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        //imageView.setImageBitmap(BitmapUtils.revitionImageSize(bitmaps.get(position - 1).getFilePath()));
        imageView.setImageBitmap(BitmapUtils.revitionImageSize(AppPathUtil.getDataPath()+"/"+bitmaps.get(position).getFilePath()));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(v.getContext(), EvidencePhotoExplorer.class).putExtra("position", position - 1);
                Intent intent = new Intent(v.getContext(), EvidencePhotoExplorer.class).putExtra("position", position);
                intent.putExtra("caseId", bitmaps.get(0).getCaseId());
                intent.putExtra("father", bitmaps.get(0).getFather());
                intent.putExtra("mode",mode);
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    private View getAddConvertView(final int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_video_layout, parent, false);
        if (mode != null && mode.equals(BaseView.VIEW))
            convertView.setVisibility(View.GONE);
        TextView textView = (TextView) convertView.findViewById(R.id.add_xx);
        textView.setText("截图");
        convertView.findViewById(R.id.take_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takeMapCapture != null) {
                    takeMapCapture.takeCapture();
                }
            }
        });
        return convertView;
    }
    public interface takeMapCapture {
        void takeCapture();
    }

}
