package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CasePeople;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.view.customview.SegmentedGroup;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

/**
 * @author maxiran
 */
public class Reporter extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private CasePeople casePeople;
    private EditText name;
    private EditText age;
    private EditText idCard;
    private EditText phoneNumber;
    private EditText company;
    private EditText currentAddress;
    private EditText registerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.reporter_deltail_layout);
        Button add_new_btn = (Button) findViewById(R.id.save);
        add_new_btn.setOnClickListener(this);
        SegmentedGroup sexGroup = (SegmentedGroup) findViewById(R.id.sex);
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        idCard = (EditText) findViewById(R.id.id_card);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        company = (EditText) findViewById(R.id.company);
        currentAddress = (EditText) findViewById(R.id.current_address);
        registerAddress = (EditText) findViewById(R.id.register_address);
        sexGroup.setOnCheckedChangeListener(this);
        casePeople = getCasePeople();
        name.setText(casePeople.getName() != null ? casePeople.getName() : "");
        age.setText(casePeople.getAge() != null ? casePeople.getAge() : "");
        idCard.setText(casePeople.getIdCard() != null ? casePeople.getIdCard() : "");
        phoneNumber.setText(casePeople.getPhoneNum() != null ? casePeople.getPhoneNum() : "");
        company.setText(casePeople.getUnit() != null ? casePeople.getUnit() : "");
        currentAddress.setText(casePeople.getCurrentAddress() != null ? casePeople.getCurrentAddress() : "");
        registerAddress.setText(casePeople.getRegisterAddress() != null ? casePeople.getRegisterAddress() : "");
        RadioButton male = (RadioButton) sexGroup.findViewById(R.id.male);
        RadioButton female = (RadioButton) sexGroup.findViewById(R.id.female);
        String sex = casePeople.getSex();
        if (sex != null) {
            male.setChecked(sex.equals("男"));
            female.setChecked(sex.equals("女"));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton) group.findViewById(checkedId);
        casePeople.setSex(button.getText().toString());

    }

    private CasePeople getCasePeople() {
        String uuid = getIntent().getStringExtra("uuid");
        return EvidenceApplication.db.findById(uuid, CasePeople.class);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                casePeople.setName(name.getText().toString());
                casePeople.setAge(age.getText().toString());
                casePeople.setCurrentAddress(currentAddress.getText().toString());
                casePeople.setIdCard(idCard.getText().toString());
                casePeople.setRegisterAddress(registerAddress.getText().toString());
                casePeople.setPhoneNum(phoneNumber.getText().toString());
                casePeople.setUnit(company.getText().toString());
                EvidenceApplication.db.update(casePeople);
                saveJson();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void saveJson() {
        casePeople = getCasePeople();
        String father = getIntent().getStringExtra("father");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(getIntent().getStringExtra("caseId"), father + casePeople.getPeopleType());
        String json = com.alibaba.fastjson.JSON.toJSONString(casePeople);
        dataTemp.setData(json);
        dataTemp.setDataType("scene_investigation_data");
        EvidenceApplication.db.update(dataTemp);
    }
}
