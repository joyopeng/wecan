package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.customview.GridViewInScrollView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsh on 2016/7/16.
 */
public class ShapeFileSelectActivity extends Activity implements View.OnClickListener{

    private final String TAG = "ShapeFileSelectActivity";
    private View mShapeSelectView;
    private ImageView mBackImage;

    private GridViewInScrollView mUnClassGrid;
    private ShapeGridAdapter mUnClassAdapter;
    private List<RecordFileInfo> mUnClassFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mUnClassMap = new HashMap<>();
    private GridViewInScrollView mSummaryGrid;
    private ShapeGridAdapter mSummaryAdapter;
    private List<RecordFileInfo> mSummaryFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mSummaryMap = new HashMap<>();
    private GridViewInScrollView mFocusGrid;
    private ShapeGridAdapter mFocusAdapter;
    private List<RecordFileInfo> mFocusFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mFocusMap = new HashMap<>();
    private GridViewInScrollView mDetailGrid;
    private ShapeGridAdapter mDetailAdapter;
    private List<RecordFileInfo> mDetailFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mDetailMap = new HashMap<>();
    private GridViewInScrollView mPositionGrid;
    private ShapeGridAdapter mPositionAdapter;
    private List<RecordFileInfo> mPositionFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mPositionMap = new HashMap<>();
    private GridViewInScrollView mOtherGrid;
    private ShapeGridAdapter mOtherAdapter;
    private List<RecordFileInfo> mOtherFiles = new ArrayList<>();
    private HashMap<String,RecordFileInfo> mOtherMap = new HashMap<>();

    private Map<Integer,String> mShowFiles = new HashMap<Integer, String>();
    private UpdateFilesThread mUpdateThread = null;
    private String mCaseId = "";
    private String mFather = "";
    private String mSection = "";
    private String mPicId = "";

    private final int MSG_DIMISS_DIALOG = 18;
    private final int MSG_FINISH_ACTIVITY = 19;

