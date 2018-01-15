package com.gofirst.scenecollection.evidence.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.List;


public class CaseTemplateAdapter extends BaseAdapter{
    private List<String> templateLists;

    public CaseTemplateAdapter(List<String> templateLists){
        this.templateLists = templateLists;
    }
    @Override
    public int getCount() {
        return templateLists.size();
    }

    @Override
    public Object getItem(int position) {
        return templateLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.case_template_item,parent,false);
            textView = (TextView) convertView.findViewById(R.id.case_template_name);
            convertView.setTag(textView);
        }else {
            textView = (TextView)convertView.getTag();
        }
        textView.setText(templateLists.get(position));
        textView.setBackgroundResource(position % 2 == 1 ? R.drawable.case_template_drawable_one : R.drawable.case_template_drawable_two);
        return convertView;
    }
}
