package com.gftxcky.draw.primitive;

import java.io.Serializable;

public class DirectionBean implements Serializable {
	 private static final long serialVersionUID = -3654032151347521866L;
	  private XCKYPoint EndPoint;
	  private XCKYPoint StartPoint;
	  
	  public DirectionBean()
	  {
		  
	  }
	  
	  public DirectionBean(DirectionBean direction)
	  {
		  this.EndPoint=new XCKYPoint(direction.getEndPoint());
		  this.StartPoint=new XCKYPoint(direction.getStartPoint());
	  }

	  public XCKYPoint getEndPoint()
	  {
	    return this.EndPoint;
	  }

	  public XCKYPoint getStartPoint()
	  {
	    return this.StartPoint;
	  }

	  public void setEndPoint(XCKYPoint endPoint)
	  {
	    this.EndPoint = endPoint;
	  }

	  public void setStartPoint(XCKYPoint startPoint)
	  {
	    this.StartPoint = startPoint;
	  }
}
