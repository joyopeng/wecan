package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/5.
 */
public class MultipleGridviewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private Boolean isSelect=false;
    private List<String> listTemp=new ArrayList<>();
    ClickListener listener;
    private String textTemp;

    public MultipleGridviewAdapter(String text,Context mContext, List<String> mList,ClickListener listener) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.listener=listener;
        this.textTemp=text;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.multiple_gridview_item, null, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(textTemp!=null){
            String[] textArray=textTemp.split(",");
            for(int i=0;i<textArray.length;i++){
                if(textArray[i].equals(mList.get(position))){
                    holder.text.setBackgroundResource(R.drawable.rect_blue);
                }
            }

        }
        if(!mList.get(position).equals("")) {
            holder.text.setText(mList.get(position));
        }
       /* if (textTemp.equals(mList.get(position))) {

            holder.text.setBackgroundResource(R.drawable.rect_blue);
        }*/
        final ViewHolder finalHolder = holder;
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("positiontao",position+"");
                if(isSelect==true){
                    listener.onClick(position);
                    v.setBackgroundResource(R.drawable.rect_blue);
                    isSelect=false;
                }else{
                    listener.onClick(-position);
                    v.setBackgroundResource(R.drawable.rect_white);
                    isSelect=true;
                }



                /*if (!isSelect){
                    v.setBackgroundResource(R.drawable.rect_blue);
                    isSelect=true;
                }else {
                    v.setBackgroundResource(R.drawable.rect_white);
                    isSelect=false;

                }*/
            }
        });

        return convertView;
    }

    public interface ClickListener{
        void onClick(int position);
    }

    private class ViewHolder {
        TextView text;
    }

}
