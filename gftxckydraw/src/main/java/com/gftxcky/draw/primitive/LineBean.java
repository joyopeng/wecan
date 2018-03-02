package com.gftxcky.draw.primitive;

import java.io.Serializable;

import android.graphics.Color;

public class LineBean implements Serializable {
	private static final long serialVersionUID = 0x6bcd885ce55d4b16L;
	private XCKYPoint EndPoint;
	private RectBean LineRect;
	private XCKYPaint Paint=new XCKYPaint();
	private XCKYPoint StartPoint;
	private String equalPoint;
	private boolean isLock;
	private boolean isSelect;
	private String lineUid;

	public LineBean()
	{
		this.setLineUid(this.toString());
	}

	public LineBean(LineBean linebean)
	{
		if (linebean == null)
			return;
		StartPoint = new XCKYPoint(linebean.getStartPoint());
		EndPoint = new XCKYPoint(linebean.getEndPoint());
		if (linebean.getLineRect() != null)
			LineRect = new RectBean(linebean.getLineRect());
		isLock = linebean.isLock();
		Paint = new XCKYPaint(linebean.getPaint());
		isSelect = linebean.isSelect();
		lineUid=linebean.getLineUid();
	}

	public XCKYPoint getEndPoint()
	{
		return EndPoint;
	}

	public String getEqualPoint()
	{
		return equalPoint;
	}

	public RectBean getLineRect()
	{
		return LineRect;
	}

	public XCKYPaint getPaint()
	{
		if (isSelect)
		{
			if (Paint.getStrokeWidth() == 30F)
				Paint.setStrokeWidth(40F);
			if (Paint.getStrokeWidth() == 2.0F)
				Paint.setStrokeWidth(5F);
			Paint.setColor(Color.RED);
		}
		else
		{
			if (Paint.getStrokeWidth() == 35F)
				Paint.setStrokeWidth(40F);
			if (Paint.getStrokeWidth() == 5F)
				Paint.setStrokeWidth(2.0F);
			Paint.setColor(Color.BLACK);
		}
		return Paint;
	}
	

	public XCKYPoint getStartPoint()
	{
		return StartPoint;
	}

	public boolean isLock()
	{
		return isLock;
	}

	public boolean isSelect()
	{
		return isSelect;
	}

	public void setEndPoint(XCKYPoint point)
	{
		EndPoint = point;
	}

	public void setEqualPoint(String s)
	{
		equalPoint = s;
	}

	public void setLIneRect(RectBean rectbean)
	{
		LineRect = rectbean;
	}

	public void setLock(boolean flag)
	{
		isLock = flag;
	}

	public void setPaint(XCKYPaint paint)
	{
		Paint = paint;
	}


	public void setSelect(boolean flag)
	{
		isSelect = flag;
	}

	public void setStartPoint(XCKYPoint point)
	{
		StartPoint = point;
	}

	public String getLineUid() {
		return lineUid;
	}

	public void setLineUid(String lineUid) {
		this.lineUid = lineUid;
	}

}
