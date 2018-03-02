package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BezierBean implements Serializable {

	private static final long serialVersionUID = 3713860976118463141L;
	private List<XCKYPoint> pointList = new ArrayList<XCKYPoint>();

	  public BezierBean()
	  {
	  }

	  public BezierBean(BezierBean paramBezierBean)
	  {
		  for (XCKYPoint point:paramBezierBean.getPointList())
		  {
			  	this.setPointList(new XCKYPoint(point));
		  }
	  }

	  public List<XCKYPoint> getPointList()
	  {
	    return this.pointList;
	  }

	  public void setPointList(XCKYPoint paramPoint)
	  {
	    this.pointList.add(paramPoint);
	  }
}
