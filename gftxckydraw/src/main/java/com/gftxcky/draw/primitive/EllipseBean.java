package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class EllipseBean implements Serializable {
	private static final long serialVersionUID = 722602142026747719L;
	  private float rectfx;
	  private float rectfx1;
	  private float rectfy;
	  private float rectfy1;
	  
	  public EllipseBean()
	  {
		  
	  }
	  
	  public EllipseBean(EllipseBean ellipse)
	  {
		  this.rectfx=ellipse.rectfx;
		  this.rectfx1=ellipse.rectfx1;
		  this.rectfy=ellipse.rectfy;
		  this.rectfy1=ellipse.rectfy1;
	  }

	  public float getRectfx()
	  {
	    return this.rectfx;
	  }

	  public float getRectfx1()
	  {
	    return this.rectfx1;
	  }

	  public float getRectfy()
	  {
	    return this.rectfy;
	  }

	  public float getRectfy1()
	  {
	    return this.rectfy1;
	  }

	  public void setRectfx(float paramFloat)
	  {
	    this.rectfx = paramFloat;
	  }

	  public void setRectfx1(float paramFloat)
	  {
	    this.rectfx1 = paramFloat;
	  }

	  public void setRectfy(float paramFloat)
	  {
	    this.rectfy = paramFloat;
	  }

	  public void setRectfy1(float paramFloat)
	  {
	    this.rectfy1 = paramFloat;
	  }
}
