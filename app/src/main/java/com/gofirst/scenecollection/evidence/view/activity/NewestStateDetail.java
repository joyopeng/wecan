package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.customview.DateTimePickDialog;
import com.gofirst.scenecollection.evidence.view.customview.InquisitionType;
import com.gofirst.scenecollection.evidence.view.customview.MultiLevelListDialog;
import com.gofirst.scenecollection.evidence.view.customview.PopListSingleLevel;
import com.gofirst.scenecollection.evidence.view.customview.SingleSelectionPeople;
import com.gofirst.scenecollection.evidence.view.customview.SpinnerPop;
import com.gofirst.scenecollection.evidence.view.customview.Text;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
public class NewestStateDetail extends Activity implements View.OnClickListener {
    private EditText callpoliceTime, sceneCategory, policeName, callPoliceName, sceneAddress, mainScene;
    private String investigationPlace = "1";
    private String exposureProcess = "2";
    private String sceneRegionalism = "1";
    private String crackedDate = "1";
    private String status = "1";
    private String id;
    private String reportTime, alarmPeople;
    private ImageView phone;
    private TextView acceptWork;
    private LinearLayout tittle;

    private RelativeLayout case_districts, case_address, case_happen_time, report_name, report_phone,
            appoint_way, appoint_way_unit, receive_name, receive_time, inquest_reason,case_type,receive_alarm_time,inquisition_type,receptionNoRelative;
            //receive_alarm_time接警时间  receive_time接勘时间
    private TextView case_districts_input, case_address_input, case_happen_time_input, report_name_input,
            report_phone_input, appoint_way_input, appoint_way_unit_input, receive_name_input, receive_time_input,
            inquest_reason_input,case_type_input,receive_alarm_time_input,inquisition_type_input,receptionNoInput;

    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
    private View mRootView;
    private String caseId;//勘查Id
    public static final int UPDATE_TEXT = 1;
    private SharePre sharePre;
    private String receptionNo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newest_state_detail);
        sharePre = new SharePre(NewestStateDetail.this, "user_info", Context.MODE_PRIVATE);
        Init();
    }

    public void Init() {

        case_districts = (RelativeLayout) findViewById(R.id.case_districts);
        case_address = (RelativeLayout) findViewById(R.id.case_address);
        case_happen_time = (RelativeLayout) findViewById(R.id.case_happen_time);
        report_name = (RelativeLayout) findViewById(R.id.report_name);
        report_phone = (RelativeLayout) findViewById(R.id.report_phone);
        appoint_way = (RelativeLayout) findViewById(R.id.appoint_way);
        appoint_way_unit = (RelativeLayout) findViewById(R.id.appoint_way_unit);
        receive_name = (RelativeLayout) findViewById(R.id.receive_name);
        receive_time = (RelativeLayout) findViewById(R.id.receive_time);
        inquest_reason = (RelativeLayout) findViewById(R.id.inquest_reason);
        case_type = (RelativeLayout) findViewById(R.id.case_type);
        receive_alarm_time= (RelativeLayout) findViewById(R.id.receive_alarm_time);
        inquisition_type= (RelativeLayout) findViewById(R.id.inquisition_type);
        receptionNoRelative = (RelativeLayout) findViewById(R.id.receptionNo);
        receptionNoInput = (TextView) findViewById(R.id.receptionNo_input);
        secondary_back_img = (ImageView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv = (TextView) findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.GONE);
        secondary_title_tv.setText("接勘");
        secondary_right_tv.setVisibility(View.VISIBLE);
        secondary_right_tv.setText("完成");

        mRootView = findViewById(R.id.rootView);

        case_districts_input = (TextView) findViewById(R.id.case_districts_input);
        case_address_input = (TextView) findViewById(R.id.case_address_input);
        case_happen_time_input = (TextView) findViewById(R.id.case_happen_time_input);
        report_name_input = (TextView) findViewById(R.id.report_name_input);
        report_phone_input = (TextView) findViewById(R.id.report_phone_input);
        appoint_way_input = (TextView) findViewById(R.id.appoint_way_input);
        appoint_way_unit_input = (TextView) findViewById(R.id.appoint_way_unit_input);
        receive_name_input = (TextView) findViewById(R.id.receive_name_input);
        receive_time_input = (TextView) findViewById(R.id.receive_time_input);
        inquest_reason_input = (TextView) findViewById(R.id.inquest_reason_input);
        case_type_input = (TextView) findViewById(R.id.case_type_input);
        receive_alarm_time_input= (TextView) findViewById(R.id.receive_alarm_time_input);
        inquisition_type_input= (TextView) findViewById(R.id.inquisition_type_input);

        case_districts.setOnClickListener(this);
        case_address.setOnClickListener(this);
        case_happen_time.setOnClickListener(this);
        report_name.setOnClickListener(this);
        report_phone.setOnClickListener(this);
        appoint_way.setOnClickListener(this);
        appoint_way_unit.setOnClickListener(this);
        receive_name.setOnClickListener(this);
        receive_time.setOnClickListener(this);
        inquest_reason.setOnClickListener(this);
        case_type.setOnClickListener(this);
        receptionNoRelative.setOnClickListener(this);
       // receive_alarm_time.setOnClickListener(this);
        inquisition_type.setOnClickListener(this);

        secondary_right_tv.setOnClickListener(this);

        inquisition_type_input.setText("简勘");


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        List<CsSceneCases> sceneAlarmList = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                " id = '" + id + "'");
        if (sceneAlarmList != null || sceneAlarmList.size() != 0) {

            case_districts_input.setText(sceneAlarmList.get(0).getSceneRegionalismName());
            case_type_input.setText(sceneAlarmList.get(0).getAlarmCategoryName());
            case_type_input.setTag(sceneAlarmList.get(0).getAlarmCategory());
            receive_alarm_time_input.setText(sceneAlarmList.get(0).getAlarmDatetime());
            case_address_input.setText(sceneAlarmList.get(0).getAlarmAddress());
            case_happen_time_input.setText(sceneAlarmList.get(0).getAlarmDatetime());
            report_name_input.setText(sceneAlarmList.get(0).getAlarmPeople());
            report_phone_input.setText(sceneAlarmList.get(0).getAlarmTel());
            //appoint_way_input.setText(sceneAlarmList.get(0));
            appoint_way_unit_input.setText(sharePre.getString("organizationCname",""));
            //receive_name_input.setText(sceneAlarmList.get(0).);
            //receive_time_input.setText(sceneAlarmList.get(0).);
            inquest_reason_input.setText(sceneAlarmList.get(0).getExposureProcess());
            receptionNo=sceneAlarmList.get(0).getReceptionNo();
            receptionNoInput.setText(receptionNo);
        }
    }

   /* private void saveDate(String id, View v)  {


        List<CsSceneCases> SceneCaselist = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "id = '" + id + "'");
        if (SceneCaselist.size() == 0) {


            PopListSingleLevel popListSingleLevel = new PopListSingleLevel(NewestStateDetail.this, "案件类型", v, getCaseTemplates(v.getContext()));
            popListSingleLevel.setListener(new PopListSingleLevel.onResultListener() {
                @Override
                public void onResult(String templateId) {
                    Intent intent = new Intent(NewestStateDetail.this, ProspectPreview.class);

                    intent.putExtra("caseId", caseId);
                    intent.putExtra("caseInfo", inquest_reason_input.getText().toString());
                    intent.putExtra("templateId", templateId);
                    startActivity(intent);
                    setTemplateId(templateId, caseId);
                    finish();
                }
            });

            Toast.makeText(NewestStateDetail.this, "接勘完成", Toast.LENGTH_SHORT).show();

        }

    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.inquisition_type:
                InquisitionType inquisitionType=new InquisitionType(NewestStateDetail.this,"勘验类型","",
                        inquisition_type_input);

                break;

            case R.id.receptionNo:
                Text textt = new Text(NewestStateDetail.this);
                if (TextUtils.isEmpty(receptionNoInput.getText().toString()))
                    receptionNoInput.setText("J");
                textt.clickPop(NewestStateDetail.this, receptionNoInput, "接警编号");
                break;

            case R.id.case_type:
                SpinnerPop spinnerPop=new SpinnerPop(NewestStateDetail.this,case_type_input,"","AJLBDM");
                break;
            case R.id.receive_alarm_time:
                DateTimePickDialog dateTimePickDialogUtil = new DateTimePickDialog(NewestStateDetail.this, "", "接警时间");
                dateTimePickDialogUtil.dateTimePicKDialog(receive_alarm_time_input);
                break;
            case R.id.case_districts:
                MultiLevelListDialog multiLevelListDialog = new MultiLevelListDialog(NewestStateDetail.this, case_districts_input, "");
                break;
            case R.id.case_address:
                Text text = new Text(NewestStateDetail.this);
                text.clickPop(NewestStateDetail.this, case_address_input, "事发地址");
                break;
            case R.id.case_happen_time:
                DateTimePickDialog dateTimePickDialogUti2 = new DateTimePickDialog(NewestStateDetail.this, "", "接警时间");
                dateTimePickDialogUti2.dateTimePicKDialog(case_happen_time_input);
                break;
            case R.id.report_name:
                Text text1 = new Text(NewestStateDetail.this);
                text1.clickPop(NewestStateDetail.this, report_name_input, "报警人");
                break;
            case R.id.report_phone:
                Text text2 = new Text(NewestStateDetail.this);
                text2.clickPop(NewestStateDetail.this, report_phone_input, "报警电话");
                break;
            case R.id.appoint_way:
                String appoint = "";
                //PopList popList=new PopList(NewestStateDetail.this);
                // popList.initView("1","指派方式",appoint,"POP_LIST");
                //appoint_way_input.setText(appoint);
                PopListSingleLevel popListSingleLevel = new PopListSingleLevel(NewestStateDetail.this, "指派方式", "XCKYJJFSDM",
                        appoint_way_input);
                break;
            case R.id.appoint_way_unit:

                Text text3 = new Text(NewestStateDetail.this);
                text3.clickPop(NewestStateDetail.this, appoint_way_unit_input, "接警单位");

                break;
            case R.id.receive_name:
                //MultiChoiclListDialog multiChoiclListDialog=new MultiChoiclListDialog(NewestStateDetail.this);
                //multiChoiclListDialog.clickPop(NewestStateDetail.this, receive_name_input, "接勘人");
                String organizationId = sharePre.getString("organizationId", "");
                SingleSelectionPeople singleSelectionPeople = new SingleSelectionPeople(NewestStateDetail.this, "接勘人", organizationId,
                        receive_name_input);

                break;
            case R.id.receive_time:
                DateTimePickDialog dateTimePickDialogUtil2 = new DateTimePickDialog(NewestStateDetail.this, "", "接勘时间");
                dateTimePickDialogUtil2.dateTimePicKDialog(receive_time_input);
                break;
            case R.id.inquest_reason:
                Text inquestTeasonText = new Text(NewestStateDetail.this);
                inquestTeasonText.clickPop(NewestStateDetail.this, inquest_reason_input, "报警内容");
                break;
            case R.id.secondary_right_tv:

                  //  saveDate(id, v);
               /* if(case_districts_input.getText().toString().equals("")){
                    Toast.makeText(NewestStateDetail.this, "请选择案发区划", Toast.LENGTH_SHORT).show();
                }else if(receive_name_input.getText().toString().equals("")){
                    Toast.makeText(NewestStateDetail.this, "请选择接勘人", Toast.LENGTH_SHORT).show();
                }else if(receive_time_input.getText().toString().equals("")){
                    Toast.makeText(NewestStateDetail.this, "请选择接勘时间", Toast.LENGTH_SHORT).show();
                }
                else{*/


                    List<CsSceneCases> SceneCaselist = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "id = '" + id + "'");
                    if (SceneCaselist.size() > 0) {
                        /*CaseTypeBigSelect caseTypeBigSelect = new CaseTypeBigSelect(NewestStateDetail.this, "勘验类型","GASSLAJ", inquest_reason_input);
                        caseTypeBigSelect.setListener(new CaseTypeBigSelect.onResultListener() {
                            @Override
                            public void onResult(String Dictkey) {
                                String templateId="";
                                Intent intent = new Intent(NewestStateDetail.this, ProspectPreview.class);
                                try {
                                    saveData();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                List<CommonTemplate> list=EvidenceApplication.db.findAll(CommonTemplate.class);
                                for(int i=0;i<list.size();i++){
                                    if(list.get(i).getCaseTypeCode().contains(Dictkey)){
                                        templateId=list.get(i).getSid();
                                    }
                                }
                                intent.putExtra("caseId", caseId);
                                intent.putExtra("caseInfo", inquest_reason_input.getText().toString());
                                intent.putExtra("templateId", templateId);
                                startActivity(intent);
                                setTemplateId(templateId, caseId);
                                finish();
                            }
                        });*/



                        String templateId="";

                        List<CommonTemplate> CommonTemplateList=EvidenceApplication.db.findAllByWhere(CommonTemplate.class,
                                "caseTypeCode = '"+(-1)+"'");
                        if (CommonTemplateList.size()>0) {

                            Intent intent = new Intent(NewestStateDetail.this, ProspectPreview.class);
                            try {
                                saveData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            templateId=CommonTemplateList.get(0).getSid();
                            intent.putExtra("caseId", caseId);
                            intent.putExtra("caseInfo", inquest_reason_input.getText().toString());
                            intent.putExtra("templateId", templateId);
                            intent.putExtra("status","0");

                            List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                    " caseNo = '"+caseId+"'");
                            if(inquisition_type_input.getText().toString().equals("简勘")){
                                intent.putExtra("dealType", true);
                                if(list!=null&&list.size()>0){
                                    list.get(0).setDealType("1");
                                    EvidenceApplication.db.update(list.get(0));
                                }

                            }else{
                                if(list!=null&&list.size()>0){
                                    list.get(0).setDealType("0");
                                    EvidenceApplication.db.update(list.get(0));
                                }

                            }

                            startActivity(intent);
                            setTemplateId(templateId, caseId);
                            new MyThread().start();
                            finish();
                        }

                    }
               // }
                break;
        }
    }


    private void saveData() throws JSONException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String Datestr = formatter.format(curDate);

        //CsSceneCases sceneAlarm = new CsSceneCases();
        //sceneAlarm.setStatus("1");
        //EvidenceApplication.db.update(sceneAlarm, "id = '" + id + "'");


