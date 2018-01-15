package com.gofirst.scenecollection.evidence.view.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.activity.PlayRecord;
import com.gofirst.scenecollection.evidence.view.activity.TakeRecord;

import java.util.List;

/**
 * 只要在XML中配置father，child参数即可使用
 * father 父级模块 如现场环境
 * child 子集模块 如天气备注
 * 通过接收广播实现文件更新回调
 *
 * @author maxiran、
 */
public class Audio extends LinearLayout implements View.OnClickListener {

    private boolean isExistFile;
    private String father;
    private String child;
    private ImageView record;
    private BroadcastReceiver receiver;
    private EditText editText;
    private String caseId;
    private TextView name;
    private String saveKey;
    private String status = "";

    public Audio(Context context) {
        super(context);
        initLayout(context);
    }

    public Audio(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.audio_text);
        father = a.getString(R.styleable.audio_text_father);
        child = a.getString(R.styleable.audio_text_child);
        a.recycle();
        initLayout(context);

    }

    public Audio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.audio_text, defStyleAttr, 0);
        father = a.getString(R.styleable.audio_text_father);
        child = a.getString(R.styleable.audio_text_child);
        a.recycle();
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_layout, this, true);
        record = (ImageView) view.findViewById(R.id.record);
        record.setOnClickListener(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshState();
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter("audio"));
    }

    @Override
    public void onClick(View v) {
        startRec();
    }

    public void startRec() {
        Intent REC = new Intent(getContext(), TakeRecord.class);
        REC.putExtra("caseId", caseId);
        REC.putExtra("father", father);;
        String name = ViewUtil.getFragementName(father);
        REC.putExtra("child", TextUtils.isEmpty(name) ? child : name);
        Intent PLAY = new Intent(getContext(), PlayRecord.class);
        PLAY.putExtra("father", father);
        PLAY.putExtra("caseId", caseId);
        getContext().startActivity(isExistFile ? PLAY : REC);
    }

    private int getRecNum() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        return list.size();
    }
    private boolean isExistFile() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "' and fileType = 'audio'");
        return list != null && list.size() != 0;
    }


    public void refreshState() {
        isExistFile = isExistFile();
        record.setImageResource(isExistFile ? R.drawable.play : R.drawable.record);
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(receiver);
        super.onDetachedFromWindow();
    }

    public void setArgs(String caseId, String father, String child) {
        this.caseId = caseId;
        this.father = father;
        this.child = child;
        refreshState();
    }

    public void setArgs(String caseId, String father, String child,String status) {
        this.caseId = caseId;
        this.father = father;
        this.child = child;
        this.status = status;
        if("0".equals(status) && record != null) {
            record.setOnClickListener(null);
        }
        refreshState();
    }

    public void setUnclickable() {
        record.setOnClickListener(null);
    }
}
