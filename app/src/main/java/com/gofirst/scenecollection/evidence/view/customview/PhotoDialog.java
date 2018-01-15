package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
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
import com.gofirst.scenecollection.evidence.view.activity.PhotoDialogActivity;
import com.gofirst.scenecollection.evidence.view.activity.ShowIcPhotoActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class PhotoDialog extends LinearLayout implements BaseView {

    public String[] args;
    public Activity activity;
    protected TextView name;
    protected TextView startPop;
    protected String saveKey;
    protected View view;
    private boolean hasSign,viewWithoutToast;
    private BroadcastReceiver receiver;
    private String mode;
    private String section;
    private String isRequiredField;
    private LinearLayout start_pop_linearLayout;

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    public PhotoDialog(Context context) {
        super(context);
    }

    public PhotoDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initLayout(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_list_ptoto_layout, this, true);
        name = (TextView) view.findViewById(R.id.name);
        start_pop_linearLayout=(LinearLayout)view.findViewById(R.id.start_pop_linearLayout);
        startPop = (TextView) view.findViewById(R.id.start_pop);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayNote(mode);
            }
        };
        context.registerReceiver(receiver, new IntentFilter("ic"));
        context.registerReceiver(receiver, new IntentFilter("id_card_info"));
    }

    @Override
    public String getText() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + args[0] + "' and father = '" + args[1] + "' and child = '" + saveKey + "'");
        return list != null && list.size() != 0 ? list.get(0).getId() : "";
    }


    @Override
    public String getViewName() {
        return "CAPTURE_IC";
    }

    @Override
    public void initView(final String mode, String name, String text, final String saveKey, String textColor, String dataType, String isRequiredField) {
        this.saveKey = saveKey;
        this.mode = mode;
        this.isRequiredField=isRequiredField;
        initLayout(getContext());
        this.name.setText(name);
        start_pop_linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {

                    Intent intent = new Intent(getContext(), PhotoDialogActivity.class);
                    intent.putExtra("caseId", args[0]);
                    intent.putExtra("father", args[1]);
                    intent.putExtra("name", saveKey);
                    intent.putExtra("section", section);
                    intent.putExtra("fileId", getSignBitmapId());

                    v.getContext().startActivity(intent);

                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    v.getContext().startActivity(intent);*/

                    hasSign = true;
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "勘查已经结束", Toast.LENGTH_SHORT).show();
                }

            }


        });
        displayNote(mode);
        ViewUtil.setTextColorAndInputManger(textColor,dataType,startPop);
    }


    @Override
    public boolean validate() {
        return hasSign;
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

    public void setSection(String section){
        this.section = section;
    }
    public String getSection(){
        return section;
    }
    public void setArgs(Activity activity, String... args) {
        this.activity = activity;
        this.args = args;
    }

    private Bitmap getSignBitmap() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + args[0]
                + "' and father = '" + args[1] + "' and child = '" + saveKey + "' and section = '"+section+"'");
        return list.size() != 0 ? BitmapUtils.revitionImageSize(AppPathUtil.getDataPath()+"/"+list.get(0).getFilePath()) : null;
    }

    private String getSignBitmapId() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + args[0]
                + "' and father = '" + args[1] + "' and child = '" + saveKey + "' and section = '"+section+"'");
        return list.size() != 0 ? list.get(0).getId() : "";
    }

    private void displayNote(String mode) {
        Bitmap bitmap = getSignBitmap();
        startPop.setText(bitmap == null ? mode.equals(BaseView.EDIT) ? "点击拍照" : "未拍照" : "");
        if (bitmap != null) {
            LinearLayout.LayoutParams params;
            if (bitmap.getWidth() > bitmap.getHeight()) {
                params = new LinearLayout.LayoutParams(125, 100);//bitmap.getHeight()
            } else {
                params = new LinearLayout.LayoutParams(100, 125);//bitmap.getHeight()
            }
            params.setMarginEnd(63);
            startPop.setLayoutParams(params);
            //startPop.setPadding(100,2,10,2);
            startPop.setBackground(new BitmapDrawable(bitmap));

            startPop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowIcPhotoActivity.class);
                    intent.putExtra("caseId", args[0]);
                    intent.putExtra("father", args[1]);
                    intent.putExtra("name", saveKey);
                    intent.putExtra("section", section);
                   // intent.putExtra("fileId", getSignBitmapId());

                    v.getContext().startActivity(intent);
                }
            });

            hasSign = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(receiver);
        super.onDetachedFromWindow();
    }


}
