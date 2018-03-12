package com.gofirst.scenecollection.evidence.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.FloatWindowView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gofirst.scenecollection.evidence.view.customview.FloatWindowView.direction;
import static com.gofirst.scenecollection.evidence.view.customview.FloatWindowView.mCurrentOrientation;
import static com.gofirst.scenecollection.evidence.view.customview.FloatWindowView.rotationtemp;

public class FloatWindowService extends Service {

    private final String TAG = "FloatWindowService";

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private FloatWindowView mFloatView;
    public static volatile boolean isCameraPreview = false;
    private String showContext = "";
    private String directionTemp = "";
    private SharePre sharePre;
    private Bitmap bitmaptemp;
    private byte[] buffer = null;
    private byte[] buffer2 = null;
    private byte[] buffer3 = null;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private boolean isEvidence;
    private EvidenceExtra evidenceExtra;
    private String path;//存储路径
    private String pathTemp, pathTemp1, pathTemp2, pathTemp3;
    private String belongTo = "";
    private String father;
    private String showLation = "0";//0：左上方，1：右上方，2：左下方，3：右下方
    private String pictureName = "IMG_2016.jpg";
    private boolean addRec;

    private String cameraType;
    private String caseId;

    private List<String> imagecache = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        initWindowParams();
        initView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isCameraPreview = true;
        mWindowManager.addView(mFloatView, wmParams);
        setListener();
        caseId = intent.getStringExtra("caseId");
        if (intent.getStringExtra("belongTo") != null) {
            belongTo = intent.getStringExtra("belongTo");
        }
        if (belongTo != null && belongTo.equals("unclass")) {
            belongTo = "";
        }
        if (intent.getStringExtra("father") != null) {
            father = intent.getStringExtra("father");
        }
        isEvidence = intent.getBooleanExtra("isEvidence", false);
        if (isEvidence) {
            String id = intent.getStringExtra("id");
            evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        }
        String v = intent.getStringExtra("cameraType");
        cameraType = v == null ? "" : v;

        addRec = intent.getBooleanExtra(BaseView.ADDREC, false);
        Intent getPhoto = new Intent("android.media.action.STILL_IMAGE_CAMERA");
        getPhoto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(getPhoto);
        new Thread() {
            public void run() {
                while (true) {
                    if (!isCameraPreview)
                        break;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                unSetListener();
                if (mFloatView != null)
                    mWindowManager.removeView(mFloatView);
//                mFloatView = null;
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        mFloatView = new FloatWindowView(getApplicationContext());
        mFloatView.setParams(wmParams);
    }

    /**
     * 初始化 LayoutParams
     */
    private void initWindowParams() {
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.y = -150;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ContentObserver photoObserver = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Cursor c = getContentResolver().query(uri, null, null, null, null);
            if (c.moveToFirst()) {
                String filepath = c.getString(c.getColumnIndex("_data"));
                File imageifle = new File(filepath);
                if (filepath.contains("Pre-loaded"))
                    return;
                if (imageifle.exists() && imageifle.length() > 0) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd")
                            .format(new Date());
                    if (!imagecache.contains(filepath)) {
                        imagecache.add(filepath);
                        bitmaptemp = BitmapFactory.decodeFile(imageifle.getAbsolutePath());
                        saveImage(filepath, timeStamp + "/" + caseId + "/originalPictures/", timeStamp + "/" + caseId + "/twoHundredPictures/", timeStamp + "/" + caseId + "/contractionPictures/");
                    }
                }
            }
        }
    };

    //
    private void show(String key, String value) {
        showContext = "";
        if (key.equals("SHOW_GATHERER") && value.equals("1")) {
            showContext = sharePre.getString("prospectPerson", "");
        }
        if (key.equals("SHOW_DIRECTION") && value.equals("1")) {
            showContext = showContext + "  " + directionTemp;
        }
        if (key.equals("SHOW_LOCATION") && value.equals("1")) {
            //  if(EvidenceApplication.latitude!=0.0) {
            showContext = showContext + "  " + EvidenceApplication.latitude + "  " + EvidenceApplication.longitude;
            //   }
        }
    }

