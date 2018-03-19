package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Window;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.yunmai.android.engine.OcrEngine;
import com.yunmai.android.util.Utils;
import com.yunmai.android.vo.IDCard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/15.
 */
public class PhotoDialogActivity extends Activity {
    private static final String TAG = PhotoDialogActivity.class.getSimpleName();
    public static final int CAMERA = 1;
    public static final int CROP_PHOTO = 2;
    private String caseId;
    private String father;
    private String saveKey;
    private String section;
    private Uri imageUri;
    private String strImgPath = "";//照片保存路径
    private File imageFile = null;//照片文件
    private final int IMAGE_MAX_WIDTH = 720;
    private final int IMAGE_MAX_HEIGHT = 1280;
    private String path = "";
    String fileName = "";
    public static final int UPDATE_TEXT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        caseId = getIntent().getStringExtra("caseId");
        father = getIntent().getStringExtra("father");
        saveKey = getIntent().getStringExtra("name");
        section = getIntent().getStringExtra("section");
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);*/
        // 创建File对象，用于存储拍照后的图片


        String timeStamp = new SimpleDateFormat("yyyyMMdd")
                .format(new Date());
        new DateFormat();
        path = timeStamp + "/" + caseId + "/identityCard";
        Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        strImgPath = AppPathUtil.getDataPath() + "/" + path + "/";
        fileName = section + ".jpg";// 照片以格式化日期方式命名
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, fileName);
        strImgPath = strImgPath + fileName;// 该照片的绝对路径
        Uri uri = Uri.fromFile(out);
        getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);//根据uri保存照片
        getPhoto.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//保存照片的质量
        startActivityForResult(getPhoto, CAMERA);//启动相机拍照
    }

    @Override
    public void onResume() {
        super.onResume();

    }



/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA && resultCode == Activity.RESULT_OK && null != data){
            String sdState= Environment.getExternalStorageState();
            if(!sdState.equals(Environment.MEDIA_MOUNTED)){
                //GameLog.log(Tag, "sd card unmount");
                return;
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            new DateFormat();
            //String name= DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
            String name= "/"+section+".jpg";
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap)bundle.get("data");
            FileOutputStream fout = null;
            String path="";
            path=timeStamp+"/"+caseId+"/identityCard";
            File file = new File(AppPathUtil.getDataPath()+"/"+timeStamp+"/"+caseId+"/identityCard");

            file.mkdirs();
            String filename=file.getPath()+name;
            try {
                fout = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally{
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //显示图片
            //saveData(filename);
            saveData(path,name);
            Intent intent = new Intent();
            intent.setAction("ic");
            sendBroadcast(intent);
            finish();
        }else{//data:null
            finish();
        }

    }*/


    /**
     * 返回照片结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            imageFile = new File(strImgPath);
            int scale = 0;
            scale = getZoomScale(imageFile);//得到缩放倍数

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;

            new Thread() {
                @Override
                public void run() {
                    byte[] picData = Utils.JPEG2Bytes(strImgPath);
                    OcrEngine ocr = new OcrEngine();
                    IDCard idCard = ocr.recognize(PhotoDialogActivity.this, picData, null, "", "");
                    Log.v(TAG, idCard.toString());
                    Intent intent = new Intent();
                    intent.setAction("id_card_info");
                    intent.putExtra("name", idCard.getName());
                    intent.putExtra("card_num", idCard.getCardNo());
                    intent.putExtra("gender", idCard.getSex());
                    intent.putExtra("address", idCard.getAddress());
                    intent.putExtra("birthday", idCard.getBirth());
                    sendBroadcast(intent);
                }
            }.start();
            saveData(path, fileName);

            //     finish();
            // photoImageView.setImageBitmap(BitmapFactory.decodeFile(strImgPath,options));//按指定options显示图片防止OOM
        } else {
            finish();
            //Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    // 在这里可以进行UI操作
//                    Intent intent = new Intent();
//                    intent.setAction("ic");
//                    sendBroadcast(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 图片缩放处理
     *
     * @param imageFile 照片文件
     * @return 缩放的倍数
     */
    private int getZoomScale(File imageFile) {
        int scale = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strImgPath, options);
        while (options.outWidth / scale >= IMAGE_MAX_WIDTH
                || options.outHeight / scale >= IMAGE_MAX_HEIGHT) {
            scale *= 4;
        }
        return scale;
    }

    public void saveData(String path, String name) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());
        String uuid = ViewUtil.getUUid();

        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);

        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(path + "/" + name);
        recordFileInfo.setTwoHundredFilePath(path + "/" + name);
        recordFileInfo.setSection(section);

        recordFileInfo.setSaveTime(timeStamp);
        recordFileInfo.setChild(saveKey);
        recordFileInfo.setFileType("png");
        recordFileInfo.setAttachmentId(uuid);
        recordFileInfo.setType("0");
        recordFileInfo.setSceneType(father);
        recordFileInfo.setAddRec(getIntent().getBooleanExtra(BaseView.ADDREC, false));
        recordFileInfo.setFather(father);
        recordFileInfo.setInvestigationId("");
        recordFileInfo.setMainId("");
        recordFileInfo.setContent("");
        recordFileInfo.setDeleteFlag("");
        recordFileInfo.setRefKeyId(ViewUtil.getUUid());
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId
                + "' and father = '" + father + "' and child = '" + saveKey + "' and section = '" + section + "'");
        if (list.size() > 0) {

            EvidenceApplication.db.update(recordFileInfo, "id='" + list.get(0).getId() + "'");
        } else {
            EvidenceApplication.db.save(recordFileInfo);
        }

        Message message = new Message();
        message.what = UPDATE_TEXT;
        handler.sendMessage(message); // 将Message对象发送出去

    }


}

