package com.gftxcky.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class NormalModeCanvas  extends View  {
	NormalModeActivity _main;
	int _width;
	int _height;
	//图片对象
	public Bitmap _bitmap;
	//画板
	private Canvas _canvas;
	//当前路径
	private Path   _path;
	//当前画笔
	private Paint _paint;
	//橡皮檫
	private Paint _rubberPaint;
	//画线画笔
	private Paint _linePaint;
	//橡皮檫半径大小
	private float _rubberRadius;
	
	//状态
	private boolean _isRubber;
	private boolean _isLine;
	private float _startX;
	private float _startY;
	private float _x;
	private float _y;

	public NormalModeCanvas(Context context,int width,int height)
	{
		super(context);
		_main=(NormalModeActivity)context;
		_width=width;
		_height=height;
		_rubberPaint = new Paint();
		_rubberPaint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		_rubberPaint.setStyle(android.graphics.Paint.Style.STROKE);
		_rubberPaint.setStrokeJoin(android.graphics.Paint.Join.ROUND);
		_rubberPaint.setAntiAlias(true);
		_rubberPaint.setStrokeWidth(60F);
		_rubberPaint.setColor(Color.WHITE);
		_linePaint = new Paint();
		_linePaint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		_linePaint.setStyle(android.graphics.Paint.Style.STROKE);
		_linePaint.setStrokeJoin(android.graphics.Paint.Join.ROUND);
		_linePaint.setAntiAlias(true);
		_linePaint.setStrokeWidth(3F);
		_linePaint.setColor(Color.BLACK);
		_linePaint.setTypeface(Typeface.SERIF);
		_bitmap = Bitmap.createBitmap(_width,_height, android.graphics.Bitmap.Config.ARGB_8888);
		_canvas = new Canvas(_bitmap);
		_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
		_canvas.drawColor(Color.WHITE);
		_paint = _linePaint;
	}

	/**
	 * 新建画布
	 */
	public void newBitMap()
	{
		_bitmap = Bitmap.createBitmap(_width, _height, android.graphics.Bitmap.Config.ARGB_8888);
		_canvas.setBitmap(_bitmap);
		_canvas.drawColor(Color.WHITE);
	}
	
	/**
	 * 切换到橡皮檫模式
	 */
	public void clear()
	{
		_paint = _rubberPaint;
		_rubberRadius = 30;
	}

	/**
	 * 切换到画模式
	 */
	public void line()
	{
		_paint = _linePaint;
		_paint.setStrokeWidth(3F);
		_paint.setColor(0xff000000);
	}


	/**
	 * 绘画
	 */
	protected void onDraw(Canvas canvas)
	{
		if (_path != null && _isLine)
			_canvas.drawPath(_path, _paint);
		canvas.drawBitmap(_bitmap, 0.0F, 0.0F, null);
		if (_isRubber)
		{
			Paint paint = new Paint();
			paint.setStyle(android.graphics.Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1.0F);
			canvas.drawCircle(_x, _y, _rubberRadius, paint);
		}
	}
	
	

	/**
	 * 操作事件
	 */
	public boolean onTouchEvent(MotionEvent motionevent)
	{
		if (_paint == null)
			return false;
		switch(motionevent.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			_isLine = true;
			_path = new Path();
			_startX = motionevent.getX();
			_startY = motionevent.getY();
			_path.moveTo(_startX, _startY);
			if (_paint == _rubberPaint)
				_isRubber = true;
			else
				_isRubber = false;
			break;
		case MotionEvent.ACTION_MOVE:
			_path.lineTo(motionevent.getX(), motionevent.getY());
			_x = motionevent.getX();
			_y = motionevent.getY();
			/*
			//只刷新操作过的地方
			//画图过快的刷新更不上，出现断线
			float minX;
			float maxX;
			float minY;
			float maxY;
			if (_startX < _x)
			{
				minX = _startX;
				maxX = _x;
			}
			else
			{
				minX = _x;
				maxX = _startX;
			}
			if (_startY < _y)
			{
				minY = _startY;
				maxY = _y;
			} else
			{
				minY = _y;
				maxY = _startY;
			}
			invalidate(new Rect((int)minX, (int)minY, (int)maxX, (int)maxY));
			*/
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			_path.reset();
			_isRubber = false;
			_isLine = false;
			invalidate();
			break;
		}
		return true;
	}
	
	/**
	 * 设置画板背景
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap)
	{
		_bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
		_canvas.setBitmap(_bitmap);
	}

	/**
	 * 设置橡皮檫大小
	 * @param size
	 */
	public void setClearPaintSize(int size)
	{
		_paint = _rubberPaint;
		_paint.setStrokeWidth(size * 2);
		_rubberRadius = size;
	}

	/**
	 * 设置画笔颜色
	 * @param color
	 */
	public void setDrawPaintColor(int color)
	{
		_paint = _linePaint;
		_paint.setColor(color);
	}

	/**
	 * 设置画笔大小
	 * @param size
	 */
	public void setDrawPaintSize(int size)
	{
		if (size < 3)
			size = 3;
		_paint = _linePaint;
		_paint.setStrokeWidth(size);
	}
	
	/**
	 * 返回图像
	 * @return
	 */
	public Bitmap getBitmap()
	{
		return _bitmap;
	}
}
