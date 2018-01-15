package com.gofirst.scenecollection.evidence.view.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;

import java.io.IOException;

/**
 * Created by maxiran on 2016/4/20.
 */
public class NoteFragment extends Fragment{


    private RelativeLayout SpenView;
    private SpenSimpleSurfaceView spenSimpleSurfaceView;
    private SpenNoteDoc spenNoteDoc;
    private SpenPageDoc spenPageDoc;
    private Button button;
    private boolean isSave;
    private ImageView display_note;
    private onDisplayNoteThumb listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_fragment,null);
        SpenView = (RelativeLayout)view.findViewById(R.id.pen_layout);
        button = (Button)view.findViewById(R.id.save);
        Spen spen = new Spen();
        try {
            spen.initialize(getActivity());spenSimpleSurfaceView = new SpenSimpleSurfaceView(getActivity());
        spenSimpleSurfaceView.setMaxZoomRatio(1);
        spenSimpleSurfaceView.setMinZoomRatio(1);
        SpenView.addView(spenSimpleSurfaceView);
        Rect rect = new Rect();
        getActivity().getWindowManager().getDefaultDisplay().getRectSize(rect);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getActivity().getResources().getDisplayMetrics());
        SpenView.measure(w, h);spenNoteDoc = new SpenNoteDoc(getActivity(),SpenView.getMeasuredWidth()-padding*2,SpenView.getMeasuredHeight()-padding*2);
        spenPageDoc = spenNoteDoc.appendPage();
        spenPageDoc.setBackgroundColor(Color.parseColor("#F8F8F8"));
        spenPageDoc.clearHistory();
        spenSimpleSurfaceView.setPageDoc(spenPageDoc, true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSave){
                    isSave = true;
                    Bitmap bitmap = spenSimpleSurfaceView.captureCurrentView(false);
                    display_note.setImageBitmap(bitmap);
                    spenPageDoc.removeAllObject();
                    spenSimpleSurfaceView.update();
                    button.setText("退出");
                    if (listener != null){
                        listener.DisplayNoteThumb(bitmap);
                    }
                }else {
                    isSave = false;
                    FragmentManager fragmentManager =  getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(fragmentManager.findFragmentByTag("12"));
                    fragmentTransaction.hide(NoteFragment.this);
                    fragmentTransaction.commit();
                    button.setText("保存");
                }
            }
        });
        view.findViewById(R.id.clear_note).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                spenPageDoc.removeAllObject();
                spenSimpleSurfaceView.update();
                display_note.setImageResource(android.R.color.white);
                display_note.setBackgroundResource(R.drawable.rect_white);
                isSave = false;
                button.setText("保存");
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"你的手机不支持电子签名",Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    @Override
    public void onDestroy() {

        if (spenSimpleSurfaceView != null){
            spenSimpleSurfaceView.close();
            spenSimpleSurfaceView = null;
        }

        if (spenNoteDoc != null){
            try {
                spenNoteDoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            spenNoteDoc = null;
        }
        if (spenPageDoc != null){
            spenPageDoc.clearHistory();
            spenPageDoc = null;
        }
        super.onDestroy();
    }

    public void setOnDisplayNoteThumb(onDisplayNoteThumb listener){
        this.listener = listener;
    }
    public interface onDisplayNoteThumb{
        void DisplayNoteThumb(Bitmap bitmap);
    }
}
