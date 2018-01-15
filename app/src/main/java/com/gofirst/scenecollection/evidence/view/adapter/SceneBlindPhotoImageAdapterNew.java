package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/28.
 */
public class SceneBlindPhotoImageAdapterNew extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private List<SceneBlindPhotoImageData> list = new ArrayList<SceneBlindPhotoImageData>();
    private LayoutInflater layoutInflater;
    private Context context;
    ClickListener listener;
    private String caseId;
    private String belongTo;
    private boolean isChice[];
    private String mode;
    private String father;
    private String templateId;
    public SceneBlindPhotoImageAdapterNew(String father,String mode,String belongTo,String caseId,String templateId,List<SceneBlindPhotoImageData> list,ClickListener listener){
        this.caseId=caseId;
        this.list = list;
        this.listener=listener;
        this.belongTo=belongTo;
        this.mode=mode;
        this.father=father;
        this.templateId = templateId;
        isChice=new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            isChice[i]=false;
        }

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
        if (position !=0 ) {

            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
            viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.picture_checkbox= (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
            viewHolder.scene_photo.setImageBitmap(list.get(position-1).getScene_photo());


            /*viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowBlindActivity.class)
                            .putExtra("mode", mode)
                            .putExtra("caseId",caseId)
                            .putExtra("position", position - 1)
                            .putExtra("templateId",templateId);

                    v.getContext().startActivity(intent);
                }
            });
            viewHolder.picture_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked==true){
                        listener.onClick(position);
                    }else{
                        listener.onClick(-position);
                    }
                }
            });*/

        } else if(position == 0) {


            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            if(mode != null && mode.equals(BaseView.VIEW)){

            }else{
               /* convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!ScenePhotos.tabflage.equals("5")&!ScenePhotos.tabflage.equals("6")){
                            ScenePhotos.tabflage="5";
                        }
                        v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class)
                                .putExtra("belongTo", belongTo)
                                .putExtra("cameraType", "blind")
                                .putExtra("caseId",caseId)
                                .putExtra("father",father));

                        //cameraType *//*scene blind addevidence*//*
                        //pictureType
                    }
                });*/
            }
        }
        return convertView;
    }

    public interface ClickListener{
        void onClick(int position);
    }


    private class ViewHolder {
        private ImageView scene_photo;
        private CheckBox picture_checkbox;
    }

    public  static class SceneBlindPhotoImageData {
        public Bitmap scene_photo;
        public Bitmap getScene_photo() {return scene_photo;}
        public void setScene_photo(Bitmap scene_photo) {this.scene_photo = scene_photo;}
    }

    public void chiceState(int post)
    {
        isChice[post]=isChice[post]==true?false:true;
        this.notifyDataSetChanged();
    }

}


