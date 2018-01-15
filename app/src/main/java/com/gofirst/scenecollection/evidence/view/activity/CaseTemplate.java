package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.adapter.CaseTemplateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maxiran
 *         案件类型展示列表
 */
public class CaseTemplate extends Activity implements AdapterView.OnItemClickListener {

    private List<String> templateIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.case_template);
        templateIds = new ArrayList<>();
        GridView gridView = (GridView) findViewById(R.id.case_template_grid);
        gridView.setAdapter(new CaseTemplateAdapter(getCaseTemplates()));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(CaseTemplate.this, ProspectPreview.class);
        intent.putExtra("caseId", getIntent().getStringExtra("caseId"));
        intent.putExtra("caseInfo", getIntent().getStringExtra("caseInfo"));
        String templateId = templateIds.get(position);
        intent.putExtra("templateId", templateId);
        intent.putExtra("mode", "edit");
        startActivity(intent);
        setTemplateId(templateId);
        finish();
    }

    private List<String> getCaseTemplates() {
        SharePre userInfo = new SharePre(CaseTemplate.this, "user_info", Context.MODE_PRIVATE);
        String orgId = userInfo.getString("organizationId", "");
        List<CommonTemplate> commonTemplates = EvidenceApplication.db.findAllByWhere(CommonTemplate.class, "orgId = '" + orgId + "'");
        List<String> caseTemplates = new ArrayList<>();
        for (CommonTemplate commonTemplate : commonTemplates) {
            List<CsDicts> csDicts = EvidenceApplication.db.findAllByWhere(CsDicts.class, "dictKey = '" +
                    commonTemplate.getCaseTypeCode() + "'" + " and " + "rootKey = 'AJLBDM'");
            for (CsDicts csDict : csDicts) {
                caseTemplates.add(csDict.getDictValue1());
            }
            templateIds.add(commonTemplate.getSid());
        }
        return caseTemplates;

    }

    private void setTemplateId(String templateId) {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + getIntent().getStringExtra("caseId") + "'");
        if (list != null && list.size() != 0) {
            CsSceneCases csSceneCases = list.get(0);
            csSceneCases.setTemplateId(templateId);
            EvidenceApplication.db.update(csSceneCases);
            Toast.makeText(this, "模板已经选择", Toast.LENGTH_SHORT).show();
        }
    }
}
