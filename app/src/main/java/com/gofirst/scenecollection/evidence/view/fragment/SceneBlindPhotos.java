package com.gofirst.scenecollection.evidence.view.fragment;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.ImageBean;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoAllAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoAllAdapter.SceneBlindPhotoAllData;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoImageAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData;
import com.gofirst.scenecollection.evidence.view.customview.CameraView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 */
public class SceneBlindPhotos extends Fragment implements View.OnClickListener {
    private GridView ImageGridview;
    private SceneBlindPhotoImageAdapter adapter;
    private SceneBlindPhotoAllAdapter allAdapter;
    private List<SceneBlindPhotoImageData> listPhoto = new ArrayList<SceneBlindPhotoImageData>();
    private List<SceneBlindPhotoAllData> listAllPhoto = new ArrayList<SceneBlindPhotoAllData>();


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
//    private TextView generalPictureCount,keyPictureCount,detailPictureCount,otherPictureCount;
    private CheckBox edit_picture_checkbox;
    public static final int UPDATE_TEXT = 1;
    public static final int BUTTON_SHOW = 3;
    private String flage="1";
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private SharePre shp_info ;
    //public static String tabflage="1";
    public static int generalPictureCount=0, keyPictureCount=0,
    detailPictureCount=0,otherPictureCount=0;
    private String belongTo="unclass";
    private LinearLayout bottom_sec;


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
        View view = inflater.inflate(R.layout.scene_blind_photos,null);
        bottom_sec=(LinearLayout)view.findViewById(R.id.bottom_sec);
        shp_info = new SharePre(getActivity(), PublicMsg.SHP_SHOW, Context.MODE_PRIVATE);
        ImageGridview = (GridView)view.findViewById(R.id.gridview);
        ScenePhotos.tabflage="5";
        getPhotos(ScenePhotos.tabflage);
//        ImageGridview.setAdapter(adapter = new SceneBlindPhotoImageAdapter(listPhoto));


        ImageView=(ImageView)view.findViewById(R.id.imageview);


        general_tab=(TextView)view.findViewById(R.id.general_tab);
        general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
        key_tab=(TextView)view.findViewById(R.id.key_tab);
        generalPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.general_picture);
        keyPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.key_picture);
        detailPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.detail_picture);
        otherPicture_relativeLayout=(RelativeLayout)view.findViewById(R.id.other_picture);

        generalPictureTextview=(TextView)view.findViewById(R.id.general_picture_text);
        keyPictureTextview=(TextView)view.findViewById(R.id.key_picture_text);
        detailPictureTextview=(TextView)view.findViewById(R.id.detail_picture_text);
        otherPictureTextview=(TextView)view.findViewById(R.id.other_picture_text);

        generalPictureCount=getPathDataCount("genaral");
        keyPictureCount=getPathDataCount("key");
        detailPictureCount=getPathDataCount("detail");
        otherPictureCount=getPathDataCount("other");;


        generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));


        ((ProspectInterface)getActivity()).setOnActivityResumeListener(new ProspectInterface.OnActivityResumeListener() {
            @Override
            public void ActivityResumeListener() {
                Log.d("notifyDataSetChanged", "test");
                getPhotos(ScenePhotos.tabflage);
                adapter.notifyDataSetChanged();

            }
        });


        general_tab.setOnClickListener(this);
        key_tab.setOnClickListener(this);


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
        path="/Pictures/MyPictures1";
        getPathData(path);
        return bitmaps;
    }

    private void getPathData(String path){
        SceneBlindPhotoImageData sceneBlindPhotoImageData;
        File file = new File(Environment.getExternalStorageDirectory().getPath()+path);
        if (!file.exists()){
            if (!file.mkdirs()){
                Log.i("MyPictures", "创建图片存储路径目录失败");
                return ;
            }
        }

        File[] files = file.listFiles();
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"belongTo like '%" + belongTo + "'");
        for (int i = 0; i < list.size(); i++){
            sceneBlindPhotoImageData=new SceneBlindPhotoImageData();
            sceneBlindPhotoImageData.setScene_photo(BitmapUtils.revitionImageSize(list.get(i).getFilePath()));
            Log.d("getFilePath",""+list.get(i).getFilePath());
            listPhoto.add(sceneBlindPhotoImageData);
        }
    }

    private int  getPathDataCount(String belongTo){
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "belongTo like '%" + belongTo + "'");
        return list.size();
    }




    private Bitmap getPhotoBitmap(File PhotoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(PhotoPath.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,thumbSize, thumbSize,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    @Override
    public void onResume() {
        super.onResume();
    //    getPhotos(ScenePhotos.tabflage);
    //    adapter.notifyDataSetChanged();
    }





    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){

            //上面的tab
            case R.id.general_tab:

                general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                PublicMsg.belongTo="unclass";
                ScenePhotos.tabflage="5";
                getPhotos(ScenePhotos.tabflage);
                adapter.notifyDataSetChanged();

                break;

            case R.id.key_tab:

                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));

                PublicMsg.belongTo="all";
               /* getPhotos(ScenePhotos.tabflage);*/
 //               adapter.notifyDataSetChanged();

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



    public class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String string=arg1.getStringExtra("data");
            Toast.makeText(arg0, "received:", Toast.LENGTH_SHORT).show();
            SceneBlindPhotos sceneBlindPhotos=new SceneBlindPhotos();
            sceneBlindPhotos.bottom_sec.setVisibility(View.VISIBLE);
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
                    bottom_sec.setVisibility(View.GONE);
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
        SceneBlindPhotoImageData sceneBlindPhotoImageData;
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                sceneBlindPhotoImageData=new SceneBlindPhotoImageData();
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
}
