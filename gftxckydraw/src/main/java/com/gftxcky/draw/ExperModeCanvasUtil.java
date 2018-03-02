package com.gftxcky.draw;

import com.gftxcky.draw.primitive.PaintType;
import com.gftxcky.draw.primitive.PathBean;
import com.gftxcky.draw.primitive.XCKYPaint;

import android.graphics.Color;
import android.graphics.DashPathEffect;


public class ExperModeCanvasUtil {
	  /**
	   * 绘画类型
	   */
	  private static int _paintType = -1;
	  private static int _config = -1;
	  private static PathBean _path;

	  public static int paintType(){return _paintType;}
	  public static int config(){return _config;}
	  public static PathBean path(){return _path;}
	  
	
	/**
	 * 得到画笔
	 * @param paintType
	 * @param paramInt2
	 * @return
	 */
	public static XCKYPaint getPaint(int paintType, int config){
		 if(config!=-1)
		  {
		     _config = 3;
		     return getPaint(paintType);
		  }
		  _config = 1;
		  XCKYPaint localPaint = defaultPaint();
		  _path = null;
		  return localPaint;
	}
	
	
	/**
	 * 得到画笔
	 * @param paintType
	 * @return
	 */
	private static XCKYPaint getPaint(int paintType){
		XCKYPaint paint = new XCKYPaint();
		paint.setDither(true);
		paint.setStyle(XCKYPaint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStrokeCap(XCKYPaint.Cap.ROUND);
		paint.setStrokeJoin(XCKYPaint.Join.ROUND);
	    if (paintType == PaintType.StraightLine || paintType == PaintType.ObliqueLine || paintType == PaintType.PolygonLine || paintType == PaintType.Window || paintType == PaintType.Door)
	    {
	    	paint.setStrokeWidth(2.0F);
	    }
	    else
	    {
	    	paint.setStrokeWidth(20.0F);
	    }
	    if (paintType == PaintType.PolygonLine || paintType == PaintType.PolygonWall)
        {
	    	_path = new PathBean();
	    	_path.setPaint(paint);
        }
	    if  (paintType ==  PaintType.RectWall)
        {
	    	paint.setStrokeCap(XCKYPaint.Cap.ROUND);
	    	_path = new PathBean();
	    	_path.setPaint(paint);
        }
        _paintType = paintType;
	    return paint;
	}
	
	
	/**
	 * 设置画选择框画笔
	 * @return
	 */
	 private static XCKYPaint defaultPaint()
	  {
		XCKYPaint localPaint = new XCKYPaint();
	    localPaint.setStyle(XCKYPaint.Style.STROKE);
	    localPaint.setAntiAlias(true);
	    localPaint.setColor(Color.RED);
	    localPaint.setStrokeCap(XCKYPaint.Cap.SQUARE);
	    localPaint.setStrokeWidth(1.0F);
	    localPaint.setPathEffect(new DashPathEffect(new float[] { 5.0F, 5.0F, 5.0F, 5.0F }, 1.0F));
	    _paintType = PaintType.None;
	    return localPaint;
	  }
}
