package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;

import java.io.IOException;

/**
 * @author maxiran
 */
public class TakeNoteDialog {

    private Dialog dialog;
    private TextView textView;
    private RelativeLayout SpenView;
    private SpenNoteDoc spenNoteDoc;
    private SpenPageDoc spenPageDoc;
    private boolean isSave;
    private SpenSimpleSurfaceView spenSimpleSurfaceView;
    private String name, belong;
    private Activity activity;
    private String mCaseId;
    private String father;

    public TakeNoteDialog(final TextView textView, Activity activity, String name, final String belong, final String father) {
        this.textView = textView;
        this.name = name;
        this.belong = belong;
        this.activity = activity;
        this.father = father;
        dialog = new Dialog(activity, R.style.noteDialog);
        dialog.setContentView(R.layout.note_fragment);
        SpenView = (RelativeLayout) dialog.findViewById(R.id.pen_layout);
        Button button = (Button) dialog.findViewById(R.id.save);
        Spen spen = new Spen();
        try {
            spen.initialize(activity);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
        spenSimpleSurfaceView = new SpenSimpleSurfaceView(activity);
        spenSimpleSurfaceView.setMaxZoomRatio(1);
        spenSimpleSurfaceView.setMinZoomRatio(1);
        SpenView.addView(spenSimpleSurfaceView);
        Rect rect = new Rect();
        activity.getWindowManager().getDefaultDisplay().getRectSize(rect);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, activity.getResources().getDisplayMetrics());
        try {
            spenNoteDoc = new SpenNoteDoc(activity, rect.width() - padding * 10, padding * 40);
        } catch (IOException e) {
            e.printStackTrace();
        }
        spenPageDoc = spenNoteDoc.appendPage();
        spenPageDoc.setBackgroundColor(Color.parseColor("#F8F8F8"));
        spenPageDoc.clearHistory();
        spenSimpleSurfaceView.setPageDoc(spenPageDoc, true);
        dialog.findViewById(R.id.clear_note).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                spenPageDoc.removeAllObject();
                spenSimpleSurfaceView.update();
                isSave = false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSave) {
                    Bitmap bitmap = spenSimpleSurfaceView.captureCurrentView(false);
                    spenPageDoc.removeAllObject();
                    spenSimpleSurfaceView.update();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bitmap.getHeight() / 2);
                    textView.setLayoutParams(params);
                    textView.setBackground(new BitmapDrawable(bitmap));
                    textView.setText("");
                    saveNoteImage(bitmap, belong, father);
                    dialog.dismiss();
                }
                isSave = !isSave;
            }
        });
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (spenSimpleSurfaceView != null) {
                    spenSimpleSurfaceView.close();
                    spenSimpleSurfaceView = null;
                }

                if (spenNoteDoc != null) {
                    try {
                        spenNoteDoc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    spenNoteDoc = null;
                }
                if (spenPageDoc != null) {
                    spenPageDoc.clearHistory();
                    spenPageDoc = null;
                }
            }
        });
    }

    private void saveNoteImage(Bitmap bitmap, String belong, String father) {
        BitmapUtils.saveBitmap(belong, activity, bitmap, name, father, name, new BitmapUtils.OnSaveSuccessListener() {
            @Override
            public void onSuccess(RecordFileInfo recordFileInfo) {
                Toast.makeText(activity, "签名已保存", Toast.LENGTH_SHORT).show();
            }
        }, mCaseId);
    }

    public void setCaseId(String caseId) {
        this.mCaseId = caseId;
    }
}
