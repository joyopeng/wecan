package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ContactPersons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class OrgSearchShowAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mShowLastTime;
    private List<ContactPersons> mPersons = new ArrayList<>();

    public OrgSearchShowAdapter(Context context, boolean showLastTime, List<ContactPersons> persons) {
        super();
        this.mContext = context;
        this.mShowLastTime = showLastTime;
        this.mPersons = persons;
    }

    @Override
    public int getCount() {
        return mPersons.size();
    }

    @Override
    public Object getItem(int position) {
        return mPersons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactPersons person = mPersons.get(position);
        Holder holder = new Holder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.org_search_show_item_layot,parent,false);
        //holder.showImag = (ImageView) convertView.findViewById(R.id.search_show_item_img);
        holder.showImag = (TextView) convertView.findViewById(R.id.search_show_item_img);
        String name = person.getEmployeeName();
        if(name.length() > 2) {
            holder.showImag.setText(name.substring(0,2));
        }else{
            holder.showImag.setText(name);
        }
        holder.showName = (TextView) convertView.findViewById(R.id.search_show_item_name);
        holder.showName.setText(name);
        holder.showPosition  = (TextView) convertView.findViewById(R.id.search_show_item_position);
        holder.showPosition.setText(person.getEmployeePost());
        holder.showLastTime = (TextView) convertView.findViewById(R.id.search_show_item_lasttime);
        if(mShowLastTime){
            holder.showLastTime.setVisibility(View.VISIBLE);
            Date date = mPersons.get(position).getLastConnectTime();
            long curr = System.currentTimeMillis();
            long dura = curr - date.getTime();
            //minu - 60000 ; hour - 3600000;
            if(dura > 86400000){
                holder.showLastTime.setText(dura/86400000 + "天前");
            }else if(dura > 3600000){
                holder.showLastTime.setText(dura/3600000 + "小时前");
            }else if(dura > 300000){
                holder.showLastTime.setText(dura/60000 + "分前");
            }else {
                holder.showLastTime.setText("刚刚");
            }
//            if(curr - date.getTime() > 300000) {
//                holder.showLastTime.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
//            }else{
//                holder.showLastTime.setText("刚刚");
//            }
        }else{
            holder.showLastTime.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class Holder{
        //public ImageView showImag;
        public TextView showImag;
        public TextView  showName;
        public TextView showPosition;
        public TextView showLastTime;
    }

    public int getPositonSection(String first){
        int result = -1;
        int length = mPersons.size();
        for(int i = 0;i < length;i++){
            String s = mPersons.get(i).getEmployeeNameSpell();
            if(s.length() <= 0){
                result = -1;
                continue;
            }
            if(first != null && first.equals(s.substring(0,1))){
                result = i;
                break;
            }
        }
        return result;
    }

    public void updateAdapter(List<ContactPersons> persons){
        this.mPersons = persons;
        //this.notifyDataSetChanged();
    }
}
