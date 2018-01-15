package com.gofirst.scenecollection.evidence.utils.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class ImageMap extends FrameLayout implements ShapeExtension,ShapeExtension.OnShapeActionListener,
        TranslateAnimation.OnAnimationListener {
    private HighlightImageView highlightImageView;
    private Bubble bubble;
    private View viewForAnimation;
    private Context mContext = null;
    //add on
    public static final int STATE_SHOW_PICTURES = 1001;
    public static final int STATE_SHOW_SHAPES = 1002;
    public static final int STATE_ADD_SHAPES = 1005; //add 20161008
    public static final int STATE_SHOW_BUBBLEVIEW = 1003;
    public static final int STATE_SHOW_DELETE_SHAPES = 1004;
    private int mCurrentState = 1001;
    //add off

    public ImageMap(Context context) {
        this(context,null);
    }

    public ImageMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialImageView(context);
    }

    public ImageMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialImageView(context);
    }

    private void initialImageView(Context context){
        //add on
        mContext = context;
        //add off
        highlightImageView = new HighlightImageView(context);
        //add delete on
        //highlightImageView.setOnShapeClickListener(this);
        //add delete off
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        addView(highlightImageView, params);
        viewForAnimation = new View(context);
        addView(viewForAnimation,0,0);
    }

    /**
     * Set a bubble view controller and it's renderDelegate interface.
     * @param bubbleView A view controller object for display on image map.
     * @param renderDelegate The display interface for bubble view controller render.
     */
    public void setBubbleView(View bubbleView,Bubble.RenderDelegate renderDelegate){
        if(bubbleView == null){
            throw new IllegalArgumentException("View for bubble cannot be null !");
        }
        //add on
        if(bubble != null){
            bubble.removeView(bubbleView);
        }
        //add off
        bubble = new Bubble(bubbleView);
        bubble.setRenderDelegate(renderDelegate);
        addView(bubble);
        bubble.view.setVisibility(View.INVISIBLE);
    }

    // 锚点新需求更改 start
    public void setSingleBubbleView(int index,Bubble.RenderDelegate renderDelegate,String tag){
        Bubble bu = null;
        List<Shape> shapes = highlightImageView.getShapes();
        for(Shape item : shapes){
            if(item.getDisplayBubble() == null && item.tag.equals(tag)) {
                View view = ((Activity) mContext).getLayoutInflater().inflate(index, null);
                Log.i("zhangsh","setShapesBubbleView width = " + view.getWidth() + ";height = " + view.getHeight());
                bu = new Bubble(view);
                bu.setRenderDelegate(renderDelegate);
                item.createBubbleRelation(bu);
                addView(bu);
                bu.showAtShapes(item,highlightImageView.getScale(),highlightImageView.getAbsoluteOffset());
            }
        }
        highlightImageView.invalidate();
    }
    // 锚点新需求更改 end
    //add on
    public void setShapesBubbleView(int index,Bubble.RenderDelegate renderDelegate){
        Bubble bu = null;
        List<Shape> shapes = highlightImageView.getShapes();
        for(Shape item : shapes){
            if(item.getDisplayBubble() == null) {
                View view = ((Activity) mContext).getLayoutInflater().inflate(index, null);
                Log.i("zhangsh","setShapesBubbleView width = " + view.getWidth() + ";height = " + view.getHeight());
                bu = new Bubble(view);
                bu.setRenderDelegate(renderDelegate);
                item.createBubbleRelation(bu);
                addView(bu);
                bu.showAtShapes(item,highlightImageView.getScale(),highlightImageView.getAbsoluteOffset());
            }else{
                bu = item.getDisplayBubble();
                bu.setRenderDelegate(renderDelegate);
                item.createBubbleRelation(bu);
                bu.showAtShapes(item,highlightImageView.getScale(),highlightImageView.getAbsoluteOffset());
                bu.setVisibility(View.VISIBLE);
            }
        }
        highlightImageView.invalidate();
    }
    //add off

    /**
     * 添加Shape，并关联到Bubble的位置
     * - Add a shape and set reference to the bubble.
     * @param shape Shape
     */
    public void addShapeAndRefToBubble(final Shape shape){
        //if(mCurrentState != STATE_SHOW_SHAPES){
        if(mCurrentState != STATE_ADD_SHAPES){
            return;
        }
        addShape(shape);
        if(bubble != null){
            shape.createBubbleRelation(bubble);
        }
    }

    @Override
    public void onTranslate (float deltaX, float deltaY) {
        highlightImageView.moveBy(deltaX, deltaY);
    }

    @Override
    public void addShape(Shape shape) {
        //if(mCurrentState != STATE_SHOW_SHAPES){
        if(mCurrentState != STATE_ADD_SHAPES){
            return;
        }
        float scale = highlightImageView.getScale();
        Log.i("zhangsh","ImageMap scale = " + scale);
        shape.onScale(scale);

        // 将图像中心移动到目标形状的中心坐标上
        // Move the center point of the image to the target shape center.
//        PointF from = highlightImageView.getAbsoluteCenter();
//        PointF to = shape.getCenterPoint();
//        TranslateAnimation movingAnimation = new TranslateAnimation(from.x,to.x/4,from.y,to.y);
//        movingAnimation.setOnAnimationListener(this);
//        movingAnimation.setInterpolator(new DecelerateInterpolator());
//        movingAnimation.setDuration(500);
//        movingAnimation.setFillAfter(true);
//        viewForAnimation.startAnimation(movingAnimation);

        PointF offset = highlightImageView.getAbsoluteOffset();
        shape.onTranslate(offset.x , offset.y);
        highlightImageView.addShape(shape);
    }
    //add on
    public void addShapeWithOutScale(Shape shape){
        //if(mCurrentState != STATE_SHOW_SHAPES){
        if(mCurrentState != STATE_ADD_SHAPES){
            return;
        }
        highlightImageView.addShape(shape);
    }
    //add off
    @Override
    public void removeShape(Object tag) {
        if(mCurrentState != STATE_SHOW_DELETE_SHAPES){
            return;
        }
        if(bubble != null && bubble.view.getVisibility() == View.VISIBLE){
            bubble.view.setVisibility(View.INVISIBLE);
        }
        //add on
        Shape shape = highlightImageView.getShape(tag);
        if(shape != null){
            removeView(shape.getDisplayBubble());
            shape.cleanBubbleRelation();
        }
        //add off
        highlightImageView.removeShape(tag);
    }

    @Override
    public void clearShapes() {
        if(mCurrentState != STATE_SHOW_DELETE_SHAPES){
            return;
        }
        for(Shape item : highlightImageView.getShapes()){
            //add on
            removeView(item.getDisplayBubble());
            //add off
            item.cleanBubbleRelation();
        }
        highlightImageView.clearShapes();
        if (bubble != null){
            bubble.view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public final void onShapeClick(Shape shape, float xOnImage, float yOnImage) {
        if(mCurrentState != STATE_SHOW_BUBBLEVIEW && mCurrentState != STATE_SHOW_DELETE_SHAPES){
            return;
        }
        //update on
        /*for(Shape item : highlightImageView.getShapes()){
            item.cleanBubbleRelation();
        }
        if(bubble != null){
            bubble.showAtShape(shape);
        }*/
        List<Shape> shapes = highlightImageView.getShapes();
        for(Shape item : shapes){
            //item.cleanBubbleRelation();
            if(item.getDisplayBubble() != null){
                item.getDisplayBubble().showAtShape(item);
            }
        }
        //update off
    }

    /**
     * set a bitmap for image map.
     * @param bitmap image
     */
    public void setMapBitmap(Bitmap bitmap){
        highlightImageView.setImageBitmap(bitmap);
    }

    //add on
    public List<Shape> getCurrentShapeCache(){
        return highlightImageView.getShapes();
    }

    public void setCurrentState(int state){
        mCurrentState = state;
    }

    public int getCurrentState(){
        return mCurrentState;
    }

    public void setCurrentScale(float scale){
        highlightImageView.setScale(scale);
    }

    public float getCurrentScale(){
        return highlightImageView.getScale();
    }

    public PointF getAbsoluteOffset(){
        return highlightImageView.getAbsoluteOffset();
    }

    public void clearBubbles(){
        for(Shape item : highlightImageView.getShapes()){
            if(item.getDisplayBubble() != null) {
                item.getDisplayBubble().setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setOnTouchAddPosition(TouchImageView.onTouchAddPosition ontouch){
        highlightImageView.setOnTouchAddPosition(ontouch);
    }
    //add off
}
