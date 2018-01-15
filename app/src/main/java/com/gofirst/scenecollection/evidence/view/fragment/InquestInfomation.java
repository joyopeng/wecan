package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CasePeople;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.model.InquestInfo;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.Reporter;
import com.gofirst.scenecollection.evidence.view.customview.AudioEditText;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author maxiran
 */
public class InquestInfomation extends Fragment implements OnClickListener {

    private TextView startTime;
    private TextView endTime;
    private EditText address;
    private AudioEditText basicInfo;
    private String caseId;
    private InquestInfo inquestInfo;
    private String father,templateId;
    private EditText mainHandler,waver;
    private TextView obsText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inquest_infomation, container, false);
        startTime = (TextView) view.findViewById(R.id.start_time);
        endTime = (TextView) view.findViewById(R.id.end_time);
        address = (EditText) view.findViewById(R.id.address);
        mainHandler =(EditText) view.findViewById(R.id.main_handler);
        waver = (EditText) view.findViewById(R.id.waver);
        basicInfo = (AudioEditText) view.findViewById(R.id.basic_info);
        obsText = (TextView) view.findViewById(R.id.observer);
        obsText.setOnClickListener(this);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        templateId = getArguments().getString("templateId");

        basicInfo.setArgs(caseId, father, "勘验基本情况");
        basicInfo.initView(BaseView.EDIT);
        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        SharePre sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        caseId = getArguments().getString("caseId");
        inquestInfo = getInquestInfo();
        String originAddress = inquestInfo.getINVESTIGATION_PLACE();
        address.setText(originAddress == null || originAddress.equals("") ? sharePre.getString("address", "无法定位") : originAddress);
        String start = inquestInfo.getINVESTIGATION_DATE_FROM();
        startTime.setText(start != null ? start : "点击进行选择");

        String end = inquestInfo.getINVESTIGATION_DATE_TO();
        endTime.setText(end != null ? end : "点击进行选择");

        String add = inquestInfo.getINVESTIGATION_PLACE();
        address.setText(add != null ? add : sharePre.getString("address", "无法定位"));

        String hand = inquestInfo.getINVESTIGATOR();
        mainHandler.setText(hand != null ? hand : "");
        CasePeople observer = getCasePeople("2");
        String name = observer.getName();
        obsText.setText(name != null ? name : "点击填写");
        String director = inquestInfo.getDIRECTOR();
        waver.setText(director != null ? director : "");
        String basic = inquestInfo.getBasicInfo();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                inquestInfo.setINVESTIGATION_DATE_FROM(startTime.getText().toString());
                inquestInfo.setINVESTIGATION_DATE_TO(endTime.getText().toString());
                inquestInfo.setINVESTIGATION_PLACE(address.getText().toString());
                inquestInfo.setINVESTIGATOR(mainHandler.getText().toString());
                inquestInfo.setDIRECTOR(waver.getText().toString());
                inquestInfo.setINVESTIGATION_NO(getInvestgationNum());
                EvidenceApplication.db.update(inquestInfo);
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                saveJson();
                break;

            case R.id.start_time:
                /*DateTimePickDialog dateTimePickDialogUtil = new DateTimePickDialog(getActivity(), "");
                dateTimePickDialogUtil.dateTimePicKDialog(startTime);*/
                break;

            case R.id.end_time:
                /*DateTimePickDialog dateTimePickDialogUtil1 = new DateTimePickDialog(getActivity(), "");
                dateTimePickDialogUtil1.dateTimePicKDialog(endTime);*/
                break;

            case R.id.observer:
                CasePeople observer = getCasePeople("2");
                Intent intent = new Intent(v.getContext(), Reporter.class);
                intent.putExtra("people_type", "见证人");
                intent.putExtra("uuid", observer.getId());
                intent.putExtra("caseId", caseId);
                intent.putExtra("father", father);
                startActivity(intent);
                break;
        }
    }

    private InquestInfo getInquestInfo() {
        List<InquestInfo> list = EvidenceApplication.db.findAllByWhere(InquestInfo.class, "caseId = '" + caseId + "'");
        if (list == null || list.size() == 0) {
            InquestInfo inquestInfo = new InquestInfo();
            inquestInfo.setCaseId(caseId);
            inquestInfo.setSceneType("scene_investigation");
            inquestInfo.setId(UUID.randomUUID().toString().replace("-", ""));
            EvidenceApplication.db.save(inquestInfo);
        }
        return EvidenceApplication.db.findAllByWhere(InquestInfo.class, "caseId = '" + caseId + "'").get(0);
    }


    private void saveJson() {
        InquestInfo inquestInfo = getInquestInfo();
        String father = getArguments().getString("father");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, father);
        String json = com.alibaba.fastjson.JSON.toJSONString(inquestInfo);
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("ATTACHMENT_ID", inquestInfo.getId());
            jsonObject.put("SCENE_INVESTIGATION",templateId);
            dataTemp.setData(jsonObject.toString());
            dataTemp.setDataType("scene_investigation_data");
            EvidenceApplication.db.update(dataTemp);
            if (basicInfo.getRecFileInfo() == null) {
            } else {
                ViewUtil.saveAudioAttachment(basicInfo.getRecFileInfo(), inquestInfo.getId(), inquestInfo.getSceneType());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String getInvestgationNum() {
        SharePre user_info = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        String organizationId = user_info.getString("organizationId", "");
        long lastNo = user_info.getLong("lastNo", 0);
        if (lastNo == 9999) {
            lastNo = 1;
        }
        long currentNo = 1 + lastNo;
        DecimalFormat format = new DecimalFormat("0000");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        String copId = "";
        List<HyOrganizations> list = EvidenceApplication.db.findAllByWhere(HyOrganizations.class, "organizationId = '" + organizationId + "'");
        if (list != null && list.size() != 0) {
            copId = list.get(0).getOrganizationNo();
        }
        user_info.put("lastNo", currentNo);
        user_info.commit();
        return "K" + copId + simpleDateFormat.format(new Date()) + format.format(currentNo);
    }

    private CasePeople getCasePeople(String peopleType) {
        List<CasePeople> list = EvidenceApplication.db.findAllByWhere(CasePeople.class, "caseInfo = '" + inquestInfo.getId() + "'");
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
        tempPeople.setCaseInfo(inquestInfo.getId());
        EvidenceApplication.db.save(tempPeople);
        return tempPeople;
    }

    @Override
    public void onResume() {
        CasePeople observer = getCasePeople("2");
        String name = observer.getName();
        obsText.setText(name != null ? name : "点击填写");
        super.onResume();
    }
}
