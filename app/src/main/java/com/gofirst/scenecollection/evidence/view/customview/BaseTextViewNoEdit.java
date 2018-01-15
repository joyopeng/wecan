package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/7/12.
 */
public class BaseTextViewNoEdit extends LinearLayout {
    private ImageView mIv;
    private TextView mTv;
    private LinearLayout linearLayout;

    public BaseTextViewNoEdit(Context context) {
        this(context,null);
        initLayout(context);
    }
    public BaseTextViewNoEdit(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }


    private void initLayout(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.base_textview_noedit, this, true);
        mIv = (ImageView)view.findViewById(R.id.iv);
        mTv = (TextView)view.findViewById(R.id.tv);
        linearLayout=(LinearLayout)view.findViewById(R.id.linearLayout);
    }
    public void setImageResource(int resId)
    {
        mIv.setImageResource(resId);
    }
    public void setTextViewText(String text)//设置名称
    {
        mTv.setText(text);
    }


    public void setIsExist(Boolean isExist){
        if(isExist){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }

    }

}

