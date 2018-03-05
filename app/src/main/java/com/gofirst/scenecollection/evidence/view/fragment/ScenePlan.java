package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.PicturesData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.ImageBean;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.adapter.ScenePlanAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ScenePlanAdapter.ScenePlanImageData;
import com.gofirst.scenecollection.evidence.view.customview.CameraView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class ScenePlan extends Fragment implements View.OnClickListener {
    private GridView ImageGridview;
    private ScenePlanAdapter adapter;
    private List<ScenePlanImageData> listPhoto = new ArrayList<>();

    public static Bitmap bimap ;
    Camera mCamera;
    CameraView mCameraView;
    private Context mContext;

    private final int SYSTEM_CAMERA_REQUESTCODE = 1;
    private final int MYAPP_CAMERA_REQUESTCODE = 2;
    private Uri imageFileUri = null;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private ImageView ImageView ;
    private HashMap<String, LinkedList<String>> mGruopMap = new HashMap<String, LinkedList<String>>();
    private LinkedList<String> chileList,list,mPauseList;
    private final static int SCAN_OK = 1;
    private LinkedList<ImageBean> mBeenList;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int thumbSize;
    private TextView all_tab,general_tab,key_tab,detail_tab,other_tab;
    private RelativeLayout generalPicture_relativeLayout,keyPicture_relativeLayout,
            detailPicture_relativeLayout,otherPicture_relativeLayout;
    private TextView generalPictureTextview,keyPictureTextview,
            detailPictureTextview,otherPictureTextview;
    private TextView generalPictureCount,keyPictureCount,detailPictureCount,otherPictureCount;
    private CheckBox edit_picture_checkbox;
    public static final int UPDATE_TEXT = 1;
    private String flage="1";
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private SharePre shp_info ;
    public static String planflage="1";
    private String caseId,father,mode;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

//		System.out.println("OneFragment  onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.scene_plan, container, false);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        mode= getArguments().getString("mode");

        ImageGridview = (GridView)view.findViewById(R.id.gridview);
        getPhotos(planflage);
        ImageGridview.setAdapter(adapter = new ScenePlanAdapter(getContext(), mode,caseId,father,listPhoto));

        ImageView=(ImageView)view.findViewById(R.id.imageview);

        all_tab=(TextView)view.findViewById(R.id.all_tab);
        general_tab=(TextView)view.findViewById(R.id.general_tab);
        general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
        key_tab=(TextView)view.findViewById(R.id.key_tab);
        detail_tab=(TextView)view.findViewById(R.id.detail_tab);
        other_tab=(TextView)view.findViewById(R.id.other_tab);

        generalPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.general_picture);
        keyPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.key_picture);
        detailPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.detail_picture);
        otherPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.other_picture);

        generalPictureTextview=(TextView)view.findViewById(R.id.general_picture_text);
        keyPictureTextview=(TextView)view.findViewById(R.id.key_picture_text);
        detailPictureTextview=(TextView)view.findViewById(R.id.detail_picture_text);
        otherPictureTextview=(TextView)view.findViewById(R.id.other_picture_text);

        generalPictureCount=(TextView)view.findViewById(R.id.general_picture_count);
        keyPictureCount=(TextView)view.findViewById(R.id.key_picture_count);
        detailPictureCount=(TextView)view.findViewById(R.id.detail_picture_count);
        otherPictureCount=(TextView)view.findViewById(R.id.other_picture_count);




        /*generalPictureCount.setText(getPathDataCount("/Pictures/MyPictures1"));
        keyPictureCount.setText(getPathDataCount("/Pictures/MyPictures2"));
        detailPictureCount.setText(getPathDataCount("/Pictures/MyPictures3"));
        otherPictureCount.setText(getPathDataCount("/Pictures/MyPictures4"));*/

        /*ScenePhotoImageData scenePhotoImageData;
        scenePhotoImageData = new ScenePhotoImageData();

        scenePhotoImageData.setAllCount(getPathDataCount("/Pictures/MyPictures"));
        scenePhotoImageData.setGeneralCount(getPathDataCount("/Pictures/MyPictures1"));
        scenePhotoImageData.setKeyCount(getPathDataCount("/Pictures/MyPictures2"));
        scenePhotoImageData.setDetailCount(getPathDataCount("/Pictures/MyPictures3"));
        scenePhotoImageData.setOtherCount(getPathDataCount("/Pictures/MyPictures4"));*/


        generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));


        ((ProspectInterface)getActivity()).setOnActivityResumeListener(new ProspectInterface.OnActivityResumeListener() {
            @Override
            public void ActivityResumeListener() {
                Log.d("notifyDataSetChanged", "test");
                getPhotos(planflage);
                adapter.notifyDataSetChanged();

            }
        });

        all_tab.setOnClickListener(this);
        general_tab.setOnClickListener(this);
        key_tab.setOnClickListener(this);
        detail_tab.setOnClickListener(this);
        other_tab.setOnClickListener(this);

        generalPicture_relativeLayout.setOnClickListener(this);
        keyPicture_relativeLayout.setOnClickListener(this);
        detailPicture_relativeLayout.setOnClickListener(this);
        otherPicture_relativeLayout.setOnClickListener(this);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SYSTEM_CAMERA_REQUESTCODE && resultCode == -1){
            Log.d("onActivityResult","onActivityResult");
            //		从保存的文件中取这个拍好的图片*//*
            //         Log.d("MyPicture", imageFileUri.getEncodedPath());
            //          setPicToImageView(ImageView, new File(imageFileUri.getEncodedPath()));

			/*//*上面没有intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);的时候*//*
			*//*将返回Bitmap的缩小图放入到data中，可以通过这样的方式取得*//*
			*//*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView.setImageBitmap(bitmap);*/
            //      adapter.notifyDataSetChanged();
        }
    }

    //-----------------------Android大图的处理方式---------------------------
    private Bitmap setPicToImageView(ImageView imageView, File imageFile){
        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();
        BitmapFactory.Options opts = new BitmapFactory.Options();

        //设置这个，只得到Bitmap的属性信息放入opts，而不把Bitmap加载到内存中
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), opts);

        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;

        int scale = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight);

        //缩放的比例
        opts.inSampleSize = scale;
        //内存不足时可被回收
        opts.inPurgeable = true;
        //设置为false,表示不仅Bitmap的属性，也要加载bitmap
        opts.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), opts);
        //   imageView.setImageBitmap(bitmap);
        return bitmap;
    }




    /*检测相机是否存在*/
    private boolean checkCameraHardWare(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }



    private ArrayList getPhotos(String path){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (listPhoto.size() != 0){
            listPhoto.clear();
        }

        if(planflage.equals("0")){
            path="xckydb/"+caseId;
            getPathData(path);
        }else if(planflage.equals("1")){
            path="xckydb/"+caseId;
            getPathData(path);
        }
        else if(planflage.equals("2")){
            path="xckydb/"+caseId;
            getPathData(path);

        } else if(planflage.equals("3")){
            path="xckydb/"+caseId;
            getPathData(path);

        }else if(planflage.equals("4")){
            path="xckydb/"+caseId;
            getPathData(path);

        }
        return bitmaps;
    }

    private void getPathData(String path){
        ScenePlanImageData scenePlanImageData;
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/"+ path);
        if (!file.exists()){
            if (!file.mkdirs()) {
                Log.d("MyPictures", "创建图片存储路径目录失败");
                Log.d("MyPictures", "mediaStorageDir : " + file.getPath());
                return ;
            }
        }

        File[] files = file.listFiles();
        Log.d("files.length", "" + files.length);
        for (int i = 0; i < files.length; i++){
            scenePlanImageData=new ScenePlanImageData();
            if (files[i].getName().endsWith(".png")){
                Log.d("files[i]", "" + files[i]);
                //               bitmaps.add(getPhotoBitmap(files[i]));
                //scenePlanImageData.setScene_photo(convertToBitmap(files[i].getPath(), 55, 55));
                scenePlanImageData.setScene_photo(getSmallBitmap(files[i].getPath().toString()));
                scenePlanImageData.setImgpath(files[i].getPath().toString());
                //            photoFiles.add(files[i]);
                listPhoto.add(scenePlanImageData);
            }
        }
        saveData(path);

    }

    private void saveData(String path){
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" +path);
        if (!file.exists()){
            if (!file.mkdirs()) {
                return ;
            }
        }

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss")
                    .format(new Date());
            String uuid = ViewUtil.getUUid();

            RecordFileInfo recordFileInfo = new RecordFileInfo();
            recordFileInfo.setId(uuid);
            //recordFileInfo.setPictureType(cameraType);
            recordFileInfo.setCaseId(caseId);
            //recordFileInfo.setFilePath(path);
            recordFileInfo.setFilePath(files[i].getPath().substring(20));
            recordFileInfo.setSaveTime(timeStamp);
            recordFileInfo.setFileType("png");
            recordFileInfo.setPictureaName(files[i].getName());//???
            recordFileInfo.setAttachmentId(uuid);
            recordFileInfo.setFather(father);
            recordFileInfo.setInvestigationId("");
            recordFileInfo.setMainId("");
            recordFileInfo.setContent("");
            recordFileInfo.setDeleteFlag("");

            recordFileInfo.setSceneType(father);
            recordFileInfo.setPhotoName(files[i].getName());
            String photoId = ViewUtil.getUUid();
            recordFileInfo.setPhotoId(photoId);
            recordFileInfo.setRefKeyId(photoId);

            List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                    "pictureaName = '"+files[i].getName()+"'");
            if(list.size()==0){
                EvidenceApplication.db.save(recordFileInfo);
            }else{
                EvidenceApplication.db.update(recordFileInfo);
            }

        }

    }

    private String  getPathDataCount(String path){
        ScenePlanImageData scenePlanImageData;
        File file = new File(Environment.getExternalStorageDirectory().getPath()+path);
        File[] files = file.listFiles();
        Log.d("files.length", "" + files.length);
        return String.valueOf(files.length);
    }




    private Bitmap getPhotoBitmap(File PhotoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(PhotoPath.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,thumbSize, thumbSize,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    @Override
    public void onDestroy() {
        save();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPhotos("0");
        adapter.notifyDataSetChanged();
    }


    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }


    public  Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public  int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }



    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){

            //上面的tab
            case R.id.all_tab:

                all_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                getPhotos("0");
                adapter.notifyDataSetChanged();

                break;
            case R.id.general_tab:
                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                planflage="1";
                getPhotos(planflage);
                adapter.notifyDataSetChanged();

                break;

            case R.id.key_tab:

                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                planflage="2";
                getPhotos(planflage);
                adapter.notifyDataSetChanged();

                break;
            case R.id.detail_tab:
                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                planflage="3";
                getPhotos(planflage);
                adapter.notifyDataSetChanged();
                break;

            case R.id.other_tab:

                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                planflage="4";
                getPhotos(planflage);
                adapter.notifyDataSetChanged();

                break;


            //下面的tab

            case R.id.general_picture:

                generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                otherPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));

                break;
            case R.id.key_picture:
                generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                otherPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));
                break;

            case R.id.detail_picture:
                generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                otherPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));

                break;
            case R.id.other_picture:
                generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                otherPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }

    private List<RecordFileInfo> getPlanFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "father = '" + getArguments().getString("father") + "' and caseId = \"" + getArguments().getString("caseId") + "\"", "fileDate desc");
    }

    private void save() {
        for (RecordFileInfo recordFileInfo : getPlanFiles()){
            PicturesData picturesData = getPictureData(recordFileInfo.getAttachmentId(), recordFileInfo.getRefKeyId(),
                    recordFileInfo.getPhotoType(),//现场照片类型
                    recordFileInfo.getPhotoTypeName(),//场照片类型名称
                    recordFileInfo.getPhotoId(),
                    recordFileInfo.getPhotoName(),
                    recordFileInfo.getSceneType(),
                    recordFileInfo.getContractionsFilePath(),
                    recordFileInfo.getTwoHundredFilePath(),
                    recordFileInfo.getFilePath(),
                    recordFileInfo.getDescription());
            String picDataJson = JSON.toJSONString(picturesData);
            try {
                JSONObject jsonObject1 = new JSONObject(picDataJson);

                jsonObject1.put("INVESTIGATION_ID", getArguments().getString("caseId"));
                jsonObject1.put("PHOTO_NAME",recordFileInfo.getPhotoName());
                jsonObject1.put("PICTURE_TYPE","1010");
                jsonObject1.put("PICTURE_TYPE_NAME","现场平面图");
                jsonObject1.put("PICTURE_ID",recordFileInfo.getAttachmentId());
                jsonObject1.put("DESCRIPTION","");
                jsonObject1.put("DELETE_FLAG","");
                jsonObject1.put("CREATE_USER_NAME","");
                jsonObject1.put("CREATE_DATETIME","");
                jsonObject1.put("filePath", recordFileInfo.getFilePath());
                jsonObject1.put("twoHundredFilePath", recordFileInfo.getFilePath());
                jsonObject1.put("contractionsFilePath", recordFileInfo.getFilePath());
                DataTemp picDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "picData");
                picDataTemp.setDataType("scene_investigation_data");
                // picDataTemp.setData(picDataJson);
                picDataTemp.setData(jsonObject1.toString());
                EvidenceApplication.db.update(picDataTemp);

                JSONObject jsonObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                jsonObject.put("filePath", recordFileInfo.getFilePath());
                jsonObject.put("twoHundredFilePath", recordFileInfo.getFilePath());
                jsonObject.put("contractionsFilePath", recordFileInfo.getFilePath());
                jsonObject.remove("ID");
                DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "recData");
                recDataTemp.setDataType("common_attachment");

                recDataTemp.setData(jsonObject.toString());
                EvidenceApplication.db.update(recDataTemp);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //产生附件DATA
            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            String recJson = JSON.toJSONString(recordFileInfo);
        }
    }

    private PicturesData getPictureData(String attachId,String refKeyId,String photoType,
                                        String photoTypeName,String photoId,String photoName,String sceneType,
                                        String contractionsFilePath,
                                        String twoHundredFilePath,
                                        String filePath,
                                        String description){
        List<PicturesData> list = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                "ATTACHMENT_ID = '" + attachId + "'");
        Log.d("TAGXX1", list.size() + "");
        if (list == null || list.size() == 0) {
            // if(photoType.equals("blind")) {
            PicturesData picturesData = new PicturesData();
            picturesData.setId(refKeyId);
            picturesData.setATTACHMENT_ID(attachId);
            Log.d("TAGXX1", picturesData.getId() + "");

            picturesData.setSCENE_TYPE(sceneType);

            picturesData.setType("0");//0：图片；1：视频；2：音频）
            picturesData.setPhotoTypeName(photoTypeName);
            picturesData.setPhotoType(photoType);
            picturesData.setPhotoId(attachId);
            picturesData.setPhotoName(photoName);
            picturesData.setDescription(description);
            List<PicturesData> listrefKeyId = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                    "id = '" + refKeyId + "'");
            if(listrefKeyId.size()>0){
                EvidenceApplication.db.update(picturesData);
            }else {
                EvidenceApplication.db.save(picturesData);
            }
            // }
        }
        return EvidenceApplication.db.findAllByWhere(PicturesData.class,
                "ATTACHMENT_ID = '" + attachId + "'").get(0);
    }
}
