package com.gofirst.scenecollection.evidence.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetUtilPing {
    private static final String TAG = NetUtilPing.class.getSimpleName();

    private static BufferedWriter bw;
    private static FileWriter writer2;
    private static String fileName;

    public static boolean ping(String host, int pingCount, String fname) {
        fileName = fname;
        try {
            initWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
//        String command = "ping -c " + pingCount + " -w 5 " + host;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                Log.v(TAG, line);
                writerInfo(line);
            }
            int status = process.waitFor();
            if (status == 0) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
            writerInfo(line);
        } catch (IOException e) {
            Log.v(TAG, e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Log.v(TAG, e.getLocalizedMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    Log.v(TAG, e.getLocalizedMessage());
                }
            }
        }
        if (bw != null && writer2 != null)
            try {
                bw.close();
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return isSuccess;
    }

    private static void initWriter() throws IOException {
        String path = AppPathUtil.getLogPath() + "/logs";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        writer2 = new FileWriter(path + "/" + fileName, true);
        bw = new BufferedWriter(writer2);
    }

    private static void writerInfo(String s) {
        if (s == null)
            return;
        try {
            if (bw != null) {
                bw.append(DateTimeUtil.DateFormat(new Date(), DateTimeUtil.FMT_EN_Y_M_D_H_M_S) + ":" + s);
                bw.newLine();
            }
            Log.v("Splash", s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到基站数据
     */
    @SuppressLint("NewApi")
    public static void getBaseData(final Context mContext) {
        // lac连接基站位置区域码 cellid连接基站编码 mcc MCC国家码 mnc MNC网号
        // signalstrength连接基站信号强度
        List<BaseDataBean> list = new ArrayList<BaseDataBean>();
        BaseDataBean beans = new BaseDataBean();
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getNetworkOperator();
        beans.setMcc(operator.substring(0, 3));
        beans.setMnc(operator.substring(3));
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {// 这是电信的
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephonyManager
                    .getCellLocation();
            beans.setCell_id(cdmaCellLocation.getBaseStationId() + "");
            beans.setLac(cdmaCellLocation.getNetworkId() + "");
        } else {// 这是移动和联通的
            GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager
                    .getCellLocation();
            beans.setCell_id(gsmCellLocation.getCid() + "");
            beans.setLac(gsmCellLocation.getLac() + "");
        }
        beans.setSignalstrength("0");
        list.add(beans);
        List<CellInfo> infoLists = telephonyManager.getAllCellInfo();
        if (infoLists.size() != 0) {
            for (CellInfo info : infoLists) {
                /** 1、GSM是通用的移动联通电信2G的基站。
                 2、CDMA是3G的基站。
                 3、LTE，则证明支持4G的基站。*/
                BaseDataBean bean = new BaseDataBean();
                if (info.toString().contains("CellInfoLte")) {
                    CellInfoLte cellInfoLte = (CellInfoLte) info;
                    CellIdentityLte cellIdentityLte = cellInfoLte
                            .getCellIdentity();
                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte
                            .getCellSignalStrength();
                    bean.setSignalstrength(cellSignalStrengthLte.getDbm() + "");
                    bean.setCell_id(cellIdentityLte.getCi() + "");
                    bean.setLac(cellIdentityLte.getTac() + "");
                    bean.setMcc(cellIdentityLte.getMcc() + "");
                    bean.setMnc(cellIdentityLte.getMnc() + "");
                } else if (info.toString().contains("CellInfoGsm")) {
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                    CellIdentityGsm cellIdentityGsm = cellInfoGsm
                            .getCellIdentity();
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm
                            .getCellSignalStrength();
                    bean.setSignalstrength(cellSignalStrengthGsm.getDbm() + "");
                    bean.setCell_id(cellIdentityGsm.getCid() + "");
                    bean.setLac(cellIdentityGsm.getLac() + "");
                    bean.setMcc(cellIdentityGsm.getMcc() + "");
                    bean.setMnc(cellIdentityGsm.getMnc() + "");
                } else if (info.toString().contains("CellInfoCdma")) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                    CellIdentityCdma cellIdentityCdma = cellInfoCdma
                            .getCellIdentity();
                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma
                            .getCellSignalStrength();
                    bean.setCell_id(cellIdentityCdma.getBasestationId() + "");
                    bean.setSignalstrength(cellSignalStrengthCdma.getCdmaDbm()
                            + "");
                    /**因为待会我要把这个list转成gson，所以这个对象的所有属性我都赋一下值，不必理会这里*/
                    bean.setLac("0");
                    bean.setMcc("0");
                    bean.setMnc("0");
                }
                list.add(bean);
            }
        }
    }
}
