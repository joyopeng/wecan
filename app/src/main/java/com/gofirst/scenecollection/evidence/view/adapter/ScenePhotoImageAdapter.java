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
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.ShowPictureActivity;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class ScenePhotoImageAdapter extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private List<ScenePhotoImageData> list = new ArrayList<ScenePhotoImageData>();
    private LayoutInflater layoutInflater;
    private Context context;
    private String caseId;
    private String belongTo;
    private String mode;
    public ScenePhotoImageAdapter(String mode,String belongTo,String caseId,List<ScenePhotoImageData> list){
        this.list = list;
        this.caseId = caseId;
        this.belongTo=belongTo;
        this.mode=mode;
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

        Log.d("position",""+position);
        Log.d("getCount()", "" + getCount());
        ViewHolder viewHolder;
        if (position !=0 ) {
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
            viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            CheckBox edit_picture_checkbox= (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
            viewHolder.scene_photo.setImageBitmap(list.get(position-1).getScene_photo());
            viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ShowPictureActivity.class)
                            .putExtra("position", position - 1)
                            .putExtra("data", "edit")
                            .putExtra("caseId", caseId)
                            .putExtra("belongTo", belongTo);
                    Log.d("belongTotao",belongTo);
                    v.getContext().startActivity(intent);

                }
            });




        } else if(position == 0) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            if ("find".equals(mode)) {

            } else if ("edit".equals(mode)) {
                convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ScenePhotos.tabflage.equals("5") || ScenePhotos.tabflage.equals("6")) {
                            ScenePhotos.tabflage = "1";
                        }
                        v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class).
                                putExtra("cameraType", "scene")
                                .putExtra("caseId", caseId)
                                .putExtra("belongTo", belongTo));

                    }
                });
            }
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


    private class ViewHolder {
        private ImageView scene_photo;
    }

    public  static class ScenePhotoImageData {
        public Bitmap scene_photo;
       /* public String allCount;
        public String generalCount;
        public String keyCount;
        public String detailCount;
        public String otherCount;*/


        public Bitmap getScene_photo() {return scene_photo;}
        public void setScene_photo(Bitmap scene_photo) {this.scene_photo = scene_photo;}

       /* public String getAllCount() {return allCount;}
        public void setAllCount(String allCount) {this.allCount = allCount;}

        public String getGeneralCount() {return generalCount;}
        public void setGeneralCount(String generalCount) {this.generalCount = generalCount;}

        public String getKeyCount() {return allCount;}
        public void setKeyCount(String keyCount) {this.keyCount = keyCount;}

        public String getdetailCount() {return detailCount;}
        public void setDetailCount(String detailCount) {this.detailCount = detailCount;}

        public String getOtherCount() {
            return otherCount;
        }
        public void setOtherCount(String otherCount) {
            this.otherCount = otherCount;
        }*/
    }
}

