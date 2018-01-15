package com.gofirst.scenecollection.evidence.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.Area;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.BaseTempField;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CommonTemplate;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsDictsConjunction;
import com.gofirst.scenecollection.evidence.model.CsDictsFavorites;
import com.gofirst.scenecollection.evidence.model.HyEmployees;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by zsh on 2016/8/10.
 */
public class Utils {

    /**
     * 公共字段定义
     */
    public static final String SHARE_SYNC_BASE_DATA_CONDITION = "sync_data_condition";   ///基础数据增量SharePreference定义字段
    public static final String SHARE_SYNC_CONTACT_CONDITION = "sync_contact_addresslist_time";   ///通讯录数据增量SharePreference定义字段
    public static final String SHARE_SYNC_SCHEDULE_CONDITION = "sync_schedule_time";   ///排班数据增量SharePreference定义字段
    public static final String SHARE_UPLOAD_TILE_SLOT_TIME = "sync_data_auto_upload_time_slot";  ///自动上传开始时间
    public static final String SHARE_AUTO_UPLOAD_SUPPORT = "sync_data_auto_upload_support";   ///是否支持自动上传

    /**********************************************************************************************
     * 公共ProgressDialog
     * ********************************************************************************************
     */
    public static Dialog loadingProgress = null;
    private static TextView mTVProgress = null;

    /**
     * 生成一个dialog并且显示
     *
     * @param context
     * @param title        暂缺省
     * @param notice       显示的信息
     * @param showProgress true:显示进度百分比  false:不显示进度百分比
     * @param canCancel    true:返回键可以退出 false:返回键不能退出只能通过 stopProgressDialog退出
     */
    public static void startProgressDialog(Context context, String title, String notice, boolean showProgress, boolean canCancel) {
        //if (loadingProgress == null) {
        loadingProgress = createLoadingDialog(context, notice, showProgress, canCancel);
        //}
        loadingProgress.show();
    }

    public static JSONObject getDeviceInfoJsonObject() throws JSONException {
        String deviceInfo = readTxtFile(DEVICE_INFO_PATH);
        if (TextUtils.isEmpty(deviceInfo)) {
            saveStringFile("{}", DEVICE_INFO_PATH);
            deviceInfo = "{}";
        }
        return new JSONObject(deviceInfo);
    }

    public static void saveDeviceInfo(JSONObject jsonObject) {
        File file = new File(DEVICE_INFO_PATH);
        if (file.delete())
            saveStringFile(jsonObject.toString(), DEVICE_INFO_PATH);
    }

    public static String DEVICE_INFO_PATH = AppPathUtil.getDBPath() + "/deviceInfo.txt";

    public static String readTxtFile(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public static void saveStringFile(String data, String fileName) {
        File file = new File(fileName);
        Log.d("save",data);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            return;
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            byte[] bytes = data.getBytes();
            out.write(bytes);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出当前dialog
     */
    public static void stopProgressDialog() {
        if (loadingProgress != null) {
            loadingProgress.dismiss();
            loadingProgress = null;
            if (mTVProgress != null) {
                mTVProgress = null;
            }
        }
    }

    public static void updateProgressDialog(Context context, String notice) {
        if (loadingProgress == null) {
            return;
        }
        TextView tipTextView = (TextView) loadingProgress.findViewById(R.id.tipTextView);
        if (tipTextView != null) {
            tipTextView.setText(notice);
        }
    }

    public static void updateDialogProgress(int percent) {
        if (loadingProgress != null && mTVProgress != null && mTVProgress.getVisibility() == View.VISIBLE) {
            //TextView progressTV = (TextView) loadingProgress.findViewById(R.id.tv_dialog_progress);
            if (mTVProgress != null) {
                mTVProgress.setText(percent + "%");
            }
        }
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @param showProgress true:显示进度百分比  false:不显示进度百分比
     * @param canCancel    true:返回键可以退出 false:返回键不能退出只能通过 stopProgressDialog退出
     * @return
     * @see #stopProgressDialog()
     * @see #updateDialogProgress(int)
     */
    public static Dialog createLoadingDialog(Context context, String msg, boolean showProgress, boolean canCancel) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        mTVProgress = (TextView) v.findViewById(R.id.tv_dialog_progress);
        mTVProgress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);

        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        if (canCancel) {
            loadingDialog.setCancelable(true);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
        } else {
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i("zhangsh", "setOnDismissListener onDismiss loadingProgress == null ? " + (loadingProgress == null));
                if (loadingProgress != null) {
                    //loadingProgress = null;
                }
            }
        });
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;
    }

    /**
     * 公共提示Dialog
     *
     * @start
     */
    public static final int MSG_TIP_DIALOG_CONFIRM = 1000001;
    private static Dialog TipDialog = null;

    public static void startTipDialog(Context context, String title, String notice, boolean showConfirmBtn, boolean showCloseBtn, boolean canCancel, final Handler handler) {
        if (TipDialog == null) {
            TipDialog = createTipDialog(context, title, notice, showConfirmBtn, showCloseBtn, canCancel, handler);
        }
        TipDialog.show();
    }

    public static void stopTipDialog() {
        if (TipDialog != null) {
            TipDialog.dismiss();
            TipDialog = null;
        }
    }

    private static Dialog createTipDialog(Context context, String title, String notice, boolean showConfirmBtn, boolean showCloseBtn, boolean canCancel, final Handler handler) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.tip_show_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.tip_dialog_view);

        TextView tipTitleView = (TextView) v.findViewById(R.id.tip_dialog_title);
        tipTitleView.setText(title);
        TextView tipNoticeView = (TextView) v.findViewById(R.id.tip_dialog_notice);
        tipNoticeView.setText(notice);
        if (showCloseBtn) {
            Button close = (Button) v.findViewById(R.id.tip_dialog_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TipDialog != null) {
                        TipDialog.dismiss();
                        TipDialog = null;
                    }
                }
            });
            close.setVisibility(View.VISIBLE);
        }
        if (showConfirmBtn) {
            Button confirm = (Button) v.findViewById(R.id.tip_dialog_confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (handler != null) {
                        handler.removeMessages(MSG_TIP_DIALOG_CONFIRM);
                        handler.sendEmptyMessage(MSG_TIP_DIALOG_CONFIRM);
                    }
                }
            });
            confirm.setVisibility(View.VISIBLE);
        }
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        if (canCancel) {
            loadingDialog.setCancelable(true);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
        } else {
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i("zhangsh", "setOnDismissListener onDismiss TipDialog == null ? " + (TipDialog == null));
                if (TipDialog != null) {
                    //TipDialog = null;
                }
            }
        });
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;
    }

    /**
     * 公共提示Dialog
     *
     * @end
     */

    public static int dp2Px(Context context, int dp) {
        int result = 0;
        float scale = context.getResources().getDisplayMetrics().density;
        result = (int) (dp * scale + 0.5);
        return result;
    }

    public static int sp2Px(Context context, int sp) {
        int result = 0;
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        result = (int) (sp * scale + 0.5f);
        return result;
    }

    /**
     * 将彩色图转换为纯黑白二色
     *
     * @param bmp 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                //分离三原色
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                //转化成灰度像素
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        //Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
        return resizeBmp;
    }

    /**
     * get current "yyyyMM" string
     */
    public static String getCurrentMonthString() {
        String result = "";
        long time = System.currentTimeMillis();
        Date date = new Date(time);

        return result;
    }

    /**
     * get appoint last "yyyy-MM-dd" string
     *
     * @param appoint 前面第几个月
     */
    public static String getAppointMonthString(int appoint) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - appoint);
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (month.length() == 1 ? ("0" + month) : month) + "-" + (day.length() == 1 ? ("0" + day) : day);
        //return String.valueOf(calendar.get(Calendar.YEAR)) + (month.length() == 1 ? ("0" + month) : month);
    }

    /**
     * get appoint last {year,month,day}
     *
     * @param appoint 前面第几个月
     * @return results[2] : day
     */
    public static int[] getAppointDate(int appoint) {
        int[] results = new int[3];
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - appoint);
        results[0] = calendar.get(Calendar.YEAR);
        results[1] = calendar.get(Calendar.MONTH) + 1;
        results[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return results;
    }

    private static SimpleDateFormat UTILS_TRACE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getSpecificTime(long time) {
        Date date = new Date(time);
        return UTILS_TRACE_DATE_FORMAT.format(date);
    }

    /**
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return = 0 : 时间相等; < 0 : 开始时间大; > 0 : 结束时间大
     */
    public static long compareDate(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return diff;
    }

    /**
     * 下载基础数据
     */
    private static SimpleDateFormat UTILS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //public static String BASE_DATA_FILE_PATH = AppPathUtil.getCachePath()+"/"+ OSUtil.getUUid()+".txt";
    public static String BASE_DATA_FILE_PATH = AppPathUtil.getCachePath() + "/" + OSUtil.getUUid() + ".gz";
    //public static String HISTORY_DATA_FILE_PATH = AppPathUtil.getCachePath()+"/"+OSUtil.getUUid()+".zip";
    public static String HISTORY_DATA_FILE_PATH_GZ = AppPathUtil.getCachePath() + "/" + OSUtil.getUUid() + ".gz";
    public static final int MSG_DOWNLOAD_BASE_DATA = 100000;
    public static final int MSG_ANALYSIS_DATA = 100001;

    //    public static final int MSG_DOWNLOAD_BASE_DATA_SUCCESS = 100000;
