package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.CreateThumbnailTask;
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.ProspectPreview;
import com.gofirst.scenecollection.evidence.view.activity.TakeRecord;
import com.gofirst.scenecollection.evidence.view.activity.TakeVideo;
import com.gofirst.scenecollection.evidence.view.customview.Audio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxiran on 2016/5/5.
 */
public class ProspectPreviewAdapter extends BaseAdapter {
    private ArrayList<ProspectPreViewItemData> lists;
    private ProspectPreview activity;
    private String caseId;
    private String status;

    //public ProspectPreviewAdapter(String caseId,ArrayList<ProspectPreViewItemData> lists, ProspectPreview activity) {
    public ProspectPreviewAdapter(String caseId,ArrayList<ProspectPreViewItemData> lists, ProspectPreview activity,String status) {
        this.caseId = caseId;
        this.lists = lists;
        this.activity = activity;
        this.status = status;
        for (ProspectPreViewItemData data : lists){
            Log.d("srs",data.getName() + " " +data.getField() + " " + data.isEditOrCamera());
        }
        Log.i("zhangsh","ProspectPreviewAdapter mode = " + status);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
     //   if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prospect_preview_item, parent, false);
            holder = new Holder();
            holder.image_background = (ImageView) convertView.findViewById(R.id.image_background);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.playOrRecord = (Audio) convertView.findViewById(R.id.play_or_record);
            holder.textBackground = (TextView) convertView.findViewById(R.id.text_background);
            holder.edit_camera_background = (LinearLayout) convertView.findViewById(R.id.edit_camera_background);
            holder.cameraIcon = (ImageView) convertView.findViewById(R.id.camera_icon);
            convertView.setTag(holder);
     //   } else {
     //       holder = (Holder) convertView.getTag();
     //   }
        //文字描述为空时，imageBackground;
        final ProspectPreViewItemData data = lists.get(position);
        if (data.getDesc().equals("")) {
            //为可编辑，摄像按钮
            if (data.isEditOrCamera()) {
                holder.edit_camera_background.setVisibility(View.VISIBLE);
                holder.image_background.setVisibility(View.GONE);
                holder.textBackground.setVisibility(View.GONE);
                holder.cameraIcon.setImageResource(R.drawable.camera_white);
                if("1".equals(status)){
                holder.cameraIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.getField().equals("SCENE_VIDEO")) {
                            Intent intent3 = new Intent(activity, TakeVideo.class);
                            intent3.putExtra("caseId",activity.caseId);
                            intent3.putExtra("father",data.getField());
                            activity.startActivityForResult(intent3, 1);
                        } else if (data.getField().equals("SCENE_PHOTO")) {
                            Intent intent3 = new Intent(activity, CameraActivity.class)
                                    //.putExtra("data", "scene")
                                    .putExtra("data", "blind")
                                    .putExtra("tabflage", "1")
                                    .putExtra("belongTo", "")
                                    .putExtra("cameraType", "blind")//scene
                                    .putExtra("caseId", caseId)
                                    .putExtra("father","SCENE_PHOTO");
                            activity.startActivityForResult(intent3, 1);
                        } else if (data.getField().equals("SCENE_BLIND_SHOOT")) {
                            Intent intent3 = new Intent(activity, CameraActivity.class)
                                    .putExtra("data", "blind")
                                    .putExtra("belongTo", "")
                                    .putExtra("cameraType", "blind")
                                    .putExtra("tabflage", "5")
                                    .putExtra("caseId",caseId)
                                    .putExtra("father","SCENE_PHOTO");
                            activity.startActivityForResult(intent3, 1);
                        }

                    }
                });}
            } else if (data.isNeedRec()) {
                //大录音图标
                holder.edit_camera_background.setVisibility(View.VISIBLE);
                holder.image_background.setVisibility(View.GONE);
                holder.textBackground.setVisibility(View.GONE);
                holder.cameraIcon.setImageResource(R.drawable.record_hd);
                if("1".equals(status)){
                holder.cameraIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent REC = new Intent(v.getContext(), TakeRecord.class);
                        REC.putExtra("caseId", activity.caseId);
                        REC.putExtra("father", data.getField());
                        REC.putExtra("child", data.getName());
                        v.getContext().startActivity(REC);
                    }
                });}
            } else {
                //图片背景
                holder.image_background.setVisibility(View.VISIBLE);
                holder.textBackground.setVisibility(View.GONE);
                holder.image_background.setImageBitmap(data.getBackground());
                holder.edit_camera_background.setVisibility(View.GONE);
                if (data.getField().equals("SCENE_PICTURE$1082")) {
                    holder.image_background.setImageResource(R.drawable.map);
                }
                if (data.getField().equals("SCENE_PICTURE$1010")) {
                    holder.image_background.setImageResource(R.drawable.plan);
                }
                if (data.getField().equals("SCENE_VIDEO")) {
                    holder.edit_camera_background.setVisibility(View.VISIBLE);
                    holder.image_background.setVisibility(View.GONE);
                    holder.textBackground.setVisibility(View.GONE);
                    if("1".equals(status)){
                    holder.cameraIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent3 = new Intent(activity, TakeVideo.class);
                            intent3.putExtra("caseId",activity.caseId);
                            intent3.putExtra("father",data.getField());
                            activity.startActivityForResult(intent3, 1);
                        }
                    });
                    }
                }
            }
        } else {
            //文字背景
            holder.image_background.setVisibility(View.GONE);
            holder.textBackground.setVisibility(View.VISIBLE);
            holder.textBackground.setText(data.getDesc());
            holder.edit_camera_background.setVisibility(View.GONE);
        }
        holder.name.setText(data.getName());
        holder.playOrRecord.setArgs(activity.caseId, data.getField(), "概要",status);
        if (data.getField().equals("SCENE_VIDEO")) {
            RecordFileInfo path = getPreviewPath(data.getField(), "video");
            if (path != null && path.getFilePath() != null && !path.getFilePath().equals("")) {
                Bitmap bitmap = CreateThumbnailTask.getVideoThumb(AppPathUtil.getDataPath(),path, 55, 55);
                displayBackground(holder);
                holder.image_background.setImageBitmap(bitmap);
            }

        } else if (data.getField().equals("SCENE_PHOTO") || data.getField().equals("SCENE_BLIND_SHOOT") || data.getField().equals("SCENE_PICTURE$1082")) {
            RecordFileInfo path = getPreviewPath(data.getField(), "png");
            if (path != null && path.getFilePath() != null && !path.getFilePath().equals("")) {
                Bitmap bitmap = BitmapUtils.revitionImageSize(AppPathUtil.getDataPath() + "/" + path.getFilePath());
                displayBackground(holder);
                holder.image_background.setImageBitmap(bitmap);
            }
        }

        if (data.getField().equals("SCENE_PICTURE$1010")){
            RecordFileInfo path = getPreviewPath(data.getField(), "png");
            if (path != null && path.getFilePath() != null && !path.getFilePath().equals("")) {
                //Bitmap bitmap = BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + path.getFilePath());
                Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/"+ path.getFilePath());
                displayBackground(holder);
                holder.image_background.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }

    private class Holder {
        private TextView name;
        private ImageView image_background, cameraIcon;
        private Audio playOrRecord;
        private TextView textBackground;
        private LinearLayout edit_camera_background;
    }

    private RecordFileInfo getPreviewPath(String father, String fileType) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "caseId = '" + activity.caseId + "' and father = '" + father + "' and fileType = '" + fileType + "'");
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    private void displayBackground(Holder holder) {
        holder.image_background.setVisibility(View.VISIBLE);
        holder.textBackground.setVisibility(View.GONE);
        holder.edit_camera_background.setVisibility(View.GONE);
    }
}