//        CsSceneCases csSceneCases = new CsSceneCases();
        CsSceneCases csSceneCases = ViewUtil.getCsSceneCasesById(id);
       // csSceneCases.setId(id);

//        csSceneCases.setSceneRegionalismName(sharePre.getString("organizationCname",""));//案发区划case_districts_input.getText().toString()
        csSceneCases.setSceneDetail(case_address_input.getText().toString());
        try {
            csSceneCases.setOccurrenceDateFrom(formatter.parse(case_happen_time_input.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String key1=(String)receive_name_input.getTag();
        csSceneCases.setReceivePeopleNum(sharePre.getString("userId", ""));//警号 key1
        csSceneCases.setAlarmPeople(report_name_input.getText().toString());
        csSceneCases.setAlarmPhone(report_phone_input.getText().toString());
        //csSceneCases.setAppointWay(appoint_way_input.getText().toString());
        csSceneCases.setAppointWayUnit(appoint_way_unit_input.getText().toString());
        csSceneCases.setReceivePeople(sharePre.getString("prospectPerson", ""));//接勘人receive_name_input.getText().toString()
        csSceneCases.setReceiveCaseTime(Datestr);//接勘时间receive_time_input.getText().toString()
        csSceneCases.setSortListDateTime(Datestr);
        csSceneCases.setExposureProcess(inquest_reason_input.getText().toString());
        csSceneCases.setCaseType(case_type_input.getText().toString());
        csSceneCases.setCaseTypeId(case_type_input.getTag().toString());
        caseId = ViewUtil.getUUid();
        csSceneCases.setCaseNo(caseId);//勘查id
        csSceneCases.setReceivePeopleNum(sharePre.getString("userId", ""));
        csSceneCases.setReceptionNo(receptionNoInput.getText().toString());
        csSceneCases.setStatus("2");
        csSceneCases.setReceptionNo(receptionNo);
        EvidenceApplication.db.update(csSceneCases, "id = '" + id + "'");
       // EvidenceApplication.db.save(csSceneCases);

        CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId,"SCENE_LAW_CASE_EXT");
        JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());
        //jsonObject.put("OCCURRENCE_DATE_FROM", case_happen_time_input.getText().toString());
        jsonObject.put("SCENE_DETAIL", case_address_input.getText().toString());
//        jsonObject.put("CASE_TYPE_NAME", case_type_input.getText().toString());
//        jsonObject.put("CASE_TYPE", case_type_input.getTag().toString());
        jsonObject.put("ASSIGNED_CONTENT", inquest_reason_input.getText().toString());
        jsonObject.put("RECEIVED_DATE", Datestr);//receive_time_input.getText().toString()
        jsonObject.put("ALARM_PEOPLE",report_name_input.getText().toString());//报警人
        jsonObject.put("ALARM_TEL",report_phone_input.getText().toString());//报警人电话
//        jsonObject.put("ASSIGNED_BY",appoint_way_unit_input.getText().toString());//指派单位
        jsonObject.put("ASSIGNED_BY",csSceneCases.getSceneRegionalismName());//指派单位
        jsonObject.put("CRACKED_DATE", " ");//破案时间
        //jsonObject.put("OCCURRENCE_DATE_TO", " ");//案发借宿时间
        jsonObject.put("RECEIVED_BY_ID",sharePre.getString("userId", ""));//接警人
        jsonObject.put("RECEIVED_BY_ID_NAME",sharePre.getString("prospectPerson",""));//接警人

        jsonObject.put("RECEPTION_NO",csSceneCases.getReceptionNo());
        jsonObject.put("SCENE_REGIONALISM",csSceneCases.getSceneRegionalism());
        jsonObject.put("SCENE_REGIONALISM_NAME",csSceneCases.getSceneRegionalismName());
        caseBasicInfo.setJson(jsonObject.toString());
        EvidenceApplication.db.update(caseBasicInfo);

        CaseBasicInfo caseBasicInfo1 = ViewUtil.getCaseBasicInfo(caseId,"SCENE_INVESTIGATION_EXT");
        JSONObject jsonObject1 = new JSONObject(caseBasicInfo1.getJson());

        jsonObject1.put("DIRECTOR_IDS",sharePre.getString("userId",""));//现场指挥人员
        jsonObject1.put("DIRECTOR_IDS_NAME",sharePre.getString("prospectPerson",""));//现场指挥人员

        jsonObject1.put("INVESTIGATOR",sharePre.getString("userId",""));//现场勘验人员
        jsonObject1.put("INVESTIGATOR_NAME",sharePre.getString("prospectPerson",""));//现场勘验人员

        jsonObject1.put("MAIN_ORGAN_ID",sharePre.getString("organizationId",""));//主勘单位
        jsonObject1.put("MAIN_ORGAN_ID_NAME", sharePre.getString("organizationCname", ""));//主勘单位

        caseBasicInfo1.setJson(jsonObject1.toString());
        EvidenceApplication.db.update(caseBasicInfo1);


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


    private void updateStatus(String MethodName){
        StringMap params = new StringMap();

        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("id",id);
        params.putString("user",sharePre.getString("userId", ""));
        params.putString("token", sharePre.getString("token", ""));
        params.putString("status", "1");


        Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date = sDateFormat.format(new java.util.Date());
                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {

                    } else {

                        Toast.makeText(NewestStateDetail.this, response
                                        .getJSONArray("data").toString(),
                                Toast.LENGTH_SHORT).show();
                        //refreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             //   Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
            //    Utils.stopProgressDialog();
            }
        });

    }


    class MyThread extends Thread {
        @Override
        public void run() {
        // 处理具体的逻辑
            updateStatus("/update/staus");

        }
    }


}
