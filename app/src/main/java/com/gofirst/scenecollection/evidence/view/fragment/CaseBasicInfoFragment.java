package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.LostGood;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.BaseDataBean;
import com.gofirst.scenecollection.evidence.utils.NetUtilPing;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.utils.WifiAdmin;
import com.gofirst.scenecollection.evidence.view.customview.AudioEditText;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

/**
 * @author maxiran
 *         配置基本模块 案件信息 现场环境等
 */
public class CaseBasicInfoFragment extends Fragment {

    private Context context;
    private CaseBasicInfo caseBasicInfo;
    private List<BaseView> viewLists;
    private boolean haseCreate, addRec;
    private LinearLayout secContainerLayout;
    private String caseId, father, templateId, mode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        haseCreate = true;
        context = getActivity();
        View view = inflater.inflate(R.layout.case_basic_info_layout, null);
        LinearLayout containerLayout = (LinearLayout) view.findViewById(R.id.container);
        secContainerLayout = (LinearLayout) view.findViewById(R.id.sec_container);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        templateId = getArguments().getString("templateId");
        mode = getArguments().getString("mode");
        caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId, father);
        addRec = getArguments().getBoolean(BaseView.ADDREC);
        saveWifiList();
        saveBaseStation();
        String isNeedRec = getArguments().getString("isNeedRec");
        if ("true".equals(isNeedRec)) {
            if ((TextUtils.isEmpty(mode) || BaseView.EDIT.equals(mode)) || (BaseView.VIEW.equals(mode) && isExistFile())) {
                AudioEditText audioEditText = new AudioEditText(getContext());
                audioEditText.initView(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT);
                audioEditText.setArgs(caseId, father, ViewUtil.getFragementName(father));
                containerLayout.addView(audioEditText);
            }
        }
        viewLists = ViewUtil.getLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT, getActivity(), containerLayout, caseBasicInfo.getJson(), caseId, father, templateId);
        ViewUtil.getSecLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT, getActivity(),
                secContainerLayout, caseId, father, templateId, addRec + "");
        return view;
    }


    @Override
    public void onDestroy() {
        String json = ViewUtil.viewSave2JsonNoRequire(getContext(), viewLists, caseBasicInfo.getJson());
        if (json != null) {
            caseBasicInfo.setJson(saveReceptionNo(json));
            EvidenceApplication.db.update(caseBasicInfo);
            save2Json();
            saveCaseTypeToDataBase(caseId, json);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (haseCreate) {
            secContainerLayout.removeAllViews();
            ViewUtil.getSecLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT, getActivity(), secContainerLayout, caseId, father, templateId, addRec + "");
            String json = ViewUtil.viewSave2JsonNoRequire(getContext(), viewLists, caseBasicInfo.getJson());
            if (json != null) {
                caseBasicInfo.setJson(saveReceptionNo(json));
                EvidenceApplication.db.update(caseBasicInfo);
                save2Json();
            }
        }
        super.onResume();
    }

    private void save2Json() {
        String caseId = getArguments().getString("caseId");
        String father = getArguments().getString("father");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, father);
        try {
            JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());
            jsonObject.put("SCENE_TYPE", caseBasicInfo.getFather());
            jsonObject.put("INVESTIGATION_NO", ViewUtil.getUUid());
            dataTemp.setDataType("scene_investigation_data");
            dataTemp.setData(jsonObject.toString());
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isExistFile() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "' and fileType = 'audio'");
        return list != null && list.size() != 0;
    }


    private void saveCaseTypeToDataBase(String caseId, String jsonObject) {
        String caseTypeName = ViewUtil.safeGetJsonValue("CASE_TYPE_NAME", jsonObject);
        if (!TextUtils.isEmpty(caseTypeName)) {
            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
            if (list != null && list.size() != 0) {
                CsSceneCases csSceneCases = list.get(0);
                csSceneCases.setCaseType(caseTypeName);
                EvidenceApplication.db.update(csSceneCases);
            }
        }

    }

    private String saveReceptionNo(String json) {
        if (!"SCENE_LAW_CASE_EXT".equals(father))
            return json;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            List<CsSceneCases> csSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
            if (csSceneCases != null && csSceneCases.size() > 0) {
                jsonObject.put("RECEPTION_NO", csSceneCases.get(0).getReceptionNo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.show(getActivity(), "保存receptionNo 出现异常", Toast.LENGTH_SHORT);
        }

        return jsonObject != null ? jsonObject.toString() : json;
    }

    public String getMacAddress() {
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    private void saveWifiList() {
        if ("SCENE_INVESTIGATION_EXT".equals(father) && (TextUtils.isEmpty(mode) || BaseView.EDIT.equals(mode))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    removeAllWifiData(caseId, "SCENE_WIFI");
                    String mac = getMacAddress();
                    WifiAdmin wifiAdmin = new WifiAdmin(context);
                    wifiAdmin.startScan(context);
                    List<ScanResult> wifiList = wifiAdmin.getWifiList();
                    for (ScanResult result : wifiList) {
                        LostGood lostGood = new LostGood();
                        lostGood.setId(ViewUtil.getUUid());
                        lostGood.setFather("SCENE_WIFI" + lostGood.getId());
                        lostGood.setCaseId(caseId);
                        try {
                            JSONObject jsonObject = new JSONObject("{}");
                            jsonObject.put("WIFIADDR", mac);
                            jsonObject.put("LEVELEXT", result.level + "");
                            jsonObject.put("SSID", result.SSID);
                            jsonObject.put("BSSID", result.BSSID);
                            jsonObject.put("ID", ViewUtil.getUUid());
                            jsonObject.put("MAIN_ID", caseId);
                            lostGood.setJson(jsonObject.toString());
                            EvidenceApplication.db.save(lostGood);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //save to DataTemp
//                        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, lostGood.getFather());
                        try {
                            DataTemp dataTemp = new DataTemp();
                            dataTemp.setId(ViewUtil.getUUid());
                            dataTemp.setCaseId(caseId);
                            dataTemp.setFather(lostGood.getFather());
                            JSONObject jsonObject = new JSONObject(lostGood.getJson());
                            jsonObject.put("SCENE_TYPE", "SCENE_WIFI");
                            jsonObject.put("ID", ViewUtil.getUUid());
                            jsonObject.put("MAIN_ID", caseId);
                            dataTemp.setDataType("scene_investigation_data");
                            dataTemp.setData(jsonObject.toString());
                            EvidenceApplication.db.save(dataTemp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void removeAllWifiData(String caseId, String father) {
        EvidenceApplication.db.deleteByWhere(LostGood.class, "caseId = '" + caseId + "'" + " and father like '%" + father + "%'");
        EvidenceApplication.db.deleteByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father like '%" + father + "%'");
    }

    private void saveBaseStation() {
        if ("SCENE_INVESTIGATION_EXT".equals(father) && (TextUtils.isEmpty(mode) || BaseView.EDIT.equals(mode))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    removeAllBaseStationData(caseId, "SCENE_BASE_STATION");
                    List<BaseDataBean> beans = NetUtilPing.getBaseData(context);
                    for (BaseDataBean bean : beans) {
                        LostGood lostGood = new LostGood();
                        lostGood.setId(ViewUtil.getUUid());
                        lostGood.setFather("SCENE_BASE_STATION" + lostGood.getId());
                        lostGood.setCaseId(caseId);
                        try {
                            JSONObject jsonObject = new JSONObject("{}");
                            jsonObject.put("CID", bean.getCell_id());
                            jsonObject.put("LAC", bean.getLac());
                            jsonObject.put("SIGNALSTRENGTH", bean.getSignalstrength());
                            jsonObject.put("MNC", bean.getMnc());
                            jsonObject.put("MCC", bean.getMcc());
                            jsonObject.put("LEVELEXT", bean.getLeveltext());
                            jsonObject.put("ASULEVEL", bean.getAsulevel());
                            jsonObject.put("CPI", bean.getCpi());
                            jsonObject.put("BSIC", bean.getBsic());
                            jsonObject.put("ARFCN", bean.getArfcn());
                            jsonObject.put("TIME", bean.getTime());
                            jsonObject.put("TAC", bean.getTac());
                            jsonObject.put("PCI", bean.getPci());
                            jsonObject.put("EARFCN", bean.getEarfcn());
                            jsonObject.put("ID", ViewUtil.getUUid());
                            lostGood.setJson(jsonObject.toString());
                            EvidenceApplication.db.save(lostGood);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            DataTemp dataTemp = new DataTemp();
                            dataTemp.setId(ViewUtil.getUUid());
                            dataTemp.setCaseId(caseId);
                            dataTemp.setFather(lostGood.getFather());
                            JSONObject jsonObject = new JSONObject(lostGood.getJson());
                            jsonObject.put("SCENE_TYPE", "SCENE_BASE_STATION");
                            jsonObject.put("ID", ViewUtil.getUUid());
                            jsonObject.put("MAIN_ID", caseId);
                            dataTemp.setDataType("scene_investigation_data");
                            dataTemp.setData(jsonObject.toString());
                            EvidenceApplication.db.save(dataTemp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void removeAllBaseStationData(String caseId, String father) {
        EvidenceApplication.db.deleteByWhere(LostGood.class, "caseId = '" + caseId + "'" + " and father like '%" + father + "%'");
        EvidenceApplication.db.deleteByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father like '%" + father + "%'");
    }

}
