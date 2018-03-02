package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WindowBean implements Serializable {
	  private static final long serialVersionUID = 4979281110249822260L;
	  private XCKYPoint LineEndPoint;
	  private XCKYPoint LineStartPoint;
	  private String LineUid;
	  private XCKYPoint endPoint;
	  private boolean isChange;
	  private boolean isLock;
	  private boolean isSelect;
	  private XCKYPaint paint;
	  private XCKYPaint pathPaint;
	  private XCKYPoint startPoint;
	  private List<PathPointBean> windowpath = new ArrayList<PathPointBean>();

	  public WindowBean()
	  {
	  }

	  public WindowBean(WindowBean paramWindowBean)
	  {
	    for (int i = 0; ; i++)
	    {
	      if (i >= paramWindowBean.getWindowPath().size())
	      {
	        this.paint = new XCKYPaint(paramWindowBean.getPaint());
	        this.startPoint = new XCKYPoint(paramWindowBean.getStartPoint());
	        this.endPoint = new XCKYPoint(paramWindowBean.getEndPoint());
	        this.isLock = paramWindowBean.isLock();
	        this.isSelect = paramWindowBean.isSelect();
	        this.LineEndPoint = new XCKYPoint(paramWindowBean.getLineEndPoint());
	        this.LineStartPoint = new XCKYPoint(paramWindowBean.getLineStartPoint());
	        this.LineUid=paramWindowBean.LineUid;
	        return;
	      }
	      this.windowpath.add(new PathPointBean((PathPointBean)paramWindowBean.getWindowPath().get(i)));
	    }
	  }


	  public XCKYPoint getLineEndPoint() {
		return LineEndPoint;
	}


	public XCKYPoint getLineStartPoint() {
		return LineStartPoint;
	}


	public XCKYPoint getEndPoint() {
		return endPoint;
	}


	public XCKYPaint getPaint()
	  {
		  XCKYPaint localPaint = new XCKYPaint();
	    localPaint.setDither(true);
	    localPaint.setStyle(XCKYPaint.Style.STROKE);
	    localPaint.setAntiAlias(true);
	    localPaint.setStrokeCap(XCKYPaint.Cap.SQUARE);
	    localPaint.setStrokeWidth(1.0F);
	    if (this.isSelect)
	      localPaint.setColor(-16711936);
	    else
	    {      
	      localPaint.setColor(-16777216);
	    }
	    this.paint = localPaint;
	    return this.paint;
	  }

	  public XCKYPaint getPathPaint()
	  {
		  XCKYPaint localPaint = new XCKYPaint();
	    localPaint.setDither(true);
	    localPaint.setAntiAlias(true);
	    localPaint.setStrokeCap(XCKYPaint.Cap.SQUARE);
	    localPaint.setStrokeWidth(30.0F);
	    localPaint.setColor(-1);
	    this.pathPaint = localPaint;
	    return this.pathPaint;
	  }

	 

	  public XCKYPoint getStartPoint() {
		return startPoint;
	}

	public List<PathPointBean> getWindowPath()
	  {
	    return this.windowpath;
	  }

	  public boolean isChange()
	  {
	    return this.isChange;
	  }

	  public boolean isLock()
	  {
	    return this.isLock;
	  }

	  public boolean isSelect()
	  {
	    return this.isSelect;
	  }

	  public void setChange(boolean paramBoolean)
	  {
	    this.isChange = paramBoolean;
	  }

	  public void setEndPoint(XCKYPoint paramPoint)
	  {
	    this.endPoint = paramPoint;
	  }

	  public void setLineEndPoint(XCKYPoint paramPoint)
	  {
	    this.LineEndPoint = paramPoint;
	  }

	  public void setLineStartPoint(XCKYPoint paramPoint)
	  {
	    this.LineStartPoint = paramPoint;
	  }

	  public void setLock(boolean paramBoolean)
	  {
	    this.isLock = paramBoolean;
	  }

	  public void setSelect(boolean paramBoolean)
	  {
	    this.isSelect = paramBoolean;
	  }

	  public void setStartPoint(XCKYPoint paramPoint)
	  {
	    this.startPoint = paramPoint;
	  }

	  public void setWindowpath(PathPointBean paramPathPointBean)
	  {
	    this.windowpath.add(paramPathPointBean);
	  }

	public String getLineUid() {
		return LineUid;
	}

	public void setLineUid(String lineUid) {
		LineUid = lineUid;
	}
}
