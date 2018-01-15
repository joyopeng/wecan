package com.gofirst.scenecollection.evidence.view.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
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
public class AudioEditText extends LinearLayout {

    private String father;
    private String child;
    private ImageView record,play;
    private BroadcastReceiver receiver;
    private String caseId;
    private RelativeLayout relativeLayout;
    public AudioEditText(Context context) {
        super(context);
        initLayout(context);
    }

    public AudioEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.audio_text);
        father = a.getString(R.styleable.audio_text_father);
        child = a.getString(R.styleable.audio_text_child);
        a.recycle();
        initLayout(context);

    }

    public AudioEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.audio_text, defStyleAttr, 0);
        father = a.getString(R.styleable.audio_text_father);
        child = a.getString(R.styleable.audio_text_child);
        a.recycle();
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_edit_text, this, true);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relative);
        record = (ImageView) view.findViewById(R.id.record);
        play = (ImageView) view.findViewById(R.id.play);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshState();
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter("audio"));
        record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRec();
            }
        });
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PLAY = new Intent(getContext(), PlayRecord.class);
                PLAY.putExtra("father", father);
                PLAY.putExtra("caseId", caseId);
                getContext().startActivity(PLAY);
            }
        });
    }



    public void startRec() {
        Intent REC = new Intent(getContext(), TakeRecord.class);
        REC.putExtra("caseId", caseId);
        REC.putExtra("father", father);
        REC.putExtra("child", child );
        getContext().startActivity(REC);
    }

    private boolean isExistFile() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'");
        return list != null && list.size() != 0;
    }



    public void refreshState() {
        boolean isExistFile = isExistFile();
        play.setVisibility(isExistFile ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(receiver);
    }



    public void initView(String mode) {
        if (mode.equals(BaseView.VIEW)) {
            record.setVisibility(GONE);
            relativeLayout.removeView(play);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(OSUtil.dip2px(getContext(),25),OSUtil.dip2px(getContext(),25));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.setMarginEnd(OSUtil.dip2px(getContext(),20));
            relativeLayout.addView(play,layoutParams);
            refreshState();
        }
    }


    public void setArgs(String caseId, String father, String child) {
        this.caseId = caseId;
        this.father = father;
        this.child = child;
        refreshState();
    }

    public String getAudioPath() {
        RecordFileInfo recordFileInfo = getRecFileInfo();
        return recordFileInfo != null ? recordFileInfo.getFilePath() : "null";
    }

    public RecordFileInfo getRecFileInfo() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "'" + " and father = '" + father + "'" + " and child = '" + child + "'");
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}
