package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.adapter.ShowBlindActivityAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ShowPhotoActivityAdapter;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class ShowPictureActivity extends Activity {
    private DragImageView showBigPhoto;
    private ViewPager showViewPager;
    private ShowPhotoActivityAdapter adapter;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int position=0;
    private ViewTreeObserver viewTreeObserver;
    private int window_width, window_height;// 控件宽度
    //private DragImageView dragImageView;// 自定义控件
    private int state_height;// 状态栏的高度

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
    //String path="/Pictures/MyPictures5";
    private String belongTo;
    private String caseId;



    private ViewPager photoPager;
    private List<File> mapFiles = new ArrayList<>();
    private int clickPosition;
    private int count=0;

    //add zsh on
    private String mCurrentSourceFilePath = "";
    private String mCaseId = "";
    private String mFather = "";
    //add zsh off
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_pictures);
//        showBigPhoto=(DragImageView)findViewById(R.id.show_big_photo);

        Intent intent= getIntent();
        position=getIntent().getIntExtra("position", 0);
        belongTo=getIntent().getStringExtra("belongTo");
        caseId=getIntent().getStringExtra("caseId");
        Log.d("position1", "" + position);
        Log.d("belongTotao",belongTo);



        photoPager = (ViewPager) findViewById(R.id.photo_pager);

        getBlindFiles();
        photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles,ShowPictureActivity.this));
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("positiontest1",""+position);
                clickPosition=position;
            }

            @Override
            public void onPageSelected(int position) {
                position=position;
                Log.d("positiontest",""+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        photoPager.setCurrentItem(position);

    }



    private List<File> getBlindFiles(){
        List<RecordFileInfo>lists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "belongTo = '" + belongTo + "' and caseId = '" + caseId + "'");
        for (RecordFileInfo recordFileInfo : lists){
            //        mapFiles.add(new File(recordFileInfo.getFilePath()));
            mapFiles.add(new File(recordFileInfo.getFilePath()));
            //add zsh on
            if("".equals(mCaseId) || "".equals(mFather)){
                mCaseId = recordFileInfo.getCaseId();
                mFather = recordFileInfo.getFather();
            }
            //add zsh off
        }
        return mapFiles;
    }

    private int  getPathDataCount(String belongTo){
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "belongTo = '" + belongTo + "'");
        return list.size();
    }

    private void setBelongTo(String belongTo){
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "belongTo = 'unclass'");
        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setPhotoType(belongTo);
        EvidenceApplication.db.update(recordFileInfo,
                "id = '" + list.get(clickPosition).getId() + "'");
        mapFiles.clear();
        getBlindFiles();
        photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowPictureActivity.this));
        //photoPager.setCurrentItem(clickPosition - 1);
    }



}
