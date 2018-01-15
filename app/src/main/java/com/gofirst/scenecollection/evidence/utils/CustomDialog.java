package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by maxiran on 2016/5/9.
 */
public class CustomDialog {

    public PopupWindow dialog;
    private TextView content;
    private TextView ensure,cancel;
    private int hei;

    public CustomDialog(Context context) {
        hei = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,context.getResources().getDisplayMetrics());
        View view = LayoutInflater.from(context).inflate(R.layout.contact_dialog,null);
        dialog = new PopupWindow(view,31*hei ,19*hei);
        content = (TextView)view.findViewById(R.id.content);
        ensure = (TextView)view.findViewById(R.id.ensure);
        cancel = (TextView)view.findViewById(R.id.cancel);


    }

    public void setContentAndListener(String content,View parent,View.OnClickListener ensureListener,View.OnClickListener cancelListener){
        this.content.setText(content);
        ensure.setOnClickListener(ensureListener);
        cancel.setOnClickListener(cancelListener);
        dialog.setOutsideTouchable(false);
        dialog.showAtLocation(parent, Gravity.CENTER,0,0);
    }
}
