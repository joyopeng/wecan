package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */
public class MultiChoiclListDialogLightAdapter extends BaseAdapter {

    private Context context;
    //private String[] beans;
    private List<String> list;
    private String defaultValue;
    private String text;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    class ViewHolder {

        TextView tvName;
        CheckBox cb;
        LinearLayout linearLayout;
        RelativeLayout relativeLayout;//outpatient_check_hospital;
    }

    public MultiChoiclListDialogLightAdapter(Context context,List<String> list,String defaultValue,String text) {//String[] beans
        // TODO Auto-generated constructor stub
        //this.beans = beans;
        this.list=list;
        this.context = context;
        this.defaultValue=defaultValue;
        this.text=text;
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {

        for (int i = 0; i < list.size(); i++) {

            if(text.contains(list.get(i))) {
                getIsSelected().put(i, true);
            }else{
                getIsSelected().put(i, false);
            }
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        final ViewHolder holder;
        String bean = list.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.multi_choicl_list_pop_item, null);
            holder = new ViewHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.linearLayout=(LinearLayout)convertView.findViewById(R.id.linear_layout_up);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.outpatient_check_hospital);
            holder.tvName = (TextView) convertView
                    .findViewById(R.id.tv_device_name);
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(bean);
        // 监听checkBox并根据原来的状态来设置新的状态
        holder.cb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);

                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                }

            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                    Resources resources = context.getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.multi_uncheckbox);
                    holder.cb.setBackgroundDrawable(btnDrawable);
                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                    Resources resources = context.getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.multi_checkbok);
                    holder.cb.setBackgroundDrawable(btnDrawable);
                }
            }
        });
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MultiChoiclListDialogLightAdapter.isSelected = isSelected;
    }
}

