package com.gofirst.scenecollection.evidence.utils.support;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Utils;


/**
 * Created by Administrator on 2016/7/7.
 */
public class AddFileDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private clickListener mClickListener = null;

    private View mLayoutScane;
    private View mLayoutPhoto;
    private View mLayoutRecord;
    private View mLayoutText;
    private View mLayoutDelete;

    public interface  clickListener{
        public void captureClick();
        public void recordClick();
        public void scanClick();
        public void saveTextClick();
        public void deleteShape();
    }

    public AddFileDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AddFileDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected AddFileDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_add_scan:
                mClickListener.scanClick();
                break;
            case R.id.layout_add_photo:
                mClickListener.captureClick();
                break;
            case R.id.layout_add_record:
                mClickListener.recordClick();
                break;
            case R.id.layout_add_text:
                mClickListener.saveTextClick();
                break;
            case R.id.layout_add_delete:
                mClickListener.deleteShape();
                break;
            default:
                break;
        }
    }

    private void initDialog(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_layout,null);
        setContentView(view);

        mLayoutScane = view.findViewById(R.id.layout_add_scan);
        mLayoutScane.setOnClickListener(this);
        mLayoutPhoto = view.findViewById(R.id.layout_add_photo);
        mLayoutPhoto.setOnClickListener(this);
        mLayoutRecord = view.findViewById(R.id.layout_add_record);
        mLayoutRecord.setOnClickListener(this);
        mLayoutText = view.findViewById(R.id.layout_add_text);
        mLayoutText.setOnClickListener(this);
        mLayoutDelete = view.findViewById(R.id.layout_add_delete);
        mLayoutDelete.setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dsp = mContext.getResources().getDisplayMetrics();
        lp.width = Utils.dp2Px(mContext,455);
        lp.height = Utils.dp2Px(mContext,96);
        dialogWindow.setAttributes(lp);
    }

    /**
     *
     * */
    public void setClickListener(clickListener listener){
        this.mClickListener = listener;
    }
}
