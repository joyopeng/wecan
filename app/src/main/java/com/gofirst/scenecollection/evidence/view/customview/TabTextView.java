package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;

/**
 * @author maxiran
 */
public class TabTextView extends TextView{
    private boolean isSelect = false;

    public String isSelect() {
        return isSelect ? getText().toString() : null;
    }

    private OnNextClickListener listener;
    public TabTextView(Context context) {
        super(context);
        initClickListener();
    }

    public TabTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClickListener();
    }

    public TabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initClickListener();
    }


    public void setIsSelect(boolean isSelect){
        if (!isSelect){
            setBackground(getResources().getDrawable(R.drawable.rect_gray));
            setTextColor(Color.BLACK);
        }else {
            setBackground(getResources().getDrawable(R.drawable.rect_blue));
            setTextColor(Color.WHITE);
        }
        this.isSelect = isSelect;
    }
    public class stateChangeListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (isSelect){
                setBackground(getResources().getDrawable(R.drawable.rect_gray));
                setTextColor(Color.BLACK);
            }else {
                setBackground(getResources().getDrawable(R.drawable.rect_blue));
                setTextColor(Color.WHITE);
            }
            isSelect = !isSelect;
            if (listener != null)
               listener.OnClick(v,isSelect);
        }
    }

    private void initClickListener(){
        setOnClickListener(new stateChangeListener());
    }

    public interface OnNextClickListener{
        void OnClick(View view,boolean isSelect);
    }

    public void setOnNextClickListener(OnNextClickListener listener){
        this.listener = listener;
    }

    public void setSelectAways(){
       setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(getContext(),"此选项为必选项",Toast.LENGTH_SHORT).show();
           }
       });
        setBackground(getResources().getDrawable(R.drawable.rect_blue));
        setTextColor(Color.WHITE);
    }
}
