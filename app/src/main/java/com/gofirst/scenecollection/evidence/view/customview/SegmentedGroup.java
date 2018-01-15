package com.gofirst.scenecollection.evidence.view.customview;

/**
 *
 */
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.gofirst.scenecollection.evidence.R.color;
import com.gofirst.scenecollection.evidence.R.dimen;
import com.gofirst.scenecollection.evidence.R.drawable;
import com.gofirst.scenecollection.evidence.R.styleable;

public class SegmentedGroup extends RadioGroup {
    private int mMarginDp;
    private Resources resources = this.getResources();
    private int mTintColor;
    private int mCheckedTextColor = -1;
    private SegmentedGroup.LayoutSelector mLayoutSelector;
    private Float mCornerRadius;

    public SegmentedGroup(Context context) {
        super(context);
        this.mTintColor = this.resources.getColor(color.radio_button_selected_color);
        this.mMarginDp = (int)this.getResources().getDimension(dimen.radio_button_stroke_border);
        this.mCornerRadius = Float.valueOf(this.getResources().getDimension(dimen.radio_button_conner_radius));
        this.mLayoutSelector = new SegmentedGroup.LayoutSelector(this.mCornerRadius.floatValue());
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = this.getContext().getTheme().obtainStyledAttributes(attrs, styleable.SegmentedGroup, 0, 0);

        try {
            this.mMarginDp = (int)typedArray.getDimension(styleable.SegmentedGroup_sc_border_width, this.getResources().getDimension(dimen.radio_button_stroke_border));
            this.mCornerRadius = Float.valueOf(typedArray.getDimension(styleable.SegmentedGroup_sc_corner_radius, this.getResources().getDimension(dimen.radio_button_conner_radius)));
            this.mTintColor = typedArray.getColor(styleable.SegmentedGroup_sc_tint_color, this.getResources().getColor(color.radio_button_selected_color));
            //this.mCheckedTextColor = typedArray.getColor(styleable.SegmentedGroup_sc_checked_text_color, this.getResources().getColor(17170443));
        } finally {
            typedArray.recycle();
        }

    }

