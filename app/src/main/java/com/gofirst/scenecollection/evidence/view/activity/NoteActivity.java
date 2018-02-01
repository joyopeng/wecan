package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gftxcky.draw.Md5;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

public class NoteActivity extends Activity {

    private final int success = 1;
    private final int failed = -1;
    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String caseId, father;
    private boolean isselfSign;
    private SharePre sharePre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.note_activity_layout);
        sharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        isselfSign = getIntent().getBooleanExtra("isselfSign", false);
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
                if (isselfSign) {
                    saveSign();
                } else {
                    save();
                    finish();
                }
            }
        });
        int titleHigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int high = getWindowManager().getDefaultDisplay().getHeight() - getStatusBarHeight() - titleHigh;
        Bitmap bitmap = getLastBitmap();
        baseBitmap = bitmap != null ? bitmap.copy(Bitmap.Config.ARGB_8888, true) : Bitmap.createBitmap(width, high, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBitmap);
        if (isselfSign) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            canvas.drawColor(Color.WHITE);
            iv.setImageBitmap(baseBitmap);
        }
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

    public void saveSign() {
        String employeeNo = sharePre.getString("user_id", "");
        try {
            Toast.makeText(this, "签名上传中...", Toast.LENGTH_SHORT).show();
            File file = new File(AppPathUtil.getDataPath() + "/selfsign");
            if (!file.exists())
                file.mkdirs();
            File orginfile = new File(file, employeeNo + ".jpg");
            OutputStream stream = new FileOutputStream(orginfile);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            File thumfile = new File(file, employeeNo + "&_thum_&.jpg");
            Bitmap thumb = Bitmap.createScaledBitmap(baseBitmap, 120, 60, false);
            OutputStream thumbstream = new FileOutputStream(thumfile);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbstream);
            thumbstream.close();
            final HashMap<String, File> map = new HashMap<>();
//            map.put(orginfile.getName(), orginfile);
            map.put(thumfile.getName(), thumfile);
            final List<File> files = new ArrayList();
            files.add(thumfile);
            files.add(orginfile);
            new Thread() {
                public void run() {
                    sendMultipart(PublicMsg.BASEURL + "/uploadSignFile", null, "uploadFile", files);
                }
            }.start();
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

    public byte[] read(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


    public void sendMultipart(final String reqUrl, final Map<String, String> params, final String pic_key, final List<File> files) {
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        //遍历map中所有参数到builder
        if (params != null) {
            for (String key : params.keySet()) {
                multipartBodyBuilder.addFormDataPart(key, params.get(key));
            }
        }

        if (files != null) {
            for (File file : files) {
                multipartBodyBuilder.addFormDataPart(pic_key, file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
        }
        //构建请求体
        RequestBody requestBody = multipartBodyBuilder.build();
        Request.Builder RequestBuilder = new Request.Builder();
        RequestBuilder.url(reqUrl);
        RequestBuilder.post(requestBody);
        Request request = RequestBuilder.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myhandler.sendEmptyMessage(failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if ("true".equals(result)) {
                    myhandler.sendEmptyMessage(success);
                } else {
                    myhandler.sendEmptyMessage(failed);
                }

            }
        });
    }

    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.what;
            switch (status) {
                case success: {
                    Toast.makeText(NoteActivity.this, "签名上传完成", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                case failed: {
                    Toast.makeText(NoteActivity.this, "签名上传失败", Toast.LENGTH_SHORT).show();
//                    finish();
                }
                break;
            }
        }
    };
}

