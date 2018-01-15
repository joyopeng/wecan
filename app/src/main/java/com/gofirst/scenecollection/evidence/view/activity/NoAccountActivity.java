package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by cocoa on 2017/9/18.
 */

public class NoAccountActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tip_show_dialog);
        TextView titleView = (TextView) findViewById(R.id.tip_dialog_title);
        titleView.setText(R.string.app_name);
        titleView.setVisibility(View.GONE);
        TextView noticeView = (TextView) findViewById(R.id.tip_dialog_notice);
        noticeView.setText("您的账号不能登录微勘程序");
        Button confirm = (Button) findViewById(R.id.tip_dialog_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        confirm.setVisibility(View.VISIBLE);
        Button close = (Button) findViewById(R.id.tip_dialog_close);
        close.setVisibility(View.GONE);
    }
}
