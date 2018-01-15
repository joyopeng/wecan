package com.gofirst.scenecollection.evidence.utils.support;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.TracePointActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class SelectEvidenceDialog extends PopupWindow {

    private Context mContext;
    private View mMainView;
    private ListView mEvidenceList;
    private List<TracePointActivity.SceneClass> mValues;

    public void setmListener(AdapterView.OnItemClickListener onItemClickListener){
        mEvidenceList.setOnItemClickListener(onItemClickListener);
    }

    public SelectEvidenceDialog(Context context, List<TracePointActivity.SceneClass> jsonValues) {
        super(context);
        this.mContext = context;
        mMainView = LayoutInflater.from(mContext).inflate(R.layout.select_evidence_dialog_layout,null);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMainView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(Utils.dp2Px(mContext,280));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(Utils.dp2Px(mContext,202));
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

        mValues = jsonValues;
        mEvidenceList = (ListView)mMainView.findViewById(R.id.select_evidence_listview);
        mEvidenceList.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter{

        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mValues.size();
        }

        @Override
        public Object getItem(int position) {
            return mValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_tv,null);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(mValues.get(position).getName());
            tv.setTextColor(mContext.getResources().getColor(R.color.text_common_black_color));
            return convertView;
        }
    }

    public void updateListView(List<TracePointActivity.SceneClass> jsonValues){
        mValues = jsonValues;
        mEvidenceList.setAdapter(new MyAdapter());
    }
}
