package com.gofirst.scenecollection.evidence.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/8/3.
 */
public class EmptyAdapter extends BaseAdapter{

    private String hintText;

    public EmptyAdapter(String hintText) {
        this.hintText = hintText;
    }

    @Override
    public int getCount() {
        return 1;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_adapter_layout,parent,false);
        TextView textView = (TextView)view.findViewById(R.id.hint);
        textView.setText(hintText);
        return view;
    }
}
