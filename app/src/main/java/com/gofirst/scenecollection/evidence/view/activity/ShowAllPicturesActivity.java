package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.PicturesData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.adapter.ShowAllActivityAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ShowPhotoActivityAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ShowAllPicturesActivity extends Activity implements View.OnClickListener{
    private DragImageView showBigPhoto;
    private ViewPager showViewPager;
    private ShowPhotoActivityAdapter adapter;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int position=0;
    private int listPosition=0;
    private ViewTreeObserver viewTreeObserver;
    private int window_width, window_height;// 控件宽度
    //private DragImageView dragImageView;// 自定义控件
    private int state_height;// 状态栏的高度
    //private CircleTextImageView generalPictureCount,keyPictureCount,detailPictureCount,otherPictureCount;

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
    String path="/Pictures/MyPictures1";
    /*private RelativeLayout generalPicture_relativeLayout,keyPicture_relativeLayout,
            detailPicture_relativeLayout,positionPicture_relativeLayout,edit;
    private TextView generalPictureTextview,keyPictureTextview,
            detailPictureTextview,otherPictureTextview;*/
    private ViewPager photoPager;
    //private List<File> mapFiles = new ArrayList<>();
    private List<Map<String,File>> mapFiles=new ArrayList<>();
    private int clickPosition;
   // private LinearLayout copy,delete,anchor,edit_show;
    private int count=0;
    private String mode,father;
    private LinearLayout bottom_sec;
    private String pictureType[]={"","1","2","3","4","9"};
    //add zsh on
    private String mCurrentSourceFilePath = "";
    private String mCaseId = "";
    private String mFather = "";
    private String caseId;
    private int idSize;
    private String id;
    private int absolutePathSize;
    private String absolutePath;
    private TextView belongto_show;
    private LinearLayout belongto_picture,anchor_picture,edit_picture,copy_picture,more_picture;
    //add zsh off
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_all_activity);
//        showBigPhoto=(DragImageView)findViewById(R.id.show_big_photo);

        Intent intent= getIntent();
        mode=getIntent().getStringExtra("mode");
        father=getIntent().getStringExtra("father");
        position=getIntent().getIntExtra("position", 0);
        listPosition=getIntent().getIntExtra("listPosition", 0);
        caseId=getIntent().getStringExtra("caseId");
        Log.d("position1", "" + position);
        if (getIntent().getBooleanExtra(BaseView.ADDREC,false))
            findViewById(R.id.anchor_picture).setVisibility(View.GONE);
        bottom_sec=(LinearLayout)findViewById(R.id.bottom_sec);
        belongto_show=(TextView)findViewById(R.id.belongto_show);
        photoPager = (ViewPager) findViewById(R.id.photo_pager);

        Init();
        getAllFiles(pictureType);



        photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("positiontest1", "" + position);

                    clickPosition = position;
                idSize=mapFiles.get(clickPosition).keySet().toString().length();
                id=mapFiles.get(clickPosition).keySet().toString().substring(1, idSize - 1);

                absolutePathSize=mapFiles.get(clickPosition).values().toString().length();
                absolutePath=mapFiles.get(clickPosition).values().toString().substring(1, absolutePathSize - 1);
                List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class," id = '" + id + "' ");
                if(list.size()==1) {
                    if (list.get(0).getDescription() == null) {
                        belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()));

                    } else {
                        if(list.get(0).getDescription().equals("")){
                            belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) );

                        }else{
                            belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) + "," + list.get(0).getDescription());
                            String test =belongto_show.getText().toString();
                            Log.d("test",test);
                        }
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
                position = position;
                Log.d("positiontest", "" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(listPosition==0){
            clickPosition = position;
            photoPager.setCurrentItem(position);
        }else{
            for(int i=0;i<listPosition;i++){
                count+=getPathDataCount(pictureType[i]);
            }
            clickPosition = count+position;
            photoPager.setCurrentItem(count+position);
        }


       /* generalPictureTextview=(TextView)findViewById(R.id.general_picture_text);
        keyPictureTextview=(TextView)findViewById(R.id.key_picture_text);
        detailPictureTextview=(TextView)findViewById(R.id.detail_picture_text);
        otherPictureTextview=(TextView)findViewById(R.id.other_picture_text);*/

        //updateCount();


        /*generalPicture_relativeLayout=(RelativeLayout)findViewById(R.id.general_picture);
        keyPicture_relativeLayout=(RelativeLayout)findViewById(R.id.key_picture);
        detailPicture_relativeLayout=(RelativeLayout)findViewById(R.id.detail_picture);
        positionPicture_relativeLayout=(RelativeLayout)findViewById(R.id.other_picture);*/

//        generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
//        generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
        /*edit=(RelativeLayout)findViewById(R.id.edit);
        copy=(LinearLayout)findViewById(R.id.copy);
        delete=(LinearLayout)findViewById(R.id.delete);
        anchor=(LinearLayout)findViewById(R.id.anchor);
        edit_show=(LinearLayout)findViewById(R.id.edit_show);
        edit.setOnClickListener(this);
        copy.setOnClickListener(this);
        delete.setOnClickListener(this);
        anchor.setOnClickListener(this);

        generalPicture_relativeLayout.setOnClickListener(this);
        keyPicture_relativeLayout.setOnClickListener(this);
        detailPicture_relativeLayout.setOnClickListener(this);
        positionPicture_relativeLayout.setOnClickListener(this);*/

        if (mode != null && mode.equals(BaseView.VIEW)){
            /*generalPicture_relativeLayout.setVisibility(View.INVISIBLE);
            keyPicture_relativeLayout.setVisibility(View.INVISIBLE);
            detailPicture_relativeLayout.setVisibility(View.INVISIBLE);
            positionPicture_relativeLayout.setVisibility(View.INVISIBLE);
            copy.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            edit.setBackgroundColor(Color.parseColor("#000000"));*/
            //bottom_sec.setVisibility(View.GONE);
            anchor_picture.setEnabled(false);
            copy_picture.setEnabled(false);
            belongto_picture.setEnabled(false);
            more_picture.setEnabled(false);
            edit_picture.setEnabled(false);
        }

//        dragImageViewRelativeLayout=(RelativeLayout)findViewById(R.id.dragImageView_relativeLayout);
         /*idSize=mapFiles.get(clickPosition).keySet().toString().length();
         id=mapFiles.get(clickPosition).keySet().toString().substring(1, idSize - 1);

        absolutePathSize=mapFiles.get(clickPosition).values().toString().length();
        absolutePath=mapFiles.get(clickPosition).values().toString().substring(1, absolutePathSize - 1);*/

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


        belongtoShow();
    }


    private String getBelongToChina(String text){
        String textTemp="";
        switch (text){
            case "":
                textTemp= "未分类";
                break;
            case "1":
                textTemp= "方位";
            break;
            case "2":
                textTemp= "概貌";
            break;
            case "3":
                textTemp = "重点";
            break;
            case "4":
                textTemp = "细目";
                break;
            case "9":
                textTemp = "其它";
            break;
        }
        return textTemp;
    }

    private List<Map<String,File>> getAllFiles(String pictureType[]){

        int count=0;
        for(int i =0;i<pictureType.length;i++){
            List<RecordFileInfo>lists= EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                    "photoType = '" + pictureType[i] + "' and caseId = '"+ caseId + "' and fileType = 'png' and father = '"+father+"' ");

            if(lists.size()!=0) {

                for (RecordFileInfo recordFileInfo : lists) {
                    //        mapFiles.add(new File(recordFileInfo.getFilePath()));
                    //mapFiles.add(new File(recordFileInfo.getTwoHundredFilePath()));
                    Map<String,File> map=new HashMap<>();
                    map.put(recordFileInfo.getId(),new File(recordFileInfo.getTwoHundredFilePath()));
                    mapFiles.add(map);
                    //add zsh on
                    if ("".equals(mCaseId) || "".equals(mFather)) {
                        mCaseId = recordFileInfo.getCaseId();
                        mFather = recordFileInfo.getFather();
                    }
                    //add zsh off
                }
            }else {
                count++;
            }
            if(count==5){
                finish();
            }
        }
        return mapFiles;
    }

    private int  getPathDataCount(String belongTo){
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + belongTo + "' and caseId = '"+ caseId +"' and fileType = 'png' and father = '"+father+"'");
        return list.size();
    }

    private void setBelongTo(String belongTo) {

        /*List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "belongTo = 'unclass' and caseId = '"+ caseId +"'");*/

        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setPhotoType(belongTo);
        recordFileInfo.setPhotoTypeName(getBelongToName(belongTo));
        EvidenceApplication.db.update(recordFileInfo,
                "id = '" + id + "'");
        updateCount();
        //mapFiles.clear();
        //getAllFiles(pictureType);
        //photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
        if(clickPosition==mapFiles.size()-1){
            finish();
        }else {
            photoPager.setCurrentItem(clickPosition + 1);
        }
    }

    private String getBelongToName(String belongTo){
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class,
                "parentKey = 'XCZPZLDM'");
        String belongToName="";
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getDictKey().equals(belongTo)) {
                    belongToName=list.get(i).getDictValue1();
                }
            }
        }
        return belongToName;
    }




    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){


            //下面的tab

            case R.id.general_picture:

                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/
               // Log.d("mapFiles", "" + mapFiles.get(clickPosition).getAbsolutePath());
                setBelongTo("2");

                //generalPictureCount.setText(String.valueOf(getPathDataCount("general")));
               /* getBlindFiles();
                photoPager.setAdapter(new EvidencePhotoAdapter(mapFiles, ShowBlindActivity.this));
                photoPager.setCurrentItem(clickPosition-1);*/

                break;
            case R.id.key_picture:
                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));
