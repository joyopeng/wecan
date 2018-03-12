package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.BaseTempField;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.model.LostGood;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SceneFileConjunction;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.LostGoodDetail;
import com.gofirst.scenecollection.evidence.view.fragment.CaseInfoFragment;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ViewUtil {

    public static BaseView getView(Context context, String viewKey, LinearLayout container) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        if (viewKey.equals("TEXT")) {
            Text normalEditText = new Text(context);
            container.addView(normalEditText, params);
            return normalEditText;
        } else if (viewKey.equals("TEXT_RECORD")) {
           /* AudioEditText audioEditText = new AudioEditText(context);
            container.addView(audioEditText, params);
            return audioEditText;*/
        } else if (viewKey.equals("POP_LIST") || viewKey.equals("POP_LIST_PLAIN")) {
            PopList popList = new PopList(context);
            container.addView(popList, params);
            return popList;
        } else if (viewKey.equals("DATETIME")) {
            DateTime dateTime = new DateTime(context);
            container.addView(dateTime, params);
            return dateTime;
        } else if (viewKey.equals("DATE")) {
            Date date = new Date(context);
            container.addView(date, params);
            return date;
        } else if (viewKey.equals("EMPLOYEE_MULTI")) {
            /*MultipleChoicesDialog MultipleChoicesDialog = new MultipleChoicesDialog(context);
            container.addView(MultipleChoicesDialog, params);
            return MultipleChoicesDialog;*/
            PopListPerson PopListPerson = new PopListPerson(context);
            container.addView(PopListPerson, params);
            return PopListPerson;
        } else if (viewKey.equals("TITL_RADIO")) {
            TitlRadio titlRadio = new TitlRadio(context);
            container.addView(titlRadio, params);
            return titlRadio;
        } else if (viewKey.equals("CAPTURE_SIGN")) {
            CaptureSign captureSign = new CaptureSign(context);
            container.addView(captureSign, params);
            return captureSign;
        } else if (viewKey.equals("HIDDEN")) {
            HideFieldClass hideFieldClass = new HideFieldClass(context);
            container.addView(hideFieldClass, params);
            return hideFieldClass;
        } else if (viewKey.equals("AREA")) {
            MultiLevelList multiLevelList = new MultiLevelList(context);
            container.addView(multiLevelList, params);
            return multiLevelList;
        } else if (viewKey.equals("ORG")) {
            Text normalEditText = new Text(context);
            normalEditText.setIsOrg();
            container.addView(normalEditText, params);
            return normalEditText;
        } else if (viewKey.equals("POP_LIST_YES_NO")) {
            PopListYesNo popListYesNo = new PopListYesNo(context);
            container.addView(popListYesNo, params);
            return popListYesNo;
        } else if (viewKey.equals("EMPLOYEE")) {
            SingleSelectionPeopleDialog singleSelectionPeopleDialog = new SingleSelectionPeopleDialog(context);
            container.addView(singleSelectionPeopleDialog, params);
            return singleSelectionPeopleDialog;
        } else if (viewKey.equals("CAPTURE_IC")) {
            PhotoDialog photoDialog = new PhotoDialog(context);
            container.addView(photoDialog, params);
            return photoDialog;
        } else if (viewKey.equals("QR_SCAN") || viewKey.equals("BAR_SCAN")) {
            QrCode qrCode = new QrCode(context);
            container.addView(qrCode, params);
            return qrCode;
        } else if (viewKey.equals("POP_LIST_MULTI_PLAIN")) {
            MultipleChoicesLightDialog multipleChoicesLightDialog = new MultipleChoicesLightDialog(context);
            container.addView(multipleChoicesLightDialog, params);
            return multipleChoicesLightDialog;
        } else if (viewKey.equals("RFID_SCAN")) {
            RFID rfid = new RFID(context);
            container.addView(rfid, params);
            return rfid;
        }
        return null;
    }


    public static String getFormatTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getUUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void saveAudioAttachment(RecordFileInfo recordFileInfo, String attchmentId, String sceneType) {
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(),
                recordFileInfo.getFather() + recordFileInfo.getChild());
        String json = com.alibaba.fastjson.JSON.toJSONString(recordFileInfo);
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.remove("id");
            jsonObject.put("id", attchmentId);
            jsonObject.put("type", "2");
            jsonObject.put("refKeyId", "");
            jsonObject.put("sceneType", sceneType);
            dataTemp.setData(jsonObject.toString());
            dataTemp.setDataType("common_attachment");
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static DataTemp getDataTemp(String caseId, String father, String dataType) {
        List<DataTemp> list = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        if (list == null || list.size() == 0) {
            DataTemp dataTemp = new DataTemp();
            dataTemp.setId(UUID.randomUUID().toString().replace("-", ""));
            dataTemp.setCaseId(caseId);
            dataTemp.setFather(father);
            dataTemp.setDataType(dataType);
            EvidenceApplication.db.save(dataTemp);
        }
        return EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'").get(0);
    }


    public static DataTemp getDataTemp(String caseId, String father) {
        List<DataTemp> list = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        if (list == null || list.size() == 0) {
            DataTemp dataTemp = new DataTemp();
            dataTemp.setId(UUID.randomUUID().toString().replace("-", ""));
            dataTemp.setCaseId(caseId);
            dataTemp.setFather(father);
            EvidenceApplication.db.save(dataTemp);
        }
        return EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'").get(0);
    }

    public static void radioGroupSetCheckByValue(RadioGroup radioGroup, String value) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (value != null)
                radioButton.setChecked(value.equals(radioButton.getText().toString()));
        }
        if (radioGroup.getCheckedRadioButtonId() == -1)
            ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }

    public static String safeGetJsonValue(String key, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                if (key.equals(iterator.next())) {
                    return jsonObject.getString(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static List<BaseView> getLayoutBaseViewLists(String mode, Activity context, LinearLayout containerLayout, String json, String... args) {
        if (args == null || args.length < 3)
            return null;
        List<CommonExtField> commonExtFieldList = CaseInfoFragment.getLayoutInfo(mode, args[2], args[1]);
        final List<BaseView> viewLists = new ArrayList<>();
        for (CommonExtField commonExtField : commonExtFieldList) {
            BaseView baseView = getView(context, commonExtField.getViewId(), containerLayout);
            if (baseView != null) {
                if (baseView instanceof AudioEditText) {
                    AudioEditText audioEditText = (AudioEditText) baseView;
                    audioEditText.setArgs(args[0], args[1], commonExtField.getName());
                } else if (baseView instanceof PopList) {
                    PopList popList = (PopList) baseView;
                    popList.setShowView(containerLayout);
                    popList.setRootKey(commonExtField.getDictType());
                }

                /*else if (baseView instanceof PopListPerson) {
                    PopListPerson popListPerson = (PopListPerson) baseView;
                    popListPerson.setShowView(containerLayout);
                    popListPerson.setRootKey();
                }*/

                else if (baseView instanceof CaptureSign) {
                    CaptureSign captureSign = (CaptureSign) baseView;
                    captureSign.setArgs(context, args[0], args[1], commonExtField.getName());
                    if (args.length == 4)
                        captureSign.setSection(args[3]);
                } else if (baseView instanceof TitlRadio) {
                    TitlRadio titlRadio = (TitlRadio) baseView;
                    titlRadio.setParentKey(commonExtField.getDictType());
                } else if (baseView instanceof HideFieldClass) {
                    HideFieldClass hideFieldClass = (HideFieldClass) baseView;
                    hideFieldClass.setCaseId(args[0]);
                    hideFieldClass.setDefaultValue(commonExtField.getDefaultValue());
                } else if (baseView instanceof Text) {
                    Text text = (Text) baseView;
                    text.setReg(commonExtField.getViewFormat());
                } else if (baseView instanceof PhotoDialog) {
                    PhotoDialog photoDialog = (PhotoDialog) baseView;
                    photoDialog.setArgs(context, args[0], args[1], commonExtField.getName());
                    if (args.length == 4)
                        photoDialog.setSection(args[3]);
                }

                String saveKey = commonExtField.getField();
                String displayValue = safeGetJsonValue(baseView.isID() ? saveKey + "_NAME" : saveKey, json);
                String[] defValues = getViewDefault(context, args[0], commonExtField.getDefaultValue(), commonExtField.getDictType(), commonExtField.getField()).split(";");
                StringBuilder defId = new StringBuilder();
                StringBuilder defName = new StringBuilder();
                for (int i = 0; i < defValues.length; i++) {
                    String[] values = defValues[i].split(",");
                    if (values.length > 1) {
                        defId.append(values[0]);
                        defName.append(values[1]);
                    } else if (values.length > 0) {
                        defName.append(values[0]);
                    }
                    if (i != defValues.length - 1) {
                        defId.append(",");
                        defName.append(",");
                    }
                }
                String text = TextUtils.isEmpty(displayValue) ? defName.toString() : displayValue;
                if ("PROTECTOR_ORG".equals(saveKey)) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (!jsonObject.has("PROTECTOR")) {
                            text = "";
                        }
                    } catch (JSONException e) {
                    }
                }
                baseView.initView(mode,
                        commonExtField.getName(),
                        isRootId(args[1], saveKey) ? args[0] : text,
                        saveKey,
                        commonExtField.getViewMinor(),
                        commonExtField.getDataType(),
                        commonExtField.getViewRequiredFlag());
                if (baseView.isID()) {
                    String saveId = safeGetJsonValue(saveKey, json);
                    baseView.setID(TextUtils.isEmpty(saveId) ? defId.toString() : saveId);
                }
                viewLists.add(baseView);
            }

        }
        return viewLists.size() != 0 ? viewLists : getLayoutBackupBaseViewLists(mode, context, containerLayout, json, args);
    }


    public static List<BaseView> getLayoutBackupBaseViewLists(String mode, Activity context, LinearLayout containerLayout, String json, String... args) {
        if (args == null || args.length < 3)
            return null;
        List<BaseTempField> commonExtFieldList = EvidenceApplication.db.findAllByWhere(BaseTempField.class, "sceneType = '" + args[1]
                + "' and deleteFlag = '0'", (BaseView.EDIT.equals(mode) ? "positionSort" : "viewPositionSort") + " asc");
        final List<BaseView> viewLists = new ArrayList<>();
        for (BaseTempField commonExtField : commonExtFieldList) {
            BaseView baseView = getView(context, commonExtField.getViewid(), containerLayout);
            if (baseView != null) {
                if (baseView instanceof AudioEditText) {
                    AudioEditText audioEditText = (AudioEditText) baseView;
                    audioEditText.setArgs(args[0], args[1], commonExtField.getName());
                } else if (baseView instanceof PopList) {
                    PopList popList = (PopList) baseView;
                    popList.setShowView(containerLayout);
                    popList.setRootKey(commonExtField.getDictType());
                }
                /*else if (baseView instanceof PopListPerson) {
                    PopListPerson popListPerson = (PopListPerson) baseView;
                    popListPerson.setShowView(containerLayout);
                    popListPerson.setRootKey("117823");
                }*/
                else if (baseView instanceof CaptureSign) {
                    CaptureSign captureSign = (CaptureSign) baseView;
                    captureSign.setArgs(context, args[0], args[1], commonExtField.getName());
                    if (args.length == 4)
                        captureSign.setSection(args[3]);
                } else if (baseView instanceof TitlRadio) {
                    TitlRadio titlRadio = (TitlRadio) baseView;
                    titlRadio.setParentKey(commonExtField.getDictType());
                } else if (baseView instanceof HideFieldClass) {
                    HideFieldClass hideFieldClass = (HideFieldClass) baseView;
                    hideFieldClass.setCaseId(args[0]);
                    hideFieldClass.setDefaultValue(commonExtField.getDefaultValue());
                } else if (baseView instanceof Text) {
                    Text text = (Text) baseView;
                    text.setReg(commonExtField.getViewFormat());
                } else if (baseView instanceof PhotoDialog) {
                    PhotoDialog photoDialog = (PhotoDialog) baseView;
                    photoDialog.setArgs(context, args[0], args[1], commonExtField.getName());
                    if (args.length == 4)
                        photoDialog.setSection(args[3]);
                }
                String saveKey = commonExtField.getField();
                String displayValue = safeGetJsonValue(baseView.isID() ? saveKey + "_NAME" : saveKey, json);
                String[] defValues = getViewDefault(context, args[0], commonExtField.getDefaultValue(), commonExtField.getDictType(), commonExtField.getField()).split(";");
                StringBuilder defId = new StringBuilder();
                StringBuilder defName = new StringBuilder();
                for (int i = 0; i < defValues.length; i++) {
                    String[] values = defValues[i].split(",");
                    if (values.length > 1) {
                        defId.append(values[0]);
                        defName.append(values[1]);
                    } else if (values.length > 0) {
                        defName.append(values[0]);
                    }
                    if (i != defValues.length - 1) {
                        defId.append(",");
                        defName.append(",");
                    }
                }
                String text = TextUtils.isEmpty(displayValue) ? defName.toString() : displayValue;
                baseView.initView(mode,
                        commonExtField.getName(),
                        isRootId(args[1], saveKey) ? args[0] : text,
                        saveKey,
                        commonExtField.getViewMinor(),
                        commonExtField.getDataType(),
                        commonExtField.getViewRequiredFlag());
                if (baseView.isID()) {
                    String saveId = safeGetJsonValue(saveKey, json);
                    baseView.setID(TextUtils.isEmpty(saveId) ? defId.toString() : saveId);
                }
                viewLists.add(baseView);

            }
        }
        return viewLists;
    }

    private static boolean isRootId(String father, String saveKey) {
        return "SCENE_INVESTIGATION_EXT".equals(father) && "ID".equals(saveKey);
    }

    public static void getSecLayoutBaseViewLists(String mode, Activity context, LinearLayout containerLayout, String caseId, String father, String templateId, String addRec) {
        List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class,
                "templateLevel = '2' and templateUpName = '" + father + "' and templateId = '" + templateId + "'", "positionSort asc");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
            SecondaryView secondaryView = new SecondaryView(context);
            containerLayout.addView(secondaryView, params);
            secondaryView.initView(mode, commonTemplateDetail.getSceneName(), safeGetSecondViewValue(caseId, commonTemplateDetail.getTableName()), commonTemplateDetail.getTableName(),
                    caseId, commonTemplateDetail.getTableName(), templateId, addRec);
        }
    }

    public static String safeGetSecondViewValue(String caseId, String tableName) {
        int num = EvidenceApplication.db.findAllByWhere(LostGood.class, "caseId = '" + caseId + "' and father = '" + tableName + "'").size();
        if (num == 0) {
            return "";
        }
        return "共" + num + (tableName.equals("SCENE_LOST_GOODS") ? "件" : "人");
    }

    public static String viewSave2Json(Context context, List<BaseView> viewLists, String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            for (BaseView baseView : viewLists) {
                // 如果是必填字段且没填值则不通过
                if (isRequireField(baseView.getIsRequireField())) {
                    if (!baseView.validate()) {
                        Toast.makeText(context, baseView.getViewName() + "是必填项 ！", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
                // 非必填字段只有填值后才保存
                if (baseView.validate()) {
                    jsonObject.put(baseView.getSaveKey(), baseView.getText());
                    baseView.saveName(jsonObject);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject != null ? jsonObject.toString() : "{}";
    }

    public static String viewSave2JsonNoRequire(Context context, List<BaseView> viewLists, String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            for (BaseView baseView : viewLists) {
//                String saveKey = baseView.getSaveKey();
//                if("INVESTIGATOR_IDS".equals(saveKey)){
//                }
                if (baseView.validate()) {
                    jsonObject.put(baseView.getSaveKey(), baseView.getText());
                    baseView.saveName(jsonObject);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject != null ? jsonObject.toString() : "{}";
    }

    public static CsSceneCases getCsSceneCasesById(String id) {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "id = '" + id + "'");
        CsSceneCases csc = null;
        if (list == null || list.size() == 0) {
            csc = new CsSceneCases();
            csc.setId(id);
            EvidenceApplication.db.save(csc);
        } else {
            csc = list.get(0);
        }
        return csc;
    }

    public static CsSceneCases getCsSceneCasesByCaseId(String caseId) {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
        CsSceneCases csc = null;
        if (list == null || list.size() == 0) {
            csc = new CsSceneCases();
            csc.setId(ViewUtil.getUUid());
            csc.setCaseNo(caseId);
            EvidenceApplication.db.save(csc);
        } else {
            csc = list.get(0);
        }
        return csc;
    }

    public static CaseBasicInfo getCaseBasicInfo(String caseId, String father) {
        List<CaseBasicInfo> list = EvidenceApplication.db.findAllByWhere(CaseBasicInfo.class, "caseId = '" + caseId + "' and father = '" + father + "'");
        CaseBasicInfo caseBasicInfo = null;
        if (list == null || list.size() == 0) {
            caseBasicInfo = new CaseBasicInfo();
            caseBasicInfo.setId(ViewUtil.getUUid());
            caseBasicInfo.setCaseId(caseId);
            caseBasicInfo.setFather(father);
            caseBasicInfo.setJson("{}");
            EvidenceApplication.db.save(caseBasicInfo);
        } else {
            caseBasicInfo = list.get(0);
        }
        return caseBasicInfo;
    }

    public static RecordFileInfo getRecordFileInfo(String id) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "id = '" + id + "'");
        RecordFileInfo info = null;
        if (list == null || list.size() == 0) {
            info = new RecordFileInfo();
            info.setId(id);
            EvidenceApplication.db.save(info);
        } else {
            info = list.get(0);
        }
        return info;
    }

    public static EvidenceExtra getEvidenceExtra(String id) {
        List<EvidenceExtra> list = EvidenceApplication.db.findAllByWhere(EvidenceExtra.class, "id = '" + id + "'");
        EvidenceExtra extra = null;
        if (list == null || list.size() == 0) {
            extra = new EvidenceExtra();
            extra.setId(id);
            extra.setSection(id);
            EvidenceApplication.db.save(extra);
        } else {
            extra = list.get(0);
        }
        return extra;
    }

    public static SceneFileConjunction getFileConjunction(String id) {
        List<SceneFileConjunction> list = EvidenceApplication.db.findAllByWhere(SceneFileConjunction.class, "id = '" + id + "'");
        SceneFileConjunction info = null;
        if (list == null || list.size() == 0) {
            info = new SceneFileConjunction();
            info.setId(id);
            EvidenceApplication.db.save(info);
        } else {
            info = list.get(0);
        }
        return info;
    }

    public static LostGood getLostGood(String id) {
        List<LostGood> list = EvidenceApplication.db.findAllByWhere(LostGood.class, "id = '" + id + "'");
        LostGood goodItem = null;
        if (list == null || list.size() == 0) {
            goodItem = new LostGood();
            goodItem.setId(id);
            EvidenceApplication.db.save(goodItem);
        } else {
            goodItem = list.get(0);
        }
        return goodItem;
    }

    public static void saveTemplateSort(String caseId, String fatherKey) {
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + caseId + "' and fatherKey = '" + fatherKey + "'");
        if (list == null || list.size() == 0) {
            List<BaseTemp> baseTempList = EvidenceApplication.db.findAllByWhere(BaseTemp.class, "tableName = '" + fatherKey + "' and templateLevel = '1'");
            if (baseTempList != null && baseTempList.size() > 0) {
                TemplateSort templateSort = new TemplateSort();
                templateSort.setId(getUUid());
                String timeStamp = getCurrentTime("yyyy-MM-dd HH:mm:ss");
                templateSort.setDate(timeStamp);
                templateSort.setCaseId(caseId);
                templateSort.setFatherKey(fatherKey);
                templateSort.setSiNeedRec(true);
                templateSort.setSort(Integer.valueOf(baseTempList.get(0).getPositionSort()));
                templateSort.setFatherValue(baseTempList.get(0).getSceneName());
                EvidenceApplication.db.save(templateSort);
            }
        }
    }

    public static String getDictKey(String rootKey, String value) {
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class,
                "rootKey = '" + rootKey + "' and dictValue1 = '" + value + "'");
        if (list == null || list.size() == 0) {
            return "";
        }
        return list.get(0).getDictKey();
    }

    public static void saveAttchment(RecordFileInfo recordFileInfo, String refKeyId) throws JSONException {
        //产生附件所需data
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId());
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put("ID", refKeyId);
        jsonObject.put("SCENE_TYPE", recordFileInfo.getFather());
        jsonObject.put("ATTACHMENT_ID", recordFileInfo.getId());
        dataTemp.setDataType("scene_investigation_data");
        dataTemp.setData(jsonObject.toString());
        EvidenceApplication.db.update(dataTemp);
        //产生附件本身data*/
        DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
        JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
        recObject.put("refKeyId", refKeyId);
        recObject.put("type", getType(recordFileInfo));
        recDataTemp.setDataType("common_attachment");
        recDataTemp.setData(recObject.toString());
        EvidenceApplication.db.update(recDataTemp);
    }

    public static String getType(RecordFileInfo recordFileInfo) {
        switch (recordFileInfo.getFileType()) {
            case "audio":
                return "2";

            case "video":
                return "1";

            case "png":
                return "0";
        }
        return "";
    }

    final static double pi = 3.14159265358979324;
    final static double a = 6378245.0;
    final static double ee = 0.00669342162296594323;

    public static void expandViewTouchDelegate(final View view, final View delegateView, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
                delegateView.setTouchDelegate(touchDelegate);
            }
        });
    }

    public static void transform(double wgLat, double wgLon, double[] latlng) {
        if (outOfChina(wgLat, wgLon)) {
            latlng[0] = wgLat;
            latlng[1] = wgLon;
            return;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        latlng[0] = wgLat + dLat;
        latlng[1] = wgLon + dLon;

    }

    private static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }


    public static String getViewDefault(Context context, String caseId, String viewDefault, String dictType, String filed) {
        //不含有"."
        if (!viewDefault.contains(".")) {
            //dictype有值
            if (!TextUtils.isEmpty(dictType)) {
                StringBuilder sb = new StringBuilder();
                String[] defvalues = viewDefault.split(",");
                for (int i = 0; i < defvalues.length; i++) {
                    sb.append(getValueFromDicts(dictType, defvalues[i]));
                    if (i != defvalues.length - 1)
                        sb.append(";");
                }
                return sb.toString();
            } else {
                //无值为对应生成规则
                return getRuleValue(viewDefault, context);
            }
        } else {
            //含有“.”
            if ("SCENE_LAW_CASE_EXT.SCENE_DETAIL".equals(viewDefault)) {
                String address = getCurrentAddress(context);
                if (!TextUtils.isEmpty(address))
                    return address;
            }
            String[] strings = viewDefault.split("\\.");
            if (strings.length != 2)
                return "";
            return getFatherValue(caseId, "ROOT".equals(strings[0]) ? "SCENE_INVESTIGATION_EXT" : strings[0], strings[1]);
        }
    }


    private static String getValueFromDicts(String parentKey, String dictKey) {
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class,
                "parentKey = '" + parentKey + "' and dictKey = '" + dictKey + "'");
        if (list != null && list.size() != 0)
            return list.get(0).getDictKey() + "," + list.get(0).getDictValue1();
        return "";

    }

    public static String getRuleValue(String defaultValue, Context context) {
        switch (defaultValue) {
            case "UUID":
                return getUUid();
            case "CURRENT_ORG":
                return getDefaultValueFromOrgId(context);
            case "CURRENT_DATE":
                return getCurrentTime("yyyy-MM-dd");
            case "CURRENT_DATETIME":
                return getCurrentTime("yyyy-MM-dd HH:mm:ss");
            case "CURRENT_USER":
                return getCurrentUser(context);
            case "CURRENT_LONGITUDE":
                return EvidenceApplication.longitude != 0.0d ?
                        EvidenceApplication.longitude + "" : getAxis()[0] + "";
            case "CURRENT_LATITUDE":
                return EvidenceApplication.latitude != 0.0d ?
                        EvidenceApplication.latitude + "" : getAxis()[1] + "";
            case "CURRENT_AREA":
                return getCurrentArea(context);
            default:
                return defaultValue;
        }
    }


    private static double[] getAxis() {
        double[] axis = new double[2];
        axis[0] = 0;
        axis[1] = 0;
        List<SysAppParamSetting> paramSettings =
                EvidenceApplication.db.findAllByWhere(SysAppParamSetting.class, "key = 'default_location_coordinate'");
        if (paramSettings.size() > 0) {
            SysAppParamSetting paramSetting = paramSettings.get(0);
            String value = paramSettings.get(0).getValue();
            if (value != null && !"".equals(value)) {
                try {
                    JSONObject object = new JSONObject(paramSetting.getValue());
                    axis[0] = Double.valueOf(object.getString("long"));
                    axis[1] = Double.valueOf(object.getString("lat"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return axis;
    }

    public static String getCurrentTime(String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }


    private static String getDefaultValueFromOrgId(Context context) {
        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String orgId = sharePre.getString("organizationId", "");
        List<HyOrganizations> list = EvidenceApplication.db.findAllByWhere(HyOrganizations.class, "organizationId = " + orgId);
        return list.size() != 0 ? list.get(0).getOrganizationId() + "," + list.get(0).getOrganizationName() : "";
    }

    private static String getCurrentUser(Context context) {
        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String userId = sharePre.getString("userId", "");
        return userId != null ? userId + "," + sharePre.getString("prospectPerson", "") : "";
    }

   /* private static String getCurrentUserId(Context context) {
        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        String userId = sharePre.getString("userId", "");
        return userId != null ?  sharePre.getString("prospectPerson", "") + "," + userId : "";
    }*/

    private static String getCurrentAddress(Context context) {
        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        return sharePre.getString("address", "");
    }

    private static String getCurrentArea(Context context) {
        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        return sharePre.getString("compartmentNo", "") + "," + sharePre.getString("compartmentName", "");
    }

    private static String getFatherValue(String caseId, String father, String field) {
        return isCaseBasicInfo(father) ? getCaseBasicInfoValue(caseId, father, field) : getLostGoodValue(caseId, father, field);
    }

    private static String getCaseBasicInfoValue(String caseId, String father, String field) {
        List<CaseBasicInfo> list = EvidenceApplication.db.findAllByWhere(CaseBasicInfo.class,
                "caseId = '" + caseId + "' and father = '" + father + "'");
        if (list != null && list.size() != 0)
            return safeGetJsonValue(field, list.get(0).getJson()) + "," + safeGetJsonValue(field + "_NAME", list.get(0).getJson());
        return "";
    }

    private static String getLostGoodValue(String caseId, String father, String field) {
        List<LostGood> list = EvidenceApplication.db.findAllByWhere(LostGood.class,
                "caseId = '" + caseId + "' and father = '" + father + "'");
        if (list != null && list.size() != 0)
            return safeGetJsonValue(field, list.get(0).getJson()) + "," + safeGetJsonValue(field + "_NAME", list.get(0).getJson());
        return "";
    }

    private static boolean isCaseBasicInfo(String father) {
        List<CommonTemplateDetail> list =
                EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "tableName = '" + father + "'");
        if (list != null && list.size() != 0) {
            String level = list.get(0).getTemplateLevel();
            if ("1".equals(level))
                return true;
            if ("2".equals(level))
                return false;
        }
        return true;
    }

    public static boolean setTextColorAndInputManger(String viewMinor, String dataType, TextView textView) {
        if (!TextUtils.isEmpty(viewMinor) && viewMinor.equals("1"))
            textView.setTextColor(Color.parseColor("#999999"));
        return !TextUtils.isEmpty(viewMinor) && (dataType.equals("DOUBLE") || dataType.equals("INT"));
    }


    public static String getFragementName(String tableName) {
        DbModel dbModel = EvidenceApplication.db.findDbModelBySQL("select sceneName from BaseTemp where tableName = '" + tableName + "'");
        return dbModel != null ? dbModel.getString("sceneName") : "";
    }

    public static boolean isRequireField(String isRequireField) {
        if (TextUtils.isEmpty(isRequireField))
            return false;
        if ("0".equals(isRequireField))
            return false;
        if ("1".equals(isRequireField))
            return true;
        return false;
    }
}
