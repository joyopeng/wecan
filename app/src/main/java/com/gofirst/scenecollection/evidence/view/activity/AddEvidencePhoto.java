package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.FileUtils;
import com.gofirst.scenecollection.evidence.view.adapter.AddEvidencePhotoAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */
public class AddEvidencePhoto extends  Activity implements View.OnClickListener {
    private final int CAMERA_WITH_DATA = 2;
    private String filePath;
    private String fileName;
    private String contractionFilePath;
    private String twoHundredFilePath;

    private GridView imageGridView;
    private AddEvidencePhotoAdapter adapter;
    private List<AddEvidencePhotoAdapter.AddEvidencePhotoData> listPhoto = new ArrayList<AddEvidencePhotoAdapter.AddEvidencePhotoData>();
    private String caseId;
    private List<Integer> listPosition=new ArrayList<>();
    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
    private Button getpicture;
    private String father;
    private String templateId;
    private String id;
    private EvidenceExtra evidenceExtra;
    private boolean addRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_evidence_photo);
        //EvidenceApplication.addActivity(this);
        Init();
        father = getIntent().getStringExtra("father");
        caseId = getIntent().getStringExtra("caseId");
        templateId = getIntent().getStringExtra("templateId");
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC,false);
        id= getIntent().getStringExtra("id");
        getPhotos();
        imageGridView.setAdapter(adapter = new AddEvidencePhotoAdapter(AddEvidencePhoto.this,id,father,caseId,templateId, listPhoto,
                new AddEvidencePhotoAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {

                        if (position > 0) {
                            listPosition.add(position);
                        } else {
                            for (int i = 0; i < listPosition.size(); i++) {
                                if (listPosition.get(i) == -position)
                                    listPosition.remove(i);
                            }

                        }
                        removeData();
                        getPicture();
                        //adapter.chiceState(position);
                        // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();

                    }
                },addRec));

    }

    private void Init(){
        secondary_back_img = (ImageView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.VISIBLE);
        secondary_back_img.setOnClickListener(this);
        secondary_title_tv.setText("选择照片");
        secondary_right_tv.setVisibility(View.GONE);
        secondary_right_tv.setText("完成");
        imageGridView = (GridView) findViewById(R.id.Image_gridView);
        getpicture = (Button)findViewById(R.id.get_picture);
        getpicture.setVisibility(View.GONE);
        getpicture.setOnClickListener(this);
    }


    private ArrayList getPhotos(){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (listPhoto.size() != 0){
            listPhoto.clear();
        }

        getPathData();
        return bitmaps;
    }



    private void getPathData(){
        AddEvidencePhotoAdapter.AddEvidencePhotoData addEvidencePhotoData;

        List<RecordFileInfo> list= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "caseId = '" + caseId + "' and fileType = 'png' and father ='SCENE_PHOTO'");
        Log.d("blindsize", "" + list.size());
        for (int i = 0; i < list.size(); i++){
            addEvidencePhotoData=new AddEvidencePhotoAdapter.AddEvidencePhotoData();
            //sceneBlindPhotoImageData.setScene_photo(BitmapUtils.revitionImageSize(list.get(i).getFilePath()));

            //BitmapFactory.decodeFile(uri.toString());
            addEvidencePhotoData.setScene_photo(BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + list.get(i).getContractionsFilePath()));
            Log.d("getFilePath", "" + list.get(i).getFilePath());
            listPhoto.add(addEvidencePhotoData);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_picture:
                removeData();
                getPicture();
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }

    private void getPicture() {
        if(listPosition.size()>0) {
            List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                    "caseId = '" + caseId + "' and fileType = 'png' and father ='SCENE_PHOTO'");

            //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());
            if (evidenceExtra == null) {
                evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
            }
            for (int i = 0; i < listPosition.size(); i++) {
                String id = ViewUtil.getUUid();
                list.get(listPosition.get(i) - 1).setId(id);
                list.get(listPosition.get(i) - 1).setSection(evidenceExtra.getSection());
                list.get(listPosition.get(i) - 1).setChild(evidenceExtra.getId());
                list.get(listPosition.get(i) - 1).setFather(evidenceExtra.getFather());
                list.get(listPosition.get(i) - 1).setIsMarked("");
                list.get(listPosition.get(i) - 1).setPictureType("");
                list.get(listPosition.get(i) - 1).setAttachmentId(id);
                list.get(listPosition.get(i) - 1).setSceneType(evidenceExtra.getFather());
                list.get(listPosition.get(i) - 1).setAttachmentId(ViewUtil.getUUid());

                try {
                    JSONObject refKeyId=new JSONObject(evidenceExtra.getJson());
                    refKeyId.get("ID");
                    list.get(listPosition.get(i) - 1).setRefKeyId(refKeyId.get("ID").toString());
                    list.get(listPosition.get(i) - 1).setPhotoId(refKeyId.get("ID").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                EvidenceApplication.db.save(list.get(listPosition.get(i) - 1));
            }
            finish();
        }else{
            Toast.makeText(AddEvidencePhoto.this,"请选择照片",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                if(resultCode != 0) {
                    saveImageToFile();
                    removeData();
                    saveData();
                    intent.setAction("update_blind");
                    sendBroadcast(intent);
                    intent = new Intent();
                    intent.putExtra("filePath", twoHundredFilePath);
                    intent.putExtra("fileName", fileName);
                }
                setResult(AddEvidence.REQUEST_TAKE_PHOTO, intent);
                finish();
                break;
            default:
                break;
        }
    }

    private void saveImageToFile() {
        byte[] buffer = null;
        filePath = adapter.getFilePath();
        fileName = adapter.getFileName();

        Bitmap originalBitmap = BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + filePath + fileName);
        if (originalBitmap == null) {
            return;
        }

        contractionFilePath = filePath.replace("originalPictures", "contractionPictures");
        File contractionFile = FileUtils.makeFilePath(AppPathUtil.getDataPath() + "/" + contractionFilePath, fileName);
        Bitmap contractionBitmap = BitmapUtils.centerSquareScaleBitmap(originalBitmap, 80);
        if (contractionBitmap != null) {
            buffer = BitmapUtils.Bitmap2Bytes(contractionBitmap);
            if (buffer != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(contractionFile);
                    fos.write(buffer);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        twoHundredFilePath = filePath.replace("originalPictures", "twoHundredPictures");
        buffer = null;
        File twoHundredFile = FileUtils.makeFilePath(AppPathUtil.getDataPath() + "/" + twoHundredFilePath, fileName);
        Bitmap towHundredBitmap = BitmapUtils.comp(originalBitmap, 0);
        if (towHundredBitmap != null) {
            buffer = BitmapUtils.Bitmap2Bytes(towHundredBitmap);
            if (buffer != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(twoHundredFile);
                    fos.write(buffer);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (originalBitmap != null) {
            originalBitmap.recycle();
            originalBitmap = null;
        }
    }

    private void removeData() {
        if (evidenceExtra == null) {
            evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        }
        EvidenceApplication.db.deleteByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "'");
    }

    public void saveData() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(new Date());
        String uuid = ViewUtil.getUUid();
        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);
        recordFileInfo.setPictureType("");
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(filePath + fileName);

        recordFileInfo.setTwoHundredFilePath(twoHundredFilePath + fileName);
        recordFileInfo.setContractionsFilePath(contractionFilePath + fileName);
        recordFileInfo.setSaveTime(timeStamp);

        recordFileInfo.setPhotoType("");//???
        recordFileInfo.setAddRec(addRec);
        recordFileInfo.setPhotoTypeName("");
        recordFileInfo.setFileType("png");
        recordFileInfo.setType("0");
        //recordFileInfo.setPictureaName(pictureName);//???
        recordFileInfo.setPhotoName(fileName);
        String photoId = "";
        photoId = ViewUtil.getUUid();
        recordFileInfo.setPhotoId(photoId);

        recordFileInfo.setAttachmentId(uuid);

        recordFileInfo.setFather(father);
        recordFileInfo.setDirection("");
        recordFileInfo.setDescription("");
        recordFileInfo.setInvestigationId("");
        recordFileInfo.setMainId("");
        recordFileInfo.setContent("");
        recordFileInfo.setDeleteFlag("");
        recordFileInfo.setRefKeyId(photoId);

        recordFileInfo.setSceneType(father);

        if (evidenceExtra == null) {
            evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        }
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

        EvidenceApplication.db.save(recordFileInfo);
    }

}
