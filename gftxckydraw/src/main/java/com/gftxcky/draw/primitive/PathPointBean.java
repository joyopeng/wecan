package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PathPointBean implements Serializable {
	private static final long serialVersionUID = -4268192375464231122L;
	  private XCKYPoint StartPoint;
	  private boolean isClose = false;
	  private List<XCKYPoint> pointList;

	  public PathPointBean()
	  {
	    this.pointList = new ArrayList<XCKYPoint>();
	  }

	  public PathPointBean(PathPointBean pathPointBean)
	  {
	    this.StartPoint = new XCKYPoint(pathPointBean.getStartPoint());
	    this.pointList = new ArrayList<XCKYPoint>();
	    for(XCKYPoint point:pathPointBean.getPointList())
	    {
	    	 this.pointList.add(new XCKYPoint(point));
	    }
	    this.isClose = pathPointBean.isClose;
	    return;
	  }

	  public List<XCKYPoint> getPointList()
	  {
	    return this.pointList;
	  }

	  public XCKYPoint getStartPoint()
	  {
	    return this.StartPoint;
	  }

	  public boolean isClose()
	  {
	    return this.isClose;
	  }

	  public void setClose(boolean isclose)
	  {
	    this.isClose = isclose;
	  }

	  public void setPointList(float x, float y)
	  {
	    this.pointList.add(new XCKYPoint(x, y));
	  }
	  
	  public void setPointList(XCKYPoint point)
	  {
	    this.pointList.add(point);
	  }

	  public void setStartPoint(float x, float y)
	  {
	    this.StartPoint = new XCKYPoint(x,y);
	  }
	  
	  public void setStartPoint(XCKYPoint point)
	  {
	    this.StartPoint = point;
	  }
}
