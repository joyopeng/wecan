package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.adapter.EvidencePhotoAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/6/5.
 */
public class ShowEvidenceExtra extends Activity{

    private ViewPager photoPager;
    private TextView pager_indicator,name;
    private List<RecordFileInfo> dataList;
    private String mode;

    @SuppressWarnings("ALL")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.evidence_photo_explorer);
        photoPager = (ViewPager) findViewById(R.id.photo_pager);
        pager_indicator = (TextView) findViewById(R.id.pager_indicator);
        name = (TextView)findViewById(R.id.name);
        final String id = getIntent().getStringExtra("id");
        mode = getIntent().getStringExtra("mode");
        photoPager.setAdapter(new EvidencePhotoAdapter(dataList = getData(id),ShowEvidenceExtra.this));
        photoPager.setCurrentItem(getIntent().getIntExtra("position", 0), false);
        photoPager.setOffscreenPageLimit(3);
        ImageView edit = (ImageView) findViewById(R.id.scene_direction_photo_edit);
        ImageView delete = (ImageView) findViewById(R.id.scene_direction_photo_remove_img);
        if (BaseView.VIEW.equals(mode))
            delete.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),AddEvidenceEdit.class).putExtra("id",id);
                String templateId = getIntent().getStringExtra("templateId");
                intent.putExtra("templateId",templateId);
                intent.putExtra("mode",mode);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvidenceApplication.db.deleteById(RecordFileInfo.class,dataList.get(photoPager.getCurrentItem()).getId());
                new File(AppPathUtil.getDataPath() + "/" + dataList.get(photoPager.getCurrentItem()).getFilePath()).delete();
                dataList = getData(id);
                if (dataList.size() != 0){
                    photoPager.setAdapter(new EvidencePhotoAdapter(dataList,ShowEvidenceExtra.this));
                }else {
                    finish();
                }
                Toast.makeText(v.getContext(),"删除成功",Toast.LENGTH_SHORT).show();
            }
        });
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pager_indicator.setText("" + (position + 1) + "/" + photoPager.getAdapter().getCount());
                name.setText("物证图片" + (position + 1));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private List<RecordFileInfo> getData(String id){
        EvidenceExtra evidenceExtra = EvidenceApplication.db.findById(id,EvidenceExtra.class);
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"section = '" + evidenceExtra.getSection() + "' and fileType = 'png'", "fileDate desc");
    }
}
