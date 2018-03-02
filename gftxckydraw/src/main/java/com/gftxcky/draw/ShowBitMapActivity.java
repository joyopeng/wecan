package com.gftxcky.draw;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ShowBitMapActivity extends Activity implements OnGestureListener, OnTouchListener {

	/**
	 * 创建
	 */
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.showbitmap_activity);
		Intent intent = getIntent();
		if (intent.getStringExtra("type").equals("4"))
		{
			Intent startIntent = new Intent();
			startIntent.putExtra("ID", intent.getStringExtra("info").split(",")[0].toString());
			Log.d("test","ID is"+ intent.getStringExtra("info"));
			startIntent.setClass(this, NormalModeActivity.class);
			startActivityForResult(startIntent, 1);
			return;
		} 
		else if (intent.getStringExtra("type").equals("3"))
		{
			Intent startIntent = new Intent();
			startIntent.putExtra("info", intent.getStringExtra("info").toString());
			Log.d("test","info is"+ intent.getStringExtra("info"));
			startIntent.setClass(this,ExperModeActivity.class);
			startActivityForResult(startIntent, 1);
			return;
		}
		else
		{
			DataBase db = new DataBase();
			Cursor cursor = db.query("SCENE_PICTURE", new String[] {
				"ID", "CASE_ID", "PICTURE_NAME","TYPE"
			}, (new StringBuilder("ID=")).append(intent.getStringExtra("id")).toString(), null, null);
			
			//
			if (cursor.moveToNext())
			{
				Intent startIntent = new Intent();
				if (cursor.getInt(3) == 4)
				{
					startIntent.setClass(this, NormalModeActivity.class);
					startIntent.putExtra("bitmap",Serializer.encode(BitmapFactory.decodeFile(new StringBuilder("/sdcard/xckydb/").append(cursor.getString(1)).append("/").append(cursor.getString(2)).toString()), 100));
					startIntent.putExtra("CID", cursor.getInt(0));
					startIntent.putExtra("ID", cursor.getString(1));
					startIntent.putExtra("bmName", cursor.getString(2));
				} 
				else
				{
					startIntent.setClass(this, ExperModeActivity.class);
					File gfile=new File(new StringBuilder("/sdcard/xckydb/").append(cursor.getString(1)).append("/").append(cursor.getString(2)+".plan").toString());
					try
					{
						FileInputStream ins=new FileInputStream(gfile);
						byte[] buffer=new byte[(int)gfile.length()];
						BufferedInputStream bufferitstream = new BufferedInputStream(ins);
						bufferitstream.read(buffer,0, buffer.length);
						bufferitstream.close();
						ins.close();
						startIntent.putExtra("gather", buffer);
						startIntent.putExtra("CID", cursor.getInt(0));
						startIntent.putExtra("ID", cursor.getString(1));
					}
					catch (IOException exception)
					{
						db.close();
						exception.printStackTrace();
						return;
					}
				}
				cursor.close();
				db.close();
				startActivityForResult(startIntent, 1);
				return;
			}
			cursor.close();
			db.close();
		}
		return;
	}
	
	@Override
	protected void onActivityResult(int i, int j, Intent intent)
	{
		super.onActivityResult(i, j, intent);
		finish();
	}
	
	@Override
	public void onBackPressed()
	{
		setResult(1);
		System.exit(0);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
