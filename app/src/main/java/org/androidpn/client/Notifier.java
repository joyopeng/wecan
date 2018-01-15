/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.view.activity.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;


/** 
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private SharedPreferences sharedPrefs;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(String notificationId, String apiKey, String title,
            String message, String uri) {
        Log.d(LOGTAG, "notify()...");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + apiKey);
        Log.d(LOGTAG, "notificationTitle=" + title);
        Log.d(LOGTAG, "notificationMessage=" + message);
        Log.d(LOGTAG, "notificationUri=" + uri);

        if (isNotificationEnabled()) {
            // Show the toast
            if (isNotificationToastEnabled()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            // Notification
            //delete on
            /*Notification notification = new Notification();
            notification.icon = getNotificationIcon();
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = message;*/
            //delete off

            //            Intent intent;
            //            if (uri != null
            //                    && uri.length() > 0
            //                    && (uri.startsWith("http:") || uri.startsWith("https:")
            //                            || uri.startsWith("tel:") || uri.startsWith("geo:"))) {
            //                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            //            } else {
            //                String callbackActivityPackageName = sharedPrefs.getString(
            //                        Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
            //                String callbackActivityClassName = sharedPrefs.getString(
            //                        Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
            //                intent = new Intent().setClassName(callbackActivityPackageName,
            //                        callbackActivityClassName);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //            }

            //Intent intent = new Intent(context,
            //        NotificationDetailsActivity.class);
            Intent intent = new Intent(context,MainActivity.class);
            intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            intent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            intent.putExtra(Constants.NOTIFICATION_TITLE, title);
            intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            intent.putExtra(Constants.NOTIFICATION_URI, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
            Log.i("zhangsh","Notifier save Json id = " + notificationId + ";key = " + apiKey + ";title = " + title  + ";uri = " + uri);
            Log.i("zhangsh","Notifier save Json message = " + message);
            if("case".equals(title)){
                if(message != null && !"".equals(message)) {
                    saveJson(message);
                }
            }
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);


            //zsh add on
            Notification notification = null;//new Notification();
            if(Build.VERSION.SDK_INT >= 16){
                notification = (new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentIntent(contentIntent)
                        .setContentText(message)).build();
            }else if(Build.VERSION.SDK_INT > 11){
                notification = (new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentIntent(contentIntent)
                        .setContentText(message)).getNotification();
            }else{
                notification = new Notification();
            }
            notification.icon = getNotificationIcon();
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = message;
            //zsh add off
            //delete on
            /*notification.setLatestEventInfo(context, title, message,
                    contentIntent);*/
            //delete off
            notificationManager.notify(random.nextInt(), notification);

            //            Intent clickIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLICKED);
            //            clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
            //            clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            //            clickIntent.putExtra(Constants.NOTIFICATION_URI, uri);
            //            //        positiveIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clickIntent, 0);
            //
            //            notification.setLatestEventInfo(context, title, message,
            //                    clickPendingIntent);
            //
            //            Intent clearIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLEARED);
            //            clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            //        negativeIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clearIntent, 0);
            //            notification.deleteIntent = clearPendingIntent;
            //
            //            notificationManager.notify(random.nextInt(), notification);

        } else {
            Log.w(LOGTAG, "Notificaitons disabled.");
        }
    }

    private int getNotificationIcon() {
        return sharedPrefs.getInt(Constants.NOTIFICATION_ICON, 0);
    }

    private boolean isNotificationEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
                true);
    }

    private boolean isNotificationSoundEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
    }

    private boolean isNotificationVibrateEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
    }

    private boolean isNotificationToastEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
    }

    private void saveJson(String json){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(json);
        //CsSceneCases sceneCase = com.alibaba.fastjson.JSON.parseObject(json,CsSceneCases.class);
        CsSceneCases cases = new CsSceneCases();
        cases.setId(object.getString("id"));
        cases.setReceptionNo(object.getString("receptionNo"));
        cases.setCaseNo(object.getString("caseNo"));
        cases.setCaseType(object.getString("caseType"));
        cases.setCaseCategory(object.getString("caseCategory"));
        cases.setCaseName(object.getString("caseName"));
        cases.setCaseLevel(object.getString("caseLevel"));
        cases.setSceneRegionalism(object.getString("sceneRegionalism"));
        cases.setSceneDetail(object.getString("sceneDetail"));
        //cases.setOccurrenceDateFrom(object.getDate("occurrenceDateFrom"));
        //cases.setOccurrenceDateTo(object.getDate("occurrenceDateTo"));
        try {
            cases.setOccurrenceDateFrom(sdf.parse(object.getString("occurrenceDateFrom")));
            cases.setOccurrenceDateTo(sdf.parse(object.getString("occurrenceDateTo")));
            cases.setTransferDate(sdf.parse(object.getString("transferDate")));
        } catch (ParseException e) {
            e.printStackTrace();
            cases.setOccurrenceDateFrom(null);
            cases.setOccurrenceDateTo(null);
            cases.setTransferDate(null);
        }
        cases.setExposureProcess(object.getString("exposureProcess"));
        cases.setVictimInfo(object.getString("victimInfo"));
        cases.setInitServerNo(object.getString("initServerNo"));
        //cases.setTransferDate(object.getDate("transferDate"));
        cases.setCrackedDate(object.getString("crackedDate"));
        cases.setSecrecy(object.getString("secrecy"));
        cases.setDeleteFlag(object.getString("deleteFlag"));
        cases.setReserver1("");
        cases.setReserver2("");
        cases.setReserver3("");
        cases.setReserver4("");
        cases.setReserver5("");
        cases.setReserver6("");
        cases.setReserver7("");
        cases.setReserver8("");
        cases.setTempFa1(object.getString("tempFa1"));
        cases.setTempFa2(object.getString("tempFa2"));
        cases.setGxsk(object.getString("gxsk"));
        cases.setQhname(object.getString("qhname"));
        cases.setIfflag(object.getString("ifflag"));
        cases.setHostId(object.getString("hostId"));
        cases.setHostYear(object.getString("hostYear"));
        cases.setStatus(object.getString("status"));
        cases.setIsReceive(false);
        cases.setReceiveCaseTime("");
        EvidenceApplication.db.save(cases);
    }
}
