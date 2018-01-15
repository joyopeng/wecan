package com.gofirst.scenecollection.evidence.view.customview;

import android.widget.GridView;

/**
 * Created by Administrator on 2016/6/29.
 */
public class PhotoGridview extends GridView {
    private boolean needScroll = false;
    public PhotoGridview(android.content.Context context,
                         android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!needScroll){
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}