package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.QRHelper;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.view.adapter.AddEvidenceViewAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.QrCode;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author maxiran
 */
public class AddEvidence extends Activity implements View.OnClickListener {
    public static final int REQUEST_TAKE_PHOTO = 1000;

    private BaseAdapter adapter;
    private EvidenceExtra evidenceExtra;
    private String father;
    private String caseId;
    private List<BaseView> viewLists;
    private boolean isShow, addRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_evidence);
        final GridView imageGridView = (GridView) findViewById(R.id.gridview);
        isShow = getIntent().getBooleanExtra("isShow", false);
        father = getIntent().getStringExtra("father");
        caseId = getIntent().getStringExtra("caseId");
        String templateId = getIntent().getStringExtra("templateId");
        evidenceExtra = getEvidenceExtra(isShow);
        String mode = getIntent().getStringExtra("mode");
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC, false);
        findViewById(R.id.take_picture).setOnClickListener(this);
        if (BaseView.VIEW.equals(mode) && !addRec)
            findViewById(R.id.camera_qr_layout).setVisibility(View.GONE);
        adapter = new AddEvidenceViewAdapter(evidenceExtra.getId(), templateId, TextUtils.isEmpty(mode) ? BaseView.EDIT : mode, evidenceExtra);
        imageGridView.setAdapter(adapter);
        findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(getFragmentTitle());
        TextView editInfo = (TextView) findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
        editInfo.setVisibility(View.VISIBLE);
        editInfo.setText("完成");
        editInfo.setOnClickListener(this);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        viewLists = ViewUtil.getLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode :
                BaseView.EDIT, AddEvidence.this, container, evidenceExtra.getJson(), evidenceExtra.getCaseId(), father, templateId);
        RelativeLayout qrRelative = (RelativeLayout) findViewById(R.id.scan_qr);
        qrRelative.setOnClickListener(this);
        TextView codeType = (TextView) qrRelative.findViewById(R.id.code_type);
        String typeName = isHasQrCode();
        if (typeName == null) {
            qrRelative.setVisibility(View.GONE);
        } else {
            codeType.setText(typeName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AddEvidenceViewAdapter) adapter).getData();
        adapter.notifyDataSetChanged();
        //如果拍完照就自动保存
        if (isHasPhoto()) {
            if (save()) {
                if (!isExtraHasSave())
                    EvidenceApplication.db.save(evidenceExtra);
                else
                    EvidenceApplication.db.update(evidenceExtra);
                save2Json();
            }
        }
    }


    private String getFragmentTitle() {
        String tableName = getIntent().getStringExtra("father");
        DbModel dbModel = EvidenceApplication.db.findDbModelBySQL("select sceneName from BaseTemp where tableName = '" + tableName + "'");
        return dbModel != null ? dbModel.getString("sceneName") : "";
    }

    /**
     * @param isShow 是否为展示
     */
    private EvidenceExtra getEvidenceExtra(boolean isShow) {
        EvidenceExtra evidenceExtra = null;
        if (!isShow) {
            try {
                evidenceExtra = new EvidenceExtra();
                evidenceExtra.setId(ViewUtil.getUUid());
                evidenceExtra.setFather(father);
                evidenceExtra.setCaseId(caseId);
                evidenceExtra.setSection(evidenceExtra.getId());
                JSONObject jsonObject = new JSONObject("{}");
                jsonObject.put("SECTION", evidenceExtra.getSection());
                evidenceExtra.setJson(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            evidenceExtra = EvidenceApplication.db.findById(getIntent().getStringExtra("id"), EvidenceExtra.class);
        }
        return evidenceExtra;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondary_right_tv:
                if (save()) {
                    if (!isExtraHasSave())
                        EvidenceApplication.db.save(evidenceExtra);
                    else
                        EvidenceApplication.db.update(evidenceExtra);
                    save2Json();
                    finish();
                }

                break;

            case R.id.take_picture:
                // 拍照,此物证已保存到表里
                getAlreadySavedEvidenceExtra();
                Intent intent1 = new Intent(AddEvidence.this, AddEvidencePhoto.class);
                intent1.putExtra("data", "addevidence");
                intent1.putExtra("tabflage", ScenePhotos.tabflage);
                intent1.putExtra("isEvidence", true);
                intent1.putExtra("father", evidenceExtra.getFather());
                intent1.putExtra("id", evidenceExtra.getId());
                intent1.putExtra("caseId", caseId);
                intent1.putExtra(BaseView.ADDREC, addRec);
                startActivityForResult(intent1, REQUEST_TAKE_PHOTO);

                break;

            case R.id.scan_qr:
                if (!addRec) {
                    Intent intent = new Intent();
                    intent.setClass(AddEvidence.this, MipcaActivityCapture.class);
                    intent.putExtra("saveKey", getSaveKey());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (data == null) {
                    return;
                }
                final String filePath = data.getStringExtra("filePath");
                final String fileName = data.getStringExtra("fileName");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap scanBitmap = BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + filePath + fileName);
                        String result = QRHelper.getReult(scanBitmap);
                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
                        resultIntent.putExtras(bundle);
                        resultIntent.setAction(getSaveKey());
                        sendBroadcast(resultIntent);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    public boolean save() {
        String json = ViewUtil.viewSave2Json(this, viewLists, evidenceExtra.getJson());
        if (json == null)
            return false;
        if (!isHasPhoto()) {
            ToastUtil.show(this, "物证必须拍摄照片", Toast.LENGTH_SHORT);
            return false;
        }
        //移除不需要的字段
        List<String> saveCodes = new ArrayList<>();
        List<String> allCodes = new ArrayList<>();
        allCodes.add("BAR_CODE");
        allCodes.add("QR_CODE");
        allCodes.add("RFID_CODE");
        for (BaseView view : viewLists) {
            String saveCode = view.getSaveKey();
            if (TextUtils.equals(allCodes.get(0), saveCode) || TextUtils.equals(allCodes.get(1), saveCode) || TextUtils.equals(allCodes.get(2), saveCode)) {
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

    private String isHasQrCode() {
        for (BaseView view : viewLists) {
            if (view instanceof QrCode)
                return view.getViewName();
        }
        return null;
    }


    private String getSaveKey() {
        for (BaseView view : viewLists) {
            if (view instanceof QrCode)
                return view.getSaveKey();
        }
        return null;
    }

    private EvidenceExtra getAlreadySavedEvidenceExtra() {
        if (!isExtraHasSave())
            EvidenceApplication.db.save(evidenceExtra);
        return evidenceExtra;
    }

    private boolean isExtraHasSave() {
        EvidenceExtra savEdevidenceExtra = EvidenceApplication.db.findById(evidenceExtra.getId(), EvidenceExtra.class);
        return savEdevidenceExtra != null;
    }

    private void save2Json() {
        String caseId = getIntent().getStringExtra("caseId");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, evidenceExtra.getFather() + evidenceExtra.getId());
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

            try {
                DataTemp recDataTemp = recordFileInfo.isAddRec() ? SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData")
                        : SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                recObject.put("refKeyId", !TextUtils.isEmpty(refKey) ? refKey : "");
                recObject.put("type", ViewUtil.getType(recordFileInfo));
                recObject.put("sceneType", recordFileInfo.getFather());
                recDataTemp.setDataType("common_attachment");
                recDataTemp.setData(recObject.toString());
                jsonObject.put("EVIDENCE_PHOTO_ID", jsonObject.get("ATTACHMENT_ID"));
                EvidenceApplication.db.update(recDataTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private List<RecordFileInfo> getRecFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "'");
    }

    private boolean isHasPhoto() {
//        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"fileType = 'png' and caseId = '" + caseId +
//                "' and father = '" + father + "'");
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "' and fileType = 'png'");
        return list != null && list.size() > 0;
    }
}
