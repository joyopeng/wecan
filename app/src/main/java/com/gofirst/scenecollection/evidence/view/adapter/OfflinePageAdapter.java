package com.gofirst.scenecollection.evidence.view.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/9/20.
 */
public class OfflinePageAdapter extends PagerAdapter {

    private ViewPager mContentPager;
    private View mOfflineUnDownloadList;
    private View mOfflineDownloadList;

    public OfflinePageAdapter(ViewPager pager,View unDownload,View download) {
        super();
        this.mContentPager = pager;
        this.mOfflineUnDownloadList = unDownload;
        this.mOfflineDownloadList = download;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        if(position == 0){
            mContentPager.removeView(mOfflineUnDownloadList);
        }else{
            mContentPager.removeView(mOfflineDownloadList);
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       // return super.instantiateItem(container, position);
        if(position == 0){
            mContentPager.addView(mOfflineUnDownloadList);
            return mOfflineUnDownloadList;
        }else{
            mContentPager.addView(mOfflineDownloadList);
            return mOfflineDownloadList;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }
}
