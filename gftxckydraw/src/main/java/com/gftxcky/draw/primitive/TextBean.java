package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class TextBean implements Serializable {
	  private static final long serialVersionUID = -4933390556250731777L;
	  private int size;
	  private String text;
	  private float x;
	  private float y;

	  public TextBean()
	  {
	  }

	  public TextBean(TextBean paramTextBean)
	  {
	    this.x = paramTextBean.x;
	    this.y = paramTextBean.y;
	    this.size = paramTextBean.size;
	    this.text = paramTextBean.text;
	  }

	  public int getSize()
	  {
	    return this.size;
	  }

	  public String getText()
	  {
	    return this.text;
	  }

	  public float getX()
	  {
	    return this.x;
	  }

	  public float getY()
	  {
	    return this.y;
	  }

	  public void setSize(int paramInt)
	  {
	    this.size = paramInt;
	  }

	  public void setText(String paramString)
	  {
	    this.text = paramString;
	  }

	  public void setX(float paramFloat)
	  {
	    this.x = paramFloat;
	  }

	  public void setY(float paramFloat)
	  {
	    this.y = paramFloat;
	  }
}
