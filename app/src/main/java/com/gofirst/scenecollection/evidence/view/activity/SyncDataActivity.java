package com.gofirst.scenecollection.evidence.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.NoScrollViewPager;
import com.gofirst.scenecollection.evidence.view.fragment.SyncDataDownloadFragment;
import com.gofirst.scenecollection.evidence.view.fragment.SyncDataUploadFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsh on 2016/10/9.
 */
public class SyncDataActivity extends FragmentActivity implements View.OnClickListener{

    public SharePre mShare;
    private TextView mTitleText;
    private ImageView mBackImgView;

    private TextView mShowDownloadBtn;
    private TextView mShowUpLoadBtn;
    private NoScrollViewPager mViewPager;
    private Fragment mSyncDataDownload,mSyncDataUpload;

    private final int SYNC_DATA_DOWNLOAD_VIEW = 0;
    private final int SYNC_DATA_UPLOAD_VIEW = 1;
    private int mCurrentTab = SYNC_DATA_DOWNLOAD_VIEW;
    private List<Fragment> mFragmentViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShare = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        String showViewItem = getIntent().getStringExtra("show_view_item");
        if(showViewItem != null && "2".equals(showViewItem)){
            mCurrentTab = SYNC_DATA_UPLOAD_VIEW;
        }else{
            mCurrentTab = SYNC_DATA_DOWNLOAD_VIEW;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sync_data_layout);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secondary_back_img:
                finish();
                break;
            case R.id.sync_data_download_btn:
                mViewPager.setCurrentItem(SYNC_DATA_DOWNLOAD_VIEW);
                break;
            case R.id.sync_data_upload_btn:
                mViewPager.setCurrentItem(SYNC_DATA_UPLOAD_VIEW);
                break;
        }
    }

    private void initView(){
        mTitleText = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleText.setText("数据同步");
        mBackImgView = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImgView.setOnClickListener(this);
        mShowDownloadBtn = (TextView)findViewById(R.id.sync_data_download_btn);
        mShowDownloadBtn.setOnClickListener(this);
        mShowUpLoadBtn = (TextView) findViewById(R.id.sync_data_upload_btn);
        mShowUpLoadBtn.setOnClickListener(this);
        mViewPager = (NoScrollViewPager) findViewById(R.id.sync_data_view_pager);
        mSyncDataDownload = new SyncDataDownloadFragment();
        mSyncDataUpload = new SyncDataUploadFragment();
        mFragmentViews.add(mSyncDataDownload);
        mFragmentViews.add(mSyncDataUpload);
        mViewPager.setAdapter(new SyncDataFragmentView(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mCurrentTab);
        updateTableViewState(mCurrentTab);
    }

    private class SyncDataFragmentView extends FragmentStatePagerAdapter {

        public SyncDataFragmentView(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentViews.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentViews.size();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            int currentItem = mViewPager.getCurrentItem();
            if(mCurrentTab == currentItem){
                return;
            }
            mCurrentTab = currentItem;
            updateTableViewState(currentItem);
        }
    }

    private void updateTableViewState(int currentItem){
        switch (currentItem){
            case SYNC_DATA_DOWNLOAD_VIEW:
                mShowDownloadBtn.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mShowUpLoadBtn.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                break;
            case SYNC_DATA_UPLOAD_VIEW:
                mShowDownloadBtn.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mShowUpLoadBtn.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                break;
        }
    }
}
