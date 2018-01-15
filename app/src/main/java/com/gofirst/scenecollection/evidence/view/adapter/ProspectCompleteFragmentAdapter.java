package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.NewestStateDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/14.
 */
public class ProspectCompleteFragmentAdapter extends BaseAdapter {

    //List<String> list;
    private List<ProspectCompleteFragmentData> list = new ArrayList<ProspectCompleteFragmentData>();
    private LayoutInflater inflater;
    private LinearLayout itemLinearLayout;
    Context context;

    public ProspectCompleteFragmentAdapter(Context context, List<ProspectCompleteFragmentData> list) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int arg0, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            view = inflater.inflate(R.layout.prospect_complete_fragment, null);
            viewHolder.colourLinearLayout= (LinearLayout) view.findViewById(R.id.colour_LinearLayout);
            viewHolder.itemLinearLayout = (LinearLayout) view.findViewById(R.id.item_linearLayout);
            viewHolder.investigationPlace= (TextView)view.findViewById(R.id.investigationPlace);
//            viewHolder.exposureProcess= (TextView)view.findViewById(R.id.exposureProcess);
            viewHolder.sceneRegionalismName= (TextView)view.findViewById(R.id.sceneRegionalismName);
            viewHolder.crackedDate= (TextView)view.findViewById(R.id.crackedDate);
            viewHolder.caseSolve= (TextView)view.findViewById(R.id.caseSolve);
            view.setTag(viewHolder);
            viewHolder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewestStateDetail.class);
                    intent.putExtra("investigationPlace", list.get(arg0).getInvestigationPlace());
                    intent.putExtra("exposureProcess", list.get(arg0).getExposureProcess());
                    intent.putExtra("sceneRegionalismName", list.get(arg0).getSceneRegionalismName());
                    intent.putExtra("crackedDate", list.get(arg0).getCrackedDate());
                    intent.putExtra("caseSolve", list.get(arg0).getCaseSolve());

                    context.startActivity(intent);
                }
            });
        }else {

            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.investigationPlace.setText(list.get(arg0).getInvestigationPlace());
//        viewHolder.exposureProcess.setText(list.get(arg0).getExposureProcess());
        viewHolder.sceneRegionalismName.setText(list.get(arg0).getSceneRegionalismName());
        viewHolder.crackedDate.setText(list.get(arg0).getCrackedDate());
        if(list.get(arg0).getCaseSolve().equals("0"))
        {
            viewHolder.caseSolve.setText("完成");
            viewHolder.caseSolve.setTextColor(Color.parseColor("#8BABCE"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#8BABCE"));

        }else if(list.get(arg0).getCaseSolve().equals("1")){
            viewHolder.caseSolve.setText("勘查中");
            viewHolder.caseSolve.setTextColor(Color.parseColor("#FFC1AA"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FFC1AA"));

        }else if(list.get(arg0).getCaseSolve().equals("2")){
            viewHolder.caseSolve.setText("未出警");
            viewHolder.caseSolve.setTextColor(Color.parseColor("#FF5555"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FF5555"));
        }

        return view;

    }


    private class ViewHolder {

        private LinearLayout itemLinearLayout;
        private TextView investigationPlace;
        //        private TextView exposureProcess;
        private TextView sceneRegionalismName;
        private TextView crackedDate;
        private TextView caseSolve;
        private LinearLayout colourLinearLayout;
    }


    public static class ProspectCompleteFragmentData {
        private String investigationPlace;
        private String exposureProcess;
        private String sceneRegionalismName;
        private String crackedDate;
        private String caseSolve;

        public String getInvestigationPlace() {
            return investigationPlace;
        }
        public void setInvestigationPlace(String investigationPlace) {
            this.investigationPlace = investigationPlace;
        }


        public String getExposureProcess() {
            return exposureProcess;
        }
        public void setExposureProcess(String exposureProcess) {
            this.exposureProcess = exposureProcess;
        }

        public String getSceneRegionalismName() {
            return sceneRegionalismName;
        }
        public void setSceneRegionalismName(String sceneRegionalismName) {
            this.sceneRegionalismName = sceneRegionalismName;
        }

        public String getCrackedDate() {
            return crackedDate;
        }
        public void setCrackedDate(String crackedDate) {
            this.crackedDate = crackedDate;
        }

        public String getCaseSolve() {
            return caseSolve;
        }
        public void setCaseSolve(String caseSolve) {
            this.caseSolve = caseSolve;
        }

    }
}

