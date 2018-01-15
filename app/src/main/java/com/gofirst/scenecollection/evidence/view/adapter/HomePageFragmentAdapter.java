package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.NewestStateDetail;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;
import com.gofirst.scenecollection.evidence.view.activity.ProspectPreview;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.fragment.HomePageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class HomePageFragmentAdapter extends BaseAdapter {

    //List<String> list;
    private List<HomePageFragmentData> list = new ArrayList<HomePageFragmentData>();
    private LayoutInflater inflater;
    private RelativeLayout itemLinearLayout;
    Context context;
    private SharePre sharePre;
    private HomePageFragment homePageFragment;

    public HomePageFragmentAdapter(Context context, List<HomePageFragmentData> list,HomePageFragment homePageFragment) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.homePageFragment=homePageFragment;
        sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);

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
        final HomePageFragmentData data = list.get(arg0);
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
        viewHolder.data = data;
        view.setTag(viewHolder);

//        viewHolder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String stu = data.getState();
//                if("0".equals(stu)){
//                    List<CsSceneCases> csSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"receptionNo = \"" + data.getReceptionNo() + "\"");
//                   // if(csSceneCases.size() > 0){
//                    if(false){
//                        CsSceneCases sceneCase = csSceneCases.get(0);
//                        Intent intent = new Intent(context, ProspectPreview.class);
//
//                        intent.putExtra("caseId", sceneCase.getCaseNo());
//                        intent.putExtra("caseInfo", sceneCase.getExposureProcess());
//                        intent.putExtra("templateId", sceneCase.getTemplateId());
//                        intent.putExtra("status", "0");
//                        context.startActivity(intent);
//                    }else {
//                        Intent intent = new Intent(context, NewestStateDetail.class);
//                        intent.putExtra("id", data.getId());
//                        homePageFragment.startActivityForResult(intent, 3);
//                    }
//                     //      context.startActivity(intent);
//                }else if("1".equals(stu)||"2".equals(stu)){
//                    List<CsSceneCases> reslist= EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
//                            "caseNo = '"+ data.getCaseId() +"'");
//                    Intent intent = new Intent(context, ProspectPreview.class);
//
//                    if(reslist.get(0).getDealType().equals("1")){
//                        intent.putExtra("dealType", true);
//                    }
//
//                    intent.putExtra("caseId", reslist.get(0).getCaseNo());
//                    intent.putExtra("caseInfo", reslist.get(0).getExposureProcess());
//                    intent.putExtra("templateId", reslist.get(0).getTemplateId());
//                    intent.putExtra("status", "1");
//                    context.startActivity(intent);
//                    //setTemplateId(templateId, caseId);
//                }else if("3".equals(stu)){
//                    List<CsSceneCases>reslist= EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
//                            "caseNo = '"+ list.get(arg0).getCaseId() +"'");
//                    Intent intent = new Intent(context, ProspectInterface.class);
//
//
//                    intent.putExtra("caseId", reslist.get(0).getCaseNo());
//                    intent.putExtra("mode", BaseView.VIEW);
//                    intent.putExtra("caseInfo", reslist.get(0).getExposureProcess());
//                    intent.putExtra("templateId", reslist.get(0).getTemplateId());
//                    initItemFromJson(reslist.get(0).getCaseNo());
//                    if(withCaseList.size() <=0){
//                        initItemFromDb(reslist.get(0).getTemplateId());
//                    }
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("tabList", withCaseList);
//                    intent.putExtras(bundle);
//
//                    context.startActivity(intent);
//                    //setTemplateId(templateId, caseId);
//                }
//            }
//        });
        viewHolder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:" +data.getMarkOrPhone()));
                context.startActivity(intent);
            }
        });



		/*}else {

			viewHolder = (ViewHolder) view.getTag();
		}*/

        //viewHolder.investigationPlace.setText(list.get(arg0).getInvestigationPlace());
        String receptionNo =  data.getReceptionNo();
        viewHolder.alarmAddress.setText(!TextUtils.isEmpty(receptionNo) && receptionNo.length() > 5 ?
                "(" +  receptionNo.substring(receptionNo.length() - 5,receptionNo.length())  + ")" + data.getAlarmAddress() :
                data.getAlarmAddress());
       // viewHolder.alarmDatetime.setText(list.get(arg0).getAlarmDatetime());
        if(data.getState().equals("0"))
        {
            viewHolder.state.setText("接");
            viewHolder.state.setBackgroundDrawable(context.getResources()
                    .getDrawable(R.drawable.scene_list_item_status_new));
            viewHolder.name.setText(data.getName());
            //viewHolder.caseType.setText(list.get(arg0).getRemark());
            viewHolder.markOrPhone.setText(data.getMarkOrPhone());
            if(TextUtils.isEmpty(data.getMarkOrPhone())){
                viewHolder.phone.setVisibility(View.INVISIBLE);
            }
            viewHolder.case_state.setText("未接勘");
            viewHolder.case_state.setTextColor(Color.argb(255, 23, 88, 148));
            viewHolder.alarmDatetime.setText(data.getAlarmDatetime().substring(0, data.getAlarmDatetime().length() - 3));
        }else if(data.getState().equals("1")||data.getState().equals("2")){
            viewHolder.state.setText("勘");
            viewHolder.state.setBackgroundDrawable(context.getResources().
                    getDrawable(R.drawable.scene_list_item_status_start));
            viewHolder.name.setText(data.getName());
            //viewHolder.caseType.setText(list.get(arg0).getRemark());
            viewHolder.markOrPhone.setText(data.getMarkOrPhone());
            if(data.getMarkOrPhone()==null||data.getMarkOrPhone().equals("")){
                viewHolder.phone.setVisibility(View.INVISIBLE);
            }
            viewHolder.alarmDatetime.setText(data.getAlarmDatetime().substring(0,data.getAlarmDatetime().length()-3));
            viewHolder.case_state.setText("勘查中");
            viewHolder.case_state.setTextColor(Color.argb(255, 23, 88, 148));
        }else if(data.getState().equals("3")){
            viewHolder.state.setText("勘");
            viewHolder.state.setBackgroundDrawable(context.getResources().
                    getDrawable(R.drawable.scene_list_item_status_start_grey));
            viewHolder.name.setText(data.getName());
            //viewHolder.caseType.setText(list.get(arg0).getRemark());
            viewHolder.markOrPhone.setText(data.getMarkOrPhone());
            viewHolder.phone.setVisibility(View.INVISIBLE);
            viewHolder.alarmDatetime.setText(data.getAlarmDatetime().substring(0,data.getAlarmDatetime().length()-3));
            viewHolder.case_state.setText("勘查结束");
            viewHolder.case_state.setTextColor(Color.argb(255, 170, 170, 170));
        }
        String type = data.getRemark();
        if(type != null && type.length() > 0){
            viewHolder.caseType.setText(type + "   ");
        }else{
            viewHolder.caseType.setText("");
        }
        viewHolder.case_exposure_process.setText(data.getExposureProcess());
        return view;
    }


    public class ViewHolder {

        private RelativeLayout itemLinearLayout;
        private TextView investigationPlace;
        private TextView exposureProcess;
        private TextView name;
        private TextView markOrPhone;
        private ImageView phone;
        private TextView alarmDatetime;
        private TextView state;
        private TextView caseType;
        private TextView alarmAddress;
        private ImageView sceneListItemGoto;
        private TextView case_state;
        private TextView case_exposure_process;
        public HomePageFragmentData data;
    }


    public static class HomePageFragmentData {
        private String investigationPlace;
        private String exposureProcess;
        private String name;
        private String markOrPhone;
        private String alarmDatetime;
        private String state;
        private String caseId;
        private String id;
        private String alarmAddress;
        private String remark;
        private String receptionNo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAlarmDatetime() {
            return alarmDatetime;
        }

        public void setAlarmDatetime(String alarmDatetime) {
            this.alarmDatetime = alarmDatetime;
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

        public String getReceptionNo() {
            return receptionNo;
        }

        public void setReceptionNo(String receptionNo) {
            this.receptionNo = receptionNo;
        }
    }



    //temporary and will delete
    private ArrayList<ProspectPreViewItemData> withCaseList = new ArrayList<>();
    private ArrayList<ProspectPreViewItemData> allList = new ArrayList<>();
    private  void initItemFromJson(String caseId) {
        withCaseList.clear();
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + caseId + "'", "sort asc");
        for (TemplateSort templateSort : list) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(templateSort.getFatherKey());
            data.setName(templateSort.getFatherValue());
            withCaseList.add(data);
        }
        match();
    }

    private void initItemFromDb(String templateId) {
        List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "templateId = '" + templateId + "' and templateType = '0' and templateLevel = '1'", "positionSort asc");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(commonTemplateDetail.getTableName());
            data.setName(commonTemplateDetail.getSceneName());
            withCaseList.add(data);
        }
        match();
    }

    private void match() {
        //与所有item匹配配置
        for (int i = 0; i < withCaseList.size(); i++) {
            ProspectPreViewItemData item = withCaseList.get(i);
            for (ProspectPreViewItemData all : allList) {
                if (item.getField().equals(all.getField())) {
                    withCaseList.remove(i);
                    withCaseList.add(i, all);
                }
            }
        }
    }

}