    private void saveImage(String originpath, String path, String path2, String path3) {

        Date startDate = new Date(System.currentTimeMillis());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        directionTemp = direction;
        if (rotationtemp.equals("clockwise")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
//            Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap waterMarkBitmap = waterMark(bitmaptemp, 200, 100, 40.0f);
            buffer = Bitmap2Bytes(waterMarkBitmap);
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
            Bitmap waterMarkBitmap2 = waterMark(bitmap2, 60, 20, 15.0f);
            buffer2 = Bitmap2Bytes(waterMarkBitmap2);
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
            buffer3 = Bitmap2Bytes(centerSquareScaleBitmap(bitmaptemp, 80));
            if (buffer3 == null) {
                Log.i("MyPicture", "自定义相机Buffer: null");
            } else {
                try {
                    ExifInterface exif = new ExifInterface(originpath);
                    if (exif.hasThumbnail()) {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(exif.getThumbnail());
                        fos.close();
                    } else {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(buffer3);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileIsExists(pathTemp1) && fileIsExists(pathTemp2) && fileIsExists(pathTemp3)) {
                saveData(pathTemp1, pathTemp2, pathTemp3);
            }
            bitmaptemp.recycle();
            bitmaptemp = null;

        } else if (rotationtemp.equals("counterclockwise")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
            //          Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();


            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap waterMarkBitmap1 = waterMark(bitmaptemp, 200, 100, 40.0f);
            buffer = Bitmap2Bytes(waterMarkBitmap1);
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
//                saveData(pathTemp);
            }
            Bitmap bitmap2 = comp(bitmaptemp, 0);
            Log.d("bitmap 大小", bitmap2.getByteCount() / 1024 + "");
            Bitmap waterMarkBitmap2 = waterMark(bitmap2, 60, 200, 15.0f);
            buffer2 = Bitmap2Bytes(waterMarkBitmap2);
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
            buffer3 = Bitmap2Bytes(centerSquareScaleBitmap(bitmaptemp, 80));
            if (buffer3 == null) {
                Log.i("MyPicture", "自定义相机Buffer: null");
            } else {
                try {
                    ExifInterface exif = new ExifInterface(originpath);
                    if (exif.hasThumbnail()) {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(exif.getThumbnail());
                        fos.close();
                    } else {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(buffer3);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileIsExists(pathTemp1) && fileIsExists(pathTemp2) && fileIsExists(pathTemp3)) {
                saveData(pathTemp1, pathTemp2, pathTemp3);
            }
            bitmaptemp.recycle();
            bitmaptemp = null;
        } else if (rotationtemp.equals("1")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
//            Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();


            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap waterMarkBitmap = waterMark(bitmaptemp, 200, 100, 40.0f);
            buffer = Bitmap2Bytes(waterMarkBitmap);
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
            Bitmap waterMarkBitmap2 = waterMark(bitmap2, 60, 200, 15.0f);
            // 200k 图
            buffer2 = Bitmap2Bytes(waterMarkBitmap2);
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
            // 缩略图
            buffer3 = Bitmap2Bytes(centerSquareScaleBitmap(bitmaptemp, 80));
            if (buffer3 == null) {
                Log.i("MyPicture", "自定义相机Buffer: null");
            } else {
                try {
                    ExifInterface exif = new ExifInterface(originpath);
                    if (exif.hasThumbnail()) {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(exif.getThumbnail());
                        fos.close();
                    } else {
                        FileOutputStream fos = new FileOutputStream(file3);
                        fos.write(buffer3);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileIsExists(pathTemp1) && fileIsExists(pathTemp2) && fileIsExists(pathTemp3)) {
                saveData(pathTemp1, pathTemp2, pathTemp3);
            }
            bitmaptemp.recycle();
            bitmaptemp = null;

        }
        Date endDate = new Date(System.currentTimeMillis());
        long diff = endDate.getTime() - startDate.getTime();
        Log.d("diff", "" + diff);

    }

    public void saveData(String path, String path2, String path3) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss")
                .format(new Date());
        String uuid = ViewUtil.getUUid();
        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);
        recordFileInfo.setPictureType(cameraType);
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(path);

        recordFileInfo.setTwoHundredFilePath(path2);
        recordFileInfo.setContractionsFilePath(path3);
        recordFileInfo.setSaveTime(timeStamp);

        recordFileInfo.setPhotoType(belongTo);//
        recordFileInfo.setAddRec(addRec);
        recordFileInfo.setPhotoTypeName(getBelongToName(belongTo));
        recordFileInfo.setFileType("png");
        recordFileInfo.
                setType("0");
        //recordFileInfo.setPictureaName(pictureName);//
        recordFileInfo.setPhotoName(pictureName);
        String photoId = "";
        photoId = ViewUtil.getUUid();
        recordFileInfo.setPhotoId(photoId);

        recordFileInfo.setAttachmentId(uuid);

        recordFileInfo.setFather(father);
        recordFileInfo.setDirection(directionTemp);
        recordFileInfo.setDescription(directionTemp);
        recordFileInfo.setInvestigationId("");
        recordFileInfo.setMainId("");
        recordFileInfo.setContent("");
        recordFileInfo.setDeleteFlag("");
        recordFileInfo.setRefKeyId(photoId);

        recordFileInfo.setSceneType(father);

        if (isEvidence) {
            //recordFileInfo.setCaseId(evidenceExtra.getCaseId());
            recordFileInfo.setFather(evidenceExtra.getFather());
            recordFileInfo.setChild(evidenceExtra.getId());
            recordFileInfo.setSection(evidenceExtra.getSection());
            recordFileInfo.setPhotoType("");//???
            //recordFileInfo.setPictureType("blind");
            recordFileInfo.setSceneType(evidenceExtra.getFather());

            try {
                JSONObject refKeyId = new JSONObject(evidenceExtra.getJson());
                refKeyId.get("ID");
                recordFileInfo.setRefKeyId(refKeyId.get("ID").toString());
                recordFileInfo.setPhotoId(refKeyId.get("ID").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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

//        Message message = new Message();
//        message.what = 5;
//        handler.sendMessage(message);

        Intent intent = new Intent();
        intent.setAction("update_blind");
        sendBroadcast(intent);
    }

    private File getOutFile(int fileType, String path, String time) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)) {
            Toast.makeText(this, "oh,no, SD卡不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        File mediaStorageDir = new File(AppPathUtil.getDataPath() + "/" + path);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyPictures", "创建图片存储路径目录失败");
                Log.d("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                return null;
            }
        }

        File file = new File(getFilePath(mediaStorageDir, fileType, path, time));
        Log.d("setPictureName", file.getPath());
        // pathTemp = getFilePath(mediaStorageDir, fileType,path);

        //       saveData(pathTemp);
        return file;
    }

    private String getFilePath(File mediaStorageDir, int fileType, String path, String time) {
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE) {
            pictureName = "IMG_" + time + ".jpg";
            filePath += pictureName;
        } else if (fileType == TYPE_FILE_VEDIO) {
            filePath += ("VIDEO_" + time + ".mp4");
        } else {
            return null;
        }
        pathTemp = path + pictureName;
        return filePath;
    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
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

    private Bitmap compressImage(Bitmap image, int length) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 >= length) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//
            if (options < 1)
                break;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 1;//每次都减少1
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);
    }


