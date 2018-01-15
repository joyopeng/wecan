package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.MessageFragmentItemDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MessageFragmentListAdapter extends BaseAdapter {

    private List<messageFragmentListData> list = new ArrayList<messageFragmentListData>();
    private LayoutInflater layoutInflater;
    private Context context;
    private LinearLayout lawCaseSceneLinearLayout;

    public MessageFragmentListAdapter( Activity context){
        this.context = context;
    }
    public MessageFragmentListAdapter(List<messageFragmentListData> list,
                                      Context context) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 8;
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

     /*   ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.lawcasescene_item, null);
            viewHolder = new ViewHolder();
            *//*viewHolder.breastEncyclopediaIcon = (ImageView) convertView
                    .findViewById(R.id.doctor_icon);*//*
            viewHolder.lawCaseSceneDescription = (TextView) convertView
                    .findViewById(R.id.lawcase_description);
            viewHolder.lawCaseSceneName = (TextView) convertView
                    .findViewById(R.id.input_name);
            viewHolder.lawCaseSceneTime = (TextView) convertView
                    .findViewById(R.id.input_time);
            viewHolder.lawCaseSceneLinearLayout = (LinearLayout) convertView
                    .findViewById(R.id.lawcasescene_linearLayout);
            convertView.setTag(viewHolder);
            viewHolder.lawCaseSceneLinearLayout
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                           *//* Intent intent = new Intent(context,
                                    BreastEncyclopediaDetail.class);
                            context.startActivity(intent);*//*
                        }
                    });

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
      *//*  VolleyInit.displayImage(list.get(position).getBreastEncyclopediaIcon(),
                viewHolder.breastEncyclopediaIcon);*//*
        viewHolder.lawCaseSceneName.setText(list.get(position)
                .getLawCaseSceneName());
        viewHolder.lawCaseSceneTime.setText(list.get(position)
                .getLawCaseSceneTime());
        viewHolder.lawCaseSceneDescription.setText(list.get(position)
                .getLawCaseSceneDescription());

        return convertView;*/
        View view;
        ViewHolder viewHolder;
        view=LayoutInflater.from(parent.getContext()).inflate(R.layout.messagefragment_item, null);

        viewHolder = new ViewHolder();
        viewHolder.messageLinearlayout = (LinearLayout) view
                .findViewById(R.id.message_linearlayout);
        view.setTag(viewHolder);
        viewHolder.messageLinearlayout
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(context,
                                MessageFragmentItemDetail.class);
                        context.startActivity(intent);
                    }
                });
        return view;
    }



    private class ViewHolder {
        //        private ImageView lawCaseSceneIcon;
        private TextView lawCaseSceneName;
        private TextView lawCaseSceneDescription;
        private TextView lawCaseSceneTime;
        private LinearLayout messageLinearlayout;
    }

    public static class messageFragmentListData {
        //        private String breastEncyclopediaIcon;
        private String lawCaseSceneName;
        private String lawCaseSceneTime;;
        private String lawCaseSceneDescription;

        /*public String getLawCaseSceneIcon() {

            return lawCaseSceneIcon;
        }

        public void setlawCaseSceneIcon(String breastEncyclopediaIcon) {
            this.breastEncyclopediaIcon = breastEncyclopediaIcon;
        }*/

        public String getLawCaseSceneName() {
            return lawCaseSceneName;
        }

        public void setLawCaseSceneName(String lawCaseSceneName) {
            this.lawCaseSceneName = lawCaseSceneName;
        }

        public String getLawCaseSceneTime() {
            return lawCaseSceneTime;
        }

        public void setlawCaseSceneTime(
                String lawCaseSceneTime) {
            this.lawCaseSceneTime = lawCaseSceneTime;
        }

        public String getLawCaseSceneDescription() {
            return lawCaseSceneDescription;
        }

        public void setlawCaseSceneDescription(
                String lawCaseSceneDescription) {
            this.lawCaseSceneDescription = lawCaseSceneDescription;
        }
    }
}

