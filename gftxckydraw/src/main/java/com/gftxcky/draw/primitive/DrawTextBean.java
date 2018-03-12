package com.gftxcky.draw.primitive;

import android.graphics.Color;

import java.io.Serializable;
import java.util.List;


public class DrawTextBean implements Serializable {
	  private static final long serialVersionUID = 5190960024676433897L;
	  private boolean isInput;
	  private boolean isMove;
	  private boolean isSelect;
	  private List<XCKYPoint> lines;
	  private XCKYPoint point;
	  private String text;
	  private XCKYPaint textPaint;
	  private RectBean textRect;
	  private float textsize = 60;
	  public DrawTextBean()
	  {}



	public DrawTextBean(DrawTextBean paramDrawTextBean)
	  {
	    this.text = paramDrawTextBean.getText();
	    this.isMove = paramDrawTextBean.isMove();
	    this.isSelect = paramDrawTextBean.isSelect();
	    this.point = new XCKYPoint(paramDrawTextBean.getPoint());
	    this.textsize = paramDrawTextBean.getTextsize();
	  }

	  public  List<XCKYPoint> getLines()
	  {
	    return this.lines;
	  }


	  public String getText()
	  {
	    return this.text;
	  }

	  public XCKYPaint getTextPaint()
	  {
	    this.textPaint = new XCKYPaint();
	    this.textPaint.setStrokeWidth(2.0F);
	    this.textPaint.setAlpha(130);
	    this.textPaint.setAntiAlias(true);
	    this.textPaint.setDither(true);
	    if (this.isMove)
	      this.textPaint.setColor(-16711936);
	    else if (this.isInput)
	      this.textPaint.setColor(-16711936);
	    else if (this.isSelect)
	      this.textPaint.setColor(Color.RED);
		  this.textPaint.setTextSize(textsize);
	    return this.textPaint;
	  }

	  public RectBean getTextRect()
	  {
	    return this.textRect;
	  }

	  public boolean isInput()
	  {
	    return this.isInput;
	  }

	  public boolean isMove()
	  {
	    return this.isMove;
	  }

	  public boolean isSelect()
	  {
	    return this.isSelect;
	  }

	  public void setInput(boolean paramBoolean)
	  {
	    this.isInput = paramBoolean;
	  }

	  public void setLines( List<XCKYPoint> pointList)
	  {
	    this.lines = pointList;
	  }

	  public void setMove(boolean paramBoolean)
	  {
	    this.isMove = paramBoolean;
	  }


	  public XCKYPoint getPoint() {
			return point;
		}


		public void setPoint(XCKYPoint point) {
			this.point = point;
		}

	  public void setSelect(boolean paramBoolean)
	  {
	    this.isSelect = paramBoolean;
	  }

	  public void setText(String paramString)
	  {
	    if (paramString.length() > 10)
	      paramString = new StringBuffer(paramString).insert(10, "\r\n").toString();
	    this.text = paramString;
	  }

	  public void setTextPaint(XCKYPaint paramPaint)
	  {
	    this.textPaint = paramPaint;
	  }

	  public void setTextRect(RectBean paramRectBean)
	  {
	    this.textRect = paramRectBean;
	  }

	  public void setTextsize(float size){
		  textsize = size;
	  }

	  public float getTextsize(){
	  	return textsize;
	  }
}
