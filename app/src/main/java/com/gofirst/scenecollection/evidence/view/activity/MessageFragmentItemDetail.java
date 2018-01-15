package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.gofirst.scenecollection.evidence.R;


/**
 * Created by Administrator on 2016/3/21.
 */
public class MessageFragmentItemDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment_item_detail);
    }
}