    private Handler mUpdateViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FINISH_ACTIVITY:
                    finish();
                    break;
                case MSG_DIMISS_DIALOG:
                    Utils.stopProgressDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shape_file_select_layout);
        mCaseId = getIntent().getStringExtra("caseId");
        mFather = getIntent().getStringExtra("father");
        mSection = getIntent().getStringExtra("section");
        mPicId = getIntent().getStringExtra("pic_id");
        Log.i("zhangsh","ShapeFileSelectActivity onCreate mCaseId = " + mCaseId + ";mFather = " + mFather + ";mSection = " + mSection + ";mPicId = " + mPicId);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shape_select_layout:
                /*mUnClassMap = mUnClassAdapter.getSelectShapes();
                mSummaryMap = mSummaryAdapter.getSelectShapes();
                mFocusMap = mFocusAdapter.getSelectShapes();
                mDetailMap = mDetailAdapter.getSelectShapes();
                mPositionMap = mPositionAdapter.getSelectShapes();*/
                //Utils.startProgressDialog(ShapeFileSelectActivity.this,"数据处理","数据处理中...",false,false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mUnClassMap = mUnClassAdapter.getSelectShapes();
                        Iterator<RecordFileInfo> iterators = mUnClassMap.values().iterator();
                        RecordFileInfo info = null;
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        mSummaryMap = mSummaryAdapter.getSelectShapes();
                        iterators = mSummaryMap.values().iterator();
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        mFocusMap = mFocusAdapter.getSelectShapes();
                        iterators = mFocusMap.values().iterator();
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        mDetailMap = mDetailAdapter.getSelectShapes();
                        iterators = mDetailMap.values().iterator();
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        mPositionMap = mPositionAdapter.getSelectShapes();
                        iterators = mPositionMap.values().iterator();
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        mOtherMap = mOtherAdapter.getSelectShapes();
                        iterators = mOtherMap.values().iterator();
                        while (iterators.hasNext()){
                            info = iterators.next();
                            info.setSection(mSection);
                            EvidenceApplication.db.update(info);
                        }
                        //Utils.stopProgressDialog();
                        mUpdateViewHandler.sendEmptyMessageDelayed(MSG_FINISH_ACTIVITY,100);
                    }
                }).start();
                break;
            case R.id.secondary_back_img:
                finish();
                break;
        }
    }

    void initView(){
        mShapeSelectView = findViewById(R.id.shape_select_layout);
        mShapeSelectView.setOnClickListener(this);
        mBackImage = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImage.setOnClickListener(this);
        mUnClassGrid = (GridViewInScrollView) findViewById(R.id.unclass_pic_gridview);
        mSummaryGrid = (GridViewInScrollView)findViewById(R.id.summary_pic_gridview);
        mFocusGrid = (GridViewInScrollView) findViewById(R.id.focus_pic_gridview);
        mDetailGrid = (GridViewInScrollView)findViewById(R.id.detail_pic_gridview);
        mPositionGrid = (GridViewInScrollView)findViewById(R.id.position_pic_gridview);
        mOtherGrid = (GridViewInScrollView)findViewById(R.id.other_pic_gridview);
        new UpdateFilesThread(this).start();
        //Utils.startProgressDialog(this,"初始化","正在初始化...",false,false);
        Utils.startProgressDialog(this,"初始化","正在初始化...",false,true);
    }

    private class UpdateFilesThread extends Thread{
        private Context ctx = null;
        private String type = "all";
        public UpdateFilesThread(Context context){
            this(context,"all");
        }
        public UpdateFilesThread(Context context,String type) {
            this.ctx = context;
            this.type = type;
        }

        @Override
        public void run() {
            Log.i("zhangsh","ShapeFileSelectActivity run start");
            //mUnClassFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and caseId = \"" + mCaseId + "\" and (belongTo is null or belongTo = \"\") and (section is null or section = \"\")");
            //mSummaryFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and caseId = \"" + mCaseId + "\" and belongTo = \"2\" and (section is null or section = \"\")");
            //mFocusFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and caseId = \"" + mCaseId + "\" and belongTo = \"3\" and (section is null or section = \"\")");
            //mDetailFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and caseId = \"" + mCaseId + "\" and belongTo = \"4\" and (section is null or section = \"\")");
            //mPositionFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and caseId = \"" + mCaseId + "\" and belongTo = \"9\" and (section is null or section = \"\")");
            mUnClassFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and (photoType is null or photoType = \"\") and (section is null or section = \"\")");
            mSummaryFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and photoType = \"2\" and (section is null or section = \"\")");
            mFocusFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and photoType = \"3\" and (section is null or section = \"\")");
            mDetailFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and photoType = \"4\" and (section is null or section = \"\")");
            mPositionFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and photoType = \"1\" and (section is null or section = \"\")");
            mOtherFiles = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = \"png\" and id != \"" + mPicId + "\" and caseId = \"" + mCaseId +
                    "\" and father = \"SCENE_PHOTO\" and photoType = \"9\" and (section is null or section = \"\")");
            mUnClassAdapter = new ShapeGridAdapter(mUnClassFiles);
            mUnClassGrid.setAdapter(mUnClassAdapter);
            mSummaryAdapter = new ShapeGridAdapter(mSummaryFiles);
            mSummaryGrid.setAdapter(mSummaryAdapter);
            mFocusAdapter = new ShapeGridAdapter(mFocusFiles);
            mFocusGrid.setAdapter(mFocusAdapter);
            mDetailAdapter = new ShapeGridAdapter(mDetailFiles);
            mDetailGrid.setAdapter(mDetailAdapter);
            mPositionAdapter = new ShapeGridAdapter(mPositionFiles);
            mPositionGrid.setAdapter(mPositionAdapter);
            mOtherAdapter = new ShapeGridAdapter(mOtherFiles);
            mOtherGrid.setAdapter(mOtherAdapter);
            mUpdateViewHandler.sendEmptyMessageDelayed(MSG_DIMISS_DIALOG,100);
            Log.i("zhangsh","ShapeFileSelectActivity run end");
        }
    }

    //列表适配器类
    private class ShapeGridAdapter extends BaseAdapter{

        private List<RecordFileInfo> mSourceShapes = new ArrayList<>();
        private HashMap<String,RecordFileInfo> mResultShapes = new HashMap<>();

        public ShapeGridAdapter(List<RecordFileInfo> shapes) {
            this.mSourceShapes = shapes;
        }

        @Override
        public int getCount() {
            return mSourceShapes.size();
        }

        @Override
        public Object getItem(int position) {
            return mSourceShapes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final RecordFileInfo info = mSourceShapes.get(position);
            final String key = info.getId();
            View layout = null;
            ImageView imgView = null;
            if(convertView == null){
                layout = getLayoutInflater().inflate(R.layout.shape_file_select_item_layout,null);
            }else{
                layout = convertView;
            }
            imgView = (ImageView) layout.findViewById(R.id.img_file_select_show);
            imgView.setImageBitmap(getBitmapFromFile(AppPathUtil.getDataPath() + "/" + info.getTwoHundredFilePath()));
            ImageView imgCheck = (ImageView) layout.findViewById(R.id.img_file_select_check);
            if(mResultShapes.get(key) == null){
                imgCheck.setBackgroundResource(R.drawable.shape_file_select_false);
            }else{
                imgCheck.setBackgroundResource(R.drawable.shape_file_select_true);
            }
            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mResultShapes.get(key) == null) {
                        mResultShapes.put(key, info);
                    }else{
                        mResultShapes.remove(key);
                    }
                    notifyDataSetChanged();
                }
            });
            return layout;
        }

        public HashMap<String,RecordFileInfo> getSelectShapes(){
            return mResultShapes;
        }
    }

    private Bitmap getBitmapFromFile(String path){
        Bitmap bp = BitmapFactory.decodeFile(path);
        int width = bp.getWidth();
        int height = bp.getHeight();
        Bitmap result = null;
        try {
            if (width >= height) {
                result = Bitmap.createBitmap(bp, (width - height) / 2, 0, height, height);
            } else {
                result = Bitmap.createBitmap(bp, 0, (height - width) / 2, width, width);
            }
        }catch (Exception e){
            Log.i(TAG,"get bitmap file exception",e);
            return  null;
        }finally {
            bp.recycle();
        }
        return result;
    }
}
