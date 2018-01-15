package com.gofirst.scenecollection.evidence.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
	public final static String FMT_EN_Y_M_D="yyyy-MM-dd";
	public final static String FMT_EN_Y_M_D_H_M="yyyy-MM-dd HH:mm";
	public final static String FMT_EN_Y_M_D_H_M_S="yyyy-MM-dd HH:mm:ss";
	public final static String FMT_CN_Y_M_D="yyyy年MM月dd日";
	public final static String FMT_CN_Y_M_D_H_M="yyyy年MM月dd日HH时mm分";
	public final static String FMT_CN_Y_M_D_H_M_S="yyyy年MM月dd日HH时mm分ss秒";
	
	public static long getDiffOfDate(Date time1, Date time2){
		long quot = 0;
		quot = time1.getTime() - time2.getTime();
		quot = quot / 1000 / 60 / 60 / 24;
		return quot;
	 }
	
	public static long getDiffOfMinute(Date startTime, Date endTime) {
		long quot = 0;
		quot = startTime.getTime() - endTime.getTime();
		quot = quot / 1000 / 60;
		return quot;
	}
	
	public static  String DateFormat(Date date){
		if(date==null )
			return "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(FMT_EN_Y_M_D);
			return format.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static  String DateFormat(Date date,String pattern){
		if(date==null )
			return "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			return format.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	public static  String DateFormatLong(Date date){
		if(date==null )
			return "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(FMT_EN_Y_M_D_H_M_S);
			return format.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public static  String DateFormatShort(Date date){
		if(date==null )
			return "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(FMT_EN_Y_M_D);
			return format.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	public static Date parseDate(String datestr) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			String fmtstr = null;
			if (datestr.indexOf(':') > 0) {
				fmtstr = FMT_EN_Y_M_D_H_M_S;
			} else {
				fmtstr = FMT_EN_Y_M_D;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
			return sdf.parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}
	public static Date parseDate(String datestr,String pattern) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.UK);
			return sdf.parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}	
	

	public static Date dateAddDays(Date sDate, int days){
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sDate);
        sCalendar.add(Calendar.DATE, days);
        return sCalendar.getTime();
		  
	}

	public static int getYear() {
		Calendar c = Calendar.getInstance();
		return  c.get(Calendar.YEAR);
	}

	public static int getMonth() {
		Calendar c = Calendar.getInstance();
		return  c.get(Calendar.MONTH);
	}

	public static int getDay() {
		Calendar c = Calendar.getInstance();
		return  c.get(Calendar.DAY_OF_MONTH);
	}
}
