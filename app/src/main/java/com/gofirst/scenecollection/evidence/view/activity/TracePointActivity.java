package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SceneFileConjunction;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.utils.support.AddFileDialog;
import com.gofirst.scenecollection.evidence.utils.support.Bubble;
import com.gofirst.scenecollection.evidence.utils.support.CircleShape;
import com.gofirst.scenecollection.evidence.utils.support.ImageMap;
import com.gofirst.scenecollection.evidence.utils.support.SavePointDetails;
import com.gofirst.scenecollection.evidence.utils.support.SelectEvidenceDialog;
import com.gofirst.scenecollection.evidence.utils.support.Shape;
import com.gofirst.scenecollection.evidence.utils.support.TouchImageView;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/7/9.
 */
public class TracePointActivity extends Activity implements View.OnClickListener,
        TouchImageView.onTouchAddPosition{

    /**
     * 新需求更改
     * update:20161102
     * */
    private View mLayoutShowIdentification;
    private ImageView mLayoutShowIdentificationBp;
    private View mLayoutAddIdentification;
    private ImageView mImgAddIdentification;
    private View mBtnLayoutAddScan;
    private View mBtnLayoutAddPhoto;
    private View mBtnLayoutAddRecord;
    private View mBtnLayoutAddText;
    private View mBtnLayoutDelete;

    private final int SHAPE_STATE_HIDE = 100;
    private final int SHAPE_STATE_SHOW = 101;
    private final int SHAPE_STATE_ADD = 102;
    private final int SHAPE_STATE_EDIT = 103;
    private int mCurrentState = SHAPE_STATE_SHOW;
    private String mEvidenceDialogValues = "[{\"key\":\"SCENE_BIO_EVIDENCE\",\"name\":\"烟头\",\"sort\":\"1\"},{\"key\":\"SCENE_BIO_EVIDENCE\",\"name\":\"血迹\",\"sort\":\"2\"},{\"key\":\"SCENE_BIO_EVIDENCE\",\"name\":\"唾液\",\"sort\":\"3\"},{\"key\":\"SCENE_BIO_EVIDENCE\",\"name\":\"头发\",\"sort\":\"4\"},{\"key\":\"SCENE_HANDPRINT\",\"name\":\"手印\",\"sort\":\"5\"},{\"key\":\"SCENE_FOOTPRINT\",\"name\":\"足迹\",\"sort\":\"6\"},{\"key\":\"\",\"name\":\"其他\",\"sort\":\"7\",children:[{\"key\":\"SCENE_HANDPRINT\",\"name\":\"手印痕迹\",\"sort\":\"11\"},{\"key\":\"SCENE_FOOTPRINT\",\"name\":\"足迹痕迹\",\"sort\":\"12\"},{\"key\":\"SCENE_BIO_EVIDENCE\",\"name\":\"生物物证\",\"sort\":\"13\"},{\"key\":\"SCENE_TOOLMARK\",\"name\":\"工具痕迹\",\"sort\":\"14\"},{\"key\":\"SCENE_ELECTRO_EVIDENCE\",\"name\":\"电子物证\",\"sort\":\"15\"},{\"key\":\"SCENE_BULLETPRINT\",\"name\":\"枪弹痕迹\",\"sort\":\"16\"},{\"key\":\"SCENE_SPECIALPRINT\",\"name\":\"特殊痕迹\",\"sort\":\"17\"},{\"key\":\"SCENE_TOXIC_EVIDENCE\",\"name\":\"毒化物证\",\"sort\":\"18\"},{\"key\":\"SCENE_PHYSICAL_EVIDENCE\",\"name\":\"理化物证\",\"sort\":\"19\"},{\"key\":\"SCENE_FILE_EVIDENCE\",\"name\":\"文检物证\",\"sort\":\"20\"},{\"key\":\"SCENE_OTHER_EVIDENCE\",\"name\":\"其他物证\",\"sort\":\"21\"}]}]";
    private SelectEvidenceDialog mSelectEvidenceDialog;

    private List<SceneClass> mNameValues = new ArrayList<>();
    private float mCurrentAddPointX = -1;
    private float mCurrentAddPointY = -1;

    private final int RESULT_SCANE = 10002;
    private final int RESULT_PHOTO = 10000;
    private final int RESULT_RECORD = 10001;
    private final int RESULT_TEXT = 10003;

    private EvidenceExtra mEvidenceExtra = null;

    /**
     * custom controls
     * */
    private static ImageMap mImageMap;
    /**
     * the layout that shows associated files
     * */
    private HorizontalScrollView mShowRelationsFiles;
    private LinearLayout mShowRelationsFilesContent;
    /**
     * show shape's operations
     * */
    private View mShowShapeOperations;
    /**
     * show shape operate
     * */
    private Button mBtnShowAddFiles;
    //private Button mBtnAddShape;
    //private Button mBtnShowShapeDetail;
    //private Button mBtnEditShape;
    /**
     * show relation files dialog for appoint shape
     * */
    private View mMapPop;
    /**
     * clear all shape or remove shape
     * */
    private Button mBtnShowDeleteShapes;
    //private Button mBtnClearAllShape;
    /**
     * show source picture
     * */
    private Button mBtnShowSource;
    /**
     * just show shapes on picture
     * */
    private Button mBtnShowCircels;
    /**
     * the source bitmap
     * */
    private Bitmap mSourceBitmap;
    private Bitmap mSourceScaledBitmap;
    /**
     * the source bitmap's scale before set to imagemap
     * */
    public static float mSourceBitmapScale = 1.0f;
    /**
     * the state of operates are used to operate shapes
     * */
    public int mShapeOperateState = -1;
    public final int STATE_SHAPE_OPERATE_NONE = -1;
    public final int STATE_SHAPE_OPERATE_ADD = 10;
    public final int STATE_SHAPE_OPERATE_DETAILS = 11;
    public final int STATE_SHAPE_OPERATE_EDITE = 12;

    private Shape mCurrentSelectedShape = null;
    /**
     * shape information will be used to save
     * */
    public static List<SavePointDetails> mSaveShapes = new ArrayList<SavePointDetails>();
    private int mSelectedShape = 0;
    /**
     * it's custom controls that used to add relation files for shape
     * */
    private AddFileDialog mAddFileDialog = null;

    /**
     * current selected shape related files
     * */
    private Map<String,String> mCurrentRelateFiles = new HashMap<String,String>();
    private SavePointDetails mCurrentDetails;

    /**
     * show relate picture layout
     * */
    private View mRelateLayout = null;
    private ViewStub mVSRelateLayout = null;
    private ImageView mRelatePic = null;
    private ImageButton mBtnRelatePicClose = null;

    //add zsh 20160715 on
    private String mSourceFilePath = "";
    private String mCaseId = "";
    private String mFather = "";
    private String mTemplateId = "";
    private String mPicId = "";
    //add zsh 20160715 off

    //add zsh 20160718 on
    private String mModel = "0";
    private String mAttachId = "";
    private boolean mJustViewShow = false;
    private FinalDb mDB = EvidenceApplication.db;
    private List<SceneFileConjunction> mSceneCon = new ArrayList<SceneFileConjunction>();
    private String mSelectTag = "";
    private SceneFileConjunction mSelectSceneCon = new SceneFileConjunction();
    private List<RecordFileInfo> mSelectSceneFileInfos = new ArrayList<RecordFileInfo>();
    private List<RecordFileInfo> mSelectAudioFileInfos = new ArrayList<RecordFileInfo>();
    //add zsh 20160718 off

    private int mScreenHeight;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.trace_point_layout);
        mModel = getIntent().getStringExtra("mode");
        mTemplateId = getIntent().getStringExtra("templateId");
        mJustViewShow = getIntent().getBooleanExtra("",false);
        String id = getIntent().getStringExtra("dataId");
        Log.i("zhangsh","onCreate id = " + id + ";mModel = " + mModel);
        mAttachId = id;
        mSceneCon = getAllSceneFilesByWhere("attachmentId = \"" + mAttachId + "\"");
        getEvidenceDialogValues();
        initView();
        mMapPop = getLayoutInflater().inflate(R.layout.popup,null);
        mScreenHeight = OSUtil.getScreenHeight(this);
        mScreenWidth = OSUtil.getScreenWidth(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageMap.getViewTreeObserver().addOnGlobalLayoutListener(mImageMapListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageMap.getViewTreeObserver().removeOnGlobalLayoutListener(mImageMapListener);
    }

    @Override
    protected void onDestroy() {
        if(mImageMap != null){
            mImageMap.removeAllViews();
            mImageMap = null;
        }
        if(mSourceBitmap != null){
            mSourceBitmap.recycle();
            mSourceBitmap = null;
        }
        if(mMapPop != null){
            mMapPop = null;
        }
        if(mAddFileDialog != null){
            mAddFileDialog = null;
        }
        if(mShowRelationsFilesContent != null){
            mShowRelationsFilesContent.removeAllViews();
        }

        saveAllExtra2Json();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mRelateLayout != null && mRelateLayout.getVisibility() == View.VISIBLE){
            mRelateLayout.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 新需求更改 start
            case R.id.layout_show_identification:
                if(mCurrentState == SHAPE_STATE_HIDE){
                    mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);
                    addInitPosition();
                    mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);
                    mCurrentState = SHAPE_STATE_SHOW;
                    mLayoutShowIdentificationBp.setBackgroundResource(R.drawable.trace_state_show_shape);
                }else if(mCurrentState == SHAPE_STATE_SHOW){
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);
                    mImageMap.clearBubbles();
                    mImageMap.clearShapes();
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_PICTURES);
                    mCurrentState = SHAPE_STATE_HIDE;
                    mLayoutShowIdentificationBp.setBackgroundResource(R.drawable.trace_state_show_picture);
                }else if(mCurrentState == SHAPE_STATE_ADD){
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);
                    mCurrentState = SHAPE_STATE_SHOW;
                    mImgAddIdentification.setBackgroundResource(R.drawable.trace_state_add_shape);
                }
                hideAddFileDialog();
                break;
            case R.id.layout_add_identification:
                if(mCurrentState == SHAPE_STATE_HIDE){
                    mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);
                    addInitPosition();
                    mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);
                    mLayoutShowIdentificationBp.setBackgroundResource(R.drawable.trace_state_show_shape);
                }else if(mCurrentState == SHAPE_STATE_SHOW){

                }
                mImgAddIdentification.setBackgroundResource(R.drawable.trace_state_add_shape_select);
                mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);
                mCurrentState = SHAPE_STATE_ADD;
                hideAddFileDialog();
                break;
            case R.id.layout_add_scan:
                Intent intentScan = new Intent();
                intentScan.setClass(TracePointActivity.this,MipcaActivityCapture.class);
                intentScan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intentScan,RESULT_SCANE);
                break;
            case R.id.layout_add_photo:
                Intent intent = new Intent();
                intent.setClass(TracePointActivity.this,ShapeFileSelectActivity.class);
                intent.putExtra("caseId",mCaseId);
                intent.putExtra("cameraType","blind");
                intent.putExtra("isAnchor",true);
                intent.putExtra("father",mFather);
                intent.putExtra("section",mEvidenceExtra.getSection());
                intent.putExtra("pic_id",mAttachId);
                startActivityForResult(intent,RESULT_PHOTO);
                break;
            case R.id.layout_add_record:
                Intent intent1 = new Intent();
                intent1.setClass(TracePointActivity.this,TakeRecord.class);
                intent1.putExtra("caseId",mCaseId);
                //intent1.putExtra("child",String.valueOf(mCurrentSelectedShape.tag));
                intent1.putExtra("child","现场照片标识");
                intent1.putExtra("father", mEvidenceExtra.getFather());
                intent1.putExtra("isAnchor",true);
                intent1.putExtra("section",mEvidenceExtra.getSection());
                startActivityForResult(intent1,RESULT_RECORD);
                break;
            case R.id.layout_add_text:
                Intent textIntent = new Intent();
                textIntent.setClass(TracePointActivity.this,AddEvidenceEdit.class);
                textIntent.putExtra("id",mEvidenceExtra.getId());
                textIntent.putExtra("mode","1");
                textIntent.putExtra("templateId",mTemplateId);
                textIntent.putExtra("father",mEvidenceExtra.getFather());
                startActivity(textIntent);
                break;
            case R.id.layout_add_delete:
                if(mCurrentSelectedShape != null){
                    hideAddFileDialog();
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);
                    mSelectTag = String.valueOf(mCurrentSelectedShape.tag);
                    int size = mSceneCon.size();
                    for(int i = 0;i < size;i++){
                        SceneFileConjunction sc = mSceneCon.get(i);
                        if(mCurrentSelectedShape.tag.equals(sc.getSection()) && "".equals(sc.getAttachmentId())){
                            mSelectSceneCon = mSceneCon.get(i);
                            break;
                        }
                    }
                    removePosition(String.valueOf(mCurrentSelectedShape.tag));
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);
                }
                break;
            //新需求更改 end

            //show source bitmap
            case R.id.show_source:
                if(mImageMap != null && mImageMap.getCurrentState() != ImageMap.STATE_SHOW_PICTURES){
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);    ///更改ImageMap 状态为删除shapes状态
                    mImageMap.clearBubbles();
                    mImageMap.clearShapes();
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_PICTURES);       ///更改ImageMap 状态为显示原始图片状态
                }
                mBtnShowSource.setBackgroundResource(R.drawable._button_down);
                mBtnShowCircels.setBackground(null);
                mBtnShowAddFiles.setBackground(null);
                mBtnShowDeleteShapes.setBackground(null);
                mBtnShowAddFiles.setVisibility(View.INVISIBLE);
                mBtnShowDeleteShapes.setVisibility(View.INVISIBLE);
                /*if(mBtnClearAllShape.getVisibility() == View.VISIBLE){   ///隐藏删除所有点按钮
                    mBtnClearAllShape.setVisibility(View.GONE);
                }*/
                if(mShowShapeOperations.getVisibility() == View.VISIBLE){   ///隐藏添加按钮
                    mShowShapeOperations.setVisibility(View.GONE);
                }
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
                    mShowRelationsFiles.setVisibility(View.GONE);
                }
                break;
            //show shapes
            case R.id.show_circel:
                if(mImageMap != null && mImageMap.getCurrentState() == ImageMap.STATE_SHOW_PICTURES){
                    //mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);     ///更改ImageMap 状态为显示及添加点状态
                    mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);     ///更改ImageMap 状态为显示及添加点状态
                    addInitPosition();
                    //mImageMap.setCurrentState(ImageMap.STATE_SHOW_PICTURES);    ///更改ImageMap 状态为显示原始图片状态
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);    ///更改ImageMap 状态为显示原始图片状态
                }else if(mImageMap != null && (mImageMap.getCurrentState() == ImageMap.STATE_SHOW_BUBBLEVIEW ||
                        mImageMap.getCurrentState() == ImageMap.STATE_SHOW_DELETE_SHAPES)){
                    mImageMap.clearBubbles();                                    ///隐藏点关联的pop layout
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);      ///更改ImageMap 状态为显示点状态
                }
                mBtnShowSource.setBackground(null);
                mBtnShowAddFiles.setBackground(null);
                mBtnShowDeleteShapes.setBackground(null);
                mBtnShowCircels.setBackgroundResource(R.drawable._button_down);
                mBtnShowAddFiles.setVisibility(View.VISIBLE);
                if(!BaseView.VIEW.equals(mModel)) {
                    mBtnShowDeleteShapes.setVisibility(View.VISIBLE);
                }
                if(mShowShapeOperations.getVisibility() == View.VISIBLE){   ///隐藏添加点按钮
                    mShowShapeOperations.setVisibility(View.GONE);
                    mShowShapeOperations.setBackground(null);
                }
                /*if(mBtnClearAllShape.getVisibility() == View.VISIBLE){  ///隐藏删除所有点按钮
                    mBtnClearAllShape.setVisibility(View.GONE);
                    mBtnClearAllShape.setBackground(null);
                }*/
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
                    mShowRelationsFiles.setVisibility(View.GONE);
                }
                break;
            //operate
            case R.id.show_add_files:
                if(mImageMap != null && mImageMap.getCurrentState() != ImageMap.STATE_SHOW_BUBBLEVIEW){
                    mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);
                    mShapeOperateState = STATE_SHAPE_OPERATE_DETAILS;
                    if(mShowRelationsFiles.getVisibility() != View.VISIBLE){
                        mShowRelationsFiles.setVisibility(View.GONE);
                        //mBtnAddShape.setBackgroundColor(Color.argb(0,0,0,0));
                    }
                }
                mBtnShowAddFiles.setBackgroundResource(R.drawable._button_down);
                mBtnShowSource.setBackground(null);
                mBtnShowCircels.setBackground(null);
                mBtnShowDeleteShapes.setBackground(null);
                /*if(mBtnClearAllShape.getVisibility() == View.VISIBLE){   ///隐藏删除所有点按钮
                    mBtnClearAllShape.setVisibility(View.GONE);
                }*/
                mShowShapeOperations.setVisibility(View.VISIBLE);
                break;
            /*case R.id.btn_add_shape:
                mImageMap.clearBubbles();                                    ///隐藏点关联的pop layout
                //mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);    ///更改ImageMap 状态为显示及添加点状态
                mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);    ///更改ImageMap 状态为显示及添加点状态
                //mBtnAddShape.setBackgroundResource(R.drawable._button_down);
                mShapeOperateState = STATE_SHAPE_OPERATE_ADD;
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
                    mShowRelationsFiles.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_shape_details:
                mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);       ///初始化点关联的附件添加pop layout
                mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);        ///更改ImageMap 状态为显示点关联pop layout
                mShapeOperateState = STATE_SHAPE_OPERATE_DETAILS;
                //mBtnAddShape.setBackground(null);
                break;
            case R.id.btn_operate_shape:
                mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);       ///初始化点关联的附件添加pop layout
                mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);        ///更改ImageMap 状态为显示点关联pop layout
                mShapeOperateState = STATE_SHAPE_OPERATE_EDITE;
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
                    mShowRelationsFiles.setVisibility(View.GONE);
                }
                //mBtnAddShape.setBackground(null);
                break;*/
                //delete
            case R.id.show_delete_shapes:
                if(mImageMap != null && mImageMap.getCurrentState() != ImageMap.STATE_SHOW_DELETE_SHAPES){
                    mImageMap.setShapesBubbleView(R.layout.popup,mMapDeleteShaps);   ///初始化点关联的删除点pop layout
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);    ///更改ImageMap 状态为删除shapes状态
                    //mBtnClearAllShape.setVisibility(View.VISIBLE);
                }
                mBtnShowDeleteShapes.setBackgroundResource(R.drawable._button_down);
                mBtnShowSource.setBackground(null);
                mBtnShowCircels.setBackground(null);
                mBtnShowAddFiles.setBackground(null);
                if(mShowShapeOperations.getVisibility() == View.VISIBLE){   ///隐藏添加点按钮
                    mShowShapeOperations.setVisibility(View.GONE);
                }
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
                    mShowRelationsFiles.setVisibility(View.GONE);
                }
                break;
            /*case R.id.btn_clear_all_shape:
                clearPositions();
                break;*/
            default:
                break;
        }
    }

    @Override
    public void onTouchClick(float x, float y) {
        if(mShowRelationsFiles.getVisibility() == View.VISIBLE){
            mShowRelationsFiles.setVisibility(View.GONE);
            mShowShapeOperations.setVisibility(View.GONE);
            return;
        }
        if(mCurrentState != SHAPE_STATE_ADD){
            return;
        }

        mImgAddIdentification.setBackgroundResource(R.drawable.trace_state_add_shape);

        mCurrentAddPointX = x;
        mCurrentAddPointY = y;
        initNameValue(mEvidenceDialogValues);
        if(mSelectEvidenceDialog == null){
            mSelectEvidenceDialog = new SelectEvidenceDialog(this,mNameValues);
            mSelectEvidenceDialog.setmListener(selectEvidenceItemClick);
            mSelectEvidenceDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mCurrentAddPointX = -1;
                    mCurrentAddPointY = -1;
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);
                }
            });
        }else{
            mSelectEvidenceDialog.updateListView(mNameValues);
            mSelectEvidenceDialog.setmListener(selectEvidenceItemClick);
        }
        mSelectEvidenceDialog.showAtLocation(findViewById(R.id.trace_point_root), Gravity.CENTER,0,0);
        mCurrentState = SHAPE_STATE_SHOW;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SceneFileConjunction sc = new SceneFileConjunction();
        //sc.setId(UUID.randomUUID().toString().replaceAll("-",""));
        //sc.setAttachmentId(mAttachId);
        //sc.setSection(mSelectTag);
        //sc.setX(mSelectSceneCon.getX());
        //sc.setY(mSelectSceneCon.getY());
        Log.i("jiu","requestCode = " + requestCode);
        if(RESULT_PHOTO == requestCode){
            if (data != null) {
                Log.i("zhangsh", "data path = " + data.getStringExtra("dataId"));
                String id = data.getStringExtra("dataId");
                //sc.setChildAttachmentId(id);
            }
        }else if(RESULT_RECORD == requestCode){
            if (data != null) {
                Log.i("zhangsh", "data path = " + data.getStringExtra("soundpath") + ";id = " + data.getStringExtra("dataId"));
                String attachid = data.getStringExtra("dataId");
                //sc.setChildAttachmentId(attachid);
                //initShapeDetailsLayout();
            }
        }else if(RESULT_SCANE == requestCode){
            if (data != null) {
                Log.i("jiu", "data = " + data.getStringExtra("result"));
                String scanResult = data.getExtras().getString("result");
                saveScanData(scanResult);
            }
        } else if(RESULT_TEXT == requestCode){

        }
        initShapeDetailsLayout();
        //mSceneCon.add(sc);
        //insertSceneCon(sc);
    }

    void initView(){
        // 新需求更改 start
        mLayoutShowIdentification = findViewById(R.id.layout_show_identification);
        mLayoutShowIdentification.setOnClickListener(this);
        mLayoutShowIdentificationBp = (ImageView)findViewById(R.id.layout_show_identification_bitmap);
        mLayoutAddIdentification = findViewById(R.id.layout_add_identification);
        if(mModel == null || BaseView.EDIT.equals(mModel)) {
            mLayoutAddIdentification.setOnClickListener(this);
            mLayoutAddIdentification.setVisibility(View.VISIBLE);
        }
        mImgAddIdentification = (ImageView) findViewById(R.id.layout_add_identification_img);
        mBtnLayoutAddScan = findViewById(R.id.layout_add_scan);
        mBtnLayoutAddScan.setOnClickListener(this);
        mBtnLayoutAddPhoto = findViewById(R.id.layout_add_photo);
        mBtnLayoutAddPhoto.setOnClickListener(this);
        mBtnLayoutAddRecord = findViewById(R.id.layout_add_record);
        mBtnLayoutAddRecord.setOnClickListener(this);
        mBtnLayoutAddText = findViewById(R.id.layout_add_text);
        mBtnLayoutAddText.setOnClickListener(this);
        mBtnLayoutDelete = findViewById(R.id.layout_add_delete);
        mBtnLayoutDelete.setOnClickListener(this);
        // 新需求更改 end
        //
        mShowShapeOperations = findViewById(R.id.show_shape_operate);
        //shape details
        mShowRelationsFiles = (HorizontalScrollView)findViewById(R.id.show_relation_files);
        mShowRelationsFilesContent = (LinearLayout) findViewById(R.id.show_relation_files_content);
        //delete
        mBtnShowDeleteShapes = (Button) findViewById(R.id.show_delete_shapes);
        mBtnShowDeleteShapes.setOnClickListener(this);
        //mBtnClearAllShape = (Button) findViewById(R.id.btn_clear_all_shape);
        //mBtnClearAllShape.setOnClickListener(this);
        //show source
        mBtnShowSource = (Button)findViewById(R.id.show_source);
        mBtnShowSource.setOnClickListener(this);
        mBtnShowSource.setBackgroundResource(R.drawable._button_down);
        //show source's shape
        mBtnShowCircels = (Button) findViewById(R.id.show_circel);
        mBtnShowCircels.setOnClickListener(this);
        //operate
        mBtnShowAddFiles = (Button)findViewById(R.id.show_add_files);
        mBtnShowAddFiles.setOnClickListener(this);
//        mBtnAddShape = (Button) findViewById(R.id.btn_add_shape);
//        mBtnAddShape.setOnClickListener(this);
//        mBtnShowShapeDetail = (Button)findViewById(R.id.btn_shape_details);
//        mBtnShowShapeDetail.setOnClickListener(this);
//        mBtnEditShape = (Button)findViewById(R.id.btn_operate_shape);
//        mBtnEditShape.setOnClickListener(this);
//        if(!BaseView.VIEW.equals(mModel)){
//            mBtnAddShape.setVisibility(View.VISIBLE);
//            mBtnEditShape.setVisibility(View.VISIBLE);
//        }
        Intent sourceIntent = getIntent();
        if(sourceIntent != null) {
            mSourceFilePath = sourceIntent.getStringExtra("filepath");
            mCaseId = sourceIntent.getStringExtra("caseId");
            mFather = sourceIntent.getStringExtra("father");
        }
        Log.i("zhangsh","path ppT = " + mSourceFilePath + ";mCaseId = " + mCaseId + ";mFather = " + mFather);
        if(mSourceFilePath == null || mSourceFilePath.isEmpty()) {
            mSourceBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Bitmap.jpg");
        }else{
            mSourceBitmap = BitmapFactory.decodeFile(mSourceFilePath);
        }

        //custom controls
        mImageMap = (ImageMap) findViewById(R.id.image_map);
        getSacleBitmap(mSourceBitmap);
        mImageMap.setMapBitmap(mSourceScaledBitmap);
        mImageMap.setOnTouchAddPosition(this);
        mImageMap.getViewTreeObserver().addOnGlobalLayoutListener(mImageMapListener);
        // 锚点新需求更改 start
        mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);
        addInitPosition();
        mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);
        mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);
        findViewById(R.id.trace_point_tab_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideAddFileDialog();
                return false;
            }
        });
        mShowShapeOperations.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // 锚点新需求更改 end
    }

    public void addInitPosition(){
        for (SceneFileConjunction item : mSceneCon){
            //Log.i("zhangsh","id = " + item.getId() + ";section = " + item.getSection() + ";childattchId = " + item.getChildAttachmentId() + ";attachId = " + item.getAttachmentId() + ";mSourceBitmapScale = " + mSourceBitmapScale);
            //if("".equals(item.getChildAttachmentId())) {
            CircleShape red = new CircleShape(item.getSection(), Color.RED);
            red.setAlaph(0);   //设置锚点透明度
            red.setValues(item.getX() * mSourceBitmapScale, item.getY() * mSourceBitmapScale, 10);
            mImageMap.addShape(red);
            //}
        }
    }

    public Shape addPosition(float x,float y, String key){
        CircleShape red = null;
        if(mImageMap.getCurrentState() == ImageMap.STATE_ADD_SHAPES) {
            String tag = UUID.randomUUID().toString().replaceAll("-","");
            red = new CircleShape(tag, Color.RED);
            red.setAlaph(0);
            red.setValues(x, y, 10);
            mImageMap.addShapeWithOutScale(red);
            //////////////////////////////////////////////////////////
            //save this shape
            PointF abPoint = mImageMap.getAbsoluteOffset();
            //添加数据到数据库
            SceneFileConjunction sc = new SceneFileConjunction();
            sc.setId(UUID.randomUUID().toString().replaceAll("-",""));
            sc.setAttachmentId(mAttachId);
            if(mEvidenceExtra != null){
                sc.setChildAttachmentId(mEvidenceExtra.getId());
            }else {
                sc.setChildAttachmentId("");
            }
            sc.setX((red.getCenterPoint().x - abPoint.x) / (mImageMap.getCurrentScale() * mSourceBitmapScale));
            sc.setY((red.getCenterPoint().y - abPoint.y) / (mImageMap.getCurrentScale() * mSourceBitmapScale));
            sc.setHeight(mScreenHeight+"");
            sc.setWidth(mScreenWidth+"");

            sc.setSection(tag);
            sc.setType(key);
            insertSceneCon(sc);
            mSceneCon.add(sc);
            Log.i("zhangsh","addPosition sc.section = " + sc.getSection() + ";sc.id = " + sc.getId());
            //添加描点按钮为一次性，添加完后，更改状态为STATE_SHOW_PICTURES，并将添加按钮更改回来
            //mBtnAddShape.setBackgroundColor(Color.argb(0,0,0,0));
            mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);
        }
        return red;
    }

    public void removePosition(String tag){
        mImageMap.removeShape(tag);
        //删除RecordFileInfo库中record内容 以及删除相应的文件 start
        /*String where = "fileType = \"audio\" and  id in ("+
                "select b.childAttachmentId from SceneFileConjunction b where b.attachmentId = \"" +
                mAttachId +"\" and b.section = \"" + tag + "\" and (b.childAttachmentId is not null or b.childAttachmentId != \"\"))";*/
        String where = "fileType = \"audio\" and section = \"" + mEvidenceExtra.getSection() + "\"";
        List<RecordFileInfo> deleteFiles = findAllAttechFiles(where);
        //下列代码利用剩余的mSelectSceneFileInfos删除相应的文件
        deleteFilesFromStorage(deleteFiles);
        deleteAttechFiles(where);
        //删除RecordFileInfo库中record内容 以及删除相应的文件 end
        updateFilesInDB(findAllAttechFiles("fileType = \"png\" and section = \"" + mEvidenceExtra.getSection() + "\""));
        deleteSceneCon("section = \"" + tag + "\" and attachmentId = \"" + mAttachId + "\"");
        deleteEvidenceExtra("id = \"" + mEvidenceExtra.getId() + "\"");
        mSceneCon = getAllSceneFilesByWhere("attachmentId = \"" + mAttachId + "\"");
    }

    public void clearPositions(){
        mImageMap.clearShapes();
        //mSaveShapes.clear();
        //删除RecordFileInfo库中record内容 以及删除相应的文件 start
        /*String where = "fileType = \"audio\" and  id in ("+
                "select b.childAttachmentId from SceneFileConjunction b where b.attachmentId = \"" +
                mAttachId +"\" and (b.childAttachmentId is not null or b.childAttachmentId != \"\"))";*/
        String where = "fileType = \"audio\" and section = \"" + mEvidenceExtra.getSection() + "\"";
        List<RecordFileInfo> deleteFiles = findAllAttechFiles(where);
        //删除deleteFiles中相应文件 on
        deleteFilesFromStorage(deleteFiles);
        //删除deleteFiles中相应文件 off
        deleteAttechFiles(where);
        //删除RecordFileInfo库中record内容 以及删除相应的文件 end
        mSceneCon.clear();
        updateFilesInDB(findAllAttechFiles("fileType = \"png\" and section = \"" + mEvidenceExtra.getSection() + "\""));
        deleteSceneCon("attachmentId = \"" + mAttachId + "\"");
        deleteEvidenceExtra("id = \"" + mEvidenceExtra.getId() + "\"");
    }

    private void getSacleBitmap(Bitmap bm){
        if(mSourceScaledBitmap != null){
            mSourceScaledBitmap.recycle();
            mSourceScaledBitmap = null;
        }
        System.gc();
       // Bitmap result = null;
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        float winWidth = display.getWidth();
        float winHeight = display.getHeight();
        float width = bm.getWidth();
        float height = bm.getHeight();
        float scale = 1.f;
        Matrix matrix = new Matrix();
        Log.i("zhangsh","window width = " + winWidth + ";height = " + winHeight + ";width = " + width + ";height = " + height);
        if(width < height){
            width = height;
            height = bm.getWidth();
            if(width > winWidth || height > winHeight){
                if(width - winWidth < height - winHeight){
                    scale = winWidth/width;
                }else{
                    scale = winHeight/height;
                }
            }else{
                //if(winWidth - width > winHeight - height){
                if(winWidth/width > winHeight/height){
                    scale = winWidth/width;
                }else{
                    scale = winHeight/height;
                }
            }
            matrix.setRotate(-90);
            matrix.postScale(scale,scale);
            mSourceScaledBitmap = Bitmap.createBitmap(bm,0,0,(int)height,(int)width,matrix,true);
        }else{
            if(width > winWidth || height > winHeight){
                if(width - winWidth > height - winHeight){
                    scale = winWidth/width;
                }else{
                    scale = winHeight/height;
                }
            }else{
                //if(winWidth - width > winHeight - height){
                if(winWidth/width > winHeight/height){
                    scale = winWidth/width;
                }else{
                    scale = winHeight/height;
                }
            }

            matrix.postScale(scale,scale);
            mSourceScaledBitmap = Bitmap.createBitmap(bm,0,0,(int)width,(int)height,matrix,true);
        }
        mSourceBitmapScale = scale;
        Log.i("zhangsh","initBitmap bmWidth = " + width + ";bmHeight = " + height + ";saveScale = " + scale);
        //mSourceScaledBitmap = Bitmap.createBitmap(bm,0,0,(int)width,(int)height,matrix,true);
    }

    /**
     *  show delte shape button
     * */
    private  Bubble.RenderDelegate mMapDeleteShaps = new Bubble.RenderDelegate() {
        @Override
        public void onDisplay(final Shape shape, View bubbleView) {
            ImageView logo = (ImageView) bubbleView.findViewById(R.id.logo);
            logo.setVisibility(View.GONE);
            ImageView close = (ImageView)bubbleView.findViewById(R.id.shape_remove);
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("zhangsh","close onClick tag = " + shape.tag + ";center(" + shape.getCenterPoint().x + "," + shape.getCenterPoint().y + ")");
                    mSelectTag = String.valueOf(shape.tag);
                    int size = mSceneCon.size();
                    for(int i = 0;i < size;i++){
                        SceneFileConjunction sc = mSceneCon.get(i);
                        if(shape.tag.equals(sc.getSection()) && "".equals(sc.getAttachmentId())){
                            mSelectSceneCon = mSceneCon.get(i);
                            break;
                        }
                    }
                    removePosition(String.valueOf(shape.tag));
                }
            });
        }
    };
    /**
     *  show add file button
     * */
    private  Bubble.RenderDelegate mMapAddFiles = new Bubble.RenderDelegate() {
        @Override
        public void onDisplay(final Shape shape, View bubbleView) {
            ImageView logo = (ImageView) bubbleView.findViewById(R.id.logo);
            logo.setVisibility(View.VISIBLE);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showAddFileDialog();
                    mShowRelationsFiles.setVisibility(View.VISIBLE);
                    if(mModel == null || BaseView.EDIT.equals(mModel)) {
                        mShowShapeOperations.setVisibility(View.VISIBLE);
                    }
                    mSelectTag = String.valueOf(shape.tag);
                    mCurrentSelectedShape = shape;

                    int size = mSceneCon.size();
                    for(int i = 0;i < size;i++){
                        SceneFileConjunction sc = mSceneCon.get(i);
                        //if(shape.tag.equals(sc.getSection()) && "".equals(sc.getChildAttachmentId())){
                        if(shape.tag.equals(sc.getSection())){
                            mSelectSceneCon = sc;
                            break;
                        }
                    }
                    //锚点新需求更改 start
                    Log.e("jiu", "childId : " + mSelectSceneCon.getChildAttachmentId());
                    mEvidenceExtra = getEvidenceExtra(mSelectSceneCon.getChildAttachmentId());

                    initShapeDetailsLayout();
                    ////锚点新需求更改 end
                }
            });
            ImageView close = (ImageView)bubbleView.findViewById(R.id.shape_remove);
            close.setVisibility(View.GONE);
        }
    };

    /**
     * Dialog image click listener
     * */
    private AddFileDialog.clickListener mDialogClickListener = new AddFileDialog.clickListener() {
        @Override
        public void captureClick() {
            if(mAddFileDialog.isShowing())
                mAddFileDialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(TracePointActivity.this,ShapeFileSelectActivity.class);
            intent.putExtra("caseId",mCaseId);
            intent.putExtra("cameraType","blind");
            intent.putExtra("isAnchor",true);
            startActivityForResult(intent,10000);
        }

        @Override
        public void recordClick() {
            if(mAddFileDialog.isShowing())
                mAddFileDialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(TracePointActivity.this,TakeRecord.class);
            intent.putExtra("caseId",mCaseId);
            intent.putExtra("child",String.valueOf(mCurrentSelectedShape.tag));
            intent.putExtra("father","point");
            intent.putExtra("isAnchor",true);
            startActivityForResult(intent,10001);
        }

        @Override
        public void scanClick() {

        }

        @Override
        public void saveTextClick() {

        }

        @Override
        public void deleteShape() {
            if(mCurrentSelectedShape != null){
                //hideAddFileDialog();
                if(mShowRelationsFiles.getVisibility() == View.VISIBLE) {
                    mShowRelationsFiles.setVisibility(View.GONE);
                    mShowShapeOperations.setVisibility(View.GONE);
                }
                mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);
                mSelectTag = String.valueOf(mCurrentSelectedShape.tag);
                int size = mSceneCon.size();
                for(int i = 0;i < size;i++){
                    SceneFileConjunction sc = mSceneCon.get(i);
                    if(mCurrentSelectedShape.tag.equals(sc.getSection()) && "".equals(sc.getAttachmentId())){
                        mSelectSceneCon = mSceneCon.get(i);
                        break;
                    }
                }
                removePosition(String.valueOf(mCurrentSelectedShape.tag));
                mImageMap.setCurrentState(ImageMap.STATE_SHOW_BUBBLEVIEW);
            }
        }
    };


    private Bitmap getBitmapXXX(String path,BitmapFactory.Options options){
        Bitmap bp = BitmapFactory.decodeFile(path,options);
        return bp;
    }

    private Bitmap getBitmapXXX(int index,BitmapFactory.Options options){
        Bitmap bp = BitmapFactory.decodeResource(getResources(),index,options);
        return bp;
    }

    private  BitmapFactory.Options getBitmapOptions(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 120;
        options.outHeight = 120;
        options.inSampleSize = 10;
        options.inJustDecodeBounds = false;
        return options;
    }

    private View.OnClickListener mRelationImgClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = String.valueOf(v.getTag());
            if("audio".equals(tag)){
                Intent intent = new Intent(TracePointActivity.this,PlayRecord.class);
                /*String where = "fileType = \"audio\" and id in (" +
                        "select b.childAttachmentId from SceneFileConjunction b where b.SECTION = \"" + mSelectSceneCon.getSection() +
                        "\" and (b.childAttachmentId is not null or b.childAttachmentId != \"\"))";*/
                String where = "fileType = \"audio\" and section = \"" + mEvidenceExtra.getSection() + "\"";
                intent.putExtra("queryWhere",where);
                startActivity(intent);
                return;
            }
            if("text".equals(tag)){
                Intent textIntent = new Intent(TracePointActivity.this,AddEvidenceEdit.class);
                textIntent.putExtra("id",mEvidenceExtra.getId());
                textIntent.putExtra("mode","0");
                textIntent.putExtra("viewWithoutToast",true);
                //textIntent.putExtra("templateId",mTemplateId);
                textIntent.putExtra("templateId",mEvidenceExtra.getFather());
                startActivity(textIntent);
                return;
            }
            if(mRelateLayout == null) {
                mVSRelateLayout = (ViewStub) findViewById(R.id.viewstub_show_relate_pic);
                mRelateLayout = mVSRelateLayout.inflate();
                mRelatePic = (ImageView) mRelateLayout.findViewById(R.id.img_show_relate_pic);
                mRelatePic.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                mBtnRelatePicClose = (ImageButton)mRelateLayout.findViewById(R.id.btn_hide_layout);
                mBtnRelatePicClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRelateLayout.setVisibility(View.GONE);
                    }
                });
            }else{
                mRelateLayout.setVisibility(View.VISIBLE);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = 1280;
            options.outHeight = 720;
            RecordFileInfo info = null;
            int size = mSelectSceneFileInfos.size();
            for (int i = 0;i < size;i++){
                info = mSelectSceneFileInfos.get(i);
                Log.i("zhangsh","mRelationImgClick tag = " + tag + ";id = " + info.getAttachmentId());
                if(tag.equals(info.getAttachmentId())){
                    break;
                }
            }
            if("png".equals(info.getFileType())){
                mRelatePic.setImageBitmap(getBitmapXXX(AppPathUtil.getDataPath() + "/" + info.getTwoHundredFilePath(),
                        options));
            }

        }
    };

    /**
     * operate SceneFileConjunction db
     * */
    private List<SceneFileConjunction> getAllSceneFilesByWhere(String where){
        Log.i("zhangsh","SceneFileConjunction where = " + where);
        List<SceneFileConjunction> result = null;
        result = mDB.findAllByWhere(SceneFileConjunction.class,where);
        return result;
    }

    private void insertSceneCon(SceneFileConjunction data){
        mDB.save(data);
    }

    private void deleteSceneCon(String where){
        Log.i("zhangsh","deleteSceneCon where = " + where);
        mDB.deleteByWhere(SceneFileConjunction.class,where);
    }

    private void deleteEvidenceExtra(String where){
        mDB.deleteByWhere(EvidenceExtra.class,where);
    }

    /**
     * operate RecordFileInfo db
     * */
    private List<RecordFileInfo> findAllAttechFiles(String where){
        Log.i("zhangsh","findAllAttechFiles where = " + where);
        List<RecordFileInfo> infos = mDB.findAllByWhere(RecordFileInfo.class,where);
        return infos;
    }

    private void deleteAttechFiles(String where){
        Log.i("zhangsh","deleteAttechFiles where = " + where);
        mDB.deleteByWhere(RecordFileInfo.class,where);
    }

    private void deleteFilesFromStorage(List<RecordFileInfo> lists){
        int size = lists.size();
        RecordFileInfo info = null;
        File file = null;
        for(int i = 0;i < size;i++){
            info = lists.get(i);
            file = new File(info.getFilePath());
            if(file.isFile() && file.exists()){
                file.delete();
            }
        }
    }

    private void updateFilesInDB(List<RecordFileInfo> lists){
        int size = lists.size();
        RecordFileInfo info = null;
        File file = null;
        for(int i = 0;i < size;i++){
            info = lists.get(i);
            info.setSection("");
            mDB.update(info);
        }
    }

    /**
     * save SceneFileConjunction details to DataTemp when case done
     * 勘验结束时在DataTemp表中保存SceneFileConjunction表的json信息
     * */
    private void saveSceneFileConjunctionDetails(){
        List<SceneFileConjunction> sceneFileConjunctions = EvidenceApplication.db.findAllByWhere(SceneFileConjunction.class,
                "attachmentId in (select a.id from RecordFileInfo a where a.caseId = \"" + mCaseId + "\")");
        Log.i("zhangsh","saveSceneFileConjunctionDetails caseId = " + mCaseId);
        DataTemp temp = null;
        int i = 0;
        for(SceneFileConjunction item : sceneFileConjunctions){
            temp = new DataTemp();
            temp.setCaseId(mCaseId);
            String id = UUID.randomUUID().toString().replaceAll("-","");
            temp.setId(id);
            temp.setDataType("scene_file_conjunction");
            temp.setFather("SCENE_BLIND_SHOOT");
            String json = com.alibaba.fastjson.JSON.toJSONString(item);
            Log.i("zhangsh","saveSceneFileConjunctionDetails i = " + i + ";id = " + id +
                    ";json = " + json);
            temp.setData(json);
            EvidenceApplication.db.save(temp);
            i++;
        }
    }

    private AdapterView.OnItemClickListener selectEvidenceItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String nameValueKey = mNameValues.get(position).getKey();
            if(nameValueKey == null || "".equals(nameValueKey)){
                try {
                    JSONArray arrays = new JSONArray(mEvidenceDialogValues);
                    int length = arrays.length();
                    JSONArray child = null;
                    for (int i = 0;i < length;i++){
                        JSONObject object = arrays.getJSONObject(i);
                        if("其他".equals(object.getString("name"))){
                            child = object.getJSONArray("children");
                            break;
                        }
                    }
                    //child = arrays.getJSONArray(arrays.length() - 1);
                    if(child.length() < 1){
                        Toast.makeText(TracePointActivity.this,"无其他项可选！",Toast.LENGTH_SHORT).show();
                        mSelectEvidenceDialog.updateListView(mNameValues);
                        mSelectEvidenceDialog.setmListener(selectEvidenceItemClick);
                        return;
                    }else {
                        initNameValue(child.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSelectEvidenceDialog.updateListView(mNameValues);
                mSelectEvidenceDialog.setmListener(secondSelectEvidenceItemClick);
                return;
            }
            showShapeView(nameValueKey);
        }
    };

    private AdapterView.OnItemClickListener secondSelectEvidenceItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String nameValueKey = mNameValues.get(position).getKey();
            showShapeView(nameValueKey);
        }
    };

    private void showShapeView(String key){
        Log.e("jiu", "key = " + key);
        //锚点需求更改 start
        mEvidenceExtra = addEvidenceExtra(key);
        //锚点需求更改 end
        //进行point添加
        mCurrentSelectedShape = addPosition(mCurrentAddPointX,mCurrentAddPointY, key);
        mImageMap.setSingleBubbleView(R.layout.popup,mMapAddFiles,String.valueOf(mCurrentSelectedShape.tag));
        mImageMap.setCurrentState(ImageMap.STATE_SHOW_SHAPES);
        //showAddFileDialog();
        showAddFileDialog();
        mSelectEvidenceDialog.dismiss();
        initShapeDetailsLayout();
        updateAttachStatus();
        searchAndCreateTemplate(key);
    }

    private void showAddFileDialog(){
        if(mShowShapeOperations.getVisibility() != View.VISIBLE){
            mShowRelationsFiles.setVisibility(View.VISIBLE);
            mShowShapeOperations.setVisibility(View.VISIBLE);
        }
        /*if(mAddFileDialog == null){
            mAddFileDialog = new AddFileDialog(TracePointActivity.this,R.style.DialogTheme);
            mAddFileDialog.setClickListener(mDialogClickListener);
            mAddFileDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(mShowRelationsFiles != null){
                        //mShowRelationsFiles.setVisibility(View.GONE);
                    }
                }
            });
        }
        mAddFileDialog.show();
        mShowRelationsFiles.setVisibility(View.VISIBLE);*/
    }

    private void hideAddFileDialog(){
        if(mShowShapeOperations.getVisibility() == View.VISIBLE){
            mShowRelationsFiles.setVisibility(View.GONE);
            mShowShapeOperations.setVisibility(View.GONE);
        }
        /*if(mAddFileDialog != null){
            mAddFileDialog.dismiss();
        }*/
    }

    private EvidenceExtra addEvidenceExtra(String father){
        EvidenceExtra extra = null;
        try {
            extra = new EvidenceExtra();
            extra.setId(ViewUtil.getUUid());
            //extra.setFather(mFather);
            extra.setFather(father);
            extra.setCaseId(mCaseId);
            extra.setSection(extra.getId());
            JSONObject object = new JSONObject("{}");
            object.put("SECTION",extra.getSection());
            extra.setJson(object.toString());
            mDB.save(extra);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extra;
    }

    private EvidenceExtra getEvidenceExtra(String id){
        EvidenceExtra extra =  mDB.findAllByWhere(EvidenceExtra.class,"id = \"" + id + "\"").get(0);
        return extra;
    }

    private void saveScanData(String data){
        try {
            JSONObject jsonObject = new JSONObject(mEvidenceExtra.getJson());
//            Iterator<String> iterator = jsonObject.keys();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                try {
//                    /*if (key.equals("BAR_CODE")) {
//                        jsonObject.put(key, data);
//                    } else if (key.equals("QR_CODE")) {
//                        jsonObject.put(key, data);
//                    } else if (key.equals("RFID_CODE")) {
//                        jsonObject.put(key, data);
//                    }*/
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            jsonObject.put("BAR_CODE",data);
            jsonObject.put("QR_CODE",data);
            jsonObject.put("RFID_CODE",data);
            mEvidenceExtra.setJson(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDB.update(mEvidenceExtra);
    }

    private void initShapeDetailsLayout(){
        mSelectSceneFileInfos = findAllAttechFiles("section = \"" + mEvidenceExtra.getSection() + "\"");
        mSelectAudioFileInfos = findAllAttechFiles("section = \"" + mEvidenceExtra.getSection() + "\" and fileType = \"audio\"");
        int length = mSelectSceneFileInfos.size();
        mShowRelationsFilesContent.removeAllViews();
        BitmapFactory.Options options = getBitmapOptions();
        int width = Utils.dp2Px(this,40);
        int left = Utils.dp2Px(this,8);
        //锚点需求更改 start
        String bar = "";
        try {
            JSONObject object = new JSONObject(mEvidenceExtra.getJson());
            bar = object.getString("BAR_CODE");
        } catch (JSONException e) {
            Log.i("TracePointActivity","initShapeDetailsLayout bar exception",e);
            e.printStackTrace();
            bar = "";
        }
        if(bar != null && !"".equals(bar)){
            TextView textView = new TextView(TracePointActivity.this);
            textView.setTag("bar");
            textView.setText(bar);
            textView.setTextColor(Color.WHITE);
            textView.setTop(Utils.dp2Px(this,30));
            textView.setLeft(left);
            mShowRelationsFilesContent.addView(textView);
        }
        //锚点需求更改 end
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        params.leftMargin = 20;
        ImageView ivv = null;
        for(int i = 0;i < length;i++){
            RecordFileInfo info = mSelectSceneFileInfos.get(i);
            if("png".equals(info.getFileType())){
                ivv = new ImageView(TracePointActivity.this);
                ivv.setTag(info.getAttachmentId());
                //ivv.setImageBitmap(getBitmapXXX(info.getTwoHundredFilePath(), options));
                ivv.setBackground(BitmapDrawable.createFromPath(AppPathUtil.getDataPath() + "/" + info.getTwoHundredFilePath()));
                ivv.setOnClickListener(mRelationImgClick);
                ivv.setLeft(left);
                mShowRelationsFilesContent.addView(ivv, params);
            }
        }
        if(mSelectAudioFileInfos.size() > 0){
            ivv = new ImageView(TracePointActivity.this);
            ivv.setTag("audio");
            ivv.setAdjustViewBounds(true);
            ivv.setLeft(left);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width,width);
            params1.setMargins(20,0,0,0);
            ivv.setLayoutParams(params1);
            ivv.setBackgroundResource(R.drawable.trace_show_audio);
            ivv.setOnClickListener(mRelationImgClick);
            mShowRelationsFilesContent.addView(ivv, params1);
        }


        if(true){
            ivv = new ImageView(TracePointActivity.this);
            ivv.setTag("text");
            ivv.setAdjustViewBounds(true);
            ivv.setLeft(left);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width,width);
            ivv.setLayoutParams(params1);
            ivv.setBackgroundResource(R.drawable.trace_edit_text);
            ivv.setOnClickListener(mRelationImgClick);
            mShowRelationsFilesContent.addView(ivv, params1);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener mImageMapListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(true/*mImageMap == null*/){
                return;
            }
            int rootWidth = mImageMap.getHeight();
            int rootH = mImageMap.getWidth();
            int bitmapW = mSourceBitmap.getWidth();
            int bitmapH = mSourceBitmap.getHeight();
            float scale = 0;
            if(bitmapW < bitmapH){
                scale = rootWidth * 1.0f / bitmapW;
            }else{
                scale = rootWidth * 1.0f / bitmapH;
            }

            if(scale == mSourceBitmapScale){
                return;
            }
            if(mSourceBitmap != null) {
                mSourceBitmapScale =scale;
                int currentState = mImageMap.getCurrentState();
                if(currentState != ImageMap.STATE_SHOW_PICTURES){
                    mImageMap.setCurrentState(ImageMap.STATE_SHOW_DELETE_SHAPES);
                    mImageMap.clearBubbles();
                    mImageMap.clearShapes();
                    getSacleBitmap(mSourceBitmap);
                    mImageMap.setMapBitmap(mSourceScaledBitmap);
                    mImageMap.setCurrentState(ImageMap.STATE_ADD_SHAPES);
                    addInitPosition();
                    mImageMap.setShapesBubbleView(R.layout.popup,mMapAddFiles);
                    mImageMap.setCurrentState(currentState);
                }else{
                    getSacleBitmap(mSourceBitmap);
                    mImageMap.setMapBitmap(mSourceScaledBitmap);
                }
                Log.i("zhangsh","onGlobalLayout mSourceBitmapScale = " + mSourceBitmapScale);
            }
        }
    };

    private void initNameValue(String json){
        //json = "[{\"key\":\"SCENE_TOOLMARK\",\"name\":\"工具痕迹\",\"sort\":\"1\"},{\"key\":\"SCENE_TOXIC_EVIDENCE\",\"name\":\"毒化物证\",\"sort\":\"2\"}]";
        mNameValues.clear();
        SceneClass sceneClass = null;
        try {
            JSONArray arrays = new JSONArray(json);
            JSONObject object = null;
            int length = arrays.length();
            for(int i = 0;i < length;i++){
                sceneClass = new SceneClass();
                object = arrays.getJSONObject(i);
                sceneClass.setKey(object.getString("key"));
                sceneClass.setName(object.getString("name"));
                sceneClass.setSort(object.getInt("sort"));
                mNameValues.add(sceneClass);
            }
            Collections.sort(mNameValues, new java.util.Comparator<SceneClass>() {
                @Override
                public int compare(SceneClass lhs, SceneClass rhs) {
                    return lhs.getSort() - rhs.getSort();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class SceneClass implements Comparator{

        private String name;
        private String key;
        private int sort;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        @Override
        public int compare(Object o, Object o1) {
            SceneClass s = (SceneClass)o;
            SceneClass s1 = (SceneClass)o1;
            return s.sort - s1.sort;
        }
    }

    private void updateAttachStatus(){
        RecordFileInfo mainInfo = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"id = \"" + mAttachId + "\"").get(0);
        if(mainInfo != null){
            String isMarked = mainInfo.getIsMarked();
            if(isMarked == null || "".equals(isMarked)){
                mainInfo.setIsMarked("yes");
                EvidenceApplication.db.update(mainInfo);
            }
        }
    }

    private void searchAndCreateTemplate(String key){
        List<TemplateSort> sorts = EvidenceApplication.db.findAllByWhere(TemplateSort.class,"caseId = \"" + mCaseId + "\" and fatherKey = \"" + key + "\"");
        if(sorts.size() == 0){
            List<TemplateSort> sss = EvidenceApplication.db.findAllByWhere(TemplateSort.class,"caseId = \"" + mCaseId + "\"","sort desc");
            TemplateSort sort = new TemplateSort();
            sort.setCaseId(mCaseId);
            sort.setId(ViewUtil.getUUid());
            sort.setSiNeedRec(true);
            sort.setFatherKey(key);
            sort.setFatherValue(ViewUtil.getFragementName(key));
            if(sss.size() == 0){
                sort.setSort(0);
            }else {
                sort.setSort(sss.get(0).getSort() + 1);
            }
            EvidenceApplication.db.save(sort);
        }
    }

    private void getEvidenceDialogValues(){
        List<SysAppParamSetting> paramSettings = EvidenceApplication.db.findAllByWhere(SysAppParamSetting.class,
                "key = 'describing-point_types'");
        if(paramSettings.size() > 0){
            SysAppParamSetting paramSetting = paramSettings.get(0);
            String value = paramSetting.getValue();
            if(value != null && !"".equals(value)) {
                mEvidenceDialogValues = value;
            }
        }
    }

    private void saveAllExtra2Json() {
        Log.e("jiu", "saveAllExtra2Json()");
        for(SceneFileConjunction sfc : mSceneCon) {
            Log.e("jiu", "childId : " + sfc.getChildAttachmentId());
            EvidenceExtra extra = getEvidenceExtra(sfc.getChildAttachmentId());
            save2Json(extra);
        }
    }

    private void save2Json(EvidenceExtra evidenceExtra) {
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(evidenceExtra.getCaseId(), "" + evidenceExtra.getFather() + evidenceExtra.getId());
        try {
            JSONObject jsonObject = new JSONObject(evidenceExtra.getJson());
            jsonObject.put("ID", evidenceExtra.getId());
            jsonObject.put("SCENE_TYPE", evidenceExtra.getFather());
            dataTemp.setDataType("scene_investigation_data");
            saveRecFile(evidenceExtra, jsonObject);
            dataTemp.setData(jsonObject.toString());
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveRecFile(EvidenceExtra evidenceExtra, JSONObject jsonObject) throws JSONException {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection()+ "'");
        if (list == null || list.size() == 0)
            return;
        String refKey = jsonObject.getString("ID");
        for (RecordFileInfo recordFileInfo : list) {
            if ("png".equals(recordFileInfo.getFileType())) {
                String lastId = null;
                try {
                    lastId = jsonObject.getString("ATTACHMENT_ID");
                    jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            try {
                DataTemp recDataTemp = recordFileInfo.isAddRec() ?
                        SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData"):
                        SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                recObject.put("refKeyId", !TextUtils.isEmpty(refKey) ? refKey : "");
                recObject.put("type", ViewUtil.getType(recordFileInfo));
                recObject.put("sceneType", recordFileInfo.getFather());
                recDataTemp.setDataType("common_attachment");
                recDataTemp.setData(recObject.toString());
//                jsonObject.put("EVIDENCE_PHOTO_ID",jsonObject.get("ATTACHMENT_ID"));
                EvidenceApplication.db.update(recDataTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