//    public static final int MSG_DOWNLOAD_BASE_DATA_FAILE = 100001;
//    public static final int MSG_ANALYSIS_DATA_SUCCESS = 100002;
//    public static final int MSG_ANALYSIS_DATA_FAILE = 100003;
    //public static final int MSG_CLEAR_DATA_SUCCESS = 100003;
    //public static final int MSG_CLEAR_DATA_FAILE = 100003;
    public static void downloadBaseData(final Context context, final String url, AjaxParams params, final String dataFilepath, final Handler handler, final boolean isNeedClear) {
        FinalHttp finalHttp = new FinalHttp();
        finalHttp.configTimeout(300000);
        finalHttp.configRequestExecutionRetryCount(0);
        //finalHttp.download(PublicMsg.BASEURL + url/*"/app/basedata_cache?token=" + token*/, dataFilepath,
        finalHttp.download(PublicMsg.BASEURL + url, params, dataFilepath,
                new AjaxCallBack<File>() {
                    @Override
                    public void onStart() {
                        Utils.startProgressDialog(context, "", String.format("开始下载数据..."), false, false);
                        super.onStart();
                    }

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLoading(long count, long current) {
                        super.onLoading(count, current);
                        int progress = 0;
                        if (current != count && current != 0) {
                            progress = (int) (current / (float) count * 100);
                        } else {
                            progress = 100;
                        }
                        Utils.updateProgressDialog(context, String.format("正在下载中...%s%%", progress));
                    }

                    @Override
                    public void onSuccess(File t) {
                        super.onSuccess(t);
                        if (isNeedClear && (t == null || t.length() == 0)) {
                            restartApp(context);
                            return;
                        }
                        Utils.updateProgressDialog(context, "正在解析数据");
                        Thread basedataThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = MSG_DOWNLOAD_BASE_DATA;
                                message.obj = true;
                                handler.sendMessage(message);
                                if (isNeedClear) {
                                    clearBaseData(context, handler);
                                    ReadTxtFile(context, dataFilepath, handler);
                                } else {
                                    //ReadTextFileAndUpdateData(context,dataFilepath,handler);
                                    ReadTextFileAndUpdateDataNew(context, dataFilepath, handler);
                                }
                                Looper.prepare();
                                ToastUtil.show(context, "基础数据下载完成并且解析成功", Toast.LENGTH_LONG);
                                Looper.loop();
                            }
                        });
                        basedataThread.start();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        Message message = new Message();
                        message.what = MSG_DOWNLOAD_BASE_DATA;
                        message.obj = false;
                        handler.sendMessage(message);
                        Utils.stopProgressDialog();
                        ToastUtil.showShort(context, "下载失败!" + errorNo + " " + strMsg);
                    }
                });
    }

    public static SQLiteDatabase getSQLiteDatabase(Context context) {
        File file = new File(AppPathUtil.getDBPath() + "/evidence.db");
        return SQLiteDatabase.openOrCreateDatabase(file.getAbsoluteFile(), null);
    }

    private static final String SQL_DELETE_CSDICTSCONJUNCTION = "DELETE FROM CsDictsConjunction where 1=1";
    private static final String SQL_INSERT_CSDICTSCONJUNCTION = "INSERT INTO CsDictsConjunction(id,dictKeyFrom,parentKeyFrom,rootKeyFrom," +
            "deleteFlag,hostId,hostYear,dictKeyTo,parentKeyTo," +
            "rootKeyTo,createUser,createDatetime,updateUser,updateDatetime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_CSDICTS = "DELETE FROM CsDicts where 1=1";
    private static final String SQL_INSERT_CSDICTS = "INSERT INTO CsDicts(sid,dictLevel,dictKey,rootKey," +
            "dictValue1,dictValue2,dictValue3,leafFlag,downloadFlag," +
            "readonlyFlag,dictSort,dictPy,openFlag,parentKey,remark) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_HYORGAIZATIONS = "DELETE FROM HyOrganizations where 1=1";
    private static final String SQL_INSERT_HYORGAIZATIONS = "INSERT INTO HyOrganizations(organizationId,organizationNo,organizationLogo," +
            "organizationName,organizationCname,organizationUpId,organizationBusiUpId,compartmentNo," +
            "organizationTel,organizationTelWatch,organizationFax,organizationAddr,organizationZip," +
            "organizationUrl,organizationOrderby,organizationType,remark) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_HYEMPLOYEES = "DELETE FROM HyEmployees where 1=1";
    private static final String SQL_INSERT_HYEMPLOYEES = "INSERT INTO HyEmployees(employeeId,employeeNo,employeeName,employeeSex," +
            "employeeBirth,employeeTel,employeeEmail,employeeCredname,employeeCredno," +
            "employeePcIp,employeeOrderby,orgDeptId,organizationId," +
            "remark,employeeStatus,deleteFlag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_COMMONTEMPLATE = "DELETE FROM CommonTemplate where 1=1";
    private static final String SQL_INSERT_COMMONTEMPLATE = "INSERT INTO CommonTemplate(sid,name,key,remark,shareFlag," +
            "orgId,caseTypeCode,dealType) values(?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_TEMPLATE_DETAIL = "DELETE FROM CommonTemplateDetail where 1=1";
    private static final String SQL_INSERT_TEMPLATE_DETAIL = "INSERT INTO CommonTemplateDetail(" +
            "id,templateId,sceneName,tableName,templateType,positionSort,templateLevel,templateUpName) values(?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_COMMON_EXT_FIELD = "DELETE FROM CommonExtField where 1=1";
    private static final String SQL_INSERT_COMMON_EXT_FIELD = "INSERT INTO CommonExtField(id,sceneName,viewName," +
            "viewFormat,hostYear,viewid,remark,maxVerCode,sceneType,viewLineNumber,viewMaxLength,dcitParentCode," +
            "hostId,viewType,dictType,minVerCode,field,dataType,name,viewRequiredFlag,deleteFlag,defaultValue,templateId,positionSort,viewMinor,viewShowMode,viewPositionSort) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_BASETEMP = "DELETE FROM BaseTemp where 1=1";
    private static final String SQL_INSERT__BASETEMP = "INSERT INTO BaseTemp(id,templateId,sceneName,flag," +
            "tableName,templateType,tableField,templateLevel,templateUpName,positionSort) values(?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_BASETEMP_FIELD = "DELETE FROM BaseTempField where 1=1";
    private static final String SQL_INSERT__BASETEMP_FIELD = "INSERT INTO BaseTempField(id,sceneName,viewName," +
            "viewFormat,hostYear,viewid,remark,maxVerCode,sceneType,viewLineNumber,viewMaxLength,dcitParentCode," +
            "hostId,viewType,dictType,minVerCode,field,dataType,name,viewRequiredFlag,deleteFlag,defaultValue,templateId,viewMinor,positionSort,viewShowMode,viewPositionSort) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_SCENE_FILE_CONJUNCTION = "DELETE FROM SceneFileConjunction where 1=1";
    private static final String SQL_INSERT__SCENE_FILE_CONJUNCTION = "INSERT INTO SceneFileConjunction(investigationId,attachmentId," +
            "childAttachmentId,deleteFlag,createUser,createDatetime,updateUser,updateDatetime,hostId,hostYear,x," +
            "y,type,remark,section) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_AREA = "DELETE FROM Area where 1=1";
    private static final String SQL_INSERT_AREA = "INSERT INTO Area(compartmentId,compartmentNo,compartmentName," +
            "compartmentUpNo,compartmentLevel,compartmentType,deleteFlag,createUser," +
            "createDatetime,updateUser,updateDatetime) values(?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_CSDICTSFAVORITES = "DELETE FROM CsDictsFavorites where 1=1";
    private static final String SQL_INSERT_CSDICTSFAVORITES = "INSERT INTO CsDictsFavorites(id,dictsId,rootKey,userId,orgId,deleteFlag,createUser,createDatetime,updateUser," +
            "updateDatetime,hostId,hostYear) values(?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_DELETE_SYSAPPPARAMSETTING = "DELETE FROM SysAppParamSetting WHERE 1=1";
    private static final String SQL_INSERT_SYSAPPPARAMSETTING = "INSERT INTO SysAppParamSetting(id,key,name,value,value2,remark,orgId,deleteFlag," +
            "createUser,createDatetime,updateUser,updateDatetime,hostId,hostYear,orgIdStr) " +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    //读取文本文件中的内容
    public static String ReadTxtFile(Context ctx, String strFilePath, final Handler handler) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        String updateTime = "";
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
            Message message = new Message();
            message.what = MSG_ANALYSIS_DATA;
            message.obj = false;
            handler.sendMessage(message);
        } else {
            try {
                //InputStream instream = new FileInputStream(file);
                InputStream instream = new GZIPInputStream(new FileInputStream(file));
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    SQLiteDatabase sQLiteDatabase;
                    sQLiteDatabase = getSQLiteDatabase(ctx);
                    sQLiteDatabase.beginTransaction();

                    int count = 0;

                    HyEmployees hyEmployees;
                    HyOrganizations hyOrganizations;
                    CsDicts csDicts;
                    Area area;
                    BaseTemp baseTemp;
                    BaseTempField baseTempField;
                    CommonTemplate commonTemplate;
                    CommonTemplateDetail commonTemplateDetail;
                    CommonExtField commonExtField;
                    CsDictsConjunction csDictsConjunction;
                    CsDictsFavorites csDictsFavorites;
                    SysAppParamSetting sysAppParamSetting;
                    String tittleName;
                    String context;

                    while ((line = buffreader.readLine()) != null) {
                        //content += line + "\n";
                        // Log.d("line", "" + line);
                        line = line.substring(1);
                        tittleName = line.substring(0, line.indexOf("#"));
                        content = line.substring(line.indexOf("#") + 1);
                        //String[] arr=line.split("#");

                        if (tittleName.equals("Total")) {
                            continue;
                        }
                        //get update time
                        int index = line.indexOf("UpdateTime##");
                        if (index != -1) {
                            updateTime = line.substring(line.lastIndexOf("#") + 1);
                        }

                        if (tittleName.equals("HyEmployees")) {
                            count++;
                            hyEmployees = new HyEmployees();
                            hyEmployees = JSON.parseObject(content, HyEmployees.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_HYEMPLOYEES,
                                    new Object[]{
                                            hyEmployees.getEmployeeId(),
                                            hyEmployees.getEmployeeNo(),//
                                            hyEmployees.getEmployeeName(),//
                                            hyEmployees.getEmployeeSex(),//
                                            hyEmployees.getEmployeeBirth(),//
                                            hyEmployees.getEmployeeTel(),//
                                            hyEmployees.getEmployeeEmail(),//
                                            hyEmployees.getEmployeeCredname(),//
                                            hyEmployees.getEmployeeCredno(),//
                                            hyEmployees.getEmployeePcIp(),//
                                            hyEmployees.getEmployeeOrderby(),//
                                            hyEmployees.getOrgDeptId(),
                                            hyEmployees.getOrganizationId(),//
                                            hyEmployees.getRemark(),//
                                            hyEmployees.getEmployeeStatus(),
                                            hyEmployees.getDeleteFlag()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time1", str);
                            }
                        } else if (tittleName.equals("HyOrganizations")) {
                            count++;
                            hyOrganizations = new HyOrganizations();
                            hyOrganizations = JSON.parseObject(content, HyOrganizations.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_HYORGAIZATIONS,
                                    new Object[]{
                                            hyOrganizations.getOrganizationId(),
                                            hyOrganizations.getOrganizationNo(),
                                            hyOrganizations.getOrganizationLogo(),
                                            hyOrganizations.getOrganizationName(),
                                            hyOrganizations.getOrganizationCname(),
                                            hyOrganizations.getOrganizationUpId(),
                                            hyOrganizations.getOrganizationBusiUpId(),
                                            hyOrganizations.getCompartmentNo(),
                                            hyOrganizations.getOrganizationTel(),
                                            hyOrganizations.getOrganizationTelWatch(),
                                            hyOrganizations.getOrganizationFax(),
                                            hyOrganizations.getOrganizationAddr(),
                                            hyOrganizations.getOrganizationZip(),
                                            hyOrganizations.getOrganizationUrl(),
                                            hyOrganizations.getOrganizationOrderby(),
                                            hyOrganizations.getOrganizationType(),
                                            hyOrganizations.getRemark()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("tim2", str);
                            }
                        } else if (tittleName.equals("CsDicts")) {
                            count++;
                            csDicts = new CsDicts();
                            csDicts = JSON.parseObject(content, CsDicts.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_CSDICTS,
                                    new Object[]{
                                            csDicts.getId(),
                                            csDicts.getDictLevel(),
                                            csDicts.getDictKey(),//字典代码
                                            // dictJsonObjectdata.getString("parentKey"),//parentKey
                                            csDicts.getRootKey(),//rootKey
                                            csDicts.getDictValue1(),//字典值1
                                            csDicts.getDictValue2(),//字典值2
                                            csDicts.getDictValue3(),//字典值3
                                            csDicts.getLeafFlag(),//叶子节点标志(0非叶节点，1叶子节点)
                                            csDicts.getDownloadFlag(),//下载标志(0非下载，1下载的字典)
                                            csDicts.getReadonlyFlag(),//只读标志(0非只读，1只读)
                                            csDicts.getDictSort(),//显示顺序
                                            csDicts.getDictPy(),//字典PY输入的编码(提供拼音的字典输入方式用)
                                            csDicts.getOpenFlag(),//启用标志
                                            csDicts.getParentKey(),
                                            csDicts.getRemark()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time3", str);
                            }
                        } else if (tittleName.equals("HyCompartments")) {//area
                            count++;
                            area = new Area();
                            area = JSON.parseObject(content, Area.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_AREA,
                                    new Object[]{area.getCompartmentId(),
                                            area.getCompartmentNo(),
                                            area.getCompartmentName(),
                                            area.getCompartmentUpNo(),
                                            area.getCompartmentLevel(),
                                            area.getCompartmentType(),
                                            area.getDeleteFlag(),
                                            area.getCreateUser(),
                                            area.getCreateDatetime(),
                                            area.getUpdateUser(),
                                            area.getUpdateDatetime()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time4", str);
                            }
                        } else if (tittleName.equals("BaseTemp")) {//BaseTemp
                            count++;
                            baseTemp = new BaseTemp();
                            baseTemp = JSON.parseObject(content, BaseTemp.class);
                            sQLiteDatabase.execSQL(SQL_INSERT__BASETEMP,
                                    new Object[]{
                                            baseTemp.getId(),
                                            baseTemp.getTemplateId(),
                                            baseTemp.getSceneName(),//
                                            baseTemp.getFlag(),//
                                            baseTemp.getTableName(),
                                            baseTemp.getTemplateType(),
                                            baseTemp.getTableField(),
                                            baseTemp.getTemplateLevel(),
                                            baseTemp.getTemplateUpName(),
                                            baseTemp.getPositionSort()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time5", str);
                            }
                        } else if (tittleName.equals("BaseTempField")) {//BaseTempField
                            count++;
                            baseTempField = new BaseTempField();
                            baseTempField = JSON.parseObject(content, BaseTempField.class);
                            sQLiteDatabase.execSQL(SQL_INSERT__BASETEMP_FIELD, new Object[]{
                                    baseTempField.getId(),
                                    baseTempField.getSceneName(),
                                    baseTempField.getViewName(),
                                    baseTempField.getViewFormat(),
                                    baseTempField.getHostYear(),
                                    baseTempField.getViewid(),
                                    baseTempField.getRemark(),
                                    baseTempField.getMaxVerCode(),
                                    baseTempField.getSceneType(),
                                    baseTempField.getViewLineNumber(),
                                    baseTempField.getViewMaxLength(),
                                    baseTempField.getDcitParentCode(),
                                    baseTempField.getHostId(),
                                    baseTempField.getViewType(),
                                    baseTempField.getDictType(),
                                    baseTempField.getMinVerCode(),
                                    baseTempField.getField(),
                                    baseTempField.getDataType(),
                                    baseTempField.getName(),
                                    baseTempField.getViewRequiredFlag(),
                                    baseTempField.getDeleteFlag(),
                                    baseTempField.getDefaultValue(),
                                    baseTempField.getTemplateId(),
                                    baseTempField.getViewMinor(),
                                    baseTempField.getPositionSort(),
                                    baseTempField.getViewShowMode(),
                                    baseTempField.getViewPositionSort()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time6", str);
                            }
                        } else if (tittleName.equals("CommonTemplate")) {//CommonTemplate
                            count++;
                            commonTemplate = new CommonTemplate();
                            commonTemplate = JSON.parseObject(content, CommonTemplate.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_COMMONTEMPLATE,
                                    new Object[]{
                                            commonTemplate.getId(),
                                            commonTemplate.getName(),
                                            commonTemplate.getKey(),//
                                            commonTemplate.getRemark(),//
                                            commonTemplate.getShareFlag(),//
                                            commonTemplate.getOrgId(),//
                                            commonTemplate.getCaseTypeCode(),//
                                            commonTemplate.getDealType()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time7", str);
                            }
                        } else if (tittleName.equals("CommonTemplateDetail")) {//CommonTemplateDetail
                            count++;
                            commonTemplateDetail = new CommonTemplateDetail();
                            commonTemplateDetail = JSON.parseObject(content, CommonTemplateDetail.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_TEMPLATE_DETAIL,
                                    new Object[]{
                                            commonTemplateDetail.getId(),
                                            commonTemplateDetail.getTemplateId(),
                                            commonTemplateDetail.getSceneName(),
                                            commonTemplateDetail.getTableName(),
                                            commonTemplateDetail.getTemplateType(),
                                            commonTemplateDetail.getPositionSort(),
                                            commonTemplateDetail.getTemplateLevel(),
                                            commonTemplateDetail.getTemplateUpName()
                                    });
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time8", str);
                            }
                        } else if (tittleName.equals("CommonExtFields")) {//CommonTemplateDetail
                            count++;
                            commonExtField = new CommonExtField();
                            commonExtField = JSON.parseObject(content, CommonExtField.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_COMMON_EXT_FIELD, new Object[]{
                                    commonExtField.getId(),
                                    commonExtField.getSceneName(),
                                    commonExtField.getViewName(),
                                    commonExtField.getViewFormat(),
                                    commonExtField.getHostYear(),
                                    commonExtField.getViewid(),
                                    commonExtField.getRemark(),
                                    commonExtField.getMaxVerCode(),
                                    commonExtField.getSceneType(),
                                    commonExtField.getViewLineNumber(),
                                    commonExtField.getViewMaxLength(),
                                    commonExtField.getDcitParentCode(),
                                    commonExtField.getHostId(),
                                    commonExtField.getViewType(),
                                    commonExtField.getDictType(),
                                    commonExtField.getMinVerCode(),
                                    commonExtField.getField(),
                                    commonExtField.getDataType(),
                                    commonExtField.getName(),
                                    commonExtField.getViewRequiredFlag(),
                                    commonExtField.getDeleteFlag(),
                                    commonExtField.getDefaultValue(),
                                    commonExtField.getTemplateId(),
                                    commonExtField.getPositionSort(),
                                    commonExtField.getViewMinor(),
                                    commonExtField.getViewShowMode(),
                                    commonExtField.getViewPositionSort()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time9", str);
                            }
                        } else if (tittleName.equals("CsDictsConjunction")) {// CsDictsConjunction
                            count++;
                            csDictsConjunction = new CsDictsConjunction();
                            csDictsConjunction = JSON.parseObject(content, CsDictsConjunction.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_CSDICTSCONJUNCTION, new Object[]{
                                    csDictsConjunction.getId(),
                                    csDictsConjunction.getDictKeyFrom(),
                                    csDictsConjunction.getParentKeyFrom(),
                                    csDictsConjunction.getRootKeyFrom(),
                                    csDictsConjunction.getDeleteFlag(),
                                    csDictsConjunction.getHostId(),
                                    csDictsConjunction.getHostYear(),
                                    csDictsConjunction.getDictKeyTo(),
                                    csDictsConjunction.getParentKeyTo(),
                                    csDictsConjunction.getRootKeyTo(),
                                    csDictsConjunction.getCreateUser(),
                                    csDictsConjunction.getCreateDatetime(),
                                    csDictsConjunction.getUpdateUser(),
                                    csDictsConjunction.getUpdateDatetime()});
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time10", str);
                            }
                        } else if (tittleName.equals("CsDictsFavorites")) {//CommonTemplateDetail
                            count++;
                            csDictsFavorites = new CsDictsFavorites();
                            csDictsFavorites = JSON.parseObject(content, CsDictsFavorites.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_CSDICTSFAVORITES,
                                    new Object[]{
                                            csDictsFavorites.getId(),
                                            csDictsFavorites.getDictsId(),
                                            csDictsFavorites.getRootKey(),
                                            csDictsFavorites.getUserId(),
                                            csDictsFavorites.getOrgId(),
                                            csDictsFavorites.getDeleteFlag(),
                                            csDictsFavorites.getCreateUser(),
                                            csDictsFavorites.getCreateDatetime(),
                                            csDictsFavorites.getUpdateUser(),
                                            csDictsFavorites.getUpdateDatetime(),
                                            csDictsFavorites.getHostId(),
                                            csDictsFavorites.getHostYear()
                                    });
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                String str = formatter.format(new Date(System.currentTimeMillis()));
                                Log.d("time8", str);
                            }
                        } else if ("SysAppParamSetting".equals(tittleName)) {
                            count++;
                            sysAppParamSetting = new SysAppParamSetting();
                            sysAppParamSetting = JSON.parseObject(content, SysAppParamSetting.class);
                            sQLiteDatabase.execSQL(SQL_INSERT_SYSAPPPARAMSETTING, new Object[]{
                                    sysAppParamSetting.getId(),
                                    sysAppParamSetting.getKey(),
                                    sysAppParamSetting.getName(),
                                    sysAppParamSetting.getValue(),
                                    sysAppParamSetting.getValue2(),
                                    sysAppParamSetting.getRemark(),
                                    sysAppParamSetting.getOrgId(),
                                    sysAppParamSetting.getDeleteFlag(),
                                    sysAppParamSetting.getCreateUser(),
                                    sysAppParamSetting.getCreateDatetime(),
                                    sysAppParamSetting.getUpdateUser(),
                                    sysAppParamSetting.getUpdateDatetime(),
                                    sysAppParamSetting.getHostId(),
                                    sysAppParamSetting.getHostYear(),
                                    sysAppParamSetting.getOrgIdStr()
                            });
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        }
                    }
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                    instream.close();
                    sQLiteDatabase.close();
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = true;
                    Bundle bundle = new Bundle();
                    if (updateTime != null && !"".equals(updateTime)) {
                        bundle.putString("update_time", updateTime);
                        message.setData(bundle);
                    }
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = false;
                    handler.sendMessage(message);
                }

            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            }
            //Utils.stopProgressDialog();
        }
        return content;
    }


    public static String ReadTextFileAndUpdateData(Context ctx, String strFilePath, final Handler handler) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        String updateTime = "";
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
            Message message = new Message();
            message.what = MSG_ANALYSIS_DATA;
            message.obj = false;
            handler.sendMessage(message);
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    SQLiteDatabase sQLiteDatabase;
                    sQLiteDatabase = getSQLiteDatabase(ctx);

                    int count = 0;
                    HyEmployees hyEmployees;
                    HyOrganizations hyOrganizations;
                    CsDicts csDicts;
                    Area area;
                    BaseTemp baseTemp;
                    BaseTempField baseTempField;
                    CommonTemplate commonTemplate;
                    CommonTemplateDetail commonTemplateDetail;
                    CommonExtField commonExtField;
                    CsDictsConjunction csDictsConjunction;
                    CsDictsFavorites csDictsFavorites;
                    String tittleName;
                    String context;
                    Cursor cursor = null;
                    while ((line = buffreader.readLine()) != null) {
                        line = line.substring(1);
                        tittleName = line.substring(0, line.indexOf("#"));
                        content = line.substring(line.indexOf("#") + 1);
                        if (tittleName.equals("Total")) {
                            continue;
                        }
                        //get update time
                        int index = line.indexOf("UpdateTime##");
                        if (index != -1) {
                            updateTime = line.substring(line.lastIndexOf("#") + 1);
                        }
                        ContentValues values = null;
                        if (tittleName.equals("HyEmployees")) {
                            hyEmployees = new HyEmployees();
                            hyEmployees = JSON.parseObject(content, HyEmployees.class);
                            cursor = sQLiteDatabase.query("HyEmployees", null, "employeeId=?", new String[]{hyEmployees.getEmployeeId().toString()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {

                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("employeeNo", hyEmployees.getEmployeeNo());
                                    values.put("employeeName", hyEmployees.getEmployeeName());
                                    values.put("employeeSex", hyEmployees.getEmployeeSex());
                                    values.put("employeeBirth", hyEmployees.getEmployeeBirth());
                                    values.put("employeeTel", hyEmployees.getEmployeeTel());
                                    values.put("employeeEmail", hyEmployees.getEmployeeEmail());
                                    values.put("employeeCredname", hyEmployees.getEmployeeCredname());
                                    values.put("employeeCredno", hyEmployees.getEmployeeCredno());
                                    values.put("employeePcIp", hyEmployees.getEmployeePcIp());
                                    values.put("employeeOrderby", hyEmployees.getEmployeeOrderby());
                                    values.put("orgDeptId", hyEmployees.getOrgDeptId());
                                    values.put("organizationId", hyEmployees.getOrganizationId());
                                    values.put("remark", hyEmployees.getRemark());
                                    values.put("employeeStatus", hyEmployees.getEmployeeStatus());
                                    values.put("deleteFlag", hyEmployees.getDeleteFlag());
                                    sQLiteDatabase.update("HyEmployees", values, "employeeId=?", new String[]{hyEmployees.getEmployeeId().toString()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_HYEMPLOYEES,
                                        new Object[]{
                                                hyEmployees.getEmployeeId(),
                                                hyEmployees.getEmployeeNo(),//
                                                hyEmployees.getEmployeeName(),//
                                                hyEmployees.getEmployeeSex(),//
                                                hyEmployees.getEmployeeBirth(),//
                                                hyEmployees.getEmployeeTel(),//
                                                hyEmployees.getEmployeeEmail(),//
                                                hyEmployees.getEmployeeCredname(),//
                                                hyEmployees.getEmployeeCredno(),//
                                                hyEmployees.getEmployeePcIp(),//
                                                hyEmployees.getEmployeeOrderby(),//
                                                hyEmployees.getOrgDeptId(),
                                                hyEmployees.getOrganizationId(),//
                                                hyEmployees.getRemark(),//
                                                hyEmployees.getEmployeeStatus(),
                                                hyEmployees.getDeleteFlag()});
                            }
                        } else if (tittleName.equals("HyOrganizations")) {
                            hyOrganizations = new HyOrganizations();
                            hyOrganizations = JSON.parseObject(content, HyOrganizations.class);
                            cursor = sQLiteDatabase.query("HyOrganizations", null, "organizationId=?", new String[]{hyOrganizations.getOrganizationId().toString()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("organizationNo", hyOrganizations.getOrganizationNo());
                                    values.put("organizationLogo", hyOrganizations.getOrganizationLogo());
                                    values.put("organizationName", hyOrganizations.getOrganizationName());
                                    values.put("organizationCname", hyOrganizations.getOrganizationCname());
                                    values.put("organizationUpId", hyOrganizations.getOrganizationUpId());
                                    values.put("organizationBusiUpId", hyOrganizations.getOrganizationBusiUpId());
                                    values.put("compartmentNo", hyOrganizations.getCompartmentNo());
                                    values.put("organizationTel", hyOrganizations.getOrganizationTel());
                                    values.put("organizationTelWatch", hyOrganizations.getOrganizationTelWatch());
                                    values.put("organizationFax", hyOrganizations.getOrganizationFax());
                                    values.put("organizationAddr", hyOrganizations.getOrganizationAddr());
                                    values.put("organizationZip", hyOrganizations.getOrganizationZip());
                                    values.put("organizationUrl", hyOrganizations.getOrganizationUrl());
                                    values.put("organizationOrderby", hyOrganizations.getOrganizationOrderby());
                                    values.put("organizationType", hyOrganizations.getOrganizationType());
                                    values.put("remark", hyOrganizations.getRemark());
                                    sQLiteDatabase.update("HyOrganizations", values, "organizationId=?", new String[]{hyOrganizations.getOrganizationId().toString()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_HYORGAIZATIONS,
                                        new Object[]{
                                                hyOrganizations.getOrganizationId(),
                                                hyOrganizations.getOrganizationNo(),
                                                hyOrganizations.getOrganizationLogo(),
                                                hyOrganizations.getOrganizationName(),
                                                hyOrganizations.getOrganizationCname(),
                                                hyOrganizations.getOrganizationUpId(),
                                                hyOrganizations.getOrganizationBusiUpId(),
                                                hyOrganizations.getCompartmentNo(),
                                                hyOrganizations.getOrganizationTel(),
                                                hyOrganizations.getOrganizationTelWatch(),
                                                hyOrganizations.getOrganizationFax(),
                                                hyOrganizations.getOrganizationAddr(),
                                                hyOrganizations.getOrganizationZip(),
                                                hyOrganizations.getOrganizationUrl(),
                                                hyOrganizations.getOrganizationOrderby(),
                                                hyOrganizations.getOrganizationType(),
                                                hyOrganizations.getRemark()});
                            }
                        } else if (tittleName.equals("CsDicts")) {
                            csDicts = new CsDicts();
                            csDicts = JSON.parseObject(content, CsDicts.class);
                            cursor = sQLiteDatabase.query("CsDicts", null, "sid=?", new String[]{csDicts.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("dictLevel", csDicts.getDictLevel());
                                    values.put("dictKey", csDicts.getDictKey());
                                    values.put("rootKey", csDicts.getRootKey());
                                    values.put("dictValue1", csDicts.getDictValue1());
                                    values.put("dictValue2", csDicts.getDictValue2());
                                    values.put("dictValue3", csDicts.getDictValue3());
                                    values.put("leafFlag", csDicts.getLeafFlag());
                                    values.put("downloadFlag", csDicts.getDownloadFlag());
                                    values.put("readonlyFlag", csDicts.getReadonlyFlag());
                                    values.put("dictSort", csDicts.getDictSort());
                                    values.put("dictPy", csDicts.getDictPy());
                                    values.put("openFlag", csDicts.getOpenFlag());
                                    values.put("parentKey", csDicts.getParentKey());
                                    values.put("remark", csDicts.getRemark());
                                    sQLiteDatabase.update("CsDicts", values, "sid=?", new String[]{csDicts.getId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_CSDICTS,
                                        new Object[]{
                                                csDicts.getId(),
                                                csDicts.getDictLevel(),
                                                csDicts.getDictKey(),//字典代码
                                                // dictJsonObjectdata.getString("parentKey"),//parentKey
                                                csDicts.getRootKey(),//rootKey
                                                csDicts.getDictValue1(),//字典值1
                                                csDicts.getDictValue2(),//字典值2
                                                csDicts.getDictValue3(),//字典值3
                                                csDicts.getLeafFlag(),//叶子节点标志(0非叶节点，1叶子节点)
                                                csDicts.getDownloadFlag(),//下载标志(0非下载，1下载的字典)
                                                csDicts.getReadonlyFlag(),//只读标志(0非只读，1只读)
                                                csDicts.getDictSort(),//显示顺序
                                                csDicts.getDictPy(),//字典PY输入的编码(提供拼音的字典输入方式用)
                                                csDicts.getOpenFlag(),//启用标志
                                                csDicts.getParentKey(),
                                                csDicts.getRemark()});
                            }
                        } else if (tittleName.equals("HyCompartments")) {//area
                            area = new Area();
                            area = JSON.parseObject(content, Area.class);
                            cursor = sQLiteDatabase.query("Area", null, "compartmentId=?", new String[]{area.getCompartmentId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("compartmentNo", area.getCompartmentNo());
                                    values.put("compartmentName", area.getCompartmentName());
                                    values.put("compartmentUpNo", area.getCompartmentUpNo());
                                    values.put("compartmentLevel", area.getCompartmentLevel());
                                    values.put("compartmentType", area.getCompartmentType());
                                    values.put("deleteFlag", area.getDeleteFlag());
                                    values.put("createUser", area.getCreateUser());
                                    values.put("createDatetime", area.getCreateDatetime());
                                    values.put("updateUser", area.getUpdateUser());
                                    values.put("updateDatetime", area.getUpdateDatetime());
                                    sQLiteDatabase.update("Area", values, "compartmentId=?", new String[]{area.getCompartmentId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_AREA,
                                        new Object[]{area.getCompartmentId(),
                                                area.getCompartmentNo(),
                                                area.getCompartmentName(),
                                                area.getCompartmentUpNo(),
                                                area.getCompartmentLevel(),
                                                area.getCompartmentType(),
                                                area.getDeleteFlag(),
                                                area.getCreateUser(),
                                                area.getCreateDatetime(),
                                                area.getUpdateUser(),
                                                area.getUpdateDatetime()});
                            }
                        } else if (tittleName.equals("BaseTemp")) {//BaseTemp
                            baseTemp = new BaseTemp();
                            baseTemp = JSON.parseObject(content, BaseTemp.class);
                            cursor = sQLiteDatabase.query("BaseTemp", null, "id=?", new String[]{baseTemp.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("sceneName", baseTemp.getSceneName());
                                    values.put("flag", baseTemp.getFlag());
                                    values.put("tableName", baseTemp.getTableName());
                                    values.put("templateType", baseTemp.getTemplateType());
                                    values.put("tableField", baseTemp.getTableField());
                                    values.put("templateLevel", baseTemp.getTemplateLevel());
                                    values.put("templateUpName", baseTemp.getTemplateUpName());
                                    values.put("positionSort", baseTemp.getPositionSort());
                                    sQLiteDatabase.update("BaseTemp", values, "id=?", new String[]{baseTemp.getId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT__BASETEMP,
                                        new Object[]{
                                                baseTemp.getId(),
                                                baseTemp.getTemplateId(),
                                                baseTemp.getSceneName(),//
                                                baseTemp.getFlag(),//
                                                baseTemp.getTableName(),
                                                baseTemp.getTemplateType(),
                                                baseTemp.getTableField(),
                                                baseTemp.getTemplateLevel(),
                                                baseTemp.getTemplateUpName(),
                                                baseTemp.getPositionSort()
                                        });
                            }
                        } else if (tittleName.equals("BaseTempField")) {//BaseTempField
                            baseTempField = new BaseTempField();
                            baseTempField = JSON.parseObject(content, BaseTempField.class);
                            cursor = sQLiteDatabase.query("BaseTempField", null, "id=?", new String[]{baseTempField.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("sceneName", baseTempField.getSceneName());
                                    values.put("viewName", baseTempField.getViewName());
                                    values.put("viewFormat", baseTempField.getViewFormat());
                                    values.put("hostYear", baseTempField.getHostYear());
                                    values.put("viewid", baseTempField.getViewid());
                                    values.put("remark", baseTempField.getRemark());
                                    values.put("maxVerCode", baseTempField.getMaxVerCode());
                                    values.put("sceneType", baseTempField.getSceneType());
                                    values.put("viewLineNumber", baseTempField.getViewLineNumber());
                                    values.put("viewMaxLength", baseTempField.getViewMaxLength());
                                    values.put("dcitParentCode", baseTempField.getDcitParentCode());
                                    values.put("hostId", baseTempField.getHostId());
                                    values.put("viewType", baseTempField.getViewType());
                                    values.put("dictType", baseTempField.getDictType());
                                    values.put("minVerCode", baseTempField.getMinVerCode());
                                    values.put("field", baseTempField.getField());
                                    values.put("dataType", baseTempField.getDataType());
                                    values.put("name", baseTempField.getName());
                                    values.put("viewRequiredFlag", baseTempField.getViewRequiredFlag());
                                    values.put("deleteFlag", baseTempField.getDeleteFlag());
                                    values.put("defaultValue", baseTempField.getDefaultValue());
                                    values.put("viewMinor", baseTempField.getViewMinor());
                                    values.put("positionSort", baseTempField.getPositionSort());
                                    values.put("viewShowMode", baseTempField.getViewShowMode());
                                    values.put("viewPositionSort", baseTempField.getViewPositionSort());
                                    sQLiteDatabase.update("BaseTempField", values, "id=?", new String[]{baseTempField.getId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT__BASETEMP_FIELD, new Object[]{
                                        baseTempField.getId(),
                                        baseTempField.getSceneName(),
                                        baseTempField.getViewName(),
                                        baseTempField.getViewFormat(),
                                        baseTempField.getHostYear(),
                                        baseTempField.getViewid(),
                                        baseTempField.getRemark(),
                                        baseTempField.getMaxVerCode(),
                                        baseTempField.getSceneType(),
                                        baseTempField.getViewLineNumber(),
                                        baseTempField.getViewMaxLength(),
                                        baseTempField.getDcitParentCode(),
                                        baseTempField.getHostId(),
                                        baseTempField.getViewType(),
                                        baseTempField.getDictType(),
                                        baseTempField.getMinVerCode(),
                                        baseTempField.getField(),
                                        baseTempField.getDataType(),
                                        baseTempField.getName(),
                                        baseTempField.getViewRequiredFlag(),
                                        baseTempField.getDeleteFlag(),
                                        baseTempField.getDefaultValue(),
                                        baseTempField.getViewMinor(),
                                        baseTempField.getPositionSort(),
                                        baseTempField.getViewShowMode(),
                                        baseTempField.getViewPositionSort()});
                            }
                        } else if (tittleName.equals("CommonTemplate")) {//CommonTemplate
                            commonTemplate = new CommonTemplate();
                            commonTemplate = JSON.parseObject(content, CommonTemplate.class);
                            cursor = sQLiteDatabase.query("CommonTemplate", null, "sid=?", new String[]{commonTemplate.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("name", commonTemplate.getName());
                                    values.put("key", commonTemplate.getKey());
                                    values.put("remark", commonTemplate.getRemark());
                                    values.put("shareFlag", commonTemplate.getShareFlag());
                                    values.put("orgId", commonTemplate.getOrgId());
                                    values.put("caseTypeCode", commonTemplate.getCaseTypeCode());
                                    values.put("dealType", commonTemplate.getDealType());
                                    sQLiteDatabase.update("CommonTemplate", values, "sid=?", new String[]{commonTemplate.getId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_COMMONTEMPLATE,
                                        new Object[]{
                                                commonTemplate.getId(),
                                                commonTemplate.getName(),
                                                commonTemplate.getKey(),//
                                                commonTemplate.getRemark(),//
                                                commonTemplate.getShareFlag(),//
                                                commonTemplate.getOrgId(),//
                                                commonTemplate.getCaseTypeCode(),//
                                                commonTemplate.getDealType()});
                            }
                        } else if (tittleName.equals("CommonTemplateDetail")) {//CommonTemplateDetail
                            commonTemplateDetail = new CommonTemplateDetail();
                            commonTemplateDetail = JSON.parseObject(content, CommonTemplateDetail.class);
                            cursor = sQLiteDatabase.query("CommonTemplateDetail", null, "id=?", new String[]{commonTemplateDetail.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                values = new ContentValues();
                                values.put("templateId", commonTemplateDetail.getTemplateId());
                                values.put("sceneName", commonTemplateDetail.getSceneName());
                                values.put("tableName", commonTemplateDetail.getTableName());
                                values.put("templateType", commonTemplateDetail.getTemplateType());
                                values.put("positionSort", commonTemplateDetail.getPositionSort());
                                values.put("templateLevel", commonTemplateDetail.getTemplateLevel());
                                values.put("templateUpName", commonTemplateDetail.getTemplateUpName());
                                sQLiteDatabase.update("CommonTemplateDetail", values, "id=?", new String[]{commonTemplateDetail.getId()});
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_TEMPLATE_DETAIL,
                                        new Object[]{
                                                commonTemplateDetail.getId(),
                                                commonTemplateDetail.getTemplateId(),
                                                commonTemplateDetail.getSceneName(),
                                                commonTemplateDetail.getTableName(),
                                                commonTemplateDetail.getTemplateType(),
                                                commonTemplateDetail.getPositionSort(),
                                                commonTemplateDetail.getTemplateLevel(),
                                                commonTemplateDetail.getTemplateUpName()
                                        });
                            }
                        } else if (tittleName.equals("CommonExtFields")) {//CommonTemplateDetail
                            commonExtField = new CommonExtField();
                            commonExtField = JSON.parseObject(content, CommonExtField.class);
                            cursor = sQLiteDatabase.query("CommonExtField", null, "id=?", new String[]{commonExtField.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                values = new ContentValues();
                                values.put("sceneName", commonExtField.getSceneName());
                                values.put("viewName", commonExtField.getViewName());
                                values.put("viewFormat", commonExtField.getViewFormat());
                                values.put("hostYear", commonExtField.getHostYear());
                                values.put("viewid", commonExtField.getViewid());
                                values.put("remark", commonExtField.getRemark());
                                values.put("maxVerCode", commonExtField.getMaxVerCode());
                                values.put("sceneType", commonExtField.getSceneType());
                                values.put("viewLineNumber", commonExtField.getViewLineNumber());
                                values.put("viewMaxLength", commonExtField.getViewMaxLength());
                                values.put("dcitParentCode", commonExtField.getDcitParentCode());
                                values.put("hostId", commonExtField.getHostId());
                                values.put("viewType", commonExtField.getViewType());
                                values.put("dictType", commonExtField.getDictType());
                                values.put("minVerCode", commonExtField.getMinVerCode());
                                values.put("field", commonExtField.getField());
                                values.put("dataType", commonExtField.getDataType());
                                values.put("name", commonExtField.getName());
                                values.put("viewRequiredFlag", commonExtField.getViewRequiredFlag());
                                values.put("deleteFlag", commonExtField.getDeleteFlag());
                                values.put("defaultValue", commonExtField.getDefaultValue());
                                values.put("templateId", commonExtField.getTemplateId());
                                values.put("positionSort", commonExtField.getPositionSort());
                                values.put("viewShowMode", commonExtField.getViewShowMode());
                                values.put("viewPositionSort", commonExtField.getViewPositionSort());
                                sQLiteDatabase.update("CommonExtField", values, "id=?", new String[]{commonExtField.getId()});
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_COMMON_EXT_FIELD, new Object[]{
                                        commonExtField.getId(),
                                        commonExtField.getSceneName(),
                                        commonExtField.getViewName(),
                                        commonExtField.getViewFormat(),
                                        commonExtField.getHostYear(),
                                        commonExtField.getViewid(),
                                        commonExtField.getRemark(),
                                        commonExtField.getMaxVerCode(),
                                        commonExtField.getSceneType(),
                                        commonExtField.getViewLineNumber(),
                                        commonExtField.getViewMaxLength(),
                                        commonExtField.getDcitParentCode(),
                                        commonExtField.getHostId(),
                                        commonExtField.getViewType(),
                                        commonExtField.getDictType(),
                                        commonExtField.getMinVerCode(),
                                        commonExtField.getField(),
                                        commonExtField.getDataType(),
                                        commonExtField.getName(),
                                        commonExtField.getViewRequiredFlag(),
                                        commonExtField.getDeleteFlag(),
                                        commonExtField.getDefaultValue(),
                                        commonExtField.getTemplateId(),
                                        commonExtField.getPositionSort(),
                                        commonExtField.getViewShowMode(),
                                        commonExtField.getViewPositionSort()});
                            }
                        } else if (tittleName.equals("CsDictsConjunction")) {// CsDictsConjunction
                            csDictsConjunction = new CsDictsConjunction();
                            csDictsConjunction = JSON.parseObject(content, CsDictsConjunction.class);
                            cursor = sQLiteDatabase.query("CsDictsConjunction", null, "id=?", new String[]{csDictsConjunction.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                values = new ContentValues();
                                values.put("dictKeyFrom", csDictsConjunction.getDictKeyFrom());
                                values.put("parentKeyFrom", csDictsConjunction.getParentKeyFrom());
                                values.put("rootKeyFrom", csDictsConjunction.getRootKeyFrom());
                                values.put("deleteFlag", csDictsConjunction.getDeleteFlag());
                                values.put("hostId", csDictsConjunction.getHostId());
                                values.put("hostYear", csDictsConjunction.getHostYear());
                                values.put("dictKeyTo", csDictsConjunction.getDictKeyTo());
                                values.put("parentKeyTo", csDictsConjunction.getParentKeyTo());
                                values.put("rootKeyTo", csDictsConjunction.getRootKeyTo());
                                values.put("createUser", csDictsConjunction.getCreateUser());
                                values.put("createDatetime", csDictsConjunction.getCreateDatetime());
                                values.put("updateUser", csDictsConjunction.getUpdateUser());
                                values.put("updateDatetime", csDictsConjunction.getUpdateDatetime());
                                sQLiteDatabase.update("CsDictsConjunction", values, "id=?", new String[]{csDictsConjunction.getId()});
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_CSDICTSCONJUNCTION, new Object[]{
                                        csDictsConjunction.getId(),
                                        csDictsConjunction.getDictKeyFrom(),
                                        csDictsConjunction.getParentKeyFrom(),
                                        csDictsConjunction.getRootKeyFrom(),
                                        csDictsConjunction.getDeleteFlag(),
                                        csDictsConjunction.getHostId(),
                                        csDictsConjunction.getHostYear(),
                                        csDictsConjunction.getDictKeyTo(),
                                        csDictsConjunction.getParentKeyTo(),
                                        csDictsConjunction.getRootKeyTo(),
                                        csDictsConjunction.getCreateUser(),
                                        csDictsConjunction.getCreateDatetime(),
                                        csDictsConjunction.getUpdateUser(),
                                        csDictsConjunction.getUpdateDatetime()});
                            }
                        } else if (tittleName.equals("CsDictsFavorites")) {//CommonTemplateDetail
                            count++;
                            csDictsFavorites = new CsDictsFavorites();
                            csDictsFavorites = JSON.parseObject(content, CsDictsFavorites.class);
                            cursor = sQLiteDatabase.query("CsDictsFavorites", null, "id=?", new String[]{csDictsFavorites.getId()}, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    values = new ContentValues();
                                    values.put("dictsId", csDictsFavorites.getDictsId());
                                    values.put("rootKey", csDictsFavorites.getRootKey());
                                    values.put("userId", csDictsFavorites.getUserId());
                                    values.put("orgId", csDictsFavorites.getOrgId());
                                    values.put("deleteFlag", csDictsFavorites.getDeleteFlag());
                                    values.put("createUser", csDictsFavorites.getCreateUser());
                                    Date date = csDictsFavorites.getCreateDatetime();
                                    if (date != null) {
                                        values.put("createDatetime", UTILS_DATE_FORMAT.format(date));
                                    } else {
                                        values.putNull("createDatetime");
                                    }
                                    values.put("updateUser", csDictsFavorites.getUpdateUser());
                                    date = csDictsFavorites.getUpdateDatetime();
                                    if (date != null) {
                                        values.put("updateDatetime", UTILS_DATE_FORMAT.format(date));
                                    } else {
                                        values.putNull("updateDatetime");
                                    }
                                    values.put("hostId", csDictsFavorites.getHostId());
                                    values.put("hostYear", csDictsFavorites.getHostYear());
                                    sQLiteDatabase.update("CsDictsFavorites", values, "id=?", new String[]{csDictsFavorites.getId()});
                                }
                            } else {
                                sQLiteDatabase.execSQL(SQL_INSERT_CSDICTSFAVORITES,
                                        new Object[]{
                                                csDictsFavorites.getId(),
                                                csDictsFavorites.getDictsId(),
                                                csDictsFavorites.getRootKey(),
                                                csDictsFavorites.getUserId(),
                                                csDictsFavorites.getOrgId(),
                                                csDictsFavorites.getDeleteFlag(),
                                                csDictsFavorites.getCreateUser(),
                                                csDictsFavorites.getCreateDatetime(),
                                                csDictsFavorites.getUpdateUser(),
                                                csDictsFavorites.getUpdateDatetime(),
                                                csDictsFavorites.getHostId(),
                                                csDictsFavorites.getHostYear()
                                        });
                            }
                        }
                    }
                    instream.close();
                    sQLiteDatabase.close();
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = true;
                    Bundle bundle = new Bundle();
                    if (updateTime != null && !"".equals(updateTime)) {
                        bundle.putString("update_time", updateTime);
                        message.setData(bundle);
                    }
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = false;
                    handler.sendMessage(message);
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            }
        }
        return content;
    }

    public static String ReadTextFileAndUpdateDataNew(Context ctx, String strFilePath, final Handler handler) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        String updateTime = "";
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
            Message message = new Message();
            message.what = MSG_ANALYSIS_DATA;
            message.obj = false;
            handler.sendMessage(message);
        } else {
            try {
                //InputStream instream = new FileInputStream(file);
                InputStream instream = new GZIPInputStream(new FileInputStream(file));
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    SQLiteDatabase sQLiteDatabase;
                    sQLiteDatabase = getSQLiteDatabase(ctx);
                    sQLiteDatabase.beginTransaction();

                    int count = 0;
                    HyEmployees hyEmployees;
                    HyOrganizations hyOrganizations;
                    CsDicts csDicts;
                    Area area;
                    BaseTemp baseTemp;
                    BaseTempField baseTempField;
                    CommonTemplate commonTemplate;
                    CommonTemplateDetail commonTemplateDetail;
                    CommonExtField commonExtField;
                    CsDictsConjunction csDictsConjunction;
                    CsDictsFavorites csDictsFavorites;
                    SysAppParamSetting sysAppParamSetting;
                    String tittleName;
                    String context;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    while ((line = buffreader.readLine()) != null) {
                        line = line.substring(1);
                        tittleName = line.substring(0, line.indexOf("#"));
                        content = line.substring(line.indexOf("#") + 1);
                        if (tittleName.equals("Total")) {
                            continue;
                        }
                        //get update time
                        int index = line.indexOf("UpdateTime##");
                        if (index != -1) {
                            updateTime = line.substring(line.lastIndexOf("#") + 1);
                        }
                        //ContentValues values = null;
                        if (tittleName.equals("HyEmployees")) {
                            count++;
                            hyEmployees = new HyEmployees();
                            hyEmployees = JSON.parseObject(content, HyEmployees.class);
                            ContentValues values = new ContentValues();
                            values.put("employeeId", hyEmployees.getEmployeeId());
                            values.put("employeeNo", hyEmployees.getEmployeeNo());
                            values.put("employeeName", hyEmployees.getEmployeeName());
                            values.put("employeeSex", hyEmployees.getEmployeeSex());
                            values.put("employeeBirth", hyEmployees.getEmployeeBirth());
                            values.put("employeeTel", hyEmployees.getEmployeeTel());
                            values.put("employeeEmail", hyEmployees.getEmployeeEmail());
                            values.put("employeeCredname", hyEmployees.getEmployeeCredname());
                            values.put("employeeCredno", hyEmployees.getEmployeeCredno());
                            values.put("employeePcIp", hyEmployees.getEmployeePcIp());
                            values.put("employeeOrderby", hyEmployees.getEmployeeOrderby());
                            values.put("orgDeptId", hyEmployees.getOrgDeptId());
                            values.put("organizationId", hyEmployees.getOrganizationId());
                            values.put("remark", hyEmployees.getRemark());
                            values.put("employeeStatus", hyEmployees.getEmployeeStatus());
                            sQLiteDatabase.replace("HyEmployees", "employeeId", values);
                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }

                        } else if (tittleName.equals("HyOrganizations")) {
                            count++;
                            hyOrganizations = new HyOrganizations();
                            hyOrganizations = JSON.parseObject(content, HyOrganizations.class);
                            ContentValues values = new ContentValues();
                            values.put("organizationId", hyOrganizations.getOrganizationId());
                            values.put("organizationNo", hyOrganizations.getOrganizationNo());
                            values.put("organizationLogo", hyOrganizations.getOrganizationLogo());
                            values.put("organizationName", hyOrganizations.getOrganizationName());
                            values.put("organizationCname", hyOrganizations.getOrganizationCname());
                            values.put("organizationUpId", hyOrganizations.getOrganizationUpId());
                            values.put("organizationBusiUpId", hyOrganizations.getOrganizationBusiUpId());
                            values.put("compartmentNo", hyOrganizations.getCompartmentNo());
                            values.put("organizationTel", hyOrganizations.getOrganizationTel());
                            values.put("organizationTelWatch", hyOrganizations.getOrganizationTelWatch());
                            values.put("organizationFax", hyOrganizations.getOrganizationFax());
                            values.put("organizationAddr", hyOrganizations.getOrganizationAddr());
                            values.put("organizationZip", hyOrganizations.getOrganizationZip());
                            values.put("organizationUrl", hyOrganizations.getOrganizationUrl());
                            values.put("organizationOrderby", hyOrganizations.getOrganizationOrderby());
                            values.put("organizationType", hyOrganizations.getOrganizationType());
                            values.put("remark", hyOrganizations.getRemark());
                            sQLiteDatabase.replace("HyOrganizations", "organizationId", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CsDicts")) {
                            count++;
                            csDicts = new CsDicts();
                            csDicts = JSON.parseObject(content, CsDicts.class);
                            ContentValues values = new ContentValues();
                            values.put("sid", csDicts.getId());
                            values.put("dictLevel", csDicts.getDictLevel());
                            values.put("dictKey", csDicts.getDictKey());
                            values.put("rootKey", csDicts.getRootKey());
                            values.put("dictValue1", csDicts.getDictValue1());
                            values.put("dictValue2", csDicts.getDictValue2());
                            values.put("dictValue3", csDicts.getDictValue3());
                            values.put("leafFlag", csDicts.getLeafFlag());
                            values.put("downloadFlag", csDicts.getDownloadFlag());
                            values.put("readonlyFlag", csDicts.getReadonlyFlag());
                            values.put("dictSort", csDicts.getDictSort());
                            values.put("dictPy", csDicts.getDictPy());
                            values.put("openFlag", csDicts.getOpenFlag());
                            values.put("parentKey", csDicts.getParentKey());
                            values.put("remark", csDicts.getRemark());
                            sQLiteDatabase.replace("CsDicts", "sid", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("HyCompartments")) {//area
                            count++;
                            area = new Area();
                            area = JSON.parseObject(content, Area.class);
                            ContentValues values = new ContentValues();
                            values.put("compartmentId", area.getCompartmentId());
                            values.put("compartmentNo", area.getCompartmentNo());
                            values.put("compartmentName", area.getCompartmentName());
                            values.put("compartmentUpNo", area.getCompartmentUpNo());
                            values.put("compartmentLevel", area.getCompartmentLevel());
                            values.put("compartmentType", area.getCompartmentType());
                            values.put("deleteFlag", area.getDeleteFlag());
                            values.put("createUser", area.getCreateUser());
                            values.put("createDatetime", area.getCreateDatetime());
                            values.put("updateUser", area.getUpdateUser());
                            values.put("updateDatetime", area.getUpdateDatetime());
                            sQLiteDatabase.replace("Area", "compartmentId", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("BaseTemp")) {//BaseTemp
                            count++;
                            baseTemp = new BaseTemp();
                            baseTemp = JSON.parseObject(content, BaseTemp.class);
                            ContentValues values = new ContentValues();
                            values.put("id", baseTemp.getId());
                            values.put("sceneName", baseTemp.getSceneName());
                            values.put("flag", baseTemp.getFlag());
                            values.put("tableName", baseTemp.getTableName());
                            values.put("templateType", baseTemp.getTemplateType());
                            values.put("tableField", baseTemp.getTableField());
                            values.put("templateLevel", baseTemp.getTemplateLevel());
                            values.put("templateUpName", baseTemp.getTemplateUpName());
                            values.put("positionSort", baseTemp.getPositionSort());
                            sQLiteDatabase.replace("BaseTemp", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("BaseTempField")) {//BaseTempField
                            count++;
                            baseTempField = new BaseTempField();
                            baseTempField = JSON.parseObject(content, BaseTempField.class);
                            ContentValues values = new ContentValues();
                            values.put("id", baseTempField.getId());
                            values.put("sceneName", baseTempField.getSceneName());
                            values.put("viewName", baseTempField.getViewName());
                            values.put("viewFormat", baseTempField.getViewFormat());
                            values.put("hostYear", baseTempField.getHostYear());
                            values.put("viewid", baseTempField.getViewid());
                            values.put("remark", baseTempField.getRemark());
                            values.put("maxVerCode", baseTempField.getMaxVerCode());
                            values.put("sceneType", baseTempField.getSceneType());
                            values.put("viewLineNumber", baseTempField.getViewLineNumber());
                            values.put("viewMaxLength", baseTempField.getViewMaxLength());
                            values.put("dcitParentCode", baseTempField.getDcitParentCode());
                            values.put("hostId", baseTempField.getHostId());
                            values.put("viewType", baseTempField.getViewType());
                            values.put("dictType", baseTempField.getDictType());
                            values.put("minVerCode", baseTempField.getMinVerCode());
                            values.put("field", baseTempField.getField());
                            values.put("dataType", baseTempField.getDataType());
                            values.put("name", baseTempField.getName());
                            values.put("viewRequiredFlag", baseTempField.getViewRequiredFlag());
                            values.put("deleteFlag", baseTempField.getDeleteFlag());
                            values.put("defaultValue", baseTempField.getDefaultValue());
                            values.put("positionSort", baseTempField.getPositionSort());
                            values.put("viewShowMode", baseTempField.getViewShowMode());
                            values.put("viewPositionSort", baseTempField.getViewPositionSort());
                            sQLiteDatabase.replace("BaseTempField", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CommonTemplate")) {//CommonTemplate
                            count++;
                            commonTemplate = new CommonTemplate();
                            commonTemplate = JSON.parseObject(content, CommonTemplate.class);
                            ContentValues values = new ContentValues();
                            values.put("sid", commonTemplate.getId());
                            values.put("name", commonTemplate.getName());
                            values.put("key", commonTemplate.getKey());
                            values.put("remark", commonTemplate.getRemark());
                            values.put("shareFlag", commonTemplate.getShareFlag());
                            values.put("orgId", commonTemplate.getOrgId());
                            values.put("caseTypeCode", commonTemplate.getCaseTypeCode());
                            values.put("dealType", commonTemplate.getDealType());
                            sQLiteDatabase.replace("CommonTemplate", "sid", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CommonTemplateDetail")) {//CommonTemplateDetail
                            count++;
                            commonTemplateDetail = new CommonTemplateDetail();
                            commonTemplateDetail = JSON.parseObject(content, CommonTemplateDetail.class);
                            ContentValues values = new ContentValues();
                            values.put("id", commonTemplateDetail.getId());
                            values.put("templateId", commonTemplateDetail.getTemplateId());
                            values.put("sceneName", commonTemplateDetail.getSceneName());
                            values.put("tableName", commonTemplateDetail.getTableName());
                            values.put("templateType", commonTemplateDetail.getTemplateType());
                            values.put("positionSort", commonTemplateDetail.getPositionSort());
                            values.put("templateLevel", commonTemplateDetail.getTemplateLevel());
                            values.put("templateUpName", commonTemplateDetail.getTemplateUpName());
                            sQLiteDatabase.replace("CommonTemplateDetail", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CommonExtFields")) {//CommonTemplateDetail
                            count++;
                            commonExtField = new CommonExtField();
                            commonExtField = JSON.parseObject(content, CommonExtField.class);
                            ContentValues values = new ContentValues();
                            values.put("id", commonExtField.getId());
                            values.put("sceneName", commonExtField.getSceneName());
                            values.put("viewName", commonExtField.getViewName());
                            values.put("viewFormat", commonExtField.getViewFormat());
                            values.put("hostYear", commonExtField.getHostYear());
                            values.put("viewid", commonExtField.getViewid());
                            values.put("remark", commonExtField.getRemark());
                            values.put("maxVerCode", commonExtField.getMaxVerCode());
                            values.put("sceneType", commonExtField.getSceneType());
                            values.put("viewLineNumber", commonExtField.getViewLineNumber());
                            values.put("viewMaxLength", commonExtField.getViewMaxLength());
                            values.put("dcitParentCode", commonExtField.getDcitParentCode());
                            values.put("hostId", commonExtField.getHostId());
                            values.put("viewType", commonExtField.getViewType());
                            values.put("dictType", commonExtField.getDictType());
                            values.put("minVerCode", commonExtField.getMinVerCode());
                            values.put("field", commonExtField.getField());
                            values.put("dataType", commonExtField.getDataType());
                            values.put("name", commonExtField.getName());
                            values.put("viewRequiredFlag", commonExtField.getViewRequiredFlag());
                            values.put("deleteFlag", commonExtField.getDeleteFlag());
                            values.put("defaultValue", commonExtField.getDefaultValue());
                            values.put("templateId", commonExtField.getTemplateId());
                            values.put("positionSort", commonExtField.getPositionSort());
                            values.put("viewShowMode", commonExtField.getViewShowMode());
                            values.put("viewPositionSort", commonExtField.getViewPositionSort());
                            sQLiteDatabase.replace("CommonExtField", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CsDictsConjunction")) {// CsDictsConjunction
                            count++;
                            csDictsConjunction = new CsDictsConjunction();
                            csDictsConjunction = JSON.parseObject(content, CsDictsConjunction.class);
                            ContentValues values = new ContentValues();
                            values.put("id", csDictsConjunction.getId());
                            values.put("dictKeyFrom", csDictsConjunction.getDictKeyFrom());
                            values.put("parentKeyFrom", csDictsConjunction.getParentKeyFrom());
                            values.put("rootKeyFrom", csDictsConjunction.getRootKeyFrom());
                            values.put("deleteFlag", csDictsConjunction.getDeleteFlag());
                            values.put("hostId", csDictsConjunction.getHostId());
                            values.put("hostYear", csDictsConjunction.getHostYear());
                            values.put("dictKeyTo", csDictsConjunction.getDictKeyTo());
                            values.put("parentKeyTo", csDictsConjunction.getParentKeyTo());
                            values.put("rootKeyTo", csDictsConjunction.getRootKeyTo());
                            values.put("createUser", csDictsConjunction.getCreateUser());
                            values.put("createDatetime", csDictsConjunction.getCreateDatetime());
                            values.put("updateUser", csDictsConjunction.getUpdateUser());
                            values.put("updateDatetime", csDictsConjunction.getUpdateDatetime());
                            sQLiteDatabase.replace("CsDictsConjunction", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if (tittleName.equals("CsDictsFavorites")) {//CommonTemplateDetail
                            count++;
                            csDictsFavorites = new CsDictsFavorites();
                            csDictsFavorites = JSON.parseObject(content, CsDictsFavorites.class);
                            ContentValues values = new ContentValues();
                            values.put("id", csDictsFavorites.getId());
                            values.put("dictsId", csDictsFavorites.getDictsId());
                            values.put("rootKey", csDictsFavorites.getRootKey());
                            values.put("userId", csDictsFavorites.getUserId());
                            values.put("orgId", csDictsFavorites.getOrgId());
                            values.put("deleteFlag", csDictsFavorites.getDeleteFlag());
                            values.put("createUser", csDictsFavorites.getCreateUser());
                            sQLiteDatabase.replace("CsDictsFavorites", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        } else if ("SysAppParamSetting".equals(tittleName)) {
                            count++;
                            sysAppParamSetting = new SysAppParamSetting();
                            sysAppParamSetting = JSON.parseObject(content, SysAppParamSetting.class);
                            ContentValues values = new ContentValues();
                            values.put("id", sysAppParamSetting.getId());
                            values.put("key", sysAppParamSetting.getKey());
                            values.put("name", sysAppParamSetting.getName());
                            values.put("value", sysAppParamSetting.getValue());
                            values.put("value2", sysAppParamSetting.getValue2());
                            values.put("remark", sysAppParamSetting.getRemark());
                            values.put("orgId", sysAppParamSetting.getOrgId());
                            values.put("deleteFlag", sysAppParamSetting.getDeleteFlag());
                            values.put("createUser", sysAppParamSetting.getCreateUser());
                            if (sysAppParamSetting.getCreateDatetime() != null) {
                                values.put("createDatetime", simpleDateFormat.format(sysAppParamSetting.getCreateDatetime()));
                            } else {
                                values.put("createDatetime", "");
                            }
                            values.put("updateUser", sysAppParamSetting.getUpdateUser());
                            if (sysAppParamSetting.getUpdateDatetime() != null) {
                                values.put("updateDatetime", simpleDateFormat.format(sysAppParamSetting.getUpdateDatetime()));
                            } else {
                                values.put("updateDatetime", "");
                            }
                            values.put("hostId", sysAppParamSetting.getHostId());
                            values.put("hostYear", sysAppParamSetting.getHostYear());
                            values.put("orgIdStr", sysAppParamSetting.getOrgIdStr());
                            sQLiteDatabase.replace("SysAppParamSetting", "id", values);

                            if (count % 2000 == 0) {
                                sQLiteDatabase.setTransactionSuccessful();
                                sQLiteDatabase.endTransaction();
                                sQLiteDatabase.beginTransaction();
                            }
                        }
                    }
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();

                    instream.close();
                    sQLiteDatabase.close();
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = true;
                    Bundle bundle = new Bundle();
                    if (updateTime != null && !"".equals(updateTime)) {
                        bundle.putString("update_time", updateTime);
                        message.setData(bundle);
                    }
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = MSG_ANALYSIS_DATA;
                    message.obj = false;
                    handler.sendMessage(message);
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            } catch (IOException ex) {
                Log.d("TestFile", "ex.getMessage()", ex);
                Message message = new Message();
                message.what = MSG_ANALYSIS_DATA;
                message.obj = false;
                handler.sendMessage(message);
            }
        }
        return content;
    }

    public static void clearBaseData(Context ctx, final Handler handler) {
        SQLiteDatabase sQLiteDatabase;
        sQLiteDatabase = getSQLiteDatabase(ctx);
        sQLiteDatabase.beginTransaction();
        sQLiteDatabase.execSQL(SQL_DELETE_CSDICTSCONJUNCTION);
        sQLiteDatabase.execSQL(SQL_DELETE_CSDICTS);
        sQLiteDatabase.execSQL(SQL_DELETE_HYORGAIZATIONS);
        sQLiteDatabase.execSQL(SQL_DELETE_HYEMPLOYEES);
        sQLiteDatabase.execSQL(SQL_DELETE_COMMONTEMPLATE);
        sQLiteDatabase.execSQL(SQL_DELETE_TEMPLATE_DETAIL);
        sQLiteDatabase.execSQL(SQL_DELETE_COMMON_EXT_FIELD);
        sQLiteDatabase.execSQL(SQL_DELETE_BASETEMP);
        sQLiteDatabase.execSQL(SQL_DELETE_BASETEMP_FIELD);
        sQLiteDatabase.execSQL(SQL_DELETE_AREA);
        sQLiteDatabase.execSQL(SQL_DELETE_CSDICTSFAVORITES);
        sQLiteDatabase.execSQL(SQL_DELETE_SYSAPPPARAMSETTING);
        sQLiteDatabase.setTransactionSuccessful();
        sQLiteDatabase.endTransaction();
        sQLiteDatabase.close();
    }

    /**
     * 动态生成历史记录表
     */
    public static void createHistoryTable(Context ctx, String tableName) {
        SQLiteDatabase sQLiteDatabase;
        sQLiteDatabase = getSQLiteDatabase(ctx);
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(caseID TEXT PRIMARY KEY," +
                "caseNo TEXT,caseName TEXT,investigator TEXT,investigatorIds TEXT,receivedDate Date,investigationDateFrom Date," +
                "investigationDateTo Date,occurrenceDateFrom Date,occurrenceDateTo Date,sceneDetail TEXT," +
                "caseType TEXT,caseTypeName TEXT,caseCategory TEXT,sceneRegionalism TEXT,sceneRegionalismName TEXT,exposureProcess TEXT," +
                "sceneInvestigationJson TEXT,sceneLawCase TEXT,sceneReceptionDispatch TEXT)");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS HistoryConfigDetail (id TEXT PRIMARY KEY,historyReception TEXT,historyInvestigation TEXT,historyLowCase TEXT)");
        sQLiteDatabase.close();
    }

    /*
    * 下载历史记录并进行存储
    * */
    public static void downloadHistory(final Context context, final String url, final AjaxParams params, final String dataFilepath, final Handler handler, final String tableName) {
        createHistoryTable(context, tableName);
        FinalHttp finalHttp = new FinalHttp();
        finalHttp.configTimeout(300000);
        finalHttp.configRequestExecutionRetryCount(0);
        finalHttp.download(PublicMsg.BASEURL + url, params, dataFilepath,
                new AjaxCallBack<File>() {
                    @Override
                    public void onStart() {
                        Utils.startProgressDialog(context, "", String.format("开始下载历史数据..."), false, false);
                        super.onStart();
                    }

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLoading(long count, long current) {
                        super.onLoading(count, current);
                        int progress = 0;
                        if (current != count && current != 0) {
                            progress = (int) (current / (float) count * 100);
                        } else {
                            progress = 100;
                        }
                        Utils.updateProgressDialog(context, String.format("正在下载中...%s%%", progress));
                    }

                    @Override
                    public void onSuccess(File t) {
                        super.onSuccess(t);
                        Utils.updateProgressDialog(context, "正在解析数据");
                        Thread historyThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = MSG_DOWNLOAD_BASE_DATA;
                                message.obj = true;
                                handler.sendMessage(message);
                                saveHistory(context, dataFilepath, handler, tableName);
                            }
                        });
                        historyThread.start();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        Message message = new Message();
                        message.what = MSG_DOWNLOAD_BASE_DATA;
                        message.obj = false;
                        handler.sendMessage(message);
                        Utils.stopProgressDialog();
                        ToastUtil.showShort(context, "下载失败!" + errorNo + " " + strMsg);
                    }
                });
    }

    private static void saveHistory(Context ctx, String strFilePath, final Handler handler, final String tableName) {
        File file = new File(strFilePath);
        List<File> files = null;
        try {
            //files = unZipFileAndSave(file, AppPathUtil.getCachePath(),tableName);
            files = unGZFileAndSave(ctx, file, AppPathUtil.getCachePath(), tableName);
        } catch (ZipException zipEx) {
            Log.i("zhangsh", "ZipException zipEx", zipEx);
        } catch (IOException e) {
            Log.i("zhangsh", "IOException e", e);
        } catch (JSONException jsonEx) {
            Log.i("zhangsh", "JSONException jsonEx", jsonEx);
        }
        Utils.stopProgressDialog();
    }

    public static ArrayList<File> unGZFileAndSave(Context context, File gzFile, String folderPath, final String tableName) throws IOException, JSONException {
        ArrayList<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!gzFile.exists() || gzFile.length() == 0) {
            return fileList;
        }
        //String descFilePath = folderPath + "20160526595156-163-text.txt";
        //descFilePath = new String(descFilePath.getBytes("8859_1"),"GB2312");
        //File descFile = new File(descFilePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzFile))));
        //OutputStream outputStream = new FileOutputStream(descFile);
        String s;
        int count = 0;
        SQLiteDatabase sQLiteDatabase;
        sQLiteDatabase = getSQLiteDatabase(context);
        sQLiteDatabase.beginTransaction();
        String lastMonthDate = getAppointMonthString(1);
        sQLiteDatabase.delete(tableName, "receivedDate < " + lastMonthDate, null);
        while ((s = reader.readLine()) != null) {
            count++;
            Log.i("zhangsh", "unGZFileAndSave s = " + s);
            JSONObject object = new JSONObject(s);
            ContentValues values = new ContentValues();
            if (count == 1) {
                JSONObject receptionOrder = object.getJSONObject("SCENE_RECEPTION_DISPATCH");
                JSONObject investigationOrder = object.getJSONObject("SCENE_INVESTIGATION");
                JSONObject caseOrder = object.getJSONObject("SCENE_LAW_CASE");
                values.put("id", tableName);
                values.put("historyReception", object.getJSONObject("SCENE_RECEPTION_DISPATCH").toString());
                values.put("historyInvestigation", object.getJSONObject("SCENE_INVESTIGATION").toString());
                values.put("historyLowCase", object.getJSONObject("SCENE_LAW_CASE").toString());
                sQLiteDatabase.replace("HistoryConfigDetail", "id", values);

                if (count % 2000 == 0) {
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                    sQLiteDatabase.beginTransaction();
                }
            } else {
                JSONObject receptionObject = object.getJSONObject("SCENE_RECEPTION_DISPATCH");
                JSONObject investigationObject = object.getJSONObject("SCENE_INVESTIGATION");
                JSONObject caseObject = object.getJSONObject("SCENE_LAW_CASE");
                if (investigationObject.has("CASE_ID")) {
                    values.put("caseID", investigationObject.getString("CASE_ID"));
                } else {
                    values.put("caseID", "");
                }
                if (caseObject.has("CASE_NO")) {
                    values.put("caseNo", caseObject.getString("CASE_NO"));
                } else {
                    values.put("caseNo", "");
                }
                if (caseObject.has("CASE_NAME")) {
                    values.put("caseName", caseObject.getString("CASE_NAME"));
                } else {
                    values.put("caseName", "");
                }
                if (investigationObject.has("INVESTIGATOR")) {
                    values.put("investigator", investigationObject.getString("INVESTIGATOR"));
                } else {
                    values.put("investigator", "");
                }
                if (investigationObject.has("INVESTIGATOR_IDS")) {
                    values.put("investigatorIds", investigationObject.getString("INVESTIGATOR_IDS"));
                } else {
                    values.put("investigatorIds", "");
                }
                if (receptionObject.has("RECEIVED_DATE")) {
                    values.put("receivedDate", receptionObject.getString("RECEIVED_DATE"));
                } else {
                    values.put("receivedDate", "");
                }
                if (investigationObject.has("INVESTIGATION_DATE_FROM")) {
                    values.put("investigationDateFrom", investigationObject.getString("INVESTIGATION_DATE_FROM"));
                } else {
                    values.put("investigationDateFrom", "");
                }
                if (investigationObject.has("INVESTIGATION_DATE_TO")) {
                    values.put("investigationDateTo", investigationObject.getString("INVESTIGATION_DATE_TO"));
                } else {
                    values.put("investigationDateTo", "");
                }
                if (caseObject.has("OCCURRENCE_DATE_FROM")) {
                    values.put("occurrenceDateFrom", caseObject.getString("OCCURRENCE_DATE_FROM"));
                } else {
                    values.put("occurrenceDateFrom", "");
                }
                if (caseObject.has("OCCURRENCE_DATE_TO")) {
                    values.put("occurrenceDateTo", caseObject.getString("OCCURRENCE_DATE_TO"));
                } else {
                    values.put("occurrenceDateTo", "");
                }
                if (caseObject.has("SCENE_DETAIL")) {
                    values.put("sceneDetail", caseObject.getString("SCENE_DETAIL"));
                } else {
                    values.put("sceneDetail", "");
                }
                if (caseObject.has("CASE_TYPE")) {
                    values.put("caseType", caseObject.getString("CASE_TYPE"));
                } else {
                    values.put("caseType", "");
                }
                if (caseObject.has("caseTypeName")) {
                    values.put("caseTypeName", caseObject.getString("caseTypeName"));
                } else {
                    values.put("caseTypeName", "");
                }
                if (caseObject.has("CASE_CATEGORY")) {
                    values.put("caseCategory", caseObject.getString("CASE_CATEGORY"));
                } else {
                    values.put("caseCategory", "");
                }
                if (caseObject.has("SCENE_REGIONALISM")) {
                    values.put("sceneRegionalism", caseObject.getString("SCENE_REGIONALISM"));
                } else {
                    values.put("sceneRegionalism", "");
                }
                if (caseObject.has("SCENE_REGIONALISM_NAME")) {
                    values.put("sceneRegionalismName", caseObject.getString("SCENE_REGIONALISM_NAME"));
                } else {
                    values.put("sceneRegionalismName", "");
                }
                if (caseObject.has("EXPOSURE_PROCESS")) {
                    values.put("exposureProcess", caseObject.getString("EXPOSURE_PROCESS"));
                } else {
                    values.put("exposureProcess", "");
                }
                values.put("sceneInvestigationJson", investigationObject.toString());
                values.put("sceneLawCase", caseObject.toString());
                values.put("sceneReceptionDispatch", receptionObject.toString());
                sQLiteDatabase.replace(tableName, "caseID", values);

                if (count % 2000 == 0) {
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                    sQLiteDatabase.beginTransaction();
                }
            }
            //outputStream.write(s.getBytes());
        }
        reader.close();
        //outputStream.close();

        sQLiteDatabase.setTransactionSuccessful();
        sQLiteDatabase.endTransaction();
        return fileList;
    }

    public static ArrayList<File> unZipFileAndSave(File zipFile, String folderPath, final String tableName) throws ZipException, IOException {
        ArrayList<File> fileList = new ArrayList<>();
        String name = zipFile.getName();
        String path = zipFile.getPath();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        //try {
        ZipFile zips = new ZipFile(zipFile);
        for (Enumeration<?> entries = zips.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            long long1 = entry.getSize();
            InputStream inputStream = zips.getInputStream(entry);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            String descFilePath = folderPath + entry.getName();
            descFilePath = new String(descFilePath.getBytes("8859_1"), "GB2312");
            File descFile = new File(descFilePath);
            //File descFile = new File(folderPath,java.net.URLEncoder.encode(entry.getName(),"UTF-8"));
            if (!descFile.exists()) {
                descFile.createNewFile();
            } else {
                descFile.delete();
                descFile.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(descFile);
                /*byte buffer[] = new byte[1024*1024];
                int readline;
                while((readline = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer,0,readline);
                }*/
            while ((line = reader.readLine()) != null) {
                outputStream.write(line.getBytes());
            }
            reader.close();
            inputStreamReader.close();
            inputStream.close();
            outputStream.close();
            fileList.add(descFile);

                /*JSONObject allData = new JSONObject(dataBuilder.toString());
                JSONObject caseOrder = allData.getJSONObject("SCENE_LAW_CASE");
                JSONObject receptionOrder = allData.getJSONObject("SCENE_RECEPTION_DISPATCH");
                JSONObject investigationOrder = allData.getJSONObject("SCENE_INVESTIGATION");
                JSONArray datas = allData.getJSONArray("data");
                JSONObject caseData = null;
                JSONObject receptionData = null;
                JSONObject investigationData = null;
                int length = datas.length();
                for (int i = 0;i < length;i++){}*/
        }
        //}catch (JSONException jsonEx){
        //    Log.i("zhangsh","JSONException jsonEx",jsonEx);
        //}
        Utils.stopProgressDialog();
        return fileList;
    }

    public static void restartApp(Context context) {
        Toast.makeText(context, "登录超时或链接异常，请重新登录！", Toast.LENGTH_SHORT).show();
        Intent exitIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(exitIntent);
    }
}
