package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gftxcky.draw.ExperModeActivity;
import com.gftxcky.draw.SelModeActivity;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ShowPlanActivity;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class ScenePlanAdapter extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private List<ScenePlanImageData> list = new ArrayList<ScenePlanImageData>();
    private LayoutInflater layoutInflater;
    private Context context;
    private String caseId, father, mode;
    private String receivePeople = "";
    private String alarmAddress = "";
    private String occurDate = "";
    private String sceneArea = "";
    private String caseType = "";
    private SharePre mShares;

    public ScenePlanAdapter(Context context, String mode, String caseId, String father, List<ScenePlanImageData> list) {
        this.list = list;
        this.context=context;
        /*layoutInflater = LayoutInflater.from(context);*/
        this.caseId = caseId;
        this.father = father;
        this.mode = mode;
        mShares = new SharePre(context, "user_info", Context.MODE_PRIVATE);

        initData();
    }


    private void initData() {
        List<CsSceneCases> csSceneCasesList = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
        if (csSceneCasesList != null && csSceneCasesList.size() > 0) {
            receivePeople = csSceneCasesList.get(0).getReceivePeople();
            alarmAddress = csSceneCasesList.get(0).getAlarmAddress();
//            sceneArea = csSceneCasesList.get(0).getSceneRegionalismName();
            sceneArea = mShares.getString("organizationCname", "");
            caseType = csSceneCasesList.get(0).getCaseType();
            if (!TextUtils.isEmpty(caseType) && !caseType.endsWith("案")) {
                caseType = caseType + "案";
            }
            Date date = csSceneCasesList.get(0).getOccurrenceDateFrom();
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                occurDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            }
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return (mode !=null && BaseView.VIEW.equals(mode)) ? list.size() : list.size() + 1;
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
        Log.d("position", "" + position);
        Log.d("getCount()", "" + getCount());
        ViewHolder viewHolder;
        if (mode != null && BaseView.VIEW.equals(mode)) {
            if (list.size() > 0) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
                viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
                CheckBox edit_picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
                viewHolder.scene_photo.setImageBitmap(list.get(position).getScene_photo());
                viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ShowPlanActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("caseId", caseId);
                        intent.putExtra("father", father);
                        intent.putExtra("mode", mode);
                        intent.putExtra("filepath",list.get(position).getScene_photo());
                        v.getContext().startActivity(intent);
                    }
                });
            }
        } else {
            if (position != 0) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
                viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
                CheckBox edit_picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
                viewHolder.scene_photo.setImageBitmap(list.get(position - 1).getScene_photo());
                viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ShowPlanActivity.class);
                        intent.putExtra("position", position - 1);
                        intent.putExtra("caseId", caseId);
                        intent.putExtra("father", father);
                        intent.putExtra("mode", mode);
//                        intent.putExtra("filepath",list.get(position -1).getImgPath());
                  /* Log.d("test123", "" + Uri.parse(list.get(position).getAbsolutePath()));
                    Log.d("test456", "" + Uri.parse(list.get(position).getParentFile().getAbsolutePath()));
                    Log.d("position", "" + position);

                    Log.d("testsize", "" + BitmapUtils.drr.size());
                    intent.setDataAndType(Uri.parse(sceneFiles.get(position).getAbsolutePath()), "Pictures/MyPictures/jpg");
                    intent.setData(Uri.parse(sceneFiles.get(position).getParentFile().getAbsolutePath()));
//                    intent.setData(Uri.parse(sceneFiles.get(position).getAbsolutePath()));
                    intent.putExtra("position", position);*/
                        v.getContext().startActivity(intent);

                   /* Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(sceneFiles.get(position).getAbsolutePath()), "Pictures/MyPictures/jpg");
                    v.getContext().startActivity(intent);*/
                    }
                });
                viewHolder.scene_photo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(v.getContext(), ShowPlanActivity.class);
                        intent.putExtra("position", position - 1);
                        intent.putExtra("caseId", caseId);
                        intent.putExtra("father", father);
                        intent.putExtra("mode", mode);
                        intent.putExtra("filepath",list.get(position -1).getImgPath());
                        v.getContext().startActivity(intent);
                        return true;
                    }
                });


           /* if(ProspectInterface.fragmentEditingFlage.equals("1")){
                edit_picture_checkbox.setVisibility(View.VISIBLE);
            }else{
                edit_picture_checkbox.setVisibility(View.GONE);
            }*/
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_plan_layout, parent, false);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mode != null && mode.equals(BaseView.VIEW)) {

                        } else {
                            v.getContext().startActivity(new Intent(v.getContext(), ExperModeActivity.class)
                                    .putExtra("caseId", caseId)
                                    .putExtra("father", father)
                                    .putExtra("info", "," + occurDate + "," + alarmAddress + "," + sceneArea + "," + receivePeople + "," + caseType));
                        }
                    }
                });
            }
        }
        return convertView;
    }

    /*检测相机是否存在*/
    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }


    private class ViewHolder {

        private ImageView scene_photo;
    }

    public static class ScenePlanImageData {
        public Bitmap scene_photo;
        public String imgpath;
       /* public String allCount;
        public String generalCount;
        public String keyCount;
        public String detailCount;
        public String otherCount;*/

        public Bitmap getScene_photo() {
            return scene_photo;
        }

        public void setScene_photo(Bitmap scene_photo) {
            this.scene_photo = scene_photo;
        }

        public String getImgPath(){
            return imgpath;
        }

        public void setImgpath(String path){
            imgpath = path;
        }
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

