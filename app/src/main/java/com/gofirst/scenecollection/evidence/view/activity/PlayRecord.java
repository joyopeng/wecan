package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.adapter.PlayRecordAdapter;

import java.util.List;

/**
 * Created by maxiran 2016/5/6.
 */
public class PlayRecord extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.record_list_layout);
        ((TextView)(findViewById(R.id.title).findViewById(R.id.actiobar_textView))).setText("录音列表");
        ListView playRecList = (ListView)findViewById(R.id.play_record_List);
        String where = getIntent().getStringExtra("queryWhere");
        if(where == null || "".equals(where)) {
            String father = getIntent().getStringExtra("father");
            String caseId = getIntent().getStringExtra("caseId");
            playRecList.setAdapter(new PlayRecordAdapter(getRecordFiles(caseId, father)));
        }else{
            playRecList.setAdapter(new PlayRecordAdapter(getRecordFromWhere(where)));
        }
    }

    private List<RecordFileInfo> getRecordFiles(String caseId,String father){
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"caseId = '" + caseId +"' and father = '" + father + "'","fileDate desc");
    }

    private List<RecordFileInfo> getRecordFromWhere(String where){
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,where);
    }
}
