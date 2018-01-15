package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class CaseTypePopAdapter extends BaseAdapter {

    private Context context;
    //private String[] beans;
    private List<String> list;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    class ViewHolder {

        TextView tvName;
        CheckBox cb;
        LinearLayout linearLayout;
    }

    public CaseTypePopAdapter(Context context,List<String> list) {//String[] beans
        // TODO Auto-generated constructor stub
        //this.beans = beans;
        this.list=list;
        this.context = context;
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
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
        ViewHolder holder;
        String bean = list.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.case_type_pop_item, null);
            holder = new ViewHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.linearLayout=(LinearLayout)convertView.findViewById(R.id.linear_layout_up);
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

        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        CaseTypePopAdapter.isSelected = isSelected;
    }
}
