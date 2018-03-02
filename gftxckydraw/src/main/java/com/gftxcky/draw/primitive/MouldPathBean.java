package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.DashPathEffect;


public class MouldPathBean implements Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 8129948347102971474L;
	  private XCKYPoint centerPoint;
	  private boolean isChange;
	  private boolean isRotate = false;
	  private boolean isSelect;
	  private MatrixBean matrix;
	  private String mouldName;
	  private XCKYPath path;
	  private List<PathPointBean> pathList;
	  private XCKYPaint pathPaint;
	  private XCKYPaint rectPaint;
	  private RectBean rectobj;
	  private RectBean rightBottomRect;
	  private XCKYPoint rotatePoint;
	  

	  public MouldPathBean()
	  {
 	     this.pathList = new ArrayList<PathPointBean>();
	  }
	  
	  public MouldPathBean(MouldPathBean mouldpathbean)
	  {
		  isRotate = false;
		
		  //
		  this.pathList = new ArrayList<PathPointBean>();
		  for(PathPointBean path:mouldpathbean.pathList)
		  {
			  pathList.add(new PathPointBean(path));
		  }
		  
		  //
		  if(mouldpathbean.getMatrix()!=null)
			  this.matrix =new MatrixBean(mouldpathbean.getMatrix());
		  this.rectobj = new RectBean(mouldpathbean.getRectobj());
	      this.rightBottomRect = new RectBean(mouldpathbean.getRightBottomRect());
	      this.mouldName = mouldpathbean.getMouldName();
	      this.rotatePoint = new XCKYPoint(mouldpathbean.getRotatePoint());
	      this.centerPoint = new XCKYPoint(mouldpathbean.getCenterPoint());
	      this.path = new XCKYPath(mouldpathbean.getPath());
	      return;
	  }
	  
	  public XCKYPoint getCenterPoint()
	  {
	    return this.centerPoint;
	  }
	  
	  public MatrixBean getMatrix()
	  {
	    return this.matrix;
	  }
	  
	  public String getMouldName()
	  {
	    return this.mouldName;
	  }
	  
	  public XCKYPath getPath()
	  {
	    return this.path;
	  }
	  
	  public List<PathPointBean> getPathList()
	  {
	    return this.pathList;
	  }
	  
	  public XCKYPaint getPathPaint()
	  {
	    this.pathPaint = new XCKYPaint();
	    this.pathPaint.setStrokeWidth(2.0F);
	    this.pathPaint.setAntiAlias(true);
	    this.pathPaint.setColor(-16777216);
	    this.pathPaint.setStyle(XCKYPaint.Style.STROKE);
	    return this.pathPaint;
	  }
	  
	  public XCKYPaint getRectPaint()
	  {
	    this.rectPaint = new XCKYPaint();
	    this.rectPaint.setAntiAlias(true);
	    this.rectPaint.setStrokeWidth(2.0F);
	    this.rectPaint.setColor(-65536);
	    this.rectPaint.setStyle(XCKYPaint.Style.STROKE);
	    DashPathEffect localDashPathEffect = new DashPathEffect(new float[] { 5.0F, 5.0F, 5.0F, 5.0F }, 1.0F);
	    this.rectPaint.setPathEffect(localDashPathEffect);
	    return this.rectPaint;
	  }
	  
	  public RectBean getRectobj()
	  {
	    return this.rectobj;
	  }
	  
	  public RectBean getRightBottomRect()
	  {
	    return this.rightBottomRect;
	  }
	  
	  public XCKYPoint getRotatePoint()
	  {
	    if (!this.isRotate) {
	       this.rotatePoint = new XCKYPoint(this.centerPoint.x, (int) (getRectobj().top - 50.0F));
	    }
	    return this.rotatePoint;
	  }
	  
	  public boolean isChange()
	  {
	    return this.isChange;
	  }
	  
	  public boolean isRotate()
	  {
	    return this.isRotate;
	  }
	  
	  public boolean isSelect()
	  {
	    return this.isSelect;
	  }
	  
	  public void setCenterPoint(XCKYPoint point)
		{
			centerPoint = point;
		}

		public void setChange(boolean flag)
		{
			isChange = flag;
		}

		public void setMatrix(MatrixBean matrixbean)
		{
			if(matrix!=null)
			{
				matrix.postConcat(matrixbean);
			}
			else
			{
				matrix=new MatrixBean(matrixbean);
			}
		}

		public void setMouldName(String s)
		{
			mouldName = s;
		}

		public void setPath(XCKYPath path1)
		{
			path = path1;
		}

		public void setPathList(List<PathPointBean> list)
		{
			pathList = list;
		}

		public void setRectobj(RectBean rectbean)
		{
			rectobj = rectbean;
		}

		public void setRightBottomRect(RectBean rectbean)
		{
			rightBottomRect = rectbean;
		}

		public void setRotate(boolean flag)
		{
			isRotate = flag;
		}

		public void setRotatePoint(XCKYPoint point)
		{
			rotatePoint = point;
		}

		public void setSelect(boolean flag)
		{
			isSelect = flag;
		}
}
