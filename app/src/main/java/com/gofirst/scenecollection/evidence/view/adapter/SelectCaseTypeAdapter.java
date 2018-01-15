package com.gofirst.scenecollection.evidence.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.view.customview.TabTextView;

import java.util.ArrayList;

/**
 * Created by maxiran on 2016/5/5.
 */
public class
SelectCaseTypeAdapter extends BaseAdapter{
    private ArrayList<ProspectPreViewItemData> list;
    private OnTabListener listener;
    public SelectCaseTypeAdapter(ArrayList<ProspectPreViewItemData> list,OnTabListener listener){
        this.list = list;
        this.listener = listener;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TabTextView tabTextView = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.case_select_type_item,parent,false);
            tabTextView = (TabTextView)convertView.findViewById(R.id.type);
            convertView.setTag(tabTextView);
        }else {
            tabTextView = (TabTextView)convertView.getTag();
        }
        tabTextView.setText(list.get(position).getName());
        tabTextView.setIsSelect(list.get(position).isSelected());
       /* if (position == 0 || position == 1 || position == 2 ){
            tabTextView.setSelectAways();
            tabTextView.setOnNextClickListener(null);
            }else {*/
            tabTextView.setOnNextClickListener(new TabTextView.OnNextClickListener() {
                @Override
                public void OnClick(View view,boolean isSelect) {
                    if (listener != null){
                        listener.onTab(position,list.get(position).getName(),isSelect);
                    }
                }
            });
       // }
        return convertView;
    }

    public interface OnTabListener{
        void onTab(int position,String name,boolean isSelect);
    }

}
