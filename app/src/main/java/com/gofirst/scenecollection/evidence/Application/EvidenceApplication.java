package com.gofirst.scenecollection.evidence.Application;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TabHost;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bth.api.cls.Comm_Bluetooth;
import com.function.SPconfig;
import com.gofirst.scenecollection.evidence.model.Area;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.BaseTempField;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsDictsConjunction;
import com.gofirst.scenecollection.evidence.model.CsDictsFavorites;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.FindCaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.FindCsSceneCases;
import com.gofirst.scenecollection.evidence.model.HyEmployees;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SceneEnvironment;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.sync.RemoteService;
import com.gofirst.scenecollection.evidence.sync.UpLoadService;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.silionmodule.Reader;
import com.silionmodule.TagFilter;
import com.silionmodule.TagOp;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gofirst.scenecollection.evidence.Application.PublicMsg.BASEURL_RELEASE;
import static com.gofirst.scenecollection.evidence.Application.PublicMsg.BASEURL_TEST;

/**
 * Created by Administrator on 2016/3/16.
 */
public class EvidenceApplication extends MultiDexApplication implements AMapLocationListener {

    public static boolean SHOW_DOWNLOAD_BASEDATA_DIALOG = false;

    public static FinalDb db;
    AMapLocationClient mLocationClient;
    public static double latitude, longitude;
    public String address;
    private SharePre sharePre;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            PublicMsg.isDebug = appInfo.metaData.getBoolean("isdebug");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        sharePre = new SharePre(getApplicationContext(), "user_info", Context.MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
       /* File deteleFile = new File(Environment.getExternalStorageDirectory().getPath() + "/CMI-CSI");
        if (!sharePre.getString("deteleFile", "").equals("1")) {
            AppPathUtil.deleteAllFiles(deteleFile);
        }*/
        //
        if (PublicMsg.isDebug) {
            PublicMsg.BASEURL = BASEURL_TEST;
        }else {
            PublicMsg.BASEURL = BASEURL_RELEASE;
                    /*CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());*/
            AppExceptionHandler appExceptionHandler = AppExceptionHandler.getInstance();
            appExceptionHandler.init(this);
        }
        //
        String ip = sharePre.getString("ip", "");
        if (!TextUtils.isEmpty(ip)) {
            PublicMsg.BASEURL = ip;
        }
        Netroid.init(this);
        File file = new File(AppPathUtil.getDBPath());
        Log.d("filegetAbsolutePath", AppPathUtil.getDBPath() + "");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.d("evidenceDb", "创建数据库存储路径目录失败");
                return;
            }
        }
        Log.d("filegetAbsolutePath", file.getAbsolutePath());


        //db = FinalDb.create(this, file.getAbsolutePath(), PublicMsg.evidenceDb, false);
        db = FinalDb.create(this, file.getAbsolutePath(), PublicMsg.evidenceDb, false, 110, new FinalDb.DbUpdateListener() {

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                sqLiteDatabase.beginTransaction();
                try {

                    if (oldVersion < 110) {
                        updateDataBaseVer1_2(sqLiteDatabase);
                        updateDataBaseVer100_101(sqLiteDatabase);
                        updateDataBaseVer100_102(sqLiteDatabase);
                        updateDataBaseVer102_104(sqLiteDatabase);
                        updateDataBaseVer104_105(sqLiteDatabase);
                        updateDataBaseVer105_106(sqLiteDatabase);
                        updateDataBaseVer104_107(sqLiteDatabase);
                        updateDataBaseVer104_108(sqLiteDatabase);
                        updateDataBaseVer110(sqLiteDatabase);
                    }


                    sqLiteDatabase.setTransactionSuccessful();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                } finally {
                    sqLiteDatabase.endTransaction();
                }

            }
        });


        //  db=FinalDb.create(this, file.getAbsolutePath(), PublicMsg.evidenceDb, false, 109, null);


        db.findById("1", CsSceneCases.class);
        db.findById("1", CsDicts.class);
        db.findById("1", HyEmployees.class);
        db.findById("1", HyOrganizations.class);
        db.findById("1", RecordFileInfo.class);
        db.findById("1", User.class);
        db.findById("1", SceneEnvironment.class);
        db.findById(1, UnUpLoadBlock.class);
        db.findById(1, UnUploadJson.class);
        db.findById(1, HyEmployees.class);
        db.findById(1, BaseTemp.class);
        db.findById("1", BaseTempField.class);
        db.findById("1", CommonExtField.class);
        db.findById("1", CsDictsConjunction.class);
        db.findById(1, Area.class);
        db.findAll(CommonTemplate.class);
        db.findAll(TemplateSort.class);
        db.findAll(CommonTemplateDetail.class, "id limit 0,1");
        db.findById("1", CsDictsFavorites.class);
        db.findById("1", SysAppParamSetting.class);
        db.findById("1", DataTemp.class);

        db.findById("1", FindCsSceneCases.class);
        db.findById("1", FindCaseBasicInfo.class);

        startService(new Intent(this, UpLoadService.class));
        startService(new Intent(this, RemoteService.class));
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.plan.gofirst.monitordevice",
                "com.plan.gofirst.monitordevice.UpLoadService");
        intent.setComponent(componentName);
        startService(intent);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            boolean changed = false;
            if (aMapLocation.getLatitude() != latitude) {
                latitude = aMapLocation.getLatitude();
                sharePre.put("latitude", latitude + "");
                changed = true;
            }
            if (aMapLocation.getLongitude() != longitude) {
                longitude = aMapLocation.getLongitude();
                sharePre.put("longitude", latitude + "");
                changed = true;
            }
            if (!TextUtils.equals(aMapLocation.getAddress(), address)) {
                address = aMapLocation.getAddress();
                sharePre.put("address", aMapLocation.getAddress());
                changed = true;
                Log.e("jiu", "address = " + address);
            }
            if (changed) {
                sharePre.commit();
            }

        }
    }

    @Override
    public void onTerminate() {
        mLocationClient.onDestroy();
        super.onTerminate();
    }


    public static SQLiteDatabase getSQLiteDatabase(Context context, File file) {
        //  return SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator+DB_NAME,null);
        return SQLiteDatabase.openOrCreateDatabase(file.getAbsolutePath() + "/" + PublicMsg.evidenceDb, null);

    }

    public static boolean hasRows(SQLiteDatabase db, String sql) {
        Cursor cursor = null;
        boolean hasRows = false;
        try {
            cursor = db.rawQuery(sql, null);
            hasRows = cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return hasRows;

    }

    private static void updateDataBaseVer1_2(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name='BaseTempField' ")) {
            try {
                db.execSQL("ALTER TABLE BaseTempField ADD COLUMN viewMinor TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name='CommonExtField' ")) {
            try {
                db.execSQL("ALTER TABLE CommonExtField ADD COLUMN viewMinor TEXT");
            } catch (Exception e) {
            }
        }

    }

    private static void updateDataBaseVer100_101(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name='BaseTemp' ")) {
            try {
                db.execSQL("ALTER TABLE BaseTemp ADD COLUMN templateLevel TEXT");
                db.execSQL("ALTER TABLE BaseTemp ADD COLUMN templateUpName TEXT");
            } catch (Exception e) {
            }
        }
    }

    //增加是否用户新增案件的标记
    private static void updateDataBaseVer100_102(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='CsSceneCases' ")) {
            try {
                db.execSQL("ALTER TABLE CsSceneCases ADD COLUMN isManualAddCase TEXT");
            } catch (Exception e) {
            }
        }
    }

    private static void updateDataBaseVer102_104(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='HyEmployees' ")) {
            try {
                db.execSQL("ALTER TABLE HyEmployees ADD COLUMN deleteFlag TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='UnUploadJson' ")) {
            try {
                db.execSQL("ALTER TABLE UnUploadJson ADD COLUMN userId TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='TemplateSort' ")) {
            try {
                db.execSQL("ALTER TABLE TemplateSort ADD COLUMN date TEXT");
            } catch (Exception e) {
            }
        }

    }


    private static void updateDataBaseVer104_105(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='User' ")) {
            try {
                db.execSQL("ALTER TABLE User ADD COLUMN permissionSetting TEXT");
            } catch (Exception e) {
            }
        }

    }


    private static void updateDataBaseVer104_108(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='DataTemp' ")) {
            try {
                db.execSQL("ALTER TABLE DataTemp ADD COLUMN addRec TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='RecordFileInfo' ")) {
            try {
                db.execSQL("ALTER TABLE RecordFileInfo ADD COLUMN addRec TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='CsSceneCases' ")) {
            try {
                db.execSQL("ALTER TABLE CsSceneCases ADD COLUMN addRec TEXT");
            } catch (Exception e) {
            }
        }
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='UnUploadJson' ")) {
            try {
                db.execSQL("ALTER TABLE UnUploadJson ADD COLUMN addRec TEXT");
                db.execSQL("ALTER TABLE UnUploadJson ADD COLUMN uploaded TEXT");
            } catch (Exception e) {
            }

        }

    }

    private static void updateDataBaseVer104_107(SQLiteDatabase db) {
        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='User' ")) {
            try {
                db.execSQL("ALTER TABLE User ADD COLUMN newUserId TEXT");
            } catch (Exception e) {
            }
        }

        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='RecordFileInfo' ")) {
            try {
                db.execSQL("ALTER TABLE RecordFileInfo ADD COLUMN hasBlock TEXT");
            } catch (Exception e) {
            }
        }
    }

    private static void updateDataBaseVer105_106(SQLiteDatabase db) {

        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='User' ")) {
            try {
                db.execSQL("ALTER TABLE User ADD COLUMN userNameId TEXT");
            } catch (Exception e) {
            }
            //db.execSQL("delete from User where 1=1");
        }
        //db.execSQL("delete from User where 1=1");
    }

    private static void updateDataBaseVer110(SQLiteDatabase db) {

        if (hasRows(db, "SELECT 'x' FROM sqlite_master where type='table' and name ='SceneFileConjunction' ")) {
            try {
                db.execSQL("ALTER TABLE SceneFileConjunction ADD COLUMN height TEXT");
                db.execSQL("ALTER TABLE SceneFileConjunction ADD COLUMN width TEXT");
            } catch (Exception e) {
            }

        }
    }

    public static List<Activity> activitiesCollector = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {

        activitiesCollector.add(activity);
    }

    public static void removeActivity(Activity activity) {

        activitiesCollector.remove(activity);
    }

    public static void finishAllActivity() {
        for (Activity activity : activitiesCollector) {

            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


    public Comm_Bluetooth CommBth;

    public int Mode;
    public Map<String, String> m;

    public Reader Mreader;
    public String Address;
    public SPconfig spf;
    public ReaderParams Rparams;
    public long exittime;
    public TabHost tabHost;
    public boolean isread;
    public String bluepassword;
    public int BackResult;

    public class ReaderParams {
        public int antportc;
        public int sleep;
        public int[] uants;
        public int readtime;
        public String Curepc;
        public int Bank;

        public TagOp To;
        public TagFilter Tf;

        public ReaderParams() {
            sleep = 0;
            readtime = 200;
            uants = new int[1];
            uants[0] = 1;

        }
    }


    public static String Constr_READ = "读";
    public static String Constr_CONNECT = "连接";
    public static String Constr_INVENTORY = "盘点";
    public static String Constr_RWLOP = "读写锁";
    public static String Constr_PASSVICE = "被动设置";
    public static String Constr_ACTIVE = "主动设置";
    public static String Constr_SetFaill = "设置失败：";
    public static String Constr_GetFaill = "获取失败：";
    public static String Constr_SetOk = "设置成功";
    public static String Constr_unsupport = "不支持";
    public static String Constr_Putandexit = "再按一次退出程序";
    public static String[] Coname = new String[]{"序号", "EPC ID", "次数", "天线",
            "协议", "RSSI", "频率", "附加数据 "};
    public static String Constr_stopscan = "请先停止扫描";
    public static String Constr_scanasetconnecto = "请扫描并选中一个蓝牙读写器,并且完成连接";
    public static String Constr_scanselectabluereader = "请扫描并选中一个蓝牙读写器";
    public static String Constr_scanselectabluereaderandconnect = "请扫描并选中一个蓝牙读写器,并且完成连接";
    public static String Constr_hadconnected = "已经连接";
    public static String Constr_plsetuuid = "请设置好UUID:";
    public static String Constr_pwderror = "密码错误";
    public static String Constr_search = "搜索";
    public static String Constr_stop = "停止";
    public static String Constr_plselectsearchblueset = "请选择要搜索的蓝牙设备";
    public static String Constr_startsearchblueok = "开始搜索蓝牙 成功";
    public static String Constr_startsearchbluefail1 = "开始搜索蓝牙:1 失败";
    public static String Constr_startsearchbluefail2 = "开始搜索蓝牙:2 失败";
    public static String Constr_startsearchbluefail12 = "开始搜索蓝牙:1,2 失败，将重启蓝牙，请等待重启完成";
    public static String Constr_canclebluematch = "取消蓝牙设备匹配:";
    public static String Constr_connectbluesetfail = "连接蓝牙设备失败:";
    public static String Constr_matchbluefail = "匹配蓝牙设备失败";
    public static String Constr_pwdmatchfail = "密码匹配失败";
    public static String Constr_connectblueokthentoreader = "连接蓝牙设备成功,将连接读写器";
    public static String Constr_connectblueserfail = "连接蓝牙设备服务失败";
    public static String Constr_connectbluesetok = "连接蓝牙设备成功";
    public static String Constr_createreaderok = "读写器创建失败";
    public String[] pdaatpot = {"一天线", "双天线", "三天线", "四天线"};
    public String[] strconectway = {"被动式", "主动式"};

    public String[] spibank = {"保留区", "EPC区", "TID区", "用户区"};
    public String[] spifbank = {"EPC区", "TID区", "用户区"};
    public String[] spilockbank = {"访问密码", "销毁密码", "EPCbank", "TIDbank", "USERbank"};
    public String[] spilocktype = {"解锁定", "暂时锁定", "永久锁定"};
    public static String Constr_sub3readmem = "读标签";
    public static String Constr_sub3writemem = "写标签";
    public static String Constr_sub3lockkill = "锁与销毁";
    public static String Constr_sub3readfail = "读失败:";
    public static String Constr_sub3nodata = "无数据";
    public static String Constr_sub3wrtieok = "写成功";
    public static String Constr_sub3writefail = "写失败:";
    public static String Constr_sub3lockok = "锁成功";
    public static String Constr_sub3lockfail = "锁失败:";
    public static String Constr_sub3killok = "销毁成功";
    public static String Constr_sub3killfial = "销毁失败:";

    String[] spireg = {"中国", "北美", "日本", "韩国", "欧洲", "印度", "加拿大", "全频段"
            , "中国2"};
    String[] spinvmo = {"普通模式", "高速模式"};
    String[] spitari = {"25微秒", "12.5微秒", "6.25微秒"};
    String[] spiwmod = {"字写", "块写"};
    String Auto = "自动";

    public static String Constr_sub4invenpra = "盘点参数";
    public static String Constr_sub4antpow = "天线功率";
    public static String Constr_sub4regionfre = "区域频率";
    public static String Constr_sub4gen2opt = "Gen2项";
    public static String Constr_sub4invenfil = "盘点过滤";
    public static String Constr_sub4addidata = "附加数据";
    public static String Constr_sub4others = "其他参数";
    public static String Constr_sub4setmodefail = "配置模式失败";
    public static String Constr_sub4hadactivemo = "已经为主动模式";
    public static String Constr_sub4setokresettoab = "设置成功，重启读写器生效";
    public static String Constr_sub4hadpasstivemo = "已经为被动模式";
    public static String Constr_sub4ndsapow = "该设备需要功率一致";
    public static String Constr_sub4unspreg = "不支持的区域";

    String[] spiregbs = {"北美", "中国", "欧频", "中国2"};
    public static String Constr_subblmode = "模式";
    public static String Constr_subblinven = "盘点";
    public static String Constr_subblfil = "过滤";
    public static String Constr_subblfre = "频率";
    public static String Constr_subbl = "蓝牙";
    public static String Constr_subblnofre = "没有选择频点";

    String[] cusreadwrite = {"读操作", "写操作"};
    String[] cuslockunlock = {"锁", "解锁"};

    public static String Constr_subcsalterpwd = "改密码";
    public static String Constr_subcslockwpwd = "带密码锁";
    public static String Constr_subcslockwoutpwd = "不带密码锁";
    public static String Constr_subcsplsetimeou = "请设置超时时间";
    public static String Constr_subcsputcnpwd = "填入当前密码与新密码";
    public static String Constr_subcsplselreg = "请选择区域";
    public static String Constr_subcsopfail = "操作失败:";
    public static String Constr_subcsputcurpwd = "填入当前密码";

    public static String Constr_subdbhaddisconnerecon = "已经断开,正在重新连接";
    public static String Constr_subdbdisconnreconn = "已经断开,正在重新连接";
    public static String Constr_subdbhadconnected = "已经连接";
    public static String Constr_subdbconnecting = "正在连接......";
    public static String Constr_subdbrev = "接收";
    public static String Constr_subdbstop = "停止";
    public static String Constr_subdbdalennot = "数据长度不对";
    public static String Constr_subdbplpuhexchar = "请输入16进制字符";


}
