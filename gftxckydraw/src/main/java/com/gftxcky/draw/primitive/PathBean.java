package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.DashPathEffect;

public class PathBean implements Serializable {
	private static final long serialVersionUID = 5025912599495501782L;
	  private XCKYPaint CirclePaint;
	  private boolean isChange;
	  private boolean isLock;
	  private boolean isSelect;
	  private List<LineBean> lineBeans;
	  private XCKYPaint paint;
	  private PathPointBean path;

	  public PathBean()
	  {
	  }

	  public PathBean(PathBean paramPathBean)
	  {
	    this.path = new PathPointBean(paramPathBean.getPath());
	    this.paint = new XCKYPaint(paramPathBean.getPaint());
	    this.lineBeans = new ArrayList<LineBean>();
	    for (int i = 0; ; i++)
	    {
	      if (i >= paramPathBean.getLineBeans().size())
	      {
	        this.isChange = paramPathBean.isChange();
	        this.isLock = paramPathBean.isLock();
	        this.isSelect = paramPathBean.isSelect();
	        return;
	      }
	      this.lineBeans.add(new LineBean((LineBean)paramPathBean.getLineBeans().get(i)));
	    }
	  }

	  public XCKYPaint getCirclePaint()
	  {
		  XCKYPaint localPaint = new XCKYPaint();
	    localPaint.setStrokeWidth(1.0F);
	    localPaint.setColor(-16777216);
	    localPaint.setStyle(XCKYPaint.Style.STROKE);
	    localPaint.setAntiAlias(true);
	    localPaint.setPathEffect(new DashPathEffect(new float[] { 5.0F, 5.0F, 5.0F, 5.0F }, 1.0F));
	    this.CirclePaint = localPaint;
	    return this.CirclePaint;
	  }

	  public List<LineBean> getLineBeans()
	  {
	    return this.lineBeans;
	  }

	  public XCKYPaint getPaint()
	  {
	    if (this.isSelect)
	      this.paint.setColor(-65536);
	    else
	    {
	      this.paint.setColor(-16777216);
	    }
	    return this.paint;
	  }

	  public PathPointBean getPath()
	  {
	    return this.path;
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

	  public void setCirclePaint(XCKYPaint paramPaint)
	  {
	    this.CirclePaint = paramPaint;
	  }

	  public void setLineBeans(List<LineBean> lineList)
	  {
	    this.lineBeans = lineList;
	  }

	  public void setLock(boolean paramBoolean)
	  {
	    this.isLock = paramBoolean;
	  }

	  public void setPaint(XCKYPaint paramPaint)
	  {
	    this.paint = paramPaint;
	  }

	  public void setPath(PathPointBean paramPathPointBean)
	  {
	    this.path = paramPathPointBean;
	  }

	  public void setSelect(boolean paramBoolean)
	  {
	    this.isSelect = paramBoolean;
	  }
}
