package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;


/**
 * Created by Administrator on 2017/5/15.
 */

public class ToggleButton extends ImageView {

    private boolean isFlashOn;
    private TriggerListener listener;

    public void setListener(TriggerListener listener) {
        this.listener = listener;
    }

    public ToggleButton(Context context) {
        super(context);
        init();
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setTriggerImage(isFlashOn);
        setImageResource(R.drawable.flash_on);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFlashOn = !isFlashOn;
                setTriggerImage(isFlashOn);
                if (listener != null)
                    listener.onTrigger(isFlashOn);
            }
        });
    }

    public interface TriggerListener{
        void onTrigger(boolean isFlashOn);
    }

    public void setTriggerImage(boolean isFlashOn){
        setImageResource(isFlashOn ? R.drawable.flash_off : R.drawable.flash_on);
        this.isFlashOn = isFlashOn;
    }
}
