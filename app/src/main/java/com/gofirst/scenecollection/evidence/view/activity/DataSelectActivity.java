package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/8/26.
 */
public class DataSelectActivity extends PopupWindow implements View.OnClickListener{
    private final String TAG = "DataSelectActivity";
    private View mMainView;
    private Button mBtnBack;
    private ListView mDataSelectLV;
    private Activity mActivity;

    public void setmListener(BaseAdapter adapter){
        mDataSelectLV.setAdapter(adapter);
    }

    public void setItemClick(AdapterView.OnItemClickListener itemClick){
        //mDataSelectLV.setOnItemSelectedListener(itemClick);
        mDataSelectLV.setOnItemClickListener(itemClick);
    }

    public DataSelectActivity(Activity activity) {
        super(activity);
        mActivity = activity;
        mMainView = LayoutInflater.from(activity).inflate(R.layout.data_select_layout,null);
        mBtnBack = (Button) mMainView.findViewById(R.id.btn_data_select_back);
        mBtnBack.setOnClickListener(this);
        mDataSelectLV = (ListView)mMainView.findViewById(R.id.data_select_listview);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMainView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        ColorDrawable dw = new ColorDrawable(Color.WHITE);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMainView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMainView.findViewById(R.id.data_select_pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        backgroundAlpha(0.8f,mActivity);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1f,mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_data_select_back:
                this.dismiss();
                break;
            default:
                break;
        }
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha,Activity context)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();

        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

}
