package com.gofirst.scenecollection.evidence.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, UpLoadService.class);
        context.startService(i);
    }
}
