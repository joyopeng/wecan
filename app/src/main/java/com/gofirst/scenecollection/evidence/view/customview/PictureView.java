package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/6/3.
 */
public class PictureView extends LinearLayout {
    public PictureView(Context context) {
        super(context);
        initLayout(context);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);

    }

    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_edit_text, this, true);

    }
}
