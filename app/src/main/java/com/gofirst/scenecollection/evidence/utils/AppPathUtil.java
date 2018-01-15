package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AppPathUtil {
	private static String ROOT_PATH="CMI-CSI";

	/**
	 * 获取App文件存放路径 /CMI-CSI
	 * @return
     */
	public static String getPath(){
		String basepath = null;
		if (hasSdcard()) {
			basepath = Environment.getExternalStorageDirectory()+ "/"+ROOT_PATH;
			createPath(basepath);
		}
		return basepath;
	}
	/**
	 * 获取App数据文件存放路径 /CMI-CSI/data
	 * @return
	 */
	public static String getDataPath(){
		String path="";
		if (hasSdcard()) {
			path = getPath() + "/data";
			createPath(path);
		}
		return path;
	}
	/**
	 * 获取App数据库文件存放路径 /CMI-CSI/db
	 * @return
	 */
	public static String getDBPath(){
		String path="";
		if (hasSdcard()) {
			path = getPath() + "/db";
			createPath(path);
		}
		return path;
	}

	/**
	 * 获取App ZIP文件存放路径 /CMI-CSI/AppZip
	 * @return
	 */
	public static String getZipPath(){
		String path="";
		if (hasSdcard()) {
			path = getPath() + "/AppZip";
			createPath(path);
		}
		return path;
	}

	/**
	 * 获取App缓存文件存放路径 /CMI-CSI/cache
	 * @return
	 */
	public static String getCachePath(){
		String path="";;
		if (hasSdcard()) {
			path =  getPath() + "/cache";
			createPath(path);
		}
		return path;
	}
	/**
	 * 获取App缓存文件存放路径 /CMI-CSI/log
	 * @return
	 */
	public static String getLogPath(){
		String path="";;
		if (hasSdcard()) {
			path =  getPath() + "/log";
			createPath(path);
		}
		return path;
	}


	public static void deleteAllFiles(File root) {
		if(root==null)
			return;
		File files[] = root.listFiles();
		if (files != null){
			for (File f : files) {
				if (f.isDirectory()) { // 判断是否为文件夹
					deleteAllFiles(f);
					try {
						f.delete();
					} catch (Exception e) {
					}
				} else {
					if (f.exists()) { // 判断是否退出（未被使用）
						deleteAllFiles(f);
						try {
							f.delete();
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}


	public static boolean hasSdcard(){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public static boolean retrieveFileFromAssets(Context context, String fileName,String path) {
		boolean bRet = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bRet;
	}


}
