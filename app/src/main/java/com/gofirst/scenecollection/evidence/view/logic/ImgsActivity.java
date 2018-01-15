package com.gofirst.scenecollection.evidence.view.logic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtil;
import com.gofirst.scenecollection.evidence.utils.FileUtils;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.MainActivity;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.logic.ImgsAdapter.OnItemClickClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ImgsActivity extends Activity implements OnClickListener {

    Bundle bundle;
    FileTraversal fileTraversal;
    GridView imgGridView;
    ImgsAdapter imgsAdapter;
    LinearLayout select_layout;
    Util util;
    RelativeLayout relativeLayout2;
    HashMap<Integer, ImageView> hashImage;
    Button choise_button;
    private byte[] buffer = null;
    private byte[] buffer2 = null;
    private byte[] buffer3 = null;
    ArrayList<String> filelist;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private String pictureName = "IMG_2016.jpg";

    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
    private String pathTemp, pathTemp1, pathTemp2, pathTemp3;
    private Bitmap bitmaptemp;
    private String caseId, father;
    private static final int MSG_SUCCESS = 0;//获取图片成功的标识
    private int pre = 50;
    private int count = 0;
    private boolean addRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.photogrally);
        InitData();
        imgGridView = (GridView) findViewById(R.id.gridView1);
        bundle = getIntent().getExtras();
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC, false);
        fileTraversal = bundle.getParcelable("data");
        //imgsAdapter=new ImgsAdapter(this, fileTraversal.filecontent,onItemClickClass);
        imgGridView.setAdapter(new ImgsAdapter(this, fileTraversal.filecontent, new ImgsAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                //String filapath=fileTraversal.filecontent.get(position);

                if (position >= 0) {
                    filelist.add(fileTraversal.filecontent.get(position));
                } else {
                    for (int i = 0; i < filelist.size(); i++) {
                        if (filelist.get(i).equals(fileTraversal.filecontent.get(-position)))
                            filelist.remove(fileTraversal.filecontent.get(-position));
                    }
                }


            }
        }));
        select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        choise_button = (Button) findViewById(R.id.button3);
        hashImage = new HashMap<Integer, ImageView>();
        filelist = new ArrayList<String>();
        //imgGridView.setOnItemClickListener(this);
        util = new Util(this);
    }

    private void InitData() {
        secondary_back_img = (ImageView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.GONE);
        secondary_title_tv.setText("选择相册");
        secondary_right_tv.setVisibility(View.VISIBLE);
        secondary_right_tv.setText("完成");
        secondary_right_tv.setOnClickListener(this);
        caseId = getIntent().getStringExtra("caseId");
        if (getIntent().getStringExtra("father") != null) {
            father = getIntent().getStringExtra("father");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondary_right_tv:
                if (filelist.size() > 0) {
                    Utils.startProgressDialog(ImgsActivity.this, "5", "正在处理图片", true, false);
                    //Utils.updateDialogProgress(50);
                    new myThread().start();
                } else {
                    Toast.makeText(ImgsActivity.this, "请选择添加的照片", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 在这里可以进行UI操作
                    count += 1;
                    int progess = (count * 100) / filelist.size();
                    Utils.updateDialogProgress(progess);
                    if (count == filelist.size()) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                    break;
                case MSG_SUCCESS:
                    //Utils.updateDialogProgress(count);
                    count += 1;
                    if (count == filelist.size()) {
                        finish();
                    }

                    //mImageView.setImageBitmap((Bitmap) msg.obj);//imageview显示从网络获取到的logo
                    //Toast.makeText(getApplication(), getApplication().getString(R.string.get_pic_success), Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };

    // Thread是一个类，必须继承
    public class myThread extends Thread {
        @Override
        public void run() {
            super.run();
            // 写子线程中的操作
            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            for (int i = 0; i < filelist.size(); i++) {
                bitmaptemp = BitmapUtil.getDiskBitmap(filelist.get(i));
                saveImage(timeStamp + "/" + caseId + "/originalPictures/", timeStamp + "/" + caseId + "/twoHundredPictures/", timeStamp + "/" + caseId + "/contractionPictures/");
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message); // 将Message对象发送出去
                //handler.obtainMessage(MSG_SUCCESS,i).sendToTarget();//获取图片成功，向ui线程发送MSG_SUCCESS标识和bitmap对象
            }
        }
    }

    class BottomImgIcon implements OnItemClickListener {

        int index;

        public BottomImgIcon(int index) {
            this.index = index;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

        }
    }

    @SuppressLint("NewApi")
    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        LinearLayout.LayoutParams params = new LayoutParams(relativeLayout2.getMeasuredHeight() - 10, relativeLayout2.getMeasuredHeight() - 10);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 100;
        //imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
            select_layout.removeView(arg0);
            choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            filelist.remove(filepath);
        }
    }

    ImgsAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            String filapath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                select_layout.removeView(hashImage.get(Position));
                filelist.remove(filapath);
                choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            } else {
                try {
                    checkBox.setChecked(true);
                    Log.i("img", "img choise position->" + Position);
                    ImageView imageView = iconImage(filapath, Position, checkBox);
                    if (imageView != null) {
                        hashImage.put(Position, imageView);
                        filelist.add(filapath);
                        select_layout.addView(imageView);
                        choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void tobreak(View view) {
        finish();
    }

    /**
     * FIXME
     * 亲只需要在这个方法把选中的文档目录已list的形式传过去即可
     *
     * @param view
     */
    public void sendfiles(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("files", filelist);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    private void saveImage(String path, String path2, String path3) {

        Date startDate = new Date(System.currentTimeMillis());

        String name = ViewUtil.getUUid();
        File file = getOutFile(TYPE_FILE_IMAGE, path, name);
        pathTemp1 = pathTemp;
        if (file == null) {
            return;
        }
        Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
//            Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();


        File file2 = getOutFile(TYPE_FILE_IMAGE, path2, name);
        pathTemp2 = pathTemp;
        if (file2 == null) {
            return;
        }

        File file3 = getOutFile(TYPE_FILE_IMAGE, path3, name);
        pathTemp3 = pathTemp;
        if (file3 == null) {
            return;
        }
        //Bitmap waterMarkBitmap=waterMark(bitmaptemp,200,100,40.0f);
        buffer = Bitmap2Bytes(bitmaptemp);
        if (buffer == null) {
            Log.i("MyPicture", "自定义相机Buffer: null");
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Bitmap bitmap2 = comp(bitmaptemp, 0);
        //Bitmap waterMarkBitmap2=waterMark(bitmap2,60,20,15.0f);
        buffer2 = Bitmap2Bytes(bitmap2);
        if (buffer2 == null) {
            Log.i("MyPicture", "自定义相机Buffer: null");
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file2);
                fos.write(buffer2);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //buffer3 = Bitmap2Bytes(comp(bitmap2, 1));
        buffer3 = Bitmap2Bytes(centerSquareScaleBitmap(bitmaptemp, 400));
        if (buffer3 == null) {
            Log.i("MyPicture", "自定义相机Buffer: null");
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file3);
                fos.write(buffer3);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileIsExists(pathTemp1) && fileIsExists(pathTemp2) && fileIsExists(pathTemp3)) {
            saveData(pathTemp1, pathTemp2, pathTemp3);
        }
        bitmaptemp.recycle();
        bitmaptemp = null;


        Date endDate = new Date(System.currentTimeMillis());
        long diff = endDate.getTime() - startDate.getTime();
        Log.d("diff", "" + diff);

    }


    public void saveData(String path, String path2, String path3) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss")
                .format(new Date());
        String uuid = ViewUtil.getUUid();
        Log.d("setPictureName1", path);
        Log.d("setPictureName1", path2);
        Log.d("setPictureName1", path3);
        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);
        recordFileInfo.setPictureType("blind");
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(path);

        recordFileInfo.setTwoHundredFilePath(path2);
        recordFileInfo.setContractionsFilePath(path3);
        recordFileInfo.setSaveTime(timeStamp);

        recordFileInfo.setPhotoType("");//???
        recordFileInfo.setAddRec(addRec);
        recordFileInfo.setPhotoTypeName(getBelongToName(""));
        recordFileInfo.setFileType("png");
        recordFileInfo.setType("0");
        //recordFileInfo.setPictureaName(pictureName);//???
        recordFileInfo.setPhotoName(pictureName);
        String photoId = "";
        photoId = ViewUtil.getUUid();
        recordFileInfo.setPhotoId(photoId);

        recordFileInfo.setAttachmentId(uuid);

        recordFileInfo.setFather(father);

        recordFileInfo.setInvestigationId("");
        recordFileInfo.setMainId("");
        recordFileInfo.setContent("");
        recordFileInfo.setDeleteFlag("");
        recordFileInfo.setRefKeyId(photoId);

        recordFileInfo.setSceneType(father);

        EvidenceApplication.db.save(recordFileInfo);
        pathTemp = "";
        pathTemp2 = "";
        pathTemp3 = "";

      /*  PicturesData picturesData =new PicturesData();
        picturesData.setSCENE_TYPE("SCENE_BLIND_SHOOT");
        picturesData.setATTACHMENT_ID(uuid);
        picturesData.setMAIN_ID(UUID.randomUUID().toString().replace("-",""));
        EvidenceApplication.db.save(picturesData);
*/

       /* Message message = new Message();
        message.what = 5;
        handler.sendMessage(message);*/
    }


    private String getBelongToName(String belongTo) {
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class,
                "parentKey = 'XCZPZLDM'");
        String belongToName = "";
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDictKey().equals(belongTo)) {
                    belongToName = list.get(i).getDictValue1();
                }
            }
        }
        if (belongTo.equals("")) {
            belongToName = "";
        }
        return belongToName;
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }


    public boolean fileIsExists(String path) {
        try {
            File f = new File(AppPathUtil.getDataPath() + "/" + path);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //图片按比例大小压缩方法（根据Bitmap图片压缩）
    //flag 0 200K图片设置，flag 1 缩络图片设置，
    private Bitmap comp(Bitmap image, int flag) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        /*if (baos.toByteArray().length / 1024 > length) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }*/
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        //newOpts.inPreferredConfig = Bitmap.Config.RGB_565;

        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 0f;//这里设置高度为800f
        float ww = 0f;//这里设置宽度为480f
        if (flag == 0) {
            //1080,720比较清晰
            hh = 1280f;//这里设置高度为800f
            ww = 720f;//这里设置宽度为480f
        } else if (flag == 1) {
            hh = 480f;//这里设置高度为800f
            ww = 320f;//这里设置宽度为480f
        }


        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
       /* newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收*/

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        //return compressImage(bitmap, length);//压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    //生成输出文件
    private File getOutFile(int fileType, String path, String name) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)) {
            Toast.makeText(ImgsActivity.this, "oh,no, SD卡不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        File mediaStorageDir = new File(AppPathUtil.getDataPath() + "/" + path);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File file = new File(getFilePath(mediaStorageDir, fileType, name, path));
        //Log.d("setPictureName", getFilePath(mediaStorageDir, fileType,name));
        //pathTemp = getFilePath(mediaStorageDir, fileType,name,path);

        //       saveData(pathTemp);

        return file;
    }

    //生成输出文件路径
    private String getFilePath(File mediaStorageDir, int fileType, String name, String path) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE) {
            pictureName = "IMG_" + name + ".jpg";
            filePath += pictureName;
        } else if (fileType == TYPE_FILE_VEDIO) {
            filePath += ("VIDEO_" + name + ".mp4");
        } else {
            return null;
        }
        pathTemp = path + pictureName;
        return filePath;
    }

}