    public SegmentedGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTintColor = this.resources.getColor(color.radio_button_selected_color);
        this.mMarginDp = (int)this.getResources().getDimension(dimen.radio_button_stroke_border);
        this.mCornerRadius = Float.valueOf(this.getResources().getDimension(dimen.radio_button_conner_radius));
        this.initAttrs(attrs);
        this.mLayoutSelector = new SegmentedGroup.LayoutSelector(this.mCornerRadius.floatValue());
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.updateBackground();
    }

    public void setTintColor(int tintColor) {
        this.mTintColor = tintColor;
        this.updateBackground();
    }

    public void setTintColor(int tintColor, int checkedTextColor) {
        this.mTintColor = tintColor;
        this.mCheckedTextColor = checkedTextColor;
        this.updateBackground();
    }

    public void updateBackground() {
        int count = super.getChildCount();

        for(int i = 0; i < count; ++i) {
            View child = this.getChildAt(i);
            this.updateBackground(child);
            if(i == count - 1) {
                break;
            }

            LayoutParams initParams = (LayoutParams)child.getLayoutParams();
            LayoutParams params = new LayoutParams(initParams.width, initParams.height, initParams.weight);
            if(this.getOrientation() == LinearLayout.HORIZONTAL) {
                params.setMargins(0, 0, -this.mMarginDp, 0);
            } else {
                params.setMargins(0, 0, 0, -this.mMarginDp);
            }

            child.setLayoutParams(params);
        }

    }

    private void updateBackground(View view) {
        int checked = this.mLayoutSelector.getSelected();
        int unchecked = this.mLayoutSelector.getUnselected();
        ColorStateList colorStateList = new ColorStateList(new int[][]{{16842919}, {-16842919, -16842912}, {-16842919, 16842912}}, new int[]{-7829368, Color.BLACK, this.mCheckedTextColor});
        ((Button)view).setTextColor(colorStateList);
        Drawable checkedDrawable = this.resources.getDrawable(checked).mutate();
        Drawable uncheckedDrawable = this.resources.getDrawable(unchecked).mutate();
        ((GradientDrawable)checkedDrawable).setColor(this.mTintColor);
        ((GradientDrawable)uncheckedDrawable).setColor(Color.parseColor("#F8F8F8"));
        ((GradientDrawable)checkedDrawable).setStroke(this.mMarginDp, this.mTintColor);
        ((GradientDrawable)uncheckedDrawable).setStroke(this.mMarginDp, Color.parseColor("#EEEEEE"));
        ((GradientDrawable)checkedDrawable).setCornerRadii(this.mLayoutSelector.getChildRadii(view));
        ((GradientDrawable)uncheckedDrawable).setCornerRadii(this.mLayoutSelector.getChildRadii(view));
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-16842912}, uncheckedDrawable);
        stateListDrawable.addState(new int[]{16842912}, checkedDrawable);
        if(VERSION.SDK_INT >= 16) {
            view.setBackground(stateListDrawable);
        } else {
            view.setBackgroundDrawable(stateListDrawable);
        }

    }

    private class LayoutSelector {
        private int children;
        private int child;
        private final int SELECTED_LAYOUT;
        private final int UNSELECTED_LAYOUT;
        private float r;
        private final float r1;
        private final float[] rLeft;
        private final float[] rRight;
        private final float[] rMiddle;
        private final float[] rDefault;
        private final float[] rTop;
        private final float[] rBot;
        private float[] radii;

        public LayoutSelector(float cornerRadius) {
            this.SELECTED_LAYOUT = drawable.radio_checked;
            this.UNSELECTED_LAYOUT = drawable.radio_unchecked;
            this.r1 = TypedValue.applyDimension(1, 0.1F, SegmentedGroup.this.getResources().getDisplayMetrics());
            this.children = -1;
            this.child = -1;
            this.r = cornerRadius;
            this.rLeft = new float[]{this.r, this.r, this.r1, this.r1, this.r1, this.r1, this.r, this.r};
            this.rRight = new float[]{this.r1, this.r1, this.r, this.r, this.r, this.r, this.r1, this.r1};
            this.rMiddle = new float[]{this.r1, this.r1, this.r1, this.r1, this.r1, this.r1, this.r1, this.r1};
            this.rDefault = new float[]{this.r, this.r, this.r, this.r, this.r, this.r, this.r, this.r};
            this.rTop = new float[]{this.r, this.r, this.r, this.r, this.r1, this.r1, this.r1, this.r1};
            this.rBot = new float[]{this.r1, this.r1, this.r1, this.r1, this.r, this.r, this.r, this.r};
        }

        private int getChildren() {
            return SegmentedGroup.this.getChildCount();
        }

        private int getChildIndex(View view) {
            return SegmentedGroup.this.indexOfChild(view);
        }

        private void setChildRadii(int newChildren, int newChild) {
            if(this.children != newChildren || this.child != newChild) {
                this.children = newChildren;
                this.child = newChild;
                if(this.children == 1) {
                    this.radii = this.rDefault;
                } else if(this.child == 0) {
                    this.radii = SegmentedGroup.this.getOrientation() == LinearLayout.HORIZONTAL?this.rLeft:this.rTop;
                } else if(this.child == this.children - 1) {
                    this.radii = SegmentedGroup.this.getOrientation() == LinearLayout.HORIZONTAL?this.rRight:this.rBot;
                } else {
                    this.radii = this.rMiddle;
                }

            }
        }

        public int getSelected()
        {
            return this.SELECTED_LAYOUT;
        }

        public int getUnselected() {
            return this.UNSELECTED_LAYOUT;
        }

        public float[] getChildRadii(View view) {
            int newChildren = this.getChildren();
            int newChild = this.getChildIndex(view);
            this.setChildRadii(newChildren, newChild);
            return this.radii;
        }
    }
}

