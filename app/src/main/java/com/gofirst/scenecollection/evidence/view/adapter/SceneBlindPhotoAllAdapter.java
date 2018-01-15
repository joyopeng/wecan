package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.ShowBlindActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class SceneBlindPhotoAllAdapter  extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private List<SceneBlindPhotoAllData> list = new ArrayList<SceneBlindPhotoAllData>();
    private LayoutInflater layoutInflater;
    private Context context;
    public SceneBlindPhotoAllAdapter(List<SceneBlindPhotoAllData> list){
        this.list = list;
        /*this.context=context;
        layoutInflater = LayoutInflater.from(context);*/
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return list.size()+1;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Log.d("position", "" + position);
        Log.d("getCount()", "" + getCount());
        ViewHolder viewHolder;
     //   if (position !=0 ) {
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
            viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.picture_checkbox= (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
            viewHolder.scene_photo.setImageBitmap(list.get(position-1).getScene_photo());


            viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowBlindActivity.class).putExtra("position",position-1);
                    v.getContext().startActivity(intent);
                }
            });

        /*} else if(position == 0) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ScenePhotos.tabflage.equals("5")&!ScenePhotos.tabflage.equals("6")){
                        ScenePhotos.tabflage="5";
                    }
                    v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class).putExtra("cameraType", "blind")
                            .putExtra("caseId","123"));

                    //cameraType *//*scene blind addevidence*//*
                    //pictureType
                }
            });
        }*/
        return convertView;
    }

    /*检测相机是否存在*/
    private boolean checkCameraHardWare(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }


    private class ViewHolder {
        private ImageView scene_photo;
        private CheckBox picture_checkbox;
    }

    public  static class SceneBlindPhotoAllData {
        public Bitmap scene_photo;
        public Bitmap getScene_photo() {return scene_photo;}
        public void setScene_photo(Bitmap scene_photo) {this.scene_photo = scene_photo;}
    }

}


