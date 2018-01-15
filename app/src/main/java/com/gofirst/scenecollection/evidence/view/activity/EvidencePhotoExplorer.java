package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.adapter.EvidencePhotoAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxiran on 2016/3/14.
 */
public class EvidencePhotoExplorer extends Activity implements View.OnClickListener{


    private ViewPager photoPager;
    private TextView pager_indicator, name;
    private ImageView mRemovewImg;
    private int mCurrentPostion = -1;
    private List<RecordFileInfo> mFiles = new ArrayList<>();
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.evidence_photo_explorer);
        photoPager = (ViewPager) findViewById(R.id.photo_pager);
        pager_indicator = (TextView) findViewById(R.id.pager_indicator);
        name = (TextView) findViewById(R.id.name);
        mRemovewImg = (ImageView)findViewById(R.id.scene_direction_photo_remove_img);
        mRemovewImg.setOnClickListener(this);
        mFiles = getMapFiles();
        photoPager.setAdapter(new EvidencePhotoAdapter(mFiles, EvidencePhotoExplorer.this));
        photoPager.setOffscreenPageLimit(3);
        mCurrentPostion = getIntent().getIntExtra("position", 0);
        mode = getIntent().getStringExtra("mode");
        if(mode != null && BaseView.VIEW.equals(mode)){
            mRemovewImg.setVisibility(View.GONE);
        }
        photoPager.setCurrentItem(mCurrentPostion);
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pager_indicator.setText("" + (position + 1) + "/" + photoPager.getAdapter().getCount());
                name.setText("地图截图" + (position + 1));
                mCurrentPostion = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scene_direction_photo_remove_img:
                removeDirectionFile();
                break;
            default:
                break;
        }
    }

    private List<RecordFileInfo> getMapFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "father = '" + getIntent().getStringExtra("father") + "' and caseId = \"" + getIntent().getStringExtra("caseId") + "\"", "fileDate desc");
    }

    private void removeDirectionFile(){
        RecordFileInfo info = mFiles.get(mCurrentPostion);
        File file = new File(AppPathUtil.getDataPath() + "/" + info.getFilePath());
        if(file != null && file.exists() && file.isFile()){
            EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + info.getFather() + info.getAttachmentId() + "picData'");
            EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + info.getFather() + info.getAttachmentId() + "recData'");
            EvidenceApplication.db.deleteByWhere(RecordFileInfo.class,"id = \"" + info.getId() + "\"");
            file.delete();
            int size = mFiles.size();
            if(size == 1){
                finish();
            }
            mFiles.remove(mCurrentPostion);
            if(mCurrentPostion + 1 == size){
                mCurrentPostion -= 1;
            }
            photoPager.setAdapter(new EvidencePhotoAdapter(mFiles, EvidencePhotoExplorer.this));
            photoPager.setCurrentItem(mCurrentPostion);
        }else{
            Toast.makeText(this,"查无此文件，请确认！",Toast.LENGTH_SHORT).show();
        }
    }
}
