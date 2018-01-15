package com.gofirst.scenecollection.evidence.utils;

import java.security.Key;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.util.Base64;
 

public class StringUtil {

	 public static final String EMPTY = "";
	public static boolean isNullorEmpty(String val){
		if(val==null)
			return true;
		if("".equals(val))
			return true;
		return false;
	}
	public static String toStr(String val){
		if(val==null)
			return "";
		return val;
	}
	public static String toStr(Double val){
		if(val==null)
			return "0.00";
		return new java.text.DecimalFormat("0.00").format(val);
	}
	public static String toStr(Double val,String pattern){
		if(val==null)
			return pattern;
		return new java.text.DecimalFormat(pattern).format(val);
	}
	
	/**
	 * 判断是否是手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		if (isNullorEmpty(phone)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^1[0-9]{10}");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	

}
