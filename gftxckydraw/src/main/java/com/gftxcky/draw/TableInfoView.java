package com.gftxcky.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class TableInfoView extends View{

	private String _tabInfo;
	public TableInfoView(Context context) {
		super(context);
	}
	
	public TableInfoView(Context context,String tabInfo) {
		super(context);
		_tabInfo=tabInfo;
	}
	
	protected void onDraw(Canvas canvas)
	{
		Paint borderPaint = new Paint();
		borderPaint.setStrokeWidth(1.0F);
		borderPaint.setStyle(android.graphics.Paint.Style.STROKE);
		borderPaint.setAntiAlias(true);
		Paint textPaint = new Paint();
		textPaint.setTextSize(20F);
		textPaint.setAntiAlias(true);
		canvas.drawRect(new Rect(50, 10, 450, 290), borderPaint);
		canvas.drawLine(200F, 10F, 200F, 290F, borderPaint);
		canvas.drawText("制图时间", 60F, 280F, textPaint);
		canvas.drawText(_tabInfo.split(",")[4], 205F, 280F, textPaint);
		canvas.drawLine(50F, 250F, 450F, 250F, borderPaint);
		canvas.drawText("制图人", 60F, 230F, textPaint);
		canvas.drawText(_tabInfo.split(",")[3], 205F, 230F, textPaint);
		canvas.drawLine(50F, 210F, 450F, 210F, textPaint);
		canvas.drawText("制图单位", 60F, 160F, textPaint);
		if (_tabInfo.split(",")[2].length() > 12)
		{
			canvas.drawText(_tabInfo.split(",")[2].substring(0, 12), 205F, 160F, textPaint);
			canvas.drawText(_tabInfo.split(",")[2].substring(12), 205F, 190F, textPaint);
		} 
		else
		{
			canvas.drawText(_tabInfo.split(",")[2], 205F, 160F, textPaint);
		}
		canvas.drawLine(50F, 130F, 450F, 130F, borderPaint);
		canvas.drawText("发案地址", 60F, 80F, textPaint);
		if (_tabInfo.split(",")[1].length() > 12)
		{
			canvas.drawText(_tabInfo.split(",")[1].substring(0, 12), 205F, 80F, textPaint);
			canvas.drawText(_tabInfo.split(",")[1].substring(12), 205F, 110F, textPaint);
		} else
		{
			canvas.drawText(_tabInfo.split(",")[1], 205F, 80F, textPaint);
		}
		canvas.drawLine(50F, 50F, 450F, 50F, borderPaint);
		canvas.drawText("发案时间", 60F, 40F, textPaint);
		canvas.drawText(_tabInfo.split(",")[0], 205F, 40F, textPaint);
	}

	public void setInfo(String tabInfo)
	{
		_tabInfo= tabInfo;
	}
}
