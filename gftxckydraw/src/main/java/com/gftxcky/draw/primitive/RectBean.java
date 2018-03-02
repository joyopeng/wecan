package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class RectBean implements Serializable {
	private static final long serialVersionUID = 46189965602253357L;
	  public int blue;
	  public int green;
	  public int red;
	  public float left;
	  public float right;
	  public float top;
	  public float bottom;

	  public RectBean()
	  {
	  }

	  public RectBean(float left, float top, float right, float bottom)
	  {
	    this.left = left;
	    this.top = top;
	    this.right = right;
	    this.bottom = bottom;
	  }

	  public RectBean(RectBean paramRectBean)
	  {
	    this.left = paramRectBean.left;
	    this.top = paramRectBean.top;
	    this.right = paramRectBean.right;
	    this.bottom = paramRectBean.bottom;
	  }

	  public int getBlue()
	  {
	    return this.blue;
	  }

	  public float getBottom()
	  {
	    return this.bottom;
	  }

	  public int getGreen()
	  {
	    return this.green;
	  }

	  public float getLeft()
	  {
	    return this.left;
	  }

	  public int getRed()
	  {
	    return this.red;
	  }

	  public float getRight()
	  {
	    return this.right;
	  }

	  public float getTop()
	  {
	    return this.top;
	  }

	  public void setBlue(int paramInt)
	  {
	    this.blue = paramInt;
	  }

	  public void setBottom(float paramFloat)
	  {
	    this.bottom = paramFloat;
	  }

	  public void setGreen(int paramInt)
	  {
	    this.green = paramInt;
	  }

	  public void setLeft(float paramFloat)
	  {
	    this.left = paramFloat;
	  }

	  public void setRed(int paramInt)
	  {
	    this.red = paramInt;
	  }

	  public void setRight(float paramFloat)
	  {
	    this.right = paramFloat;
	  }

	  public void setTop(float paramFloat)
	  {
	    this.top = paramFloat;
	  }
}
