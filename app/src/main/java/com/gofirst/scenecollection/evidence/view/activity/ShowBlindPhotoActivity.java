package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindPhotoImageAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ShowPhotoActivityAdapter;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;
import com.thinkcool.circletextimageview.CircleTextImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/18.
 */
public class ShowBlindPhotoActivity extends Activity implements View.OnClickListener{
    private DragImageView showBigPhoto;
    private ViewPager showViewPager;
    private ShowPhotoActivityAdapter adapter;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int position=0;
    private ViewTreeObserver viewTreeObserver;
    private int window_width, window_height;// 控件宽度
    private DragImageView dragImageView;// 自定义控件
    private int state_height;// 状态栏的高度
    private CircleTextImageView generalPictureCount,keyPictureCount,detailPictureCount,otherPictureCount;

    final int RIGHT = 0;
    final int LEFT = 1;
    private GestureDetector gestureDetector;

    private Bitmap imgMarker;
    private int width,height;   //图片的高度和宽带
    private Bitmap imgTemp;  //临时标记图
    int h;
    int w;
    LinearLayout ll;
    RelativeLayout dragImageViewRelativeLayout;
    String path="/Pictures/MyPictures5";
    private RelativeLayout generalPicture_relativeLayout,keyPicture_relativeLayout,
            detailPicture_relativeLayout,otherPicture_relativeLayout;
    private TextView generalPictureTextview,keyPictureTextview,
            detailPictureTextview,otherPictureTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_blind_photo_activity);
        showBigPhoto=(DragImageView)findViewById(R.id.show_big_photo);
        Intent intent= getIntent();
        position=getIntent().getIntExtra("position",0)-1;
        Log.d("position1", "" + position);

        generalPictureTextview=(TextView)findViewById(R.id.general_picture_text);
        keyPictureTextview=(TextView)findViewById(R.id.key_picture_text);
        detailPictureTextview=(TextView)findViewById(R.id.detail_picture_text);
        otherPictureTextview=(TextView)findViewById(R.id.other_picture_text);



        generalPictureCount=(CircleTextImageView)findViewById(R.id.general_picture_count);
//        generalPictureCount.setText(String.valueOf(SceneBlindPhotos.generalPictureCount));
        keyPictureCount=(CircleTextImageView)findViewById(R.id.key_picture_count);
//        keyPictureCount.setText(String.valueOf(SceneBlindPhotos.keyPictureCount));
        detailPictureCount=(CircleTextImageView)findViewById(R.id.detail_picture_count);
//        detailPictureCount.setText(String.valueOf(SceneBlindPhotos.detailPictureCount));
        otherPictureCount=(CircleTextImageView)findViewById(R.id.other_picture_count);
//        otherPictureCount.setText(String.valueOf(SceneBlindPhotos.otherPictureCount));

        generalPicture_relativeLayout=(RelativeLayout)findViewById(R.id.general_picture);
        keyPicture_relativeLayout=(RelativeLayout)findViewById(R.id.key_picture);
        detailPicture_relativeLayout=(RelativeLayout)findViewById(R.id.detail_picture);
        otherPicture_relativeLayout=(RelativeLayout)findViewById(R.id.other_picture);

//        generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
//        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));

        generalPicture_relativeLayout.setOnClickListener(this);
        keyPicture_relativeLayout.setOnClickListener(this);
        detailPicture_relativeLayout.setOnClickListener(this);
        otherPicture_relativeLayout.setOnClickListener(this);

//        dragImageViewRelativeLayout=(RelativeLayout)findViewById(R.id.dragImageView_relativeLayout);


        if(ScenePhotos.tabflage.equals("0")){
            path="/Pictures/MyPictures";
        }else if(ScenePhotos.tabflage.equals("5")){
            path="/Pictures/MyPictures5";
        } else if(ScenePhotos.tabflage.equals("6")){
            path="/Pictures/MyPictures6";
        } else if(ScenePhotos.tabflage.equals("3")){
            path="/Pictures/MyPictures3";
        } else if(ScenePhotos.tabflage.equals("4")){
            path="/Pictures/MyPictures4";
        }
        getPhotos(path);

        /** 获取可見区域高度 **/
        WindowManager manager = getWindowManager();
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();
        ll = (LinearLayout)findViewById(R.id.bottom_sec);
        showBigPhoto.setmActivity(this);//注入Activity.
        /** 测量状态栏高度 **/
        viewTreeObserver = showBigPhoto.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (state_height == 0) {
                            // 获取状况栏高度
                            Rect frame = new Rect();
                            getWindow().getDecorView()
                                    .getWindowVisibleDisplayFrame(frame);
                            state_height = frame.top;
                            showBigPhoto.setScreen_H(window_height - state_height);
                            showBigPhoto.setScreen_W(window_width);
                        }

                    }
                });

        gestureDetector = new GestureDetector(ShowBlindPhotoActivity.this,onGestureListener);


