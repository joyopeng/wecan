package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class MoreNewCaseAdapter extends BaseAdapter {

    //List<String> list;
    private List<MoreNewCaseData> list = new ArrayList<MoreNewCaseData>();
    private LayoutInflater inflater;
    private RelativeLayout itemLinearLayout;
    Context context;

    public MoreNewCaseAdapter(Context context, List<MoreNewCaseData> list) {
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
        //if (view == null) {
        viewHolder = new ViewHolder();

        view = inflater.inflate(R.layout.home_page_list_item, null);
        viewHolder.itemLinearLayout = (RelativeLayout) view.findViewById(R.id.item_linearLayout);
        //viewHolder.investigationPlace= (TextView)view.findViewById(R.id.investigationPlace);
        viewHolder.name=(TextView)view.findViewById(R.id.name);
        viewHolder.markOrPhone=(TextView)view.findViewById(R.id.mark_or_phone);
        viewHolder.alarmAddress= (TextView)view.findViewById(R.id.alarm_address);
        viewHolder.alarmDatetime= (TextView)view.findViewById(R.id.date);
        viewHolder.state= (TextView)view.findViewById(R.id.state);
        viewHolder.phone=(ImageView)view.findViewById(R.id.phone);
        viewHolder.sceneListItemGoto=(ImageView)view.findViewById(R.id.scene_list_item_goto);
        viewHolder.caseType=(TextView)view.findViewById(R.id.case_type);
        viewHolder.case_state=(TextView)view.findViewById(R.id.case_state);
        viewHolder.case_exposure_process = (TextView)view.findViewById(R.id.case_exposure_process);
        view.setTag(viewHolder);

        viewHolder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:" + list.get(arg0).getMarkOrPhone()));
                context.startActivity(intent);
            }
        });



		/*}else {

			viewHolder = (ViewHolder) view.getTag();
		}*/

        //viewHolder.investigationPlace.setText(list.get(arg0).getInvestigationPlace());
        viewHolder.name.setText(list.get(arg0).getName());
        viewHolder.markOrPhone.setText(list.get(arg0).getMarkOrPhone());
        viewHolder.caseType.setText(list.get(arg0).getRemark());
        String receptionNo = list.get(arg0).getReceptionNo();
        viewHolder.alarmAddress.setText(!TextUtils.isEmpty(receptionNo) && receptionNo.length() > 5 ?
                "(" +  receptionNo.substring(receptionNo.length() - 5,receptionNo.length())  + ")" + list.get(arg0).getAlarmAddress() :
                list.get(arg0).getAlarmAddress());
        viewHolder.alarmDatetime.setText(list.get(arg0).getAlarmDatetime().substring(0, list.get(arg0).getAlarmDatetime().length()-3));
        viewHolder.case_state.setText("未接勘");
        /*if(list.get(arg0).getStatus().equals("0"))
        {
            viewHolder.state.setText("未出警");
            viewHolder.state.setTextColor(Color.parseColor("#FF5555"));

        }else if(list.get(arg0).getStatus().equals("1")){
            viewHolder.state.setText("勘查中");
            viewHolder.state.setTextColor(Color.parseColor("#FFC1AA"));


        }else if(list.get(arg0).getStatus().equals("3")){
            viewHolder.state.setText("完成");
            viewHolder.state.setTextColor(Color.parseColor("#8BABCE"));

        }*/
        viewHolder.case_exposure_process.setText(list.get(arg0).getExposureProcess());
        return view;
    }



    private class ViewHolder {

        private RelativeLayout itemLinearLayout;
        private TextView investigationPlace;
        private TextView exposureProcess;
        private TextView name;
        private TextView markOrPhone;
        private ImageView phone;
        private TextView alarmDatetime;
        private TextView state;
        private ImageView sceneListItemGoto;
        private TextView caseType;
        private TextView alarmAddress;
        private TextView case_state;
        private TextView case_exposure_process;
    }


    public static class MoreNewCaseData {
        private String investigationPlace;
        private String exposureProcess;
        private String name;
        private String markOrPhone;
        private String state;
        private String caseId;
        private String id;

        private String sceneRegionalismName;
        private String alarmDatetime;
        private String alarmAddress;
        private String remark;
        private String receptionNo;

        public String getReceptionNo() {
            return receptionNo;
        }

        public void setReceptionNo(String receptionNo) {
            this.receptionNo = receptionNo;
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMarkOrPhone() {
            return markOrPhone;
        }

        public void setMarkOrPhone(String markOrPhone) {
            this.markOrPhone = markOrPhone;
        }


        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCaseId() {
            return caseId;
        }

        public void setCaseId(String caseId) {
            this.caseId = caseId;
        }

        public String getSceneRegionalismName() {
            return sceneRegionalismName;
        }

        public void setSceneRegionalismName(String sceneRegionalismName) {
            this.sceneRegionalismName = sceneRegionalismName;
        }

        public String getAlarmDatetime() {
            return alarmDatetime;
        }

        public void setAlarmDatetime(String alarmDatetime) {
            this.alarmDatetime = alarmDatetime;
        }

        public String getAlarmAddress() {
            return alarmAddress;
        }

        public void setAlarmAddress(String alarmAddress) {
            this.alarmAddress = alarmAddress;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}

