package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class AllQueryFragmentAdapter extends BaseAdapter {

    //List<String> list;
    private List<AllQueryFragmentData> list = new ArrayList<AllQueryFragmentData>();
    private LayoutInflater inflater;
    private LinearLayout itemLinearLayout;
    Context context;
    private List<String> templateIds = new ArrayList<>();
    private String prospectPerson;
    private ArrayList<ProspectPreViewItemData> WithCaseList = new ArrayList<>();

    public AllQueryFragmentAdapter(String prospectPerson,Context context, List<AllQueryFragmentData> list) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.prospectPerson=prospectPerson;
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

            view = inflater.inflate(R.layout.allquery_fragment_item, null);
            viewHolder.colourLinearLayout = (LinearLayout) view.findViewById(R.id.colour_LinearLayout);
            viewHolder.itemLinearLayout = (LinearLayout) view.findViewById(R.id.item_linearLayout);
            viewHolder.investigationPlace = (TextView) view.findViewById(R.id.investigationPlace);
//            viewHolder.exposureProcess= (TextView)view.findViewById(R.id.exposureProcess);
            viewHolder.sceneRegionalismName = (TextView) view.findViewById(R.id.sceneRegionalismName);
            viewHolder.crackedDate = (TextView) view.findViewById(R.id.crackedDate);
            viewHolder.status = (TextView) view.findViewById(R.id.caseSolve);
            view.setTag(viewHolder);
            viewHolder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(context, NewestStateDetail.class);
                    intent.putExtra("investigationPlace", list.get(arg0).getInvestigationPlace());
                    intent.putExtra("exposureProcess", list.get(arg0).getExposureProcess());
                    intent.putExtra("sceneRegionalism", list.get(arg0).getSceneRegionalism());
                    intent.putExtra("crackedDate", list.get(arg0).getCrackedDate());
                    intent.putExtra("status", list.get(arg0).getStatus());
                    intent.putExtra("id",list.get(arg0).getId());
                    intent.putExtra("sceneInvestigationId",list.get(arg0).getId());
                    intent.putExtra("caseType",list.get(0).getCaseType());*/
                    if (list.get(arg0).getTemplateId()!=null) {

                    //templateIds = new ArrayList<>();
                    Intent intent = new Intent(context, ProspectInterface.class);//ProspectPreview
                    intent.putExtra("caseId", list.get(arg0).getCaseId());
                    //intent.putExtra("caseInfo", list.get(arg0).getExposureProcess());
                    intent.putExtra("templateId", list.get(arg0).getTemplateId());
                    intent.putExtra("mode", "find");
                        WithCaseList.clear();
                    ArrayList<ProspectPreViewItemData> listData = getProspectData(list.get(arg0).getTemplateId());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tabList", listData);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else{
                        Toast.makeText(context,"暂无案件类型",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.investigationPlace.setText(list.get(arg0).getInvestigationPlace());
//        viewHolder.exposureProcess.setText(list.get(arg0).getExposureProcess());

        viewHolder.crackedDate.setText(list.get(arg0).getCrackedDate());
        if (list.get(arg0).getStatus().equals("0")) {
            viewHolder.status.setText("未出警");
            viewHolder.status.setTextColor(Color.parseColor("#FF5555"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FF5555"));
            viewHolder.sceneRegionalismName.setText("");
        } else if (list.get(arg0).getStatus().equals("1")) {
            viewHolder.status.setText("勘查中");
            viewHolder.status.setTextColor(Color.parseColor("#FFC1AA"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FFC1AA"));
            viewHolder.sceneRegionalismName.setText(prospectPerson);

        } else if (list.get(arg0).getStatus().equals("3")) {
            viewHolder.status.setText("完成");
            viewHolder.status.setTextColor(Color.parseColor("#8BABCE"));
            viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#8BABCE"));
            viewHolder.sceneRegionalismName.setText(prospectPerson);
        }

        return view;

    }


    private class ViewHolder {

        private LinearLayout itemLinearLayout;
        private TextView investigationPlace;
        private TextView exposureProcess;
        private TextView sceneRegionalismName;
        private TextView crackedDate;
        private TextView status;
        private LinearLayout colourLinearLayout;

    }

    public static class AllQueryFragmentData {
        private String investigationPlace;
        private String exposureProcess;
        private String sceneRegionalism;
        private String crackedDate;
        private String status;
        private String id;
        private String sceneInvestigationId;//勘验Id
        private String caseType;
        private String caseId;
        private String templateId;

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

        public String getSceneRegionalism() {
            return sceneRegionalism;
        }

        public void setSceneRegionalism(String sceneRegionalism) {
            this.sceneRegionalism = sceneRegionalism;
        }

        public String getCrackedDate() {
            return crackedDate;
        }

        public void setCrackedDate(String crackedDate) {
            this.crackedDate = crackedDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSceneInvestigationId() {
            return sceneInvestigationId;
        }

        public void setSceneInvestigationId(String sceneInvestigationId) {
            this.sceneInvestigationId = sceneInvestigationId;
        }

        public String getCaseType() {
            return caseType;
        }

        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        public String getCaseId() {
            return caseId;
        }

        public void setCaseId(String caseId) {
            this.caseId = caseId;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }


    private String getCaseTemplates() {

        templateIds.clear();
        ProspectPreViewItemData caseInfoItem = new ProspectPreViewItemData();
        caseInfoItem.setName("案件信息");
        caseInfoItem.setEditOrCamera(false);
        caseInfoItem.setField("SCENE_INFO");
        //caseInfoItem.setDesc(getIntent().getStringExtra("caseInfo"));
        caseInfoItem.setNeedRec(true);
        caseInfoItem.setPlayOrRecord(true);

        ProspectPreViewItemData caseEnvironment = new ProspectPreViewItemData();
        caseEnvironment.setName("现场环境");
        caseEnvironment.setEditOrCamera(false);
        caseEnvironment.setField("SCENE_ENVIRONMENT");
        caseEnvironment.setNeedRec(true);

        ProspectPreViewItemData prospectInfo = new ProspectPreViewItemData();
        prospectInfo.setName("接勘信息");
        prospectInfo.setEditOrCamera(false);
        prospectInfo.setSelected(true);
        prospectInfo.setDesc("接勘情况");
        prospectInfo.setField("SCENE_INVESTIGATION_EXT");

        ProspectPreViewItemData sceneDispatch = new ProspectPreViewItemData();
        sceneDispatch.setName("勘验信息");
        sceneDispatch.setEditOrCamera(false);
        sceneDispatch.setDesc("主勘人员，副勘人员");
        sceneDispatch.setField("SCENE_DISPATCH");
        WithCaseList.add(0, caseInfoItem);
        WithCaseList.add(1, caseEnvironment);
        WithCaseList.add(2, prospectInfo);
        WithCaseList.add(3, sceneDispatch);



        SharePre userInfo = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String orgId = userInfo.getString("organizationId", "");
        String caseTemplatesId="";
        List<CommonTemplate> commonTemplates = EvidenceApplication.db.findAllByWhere(CommonTemplate.class, "orgId = '" + orgId + "'");
        List<String> caseTemplates = new ArrayList<>();
        for (CommonTemplate commonTemplate : commonTemplates) {
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "dictKey = '" +
                    commonTemplate.getCaseTypeCode() + "'" + " and " + "rootKey = 'AJLBDM'");
            for (CsDicts csDict : csDicts) {
                caseTemplates.add(csDict.getDictValue1());
            }
            Log.d("commonTemplate", "" + commonTemplate.getSid());
            caseTemplatesId=commonTemplate.getSid().toString();
        }
        return caseTemplatesId;
    }


   private ArrayList<ProspectPreViewItemData> getProspectData( String templateId){
      ArrayList list = new ArrayList<>();
       List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "templateId = '" + templateId + "'","positionSort asc");
       for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
           ProspectPreViewItemData data = new ProspectPreViewItemData();
           data.setField(commonTemplateDetail.getTableName());
           data.setName(commonTemplateDetail.getSceneName());
           WithCaseList.add(data);
       }
       getCaseTemplates();
       return WithCaseList;
   }



   // private List<String> templateIds = new ArrayList<>();
   // private ArrayList<ProspectPreViewItemData> WithCaseList = new ArrayList<>();
   /* private List<String> getCaseTemplates(){
        templateIds.clear();
        ProspectPreViewItemData caseInfoItem = new ProspectPreViewItemData();
        caseInfoItem.setName("案件信息");
        caseInfoItem.setEditOrCamera(false);
        caseInfoItem.setField("SCENE_INFO");
        //caseInfoItem.setDesc(getIntent().getStringExtra("caseInfo"));
        caseInfoItem.setNeedRec(true);
        caseInfoItem.setPlayOrRecord(true);

        ProspectPreViewItemData caseEnvironment = new ProspectPreViewItemData();
        caseEnvironment.setName("现场环境");
        caseEnvironment.setEditOrCamera(false);
        caseEnvironment.setField("SCENE_ENVIRONMENT");
        caseEnvironment.setNeedRec(true);

        ProspectPreViewItemData prospectInfo = new ProspectPreViewItemData();
        prospectInfo.setName("接勘信息");
        prospectInfo.setEditOrCamera(false);
        prospectInfo.setSelected(true);
        prospectInfo.setDesc("接勘情况");
        prospectInfo.setField("SCENE_INVESTIGATION_EXT");

        ProspectPreViewItemData sceneDispatch = new ProspectPreViewItemData();
        sceneDispatch.setName("勘验信息");
        sceneDispatch.setEditOrCamera(false);
        sceneDispatch.setDesc("主勘人员，副勘人员");
        sceneDispatch.setField("SCENE_DISPATCH");
        WithCaseList.add(0, caseInfoItem);
        WithCaseList.add(1, caseEnvironment);
        WithCaseList.add(2, prospectInfo);
        WithCaseList.add(3, sceneDispatch);

        SharePre userInfo = new SharePre(NvestigatQuery.this,"user_info", Context.MODE_PRIVATE);
        String orgId = userInfo.getString("organizationId","");
        orgId="1";
        List<CommonTemplate> commonTemplates = EvidenceApplication.db.findAllByWhere(CommonTemplate.class,"orgId = '" + orgId + "'");
        List<String> caseTemplates = new ArrayList<>();
        for (CommonTemplate commonTemplate : commonTemplates){
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class,"dictKey = '" +
                    commonTemplate.getCaseTypeCode() + "'" + " and " + "rootKey = 'AJLBDM'");
            for (CsDicts csDict : csDicts){
                caseTemplates.add(csDict.getDictValue1());
            }
            templateIds.add(commonTemplate.getSid());
        }
        return caseTemplates;
    }*/
    /**
     * 获取关联案件类型的模块
     */
    private void initPreviewItem(String templateId) {
        List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "templateId = '" + templateId + "'");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(commonTemplateDetail.getTableName());
            data.setName(commonTemplateDetail.getSceneName());
            WithCaseList.add(data);
        }
    }


}
