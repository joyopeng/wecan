package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/7/9.
 */
public class PhotoGridviewAdapter extends BaseAdapter {
    Context context;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            View view = View.inflate(context, R.layout.all_photo_gridview, null);
        }
        return null;
    }
}