package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.FileUtils;
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.ShowBlindActivity;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */
public class AddEvidencePhotoAdapter extends BaseAdapter {

    /*ArrayList<Bitmap> thumbs;
    ArrayList<File> sceneFiles;*/
    private final int CAMERA_WITH_DATA = 2;
    private String filePath = null;
    private String fileName = null;

    private List<AddEvidencePhotoData> list = new ArrayList<AddEvidencePhotoData>();
    private LayoutInflater layoutInflater;
    private Context context;
    ClickListener listener;
    private String caseId;
    private String belongTo;
    private boolean isChice[];
    private String mode;
    private String father;
    private String templateId;
    private String id;
    private boolean addRec;
    public AddEvidencePhotoAdapter(Context context,String id,String father,String caseId,
                                   String templateId,List<AddEvidencePhotoData> list,ClickListener listener,boolean addRec){
        this.caseId=caseId;
        this.list = list;
        this.listener=listener;
        this.belongTo=belongTo;
        this.mode=mode;
        this.id=id;
        this.father=father;
        this.context=context;
        this.templateId = templateId;
        isChice=new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            isChice[i]=false;
        }

        /*this.context=context;
        layoutInflater = LayoutInflater.from(context);*/
        this.addRec = addRec;
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
        if(mode != null && mode.equals(BaseView.VIEW)){//不可编辑
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
            viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
            viewHolder.scene_photo.setImageBitmap(list.get(position).getScene_photo());


            viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowBlindActivity.class)
                            .putExtra("mode", mode)
                            .putExtra("caseId", caseId)
                            .putExtra("position", position)
                            .putExtra("templateId", templateId);

                    v.getContext().startActivity(intent);
                }
            });
            viewHolder.picture_checkbox.setVisibility(View.GONE);
            viewHolder.picture_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        listener.onClick(position);
                    } else {
                        listener.onClick(position);
                    }
                }
            });

            // convertView.setVisibility(View.GONE);
        }else {//可编辑


            if (position != 0) {

                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_blind_item, parent, false);
                viewHolder.scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
                viewHolder.picture_checkbox = (CheckBox) convertView.findViewById(R.id.edit_picture_checkbox);
                viewHolder.scene_photo.setImageBitmap(list.get(position - 1).getScene_photo());

                viewHolder.scene_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(v.getContext(), ShowBlindActivity.class)
//                                .putExtra("mode", mode)
//                                .putExtra("caseId", caseId)
//                                .putExtra("position", position - 1)
//                                .putExtra("templateId", templateId);
//
//                        v.getContext().startActivity(intent);
                        listener.onClick(position);
                    }
                });
                viewHolder.picture_checkbox.setVisibility(View.GONE);
                viewHolder.picture_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            listener.onClick(position);
                        } else {
                            listener.onClick(-position);
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
//                                    .putExtra("data", "addevidence")
//                                    .putExtra("isEvidence", true)
//                                    .putExtra("father", father)
//                                    .putExtra("id", id)
//                                    .putExtra("caseId", caseId)
//                                    .putExtra(BaseView.ADDREC,addRec));

                                   /* .putExtra("belongTo", belongTo)
                                    .putExtra("cameraType", "blind")
                                    .putExtra("caseId", caseId)
                                    .putExtra("father", father));*/

                            //cameraType /*scene blind addevidence*/
                            //pictureType

//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message); // 将Message对象发送出去
                            startCamera(v.getContext());
                        }
                    });




                }
            }
        }
        return convertView;
    }



    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Activity activity = (Activity)context;
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    };


    public interface ClickListener{
        void onClick(int position);
    }


    private class ViewHolder {
        private ImageView scene_photo;
        private CheckBox picture_checkbox;
    }

    public  static class AddEvidencePhotoData {
        public Bitmap scene_photo;
        public Bitmap getScene_photo() {return scene_photo;}
        public void setScene_photo(Bitmap scene_photo) {this.scene_photo = scene_photo;}
    }

    public void chiceState(int post)
    {
        isChice[post]=isChice[post]==true?false:true;
        this.notifyDataSetChanged();
    }

    public void startCamera(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        filePath = timeStamp + "/" + caseId + "/originalPictures/";
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = "IMG_" + time + ".jpg";
        FileUtils.makeFilePath(AppPathUtil.getDataPath() + "/" + filePath, fileName);
        Intent intent = null;
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm
                .checkPermission("android.permission.CAMERA", "com.gofirst.scenecollection.evidence"));
        if (!permission) {
            Toast.makeText(context, "无法访问相机，需在设置中开启该权限", Toast.LENGTH_LONG).show();
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(AppPathUtil.getDataPath() + "/" + filePath, fileName)));
//            } else {
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(context, "com.china.yamato.fileprovider",
//                        new File(AppPathUtil.getDataPath() + "/" + filePath, fileName));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//            }
            ((Activity) context).startActivityForResult(intent, CAMERA_WITH_DATA);
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }
}


