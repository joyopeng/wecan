package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.view.activity.NoteActivity;

import org.json.JSONObject;

/**
 * 签名控件
 */
public class CaptureSign extends LinearLayout implements BaseView {

    public String[] args;
    public Activity activity;
    protected TextView name;
    protected TextView startPop;
    protected String saveKey;
    protected View view;
    private boolean hasSign,viewWithoutToast;
    private BroadcastReceiver receiver;
    private String mode;
    private View click;
    private String section, text,isRequiredField;

    public CaptureSign(Context context) {
        super(context);
    }

    public CaptureSign(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptureSign(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    protected void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.cpature_sign_layout, this, true);
        name = (TextView) view.findViewById(R.id.name);
        startPop = (TextView) view.findViewById(R.id.start_pop);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                text = intent.getStringExtra("id");
                displayNote(mode);
            }
        };
        click = view.findViewById(R.id.click);
        context.registerReceiver(receiver, new IntentFilter("note"));
    }

    @Override
    public String getText() {
        return text;
    }


    @Override
    public String getViewName() {
        return name.getText().toString();
    }

    @Override
    public void initView(final String mode, final String name, final String text, final String saveKey, String textColor, String dataType,String isRequiredField) {
        this.saveKey = saveKey;
        this.mode = mode;
        if (!TextUtils.isEmpty(text))
            this.text = text;
        initLayout(getContext());
        this.name.setText(name);
        this.isRequiredField = isRequiredField;
        click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    Intent intent = new Intent(getContext(), NoteActivity.class);
                    intent.putExtra("caseId", args[0]);
                    intent.putExtra("father", args[1]);
                    intent.putExtra("name", saveKey);
                    intent.putExtra("section", section);
                    intent.putExtra("refId", CaptureSign.this.text);
                    intent.putExtra("fileId", getSignBitmapId());
                    v.getContext().startActivity(intent);
                    hasSign = true;
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "勘查已经结束", Toast.LENGTH_SHORT).show();
                }

            }
        });
        displayNote(mode);
        ViewUtil.setTextColorAndInputManger(textColor, dataType, startPop);
    }

    @Override
    public boolean validate() {
        return hasSign && !TextUtils.isEmpty(text);
    }

    @Override
    public String getSaveKey() {
        return saveKey;
    }

    @Override
    public String getIsRequireField() {
        return isRequiredField;
    }

    @Override
    public void saveName(JSONObject jsonObject) {

    }

    @Override
    public boolean isID() {
        return false;
    }

    @Override
    public void setID(String id) {

    }


    public void setSection(String section) {
        this.section = section;
    }

    public void setArgs(Activity activity, String... args) {
        this.activity = activity;
        this.args = args;
    }

    private Bitmap getSignBitmap() {
        if (TextUtils.isEmpty(text))
            return null;
        RecordFileInfo list = EvidenceApplication.db.findById(text, RecordFileInfo.class);
        return list != null ? BitmapUtils.revitionImageSize(AppPathUtil.getDataPath() + "/" + list.getFilePath()) : null;
    }

    private String getSignBitmapId() {
        return TextUtils.isEmpty(text) ? "" : text;
    }

    private void displayNote(String mode) {
        Bitmap bitmap = getSignBitmap();
        startPop.setText(bitmap == null ? mode.equals(BaseView.EDIT) ? "点击签名" : "未签名" : "");
        if (bitmap != null) {
            startPop.setBackground(new BitmapDrawable(bitmap));
            hasSign = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(receiver);
        super.onDetachedFromWindow();
    }

}
