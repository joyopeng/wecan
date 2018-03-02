package com.gftxcky.draw;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NormalModeActivity extends Activity {

	private Toast _mToast; 
	private LinearLayout _mainContent;  
	private NormalModeCanvas normalModeCanvas = null;
	private PopupWindow _popupWin;
	private boolean _isChange=false;
	//
	private int _curBrushLevel;
	private int _brushSize[] = {
		3, 10, 20, 30, 40
	};
	private int _curRubberLevel;
	private int _rubberSize[] = {
		30, 40, 50
	};
	//工程相关
	private String _info="";
	private String _projectID;
	private String _title;
	private int _picID;
	private String _picName;

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.normalmode_activity);
		_projectID = getIntent().getStringExtra("ID");
//		getActionBar().hide();
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w=dm.widthPixels-30;
		int h=dm.heightPixels-125;
		normalModeCanvas = new NormalModeCanvas(this,w,h);
		if (getIntent().getByteArrayExtra("bitmap") != null)
		{
			android.graphics.Bitmap bitmap = Serializer.decodeBitmap(getIntent().getByteArrayExtra("bitmap"));
			if (bitmap != null)
				normalModeCanvas.setBitmap(bitmap);
			_picID = getIntent().getIntExtra("CID", -1);
			_title = getIntent().getStringExtra("bmName");
			_picName=_title;
			_isChange = true;
		}
		_mainContent= (LinearLayout)findViewById(R.id.normalmodeContent);
		getMainContent().removeAllViews();
		getMainContent().addView(normalModeCanvas);
		((ImageButton)findViewById(R.id.brush)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				NormalModeActivity.this.showToast("画笔");
				normalModeCanvas.line();
				((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
				((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
				getMainContent().removeAllViews();
				getMainContent().addView(normalModeCanvas);
				
			}
		});
		((ImageButton)findViewById(R.id.brushplus)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_curBrushLevel++;
				if(_brushSize.length<=_curBrushLevel)
					_curBrushLevel=_brushSize.length-1;
				//
				NormalModeActivity.this.showToast("画笔等级:"+(_curBrushLevel+1));
				normalModeCanvas.setDrawPaintSize(_brushSize[_curBrushLevel]);
				//
				((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
				((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
				getMainContent().removeAllViews();
				getMainContent().addView(normalModeCanvas);
			}
		});
		((ImageButton)findViewById(R.id.brushreduce)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_curBrushLevel--;
				if(_curBrushLevel<0)
					_curBrushLevel=0;
				//
				NormalModeActivity.this.showToast("画笔等级:"+(_curBrushLevel+1));
				normalModeCanvas.setDrawPaintSize(_brushSize[_curBrushLevel]);
				//
				((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
				((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
				getMainContent().removeAllViews();
				getMainContent().addView(normalModeCanvas);
			}
		});
		((ImageButton)findViewById(R.id.paintcolor)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (_popupWin != null)
				{
					_popupWin.dismiss();
					_popupWin=null;
					return;
				} 
				else
				{
					final View pop = LayoutInflater.from(NormalModeActivity.this).inflate(R.layout.popuwindow_paintcolor, null);
					_popupWin=new PopupWindow(pop);
					_popupWin.setWidth(60);
					_popupWin.setHeight(250);
					_popupWin.showAsDropDown(view, 0, -(260+view.getHeight()));
//					(NormalModeActivity.this.findViewById(R.id.normalmodeContent), 83, 225, 45);
					((ImageButton)pop.findViewById(R.id.black)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.paintcolor)).setImageDrawable(NormalModeActivity.this.getResources().getDrawable(R.drawable.black));
							normalModeCanvas.setDrawPaintColor(Color.BLACK);
							getMainContent().removeAllViews();
							getMainContent().addView(normalModeCanvas);
							_popupWin.dismiss();
							_popupWin=null;
						}
					});
					((ImageButton)pop.findViewById(R.id.red)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.paintcolor)).setImageDrawable(NormalModeActivity.this.getResources().getDrawable(R.drawable.red));
							normalModeCanvas.setDrawPaintColor(Color.RED);
							getMainContent().removeAllViews();
							getMainContent().addView(normalModeCanvas);
							_popupWin.dismiss();
							_popupWin=null;							
						}
					});
					((ImageButton)pop.findViewById(R.id.blue)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.paintcolor)).setImageDrawable(NormalModeActivity.this.getResources().getDrawable(R.drawable.blue));
							normalModeCanvas.setDrawPaintColor(Color.BLUE);
							getMainContent().removeAllViews();
							getMainContent().addView(normalModeCanvas);
							_popupWin.dismiss();
							_popupWin=null;								
						}
					});
					((ImageButton)pop.findViewById(R.id.green)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.__button_down);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.button_selector2);
							((ImageButton)NormalModeActivity.this.findViewById(R.id.paintcolor)).setImageDrawable(NormalModeActivity.this.getResources().getDrawable(R.drawable.green));
							normalModeCanvas.setDrawPaintColor(Color.GREEN);
							getMainContent().removeAllViews();
							getMainContent().addView(normalModeCanvas);
							_popupWin.dismiss();
							_popupWin=null;								
						}
					});
					return;
			}
			}
		});
		((ImageButton)findViewById(R.id.rubber)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NormalModeActivity.this.showToast("橡皮");
				//
				normalModeCanvas.clear();
				//
				((ImageButton)NormalModeActivity.this.findViewById(R.id.rubber)).setBackgroundResource(R.drawable.__button_down);
				((ImageButton)NormalModeActivity.this.findViewById(R.id.brush)).setBackgroundResource(R.drawable.button_selector2);
				getMainContent().removeAllViews();
				getMainContent().addView(normalModeCanvas);
			}
		});
		((ImageButton)findViewById(R.id.newBm)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NormalModeActivity.this.showToast("新建画布");
				//
				normalModeCanvas.newBitMap();
				getMainContent().removeAllViews();
				getMainContent().addView(normalModeCanvas);
				_isChange= false;
			}
		});
		((ImageButton)findViewById(R.id.savecanvas)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataBase db=new DataBase();
				if(_isChange)
				{
					Bitmap bitmap= normalModeCanvas.getBitmap();
//					contentvalues.put("scaledBitmap", Serializer.encode(Bitmap.createScaledBitmap(bitmap, 485, 292, true), 60));
					File prjfile = new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").append(_picName).toString());
					File cfile=new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").toString());
					if(!cfile.exists())
					{
						cfile.mkdir();
					}
					try
					{
						FileOutputStream outputstream = new FileOutputStream(prjfile);
						BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
						bufferotstream.write(Serializer.encode(bitmap, 80));
						bufferotstream.close();
						outputstream.close();
					}
					catch (IOException exception)
					{
						exception.printStackTrace();
						NormalModeActivity.this.showToast("数据更新失败");
						return;
					}
					NormalModeActivity.this.showToast("数据更新成功");
				}
				else
				{
					ContentValues contentvalues = new ContentValues();
					contentvalues.put("CASE_ID", _projectID);
					Bitmap bitmap= normalModeCanvas.getBitmap();
//					contentvalues.put("scaledBitmap", Serializer.encode(Bitmap.createScaledBitmap(bitmap, 485, 292, true), 60));
					String fname = new StringBuilder(Md5.getMD5(new StringBuilder(_projectID)
											.append(new Date().toString()).toString())).append(".png").toString();
					File prjfile = new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").append(fname).toString());
					File cfile=new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).toString());
					if(!cfile.exists())
					{
						cfile.mkdir();
					}
					try
					{
						FileOutputStream outputstream = new FileOutputStream(prjfile);
						BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
						bufferotstream.write(Serializer.encode(bitmap, 80));
						bufferotstream.close();
						outputstream.close();
					}
					catch (IOException exception)
					{
						exception.printStackTrace();
						db.close();
						return;
					}
					contentvalues.put("PICTURE_NAME", fname);
					contentvalues.put("TYPE", 4);
					_picID=(int)db.insert("SCENE_PICTURE", "_id", contentvalues);
					if ( _picID> 0L)
					{
						_isChange=true;
						NormalModeActivity.this.showToast("数据保存成功");
					}
					else
						NormalModeActivity.this.showToast("数据保存失败");
				}
				db.close();
			}
		});
		((ImageButton)findViewById(R.id.thumbnail)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_popupWin != null)
				{
					_popupWin.dismiss();
					_popupWin=null;
					return;
				} 
				if(_isChange)
				{
//					DataBase db = new DataBase();
//					ContentValues contentvalues = new ContentValues();
					Bitmap bitmap= normalModeCanvas.getBitmap();
//					contentvalues.put("scaledBitmap", Serializer.encode(Bitmap.createScaledBitmap(bitmap, 485, 292, true), 60));
					File prjfile = new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").append(_picName).toString());
					File cfile=new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").toString());
					if(!cfile.exists())
					{
						cfile.mkdir();
					}
					try
					{
						FileOutputStream outputstream = new FileOutputStream(prjfile);
						BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
						bufferotstream.write(Serializer.encode(bitmap, 80));
						bufferotstream.close();
						outputstream.close();
					}
					catch (IOException exception)
					{
						exception.printStackTrace();
//						db.close();
						NormalModeActivity.this.showToast("更新失败");
						return;
					}
					NormalModeActivity.this.showToast("更新成功");
//					db.close();
				}
				DataBase db = new DataBase();
				Cursor cursor = db.query("SCENE_PICTURE", new String[] {
						"_id", "CASE_ID", "PICTURE_NAME"
					}, new StringBuilder("TYPE=4 and CASE_ID=").append(_projectID).toString(), null, null);

				View pop=LayoutInflater.from(NormalModeActivity.this).inflate(R.layout.showbitmapbar, null);
				LinearLayout linearlayout = (LinearLayout)pop.findViewById(R.id.showbitMapBar);
				linearlayout.removeAllViews();
				while(cursor.moveToNext())
				{
					RelativeLayout relativelayout = new RelativeLayout(NormalModeActivity.this);
					relativelayout.setBackgroundColor(Color.TRANSPARENT);
					relativelayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(222, 200));
					ImageView imageview = new ImageView(NormalModeActivity.this);
					imageview.setBackgroundColor(Color.WHITE);
					imageview.setMaxHeight(200);
					imageview.setMaxWidth(190);
					imageview.setAdjustViewBounds(true);
					imageview.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -1));
					imageview.setTag(new StringBuilder(cursor.getString(1)).append("/").append(cursor.getString(2)).toString());
					imageview.setId(cursor.getInt(0));
					imageview.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(new StringBuilder("/sdcard/xckydb/").append(imageview.getTag().toString()).toString()), 485, 292, true));
					imageview.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View vw) {
							_isChange=true;
							ImageView imageview = (ImageView)vw;
							_picID=imageview.getId();
							String imgtag=imageview.getTag().toString();
							_picName=imgtag.split("/")[1];
							normalModeCanvas.setBitmap(BitmapFactory.decodeFile(new StringBuilder("/sdcard/xckydb/").append(imgtag).toString()));
							getMainContent().removeAllViews();
							getMainContent().addView(normalModeCanvas);
							_popupWin.dismiss();
							_popupWin= null;							
						}
					});
					ImageButton imagebutton = new ImageButton(NormalModeActivity.this);
					RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(50, 50);
					layoutparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					imagebutton.setLayoutParams(layoutparams);
					imagebutton.setTag(cursor.getInt(0));
					imagebutton.setImageDrawable(NormalModeActivity.this.getResources().getDrawable(R.drawable.del));
					imagebutton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View vw) {
							DataBase db= new DataBase();
							if (db.delete("SCENE_PICTURE",new StringBuilder("_id=").append(vw.getTag()).toString()) > 0)
							{
								((LinearLayout)vw.getParent().getParent()).removeView((RelativeLayout)vw.getParent());
								NormalModeActivity.this.showToast("删除成功");
								_isChange=false;
							} else
							{
								NormalModeActivity.this.showToast("删除失败");
							}
							db.close();
						}
					});
					relativelayout.addView(imageview);
					relativelayout.addView(imagebutton);
					LinearLayout linearlayout2 = new LinearLayout(NormalModeActivity.this);
					linearlayout2.setLayoutParams(new ViewGroup.LayoutParams(2, 200));
					linearlayout2.setBackgroundColor(Color.TRANSPARENT);
					linearlayout.addView(linearlayout2);
					linearlayout.addView(relativelayout);
				}
				LinearLayout linearlayout2 = new LinearLayout(NormalModeActivity.this);
				linearlayout2.setLayoutParams(new ViewGroup.LayoutParams(2, 200));
				linearlayout2.setBackgroundColor(Color.TRANSPARENT);
				_popupWin=new PopupWindow(pop);
				_popupWin.setWidth(NormalModeActivity.this.getWindowManager().getDefaultDisplay().getWidth()-30);
				_popupWin.setHeight(200);
				_popupWin.showAtLocation(NormalModeActivity.this.findViewById(R.id.normalmodeContent),Gravity.LEFT|Gravity.BOTTOM,15,60);
				cursor.close();
				db.close();
			}
		});
	}
	
	private static final int AIRPLAY_MESSAGE_HIDE_TOAST =1;
	private Handler _mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case AIRPLAY_MESSAGE_HIDE_TOAST:
					cancelToast();
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 提示
	 * @param paramString
	 */
	public void showToast(String msg){
		float time=0.8f;
		if(_mToast==null)
		{
			_mToast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		}
		else
		{
			_mToast.setText(msg);
			_mToast.setDuration(Toast.LENGTH_SHORT);
		}
		_mToast.show();
		//隐藏
		_mHandler.sendMessageDelayed(_mHandler.obtainMessage(AIRPLAY_MESSAGE_HIDE_TOAST),(int)(time*1000));
    }
	
	/**
	 * 取消提示
	 * @param paramString
	 */
	private void cancelToast()
	{
		if(_mToast!=null)
		{
			_mToast.cancel();
		}
	}

	public LinearLayout getMainContent() {
		return _mainContent;
	}


}
