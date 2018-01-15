package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddEvidenceEdit extends Activity {


    private EvidenceExtra evidenceExtra;
    private List<BaseView> viewLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_evidence_extra_pop);
        String templateId = getIntent().getStringExtra("templateId");
        String mode = getIntent().getStringExtra("mode");
        String id = getIntent().getStringExtra("id");
        evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        String father = evidenceExtra.getFather();
        ((TextView) findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(ViewUtil.getFragementName(evidenceExtra.getFather()));
        findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
        textView.setVisibility(BaseView.VIEW.equals(mode) ? View.INVISIBLE : View.VISIBLE);
        textView.setText("完成");
               textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save()) {
                    if (!isExtraHasSave())
                        EvidenceApplication.db.save(evidenceExtra);
                    else
                        EvidenceApplication.db.update(evidenceExtra);
                    save2Json();
                    finish();
                }
            }
        });
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        boolean viewWithoutToast = getIntent().getBooleanExtra("viewWithoutToast",false);
        viewLists = ViewUtil.getLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT,AddEvidenceEdit.this, container, evidenceExtra.getJson(), evidenceExtra.getCaseId(), father, templateId);
        for (BaseView baseView : viewLists)
            baseView.setViewWithoutToast(viewWithoutToast);
    }

    public boolean save() {
        String json = ViewUtil.viewSave2Json(this, viewLists, evidenceExtra.getJson());
        if (json == null)
            return false;
        //移除不需要的字段
        List<String> saveCodes = new ArrayList<>();
        List<String> allCodes = new ArrayList<>();
        allCodes.add("BAR_CODE");
        allCodes.add("QR_CODE");
        allCodes.add("RFID_CODE");
        for (BaseView view : viewLists) {
            String saveCode = view.getSaveKey();
            if (TextUtils.equals(allCodes.get(0),saveCode) ||TextUtils.equals(allCodes.get(1),saveCode) ||TextUtils.equals(allCodes.get(2),saveCode)){
                saveCodes.add(saveCode);
            }
        }
        allCodes.removeAll(saveCodes);
        try {
            JSONObject object = new JSONObject(json);
            for (String key : allCodes)
                object.remove(key);
            evidenceExtra.setJson(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            evidenceExtra.setJson(json);
        }

        return true;
    }

    private boolean isExtraHasSave() {
        EvidenceExtra savEdevidenceExtra = EvidenceApplication.db.findById(evidenceExtra.getId(), EvidenceExtra.class);
        return savEdevidenceExtra != null;
    }

    private void save2Json() {
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(evidenceExtra.getCaseId(), "" + evidenceExtra.getFather() + evidenceExtra.getId());
        try {
            JSONObject jsonObject = new JSONObject(evidenceExtra.getJson());
            jsonObject.put("SCENE_TYPE", evidenceExtra.getFather());
            dataTemp.setDataType("scene_investigation_data");
            saveRecFile(jsonObject);
            dataTemp.setData(jsonObject.toString());
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void saveRecFile(JSONObject jsonObject) throws JSONException {
        List<RecordFileInfo> list = getRecFiles();
        if (list == null || list.size() == 0)
            return;
        String refKey = jsonObject.getString("ID");
        for (RecordFileInfo recordFileInfo : list) {
            if ("png".equals(recordFileInfo.getFileType())) {
                String lastId = null;
                try {
                    lastId = jsonObject.getString("ATTACHMENT_ID");
                    jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            try {
                DataTemp recDataTemp = recordFileInfo.isAddRec() ?
                        SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData"):
                         SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                recObject.put("refKeyId", !TextUtils.isEmpty(refKey) ? refKey : "");
                recObject.put("type", ViewUtil.getType(recordFileInfo));
                recObject.put("sceneType", recordFileInfo.getFather());
                recDataTemp.setDataType("common_attachment");
                recDataTemp.setData(recObject.toString());
                if ("png".equals(recordFileInfo.getFileType())) {
                    jsonObject.put("EVIDENCE_PHOTO_ID", jsonObject.get("ATTACHMENT_ID"));
                }
                EvidenceApplication.db.update(recDataTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<RecordFileInfo> getRecFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection()+ "'");
    }
}
