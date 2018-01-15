package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.SceneEnvironment;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.AudioEditText;
import com.gofirst.scenecollection.evidence.view.customview.SegmentedGroup;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

;

/**
 * Created by maxiran on 2016/4/20.
 */
public class SceneInfoFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, Netroid.OnLister<JSONObject>, View.OnClickListener {

    private Spinner weatherSpinner, windSpinner;
    private EditText temperatureText;
    private SceneEnvironment sceneEnvironment;
    private String caseId;
    private AudioEditText weatherOther;
    private TextView protectDate;
    private String father;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scenen_info_layout, null);
        SegmentedGroup lightingGroup = (SegmentedGroup) view.findViewById(R.id.lighting_sources);
        SegmentedGroup reportGroup = (SegmentedGroup) view.findViewById(R.id.report_type);
        weatherSpinner = (Spinner) view.findViewById(R.id.weather_spinner);
        windSpinner = (Spinner) view.findViewById(R.id.wind_spinner);
        temperatureText = (EditText) view.findViewById(R.id.temperature);
        Button saveBtn = (Button) view.findViewById(R.id.save_btn);
        weatherOther = (AudioEditText) view.findViewById(R.id.weather_other);
        protectDate = (TextView) view.findViewById(R.id.protect_date);
        protectDate.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        setSpinnerItemSelectedByValue(weatherSpinner, "雨");
        lightingGroup.setOnCheckedChangeListener(this);
        reportGroup.setOnCheckedChangeListener(this);
        caseId = getArguments().getString("caseId");
        if (getArguments().getString("mode").equals("find")) {
            view.findViewById(R.id.save_btn).setVisibility(View.GONE);
        }
        sceneEnvironment = getSceneEnvironment();
        father = getArguments().getString("father");

        weatherOther.setArgs(caseId, father, "天气备注");
        SharePre user_info = new SharePre(getActivity(), "user_ info", Context.MODE_PRIVATE);
        Netroid.GetHttp("/app/weather", this, user_info.getString("token", ""));
        String weatherInfo = sceneEnvironment.getWeather();
        setSpinnerItemSelectedByValue(weatherSpinner, weatherInfo != null ? weatherInfo : "");
        String tempInfo = sceneEnvironment.getEnvTemperature();
        temperatureText.setText(tempInfo != null ? tempInfo : "");
        String windInfo = sceneEnvironment.getWind();
        setSpinnerItemSelectedByValue(windSpinner, windInfo != null ? windInfo : "");
        String otherInfo = sceneEnvironment.getOtherWeather();

        String protectTimeInfo = sceneEnvironment.getProtectionDate();
        protectDate.setText(protectTimeInfo != null ? protectTimeInfo : "点击进行选择");
        ViewUtil.radioGroupSetCheckByValue(lightingGroup, sceneEnvironment.getLighting());
        ViewUtil.radioGroupSetCheckByValue(reportGroup, sceneEnvironment.getSceneCondition());
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton) group.findViewById(checkedId);
        switch (group.getId()) {

            case R.id.lighting_sources:
                sceneEnvironment.setLighting(button.getText().toString());
                break;

            case R.id.report_type:
                sceneEnvironment.setSceneCondition(button.getText().toString());
                break;
        }
    }


    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        for (int i = 0; i < apsAdapter.getCount(); i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);
                break;
            }
        }
    }

    @Override
    public void onSuccess(JSONObject response) {
        try {
            if (response.getBoolean("success")) {
                JSONObject data = response.getJSONObject("data");
                String weather = data.getString("weather");
                setSpinnerItemSelectedByValue(weatherSpinner, weather);
                String wind = data.getString("wind");
                setSpinnerItemSelectedByValue(windSpinner, wind);
                String temperature = data.getString("temperature");
                temperatureText.setText(temperature);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(NetroidError error) {

    }

    private SceneEnvironment getSceneEnvironment() {
        List<SceneEnvironment> list = EvidenceApplication.db.findAllByWhere(SceneEnvironment.class, "caseId = '" + caseId + "'");
        if (list == null || list.size() == 0) {
            sceneEnvironment = new SceneEnvironment();
            sceneEnvironment.setCaseId(caseId);
            sceneEnvironment.setSceneType("scene_environment");
            sceneEnvironment.setId(ViewUtil.getUUid());
            EvidenceApplication.db.save(sceneEnvironment);
        }
        return EvidenceApplication.db.findAllByWhere(SceneEnvironment.class, "caseId = '" + caseId + "'").get(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                sceneEnvironment.setWeather(weatherSpinner.getSelectedItem().toString());
                sceneEnvironment.setEnvTemperature(temperatureText.getText().toString());
                sceneEnvironment.setWind(windSpinner.getSelectedItem().toString());
                sceneEnvironment.setOtherWeather_audio(weatherOther.getAudioPath());
                sceneEnvironment.setProtectionDate(protectDate.getText().toString());
                EvidenceApplication.db.update(sceneEnvironment);
                saveJson();
                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.protect_date:
               /* DateTimePickDialog dateTimePickDialogUtil = new DateTimePickDialog(getActivity(), "");
                dateTimePickDialogUtil.dateTimePicKDialog(protectDate);*/
                break;
        }
    }

    public static DataTemp getDataTemp(String caseId, String father) {
        List<DataTemp> list = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        if (list == null || list.size() == 0) {
            DataTemp dataTemp = new DataTemp();
            dataTemp.setId(ViewUtil.getUUid());
            dataTemp.setCaseId(caseId);
            dataTemp.setFather(father);
            EvidenceApplication.db.save(dataTemp);
        }
        return EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'").get(0);
    }


    public static DataTemp getAddRecDataTemp(String caseId, String father) {
        List<DataTemp> list = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        if (list == null || list.size() == 0) {
            DataTemp dataTemp = new DataTemp();
            dataTemp.setId(ViewUtil.getUUid());
            dataTemp.setCaseId(caseId);
            dataTemp.setFather(father);
            dataTemp.setAddRec(true);
            EvidenceApplication.db.save(dataTemp);
        }
        return EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'").get(0);
    }

    private void saveJson() {
        DataTemp dataTemp = getDataTemp(caseId, father);
        SceneEnvironment sceneEnvironment = getSceneEnvironment();
        String json = com.alibaba.fastjson.JSON.toJSONString(sceneEnvironment);
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("ATTACHMENT_ID", sceneEnvironment.getId());
            dataTemp.setData(jsonObject.toString());
            dataTemp.setDataType("scene_investigation_data");
            EvidenceApplication.db.update(dataTemp);
            if (weatherOther.getRecFileInfo() != null) {
                ViewUtil.saveAudioAttachment(weatherOther.getRecFileInfo(), sceneEnvironment.getId(), sceneEnvironment.getSceneType());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}





