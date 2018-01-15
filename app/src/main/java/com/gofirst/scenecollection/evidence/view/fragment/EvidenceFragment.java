package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.activity.AddEvidence;
import com.gofirst.scenecollection.evidence.view.adapter.EmptyAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ExtractEvidenceFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author maxiran
 *         物证模块
 */

public class EvidenceFragment extends Fragment implements OnClickListener {

    private ListView evidenceList;
    private String father;
    private String caseId;
    private String templateId;
    private String mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.extract_evidence_fragment, container, false);
        Button addNewEvidence = (Button) view.findViewById(R.id.add_new_evidence);
        addNewEvidence.setOnClickListener(this);
        father = getArguments().getString("father");
        caseId = getArguments().getString("caseId");
        templateId = getArguments().getString("templateId");
        mode = getArguments().getString("mode");
        if (mode != null && mode.equals(BaseView.VIEW))
            view.findViewById(R.id.add_new_evidence).setVisibility(View.GONE);
        evidenceList = (ListView) view.findViewById(R.id.extract_evidence_list);
        List<EvidenceExtra> list = getEvidence();
        if (list != null && list.size() != 0)
            evidenceList.setAdapter(new ExtractEvidenceFragmentAdapter(templateId, list,mode,getArguments().getBoolean(BaseView.ADDREC)));
        else
            evidenceList.setAdapter(new EmptyAdapter("你还没有录入"));
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_evidence:
                Intent intent = new Intent(getActivity(), AddEvidence.class);
                intent.putExtra("father", father);
                intent.putExtra("caseId", caseId);
                intent.putExtra("templateId", templateId);
                intent.putExtra("mode", mode);
                intent.putExtra(BaseView.ADDREC, getArguments().getBoolean(BaseView.ADDREC));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<EvidenceExtra> list = getEvidence();
        if (list != null && list.size() != 0)
            evidenceList.setAdapter(new ExtractEvidenceFragmentAdapter(templateId, list,mode,getArguments().getBoolean(BaseView.ADDREC)));
        else
            evidenceList.setAdapter(new EmptyAdapter("你还没有录入"));
    }

    private List<EvidenceExtra> getEvidence() {
        return EvidenceApplication.db.findAllByWhere(EvidenceExtra.class, "father = '" + father + "'" + " and caseId = '" + caseId + "'");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void saveJson() {
        List<EvidenceExtra> evidenceExtras = getEvidence();
        for (EvidenceExtra evidenceExtra : evidenceExtras) {
            //保存物证到dataTemp
            DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, evidenceExtra.getId());
            save2DataTemp(evidenceExtra, dataTemp);
            //保存附件到dataTemp
            saveAttach(evidenceExtra);
        }

    }


    private void saveAttach(EvidenceExtra evidenceExtra) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "'");
        for (RecordFileInfo recordFileInfo : list) {
            //产生附件所需要的data
            EvidenceExtra copyEvidenceExtra = getCopyEvidenceExtra(recordFileInfo, evidenceExtra);
            DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, father + copyEvidenceExtra.getId());
            try {
                JSONObject jsonObject = new JSONObject(evidenceExtra.getJson());
                jsonObject.put("ID", ViewUtil.getUUid());
                jsonObject.put("SCENE_TYPE", evidenceExtra.getFather());
                jsonObject.put("ATTACHMENT_ID", evidenceExtra.getId());
                dataTemp.setDataType("scene_investigation_data");
                dataTemp.setData(jsonObject.toString());
                EvidenceApplication.db.update(dataTemp);
                //产生附件本身data
                DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                recObject.put("refKeyId", evidenceExtra.getId());
                recDataTemp.setDataType("common_attachment");
                recDataTemp.setData(recObject.toString());
                EvidenceApplication.db.update(recDataTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private EvidenceExtra getCopyEvidenceExtra(RecordFileInfo recordFileInfo, EvidenceExtra evidenceExtra) {
        EvidenceExtra copyEvidenceExtra = EvidenceApplication.db.findById(recordFileInfo.getId(), EvidenceExtra.class);
        String id = ViewUtil.getUUid();
        if (copyEvidenceExtra == null) {
            copyEvidenceExtra = (EvidenceExtra) evidenceExtra.clone();
            copyEvidenceExtra.setId(id);
            copyEvidenceExtra.setFather(evidenceExtra.getFather() + "copy");
            EvidenceApplication.db.save(copyEvidenceExtra);
        }
        return EvidenceApplication.db.findById(id, EvidenceExtra.class);
    }

    private void save2DataTemp(EvidenceExtra evidenceExtra, DataTemp dataTemp) {
        try {
            JSONObject jsonObject = new JSONObject(evidenceExtra.getJson());
            jsonObject.put("ID", evidenceExtra.getId());
            jsonObject.put("SCENE_TYPE", evidenceExtra.getFather());
            jsonObject.put("SECTION", evidenceExtra.getSection());
            dataTemp.setDataType("scene_investigation_data");
            dataTemp.setData(jsonObject.toString());
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAnchorRecFiles() throws JSONException {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "father = '"
                + father + "'" + " and caseId = '" + caseId + "' and belongTo = 'anchor'");
        for (RecordFileInfo recordFileInfo : list) {
            //产生附件本身data
            DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
            JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
            recDataTemp.setDataType("common_attachment");
            recDataTemp.setData(recObject.toString());
            EvidenceApplication.db.update(recDataTemp);
        }
    }
}
