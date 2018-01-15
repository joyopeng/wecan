package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsDictsConjunction;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.CaseTypePopAdapter;
import com.gofirst.scenecollection.evidence.view.customview.DateTimePickDialog;
import com.gofirst.scenecollection.evidence.view.customview.InquisitionType;
import com.gofirst.scenecollection.evidence.view.customview.MultiChoicePopWindow;
import com.gofirst.scenecollection.evidence.view.customview.MultiLevelListDialog;
import com.gofirst.scenecollection.evidence.view.customview.SingleSelectionPeople;
import com.gofirst.scenecollection.evidence.view.customview.SpinnerPop;
import com.gofirst.scenecollection.evidence.view.customview.Text;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by maxiran 2016/4/18.
 */
public class AddNewCase extends Activity implements View.OnClickListener{

    private RelativeLayout caseDistricts,caseAddress,caseType,caseHappenTime,caseHappenTimeEnd,receiveName,inquestReason,inquisition_type,receiveCaseNum;
    private ImageView titleBarAddImg;
    private TextView titleBarAddtxt;
    private TextView caseDistrictsInput,caseAddressInput,caseTypeinput,
            caseHappenTimeInput,caseHappenTimeEndInput,receiveNameInput,inquestReasonInput,inquisition_type_input,receiveCaseNumInput;
    private View mRootView;
    private List<String> mMultiDataList;
    private final static int COUNT = 3;
    private MultiChoicePopWindow mMultiChoicePopWindow;
    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;

