package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseInfo;
import com.gofirst.scenecollection.evidence.model.CasePeople;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.view.activity.Reporter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.SegmentedGroup;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * @author maxiran
 */
public class CaseInfoFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private CaseInfo caseInfo;
    private TextView caseHappenTime, victimPeople, reportPeople;
    private TextView caseEndTime;
    private Spinner flipSpinner;
    private String caseId;
    private String father;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.case_info_fragment, null);
        view.findViewById(R.id.add_new).setOnClickListener(this);
        SegmentedGroup homicideGroup = (SegmentedGroup) view.findViewById(R.id.is_homicide);
        SegmentedGroup normalGroup = (SegmentedGroup) view.findViewById(R.id.is_normal);
        caseHappenTime = (TextView) view.findViewById(R.id.case_start_time);
        caseEndTime = (TextView) view.findViewById(R.id.case_end_time);
        flipSpinner = (Spinner) view.findViewById(R.id.change_level_spinner);
        victimPeople = (TextView) view.findViewById(R.id.victim_people);
        reportPeople = (TextView) view.findViewById(R.id.report_people);
        /*AudioEditText audioEditText = (AudioEditText)view.findViewById(R.id.case_find_process);
        audioEditText.initView(BaseView.EDIT,"案件发现过程","","case_find_process");
        audioEditText.setArgs(caseId,father,"案件发现过程");*/
        victimPeople.setOnClickListener(this);
        reportPeople.setOnClickListener(this);
        caseHappenTime.setOnClickListener(this);
        caseEndTime.setOnClickListener(this);
        homicideGroup.setOnCheckedChangeListener(this);
        normalGroup.setOnCheckedChangeListener(this);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        if (getArguments().getString("mode").equals("find")) {
            view.findViewById(R.id.add_new).setVisibility(View.GONE);
        }
        caseInfo = getCaseInfo();
        caseInfo.setCaseId(caseId);
        String caseHappen = caseInfo.getCaseHappenTime();
        caseHappenTime.setText(caseHappen == null ? "未填写" : caseHappen);
        String caseEnd = caseInfo.getCaseEndTime();
        caseEndTime.setText(caseEnd == null ? "未填写" : caseEnd);
        String flip = caseInfo.getFlipLevel();
        SceneInfoFragment.setSpinnerItemSelectedByValue(flipSpinner, flip == null ? "" : flip);
        String hvalue = null;
        if (caseInfo.getIsHomicide() != null)
            hvalue = caseInfo.getIsHomicide().equals("1") ? "是" : "否";
        String nvalue = null;
        if (caseInfo.getIsNormal() != null)
            nvalue = caseInfo.getIsNormal().equals("1") ? "是" : "否";
        ViewUtil.radioGroupSetCheckByValue(homicideGroup, hvalue);
        ViewUtil.radioGroupSetCheckByValue(normalGroup, nvalue);
        getPeopleInfo();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new:
                caseInfo.setFlipLevel(flipSpinner.getSelectedItem().toString());
                caseInfo.setCaseHappenTime(caseHappenTime.getText().toString());
                caseInfo.setCaseEndTime(caseEndTime.getText().toString());
                EvidenceApplication.db.update(caseInfo);
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                saveJson();
                break;
            case R.id.victim_people:
                CasePeople victim = getCasePeople("1");
                Intent intent = new Intent(v.getContext(), Reporter.class);
                intent.putExtra("people_type", "被害人");
                intent.putExtra("uuid", victim.getId());
                intent.putExtra("caseId", caseId);
                intent.putExtra("father", father);
                startActivity(intent);
                break;

            case R.id.report_people:
                CasePeople report = getCasePeople("0");
                Intent intent1 = new Intent(v.getContext(), Reporter.class);
                intent1.putExtra("people_type", "报案人");
                intent1.putExtra("uuid", report.getId());
                intent1.putExtra("caseId", caseId);
                intent1.putExtra("father", father);
                startActivity(intent1);
                break;

            case R.id.case_start_time:
               /* DateTimePickDialog dateTimePickDialogUtil = new DateTimePickDialog(getActivity(), "");
                dateTimePickDialogUtil.dateTimePicKDialog(caseHappenTime);*/
                break;

            case R.id.case_end_time:
               /* DateTimePickDialog dateTimePickDialogUtil1 = new DateTimePickDialog(getActivity(), "");
                dateTimePickDialogUtil1.dateTimePicKDialog(caseEndTime);*/
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton) group.findViewById(checkedId);
        switch (group.getId()) {

            case R.id.is_homicide:
                String value = button.getText().equals("是") ? "1" : "0";
                caseInfo.setIsHomicide(value);
                break;

            case R.id.is_normal:
                String value1= button.getText().equals("是") ? "1" : "0";
                caseInfo.setIsNormal(value1);
                break;

        }

    }

    public static List<CommonExtField> getLayoutInfo(String mode,String templateId, String sceneType) {
        return EvidenceApplication.db.findAllByWhere(CommonExtField.class, "templateId = '" + templateId + "'" +
                " and sceneType = '" + sceneType + "' and deleteFlag = '0'",(BaseView.EDIT.equals(mode) ? "positionSort" : "viewPositionSort") + " asc");

    }


    private CaseInfo getCaseInfo() {
        List<CaseInfo> list = EvidenceApplication.db.findAllByWhere(CaseInfo.class, "caseId = '" + caseId + "'");
        if (list == null || list.size() == 0) {
            caseInfo = new CaseInfo();
            caseInfo.setCaseId(caseId);
            caseInfo.setId(ViewUtil.getUUid());
            EvidenceApplication.db.save(caseInfo);
        }
        return EvidenceApplication.db.findAllByWhere(CaseInfo.class, "caseId = '" + caseId + "'").get(0);
    }

    private CasePeople getCasePeople(String peopleType) {
        List<CasePeople> list = EvidenceApplication.db.findAllByWhere(CasePeople.class, "caseInfo = '" + caseInfo.getId() + "'");
        if (list != null && list.size() != 0) {
            for (CasePeople casePeople : list) {
                if (casePeople.getPeopleType().equals(peopleType)) {
                    return casePeople;
                }

            }
        }
        return getNewCasePeople(peopleType);
    }

    private CasePeople getNewCasePeople(String peopleType) {
        CasePeople tempPeople = new CasePeople();
        tempPeople.setPeopleType(peopleType);
        tempPeople.setId(UUID.randomUUID().toString().replace("-", ""));
        tempPeople.setCaseId(caseId);
        tempPeople.setSceneType("scene_victim");
        tempPeople.setCaseInfo(caseInfo.getId());
        EvidenceApplication.db.save(tempPeople);
        return tempPeople;
    }

    private void saveJson() {
        CaseInfo caseInfo = getCaseInfo();
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, father);
        String json = com.alibaba.fastjson.JSON.toJSONString(caseInfo);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            dataTemp.setData(jsonObject.toString());
            dataTemp.setDataType("scene_law_case");
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPeopleInfo() {
        CasePeople report = getCasePeople("0");
        String name = report.getName();
        reportPeople.setText(name != null ? name : "点击编辑");
        CasePeople victim = getCasePeople("1");
        String vName = victim.getName();
        victimPeople.setText(vName != null ? vName : "点击编辑");
    }

    @Override
    public void onResume() {
        getPeopleInfo();
        super.onResume();
    }
}
