package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.customview.DragImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 */
public class ShowImgsActivity  extends Activity {

    Bundle data;
    private Bitmap bitmaps[];
    private int position;
    private Bundle bundle;
    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
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
        bundle= getIntent().getExtras();
        //List<HashMap<String,String>> mylist = (List<HashMap<String, String>>)intent.getSerializableExtra("list");
        List<String> mylist = (List<String>)intent.getSerializableExtra("list");
        position=getIntent().getIntExtra("position", 0);
        Log.d("position",position+"");

        Init();

        imageview.setImageBitmap(BitmapFactory.decodeFile(mylist.get(position)));
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
        /*secondary_back_img=(ImageView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.GONE);
        //secondary_title_tv.setText("");
        secondary_title_tv.setVisibility(View.GONE);
        secondary_right_tv.setVisibility(View.GONE);*/

    }

}