    boolean booleans[]=new boolean[15];
    private List<String> list=new ArrayList<>();
    private ListView listView;
    private CaseTypePopAdapter adapter;
    private String caseId,caseTypeinputKey;
    private SharePre sharePre;
    private String caseTypeinputKeyTemp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.add_new_case);
        sharePre = new SharePre(AddNewCase.this, "user_info", Context.MODE_PRIVATE);
        init();
    }


    private void init() {
        receiveCaseNum = (RelativeLayout) findViewById(R.id.receive_case_num);
        caseDistricts=(RelativeLayout)findViewById(R.id.case_districts);
        caseAddress=(RelativeLayout)findViewById(R.id.case_address);
        caseType=(RelativeLayout)findViewById(R.id.case_type);
        caseHappenTime=(RelativeLayout)findViewById(R.id.case_happen_time);
        receiveName=(RelativeLayout)findViewById(R.id.receive_name);
        receiveName.setVisibility(View.GONE);
        inquestReason=(RelativeLayout) findViewById(R.id.inquest_reason);
        caseHappenTimeEnd=(RelativeLayout)findViewById(R.id.case_happen_time_end);
        inquisition_type= (RelativeLayout) findViewById(R.id.inquisition_type);

        receiveCaseNumInput = (TextView)findViewById(R.id.receive_case_num_input);
        caseDistrictsInput=(TextView)findViewById(R.id.case_districts_input);
        caseAddressInput=(TextView) findViewById(R.id.case_address_input);
        caseTypeinput=(TextView)findViewById(R.id.case_type_input);
        caseHappenTimeInput=(TextView) findViewById(R.id.case_happen_time_input);
        caseHappenTimeEndInput=(TextView) findViewById(R.id.case_happen_time_end_input);
        receiveNameInput=(TextView)findViewById(R.id.receive_name_input);
        inquestReasonInput=(TextView)findViewById(R.id.inquest_reason_input);
        inquisition_type_input= (TextView) findViewById(R.id.inquisition_type_input);

        secondary_back_img=(ImageView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.GONE);
        secondary_title_tv.setText("新增案件");
        secondary_right_tv.setVisibility(View.VISIBLE);
        secondary_right_tv.setText("完成");

        mRootView= findViewById(R.id.rootView);

        caseDistricts.setOnClickListener(this) ;
        caseAddress.setOnClickListener(this) ;
        caseType.setOnClickListener(this) ;
        caseHappenTime.setOnClickListener(this) ;
        receiveName.setOnClickListener(this) ;
        inquestReason.setOnClickListener(this) ;
        caseHappenTimeEnd.setOnClickListener(this) ;
        secondary_right_tv.setOnClickListener(this);
        inquisition_type.setOnClickListener(this);
        receiveCaseNum.setOnClickListener(this);
        inquisition_type_input.setText("简勘");

        list.add("故意杀人案");
        list.add("抢劫案");
        list.add("盗窃案");
        list.add("强奸案");
        list.add("偷税案");
        list.add("故意伤害案");
        list.add("聚众斗殴案");
        list.add("寻衅滋事案");
        list.add("贩卖毒品案");
        list.add("合同诈骗案");
        list.add("票据诈骗案");
        list.add("职务侵占案");
        list.add("交通肇事案");

        caseDistrictsInput.setText(sharePre.getString("organizationCname", ""));
//        caseAddressInput.setText(sharePre.getString("organizationCname", ""));
        caseAddressInput.setText(sharePre.getString("address", ""));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.receive_case_num:
                // 接警编号
                Text caseRec = new Text(AddNewCase.this);
                if (TextUtils.isEmpty(receiveCaseNumInput.getText().toString()))
                    receiveCaseNumInput.setText("J");
                caseRec.clickPop(AddNewCase.this, receiveCaseNumInput, "接警编号");
                break;
            case R.id.inquisition_type:
                InquisitionType inquisitionType = new InquisitionType(AddNewCase.this, "勘验类型", "",
                        inquisition_type_input);
                break;

            case R.id.case_districts:
                MultiLevelListDialog multiLevelListDialog = new MultiLevelListDialog(AddNewCase.this, caseDistrictsInput, "");
                break;
            case R.id.case_address:
                Text text = new Text(AddNewCase.this);
                text.clickPop(AddNewCase.this, caseAddressInput, "发案地点");
                break;
            case R.id.case_type:
                SpinnerPop spinnerPop = new SpinnerPop(AddNewCase.this, caseTypeinput, "", "AJLBDM");
                break;
            case R.id.case_happen_time:
                DateTimePickDialog dateTimePickDialogUtil = new DateTimePickDialog(AddNewCase.this, "", "案发开始时间");
                dateTimePickDialogUtil.dateTimePicKDialog(caseHappenTimeInput);
                break;

            case R.id.case_happen_time_end:
                DateTimePickDialog dateTimePickDialogUti2 = new DateTimePickDialog(AddNewCase.this, "", "案发结束时间");
                dateTimePickDialogUti2.dateTimePicKDialog(caseHappenTimeEndInput);
                break;

            case R.id.receive_name:
                //多选的使用
                //MultiChoiclListDialog multiChoiclListDialog=new MultiChoiclListDialog(AddNewCase.this);
                //multiChoiclListDialog.clickPop(AddNewCase.this, receiveNameInput, "接勘人");

                String organizationId = sharePre.getString("organizationId", "");
                SingleSelectionPeople singleSelectionPeople = new SingleSelectionPeople(AddNewCase.this, "接警人", organizationId,
                        receiveNameInput);

                break;
            case R.id.inquest_reason:
                Text inquestTeasonText = new Text(AddNewCase.this);
                inquestTeasonText.clickPop(AddNewCase.this, inquestReasonInput, "勘验事由");
                break;
            //完成
            case R.id.secondary_right_tv:

                //caseTypePop(AddNewCase.this, "请先选择勘验类型");


                caseTypeinputKey = (String) caseTypeinput.getTag();

                List<CsDictsConjunction> caseTypeList = EvidenceApplication.db.findAllByWhere(CsDictsConjunction.class,
                        "dictKeyTo = '" + caseTypeinputKey + "'");
                List<CommonTemplate> list = null;
                if (caseTypeList.size() > 0) {
                    caseTypeinputKeyTemp = caseTypeList.get(0).getDictKeyFrom();
                    list = EvidenceApplication.db.findAllByWhere(CommonTemplate.class,
                            "caseTypeCode like '%" + caseTypeinputKeyTemp + "%'", "length(caseTypeCode) desc");//desc

                }

                  if (caseDistrictsInput.getText().toString().equals("")) {
                    Toast.makeText(AddNewCase.this, "请选择发案区划", Toast.LENGTH_SHORT).show();
                } else if (caseAddressInput.getText().toString().equals("")) {
                    Toast.makeText(AddNewCase.this, "请输入发案地点", Toast.LENGTH_SHORT).show();
                } else if (caseTypeinput.getText().toString().equals("")) {
                    Toast.makeText(AddNewCase.this, "请选择案件类别", Toast.LENGTH_SHORT).show();
                } else/* if(receiveNameInput.getText().toString().equals("")){
                    Toast.makeText(AddNewCase.this, "请选择接警人", Toast.LENGTH_SHORT).show();
                }else */if (!caseHappenTimeInput.getText().toString().equals("") &&
                        !caseHappenTimeEndInput.getText().toString().equals("") &&
                        caseHappenTimeEndInput.getText().toString().compareTo(caseHappenTimeInput.getText().toString()) <= 0) {
                    Toast.makeText(AddNewCase.this, "案发结束时间输入错误", Toast.LENGTH_SHORT).show();
                } else if (caseHappenTimeInput.getText().toString().equals("") &&
                        !caseHappenTimeEndInput.getText().toString().equals("")) {
                    Toast.makeText(AddNewCase.this, "请选择案发开始时间", Toast.LENGTH_SHORT).show();
                } else if (!caseHappenTimeInput.getText().toString().equals("") &&
                        caseHappenTimeEndInput.getText().toString().equals("")) {
                    Toast.makeText(AddNewCase.this, "请选择案发结束时间", Toast.LENGTH_SHORT).show();
                } else if (isHasReceptionNo(receiveCaseNumInput.getText().toString())) {
                    Toast.makeText(AddNewCase.this, "接警编号已经存在", Toast.LENGTH_SHORT).show();
                } else if (list == null || list.size() == 0) {
                    String key = "";
                      if (TextUtils.equals(receiveCaseNumInput.getText().toString(),"J"))
                          receiveCaseNumInput.setText(null);
                    //PopListSingleLevel popListSingleLevel = new PopListSingleLevel(AddNewCase.this, "勘验类型", v, getCaseTemplates(v.getContext()));
                       /* CaseTypeBigSelect caseTypeBigSelect = new CaseTypeBigSelect(AddNewCase.this, "勘验类型","GASSLAJ", caseTypeinput);
                        caseTypeBigSelect.setListener(new CaseTypeBigSelect.onResultListener() {
                            @Override
                            public void onResult(String Dictkey) {
                                String templateId="";
                                Intent intent = new Intent(AddNewCase.this, ProspectPreview.class);
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
                                intent.putExtra("caseInfo", inquestReasonInput.getText().toString());
                                intent.putExtra("templateId", templateId);
                                startActivity(intent);
                               setTemplateId(templateId, caseId);
                                finish();
                            }
                        });*/

                    String templateId = "";

                    List<CommonTemplate> CommonTemplateList = EvidenceApplication.db.findAllByWhere(CommonTemplate.class,
                            "caseTypeCode = '" + (-1) + "' ");
                    if (CommonTemplateList.size() > 0) {

                        Intent intent = new Intent(AddNewCase.this, ProspectPreview.class);
                        try {
                            saveData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        templateId = CommonTemplateList.get(0).getSid();

                        intent.putExtra("caseId", caseId);
                        intent.putExtra("caseInfo", inquestReasonInput.getText().toString());
                        intent.putExtra("templateId", templateId);

                        List<CsSceneCases> list1 = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                                " caseNo = '" + caseId + "'");
                        list1.get(0).setReceivePeopleNum(sharePre.getString("userId", ""));
                        list1.get(0).setReceivePeople(sharePre.getString("prospectPerson", ""));
                        list1.get(0).setReceiveCaseTime(ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        list1.get(0).setSortListDateTime(ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));

                        if (inquisition_type_input.getText().toString().equals("简勘")) {
                            intent.putExtra("dealType", true);
                            if (list1 != null && list1.size() > 0) {
                                list1.get(0).setDealType("1");
                                EvidenceApplication.db.update(list1.get(0));
                            }

                        } else {
                            if (list1 != null && list1.size() > 0) {
                                list1.get(0).setDealType("0");
                                EvidenceApplication.db.update(list1.get(0));
                            }

                        }
                        startActivity(intent);
                        setTemplateId(templateId, caseId);
                        finish();
                    }else{
                        Toast.makeText(AddNewCase.this, "基础模板不存在", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Intent intent = new Intent(AddNewCase.this, ProspectPreview.class);
                    try {
                        saveData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<CsSceneCases> list1 = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                            " caseNo = '" + caseId + "'");
                    list1.get(0).setReceivePeopleNum(sharePre.getString("userId", ""));
                    list1.get(0).setReceivePeople(sharePre.getString("prospectPerson", ""));
                    list1.get(0).setReceiveCaseTime(ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
                    list1.get(0).setSortListDateTime(ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));

                    intent.putExtra("caseId", caseId);
                    intent.putExtra("caseInfo", inquestReasonInput.getText().toString());
                    intent.putExtra("templateId", list.get(0).getSid());
                    startActivity(intent);
                    setTemplateId(list.get(0).getSid(), caseId);
                    finish();
                }

                break;
        }
    }

    private boolean isHasReceptionNo(String receptionNo){
        if (TextUtils.isEmpty(receptionNo))
            return false;
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"receptionNo = '" + receptionNo + "'");
        return list != null && list.size() > 0;
    }

    private void saveData() throws JSONException {

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());

            CsSceneCases csSceneCases=new CsSceneCases();
            csSceneCases.setId(ViewUtil.getUUid());
            caseId=UUID.randomUUID().toString().replace("-", "");
            csSceneCases.setCaseNo(caseId);

            csSceneCases.setSceneRegionalismName(caseDistrictsInput.getText().toString());
            csSceneCases.setSceneDetail(caseAddressInput.getText().toString());
            csSceneCases.setCaseType(caseTypeinput.getText().toString());
            try {
                csSceneCases.setOccurrenceDateFrom(sDateFormat.parse(caseHappenTimeInput.getText().toString()));
                csSceneCases.setOccurrenceDateTo(sDateFormat.parse(caseHappenTimeEndInput.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            csSceneCases.setReceivePeople(receiveNameInput.getText().toString());
            csSceneCases.setExposureProcess(inquestReasonInput.getText().toString());
            csSceneCases.setReceiveCaseTime(date);
            csSceneCases.setSortListDateTime(date);

            csSceneCases.setStatus("2");
            String caseDistrictsInputKey=(String)caseDistrictsInput.getTag();//案件区划ID
            csSceneCases.setSceneRegionalism(caseDistrictsInputKey);
            caseTypeinputKey=(String)caseTypeinput.getTag();//案发类型id
            csSceneCases.setCaseTypeId(caseTypeinputKey);
            String receiveNameInputKey=(String)receiveNameInput.getTag();//接勘人id
            csSceneCases.setReceivePeopleNum(receiveNameInputKey);

            csSceneCases.setInquestId(UUID.randomUUID().toString().replace("-", ""));
            csSceneCases.setReceptionNo(receiveCaseNumInput.getText().toString());
            csSceneCases.setIsManualAddCase(true);
            csSceneCases.setAlarmAddress(csSceneCases.getSceneDetail());
            EvidenceApplication.db.save(csSceneCases);

            CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId,"SCENE_LAW_CASE_EXT");
            JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());


        jsonObject.put("OCCURRENCE_DATE_FROM",caseHappenTimeInput.getText().toString());
        jsonObject.put("SCENE_DETAIL",caseAddressInput.getText().toString());
        jsonObject.put("CASE_TYPE",caseTypeinput.getTag());
        jsonObject.put("CASE_TYPE_NAME",caseTypeinput.getText().toString());
        jsonObject.put("ASSIGNED_CONTENT",inquestReasonInput.getText().toString());
        jsonObject.put("RECEIVED_DATE",date);
        jsonObject.put("CRACKED_DATE"," ");//破案时间
        jsonObject.put("OCCURRENCE_DATE_TO",caseHappenTimeEndInput.getText().toString());//案发借宿时间
        jsonObject.put("RECEIVED_BY_ID",receiveNameInput.getTag());//接警人
        jsonObject.put("RECEIVED_BY_ID_NAME",receiveNameInput.getText().toString());//接警人
        jsonObject.put("SCENE_REGIONALISM_NAME",caseDistrictsInput.getText().toString());//区划
        jsonObject.put("SCENE_REGIONALISM",caseDistrictsInputKey);//区划
        jsonObject.put("RECEPTION_NO",csSceneCases.getReceptionNo());//接警编号

        //jsonObject.put("RECEIVED_BY_ID",sharePre.getString("userId", ""));//接警人
        //jsonObject.put("RECEIVED_BY_ID_NAME",sharePre.getString("prospectPerson", ""));//接警人



        caseBasicInfo.setJson(jsonObject.toString());
        EvidenceApplication.db.update(caseBasicInfo);

            CaseBasicInfo caseBasicInfo1 = ViewUtil.getCaseBasicInfo(caseId,"SCENE_INVESTIGATION_EXT");
            JSONObject jsonObject1 = new JSONObject(caseBasicInfo1.getJson());


            jsonObject1.put("DIRECTOR_IDS",sharePre.getString("userId",""));//现场指挥人员
            jsonObject1.put("DIRECTOR_IDS_NAME",sharePre.getString("prospectPerson",""));//现场指挥人员

            jsonObject1.put("INVESTIGATOR_IDS",sharePre.getString("userId",""));//现场勘验人员
            jsonObject1.put("INVESTIGATOR_IDS_NAME",sharePre.getString("prospectPerson",""));//现场勘验人员

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
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = 'GASSLAJ'");

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



}
