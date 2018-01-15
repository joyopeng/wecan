package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.ShowPhotoActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ShowPhotoActivityAdapter extends BaseAdapter {

    ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;
    public ShowPhotoActivityAdapter(ArrayList thumbs,ArrayList sceneFiles){
        this.thumbs = thumbs;
        this.sceneFiles = sceneFiles;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return thumbs.size()+1;
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
        Log.d("getCount()",""+getCount());

        if (position != getCount() - 1) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview);
            imageView.setImageBitmap(thumbs.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ShowPhotoActivity.class);
                    Log.d("test1213",""+ Uri.parse(sceneFiles.get(position).getAbsolutePath()));
                    Log.d("testsize", ""+ BitmapUtils.drr.size());
                    intent.setDataAndType(Uri.parse(sceneFiles.get(position).getAbsolutePath()), "Pictures/MyPictures/jpg");
//                    intent.setData(Uri.parse(sceneFiles.get(position).getAbsolutePath()));
                    v.getContext().startActivity(intent);

                   /* Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(sceneFiles.get(position).getAbsolutePath()), "Pictures/MyPictures/jpg");
                    v.getContext().startActivity(intent);*/
                }
            });


        } else if (position == getCount() - 1) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_video_layout, parent, false);
            convertView.findViewById(R.id.take_video).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class));
                }
            });
        }
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



}


