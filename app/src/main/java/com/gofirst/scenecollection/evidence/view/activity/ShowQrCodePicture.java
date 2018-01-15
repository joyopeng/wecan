package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ShowQrCodePicture extends Activity {
    private Bitmap bitmap;

    private ImageView showImageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_qr_code_picture);
        showImageview=(ImageView)findViewById(R.id.show_imageview);

        Intent intent = getIntent();
        if (intent != null) {
            bitmap = intent.getParcelableExtra("bitmap");
            showImageview.setImageBitmap(bitmap);
        }

    }
}

