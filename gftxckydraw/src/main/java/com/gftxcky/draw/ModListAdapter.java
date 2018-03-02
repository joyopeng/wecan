package com.gftxcky.draw;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ModListAdapter extends  BaseExpandableListAdapter
{
	private ExperModeActivity _main;
	
	public ModListAdapter(ExperModeActivity expermodeactivity)
	{
		super();
		_main = expermodeactivity;
	}
	
	private TextView getNameTextView(String name)
	{
		android.widget.AbsListView.LayoutParams layoutparams = new android.widget.AbsListView.LayoutParams(-1, getSwitchScale(30));
		ModNameTextView nameText = new ModNameTextView(_main);
		nameText.setLayoutParams(layoutparams);
		nameText.setGravity(19);
		nameText.setPadding(getSwitchScale(16), 0, 0, 0);
		nameText.setText(name);
		nameText.setTextColor(Color.BLACK);
		nameText.setTextSize(16F);
		return nameText;
	}
	
	private ModNameTextView getClickNameTextView(String name)
	{
		android.widget.AbsListView.LayoutParams layoutparams = new android.widget.AbsListView.LayoutParams(-1, getSwitchScale(25));
		ModNameTextView nameText = new ModNameTextView(_main);
		nameText.setLayoutParams(layoutparams);
		nameText.setGravity(Gravity.CENTER_VERTICAL);
		nameText.setPadding(getSwitchScale(40), 0, 0, 0);
		nameText.setText(name.split("-")[0]);
		nameText.setTextColor(Color.BLACK);
		nameText.setTextSize(15F);
		nameText.setOnClickListener(new ModeTextOnClickListener(true));
		return nameText;
	}
	
	public String getChild(int mindex, int cindex)
	{
		return _main.getModListChilds().get(mindex).get(cindex);
	}
	
	public long getChildId(int i, int j)
	{
		return 0L;
	}
	
	public int getChildrenCount(int index)
	{
		return _main.getModListChilds().get(index).size();
	}
	
	public Object getGroup(int index)
	{
		return _main.getModList().get(index);
	}
	
	public int getGroupCount()
	{
		return _main.getModList().size();
	}
	
	public long getGroupId(int index)
	{
		return (long)index;
	}
	
	
	public boolean hasStableIds()
	{
		return false;
	}
	
	public boolean isChildSelectable(int i, int j)
	{
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textview;
		if (convertView != null)
		{
			textview = (TextView)convertView;
			textview.setText(_main.getModList().get(groupPosition));
		} 
		else
		{
			textview = getNameTextView(_main.getModList().get(groupPosition));
		}
		//
		if (getChildrenCount(groupPosition) == 0)
		{
			textview.setOnClickListener(new ModeTextOnClickListener(false));
		}
		return textview;
	} 
	
	public View getChildView(int i, int j, boolean flag, View view, ViewGroup viewgroup)
	{ 
		return getClickNameTextView(_main.getModListChilds().get(i).get(j));
	}
	
	
	public class ModNameTextView extends TextView
	{

		public ModNameTextView(Context context)
		{
			super(context);
		}

		@SuppressLint("DrawAllocation") 
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			canvas.drawLine(1F,  getHeight()-1, getWidth()-1, -1 + getHeight()-1, paint);
		}
	}
	
	public class ModeTextOnClickListener implements OnClickListener
	{
		private Boolean _isChild;
		public ModeTextOnClickListener(Boolean isChild)
		{
			_isChild=isChild;
		}

		@Override
		public void onClick(View view) {
				TextView textview = (TextView)view;
				_main.getMouldSelLinear().removeAllViews();
				String name = textview.getText().toString().split(" ")[0];
				String query=_isChild?(new StringBuilder("c='")).append(name).append("'").toString():(new StringBuilder("f='")).append(name).append("' or g='").append(name).append("' or h='").append(name).append("' or i='").append(name).append("'").toString();
				DataBase db=new DataBase("mod");
				Cursor cursor = db.query("mouldinfo", new String[] {
					"a,j,k"
				},query ,null ,null);
				
				LinearLayout linearlayout=null;
				int index=0;
				while(cursor.moveToNext())
				{
					if(index==0)
					{
						linearlayout= new LinearLayout(_main);
		 				linearlayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(getSwitchScale(300), getSwitchScale(150)));
		 				linearlayout.setOrientation(LinearLayout.HORIZONTAL);
					}
					
					//
					LinearLayout vlinearlayout = new LinearLayout(_main);
					vlinearlayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(getSwitchScale(150), getSwitchScale(150)));
					vlinearlayout.setOrientation(LinearLayout.VERTICAL);
					vlinearlayout.setGravity(Gravity.CENTER);
					ImageView imageview = new ImageView(_main);
					imageview.setAdjustViewBounds(true);
					imageview.setMaxHeight(getSwitchScale(100));
					imageview.setMaxWidth(getSwitchScale(125));
					//读取图片资源
					imageview.setImageBitmap(_main.getImageFromAssetsFile((new StringBuilder(cursor.getString(1))).append("_s.jpg").toString()));
					imageview.setTag((new StringBuilder(cursor.getString(0))).append("-").append(cursor.getString(1)).append("-").append(cursor.getString(2)).toString());
					imageview.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ImageView  imgview= (ImageView)v;
							ModNameTextView text = new ModNameTextView(_main);
							text.setText(imgview.getTag().toString().split("-")[0]);
							text.setTag((new StringBuilder(imgview.getTag().toString().split("-")[1])).append("-").append(imgview.getTag().toString().split("-")[2]).toString());
							text.setPadding(getSwitchScale(10), 0, 0, 0);
							text.setGravity(Gravity.CENTER_VERTICAL);
							text.setHeight(50);
							text.setTextColor(Color.BLACK);
							text.setTextSize(13F);
							text.setOnTouchListener(new OnTouchListener() {
								@Override
								public boolean onTouch(View v, MotionEvent event) {
									if (event.getAction() == MotionEvent.ACTION_DOWN)
										_main.setSelModX(event.getX());
									if (event.getAction() ==  MotionEvent.ACTION_MOVE && event.getX() - _main.getSelModX() > 100F)
										((LinearLayout)v.getParent()).removeView(v);
									return true;
								}
							});
							_main.getSelEdMouldLinear().addView(text);
						}
					});
					vlinearlayout.addView(imageview);
					TextView nametext= new TextView(_main);
					nametext.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, getSwitchScale(25)));
					nametext.setText(cursor.getString(0));
					nametext.setTextColor(Color.BLACK);
					nametext.setTextSize(16F);
					nametext.setGravity(Gravity.CENTER_HORIZONTAL);
					vlinearlayout.addView(nametext);
					linearlayout.addView(vlinearlayout);
					
					//
					if (++index == 3)
					{
						_main.getMouldSelLinear().addView(linearlayout);
						index = 0;
					}
				}
				//将未满3个的也加入
				if(index!=0)
					_main.getMouldSelLinear().addView(linearlayout);
				cursor.close();
				db.close();
			return;	
		}
		
	}

	private int getSwitchScale(int value){
		float scale = _main.getResources().getDisplayMetrics().density;
		return (int)(value * scale + 0.5f);
	}
}
