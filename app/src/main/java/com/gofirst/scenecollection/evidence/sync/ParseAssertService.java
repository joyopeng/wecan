package com.gofirst.scenecollection.evidence.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.AppOfflineMapPackage;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.gofirst.scenecollection.evidence.utils.Utils.BASE_DATA_FILE_PATH;

public class ParseAssertService extends Service {
    private static final String baseDatafile = "basedata";
    private static final String mapFile = "suzhou.dat";
    private SharePre sharePre;
    private String DOWNLOAD_MAP_DATA_PATH = "";

    public ParseAssertService() {
        copyBaseData(this);
        copyMapData(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean suc1 = (boolean) msg.obj;
            if (suc1) {
                sharePre = new SharePre(ParseAssertService.this, "user_info", Context.MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
                Bundle bundle = msg.getData();
                String coredataUpdateTime = bundle.getString("update_time", "");
                if (coredataUpdateTime != null && !"".equals(coredataUpdateTime)) {
                    sharePre.put(Utils.SHARE_SYNC_BASE_DATA_CONDITION, coredataUpdateTime);
                    sharePre.commit();
                    List<User> userList = EvidenceApplication.db.findAllByWhere(User.class, "userId = \"" + sharePre.getString("userId", "") + "\"");
                    if (userList.size() != 0) {
                        User user = userList.get(0);
                        user.setCoredataUpdateTime(coredataUpdateTime);
                        EvidenceApplication.db.update(user);
                    }
                }
            }
        }
    };


    /**
     * 从assert复制基础数据包
     */
    private void copyBaseData(final Context context) {
        new Thread() {
            @Override
            public void run() {
                copyFilesFassets(context, baseDatafile, BASE_DATA_FILE_PATH);
                Utils.ReadTxtFile(context, BASE_DATA_FILE_PATH, myHandler);
            }
        }.start();
    }

    /**
     * 从assert复制基础数据包
     */
    private void copyMapData(final Context context) {
        new Thread() {
            @Override
            public void run() {
                DOWNLOAD_MAP_DATA_PATH = context.getObbDir() + "/amp/data/vmap";
                File dir = new File(DOWNLOAD_MAP_DATA_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                copyFilesFassets(context, "suzhou.dat", DOWNLOAD_MAP_DATA_PATH + "/suzhou.dat");
                Utils.ReadTxtFile(context, DOWNLOAD_MAP_DATA_PATH + "/suzhou.dat", myHandler);
                AppOfflineMapPackage info = new AppOfflineMapPackage();
                info.setAreaId("127");
                info.setMapSpell("suzhou");
                info.setVersionCode("5");
                info.setFileSize("18535614");
                info.setId("fcf15aa1c1bc4d2b84daa486131934c5");
                info.setAreaName("苏州市");
//                info.setCreateDatetime(Calendar.getInstance().s);
                EvidenceApplication.db.save(info);
            }
        }.start();
    }

    private void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            InputStream is = context.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024 * 1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
