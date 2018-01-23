package com.gofirst.scenecollection.evidence.view.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.RoundImageView;
import com.gofirst.scenecollection.evidence.view.customview.ToggleButton;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class CameraActivity extends Activity implements OnClickListener, SensorEventListener {
    private com.gofirst.scenecollection.evidence.view.customview.RoundImageView btn_camera_capture = null;


    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;
    private byte[] buffer = null;
    private byte[] buffer2 = null;
    private byte[] buffer3 = null;

    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    private OrientationEventListener mOrEventListener; // 设备方向监听器
    private Boolean mCurrentOrientation=true; // 当前设备方向 横屏竖屏true,竖屏false

    public static final int UPDATE_TEXT = 1;
    public static final int EVIDENCE_EXTRA = 2;
    private TextView blind_picture, general_picture, key_picture, detail_picture, other_picture;
    //private String picture_flage="1";
    private String cameraType;
    private String caseId;
    private LinearLayout select_text;
    private String pictureName = "IMG_2016.jpg";
    private Bitmap bitmaptemp;
    private String phoneName;
    private String rotationtemp = "clockwise";
    private boolean isEvidence;
    private EvidenceExtra evidenceExtra;
    private TimeCount time;
    private String path;//存储路径
    private String pathTemp, pathTemp1, pathTemp2, pathTemp3;
    Context context;
    private String belongTo = "";
    private String father;
    // 定义真机的Sensor管理器
    private SensorManager mSensorManager;
    // 记录指南针图片转过的角度
    private float currentDegree = 0f;
    private String direction = "正东";
    private String directionTemp = "";
    private String showContext="";
    private TextView show;
    /**
     * 调整Zoom用的seekbar
     */
    private SeekBar mSeekBar;
    private int cameraCount, maxZoom;
    private int zoomCount = 0;
    private int zoomCountTemp = 0;
    private SharePre sharePre;
    private String showLation="0";//0：左上方，1：右上方，2：左下方，3：右下方
    private boolean addRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //请求窗口特性：无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //添加窗口特性：全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_activity);
        EvidenceApplication.addActivity(this);
        sharePre = new SharePre(CameraActivity.this, "user_info", Context.MODE_PRIVATE);
        // 获取真机的传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        phoneName = android.os.Build.MANUFACTURER;
        time = new TimeCount(200, 1000);//构造CountDownTimer对象
        Log.d("phoneName", phoneName);
        String v = getIntent().getStringExtra("cameraType");
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC,false);
        ((ToggleButton)findViewById(R.id.flash_btn)).setListener(new ToggleButton.TriggerListener() {
            @Override
            public void onTrigger(boolean isFlashOn) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(isFlashOn ? Camera.Parameters.FLASH_MODE_TORCH :Camera.Parameters.FLASH_MODE_OFF);// 开启
                camera.setParameters(parameters);
                camera.startPreview();
            }
        });
        cameraType = v == null ? "" : v;
        if (getIntent().getStringExtra("caseId") != null) {
            caseId = getIntent().getStringExtra("caseId");
        }
        caseId = getIntent().getStringExtra("caseId");
        if (getIntent().getStringExtra("belongTo") != null) {
            belongTo = getIntent().getStringExtra("belongTo");
        }
        if (belongTo != null && belongTo.equals("unclass")) {
            belongTo = "";
        }
        if (getIntent().getStringExtra("father") != null) {
            father = getIntent().getStringExtra("father");
        }
        //if(getIntent().getStringExtra("isEvidence")!=null) {
        isEvidence = getIntent().getBooleanExtra("isEvidence", false);
        // }

        if (isEvidence) {
            String id = getIntent().getStringExtra("id");
            evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        }
        Init();
        startOrientationChangeListener(); // 启动设备方向监听器，横竖屏切换
        btn_camera_capture = (RoundImageView) findViewById(R.id.camera_capture);
        btn_camera_capture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (mCurrentOrientation) {
                    //time.start();
                btn_camera_capture.setEnabled(false);
                //btn_camera_capture.setImageResource(R.drawable.radio_unchecked1);
                btn_camera_capture.setBackgroundResource(R.drawable.radio_unchecked1);
                    camera.takePicture(mShutterCallback, null, pictureCallback);
            }
        });


    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
    {
        public void onShutter() {

        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
        camera = null;
        mSensorManager.unregisterListener(CameraActivity.this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            camera = getCameraInstance();
        }
        //必须放在onResume中，不然会出现Home键之后，再回到该APP，黑屏
        mySurfaceView = new MySurfaceView(CameraActivity.this, camera);
        //设置屏幕旋转90度
        camera.setDisplayOrientation(90);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mySurfaceView);
        preview.setOnTouchListener(new ImageTouchListener());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Camera.Parameters mParameters = camera.getParameters();
                maxZoom = mParameters.getMaxZoom();
                //int a=(int) (progress * 1.0f / (maxZoom * 100) * maxZoom);
                // int zoom=(int) (progress * maxZoom/100 );
                zoomCount = (int) (progress * maxZoom / 100);
                mParameters.setZoom(zoomCount);
                camera.setParameters(mParameters);


            }
        });


        /*int a;
        camera.getParameters().getZoom();*/

       /* preview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("scroll1", scrollX + "");
                Log.d("scroll2", scrollY + "");
                Log.d("Scroll3", oldScrollX + "");
                Log.d("Scroll4", oldScrollY + "");

                    Camera.Parameters mParameters = camera.getParameters();
                    mParameters.setZoom(80);
                    camera.setParameters(mParameters);

            }
        });*/


       /* preview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Camera.Parameters mParameters = camera.getParameters();
                mParameters.setZoom(80);
                camera.setParameters(mParameters);
            }
        });
*/


        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(CameraActivity.this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /*得到一相机对象*/
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = camera.open();
            camera.getParameters();
//            List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
//            Camera.Parameters mParameters = camera.getParameters();
//            mParameters.setPictureSize(5120, 3840);
//            mParameters.setPictureSize(3264, 1840);
//            camera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    private PictureCallback pictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                Log.d("MyPicture", "picture taken data: null");
            } else {
                Log.d("MyPicture", "picture taken data: " + data.length);
            }
