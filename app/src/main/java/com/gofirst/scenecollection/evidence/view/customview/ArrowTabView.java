
package com.gofirst.scenecollection.evidence.view.customview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

public class ArrowTabView extends TextView {
    private boolean isArrowUp;
    private ArrowChangeListener listener;
    private float currentAngle = 0;
    private int currentLevel;
    private String parentKey;
    public ArrowTabView(Context context) {
        super(context);
        refreshView();
        initListener();
    }

    public ArrowTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshView();
        initListener();
    }

    public ArrowTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshView();
        initListener();
    }

    private void refreshView() {
        setBackgroundResource(isArrowUp ? R.drawable.up_arrow : R.drawable.down_arrow);
    }

    private void initListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isArrowUp = !isArrowUp;
                smoothRotateArrow();
            }
        });
    }

    private void smoothRotateArrow(){
        setPivotX(getWidth()/2);
        setPivotY(getHeight()/2);
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", currentAngle,currentAngle+=180);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimation.setDuration(300);
        rotateAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null)
                    listener.onArrowDirectChange(isArrowUp,currentLevel,parentKey);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        rotateAnimation.start();
    }
    public void setListener(ArrowChangeListener listener) {
        this.listener = listener;
    }

    public interface ArrowChangeListener {
        void onArrowDirectChange(boolean isArrowUp,int currentLevel,String parentKey);
    }

    public void changState(){
        isArrowUp = !isArrowUp;
        smoothRotateArrow();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }
}