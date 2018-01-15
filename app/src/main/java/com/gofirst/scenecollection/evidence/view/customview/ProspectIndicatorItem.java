package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author maxiran
 */
public class ProspectIndicatorItem extends View {

    private int startX;
    private int halfHeight;
    private int oneDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
    private float circleRadius;
    private Paint LinePaint = new Paint();
    private Paint LinePaint1 = new Paint();
    private Paint CirclePaint = new Paint();
    private boolean isFinish;
    private boolean isSelect;
    private int orangeColor = Color.parseColor("#FF9853");
    private int grayColor = Color.parseColor("#909090");

    public ProspectIndicatorItem(Context context) {
        super(context);
        init();
    }

    public ProspectIndicatorItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProspectIndicatorItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setIsFinish(boolean isFinish){
        this.isFinish = isFinish;
        LinePaint.setColor(isSelect ? orangeColor : grayColor);
        CirclePaint.setColor(isSelect ? orangeColor : grayColor);
        CirclePaint.setStyle(isFinish ? Paint.Style.FILL : isSelect ? Paint.Style.FILL : Paint.Style.STROKE);
        invalidate();
    }

    public void setIsSelect(boolean isSelect){
        this.isSelect = isSelect;
        LinePaint.setColor(isSelect ? orangeColor : grayColor);
        CirclePaint.setColor(isSelect ? orangeColor : grayColor);
        CirclePaint.setStyle(isSelect ? Paint.Style.FILL : isFinish ? Paint.Style.FILL : Paint.Style.STROKE);
        invalidate();
    }

    public void setBottomSelect(boolean isBottom){
        LinePaint1.setColor(isBottom ? orangeColor : grayColor);
        invalidate();
    }
    private void init(){
        LinePaint.setColor(grayColor);
        LinePaint1.setColor(grayColor);
        LinePaint.setStrokeWidth(oneDip);
        LinePaint1.setStrokeWidth(oneDip);
        CirclePaint.setStyle(Paint.Style.STROKE);
        CirclePaint.setColor(grayColor);
        CirclePaint.setStrokeWidth(oneDip);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec , heightMeasureSpec);
        startX = getMeasuredWidth()/4;
        halfHeight = getMeasuredHeight()/2;
        circleRadius = 3 * oneDip;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(startX,0,startX,halfHeight - circleRadius,LinePaint);
        canvas.drawCircle(startX,halfHeight,circleRadius,CirclePaint);
        canvas.drawLine(startX,halfHeight + circleRadius,startX,halfHeight * 2,LinePaint1);
    }
}
