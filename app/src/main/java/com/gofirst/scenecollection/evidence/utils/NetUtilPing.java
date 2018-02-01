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
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
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
import java.lang.reflect.Field;
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
    public static List<BaseDataBean> getBaseData(final Context mContext) {
        // lac连接基站位置区域码 cellid连接基站编码 mcc MCC国家码 mnc MNC网号
        // signalstrength连接基站信号强度
        List<BaseDataBean> results = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        CellLocation location = telephonyManager.getCellLocation();
        List<CellInfo> infoLists = telephonyManager.getAllCellInfo();
        if (infoLists != null && infoLists.size() != 0) {
            for (CellInfo info : infoLists) {
                BaseDataBean beans = new BaseDataBean();
                if (info.toString().contains("CellInfoLte")) {
                    CellInfoLte cellInfoLte = (CellInfoLte) info;
                    CellIdentityLte cellIdentityLte = cellInfoLte
                            .getCellIdentity();
                    beans.setCell_id(cellIdentityLte.getCi() + "");
                    beans.setTac(cellIdentityLte.getTac() + "");
                    beans.setMcc(cellIdentityLte.getMcc() + "");
                    beans.setMnc(cellIdentityLte.getMnc() + "");
                    beans.setPci(cellIdentityLte.getPci() + "");
                    try {
                        Class userCla = (Class) cellIdentityLte.getClass();
                        Field[] fields = userCla.getDeclaredFields();
                        for (Field f : fields) {
                            if ("mEarfcn".equalsIgnoreCase(f.getName())) {
                                if (!f.isAccessible()) {
                                    f.setAccessible(true);
                                }
                                beans.setArfcn(f.getInt(cellIdentityLte) + "");
                                break;
                            }
                        }
                    } catch (IllegalAccessException ee) {
                    }
                    //
                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte
                            .getCellSignalStrength();
                    beans.setSignalstrength(cellSignalStrengthLte.getDbm() + "");
                    beans.setLeveltext(cellSignalStrengthLte.getLevel() + "");
                    beans.setAsulevel(cellSignalStrengthLte.getAsuLevel() + "");
                    beans.setCpi("");
                    beans.setBsic("");
                    beans.setLac("");
                    beans.setTime(cellInfoLte.getTimeStamp() + "");
                } else if (info.toString().contains("CellInfoGsm")) {
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                    CellIdentityGsm cellIdentityGsm = cellInfoGsm
                            .getCellIdentity();
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm
                            .getCellSignalStrength();
                    beans.setSignalstrength(cellSignalStrengthGsm.getDbm() + "");
                    beans.setCell_id(cellIdentityGsm.getCid() + "");
                    beans.setLac(cellIdentityGsm.getLac() + "");
                    beans.setMcc(cellIdentityGsm.getMcc() + "");
                    beans.setMnc(cellIdentityGsm.getMnc() + "");
                    try {
                        Class userCla = (Class) cellIdentityGsm.getClass();
                        Field[] fields = userCla.getDeclaredFields();
                        for (Field f : fields) {
                            if ("mBsic".equalsIgnoreCase(f.getName())) {
                                if (!f.isAccessible()) {
                                    f.setAccessible(true);
                                }
                                beans.setBsic(f.getInt(cellIdentityGsm) + "");
                                continue;
                            }
                            if ("mArfcn".equalsIgnoreCase(f.getName())) {
                                if (!f.isAccessible()) {
                                    f.setAccessible(true);
                                }
                                beans.setEarfcn(f.getInt(cellIdentityGsm) + "");
                                continue;
                            }
                        }
                    } catch (IllegalAccessException ee) {
                    }
                    beans.setAsulevel(cellSignalStrengthGsm.getAsuLevel() + "");
                    beans.setLeveltext(cellSignalStrengthGsm.getLevel() + "");
                } else if (info.toString().contains("CellInfoCdma")) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                    CellIdentityCdma cellIdentityCdma = cellInfoCdma
                            .getCellIdentity();
                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma
                            .getCellSignalStrength();
                    beans.setCell_id(cellIdentityCdma.getBasestationId() + "");
                    beans.setSignalstrength(cellSignalStrengthCdma.getCdmaDbm()
                            + "");
                    beans.setLac("0");
                    beans.setMcc("0");
                    beans.setMnc("0");
                    beans.setAsulevel(cellSignalStrengthCdma.getAsuLevel() + "");
                }
                results.add(beans);
            }
        } else {
//            List<NeighboringCellInfo> list = telephonyManager.getNeighboringCellInfo();
//            Log.v("aaaa", list.size()+"");
//            CellLocation location1 = telephonyManager.getCellLocation();
//            if (location1 instanceof GsmCellLocation) {
//                location1 = (GsmCellLocation) location1;
//                Log.v("aaaa", ((GsmCellLocation) location1).getCid() + "");
//            }

        }

        return results;
    }
}
