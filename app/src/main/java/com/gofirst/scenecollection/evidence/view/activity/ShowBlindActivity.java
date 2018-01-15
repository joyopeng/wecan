package com.gofirst.scenecollection.evidence.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.PicturesData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.adapter.ShowBlindActivityAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ShowPhotoActivityAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class ShowBlindActivity extends Activity implements View.OnClickListener{
    private DragImageView showBigPhoto;
    private ViewPager showViewPager;
    private ShowPhotoActivityAdapter adapter;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int position=0;
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
    private List<File> mapFiles = new ArrayList<>();
    private int clickPosition;
    //private LinearLayout copy,delete,anchor,edit_show;
    private int count=0;
    private String mode,caseId,father;
    private LinearLayout bottom_sec;
    private TextView description;

    //add zsh on
    private String mCurrentSourceFilePath = "";
    private String mCaseId = "";
    private String mFather = "";
    private LinearLayout belongto_picture,anchor_picture,edit_picture,copy_picture,more_picture;
    //add zsh off
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_blind_activity);
//        showBigPhoto=(DragImageView)findViewById(R.id.show_big_photo);

        Intent intent= getIntent();
        mode=getIntent().getStringExtra("mode");
        caseId=getIntent().getStringExtra("caseId");
        father=getIntent().getStringExtra("father");
        position=getIntent().getIntExtra("position", 0);
        Log.d("position1", "" + position);
        bottom_sec=(LinearLayout)findViewById(R.id.bottom_sec);

        Init();

        photoPager = (ViewPager) findViewById(R.id.photo_pager);
        if (getIntent().getBooleanExtra(BaseView.ADDREC,false))
            findViewById(R.id.anchor_picture).setVisibility(View.GONE);
        getBlindFiles();
        photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles,ShowBlindActivity.this));
        photoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("positiontest1",""+position);
                clickPosition=position;
               /* List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                       "twoHundredFilePath = '"+mapFiles.get(clickPosition)+"'" );*/
                List<RecordFileInfo>list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and father ='"+father+"' and caseId = '"+caseId+"'");
                if(list.size()>0) {
                    if (list.get(clickPosition).getDescription()==null) {
                        description.setText("未分类");
                    } else {
                        if(list.get(clickPosition).getDescription().equals("")){
                            description.setText("未分类");
                        }else{
                            description.setText("未分类，"+list.get(clickPosition).getDescription());
                        }

                    }
                }

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

        if (mode != null && mode.equals(BaseView.VIEW)){

            copy_picture.setEnabled(false);

            more_picture.setEnabled(false);
        }

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

    }


    private List<File> getBlindFiles(){
        mapFiles.clear();
        List<RecordFileInfo>lists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + PublicMsg.belongTo + "' and caseId = '" + caseId + "'and fileType = 'png' and father = '"+father+"'");
        Log.d("shosize",lists.size()+"");
        if(lists.size()==0){
            finish();
        }
        for (RecordFileInfo recordFileInfo : lists){
            //mapFiles.add(new File(recordFileInfo.getFilePath()));AppPathUtil.getDataPath() + "/" +
            mapFiles.add(new File(AppPathUtil.getDataPath() + "/" +recordFileInfo.getTwoHundredFilePath()));

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
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + belongTo + "' and caseId = '" + caseId + "'and fileType = 'png' and father = '"+father+"'");
        return list.size();
    }

    private void setBelongTo(String belongTo){
       /* List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '' and fileType = 'png' and caseId ='"+caseId+"'");*/

        List<RecordFileInfo>list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '' and fileType = 'png' and father = '"+father+"' and caseId ='"+ caseId +"'");

        RecordFileInfo recordFileInfo = list.get(clickPosition);
        recordFileInfo.setPhotoType(belongTo);
        recordFileInfo.setPhotoTypeName(getBelongToName(belongTo));
        EvidenceApplication.db.update(recordFileInfo,
                "id = '" + list.get(clickPosition).getId() + "'");

        mapFiles.clear();
        getBlindFiles();
        photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
        //photoPager.setCurrentItem(clickPosition - 1);

    }




    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()){


            //下面的tab


            case R.id.general_picture:

                Log.d("mapFiles", "" + mapFiles.get(clickPosition).getAbsolutePath());
                setBelongTo("2");

                /*generalPictureCount.setText(String.valueOf(getPathDataCount("general")));*/
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
                otherPictureTextview.setTextColor(Color.parseColor("#000000"));*/

                setBelongTo("3");
                /*keyPictureCount.setText(String.valueOf(getPathDataCount("key")));*/

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
                /*detailPictureCount.setText(String.valueOf(getPathDataCount("detail")));*/

                break;
            case R.id.other_picture:
               /* generalPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                keyPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailPicture_relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                positionPicture_relativeLayout.setBackgroundColor(Color.parseColor("#1E3346"));

                generalPictureTextview.setTextColor(Color.parseColor("#000000"));
                keyPictureTextview.setTextColor(Color.parseColor("#000000"));
                detailPictureTextview.setTextColor(Color.parseColor("#000000"));
                otherPictureTextview.setTextColor(Color.parseColor("#FFFFFF"));*/
                setBelongTo("9");
                /*otherPictureCount.setText(String.valueOf(getPathDataCount("position")));*/

                break;

            case R.id.edit:
                count++;
                if(count%2==1){
                    //edit_show.setVisibility(View.VISIBLE);
                }
                else{//edit_show.setVisibility(View.GONE);
                }
                break;
            /*case R.id.copy:

                List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "twoHundredFilePath = '" + mapFiles.get(clickPosition).getAbsolutePath() + "'");
                list.get(0).setId(UUID.randomUUID().toString());
                EvidenceApplication.db.save(list.get(0));
                mapFiles.add(new File(list.get(0).getTwoHundredFilePath()) );
                photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
                photoPager.setCurrentItem(clickPosition);

                break;*/

            case R.id.delete:
                List<RecordFileInfo> list1 = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "photoType = '' and fileType = 'png' and father = '"+father+"' and caseId ='"+caseId+"'");


                Log.d("deletelength", list1.size() + "");
                //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());

                EvidenceApplication.db.deleteById(RecordFileInfo.class, list1.get(clickPosition).getId());
                mapFiles.clear();
                getBlindFiles();
                photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
                photoPager.setCurrentItem(clickPosition);


                break;

           /* case R.id.anchor:
                List<RecordFileInfo>lists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "photoType = '" + PublicMsg.belongTo + "'");
                Intent intent = new Intent();
                intent.setClass(this,TracePointActivity.class);
                intent.putExtra("dataId",lists.get(position).getId());//lists.get(position).getId());
                intent.putExtra("filepath",mapFiles.get(position).getPath().toString());
                intent.putExtra("caseId",mCaseId);
                intent.putExtra("father",mFather);
                intent.putExtra("mode",mode);
                intent.putExtra("templateId",getIntent().getStringExtra("templateId"));
                startActivity(intent);
                Toast.makeText(ShowBlindActivity.this,"锚点",Toast.LENGTH_SHORT).show();
                break;*/

            case R.id.belongto_picture:
                    showBeLongDialog();
                break;
            //anchor_picture,edit_picture,copy_picture,more_picture
            case R.id.anchor_picture:
                    List<RecordFileInfo>lists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and caseId ='" + caseId + "' and father = '"+father+"'");
                    Intent intent = new Intent();
                    intent.setClass(this, TracePointActivity.class);
                    intent.putExtra("dataId",lists.get(clickPosition).getId());//lists.get(position).getId());
                    intent.putExtra("filepath",mapFiles.get(clickPosition).getPath().toString());
                intent.putExtra("caseId",mCaseId);
                intent.putExtra("father", mFather);
                    intent.putExtra("mode", mode);
                intent.putExtra("templateId", getIntent().getStringExtra("templateId"));
                    startActivity(intent);

                    //Toast.makeText(ShowBlindActivity.this,"锚点",Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_picture:
                    //showEditDialog();
                List<RecordFileInfo>editEists=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and caseId ='" + caseId + "' and father = '"+father+"'");

                Intent intent1 = new Intent();
                intent1.putExtra("position", String.valueOf(clickPosition + 1));
                intent1.putExtra("id", editEists.get(clickPosition).getId());
                intent1.setClass(this, EditPictureActivity.class);
                startActivity(intent1);
                    //Toast.makeText(ShowBlindActivity.this,"编辑",Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_picture:
                    List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                            "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and caseId ='" + caseId + "' and father = '" + father + "'");
                    String id = ViewUtil.getUUid();
                    String photoId= ViewUtil.getUUid();
                    list.get(clickPosition).setId(id);
                    list.get(clickPosition).setAttachmentId(id);
                    list.get(clickPosition).setIsMarked("");
                    list.get(clickPosition).setPhotoId(photoId);
                    list.get(clickPosition).setRefKeyId(photoId);
                EvidenceApplication.db.save(list.get(clickPosition));
                    mapFiles.add(new File(list.get(clickPosition).getTwoHundredFilePath()) );
                    photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
                    photoPager.setCurrentItem(clickPosition);
                break;
            case R.id.more_picture:
                showMoreDialog();
                break;
        }
    }


    Dialog UserNameDialog;

    private void showMoreDialog() {
        TextView left_rotation, right_rotation,delete;
        UserNameDialog = new Dialog(ShowBlindActivity.this, R.style.FullHeightDialog1);
        UserNameDialog.setContentView(R.layout.more_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        left_rotation = (TextView) UserNameDialog.findViewById(R.id.left_rotation);
        right_rotation= (TextView) UserNameDialog.findViewById(R.id.right_rotation);
        delete= (TextView) UserNameDialog.findViewById(R.id.delete);

        UserNameDialog.show();
        left_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
               // List<RecordFileInfo> list1 = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "photoType = '' and fileType = 'png'");
                List<RecordFileInfo>list1=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                        "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and caseId ='"+caseId+"' and father = '"+father+"'");
                Log.d("deletelength", list1.size() + "");
                //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());
                EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + list1.get(clickPosition).getFather() + list1.get(clickPosition).getAttachmentId() + "picData'");
                EvidenceApplication.db.deleteByWhere(DataTemp.class, "father = '" + list1.get(clickPosition).getFather() + list1.get(clickPosition).getAttachmentId() + "recData'");

                EvidenceApplication.db.deleteById(RecordFileInfo.class, list1.get(clickPosition).getId());


                List<PicturesData> listPicture = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                        "photoId = '"+ list1.get(clickPosition).getId()+"'");
                if(listPicture!=null&&listPicture.size()>0){
                    EvidenceApplication.db.deleteById(PicturesData.class,listPicture.get(0).getId());
                }


                mapFiles.clear();
                getBlindFiles();
                photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
                photoPager.setCurrentItem(clickPosition);
                UserNameDialog.dismiss();
            }
        });

    }



    @SuppressLint("WrongViewCast")
    private void showEditDialog() {
        EditText left_rotation, right_rotation,delete;
        UserNameDialog = new Dialog(ShowBlindActivity.this, R.style.BelongToDialog);
        UserNameDialog.setContentView(R.layout.edit_dialog);
        UserNameDialog.setCanceledOnTouchOutside(true);// 点击Dialog外部可以关闭Dialog

        //left_rotation = (TextView) UserNameDialog.findViewById(R.id.left_rotation);
        //right_rotation= (TextView) UserNameDialog.findViewById(R.id.right_rotation);
        delete= (EditText) UserNameDialog.findViewById(R.id.delete);

        UserNameDialog.show();


       /* delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecordFileInfo> list1 = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "photoType = '' and fileType = 'png'");
                Log.d("deletelength", list1.size() + "");
                //                EvidenceApplication.db.deleteById(RecordFileInfo.class,list.get(i).getCaseId());

                EvidenceApplication.db.deleteById(RecordFileInfo.class, list1.get(clickPosition).getId());
                mapFiles.clear();
                getBlindFiles();
                photoPager.setAdapter(new ShowBlindActivityAdapter(mapFiles, ShowBlindActivity.this));
                photoPager.setCurrentItem(clickPosition);
                UserNameDialog.dismiss();
            }
        });*/

    }


    private void showBeLongDialog() {
        TextView general, key,detail,position, other;
        UserNameDialog = new Dialog(ShowBlindActivity.this, R.style.FullHeightDialog1);
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

        description=(TextView)findViewById(R.id.description);

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
    protected void onResume() {
        super.onResume();

        /*List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "id = '"+mapFiles.get(clickPosition)+"'" );*/
        List<RecordFileInfo>list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "photoType = '" + PublicMsg.belongTo + "' and fileType = 'png' and father ='"+father+"' and caseId = '"+caseId+"'");

       /* Intent intent = new Intent();
        intent.setClass(this, TracePointActivity.class);
        intent.putExtra("dataId",list.get(position).getId());//lists.get(position).getId());
        intent.putExtra("filepath", mapFiles.get(position).getPath().toString());*/


        if(list.size()>0) {
            if (list.get(clickPosition).getDescription()==null) {
                description.setText("");
            } else {
                if(list.get(clickPosition).getDescription().equals("")){
                    description.setText("未分类");
                }else{
                    description.setText("未分类，"+list.get(clickPosition).getDescription());
                }
            }
        }




    }



}
