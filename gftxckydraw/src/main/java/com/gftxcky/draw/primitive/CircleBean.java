package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class CircleBean implements Serializable  {
	 
	private static final long serialVersionUID = -6745614708415269287L;
	  private float centerx;
	  private float centery;
	  private float radiusx;
	  private float radiusy;

	  public CircleBean()
	  {
	  }

	  public CircleBean(CircleBean paramCircleBean)
	  {
	    this.centerx = paramCircleBean.getCenterx();
	    this.centery = paramCircleBean.getCentery();
	    this.radiusx = paramCircleBean.getRadiusx();
	    this.radiusy = paramCircleBean.getRadiusy();
	  }

	  public float getCenterx()
	  {
	    return this.centerx;
	  }

	  public float getCentery()
	  {
	    return this.centery;
	  }

	  public float getRadiusx()
	  {
	    return this.radiusx;
	  }

	  public float getRadiusy()
	  {
	    return this.radiusy;
	  }

	  public void setCenterx(float x)
	  {
	    this.centerx = x;
	  }

	  public void setCentery(float y)
	  {
	    this.centery = y;
	  }

	  public void setRadiusx(float radiusx)
	  {
	    this.radiusx = radiusx;
	  }

	  public void setRadiusy(float radiusy)
	  {
	    this.radiusy = radiusy;
	  }
}
