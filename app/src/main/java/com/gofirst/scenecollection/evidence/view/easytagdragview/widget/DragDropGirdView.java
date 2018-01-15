/*
 * Copyright 2015 - 2016 solartisan/imilk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gofirst.scenecollection.evidence.view.easytagdragview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.GridView;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.easytagdragview.listener.OnDragDropListener;

import java.lang.reflect.Field;


public class DragDropGirdView extends GridView implements OnDragDropListener,
        DragDropController.DragItemContainer {

    public static final String LOG_TAG = DragDropGirdView.class.getSimpleName();
    public static final String DRAG_FAVORITE_TILE = "FAVORITE_TILE";
    private float mTouchSlop;
    private boolean mIsFixedHeight;
    private int mTopScrollBound;
    private int mBottomScrollBound;
    private int mLastDragY;
    private Handler mScrollHandler;
    private final long SCROLL_HANDLER_DELAY_MILLIS = 5;
    private final int DRAG_SCROLL_PX_UNIT = 25;
    private boolean mIsDragScrollerRunning = false;
    private int mTouchDownForDragStartX;
    private int mTouchDownForDragStartY;
    private Bitmap mDragShadowBitmap;
    private ImageView mDragShadowOverlay;
    private View mDragShadowParent;
    private int mAnimationDuration;
    final int[] mLocationOnScreen = new int[2];
    private int mTouchOffsetToChildLeft;
    private int mTouchOffsetToChildTop;
    private int mDragShadowLeft;
    private int mDragShadowTop;

    private DragDropController mDragDropController = new DragDropController(this);

    private final float DRAG_SHADOW_ALPHA = 0.7f;
    private final float BOUND_GAP_RATIO = 0.2f;

    private final Runnable mDragScroller = new Runnable() {
        @Override
        public void run() {
            if (mLastDragY <= mTopScrollBound) {
                smoothScrollBy(-DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            } else if (mLastDragY >= mBottomScrollBound) {
                smoothScrollBy(DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            }
            mScrollHandler.postDelayed(this, SCROLL_HANDLER_DELAY_MILLIS);
        }
    };

    private final AnimatorListenerAdapter mDragShadowOverAnimatorListener =
            new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mDragShadowBitmap != null) {
                        mDragShadowBitmap.recycle();
                        mDragShadowBitmap = null;
                    }
                    mDragShadowOverlay.setVisibility(GONE);
                    mDragShadowOverlay.setImageBitmap(null);
                }
            };

    public DragDropGirdView(Context context) {
        this(context, null);
    }

    public DragDropGirdView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DragDropGirdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DragDropGirdView);
        mIsFixedHeight = ta.getBoolean(R.styleable.DragDropGirdView_fixed_type, false);
        mAnimationDuration = ta.getInteger(R.styleable.DragDropGirdView_anim_duration, context.getResources().getInteger(R.integer.fade_duration));
        ta.recycle();
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mDragDropController.addOnDragDropListener(this);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownForDragStartX = (int) ev.getX();
            mTouchDownForDragStartY = (int) ev.getY();
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        final int eX = (int) event.getX();
        final int eY = (int) event.getY();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (!DRAG_FAVORITE_TILE.equals(event.getLocalState())) {
                    return false;
                }
                if (!mDragDropController.handleDragStarted(eX, eY)) {
                    //True if the drag is started, false if the drag is cancelled for some reason.
                    return false;
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                mLastDragY = eY;
                mDragDropController.handleDragHovered(this, eX, eY);
                if (!mIsDragScrollerRunning &&
                        (Math.abs(mLastDragY - mTouchDownForDragStartY) >= 4 * mTouchSlop)) {
                    mIsDragScrollerRunning = true;
                    ensureScrollHandler();
                    //开始执行滑动
                    mScrollHandler.postDelayed(mDragScroller, SCROLL_HANDLER_DELAY_MILLIS);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                final int boundGap = (int) (getHeight() * BOUND_GAP_RATIO);
                mTopScrollBound = (getTop() + boundGap);
                mBottomScrollBound = (getBottom() - boundGap);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
            case DragEvent.ACTION_DROP:
                ensureScrollHandler();
                mScrollHandler.removeCallbacks(mDragScroller);
                mIsDragScrollerRunning = false;
                // Either a successful drop or it's ended with out drop.
                if (action == DragEvent.ACTION_DROP || action == DragEvent.ACTION_DRAG_ENDED) {
                    mDragDropController.handleDragFinished(eX, eY, false);
                }
                break;
            default:
                break;
        }
        // This ListView will consume the drag events on behalf（维护） of its children.
        return true;
    }

    public void setDragShadowOverlay(ImageView overlay) {
        mDragShadowOverlay = overlay;
        mDragShadowParent = (View) mDragShadowOverlay.getParent();
    }

    private void ensureScrollHandler() {
        if (mScrollHandler == null) {
            mScrollHandler = getHandler();
        }
    }

    public DragDropController getDragDropController() {
        return mDragDropController;
    }

    @Override
    public void onDragStarted(int x, int y, View tileView) {
        if (mDragShadowOverlay == null) {
            return;
        }
        mDragShadowOverlay.clearAnimation();
        mDragShadowBitmap = createDraggedChildBitmap(tileView);
        if (mDragShadowBitmap == null) {
            return;
        }

        tileView.getLocationOnScreen(mLocationOnScreen);
       /* int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getContext().getResources().getDisplayMetrics());*/
        mDragShadowLeft = mLocationOnScreen[0];
        /*mLocationOnScreen[1] -= margin;*/
        mDragShadowTop = mLocationOnScreen[1];

        // To offset the drag shadow position, this looks under finger.
        mTouchOffsetToChildLeft = x - mDragShadowLeft;
        mTouchOffsetToChildTop = y - mDragShadowTop;

        mDragShadowParent.getLocationOnScreen(mLocationOnScreen);
        mDragShadowLeft -= mLocationOnScreen[0];
       /* mLocationOnScreen[1] -= margin;*/
        mDragShadowTop -= mLocationOnScreen[1];

        mDragShadowOverlay.setImageBitmap(mDragShadowBitmap);
        mDragShadowOverlay.setVisibility(VISIBLE);
        mDragShadowOverlay.setAlpha(DRAG_SHADOW_ALPHA);

        mDragShadowOverlay.setX(mDragShadowLeft);
        mDragShadowOverlay.setY(mDragShadowTop);
    }

    @Override
    public void onDragHovered(int x, int y, View tileView) {
        // Update the drag shadow location.
      /* int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getContext().getResources().getDisplayMetrics());
        y = y + margin;*/
        mDragShadowParent.getLocationOnScreen(mLocationOnScreen);
        mDragShadowLeft = x - mTouchOffsetToChildLeft - mLocationOnScreen[0];
        mDragShadowTop = y - mTouchOffsetToChildTop - mLocationOnScreen[1];
        // Draw the drag shadow at its last known location if the drag shadow exists.
        if (mDragShadowOverlay != null) {
            mDragShadowOverlay.setX(mDragShadowLeft);
            mDragShadowOverlay.setY(mDragShadowTop);
        }
        Log.d("x",x+"");
        Log.d("y",y+"");
        Log.d("mDragShadowLeft",mDragShadowLeft+"");
        Log.d("mDragShadowTop",mDragShadowTop+"");
    }

    @Override
    public void onDragFinished(int x, int y) {
        if (mDragShadowOverlay != null) {
            mDragShadowOverlay.clearAnimation();
            mDragShadowOverlay.animate().alpha(0.0f)
                    .setDuration(mAnimationDuration)
                    .setListener(mDragShadowOverAnimatorListener)
                    .start();
        }
    }

    @Override
    public void onDroppedOnRemove() {
        //TODO REMOVE
    }

    private Bitmap createDraggedChildBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        final Bitmap cache = view.getDrawingCache();

        Bitmap bitmap = null;
        if (cache != null) {
            try {
                bitmap = cache.copy(Bitmap.Config.ARGB_8888, false);
            } catch (final OutOfMemoryError e) {
                Log.w(LOG_TAG, "Failed to copy bitmap from Drawing cache", e);
                bitmap = null;
            }
        }

        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }

    @Override
    public View getViewForLocation(int x, int y) {
        // 获取GridView绝对坐标
        getLocationOnScreen(mLocationOnScreen);
        // 计算相对坐标
        final int viewX = x - mLocationOnScreen[0];
        final int viewY = y - mLocationOnScreen[1];
        return getViewAtPosition(viewX, viewY);
    }

    /**
     * Find the view under the pointer.
     */
    private View getViewAtPosition(int x, int y) {
        final int count = getChildCount();
        View child;
        y = y + getStatusBarHeight() + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,91,getResources().getDisplayMetrics());
        for (int childIdx = 0; childIdx < count; childIdx++) {
            child = getChildAt(childIdx);
            if (y >= child.getTop() && y <= child.getBottom() && x >= child.getLeft()
                    && x <= child.getRight()) {
                return child;
            }
        }
        return null;
    }

    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsFixedHeight) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }
}