/*
        ImageView imgApple2 = new ImageView(ShowPhotoActivity.this);
        imgApple2.setBackgroundColor(Color.parseColor("#ffb6b4"));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
        layoutParams.topMargin=8;
        layoutParams.leftMargin=8;
        layoutParams.rightMargin=8;
        layoutParams.bottomMargin=8;

        dragImageViewRelativeLayout.addView(imgApple2,layoutParams);*/



       /* showBigPhoto.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当按下时获取到屏幕中的xy位置
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showBigPhoto.getWidth();
                    showBigPhoto.getHeight();

                    Log.d("showBigPhoto.x", showBigPhoto.getX() + "");
                    Log.d("showBigPhoto.y", showBigPhoto.getY() + "");

                    Log.d("event.x", event.getX() + "");
                    Log.d("event.y", event.getY() + "");

                    Log.d("/.x", event.getX() / showBigPhoto.getWidth() + "");
                    Log.d("/.y", event.getY() / showBigPhoto.getHeight() + "");

                    *//*ImageView tv = new ImageView(ShowPhotoActivity.this);
                    tv.setImageResource(R.drawable.add_mark);
                    ll. addView(tv);*//*
                    ImageView imgApple2 = new ImageView(ShowPhotoActivity.this);
                    imgApple2.setBackgroundColor(Color.parseColor("#ffb6b4"));

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
                   *//* layoutParams.topMargin=8;
                    layoutParams.leftMargin=8;
                    layoutParams.rightMargin=8;
                    layoutParams.bottomMargin=8;*//*
                    layoutParams.topMargin = (int) event.getY();
                    layoutParams.leftMargin = (int) event.getX();
                    ;

                    dragImageViewRelativeLayout.addView(imgApple2, layoutParams);

                }
                return false;
            }
        });
*/

    }




    private void getPhotos(String path){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory().getPath()+path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].getName().contains(".jpg")){
                Log.d("fileslength",""+files.length);
                    Bitmap bit = BitmapFactory.decodeFile(files[position].getPath());
                    Log.d("files123",""+files[0].getPath());
                    h=bit.getHeight();
                    w=bit.getWidth();
                    showBigPhoto.setImageBitmap(bit);
            }
        }


    }



    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    float x = e2.getX() - e1.getX();
                    float y = e2.getY() - e1.getY();

                    if (x > 0) {
                        doResult(RIGHT);
                    } else if (x < 0) {
                        doResult(LEFT);
                    }
                    return true;
                }
            };

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void doResult(int action) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + path);//"/Pictures/MyPictures4"
        File[] files = file.listFiles();
        switch (action) {
            case RIGHT:
                System.out.println("go right");

                if(position <=files.length&position!=0) {
                    position=position-1;
                    getPhotos(path);
                }
                else if(position == 0){
                    getPhotos(path);
                }

                break;

            case LEFT:
                System.out.println("go left");
                Log.d("left","left");

                if(position < files.length) {
                    position=position+1;
                    getPhotos(path);
                }
                else if(position == files.length){
                    getPhotos(path);
                }
                break;

        }
    }


    public Bitmap doodle(Bitmap src, Bitmap watermark,int x,int y)
    {
        // 另外创建一张图片
        Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas canvas = new Canvas(newb);
        canvas.drawBitmap(src, x, y, null);// 在 0，0坐标开始画入原图片src
        canvas.drawBitmap(watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2, null); // 涂鸦图片画到原图片中间位置
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        watermark.recycle();
        watermark = null;

        return newb;
    }


    public  Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){


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

                /*File file1 = new File(Environment.getExternalStorageDirectory().getPath()+path);
                File[] files1 = file1.listFiles();
                files1[position].getPath();
                copyFile(files1[position].getPath(), Environment.getExternalStorageDirectory().getPath() + "/Pictures/MyPictures1/1.jpg");
                SceneBlindPhotos.generalPictureCount=SceneBlindPhotos.generalPictureCount+1;
                generalPictureCount.setText(String.valueOf(SceneBlindPhotos.generalPictureCount));
                files1[position].delete();
                position=position-1;*/

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

                /*File file2 = new File(Environment.getExternalStorageDirectory().getPath()+path);
                File[] files2 = file2.listFiles();
                files2[position].getPath();

                copyFile(files2[position].getPath(), Environment.getExternalStorageDirectory().getPath() + "/Pictures/MyPictures2/1.jpg");
                SceneBlindPhotos.keyPictureCount=SceneBlindPhotos.keyPictureCount+1;
                keyPictureCount.setText(String.valueOf(SceneBlindPhotos.keyPictureCount));

                files2[position].delete();
                position=position-1;*/
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

                /*File file3 = new File(Environment.getExternalStorageDirectory().getPath()+path);
                File[] files3 = file3.listFiles();
                files3[position].getPath();

                copyFile(files3[position].getPath(), Environment.getExternalStorageDirectory().getPath() + "/Pictures/MyPictures3/1.jpg");
                SceneBlindPhotos.detailPictureCount=SceneBlindPhotos.detailPictureCount+1;
                detailPictureCount.setText(String.valueOf(SceneBlindPhotos.detailPictureCount));
                files3[position].delete();
                position=position-1;*/
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

                /*File file4 = new File(Environment.getExternalStorageDirectory().getPath()+path);
                File[] files4 = file4.listFiles();
                files4[position].getPath();

                copyFile(files4[position].getPath(), Environment.getExternalStorageDirectory().getPath() + "/Pictures/MyPictures4/1.jpg");
                SceneBlindPhotos.otherPictureCount=SceneBlindPhotos.otherPictureCount+1;
                otherPictureCount.setText(String.valueOf(SceneBlindPhotos.otherPictureCount));
                files4[position].delete();
                position=position-1;*/
                break;
        }
    }




    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath){
        try {
            int bytesum = 0;
            int byteread = 0;
            Log.d("oldPath",oldPath);
            Log.d("newPath",newPath);

            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                Log.d("exists","exists");
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                    Log.d("while","while");
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    private  int  getPathDataCount(String path){
        SceneBlindPhotoImageAdapter.SceneBlindPhotoImageData sceneBlindPhotoImageData;
        File file = new File(Environment.getExternalStorageDirectory().getPath()+path);
        if (!file.exists()){
            if (!file.mkdirs()){
                Log.i("MyPictures", "mediaStorageDir : " + file.getPath());
            }
        }
        File[] files = file.listFiles();
        Log.d("files.length", "" + files.length);
        return files.length;
        //       return String.valueOf(files.length);
    }



}
