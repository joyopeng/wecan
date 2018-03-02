package com.gftxcky.draw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Serializer {
	
	/**
	 * 反序列化图片
	 * @param bytes
	 * @return
	 */
	public static Bitmap decodeBitmap(byte[] bytes)
	{
		if (bytes.length != 0)
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		else
			return null;
	}
	
	/**
	 * 反序列化对象
	 * @param bytes
	 * @return
	 */
	public static Object decode(byte[] bytes)
	{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		Object obj;
		try
		{
			ObjectInputStream objectinputstream = new ObjectInputStream(inputStream);
			obj = objectinputstream.readObject();
			objectinputstream.close();
			inputStream.close();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			return null;
		}
		return obj;
	}

	/**
	 * 序列化图片
	 * @param bitmap
	 * @param quality
	 * @return
	 */
	public static byte[] encode(Bitmap bitmap, int quality)
	{
		ByteArrayOutputStream inputStream = new ByteArrayOutputStream();
		bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, quality, inputStream);
		return inputStream.toByteArray();
	}

	/**
	 * 序列 化对象
	 * @param obj
	 * @return
	 */
	public static byte[] encode(Object obj)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream objectoutputstream = new ObjectOutputStream(outputStream);
			objectoutputstream.writeObject(obj);
			objectoutputstream.close();
			outputStream.close();
		}
		catch (IOException ioexception)
		{
			ioexception.printStackTrace();
		}
		return outputStream.toByteArray();
	}
}
