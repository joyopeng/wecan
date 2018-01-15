package com.gofirst.scenecollection.evidence.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseHistory;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ProspectPreview;
import com.gofirst.scenecollection.evidence.view.customview.PopListSingleLevel;

import net.tsz.afinal.db.sqlite.DbModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author maxiran
 *         已接勘案件列表
 */
public class SceneProspectListAdapter extends BaseAdapter {

    private List<DbModel> list;
    private List<CaseHistory> historys;
    private String employeeName;
    private String employeeId;

    public SceneProspectListAdapter(String employeeName, List<DbModel> list, String employeeId) {
        this.list = list;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.historys = new ArrayList<>();
    }

    public SceneProspectListAdapter(String employeeName, List<DbModel> list, List<CaseHistory> historys,
                                    String employeeId,OnAdditionalRecordingListener onAdditionalRecordingListener) {
        this.list = list;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.historys = historys;
        this.onAdditionalRecordingListener = onAdditionalRecordingListener;
    }

    @Override
    public int getCount() {
        return list.size() + historys.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < list.size()){
            list.get(position);
        }else{
            historys.get(position);
        }
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ALL")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int len = list.size();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_prospect_item, parent, false);
            viewHolder.caseStatus = (TextView) convertView.findViewById(R.id.scene_list_item_status_txt);
            viewHolder.caseName = (TextView) convertView.findViewById(R.id.scene_list_item_details_name);
            viewHolder.caseAlarm = (TextView) convertView.findViewById(R.id.scene_list_item_details_alarm);
            viewHolder.caseType = (TextView) convertView.findViewById(R.id.scene_list_item_details_type);
            viewHolder.caseAdress = (TextView) convertView.findViewById(R.id.scene_list_item_details_adr);
            viewHolder.caseDate = (TextView) convertView.findViewById(R.id.scene_list_item_details_date);
            viewHolder.casePhone = (ImageView) convertView.findViewById(R.id.scene_list_item_details_phone);
            viewHolder.gotoButton = (ImageView) convertView.findViewById(R.id.scene_list_item_goto);
            viewHolder.caseStatusStr = (TextView) convertView.findViewById(R.id.scene_list_item_status);
            viewHolder.caseAlarmContent = (TextView) convertView.findViewById(R.id.scene_list_item_alarm_content);
            viewHolder.additional_recording = (ImageView)convertView.findViewById(R.id.additional_recording);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position < len) {
            //final CsSceneCases caseInfo = (CsSceneCases) list.get(position);
            final DbModel caseInfo = list.get(position);
            if ("0".equals(caseInfo.getString("status"))) {
                viewHolder.caseStatus.setText("接");
                viewHolder.caseStatus.setBackgroundResource(R.drawable.scene_list_item_status_new);
            }else if("3".equals(caseInfo.getString("status"))){
                viewHolder.caseStatus.setText("勘");
                //viewHolder.caseStatus.setBackgroundResource(R.drawable.scene_list_item_status_start);
                viewHolder.caseStatus.setBackgroundResource(R.drawable.scene_list_item_status_start_grey);
            } else {
                viewHolder.caseStatus.setText("勘");
                viewHolder.caseStatus.setBackgroundResource(R.drawable.scene_list_item_status_start);
            }
            //viewHolder.caseName.setText(employeeName);
            viewHolder.caseName.setText(caseInfo.getString("receivePeople"));
            //String alarm = caseInfo.getString("receivePeopleNum");
            //viewHolder.caseAlarm.setText((alarm == null || "null".equals(alarm)) ? "" : alarm);
            String caseTypeStr = caseInfo.getString("caseType");
            if (caseTypeStr != null && !"null".equals(caseTypeStr)) {
                viewHolder.caseType.setText(caseTypeStr + "   ");
            }
            String receptionNo = caseInfo.getString("receptionNo");
            viewHolder.caseAdress.setText(!TextUtils.isEmpty(receptionNo) && receptionNo.length() > 5 ?
                    "(" +  receptionNo.substring(receptionNo.length() - 5,receptionNo.length())  + ")" + caseInfo.getString("sceneDetail") :
                    caseInfo.getString("sceneDetail"));
            //viewHolder.caseDate.setText(caseInfo.getOccurrenceDateFrom().toString());
            viewHolder.caseDate.setText(caseInfo.getString("receiveCaseTime").substring(0, caseInfo.getString("receiveCaseTime").length() - 3));
            String status = caseInfo.getString("status");
            if ("1".equals(status)||"2".equals(status)) {
                viewHolder.caseStatusStr.setText("勘查中");
                //viewHolder.caseStatusStr.setTextColor(Color.argb(255, 170, 170, 170));
                viewHolder.caseStatusStr.setTextColor(Color.argb(255, 23, 88, 148));
            } else if ("3".equals(status)) {
                viewHolder.caseStatusStr.setText("勘查结束");
                //viewHolder.caseStatusStr.setTextColor(Color.argb(255, 23, 88, 148));
                viewHolder.caseStatusStr.setTextColor(Color.argb(255, 170, 170, 170));
                viewHolder.additional_recording.setVisibility(View.VISIBLE);
            } else {
                viewHolder.caseStatusStr.setText("未接勘");
                //viewHolder.caseStatusStr.setTextColor(Color.argb(255, 170, 170, 170));
                viewHolder.caseStatusStr.setTextColor(Color.argb(255, 23, 88, 148));
            }
            viewHolder.casePhone.setOnClickListener(null);
            viewHolder.caseAlarmContent.setText(caseInfo.getString("exposureProcess"));
            //boolean prospectState = getProspectState(list.get(position).getString("caseNo"));
            //viewHolder.gotoButton.setOnClickListener(prospectState ? new continueProspect(list.get(position)) : new startProspect(list.get(position)));
        }else{
            final CaseHistory history = historys.get(position - len);
            viewHolder.caseStatus.setText("勘");
            viewHolder.caseStatus.setBackgroundResource(R.drawable.scene_list_item_status_start_grey);
            viewHolder.caseName.setText(history.getInvestigator());
            //viewHolder.caseAlarm.setText(history.getInvestigatorIds());
            viewHolder.caseType.setText(history.getCaseTypeName());
            viewHolder.caseAdress.setText(history.getSceneDetail());
            viewHolder.caseDate.setText(history.getInvestigationDateFrom());
            viewHolder.caseAlarmContent.setText(history.getExposureProcess());
            viewHolder.caseStatusStr.setText("勘查结束");
            viewHolder.caseStatusStr.setTextColor(Color.argb(255, 170, 170, 170));
            viewHolder.casePhone.setOnClickListener(null);
        }
        viewHolder.additional_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdditionalRecordingListener != null)
                    onAdditionalRecordingListener.onAdditionalRecording(position);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView caseStatus,
                caseName,
                caseAlarm,
                caseType,
                caseAdress,
                caseDate,caseStatusStr,caseAlarmContent;
        private ImageView casePhone,
                gotoButton,additional_recording;
    }

    private boolean getProspectState(String caseId) {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
        return list != null && list.size() != 0 && list.get(0).getTemplateId() != null;
    }
    public class startProspect implements View.OnClickListener {
        private DbModel caseInfo1;

        public startProspect(DbModel caseInfo) {
            this.caseInfo1 = caseInfo;
        }

        @Override
        public void onClick(final View v) {
            PopListSingleLevel popListSingleLevel = new PopListSingleLevel(v.getContext(), "案件类型", v, getCaseTemplates(v.getContext()));
            popListSingleLevel.setListener(new PopListSingleLevel.onResultListener() {
                @Override
                public void onResult(String templateId) {
                    Intent intent = new Intent(v.getContext(), ProspectPreview.class);
                    intent.putExtra("caseId", caseInfo1.getString("caseNo"));
                    intent.putExtra("caseInfo", caseInfo1.getString("exposureProcess"));
                    intent.putExtra("templateId", templateId);
                    v.getContext().startActivity(intent);
                    setTemplateId(templateId, caseInfo1.getString("caseNo"));
                }
            });
        }
    }


    public class continueProspect implements View.OnClickListener {
        private DbModel caseInfo1;

        public continueProspect(DbModel caseInfo) {
            this.caseInfo1 = caseInfo;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ProspectPreview.class);
            intent.putExtra("caseId", caseInfo1.getString("caseNo"));
            intent.putExtra("caseInfo", caseInfo1.getString("exposureProcess"));
            intent.putExtra("templateId", caseInfo1.getString("templateId"));
            v.getContext().startActivity(intent);
        }
    }


    private List<CsDicts> getCaseTemplates(Context context) {
        SharePre userInfo = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String orgId = userInfo.getString("organizationId", "");
        List<CommonTemplate> commonTemplates = EvidenceApplication.db.findAllByWhere(CommonTemplate.class, "orgId = '" + orgId + "'");
        List<CsDicts> caseTemplates = new ArrayList<>();
        for (CommonTemplate commonTemplate : commonTemplates) {
            CsDicts template = new CsDicts();
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "dictKey = '" +
                    commonTemplate.getCaseTypeCode() + "'" + " and " + "rootKey = 'AJLBDM'");
            for (CsDicts csDict : csDicts) {
                template.setDictValue1(csDict.getDictValue1());
            }
            template.setDictValue2(commonTemplate.getSid());
            caseTemplates.add(template);
        }
        return caseTemplates;

    }

    private void setTemplateId(String templateId, String caseId) {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
        if (list != null && list.size() != 0) {
            CsSceneCases csSceneCases = list.get(0);
            csSceneCases.setTemplateId(templateId);
            EvidenceApplication.db.update(csSceneCases);
        }
    }

    private OnAdditionalRecordingListener onAdditionalRecordingListener;



    public interface OnAdditionalRecordingListener{
        void onAdditionalRecording(int position);
    }
}