*/
                setBelongTo("3");
                //keyPictureCount.setText(String.valueOf(getPathDataCount("key")));

                break;

            case R.id.detail_picture:
                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/
                setBelongTo("4");
                //detailPictureCount.setText(String.valueOf(getPathDataCount("detail")));

                break;
            case R.id.other_picture:
                /*generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));*/
                setBelongTo("9");
                //otherPictureCount.setText(String.valueOf(getPathDataCount("position")));

                break;

            case R.id.edit:
                /*count++;
                if(count%2==0){
                    edit_show.setVisibility(View.GONE);
                }
                else{edit_show.setVisibility(View.VISIBLE);}*/
                break;
            case R.id.copy:

                /*List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "id = '" + id + "'");
                list.get(0).setId(UUID.randomUUID().toString());
                list.get(0).setBelongTo("unclass");
                EvidenceApplication.db.save(list.get(0));
                Map<String,File> map=new HashMap<>();
                map.put(list.get(0).getId(),new File(list.get(0).getTwoHundredFilePath()));
                mapFiles.add(map);
                photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
                photoPager.setCurrentItem(clickPosition);*/

                break;

            case R.id.delete:
                    EvidenceApplication.db.deleteById(RecordFileInfo.class, id);
                    mapFiles.clear();
                    getAllFiles(pictureType);
                    if(mapFiles.size()==0){
                        finish();
                    }else {
                        photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
                    }
                    /*if(clickPosition>1){
                        photoPager.setCurrentItem(clickPosition);
                    }*/
                    Log.d("clickPosition",clickPosition+"");
                break;

            case R.id.anchor_picture:
                List<RecordFileInfo>lists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "fileType = 'png' and caseId = '"+ caseId +"' and father = '"+father+"'");
                Intent intent = new Intent();
                intent.setClass(this, TracePointActivity.class);
                intent.putExtra("dataId", id);//lists.get(position).getId());
                intent.putExtra("filepath", AppPathUtil.getDataPath()+"/"+absolutePath);
                intent.putExtra("caseId",mCaseId);
                intent.putExtra("father",mFather);
                intent.putExtra("mode",mode);
                intent.putExtra("templateId",getIntent().getStringExtra("templateId"));
                startActivity(intent);
                //Toast.makeText(ShowAllPicturesActivity.this, "锚点", Toast.LENGTH_SHORT).show();
                break;
            case R.id.belongto_picture:
                showBeLongDialog();
                break;
            case R.id.edit_picture:
                List<RecordFileInfo>editEists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "id = '"+id+"'");

                Intent intent1 = new Intent();
                intent1.putExtra("position",String.valueOf(clickPosition + 1));
                intent1.putExtra("id", id);
                intent1.setClass(this, EditPictureActivity.class);
                startActivity(intent1);
                //Toast.makeText(ShowAllPicturesActivity.this,"编辑",Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_picture:
                    List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "id = '" + id + "'");
                    String uuid = ViewUtil.getUUid();
                    String photoId=ViewUtil.getUUid();
                    list.get(0).setId(uuid);
                    list.get(0).setAttachmentId(uuid);
                    list.get(0).setPhotoType("");//unclass
                    list.get(0).setIsMarked("");
                    list.get(0).setPhotoId(photoId);
                    list.get(0).setRefKeyId(photoId);
                    EvidenceApplication.db.save(list.get(0));
                    Map<String,File> map=new HashMap<>();
                    map.put(list.get(0).getId(),new File(list.get(0).getTwoHundredFilePath()));
                    mapFiles.add(map);
                    photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
                    photoPager.setCurrentItem(clickPosition);
                break;
            case R.id.more_picture:
                showMoreDialog();
                break;
        }
    }


    public void updateCount(){
        /*generalPictureCount=(CircleTextImageView)findViewById(R.id.general_picture_count);
        generalPictureCount.setText(String.valueOf(getPathDataCount("general")));
        keyPictureCount=(CircleTextImageView)findViewById(R.id.key_picture_count);
        keyPictureCount.setText(String.valueOf(getPathDataCount("key")));
        detailPictureCount=(CircleTextImageView)findViewById(R.id.detail_picture_count);
        detailPictureCount.setText(String.valueOf(getPathDataCount("detail")));
        otherPictureCount=(CircleTextImageView)findViewById(R.id.other_picture_count);
        otherPictureCount.setText(String.valueOf(getPathDataCount("position")));*/

    }

    private void Init(){
        //anchor_picture,edit_picture,copy_picture,more_picture
        belongto_picture=(LinearLayout)findViewById(R.id.belongto_picture);
        belongto_picture.setOnClickListener(this);

        anchor_picture=(LinearLayout)findViewById(R.id.anchor_picture);
        anchor_picture.setOnClickListener(this);

        edit_picture=(LinearLayout)findViewById(R.id.edit_picture);
        edit_picture.setOnClickListener(this);

        copy_picture=(LinearLayout)findViewById(R.id.copy_picture);
        copy_picture.setOnClickListener(this);

        more_picture=(LinearLayout)findViewById(R.id.more_picture);
        more_picture.setOnClickListener(this);

    }

    Dialog UserNameDialog;

    private void showMoreDialog() {
        TextView left_rotation, right_rotation,delete;
        UserNameDialog = new Dialog(ShowAllPicturesActivity.this, R.style.FullHeightDialog1);
        UserNameDialog.setContentView(R.layout.more_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        left_rotation = (TextView) UserNameDialog.findViewById(R.id.left_rotation);
        right_rotation= (TextView) UserNameDialog.findViewById(R.id.right_rotation);
        delete= (TextView) UserNameDialog.findViewById(R.id.delete);

        UserNameDialog.show();
        left_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecordFileInfo> rotationList=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "id = '" + id + "'");
                if(rotationList.size()>0){
                    //rotationList.get(0).getTwoHundredFilePath().toString();
                    adjustPhotoRotation(BitmapFactory.decodeFile(rotationList.get(0).getTwoHundredFilePath().toString()), 90);
                }
                //adjustPhotoRotation();
                UserNameDialog.dismiss();

            }
        });

        right_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserNameDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordFileInfo recordFileInfo=EvidenceApplication.db.findById(id, RecordFileInfo.class);
                EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "picData'");
                EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "recData'");

                EvidenceApplication.db.deleteById(RecordFileInfo.class, id);
                List<PicturesData> listPicture = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                        "photoId = '"+ id+"'");
                if(listPicture!=null&&listPicture.size()>0){
                    EvidenceApplication.db.deleteById(PicturesData.class,listPicture.get(0).getId());
                }

                mapFiles.clear();
                getAllFiles(pictureType);
                if(mapFiles.size()==0){
                    finish();
                }else {
                    photoPager.setAdapter(new ShowAllActivityAdapter(mapFiles, ShowAllPicturesActivity.this));
                }

                UserNameDialog.dismiss();
            }
        });

    }

    private void showBeLongDialog() {
        TextView general, key,detail,position,other;
        UserNameDialog = new Dialog(ShowAllPicturesActivity.this, R.style.FullHeightDialog1);
        UserNameDialog.setContentView(R.layout.belongto_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        general = (TextView) UserNameDialog.findViewById(R.id.generalPicture_relativeLayout);
        key= (TextView) UserNameDialog.findViewById(R.id.keyPicture_relativeLayout);
        detail= (TextView) UserNameDialog.findViewById(R.id.detailPicture_relativeLayout);
        position= (TextView) UserNameDialog.findViewById(R.id.positionPicture_relativeLayout);
        other= (TextView) UserNameDialog.findViewById(R.id.otherPicture_relativeLayout);

        UserNameDialog.show();
        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBelongTo("2");
                UserNameDialog.dismiss();
            }
        });

        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBelongTo("3");
                UserNameDialog.dismiss();
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBelongTo("4");
                UserNameDialog.dismiss();
            }
        });

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBelongTo("1");
                UserNameDialog.dismiss();
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBelongTo("9");
                UserNameDialog.dismiss();
            }
        });
    }



    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
    {

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

    @Override
    protected void onResume() {
        super.onResume();
        //belongtoShow();
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class," id = '" + id + "' ");
        if(list.size()==1) {
            if (list.get(0).getDescription() == null) {
                belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()));

            } else {
                if(list.get(0).getDescription().equals("")){
                    belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) );

                }else{
                    belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) + "," + list.get(0).getDescription());
                    String test =belongto_show.getText().toString();
                    Log.d("test",test);
                }
            }
        }



    }

    private void belongtoShow(){
        //clickPosition = position;
        idSize=mapFiles.get(clickPosition).keySet().toString().length();
        id=mapFiles.get(clickPosition).keySet().toString().substring(1, idSize - 1);

        absolutePathSize=mapFiles.get(clickPosition).values().toString().length();
        absolutePath=mapFiles.get(clickPosition).values().toString().substring(1, absolutePathSize - 1);
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class," id = '" + id + "' ");
        if(list.size()==1) {
            if (list.get(0).getDescription() == null) {
                belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()));

            } else {
                if(list.get(0).getDescription().equals("")){
                    belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) );

                }else{
                    belongto_show.setText(getBelongToChina(list.get(0).getPhotoType().toString()) + "," + list.get(0).getDescription());
                    String test =belongto_show.getText().toString();
                    Log.d("test",test);
                }
            }
        }
    }





}
