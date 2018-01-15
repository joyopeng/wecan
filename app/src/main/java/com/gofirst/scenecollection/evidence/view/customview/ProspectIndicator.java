package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxiran on 2016/4/19.
 */
public class ProspectIndicator extends View {

    private Paint blackPaint = new Paint();
    private float midX;
    private float halfItemYdelta;
    private int position = 5;
    private float circleRadius;
    private List<processInfo> processInfoList = new ArrayList<>();
    private float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.8f, getContext().getResources().getDisplayMetrics());
    public ProspectIndicator(Context context) {
        super(context);
        init();
    }

    public ProspectIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawCircle(canvas);

    }

    public void setIndicatorPosition(int position){
            this.position = position;
            invalidate();

    }

    private void init(){
        circleRadius = 4 * width;
        blackPaint.setStrokeWidth(width);
        blackPaint.setColor(Color.BLACK);
        processInfo process;
        for (int i = 0;i < 14;i++){
            process = new processInfo();
            processInfoList.add(process);
        }
        processInfoList.get(5).setIsFinish(true);
        processInfoList.get(9).setIsFinish(true);
        processInfoList.get(3).setIsFinish(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        midX = getWidth()/3;
        halfItemYdelta = getHeight()/28;
    }

    private void drawLine(Canvas canvas){
        //绘制黄线
        for (int i = 0;i < 14;i++ ){
            processInfoList.get(i).setIsSelect(i==position-1);
            if (i == 0){
                canvas.drawLine(midX,0,midX,halfItemYdelta-circleRadius,processInfoList.get(0).LinePaint);
            }else{
                float startY = (2 * i - 1) * halfItemYdelta + circleRadius;
                float stopY = (2 * i + 1) * halfItemYdelta - circleRadius;
                canvas.drawLine(midX, startY, midX, stopY, processInfoList.get(i).LinePaint);
            }
        }
      //最后一根线
        canvas.drawLine(midX,27*halfItemYdelta+circleRadius,midX,28*halfItemYdelta,blackPaint);
    }

    private void drawCircle(Canvas canvas){
        for (int i = 1; i <= 14;i++){
            canvas.drawCircle(midX, halfItemYdelta * (2*i-1),circleRadius,processInfoList.get(i-1).CirclePaint);

        }
    }

    public class processInfo{
        Paint LinePaint = new Paint();
        Paint CirclePaint = new Paint();
        boolean isFinish;
        public processInfo() {
            //初始化为未完成
            isFinish = false;
            init();
        }

        private void init() {
            //未完成黑色空心
            LinePaint.setColor(Color.parseColor("#909090"));
            LinePaint.setStrokeWidth(width);
            CirclePaint.setStyle(Paint.Style.STROKE);
            CirclePaint.setColor(Color.parseColor("#909090"));
            CirclePaint.setStrokeWidth(width);
        }
        public void setIsSelect(boolean select){
          if (select){
              //选中橙色实心
            LinePaint.setColor(Color.parseColor("#FF9853"));
            CirclePaint.setColor(Color.parseColor("#FF9853"));
            CirclePaint.setStyle(Paint.Style.FILL);
          }else {
            setFinishState();
          }
        }

        public void setIsFinish(boolean isFinish){
            this.isFinish = isFinish;
            setFinishState();
        }

        private void setFinishState(){
            if (isFinish){
                //已完成黑色实心
                LinePaint.setColor(Color.parseColor("#909090"));
                CirclePaint.setColor(Color.parseColor("#909090"));
                CirclePaint.setStyle(Paint.Style.FILL);
            }else {
                init();
            }
        }
    }
}
