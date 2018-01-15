package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.Text;

import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public class EditPictureActivity extends Activity implements View.OnClickListener{
    private ImageView imageview;
    Bundle data;
    private Bitmap bitmaps[];
    private String position;
    private String pictureId;
    private String caseId="";
    private String father="";
    private String name="";
    private String section="";

    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
    private TextView serial_number;
    private TextView picture_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.edit_picture_activity);

        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        pictureId= intent.getStringExtra("id");
        Log.d("pictureId",pictureId+"");
        Init();

    }

    private void Init(){

        secondary_back_img = (ImageView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.VISIBLE);
        secondary_title_tv.setText("编辑现场照片");
        secondary_right_tv.setVisibility(View.GONE);
        secondary_back_img.setOnClickListener(this);

        serial_number=(TextView)findViewById(R.id.serial_number);
        serial_number.setText(position);
        picture_edit=(TextView)findViewById(R.id.picture_edit);
        picture_edit.setOnClickListener(this);
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "id = '"+pictureId+"'");
        if(list.size()>0) {
            if (list.get(0).getDescription()==null||list.get(0).getDescription().equals("")) {
                picture_edit.setText("点击输入");
            } else {
                picture_edit.setText(list.get(0).getDescription());
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secondary_back_img:
                updateData();
                finish();
                break;

            case R.id.picture_edit:
                Text text=new Text(EditPictureActivity.this);
                text.clickPop(EditPictureActivity.this, picture_edit, "编辑现场照片");
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            updateData();
            finish();
        }

        return false;
    }

    private void updateData(){
        RecordFileInfo recordFileInfo=new RecordFileInfo();
            if(picture_edit.getText().toString().equals("点击输入")){
                recordFileInfo.setDescription("");
            }else {
                recordFileInfo.setDescription(picture_edit.getText().toString());
            }
        EvidenceApplication.db.update(recordFileInfo,"id = '"+pictureId+"'");
    }



}

