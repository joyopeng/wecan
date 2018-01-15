package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.CreateThumbnailTask;
import com.gofirst.scenecollection.evidence.view.activity.TakeVideo;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.List;

/**
 * @author maxiran
 */
public class VideoGridAdapter extends BaseAdapter {

    private List<RecordFileInfo> videoFiles;
    private String caseId, father, mode;

    public VideoGridAdapter(List<RecordFileInfo> videoFiles, String caseId, String father) {
        this.videoFiles = videoFiles;
        this.caseId = caseId;
        this.father = father;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return mode != null && mode.equals(BaseView.VIEW) ? videoFiles.size() : videoFiles.size() + 1;
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
        return mode != null && mode.equals(BaseView.VIEW) ? getConvertView(position + 1,convertView,parent) :  position != 0 ? getConvertView(position,convertView,parent) :
                getAddConvertView(position,convertView,parent);
    }


    private View getConvertView(final int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_thumbnail, parent, false);
        ((TextView) convertView.findViewById(R.id.video_name)).setText("录像" + position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        new CreateThumbnailTask(imageView, AppPathUtil.getDataPath())
                .executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, videoFiles.get(position - 1));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(AppPathUtil.getDataPath() + "/" + videoFiles.get(position - 1).getFilePath()), "video/mp4");
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }


    private View getAddConvertView(final int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_video_layout, parent, false);
        convertView.findViewById(R.id.take_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), TakeVideo.class).putExtra("caseId", caseId).putExtra("father", father));
            }
        });
        return convertView;
    }
}
