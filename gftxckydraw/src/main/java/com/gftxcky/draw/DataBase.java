package com.gftxcky.draw;


import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DataBase {
	private SQLiteDatabase _database;

	public DataBase()
	{
		String sdcard = Environment.getExternalStorageDirectory().toString();
		if ((new File((new StringBuilder(sdcard)).append("/xckydb/gftxcky.db").toString())).exists())
			_database = SQLiteDatabase.openOrCreateDatabase((new StringBuilder(sdcard)).append("/xckydb/gftxcky.db").toString(), null);
	}

	public DataBase(String name)
	{
		String sdcard = Environment.getExternalStorageDirectory().toString();
		if (!(new File((new StringBuilder(sdcard)).append("/xckydb/").append(name).append(".db").toString())).exists())
		{
			if(name=="mod")
			{
				
			}
		}
		if ((new File((new StringBuilder(sdcard)).append("/xckydb/").append(name).append(".db").toString())).exists())
			_database = SQLiteDatabase.openOrCreateDatabase((new StringBuilder(sdcard)).append("/xckydb/").append(name).append(".db").toString(), null);
	}
	


	public int udpate(String table, ContentValues contentvalues, String where)
	{
		return _database.update(table, contentvalues, where, null);
	}

	public int delete(String table, String where)
	{
		return _database.delete(table, where, null);
	}

	public long insert(String table, String nullColumnHack, ContentValues contentvalues)
	{
		return _database.insert(table, nullColumnHack, contentvalues);
	}

	public Cursor query(String table, String[] columns, String selection, String orderBy, String groupBy)
	{
		return _database.query(table, columns, selection, null, groupBy, null, orderBy);
	}

	public void close()
	{
		_database.close();
	}
}
