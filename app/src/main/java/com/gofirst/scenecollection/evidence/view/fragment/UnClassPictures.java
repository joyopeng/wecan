package com.gofirst.scenecollection.evidence.view.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.PicturesData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.sync.UnUploadSingleJson;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.ImageBean;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoAllAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoImageAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.CameraView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class UnClassPictures extends Fragment implements View.OnClickListener{
    private GridView ImageGridview;
    private SceneBlindPhotoImageAdapter adapter;
    private SceneBlindPhotoAllAdapter allAdapter;
    private List<SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData> listPhoto = new ArrayList<SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData>();
    private List<SceneBlindPhotoAllAdapter.SceneBlindPhotoAllData> listAllPhoto = new ArrayList<SceneBlindPhotoAllAdapter.SceneBlindPhotoAllData>();
    private BelongToListener belongToListener;




    public static Bitmap bimap ;
    Camera mCamera;
    CameraView mCameraView;
    private Context mContext;

    private final int SYSTEM_CAMERA_REQUESTCODE = 1;
    private final int MYAPP_CAMERA_REQUESTCODE = 2;
    private Uri imageFileUri = null;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private android.widget.ImageView ImageView ;
    private HashMap<String, LinkedList<String>> mGruopMap = new HashMap<String, LinkedList<String>>();
    private LinkedList<String> chileList,list,mPauseList;
    private final static int SCAN_OK = 1;
    private LinkedList<ImageBean> mBeenList;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int thumbSize;
//    private TextView general_tab,key_tab;
    private RelativeLayout generalPicture_relativeLayout,keyPicture_relativeLayout,
            detailPicture_relativeLayout,positionPicture_relativeLayout;
    /*private TextView generalPictureTextview,keyPictureTextview,
            detailPictureTextview,otherPictureTextview;
    private CircleTextImageView generalCountText,keyCountText,detailCountText,otherCountText;*/
    private CheckBox edit_picture_checkbox;
    public static final int UPDATE_TEXT = 1;
    public static final int BUTTON_SHOW = 3;
    private String flage="1";
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private SharePre shp_info ;
    //public static String tabflage="1";
    public  int generalPictureCount=0, keyPictureCount=0,
            detailPictureCount=0,otherPictureCount=0;
    private String belongTo="";//unclass
    //private LinearLayout bottom_sec,edit_show,copy,delete;
    private LinearLayout bottom_sec;
    private int count=0;
    private ArrayList<Integer> listPosition = new ArrayList<>();
    private String caseId;
    private String father;
    private String fileType="png";
    private String mode;
    private LinearLayout belongto_linearLayout,copy_linearLayout,more_linearLayout;
    private boolean addRec;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
//		System.out.println("OneFragment  onCreate");
        super.onCreate(savedInstanceState);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                reStoreArg(getArguments());
                updatePictures();
                adapter.setCheckList(listPosition);
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter("update_blind"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.scene_blind_photos,null);
        reStoreArg(getArguments());
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        belongTo = getArguments().getString("belongTo");
        addRec = getArguments().getBoolean(BaseView.ADDREC);
        if(belongTo!=null&&belongTo.equals("unclass")){
            belongTo="";
        }
        mode=getArguments().getString("mode");
        bottom_sec=(LinearLayout)view.findViewById(R.id.bottom_sec);
        shp_info = new SharePre(getActivity(), PublicMsg.SHP_SHOW, Context.MODE_PRIVATE);
        ImageGridview = (GridView)view.findViewById(R.id.gridview);
        ScenePhotos.tabflage="5";
        Init(view);
        getPhotos(ScenePhotos.tabflage);
        ImageGridview.setAdapter(adapter = new SceneBlindPhotoImageAdapter(father,mode,belongTo,
                caseId,getArguments().getString("templateId"),listPhoto,
                new SceneBlindPhotoImageAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {

                        if(position>0) {
                            listPosition.add(position);
                        }else{
                            for(int i=0;i<listPosition.size();i++){
                                if(listPosition.get(i)==-position)
                                    listPosition.remove(i);
                            }

                        }

                        //adapter.chiceState(position);

                       // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();

                    }
                },addRec));
        ImageView=(ImageView)view.findViewById(R.id.imageview);

        /*generalPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.general_picture);
        keyPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.key_picture);
        detailPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.detail_picture);
        positionPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.other_picture);*/
        //edit=(RelativeLayout)view.findViewById(R.id.edit);
        //edit.setOnClickListener(this);
       // edit_show=(LinearLayout)view.findViewById(R.id.edit_show);
        if(mode != null && mode.equals(BaseView.VIEW)){
            //bottom_sec.setVisibility(View.GONE);
           // edit_show.setVisibility(View.GONE);
            copy_linearLayout.setEnabled(false);
            more_linearLayout.setEnabled(false);

        }
        /*copy=(LinearLayout)view.findViewById(R.id.copy);
        delete=(LinearLayout)view.findViewById(R.id.delete);
        copy.setOnClickListener(this);
        delete.setOnClickListener(this);*/

        /*generalPictureTextview=(TextView)view.findViewById(R.id.general_picture_text);
        keyPictureTextview=(TextView)view.findViewById(R.id.key_picture_text);
        detailPictureTextview=(TextView)view.findViewById(R.id.detail_picture_text);
        otherPictureTextview=(TextView)view.findViewById(R.id.other_picture_text);


        generalCountText=(CircleTextImageView)view.findViewById(R.id.general_picture_count);
        generalCountText.setText(String.valueOf(getPathDataCount("general")));
        keyCountText=(CircleTextImageView)view.findViewById(R.id.key_picture_count);
        keyCountText.setText(String.valueOf(getPathDataCount("key")));
        detailCountText=(CircleTextImageView)view.findViewById(R.id.detail_picture_count);
        detailCountText.setText(String.valueOf(getPathDataCount("detail")));
        otherCountText=(CircleTextImageView)view.findViewById(R.id.other_picture_count);
        otherCountText.setText(String.valueOf(getPathDataCount("position")));*/


        generalPictureCount=getPathDataCount("genaral");
        keyPictureCount=getPathDataCount("key");
        detailPictureCount=getPathDataCount("detail");
        otherPictureCount=getPathDataCount("position");


        /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));*/


        ((ProspectInterface) getActivity()).setOnActivityResumeListener(new ProspectInterface.OnActivityResumeListener() {
            @Override
            public void ActivityResumeListener() {
                Log.d("notifyDataSetChanged", "test");
                getPhotos(ScenePhotos.tabflage);
                adapter.notifyDataSetChanged();

            }
        });


      /*  general_tab.setOnClickListener(this);
        key_tab.setOnClickListener(this);*/


        /*generalPicture_relativeLayout.setOnClickListener(this);
        keyPicture_relativeLayout.setOnClickListener(this);
        detailPicture_relativeLayout.setOnClickListener(this);
        positionPicture_relativeLayout.setOnClickListener(this);*/




        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveArg(getArguments());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveArg(getArguments());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reStoreArg(savedInstanceState);
    }

    private void saveArg(Bundle outState){
        if (listPosition.size() != 0)
            outState.putSerializable("listPosition",listPosition);
    }
    private void reStoreArg(Bundle savedInstanceState){
        if (savedInstanceState != null && listPosition.size() == 0){
            List<Integer> saveListPosition = (List<Integer>) savedInstanceState.getSerializable("listPosition");
            if (saveListPosition != null)
            listPosition.addAll(saveListPosition);
        }
    }
    private void Init(View v){
        belongto_linearLayout=(LinearLayout)v.findViewById(R.id.belongto_linearLayout);
        copy_linearLayout=(LinearLayout)v.findViewById(R.id.copy_linearLayout);
        more_linearLayout=(LinearLayout)v.findViewById(R.id.more_linearLayout);
        belongto_linearLayout.setOnClickListener(this);
        copy_linearLayout.setOnClickListener(this);
        more_linearLayout.setOnClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SYSTEM_CAMERA_REQUESTCODE && resultCode == -1){
            Log.d("onActivityResult","onActivityResult");
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
        path="/Picture/MyPictures3";
        getPathData(path);
        return bitmaps;
    }

    private String getBelongToName(String belongTo){
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class,
                "parentKey = 'XCZPZLDM'");
        String belongToName="";
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getDictKey().equals(belongTo)) {
                    belongToName=list.get(i).getDictValue1();
                }
            }
        }
        return belongToName;
    }

    private void getPathData(String path){
        SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData sceneBlindPhotoImageData;
  //      File file = new File(Environment.getExternalStorageDirectory().getPath()+path);
        /*File file =new File(getActivity().getObbDir()+path);
        if (!file.exists()){
            if (!file.mkdirs()){
                Log.i("MyPictures", "创建图片存储路径目录失败");
                return ;
            }
        }

        File[] files = file.listFiles();*/
        List<RecordFileInfo> list= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + "" + "' and caseId = '" + caseId + "' and fileType = 'png' and father ='"+father+"'");
        Log.d("blindsize", "" + list.size());
        for (int i = 0; i < list.size(); i++){
            sceneBlindPhotoImageData=new SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData();
            //sceneBlindPhotoImageData.setScene_photo(BitmapUtils.revitionImageSize(list.get(i).getFilePath()));

            //BitmapFactory.decodeFile(uri.toString());
            sceneBlindPhotoImageData.setScene_photo(BitmapFactory.decodeFile(AppPathUtil.getDataPath()+"/"+list.get(i).getContractionsFilePath()));
            Log.d("getFilePath", "" + list.get(i).getFilePath());
            listPhoto.add(sceneBlindPhotoImageData);
        }
    }

    private int  getPathDataCount(String belongTo){
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png'and father ='" + father + "'");
        return list.size();
    }




    private Bitmap getPhotoBitmap(File PhotoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(PhotoPath.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,thumbSize, thumbSize,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private void updatePictures() {
        getPhotos(ScenePhotos.tabflage);
        adapter.notifyDataSetChanged();
        ImageGridview.setAdapter(adapter = new SceneBlindPhotoImageAdapter(father, mode, belongTo,
                caseId, getArguments().getString("templateId"), listPhoto,
                new SceneBlindPhotoImageAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {

                        if (position > 0) {
                            listPosition.add(position);
                        } else {
                            for (int i = 0; i < listPosition.size(); i++) {
                                if (listPosition.get(i) == -position)
                                    listPosition.remove(i);
                            }

                        }

                        //adapter.chiceState(position);

                        // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();

                    }
                },addRec));
    }
    @Override
    public void onResume() {
        super.onResume();
        reStoreArg(getArguments());
        updatePictures();
        adapter.setCheckList(listPosition);
       /* generalCountText.setText(String.valueOf(getPathDataCount("general")));
        keyCountText.setText(String.valueOf(getPathDataCount("key")));
        detailCountText.setText(String.valueOf(getPathDataCount("detail")));
        otherCountText.setText(String.valueOf(getPathDataCount("position")));*/
//        saveJson();
    }





    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){


            //下面的tab

            case R.id.general_picture:

               /* generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));*/

                /*generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/
                if(listPosition.size()>0){
                    setBelongTo("2");//general
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.key_picture:
                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));*/

                /*generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/
                if(listPosition.size()>0){
                    setBelongTo("3");//key
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.detail_picture:
                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));*/

                /*generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/
                if(listPosition.size()>0){
                    setBelongTo("4");//detail
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.other_picture:
               /* generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));*/

                /*generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));*/
                if(listPosition.size()>0){
                    setBelongTo("9");//position
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.edit:
                count++;
               /* if(count%2==0){
                    edit_show.setVisibility(View.VISIBLE);
                }
                else{edit_show.setVisibility(View.GONE);}*/
                break;
            case R.id.copy_linearLayout:
                if(listPosition.size()>0){
                    List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png' and father ='"+father+"'");
                    for(int i=0;i<listPosition.size();i++) {
                        String id = ViewUtil.getUUid();
                        String photoId= ViewUtil.getUUid();
                        list.get(listPosition.get(i)-1).setId(id);
                        list.get(listPosition.get(i) - 1).setAttachmentId(id);
                        list.get(listPosition.get(i)-1).setIsMarked("");
                        list.get(listPosition.get(i)-1).setPhotoId(photoId);
                        list.get(listPosition.get(i)-1).setRefKeyId(photoId);
                        EvidenceApplication.db.save(list.get(listPosition.get(i)-1));
                    }
                    getPhotos(ScenePhotos.tabflage);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(),"复制了"+listPosition.size()+"张照片",Toast.LENGTH_SHORT).show();
                    listPosition.clear();
                }else{
                    Toast.makeText(getActivity(),"请选择复制的照片",Toast.LENGTH_SHORT).show();
                }
                if (belongToListener != null)
                    belongToListener.onBelongTo();
                break;

//            case R.id.delete:
//                if(listPosition.size()>0){
//                    Log.d("delete","delete");
//                    List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
//                            "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png' and father ='"+father+"'");
//                    Log.d("deletelength", list.size() + "");
//    //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());
//                    for(int i=0;i<listPosition.size();i++) {
//                        EvidenceApplication.db.deleteById(RecordFileInfo.class, list.get(listPosition.get(i) - 1).getId());
//                        Log.d("delete1", list.get(listPosition.get(i) - 1).getId() + "");
//                    }
//
//                    getPhotos(ScenePhotos.tabflage);
//                    adapter.notifyDataSetChanged();
//                    listPosition.clear();
//                }
//                else{
//                    Toast.makeText(getActivity(),"请选择删除的照片",Toast.LENGTH_SHORT).show();
//                }
//                if (belongToListener != null)
//                    belongToListener.onBelongTo();
//                break;

            case R.id.belongto_linearLayout:
                showUpdateDialog();
                break;

            case R.id.more_linearLayout:
                showDeleteDialog();
                break;
        }
    }

    private void copyPicture(){
        if(listPosition.size()>0){
            List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                    "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and fileType = 'png' and father ='"+father+"'");
            for(int i=0;i<listPosition.size();i++) {
                list.get(listPosition.get(i)-1).setId(ViewUtil.getUUid());
                EvidenceApplication.db.save(list.get(listPosition.get(i)-1));
            }
            getPhotos(ScenePhotos.tabflage);
            adapter.notifyDataSetChanged();

            Toast.makeText(getActivity(),"复制了"+listPosition.size()+"张照片",Toast.LENGTH_SHORT).show();
            listPosition.clear();
        }else{
            Toast.makeText(getActivity(),"请选择复制的照片",Toast.LENGTH_SHORT).show();
        }
        if (belongToListener != null)
            belongToListener.onBelongTo();
    }

    private void setBelongTo(String belongTo){
        List<RecordFileInfo> list = EvidenceApplication.db.
                findAllByWhere(RecordFileInfo.class,
                        "photoType = '' and caseId = '" + caseId + "' and father ='"+father+"'");//unclass

        for(int i=0;i<listPosition.size();i++) {
            RecordFileInfo recordFileInfo = list.get(listPosition.get(i)-1);
            recordFileInfo.setPhotoType(belongTo);
            recordFileInfo.setPhotoTypeName(getBelongToName(belongTo));
            EvidenceApplication.db.update(recordFileInfo,
                    "id = '" +list.get(listPosition.get(i)-1).getId() + "'");
        }
        getPhotos(ScenePhotos.tabflage);
        adapter.notifyDataSetChanged();
        /*generalCountText.setText(String.valueOf(getPathDataCount("general")));
        keyCountText.setText(String.valueOf(getPathDataCount("key")));
        detailCountText.setText(String.valueOf(getPathDataCount("detail")));
        otherCountText.setText(String.valueOf(getPathDataCount("position")));*/
        listPosition.clear();
        if (belongToListener != null)
            belongToListener.onBelongTo();

    }

    public interface BelongToListener{
        void onBelongTo();
    }

    public void setOnBelongToListener(BelongToListener belongToListener){
        this.belongToListener = belongToListener;
    }


    public class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String string=arg1.getStringExtra("data");
            Toast.makeText(arg0, "received:", Toast.LENGTH_SHORT).show();
     //       SceneBlindPhotos sceneBlindPhotos=new SceneBlindPhotos();