    //图片按比例大小压缩方法（根据Bitmap图片压缩）
    //flag 0 200K图片设置(2400 * 1600)，flag 1 缩络图片设置8k(200 * 150)，
    private Bitmap comp(Bitmap image, int flag) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 4;//设置缩放比例
       /* newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收*/

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap, 300);//压缩好比例大小后再进行质量压缩
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

        return compressImage(result, 8);
    }

    private Bitmap waterMark(Bitmap photo, int w, int h, float size) {
        getPhotoWaterMark();
        String str = "陶发test水印";
        Bitmap icon;
        //photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
        int width = photo.getWidth(), hight = photo.getHeight();
        System.out.println("宽" + width + "高" + hight);
        icon = Bitmap.createBitmap(width, hight, Bitmap.Config.RGB_565); //建立一个空的BItMap
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
        textPaint.setTextSize(size);//字体大小40.0f/15.0f
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        textPaint.setColor(Color.RED);//采用的颜色
        //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置

        drawShowText(canvas, textPaint, width, hight, showLation, mCurrentOrientation);


        /*canvas.drawText(showContext, 10, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左上角
        canvas.drawText(showContext, 10, hight-20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左下角
        //canvas.drawText(showContext, width-100, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
        //canvas.drawText(showContext, width-100, hight-20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角
        canvas.drawText(showContext, width-260, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
        canvas.drawText(showContext, width-260, hight-20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角*/

        //canvas.drawText(showContext, width - w, hight - h, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return icon;
        //image.setImageBitmap(icon);
    }

    private void drawShowText(Canvas canvas, Paint textPaint, int width, int hight, String value, Boolean mCurrentOrientation) {
        //mCurrentOrientation True  横屏  false 竖屏
        switch (value) {
            case "0":
                if (mCurrentOrientation) {
                    canvas.drawText(showContext, 10, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左上角
                } else {
                    canvas.drawText(showContext, 10, 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左上角
                }
                break;
            case "1":
                if (EvidenceApplication.latitude != 0.0) {
                    if (mCurrentOrientation) {
                        canvas.drawText(showContext, width - 260, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    } else {
                        canvas.drawText(showContext, width - 260, 100, textPaint);//  右上角
                    }

                } else {
                    if (mCurrentOrientation) {
                        canvas.drawText(showContext, width - 100, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    } else {
                        canvas.drawText(showContext, width - 100, 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    }
                }
                break;
            case "2":
                if (mCurrentOrientation) {
                    canvas.drawText(showContext, 10, hight - 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左下角
                } else {
                    canvas.drawText(showContext, 10, hight - 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左下角
                }

                break;
            case "3":
                if (EvidenceApplication.latitude != 0.0) {
                    if (mCurrentOrientation) {
                        canvas.drawText(showContext, width - 260, hight - 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角
                    } else {
                        canvas.drawText(showContext, width - 260, hight - 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角
                    }
                } else {
                    if (mCurrentOrientation) {
                        canvas.drawText(showContext, width - 100, hight - 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角

                    } else {
                        canvas.drawText(showContext, width - 100, hight - 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角

                    }
                }
                break;

        }

    }

    private void getPhotoWaterMark() {
        List<SysAppParamSetting> list =
                EvidenceApplication.db.findAllByWhere(SysAppParamSetting.class, "key = 'photo_controller'");

        if (list.size() > 0) {

            JSONArray result = null;//转换为JSONObject
            JSONObject jsonObjectdata;
            try {
                String temop = list.get(0).getValue().toString();
                result = new JSONArray(temop);
                Log.d("result", result + "'");
                for (int i = 0; i < result.length(); i++) {
                    jsonObjectdata = result.getJSONObject(i);
                    String key = jsonObjectdata.get("key").toString();
                    String value = jsonObjectdata.get("value").toString();
                    String name = jsonObjectdata.get("name").toString();
                    if (key.contains("SHOW") && value.equals("1")) {
                        show(key, value);
                    }
                    if (key.equals("TEXT_DIRECTION")) {
                        showLation = value;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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

    //
    private void setListener() {
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, photoObserver);
    }

    private void unSetListener() {
        getContentResolver().unregisterContentObserver(photoObserver);
    }
}