//            if(phoneName.contains("samsung")){
            if (rotationtemp.equals("clockwise")) {//顺时针
                Log.d("sanxing", "yes");

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //Matrix matrix = new Matrix();
                //matrix.setRotate(180, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                //bitmaptemp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                //              bitmap.recycle();
                //             bitmap=null;
                bitmaptemp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                Canvas canvas = new Canvas(bitmaptemp);
                //主要以这个对象调用旋转方法
                Matrix matrix = new Matrix();
                //以图片中心作为旋转中心，旋转180°
                matrix.setRotate(180, bitmaptemp.getWidth() / 2, bitmaptemp.getHeight() / 2);
                Paint paint = new Paint();
                //设置抗锯齿,防止过多的失真
                paint.setAntiAlias(true);
                canvas.drawBitmap(bitmap, matrix, paint);

                //bitmaptemp = BitmapFactory.decodeByteArray(data, 0, data.length);

                // 将Message对象发送出去

            } else if (rotationtemp.equals("counterclockwise")) {
                Log.d("sanxing", "no");
                buffer = new byte[data.length];
                buffer2 = new byte[data.length];
                buffer3 = new byte[data.length];
                bitmaptemp = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            else if (rotationtemp.equals("1")) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //Matrix matrix = new Matrix();
                //matrix.setRotate(180, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                //bitmaptemp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                //              bitmap.recycle();
                //             bitmap=null;

                bitmaptemp=rotateBitmapByDegree(bitmap,90);
               /* bitmaptemp = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());
                Canvas canvas = new Canvas(bitmaptemp);
                //主要以这个对象调用旋转方法
                Matrix matrix = new Matrix();
                //以图片中心作为旋转中心，旋转180°
                matrix.setRotate(90, bitmaptemp.getWidth() / 2, bitmaptemp.getHeight() / 2);
                Paint paint = new Paint();
                //设置抗锯齿,防止过多的失真
                paint.setAntiAlias(true);
                canvas.drawBitmap(bitmap, matrix, paint);*/

            }

            Message message = new Message();
            message.what = isEvidence ? EVIDENCE_EXTRA : UPDATE_TEXT;
            handler.sendMessage(message);

               /* Log.d("sanxing","yes");
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                Matrix matrix = new Matrix();
                matrix.setRotate(180,bitmap.getWidth()/2,bitmap.getHeight()/2);
                bitmaptemp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                Message message = new Message();
                message.what = UPDATE_TEXT;
                handler.sendMessage(message); // 将Message对象发送出去*/
 /*           }else{
                Log.d("sanxing","no");
                buffer = new byte[data.length];
                buffer = data.clone();
                Message message = new Message();
                message.what = UPDATE_TEXT;
                handler.sendMessage(message); // 将Message对象发送出去
            }*/
        }
    };


    public  Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }


    //-----------------------保存图片---------------------------------------
    private void saveImageToFile() {

        MyThread myThread = new MyThread();
        new Thread(myThread).start();


    }

    class MyThread extends Thread {
        @Override
        public void run() {
            // 处理具体的逻辑
            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            Log.d("tftest",timeStamp+"");
            saveImage(timeStamp + "/" + caseId + "/originalPictures/", timeStamp + "/" + caseId + "/twoHundredPictures/", timeStamp + "/" + caseId + "/contractionPictures/");
            Log.i("zhangsh", "Run over");
        }
    }

    private void saveImage(String path, String path2, String path3) {

        Date startDate = new Date(System.currentTimeMillis());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        directionTemp = direction;
        if (rotationtemp.equals("clockwise")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
//            Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
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

        } else if (rotationtemp.equals("counterclockwise")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
            //          Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();


            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
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
            Log.d("bitmap 大小",bitmap2.getByteCount()/1024+"");
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
        }else if (rotationtemp.equals("1")) {
            File file = getOutFile(TYPE_FILE_IMAGE, path, timeStamp);
            pathTemp1 = pathTemp;
            if (file == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("MyPicture", "自定义相机图片路径:" + file.getPath());
//            Toast.makeText(CameraActivity.this, "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();


            File file2 = getOutFile(TYPE_FILE_IMAGE, path2, timeStamp);
            pathTemp2 = pathTemp;
            if (file2 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
                return;
            }

            File file3 = getOutFile(TYPE_FILE_IMAGE, path3, timeStamp);
            pathTemp3 = pathTemp;
            if (file3 == null) {
                Toast.makeText(CameraActivity.this, "文件创建失败,请检查SD卡读写权限", Toast.LENGTH_SHORT).show();
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

        }
        Date endDate = new Date(System.currentTimeMillis());
        long diff = endDate.getTime() - startDate.getTime();
        Log.d("diff", "" + diff);

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

    //-----------------------生成Uri---------------------------------------
    //得到输出文件的URI
   /* private Uri getOutFileUri(int fileType) {

        return Uri.fromFile(getOutFile(fileType));
    }
*/
    //生成输出文件
    private File getOutFile(int fileType, String path, String time) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)) {
            Toast.makeText(CameraActivity.this, "oh,no, SD卡不存在", Toast.LENGTH_SHORT).show();
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

    public void saveData(String path, String path2, String path3) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss")
                .format(new Date());
        String uuid = ViewUtil.getUUid();
        Log.d("setPictureName1", path);
        Log.d("setPictureName1", path2);
        Log.d("setPictureName1", path3);
        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);
        recordFileInfo.setPictureType(cameraType);
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(path);

        recordFileInfo.setTwoHundredFilePath(path2);
        recordFileInfo.setContractionsFilePath(path3);
        recordFileInfo.setSaveTime(timeStamp);

        recordFileInfo.setPhotoType(belongTo);//???
        recordFileInfo.setAddRec(addRec);
        recordFileInfo.setPhotoTypeName(getBelongToName(belongTo));
        recordFileInfo.setFileType("png");
        recordFileInfo.
                setType("0");
        //recordFileInfo.setPictureaName(pictureName);//???
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
                JSONObject refKeyId=new JSONObject(evidenceExtra.getJson());
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

        Message message = new Message();
        message.what = 5;
        handler.sendMessage(message);

        Intent intent = new Intent();
        intent.setAction("update_blind");
        sendBroadcast(intent);
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

    private String getBelongTo(String belongTo) {
        String str = "1";
        switch (belongTo) {
            case "general":
                str = "general";
                break;
            case "detai":
                str = "detail";
                break;
            case "key":
                str = "key";
                break;
            case "other":
                str = "other";
                break;
        }
        return str;
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //生成输出文件路径
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

    private final void startOrientationChangeListener() {
        mOrEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)
                        || ((rotation >= 135) && (rotation <= 225))) {// portrait
                    mCurrentOrientation = false;
                    // Log.i("CameraActivity", "竖屏");
                    btn_camera_capture.setFocusable(false);

                    // Toast.makeText(CameraActivity.this,"请横屏拍摄",Toast.LENGTH_SHORT).show();
                    rotationtemp = "1";
                } else if (((rotation > 45) && (rotation < 135))
                        || ((rotation > 225) && (rotation < 315))) {// landscape rotationtemp
                    Log.d("rotation", "" + rotation);
                    mCurrentOrientation = true;
                    //                   Log.i("CameraActivity", "横屏");
                    btn_camera_capture.setFocusable(true);
                    if (((rotation > 45) && (rotation < 135))) {
                        rotationtemp = "clockwise";
                    } else if (((rotation > 225) && (rotation < 315))) {
                        rotationtemp = "counterclockwise";
                    }
                    Log.d("rotationtemp", rotationtemp);
//;
                }
            }
        };
        mOrEventListener.enable();
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    // 在这里可以进行UI操作
                    saveImageToFile();
                    camera.startPreview();
                    break;

                case EVIDENCE_EXTRA:
                    // 在这里可以进行UI操作
                    saveImageToFile();
                    //saveEvidence(bitmaptemp);
                    camera.startPreview();
                    break;
                case 5:
                    // 在这里可以进行UI操作
                    time.start();
                    btn_camera_capture.setEnabled(true);
                    btn_camera_capture.setBackground(null);
                    break;
                default:
                    break;
            }
        }
    };

    public void Init() {

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);


        select_text = (LinearLayout) findViewById(R.id.select_text);
        if (cameraType.equals("scene")) {
            select_text.setVisibility(View.VISIBLE);

        } else if (cameraType.equals("blind") || cameraType.equals("addevidence")) {
            select_text.setVisibility(View.GONE);
        } else {
            select_text.setVisibility(View.GONE);
        }
        blind_picture = (TextView) findViewById(R.id.blind_picture);
        general_picture = (TextView) findViewById(R.id.general_picture);
        key_picture = (TextView) findViewById(R.id.key_picture);
        detail_picture = (TextView) findViewById(R.id.detail_picture);
        other_picture = (TextView) findViewById(R.id.other_picture);

        blind_picture.setOnClickListener(this);
        general_picture.setOnClickListener(this);
        key_picture.setOnClickListener(this);
        detail_picture.setOnClickListener(this);
        other_picture.setOnClickListener(this);
        if (ScenePhotos.tabflage.equals("1")) {
            general_picture.setTextColor(Color.parseColor("#FFCC33"));
            key_picture.setTextColor(Color.parseColor("#FFFFFF"));
            detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
            other_picture.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (ScenePhotos.tabflage.equals("2")) {
            general_picture.setTextColor(Color.parseColor("#FFFFFF"));
            key_picture.setTextColor(Color.parseColor("#FFCC33"));
            detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
            other_picture.setTextColor(Color.parseColor("#FFFFFF"));

        } else if (ScenePhotos.tabflage.equals("3")) {
            general_picture.setTextColor(Color.parseColor("#FFFFFF"));
            key_picture.setTextColor(Color.parseColor("#FFFFFF"));
            detail_picture.setTextColor(Color.parseColor("#FFCC33"));
            other_picture.setTextColor(Color.parseColor("#FFFFFF"));

        } else if (ScenePhotos.tabflage.equals("4")) {
            general_picture.setTextColor(Color.parseColor("#FFFFFF"));
            key_picture.setTextColor(Color.parseColor("#FFFFFF"));
            detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
            other_picture.setTextColor(Color.parseColor("#FFCC33"));
        }
        show = (TextView) findViewById(R.id.show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blind_picture:
                blind_picture.setTextColor(Color.parseColor("#FFCC33"));
                general_picture.setTextColor(Color.parseColor("#FFFFFF"));
                key_picture.setTextColor(Color.parseColor("#FFFFFF"));
                detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
                other_picture.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.general_picture:
                blind_picture.setTextColor(Color.parseColor("#FFFFFF"));
                general_picture.setTextColor(Color.parseColor("#FFCC33"));
                key_picture.setTextColor(Color.parseColor("#FFFFFF"));
                detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
                other_picture.setTextColor(Color.parseColor("#FFFFFF"));
                belongTo = "general";
                ScenePhotos.tabflage = "1";

                break;
            case R.id.key_picture:
                blind_picture.setTextColor(Color.parseColor("#FFFFFF"));
                general_picture.setTextColor(Color.parseColor("#FFFFFF"));
                key_picture.setTextColor(Color.parseColor("#FFCC33"));
                detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
                other_picture.setTextColor(Color.parseColor("#FFFFFF"));
                ScenePhotos.tabflage = "2";
                belongTo = "key";
                break;
            case R.id.detail_picture:
                blind_picture.setTextColor(Color.parseColor("#FFFFFF"));
                general_picture.setTextColor(Color.parseColor("#FFFFFF"));
                key_picture.setTextColor(Color.parseColor("#FFFFFF"));
                detail_picture.setTextColor(Color.parseColor("#FFCC33"));
                other_picture.setTextColor(Color.parseColor("#FFFFFF"));
                belongTo = "detail";
                ScenePhotos.tabflage = "3";
                break;
            case R.id.other_picture:
                blind_picture.setTextColor(Color.parseColor("#FFFFFF"));
                general_picture.setTextColor(Color.parseColor("#FFFFFF"));
                key_picture.setTextColor(Color.parseColor("#FFFFFF"));
                detail_picture.setTextColor(Color.parseColor("#FFFFFF"));
                other_picture.setTextColor(Color.parseColor("#FFCC33"));
                belongTo = "other";
                ScenePhotos.tabflage = "4";
                break;
        }
    }

    public void saveEvidence(Bitmap bitmap) {
        File file = new File(AppPathUtil.getDataPath() + "/evidence");
        File filePath;
        if (!file.exists()) {
            Toast.makeText(CameraActivity.this, file.mkdir() ? "文件夹创建成功" : "文件夹创建失败", Toast.LENGTH_SHORT).show();
        }
        try {
            Toast.makeText(CameraActivity.this, "文件保存中....", Toast.LENGTH_SHORT).show();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath = new File(file, System.currentTimeMillis() + ".png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            RecordFileInfo recordFileInfo = new RecordFileInfo();
            recordFileInfo.setId(ViewUtil.getUUid());
            recordFileInfo.setCaseId(evidenceExtra.getCaseId());
            recordFileInfo.setFather(evidenceExtra.getFather());
            recordFileInfo.setChild(evidenceExtra.getId());
            recordFileInfo.setSection(evidenceExtra.getSection());
            recordFileInfo.setFilePath(filePath.toString());
            recordFileInfo.setFileType("png");
            recordFileInfo.setFileDate(new Date());
            EvidenceApplication.db.save(recordFileInfo);
            EvidenceApplication.db.update(evidenceExtra);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
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
        return  BitmapFactory.decodeStream(isBm, null, null);
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

        return compressImage(result,8);
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            btn_camera_capture.setEnabled(true);
            //btn_camera_capture.setImageResource(R.drawable.camera);
            //btn_camera_capture.setBackgroundResource(R.drawable.radio_checked1);
            btn_camera_capture.setBackground(null);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
//			submitbutton.setClickable(false);
            btn_camera_capture.setEnabled(false);
            //btn_camera_capture.setImageResource(R.drawable.radio_unchecked1);
            btn_camera_capture.setBackgroundResource(R.drawable.radio_unchecked1);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 如果真机上触发event的传感器类型为水平传感器类型
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 获取绕Z轴转过的角度
            float degree = event.values[0];
            // 创建旋转动画（反向转过degree度）
            RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            // 设置动画的持续时间
            ra.setDuration(200);
            // 设置动画结束后的保留状态
            ra.setFillAfter(true);
            // 启动动画
            //image.startAnimation(ra);
            currentDegree = -degree;
            //           Log.d("currentDegree0", "" + currentDegree);

            String s = Float.toString(degree);
            s = s.substring(0, s.indexOf('.'));
            int i = Integer.parseInt(s);
            if (rotationtemp.equals("counterclockwise")) {
                i = i + 90;
                if (i >= 360) {
                    i = i - 360;
                }
            } else if (rotationtemp.equals("clockwise")) {
                i = i - 90;
                if (i < 0) {
                    i = Math.abs(i);
                }
            }
            if ((i >= 338 && i <= 360) || (i >= 0 && i < 23)) {
                direction = "由南向北";
                show.setText("由南向北");
            } else if (i >= 23 && i < 68) {
                direction = "由西南向东北";
                show.setText("由西南向东北");
            } else if (i >= 68 && i < 113) {
                direction = "由西向东";
                show.setText("由西向东");
            } else if (i >= 113 && i < 158) {
                direction = "由西北向东南";
                show.setText("由西北向东南");
            } else if (i >= 158 && i < 203) {
                direction = "由北向南";
                show.setText("由北向南");
            } else if (i >= 203 && i < 248) {
                direction = "由东北向西南";
                show.setText("由东北向西南");
            } else if (i >= 248 && i < 293) {
                direction = "由东向西";
                show.setText("由东向西");
            } else if (i >= 293 && i < 338) {
                direction = "由东南向西北";
                show.setText("由东南向西北");
            }
            //           show.setText(s);

        }
    }

    private Bitmap waterMark(Bitmap photo, int w, int h, float size) {
        getPhotoWaterMark();
        String str = "陶发test水印";
        Bitmap icon;
        //photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
        int width = photo.getWidth(), hight = photo.getHeight();
        System.out.println("宽" + width + "高" + hight);
        icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
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

        drawShowText(canvas, textPaint, width, hight, showLation,mCurrentOrientation);


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


    private class ImageTouchListener implements View.OnTouchListener {

        //声明一个坐标点
        private PointF startPoint;
        //声明并实例化一个Matrix来控制图片
        private Matrix matrix = new Matrix();
        //声明并实例化当前图片的Matrix
        private Matrix mCurrentMatrix = new Matrix();


        //缩放时初始的距离
        private float startDistance;
        //拖拉的标记
        private static final int DRAG = 1;
        //缩放的标记
        private static final int ZOOM = 2;
        //标识记录
        private int mode;
        //缩放的中间点
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //System.out.println(ACTION_DOWN);
                    //Log.w(Drag, ACTION_DOWN);
                    //此时处于拖拉方式下
                    mode = DRAG;
                    //获得当前按下点的坐标
                    startPoint = new PointF(event.getX(), event.getY());
                    //把当前图片的Matrix设置为按下图片的Matrix
                    //mCurrentMatrix.set(imageView.getImageMatrix());
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Log.w(Drag, ACTION_MOVE);
                    //根据不同的模式执行相应的缩放或者拖拉操作
                    switch (mode) {
                        case DRAG:
                            //移动的x坐标的距离
                            float dx = event.getX() - startPoint.x;
                            //移动的y坐标的距离
                            float dy = event.getY() - startPoint.y;
                            //设置Matrix当前的matrix
                            matrix.set(mCurrentMatrix);
                            //告诉matrix要移动的x轴和Y轴的距离
                            matrix.postTranslate(dx, dy);
                            break;
                        case ZOOM:
                            //计算缩放的距离
                            float endDistance = distance(event);
                            //计算缩放比率
                            float scale = endDistance / startDistance;


                            //设置当前的Matrix
                            matrix.set(mCurrentMatrix);
                            //设置缩放的参数
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            Log.d("scale", scale + "");

                            Camera.Parameters mParameters1 = camera.getParameters();
                            maxZoom = mParameters1.getMaxZoom();
                            //int a=(int) (progress * 1.0f / (maxZoom * 100) * maxZoom);

                            if (scale > 1) {
                                zoomCount = zoomCount + (int) ((scale - 1) * 2);
                                if (zoomCount < maxZoom) {
                                    mParameters1.setZoom(zoomCount);
                                } else {
                                    mParameters1.setZoom(maxZoom);
                                    zoomCount = maxZoom;
                                }
                                camera.setParameters(mParameters1);
                                //camera.setParameters(mParameters1);
                            } else {
                                zoomCount = zoomCount - (int) ((1 - scale) * 4);
                                if (zoomCount > 0) {
                                    mParameters1.setZoom(zoomCount);
                                } else {
                                    mParameters1.setZoom(0);
                                    zoomCount = 0;
                                }
                                camera.setParameters(mParameters1);
                            }
                            mSeekBar.setProgress((int) (zoomCount * 100 / maxZoom));
                            //int zoom1=(int) (99 );
                            //mParameters1.setZoom(zoomCount);
                            //camera.setParameters(mParameters1);

                            Log.d("zoomCount", zoomCount + "");

                            break;

                    }

                    break;
                //已经有一个手指按住屏幕，再有一个手指按下屏幕就会触发该事件
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Log.w(Drag, ACTION_POINTER_DOWN);
                    //此时为缩放模式
                    mode = ZOOM;
                    //计算开始时两个点的距离
                    startDistance = distance(event);
                    //当两个点的距离大于10时才进行缩放操作
                    if (startDistance > 10) {
                        //计算中间点
                        midPoint = mid(event);
                        //得到进行缩放操作之前，照片的绽放倍数
                        // mCurrentMatrix.set(imageView.getImageMatrix());
                       /* Camera.Parameters mParameters1 = camera.getParameters();
                        maxZoom = mParameters1.getMaxZoom();
                        //int a=(int) (progress * 1.0f / (maxZoom * 100) * maxZoom);
                        int zoom1=(int) (99 );
                        mParameters1.setZoom(zoom1 );
                        camera.setParameters(mParameters1);*/
                    }
                    break;
                //已经有一个手指离开屏幕，还有手指在屏幕上时就会触发该事件
                case MotionEvent.ACTION_POINTER_UP:
                    //Log.w(Drag, ACTION_POINTER_UP);
                    mode = 0;
                    break;
                case MotionEvent.ACTION_UP:
                    //Log.w(Drag, ACTION_UP);
                    mode = 0;
                    break;
                default:
                    break;
            }
            //按照Matrix的要求移动图片到某一个位置
            //imageView.setImageMatrix(matrix);
            //返回true表明我们会消费该动作，不需要父控件进行进一步的处理
            return true;
        }


    }

    public static float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static PointF mid(MotionEvent event) {
        float x = (event.getX(1) - event.getX(0)) / 2;
        float y = (event.getY(1) - event.getY(0)) / 2;
        return new PointF(x, y);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EvidenceApplication.finishAllActivity();
    }

    private void getPhotoWaterMark(){
        List<SysAppParamSetting> list =
                EvidenceApplication.db.findAllByWhere(SysAppParamSetting.class, "key = 'photo_controller'");

        if (list.size()>0){

            JSONArray result = null;//转换为JSONObject
            JSONObject jsonObjectdata;
            try {
                String temop =list.get(0).getValue().toString();
                result = new JSONArray(temop);
                Log.d("result",result+"'");
                for(int i=0;i<result.length();i++){
                    jsonObjectdata = result.getJSONObject(i);
                    String key =jsonObjectdata.get("key").toString();
                    String value =jsonObjectdata.get("value").toString();
                    String name =jsonObjectdata.get("name").toString();
                    if(key.contains("SHOW")&&value.equals("1")) {
                        show(key, value);
                    }
                    if(key.equals("TEXT_DIRECTION")){
                        showLation=value;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void show(String key , String value){
        showContext="";
        if(key.equals("SHOW_GATHERER")&&value.equals("1")){
            showContext=sharePre.getString("prospectPerson","");
        }
        if(key.equals("SHOW_DIRECTION")&&value.equals("1")){
            showContext =showContext+"  "+directionTemp;
        }
        if (key.equals("SHOW_LOCATION")&&value.equals("1")){
          //  if(EvidenceApplication.latitude!=0.0) {
                showContext = showContext + "  " + EvidenceApplication.latitude + "  " + EvidenceApplication.longitude;
         //   }
        }
    }

    private void drawShowText(Canvas canvas,Paint textPaint,int width ,int hight,String value,Boolean mCurrentOrientation ){
        //mCurrentOrientation True  横屏  false 竖屏
        switch(value){
            case "0":
                if(mCurrentOrientation) {
                    canvas.drawText(showContext, 10, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左上角
                }else{
                    canvas.drawText(showContext, 10, 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左上角
                }
                break;
            case "1":
                if(EvidenceApplication.latitude!=0.0) {
                    if(mCurrentOrientation) {
                        canvas.drawText(showContext, width-260, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    }else{
                        canvas.drawText(showContext, width-260, 100, textPaint);//  右上角
                    }

                }else{
                    if(mCurrentOrientation) {
                        canvas.drawText(showContext, width - 100, 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    }else{
                        canvas.drawText(showContext, width - 100, 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右上角
                    }
                }
                break;
            case "2":
                if(mCurrentOrientation) {
                    canvas.drawText(showContext, 10, hight-20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左下角
                }else{
                    canvas.drawText(showContext, 10, hight-100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  左下角
                }

                break;
            case "3":
                if(EvidenceApplication.latitude!=0.0) {
                    if(mCurrentOrientation) {
                        canvas.drawText(showContext, width - 260, hight - 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角
                    }else{
                        canvas.drawText(showContext, width - 260, hight - 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角
                    }
                }else{
                    if(mCurrentOrientation){
                        canvas.drawText(showContext, width - 100, hight - 20, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角

                    }else{
                        canvas.drawText(showContext, width - 100, hight - 100, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制  右下角

                    }
                }
                break;

        }

    }

}