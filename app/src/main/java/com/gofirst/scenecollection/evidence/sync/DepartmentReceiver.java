package com.gofirst.scenecollection.evidence.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/4/17.
 */
public class DepartmentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("violetjack.testaction")){
            String result = intent.getStringExtra("message");
            Log.e("result", "result = " + result);
        }
    }
}
