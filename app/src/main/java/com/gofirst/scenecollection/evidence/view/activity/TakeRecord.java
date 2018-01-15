package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.UniversalRecorder;

import java.util.List;

/**
 * @author maxiran on 2016/5/5.
 */
public class TakeRecord extends Activity implements UniversalRecorder.OnRecListener {

    private Chronometer chronometer;
    private ImageView start, stop, rec_list;
    private UniversalRecorder universalRecorder;
    private String caseId;
    private String father;
    //add zsh 20160715 on
    private boolean mIsAnchor = false;
    private String mSection = "";
    //add zsh 20160715 off
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.take_record_layout);
        chronometer = (Chronometer) findViewById(R.id.timer);
        start = (ImageView) findViewById(R.id.start);
        stop = (ImageView) findViewById(R.id.stop);
        rec_list = (ImageView) findViewById(R.id.rec_list);
        father = getIntent().getStringExtra("father");
        final String child = getIntent().getStringExtra("child");
        caseId = getIntent().getStringExtra("caseId");
        mIsAnchor = getIntent().getBooleanExtra("isAnchor", false);
        mSection = getIntent().getStringExtra("section");
        universalRecorder = new UniversalRecorder(caseId, father, this);
        universalRecorder.setOnRecListener(this);
        if (mIsAnchor) {
            universalRecorder.setIsAchar(true);
            universalRecorder.setSection(mSection);
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    universalRecorder.startAudioRec(child);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                universalRecorder.stopAudioRec();
                //add zsh 20160715 on
                if (mIsAnchor) {
                    Intent result = new Intent();
                    result.putExtra("soundpath", universalRecorder.getCurrentPath());
                    if (universalRecorder.getRecordFileInfo() != null) {
                        result.putExtra("dataId", universalRecorder.getRecordFileInfo().getId());
                    } else {
                        result.putExtra("dataId", "");
                    }
                    setResult(10001, result);
                    TakeRecord.this.finish();
                }
                //add zsh 20160715 off
            }
        });
        rec_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PLAY = new Intent(TakeRecord.this, PlayRecord.class)
                        .putExtra("father", getIntent().getStringExtra("father"))
                        .putExtra("caseId", caseId);
                startActivity(PLAY);
            }
        });
    }

    private void startRecordTimer() {
        chronometer.start();
        chronometer.setTextColor(Color.RED);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // // TODO: 2016/5/5

            }
        });
    }


    private int getRecNum() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        return list.size();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
    }

    @Override
    protected void onDestroy() {
        universalRecorder.stopAudioRec();
        super.onDestroy();
    }

    @Override
    public void onRecStart() {
        startRecordTimer();
    }

    @Override
    public void onRecStop() {
        chronometer.stop();
        chronometer.setTextColor(Color.WHITE);
    }
}

