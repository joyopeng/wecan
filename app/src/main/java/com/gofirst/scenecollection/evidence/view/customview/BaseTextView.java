
package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/6/8.
 */

public class BaseTextView  extends LinearLayout {
    private ImageView mIv;
    private TextView mTv;
    private EditText mEt;
    private LinearLayout linearLayout;

    public BaseTextView(Context context) {
        this(context,null);
        initLayout(context);
    }
    public BaseTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }


    private void initLayout(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.base_textview, this, true);
        mIv = (ImageView)view.findViewById(R.id.iv);
        mTv = (TextView)view.findViewById(R.id.tv);
        mEt = (EditText)view.findViewById(R.id.et);
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
    public void setEditView(String text)
    {
        mEt.setText(text);
    }
    public String getEditView(){
        return mEt.getText().toString();
    }

    public void setIsEdit(Boolean isEdit){
        if(isEdit){
            mEt.setEnabled(true);
        }else{
            mEt.setEnabled(false);
        }
    }

    public void setIsExist(Boolean isExist){
        if(isExist){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }

    }

}

