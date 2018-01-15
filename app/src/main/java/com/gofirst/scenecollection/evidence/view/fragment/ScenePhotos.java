package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.ImageBean;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.adapter.ScenePhotoImageAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ScenePhotoImageAdapter.ScenePhotoImageData;
import com.gofirst.scenecollection.evidence.view.customview.CameraView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class ScenePhotos extends Fragment implements OnClickListener {
    private GridView ImageGridview;
    private ScenePhotoImageAdapter adapter;
    private List<ScenePhotoImageData> listPhoto = new ArrayList<ScenePhotoImageData>();

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
    public static String tabflage="1";
    private String caseId;
    private String father;
    private String belongTo="general";
    private String mode;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.scene_photos, container, false);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        belongTo = getArguments().getString("belongTo");
        mode= getArguments().getString("mode");


        shp_info = new SharePre(getActivity(), PublicMsg.SHP_SHOW, Context.MODE_PRIVATE);
        ImageGridview = (GridView)view.findViewById(R.id.gridview);
        Log.d("onCreateView","onCreateView");
        tabflage="1";
        getPhotos(belongTo);
        Log.d("onCreateView",ScenePhotos.tabflage);
        ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode,belongTo,caseId,listPhoto));

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

        generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));


        ((ProspectInterface)getActivity()).setOnActivityResumeListener(new ProspectInterface.OnActivityResumeListener() {
            @Override
            public void ActivityResumeListener() {
                Log.d("notifyDataSetChanged", "test");
                getPhotos(belongTo);
                Log.d("belongTotao1", belongTo);
                ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode,belongTo,caseId,listPhoto));
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
        BitmapFactory.Options opts = new Options();

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



    private ArrayList getPhotos(String belongTo){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (listPhoto.size() != 0){
            listPhoto.clear();
        }

        getPathData(belongTo);

        return bitmaps;
    }




    private void getPathData(String belongTo){
        ScenePhotoImageAdapter.ScenePhotoImageData scenePhotoImageData = null;

        List<RecordFileInfo> list= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "belongTo = '" + belongTo + "' and caseId = '" + caseId + "'");
        for (int i = 0; i < list.size(); i++){
            scenePhotoImageData=new ScenePhotoImageAdapter.ScenePhotoImageData();
            //sceneBlindPhotoImageData.setScene_photo(BitmapUtils.revitionImageSize(list.get(i).getFilePath()));

            //BitmapFactory.decodeFile(uri.toString());
            scenePhotoImageData.setScene_photo(BitmapFactory.decodeFile(list.get(i).getContractionsFilePath().toString()));
            Log.d("getFilePath",""+list.get(i).getFilePath());
            listPhoto.add(scenePhotoImageData);
        }
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
    private String  getPathDataCount(String path){
        ScenePhotoImageData scenePhotoImageData;
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
    public void onResume() {
        super.onResume();
        getPhotos(belongTo);
        Log.d("belongTotao1", belongTo);
        ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode, belongTo, caseId, listPhoto));
        adapter.notifyDataSetChanged();
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

                adapter.notifyDataSetChanged();

                break;
            case R.id.general_tab:
                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                tabflage="1";
                belongTo="general";
                getPhotos(belongTo);
                ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode, belongTo, caseId, listPhoto));
                adapter.notifyDataSetChanged();

                break;

            case R.id.key_tab:

                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                belongTo="key";
                getPhotos(belongTo);
                ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode, belongTo, caseId, listPhoto));
                adapter.notifyDataSetChanged();

                break;
            case R.id.detail_tab:
                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                other_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                belongTo="detail";
                getPhotos(belongTo);
                ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode, belongTo, caseId, listPhoto));
                adapter.notifyDataSetChanged();
                break;

            case R.id.other_tab:

                all_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                detail_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                other_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                belongTo="other";
                getPhotos(belongTo);
                ImageGridview.setAdapter(adapter = new ScenePhotoImageAdapter(mode, belongTo, caseId, listPhoto));
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
                belongTo="general";

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
                belongTo="key";
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
                belongTo="detail";

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
                belongTo="other";
                break;
        }
    }
}
