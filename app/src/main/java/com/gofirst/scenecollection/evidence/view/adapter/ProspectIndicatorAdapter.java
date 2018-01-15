package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.customview.ProspectIndicatorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maxiran
 */
public class ProspectIndicatorAdapter extends BaseAdapter{

    private int lastSelPosition = 0;
    private List<View> itemList = new ArrayList<>();
    private List<String> itemNames = new ArrayList<>();
    private Context context;
    private ListView listView;
    private OnItemSelectListener listener;

    /**
     *
     * 适配器的构造器
     * @param context 上下文
     * @param itemNames 选项名字列表
     * @param listView 选项的列表视图
     *
     */
    public ProspectIndicatorAdapter(Context context, List<String> itemNames, ListView listView) {
        this.context = context;
        this.itemNames = itemNames;
        this.listView = listView;
        listView.setDividerHeight(0);
        for (int i = 0 ;i < itemNames.size(); i++){
            itemList.add(LayoutInflater.from(context).inflate(R.layout.prospectindictor_item,listView,false));
        }

    }

    @Override
    public int getCount() {
        return itemNames.size();
    }

    /**
     * @param position
     * @return 选项名
     */
    @Override
    public String getItem(int position) {
        return itemNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = itemList.get(position);
        final TextView name  = (TextView)view.findViewById(R.id.name);
        String names = itemNames.get(position);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(names);
        stringBuilder.insert(2,"\n");
        name.setText(stringBuilder.toString());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 setPosition(position,0);
            }
        });
        return view;
    }

    /**
     * 设置选中选项
     * @param params 你需要选中的选项位置，1用来标志非用户点击
     *
     */

    public void setPosition(int... params){
        int position = params[0];
        if (position < 0 || position >= itemList.size()){
            Toast.makeText(context,"位置超出范围",Toast.LENGTH_SHORT).show();
            position = 0;
        }
        if (params[1] == 1){
            listView.setSelection(position);
        }
        if (listener != null){
            listener.onItemSelect(position,itemNames.get(position));
        }
        View view = itemList.get(position);
        final ProspectIndicatorItem item = (ProspectIndicatorItem)view.findViewById(R.id.item);
        final TextView name  = (TextView)view.findViewById(R.id.name);
        if (lastSelPosition != 0){
            //上次上方状态重置
            ProspectIndicatorItem lastItem  = (ProspectIndicatorItem) itemList.get(lastSelPosition - 1).findViewById(R.id.item);
            TextView lastName  = (TextView)itemList.get(lastSelPosition - 1).findViewById(R.id.name);
            lastItem.setIsSelect(false);
            lastName.setTextColor(Color.parseColor("#909090"));
            //上次下方状态重置
            if (lastSelPosition != 1){
                ProspectIndicatorItem lastTopItem  = (ProspectIndicatorItem) itemList.get(lastSelPosition - 2).findViewById(R.id.item);
                lastTopItem.setBottomSelect(false);
            }
        }
        //本次状态设置
        name.setTextColor(Color.parseColor("#FF9853"));
        item.setIsSelect(true);
        if (position != 0){
            ((ProspectIndicatorItem)itemList.get(position - 1).findViewById(R.id.item)).setBottomSelect(true);
        }
        lastSelPosition = position + 1;
    }

    /**
     * 设置选项监听
     * @param listener 选中选项监听器
     */

    public void setOnItemSelectListener(OnItemSelectListener listener){
        this.listener = listener;
    }

    public interface OnItemSelectListener{
        void onItemSelect(int position,String name);
    }
}
