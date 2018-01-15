package com.gofirst.scenecollection.evidence.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.ProspectInfo;
import com.gofirst.scenecollection.evidence.view.customview.SegmentedGroup;
import com.gofirst.scenecollection.evidence.view.customview.SpinnerPop;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.util.List;
import java.util.UUID;

/**
 * @author maxiran
 */
public class ProspectInfoFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{

    private String caseId;
    private ProspectInfo prospectInfo;
    private TextView caseClass;
    private EditText appointUnit,caseGov,handler;
    private TextView dispatchTime;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prospect_info_fragment,container,false);
        SegmentedGroup appointGroup = (SegmentedGroup)view.findViewById(R.id.appoint_group);
        appointGroup.setOnCheckedChangeListener(this);
        caseId = getArguments().getString("caseId");
        prospectInfo = getProspectInfo();
        view.findViewById(R.id.save).setOnClickListener(this);
        caseClass = (TextView)view.findViewById(R.id.case_class);
        appointUnit = (EditText) view.findViewById(R.id.appoint_unit);
        caseClass.setOnClickListener(this);
        dispatchTime = (TextView)view.findViewById(R.id.dispatch_time);
        handler = (EditText)view.findViewById(R.id.handler);
        caseGov = (EditText)view.findViewById(R.id.case_gov);
        dispatchTime.setOnClickListener(this);
        if(getArguments().getString("mode").equals("find")){
            view.findViewById(R.id.save).setVisibility(View.GONE);
        }
        String dispatchTimes = prospectInfo.getReceivedDate();
        dispatchTime.setText(dispatchTimes != null ? dispatchTimes : "");
        String caseClassInfo = prospectInfo.getCaseType();
        caseClass.setText(caseClassInfo != null ? caseClassInfo : "");
        ViewUtil.radioGroupSetCheckByValue(appointGroup,prospectInfo.getAssignedWayT());
        String caseGovInfo = prospectInfo.getCaseGov();
        caseGov.setText(caseGovInfo != null ? caseGovInfo : "");
        String appointUnitInfo = prospectInfo.getAssignedBy();
        appointUnit.setText(appointUnitInfo != null ? appointUnitInfo : "");
        String handlerInfo = prospectInfo.getHandler();
        handler.setText(handlerInfo != null ? handlerInfo : "");
        return view;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton) group.findViewById(checkedId);
        String value = button.getText().toString();
        prospectInfo.setAssignedWayT(value);
        prospectInfo.setAssignedWayS(getAppointCode(value));
    }

    private ProspectInfo getProspectInfo() {
        List<ProspectInfo> list = EvidenceApplication.db.findAllByWhere(ProspectInfo.class, "caseId = '" + caseId + "'");
        if (list == null || list.size() == 0) {
            ProspectInfo prospectInfo = new ProspectInfo();
            prospectInfo.setCaseId(caseId);
            prospectInfo.setId(UUID.randomUUID().toString().replace("-",""));
            prospectInfo.setSceneType("scene_investigation");
            EvidenceApplication.db.save(prospectInfo);
        }
        return EvidenceApplication.db.findAllByWhere(ProspectInfo.class, "caseId = '" + caseId + "'").get(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                prospectInfo.setInitServerNo("230716000000");
                prospectInfo.setAssignedBy(appointUnit.getText().toString());
                prospectInfo.setReceivedBy("张伟");
                prospectInfo.setReceivedDate(dispatchTime.getText().toString());
                prospectInfo.setCaseType(caseClass.getText().toString());
                prospectInfo.setCaseGov(caseGov.getText().toString());
                prospectInfo.setHandler(handler.getText().toString());
                EvidenceApplication.db.update(prospectInfo);
                saveJson();
                Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
                break;

            case R.id.case_class:
               new SpinnerPop(getActivity(),caseClass,"AJLBDM","案件性质");
                break;

            case R.id.dispatch_time:
               /* DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(getActivity(),"");
                dateTimePickDialog.dateTimePicKDialog(dispatchTime);*/
                break;

        }
    }

    private String getAppointCode(String value){
       List<CsDicts> list =  EvidenceApplication.db.findAllByWhere(CsDicts.class,"rootKey = 'XCKYJJFSDM' and dictValue1 = '" + value + "'");
        return list == null || list.size() == 0 ? "" : list.get(0).getDictKey();
    }


    private void saveJson() {
        ProspectInfo prospectInfo = getProspectInfo();
        String father = getArguments().getString("father");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId,father);
        String json = com.alibaba.fastjson.JSON.toJSONString(prospectInfo);
        dataTemp.setData(json);
        dataTemp. setDataType("scene_reception_dispatch");
        EvidenceApplication.db.update(dataTemp);
    }
}
