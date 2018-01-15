package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;

public class NoteActivity extends Activity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String caseId, father;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.note_activity_layout);
        this.iv = (ImageView) this.findViewById(R.id.note_area);
        findViewById(R.id.clear_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                canvas.drawColor(Color.WHITE);
                iv.setImageBitmap(baseBitmap);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        findViewById(R.id.finish_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });
        int titleHigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int high = getWindowManager().getDefaultDisplay().getHeight() - getStatusBarHeight() - titleHigh;
        Bitmap bitmap = getLastBitmap();
        baseBitmap = bitmap != null ? bitmap.copy(Bitmap.Config.ARGB_8888, true) : Bitmap.createBitmap(width, high, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBitmap);
        paint = new Paint();
//        paint.setColor(getResources().getColor(R.color.main_blue));
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setStrokeWidth(16);
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);
        iv.setImageBitmap(baseBitmap);
        iv.setOnTouchListener(new View.OnTouchListener() {

            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int stopX = (int) event.getX();
                        int stopY = (int) event.getY();
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        iv.setImageBitmap(baseBitmap);
                        break;
                }
                return true;
            }
        });
    }

    public void save() {
        try {
            Toast.makeText(this, "签名保存中...", Toast.LENGTH_SHORT).show();
            caseId = getIntent().getStringExtra("caseId");
            father = getIntent().getStringExtra("father");
            String secPath = ViewUtil.getCurrentTime("yyyyMMdd") + "/" + caseId + "/" + father;
            File file = new File(AppPathUtil.getDataPath() + "/" + secPath);
            if (!file.exists())
                file.mkdirs();
            String name = getIntent().getStringExtra("refId");
            RecordFileInfo recordFileInfo = getRecordFileInfo(caseId,
                    father, TextUtils.isEmpty(name) ? ViewUtil.getUUid() : name, getIntent().getStringExtra("section"));
            if (!TextUtils.isEmpty(name))
                new File(AppPathUtil.getDataPath() + "/" + recordFileInfo.getFilePath()).delete();
            OutputStream stream = new FileOutputStream(new File(file, recordFileInfo.getId() + ".png"));
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            recordFileInfo.setFilePath(secPath + "/" + recordFileInfo.getId() + ".png");
            recordFileInfo.setFileDate(new Date());
            recordFileInfo.setFileType("png");
            recordFileInfo.setChild("note");
            recordFileInfo.setTwoHundredFilePath(recordFileInfo.getFilePath());
            EvidenceApplication.db.update(recordFileInfo);
            Intent intent = new Intent();
            intent.setAction("note");
            intent.putExtra("id", recordFileInfo.getId());
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    private Bitmap getLastBitmap() {
        String fileId = getIntent().getStringExtra("refId");
        if (TextUtils.isEmpty(fileId))
            return null;
        RecordFileInfo recordFileInfo = EvidenceApplication.db.findById(fileId, RecordFileInfo.class);
        return recordFileInfo != null ? BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + recordFileInfo.getFilePath()) : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public RecordFileInfo getRecordFileInfo(String caseId, String father, String id, String section) {
        RecordFileInfo list = EvidenceApplication.db.findById(id, RecordFileInfo.class);
        if (list == null) {
            RecordFileInfo saveNote = new RecordFileInfo();
            saveNote.setId(id);
            saveNote.setCaseId(caseId);
            saveNote.setFather(father);
            saveNote.setSection(section);
            EvidenceApplication.db.save(saveNote);
        }
        return EvidenceApplication.db.findById(id, RecordFileInfo.class);
    }
}

