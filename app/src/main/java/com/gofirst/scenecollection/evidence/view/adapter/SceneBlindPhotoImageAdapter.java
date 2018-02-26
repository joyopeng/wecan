package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.sync.FloatWindowService;
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.ShowBlindActivity;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.FloatWindowView;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;
import com.gofirst.scenecollection.evidence.view.logic.ImgFileListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 */
public class SceneBlindPhotoImageAdapter extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private List<SceneBlindPhotoImageData> list = new ArrayList<SceneBlindPhotoImageData>();
    private LayoutInflater layoutInflater;
    private List<Integer> checkList;
    private Context context;
    ClickListener listener;
    private String caseId;
    private String belongTo;
    private boolean isChice[];
    private String mode;
    private String father;
    private String templateId;
    private boolean isAddRec;
    public SceneBlindPhotoImageAdapter(String father,String mode,String belongTo,String caseId,String templateId,List<SceneBlindPhotoImageData> list,
                                       ClickListener listener,boolean isAddRec){
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
        this.isAddRec = isAddRec;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(mode != null && mode.equals(BaseView.VIEW)){
            return list.size();
        }else{
            return list.size()+2;
        }
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;

    }

    public void setCheckList(List<Integer> checkList) {
        this.checkList = checkList;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Log.d("position",""+position);
        Log.d("getCount()", "" + getCount());
        final ViewHolder viewHolder;
        if(mode != null && mode.equals(BaseView.VIEW)){//不可编辑
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
            viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
            viewHolder.scene_photo.setImageBitmap(list.get(position).getScene_photo());
            viewHolder.anchor_flage = (ImageView) convertView.findViewById(R.id.anchor_flage_picture);
            List<RecordFileInfo> list= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                    "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png' and father ='"+father+"'");
            if(list.size()>0){
                if(list.get(position).getIsMarked().equals("yes")){
                    viewHolder.anchor_flage.setVisibility(View.VISIBLE);
                }
            }
            viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowBlindActivity.class)
                            .putExtra("mode", mode)
                            .putExtra("caseId", caseId)
                            .putExtra("position", position)
                            .putExtra("father",father)
                            .putExtra("templateId", templateId)
                            .putExtra(BaseView.ADDREC,isAddRec);

                    v.getContext().startActivity(intent);
                }
            });
            viewHolder.picture_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listener.onClick(position+1);
                    } else {
                        listener.onClick(-(position+1));
                    }
                }
            });


           // convertView.setVisibility(View.GONE);
        }else {//可编辑


            if (position != 0&&position != 1) {

                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
                viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
                viewHolder.picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
                viewHolder.anchor_flage = (ImageView) convertView.findViewById(R.id.anchor_flage_picture);
                viewHolder.scene_photo.setImageBitmap(list.get(position - 2).getScene_photo());
                // 动态扩大复选框点击范围 add by maxiran 2017 3.28
                for (Integer integer : checkList){
                   if (integer + 1 == position)
                        viewHolder.picture_checkbox.setChecked(true);
                }

                ((View)viewHolder.picture_checkbox.getParent()).post(new Runnable() {
                    @Override
                    public void run() {
                        Rect bounds = new Rect();
                        viewHolder.picture_checkbox.setEnabled(true);
                        viewHolder.picture_checkbox.getHitRect(bounds);
                        bounds.top -= 50;
                        bounds.bottom += 50;
                        bounds.left -= 50;
                        bounds.right += 50;
                        TouchDelegate touchDelegate = new TouchDelegate(bounds, viewHolder.picture_checkbox);
                        if (View.class.isInstance(viewHolder.picture_checkbox.getParent()))
                            viewHolder.scene_photo.setTouchDelegate(touchDelegate);
                    }
                });

                List<RecordFileInfo> lists= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png' and father = '"+father+"'");
               /* //if(list.get(position - 2).getScene_photo().equals(lists.get(position - 2).getContractionsFilePath())){
                if(position>=lists.size()){
                    if(lists.get(position - 3).getIsMarked()!=null&&lists.get(position - 3).getIsMarked().equals("yes")){
                        viewHolder.anchor_flage.setVisibility(View.VISIBLE);
                   }
                }else{*/
                Log.i("zhangsh"," else size = " + lists.size() + ";positio = " + position);
                if(position-2>=lists.size()){
                   /* if(lists.get(position - 3).getIsMarked()!=null&&lists.get(position - 3).getIsMarked().equals("yes")){
                        viewHolder.anchor_flage.setVisibility(View.VISIBLE);
                    }*/
                }
                else{
                    if(lists.get(position - 2).getIsMarked()!=null&&lists.get(position - 2).getIsMarked().equals("yes")){
                        viewHolder.anchor_flage.setVisibility(View.VISIBLE);
                    }
                }


                viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ShowBlindActivity.class)
                                .putExtra("mode", mode)
                                .putExtra("caseId", caseId)
                                .putExtra("position", position - 2)
                                .putExtra("father",father)
                                .putExtra("templateId", templateId)
                                .putExtra(BaseView.ADDREC,isAddRec);

                        v.getContext().startActivity(intent);
                    }
                });
                viewHolder.picture_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            listener.onClick(position-1);
                        } else {
                            listener.onClick(-position+1);
                        }
                    }
                });

            } else if (position == 0) {

                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
                if (mode != null && mode.equals(BaseView.VIEW)) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!ScenePhotos.tabflage.equals("5") & !ScenePhotos.tabflage.equals("6")) {
                                ScenePhotos.tabflage = "5";
                            }
//                            v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class)
//                                    .putExtra("belongTo", belongTo)
//                                    .putExtra("cameraType", "blind")
//                                    .putExtra("caseId", caseId)
//                                    .putExtra("father", father)
//                                    .putExtra(BaseView.ADDREC,isAddRec));
                            v.getContext().startService(new Intent(v.getContext(), FloatWindowService.class)
                                    .putExtra("belongTo", belongTo)
                                    .putExtra("cameraType", "blind")
                                    .putExtra("caseId", caseId)
                                    .putExtra("father", father)
                                    .putExtra(BaseView.ADDREC,isAddRec));

                            //cameraType /*scene blind addevidence*/
                            //pictureType
                        }
                    });
                }
            }else if (position == 1) {


                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_from_layout, parent, false);
                if (mode != null && mode.equals(BaseView.VIEW)) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!ScenePhotos.tabflage.equals("5") & !ScenePhotos.tabflage.equals("6")) {
                                ScenePhotos.tabflage = "5";
                            }

                            Intent intent = new Intent();
                            intent.setClass(v.getContext(), ImgFileListActivity.class);
                            intent.putExtra("caseId", caseId);
                            intent.putExtra("father", father);
                            intent.putExtra(BaseView.ADDREC,isAddRec);
                            v.getContext().startActivity(intent);


                            /*v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class)
                                    .putExtra("belongTo", belongTo)
                                    .putExtra("cameraType", "blind")
                                    .putExtra("caseId", caseId)
                                    .putExtra("father", father));*/

                            //cameraType /*scene blind addevidence*/
                            //pictureType
                        }
                    });
                }
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
        private ImageView anchor_flage;
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

