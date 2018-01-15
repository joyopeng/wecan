package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/30.
 */
public class ShowIcPhotoActivity extends Activity {

    Bundle data;
    private Bitmap bitmaps[];
    private int position;
    private String caseId="";
    private String father="";
    private String name="";
    private String section="";

    private int window_width, window_height;// 控件宽度
    private int state_height;// 状态栏的高度
    private DragImageView imageview;
    private ViewTreeObserver viewTreeObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.show_imgs_activity);


        /** 获取可見区域高度 **/
        WindowManager manager = getWindowManager();
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();


        Intent intent=getIntent();

        caseId=intent.getStringExtra("caseId");
        father=intent.getStringExtra("father");
        name=intent.getStringExtra("name");
        section=intent.getStringExtra("section");

        Init();

        imageview.setImageBitmap(getSignBitmap());
      //  imageview.setBackground(new BitmapDrawable(getSignBitmapId()));BitmapFactory.decodeFile(mylist.get(position))
        imageview.setmActivity(this);//注入Activity.
        /** 测量状态栏高度 **/
        viewTreeObserver = imageview.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (state_height == 0) {
                            // 获取状况栏高度
                            Rect frame = new Rect();
                            getWindow().getDecorView()
                                    .getWindowVisibleDisplayFrame(frame);
                            state_height = frame.top;
                            imageview.setScreen_H(window_height - state_height);
                            imageview.setScreen_W(window_width);
                        }

                    }
                });
    }

    private void Init(){
        imageview=(DragImageView)findViewById(R.id.imageview);

    }
    private Bitmap getSignBitmap() {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId
                + "' and father = '" + father + "' and child = '" + name + "' and section = '"+section+"'");
        return list.size() != 0 ? BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + list.get(0).getFilePath()) : null;
    }

}

