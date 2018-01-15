package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.LostGood;
import com.gofirst.scenecollection.evidence.view.adapter.LostGoodsAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class SeconddaryView extends Activity {


    private String caseId,templateId;
    private ListView listView;
    private String father;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.lost_goods_layout);
        listView = (ListView) findViewById(R.id.good_list);
        ((TextView)findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(getIntent().getStringExtra("title"));
        caseId = getIntent().getStringExtra("caseId");
        father = getIntent().getStringExtra("father");
        templateId = getIntent().getStringExtra("templateId");
        LostGoodsAdapter adapter = new LostGoodsAdapter(caseId, getGoods(), father,templateId,getIntent().getBooleanExtra(BaseView.ADDREC,false));
        String mode = getIntent().getStringExtra("mode");
        adapter.setMode(mode,getIntent().getStringExtra("title"));
        listView.setAdapter(adapter);
        findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private List<LostGood> getGoods() {
        return EvidenceApplication.db.findAllByWhere(LostGood.class, "caseId = '" + caseId + "' and father = '" + father + "'");
    }

    @Override
    public void onResume() {
        LostGoodsAdapter adapter = new LostGoodsAdapter(caseId, getGoods(), father,templateId,getIntent().getBooleanExtra(BaseView.ADDREC,false));
        String mode = getIntent().getStringExtra("mode");
        adapter.setMode(mode,getIntent().getStringExtra("title"));
        listView.setAdapter(adapter);
        super.onResume();
    }

}
