package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.CameraHelper;
import com.gofirst.scenecollection.evidence.utils.UniversalRecorder;
import com.gofirst.scenecollection.evidence.view.customview.CameraView;

/**
 * Created by maxiran on 2016/4/26.
 */
public class TakeVideo extends Activity implements UniversalRecorder.OnRecListener {

    private CameraView mPreview;
    private UniversalRecorder universalRecorder;
    private Chronometer chronometer;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.take_video_layout);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        mPreview = (CameraView) findViewById(R.id.camera_view);
        mCamera = CameraHelper.getDefaultCameraInstance();
        mPreview.setCamera(mCamera);
        String caseId  = getIntent().getStringExtra("caseId");
        String father  = getIntent().getStringExtra("father");
        universalRecorder = new UniversalRecorder(caseId,father,this);
        universalRecorder.setOnRecListener(this);
        findViewById(R.id.take_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (universalRecorder.isRecing) {
                    universalRecorder.stopVideoRec();
                } else {
                    universalRecorder.startVideoRec(mCamera,"video");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        universalRecorder.stopVideoRec();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
        setResult(RESULT_OK);
        super.onDestroy();
    }

    private void startRecordTimer() {
        chronometer.start();
        chronometer.setTextColor(Color.RED);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // TODO: 2016/4/27

            }
        });
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
