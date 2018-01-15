package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.customview.PhotoGridview;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/9.
 */
public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ArrayList<HashMap<String, Object>>> mList;
    private Context mContext;
    private String caseId;
    private String mode;
    private String templateId;
    private String father;
    private boolean isAddRec;

    public ListViewAdapter(String father,String mode,String caseId,String templateId,ArrayList<ArrayList<HashMap<String, Object>>> mList, Context mContext,boolean isAddRec) {
        super();
        this.mList = mList;
        this.mContext = mContext;
        this.caseId=caseId;
        this.mode=mode;
        this.templateId = templateId;
        this.father = father;
        this.isAddRec = isAddRec;
    }
    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return this.mList.size();
        }
    }
    @Override
    public Object getItem(int position) {
        if (mList == null) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.listview_item, null, false);
            holder.tittle = (TextView) convertView.findViewById(R.id.listview_item_imageview);
            holder.gridView = (PhotoGridview) convertView.findViewById(R.id.listview_item_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (this.mList != null) {
            if (holder.tittle != null) {
                /*holder.tittle.setImageDrawable
                        (mContext.getResources().getDrawable(R.drawable.apply));*/

                switch(position){
                    case 0:
                        holder.tittle.setText("未分类");
                        break;
                    case 1:
                        holder.tittle.setText("方位");
                        break;
                    case 2:
                        holder.tittle.setText("概貌");
                        break;
                    case 3:
                        holder.tittle.setText("重点");
                        break;
                    case 4:
                        holder.tittle.setText("细目");
                        break;
                    case 5:
                        holder.tittle.setText("其它");
                        break;
                }
            }
            if (holder.gridView != null) {
                ArrayList<HashMap<String, Object>> arrayListForEveryGridView = this.mList.get(position);
                GridViewAdapter gridViewAdapter=new GridViewAdapter(father,mode,caseId,templateId,position,mContext, arrayListForEveryGridView);
                gridViewAdapter.setAddRec(isAddRec);
                holder.gridView.setAdapter(gridViewAdapter);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tittle;
        PhotoGridview gridView;
    }
}