//            sceneBlindPhotos.bottom_sec.setVisibility(View.VISIBLE);
            /*Message message = new Message();
            message.what = BUTTON_SHOW;
            handler.sendMessage(message);*/ // 将Message对象发送出去

        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUTTON_SHOW:
                    // 在这里可以进行UI操作
                    //bottom_sec.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };


    private void isExistFiles(File file){
        if (!file.exists()){
            file.mkdirs();
        }
        SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData sceneBlindPhotoImageData;
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                sceneBlindPhotoImageData=new SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData();
                if (files[i].getName().contains(".jpg")) {

                    sceneBlindPhotoImageData.setScene_photo(BitmapUtils.revitionImageSize(files[i].getPath()));
                    //            photoFiles.add(files[i]);
                    listPhoto.add(sceneBlindPhotoImageData);
                }else if (files[i].isDirectory()){
                    isExistFiles(files[i]);
                }
            }
        }
    }



    private void saveJson() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "caseId = '" + caseId + "' and fileType = '" + fileType + "' and pictureType = 'blind' ");
        Log.d("testtao",list.size()+"");
        for (RecordFileInfo recordFileInfo : list){
            Log.d("testtao",100+"");
            //产生主DATA
            if (recordFileInfo.getAttachmentId() != null){
                Log.d("testtao",200+"");
                if(recordFileInfo.getPictureType().equals("blind")) {
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
                   com.alibaba.fastjson.JSONObject picDataJson = (com.alibaba.fastjson.JSONObject) JSON.toJSON(picturesData);
                    picDataJson.put("MAIN_ID", UnUploadSingleJson.getIvestId(caseId, "SCENE_INVESTIGATION_EXT"));
                    DataTemp picDataTemp = recordFileInfo.isAddRec() ? SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "picData"):
                            SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "picData");
                    picDataTemp.setDataType("scene_investigation_data");
                    picDataTemp.setData(picDataJson.toString());
                    EvidenceApplication.db.update(picDataTemp);
                    //产生附件DATA
                    String timeStamp = new SimpleDateFormat("yyyyMMdd")
                            .format(new Date());
                    String recJson = JSON.toJSONString(recordFileInfo);
                    try {
                        JSONObject jsonObject = new JSONObject(recJson);
                        jsonObject.put("filePath", recordFileInfo.getFilePath());
                        jsonObject.put("twoHundredFilePath", recordFileInfo.getTwoHundredFilePath());
                        jsonObject.put("contractionsFilePath",recordFileInfo.getContractionsFilePath());
                        jsonObject.remove("ID");
                        DataTemp recDataTemp = recordFileInfo.isAddRec() ? SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(),
                                recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "recData") : SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(),
                                recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "recData");
                        recDataTemp.setDataType("common_attachment");
                        recDataTemp.setData(jsonObject.toString());
                        EvidenceApplication.db.update(recDataTemp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause", "onPausetaofa");
        saveJson();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop", "onStoptaofa");
        saveJson();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroytaofa");
        saveJson();
    }


    Dialog UserNameDialog;

    private void showUpdateDialog() {
        TextView general, key,detail,position, other;
        UserNameDialog = new Dialog(getActivity(), R.style.FullHeightDialog1);
        UserNameDialog.setContentView(R.layout.belongto_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        general = (TextView) UserNameDialog.findViewById(R.id.generalPicture_relativeLayout);
        key= (TextView) UserNameDialog.findViewById(R.id.keyPicture_relativeLayout);
        detail= (TextView) UserNameDialog.findViewById(R.id.detailPicture_relativeLayout);
        position= (TextView) UserNameDialog.findViewById(R.id.positionPicture_relativeLayout);
        other= (TextView) UserNameDialog.findViewById(R.id.otherPicture_relativeLayout);
        UserNameDialog.show();
        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listPosition.size()>0){
                    setBelongTo("2");
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }
                UserNameDialog.dismiss();
            }
        });

        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listPosition.size()>0){
                    setBelongTo("3");
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }
                UserNameDialog.dismiss();
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listPosition.size()>0){
                    setBelongTo("4");
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }

                UserNameDialog.dismiss();
            }
        });

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listPosition.size()>0){
                    setBelongTo("1");
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }

                UserNameDialog.dismiss();
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listPosition.size()>0){
                    setBelongTo("9");
                }
                else{
                    Toast.makeText(getActivity(),"请选择分类的照片",Toast.LENGTH_SHORT).show();
                }

                UserNameDialog.dismiss();
            }
        });
        if (belongToListener != null)
            belongToListener.onBelongTo();

    }

    private void showDeleteDialog() {
        TextView delete;
        UserNameDialog = new Dialog(getActivity(), R.style.FullHeightDialog1);
        UserNameDialog.setContentView(R.layout.delete_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        delete = (TextView) UserNameDialog.findViewById(R.id.delete_relativeLayout);


        UserNameDialog.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listPosition.size()>0){
                    Log.d("delete","delete");
                    List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "photoType = '" + belongTo + "' and caseId = '" + caseId + "' and father ='"+father+"'");
                    Log.d("deletelength", list.size() + "");
                    //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());
                    for(int i=0;i<listPosition.size();i++) {
                        EvidenceApplication.db.deleteById(RecordFileInfo.class, list.get(listPosition.get(i)-1).getId());
                        Log.d("delete1",list.get(listPosition.get(i)-1).getId()+"");
                        List<PicturesData> listPicture = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                                "photoId = '"+ list.get(listPosition.get(i) - 1).getId()+"'");
                        if(listPicture!=null&&listPicture.size()>0){
                            EvidenceApplication.db.deleteById(PicturesData.class,listPicture.get(0).getId());
                        }
                        EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + list.get(listPosition.get(i)-1).getFather() + list.get(listPosition.get(i)-1).getAttachmentId() + "picData'");
                        EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + list.get(listPosition.get(i)-1).getFather() + list.get(listPosition.get(i)-1).getAttachmentId() + "recData'");
                    }

                    getPhotos(ScenePhotos.tabflage);
                    adapter.notifyDataSetChanged();
                    listPosition.clear();
                }
                else{
                    Toast.makeText(getActivity(),"请选择删除的照片",Toast.LENGTH_SHORT).show();
                }
                if (belongToListener != null)
                    belongToListener.onBelongTo();
                else{
                    Toast.makeText(getActivity(),"请选择删除的照片",Toast.LENGTH_SHORT).show();
                }
                UserNameDialog.dismiss();
            }
        });

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
                //Log.d("TAGXX1", picturesData.getId() + "");

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
        }else{
            PicturesData picturesData = new PicturesData();
            picturesData.setId(refKeyId);
            picturesData.setATTACHMENT_ID(attachId);
            //Log.d("TAGXX1", picturesData.getId() + "");

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

        }
            return EvidenceApplication.db.findAllByWhere(PicturesData.class, "ATTACHMENT_ID = '" + attachId + "'").get(0);
    }

}




