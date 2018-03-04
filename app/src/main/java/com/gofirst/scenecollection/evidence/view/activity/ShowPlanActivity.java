package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gftxcky.draw.ExperModeActivity;
import com.gftxcky.draw.ExperModeCanvas;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.adapter.ShowBlindActivityAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ShowPhotoActivityAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/17.
 */
public class ShowPlanActivity extends Activity implements View.OnClickListener {
    private ViewPager showViewPager;
    private ShowPhotoActivityAdapter adapter;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int position = 1;
    private ViewTreeObserver viewTreeObserver;
    private int window_width, window_height;// 控件宽度
    private DragImageView dragImageView;// 自定义控件
    private int state_height;// 状态栏的高度

    final int RIGHT = 0;
    final int LEFT = 1;
    private GestureDetector gestureDetector;

    private Bitmap imgMarker;
    private int width, height;   //图片的高度和宽带
    private Bitmap imgTemp;  //临时标记图
    int h;
    int w;
    LinearLayout ll;
    RelativeLayout dragImageViewRelativeLayout;
    String path = "/Pictures/MyPictures";
    private ViewPager photoPager;
    private List<File> mapFiles = new ArrayList<>();
    private String caseId;
    private List<RecordFileInfo> mRecordFileLists = new ArrayList<>();
    private int mCurrentPosition = 0;
    private ShowBlindActivityAdapter mPagerAdapter = null;
    private ImageView mDeleteBtn;
    private String mode;
    private String father;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_photo_activity);


        position = getIntent().getIntExtra("position", 0);
        caseId = getIntent().getStringExtra("caseId");
        mode = getIntent().getStringExtra("mode");
        father = getIntent().getStringExtra("father");
        String filepath = getIntent().getStringExtra("filepath");
        Log.d("position1", "" + position);
        File planfile = new File(filepath + ".plan");
        if (planfile.exists()) {
            Intent intent = new Intent(this, ExperModeActivity.class);
            intent.putExtra("caseId", caseId);
            intent.putExtra("father", father);
            intent.putExtra("ID", caseId);
            intent.putExtra("gatherpath", planfile.getAbsolutePath());
            startActivity(intent);
            finish();
        }

//        dragImageViewRelativeLayout=(RelativeLayout)findViewById(R.id.dragImageView_relativeLayout);
        mDeleteBtn = (ImageView) findViewById(R.id.scene_plan_photo_remove_img);
        if (mode == null || !mode.equals(BaseView.VIEW)) {
            mDeleteBtn.setVisibility(View.VISIBLE);
            mDeleteBtn.setOnClickListener(this);
        }
        photoPager = (ViewPager) findViewById(R.id.photo_pager);

        getsceneFiles();
        photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowPlanActivity.this));
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("positiontest1", "" + position);

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                Log.d("positiontest", "" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        photoPager.setCurrentItem(position);
        if (ScenePhotos.tabflage.equals("0")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("1")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("2")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("3")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("4")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("5")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("6")) {
            path = "/xckydb/" + caseId;
        }
        //       getPhotos(path);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_plan_photo_remove_img:
                removeScenePlanBitmap();
                break;
        }
    }

    private List<File> getsceneFiles() {
        mRecordFileLists = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "father = '" + getIntent().getStringExtra("father") + "' and caseId = \"" + caseId + "\"");

        if (ScenePhotos.tabflage.equals("0")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("1")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("2")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("3")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("4")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("5")) {
            path = "/xckydb/" + caseId;
        } else if (ScenePhotos.tabflage.equals("6")) {
            path = "/xckydb/" + caseId;
        }
        if (mapFiles.size() != 0) {
            mapFiles.clear();
        }
        File file = new File(Environment.getExternalStorageDirectory().getPath() + path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(".png")) {
                    mapFiles.add(files[i]);
                }
            }
        }
        return mapFiles;
    }


    private void getPhotos(String path) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory().getPath() + path);
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(".png")) {
                if (position == i) {
                    Bitmap bit = BitmapFactory.decodeFile(files[position].getPath());
                    h = bit.getHeight();
                    w = bit.getWidth();
//                    showBigPhoto.setImageBitmap(bit);
                }
            }
        }


    }


    public Bitmap doodle(Bitmap src, Bitmap watermark, int x, int y) {
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


    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    private void removeScenePlanBitmap() {
        File f = mapFiles.get(mCurrentPosition);
        int size = mapFiles.size();
        if (f != null && f.exists() && f.isFile()) {
            EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + mRecordFileLists.get(0).getFather() + mRecordFileLists.get(0).getAttachmentId() + "picData'");
            EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + mRecordFileLists.get(0).getFather() + mRecordFileLists.get(0).getAttachmentId() + "recData'");
            EvidenceApplication.db.deleteByWhere(RecordFileInfo.class, "caseId = \"" + caseId + "\" and filePath = \"" +
                    f.getPath().substring(20) + "\"");
            f.delete();
            if (size == 1) {
                finish();
            }
            mapFiles.remove(mCurrentPosition);
            if (mCurrentPosition + 1 == size) {
                mCurrentPosition -= 1;
            }
            photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowPlanActivity.this));
            photoPager.setCurrentItem(mCurrentPosition);
        } else {
            Toast.makeText(this, "查无此文件，请确认！", Toast.LENGTH_SHORT).show();
        }
    }

}
