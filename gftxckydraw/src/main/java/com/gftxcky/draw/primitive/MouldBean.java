package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.DashPathEffect;

public class MouldBean implements Serializable {
	 private static final long serialVersionUID = -8586826777887509106L;
	  private List<BezierBean> BezierList;
	  private List<CircleBean> CircleList;
	  private List<DirectionBean> DirectionList;
	  private List<EllipseBean> EllipseList;
	  private List<LineBean> LineList;
	  private List<PathPointBean> PolyList;
	  private List<RectBean> RectList = new ArrayList<RectBean>();
	  private List<TextBean> TextList;
	  private XCKYPoint centerPoint;
	  private boolean isChange;
	  private boolean isRotate = false;
	  private boolean isSelect;
	  private MatrixBean matrix;
	  private XCKYPath path;
	  private XCKYPaint pathPaint;
	  private XCKYPaint rectPaint;
	  private RectBean rectobj;
	  private RectBean rightBottomRect;
	  private XCKYPoint rotatePoint;
	  private XCKYPaint textPaint;
	  private int textSize;

	  public MouldBean()
	  {
	    this.TextList = new ArrayList<TextBean>();
	    this.BezierList = new ArrayList<BezierBean>();
	    this.LineList = new ArrayList<LineBean>();
	    this.DirectionList = new ArrayList<DirectionBean>();
	    this.CircleList = new ArrayList<CircleBean>();
	    this.PolyList = new ArrayList<PathPointBean>();
	    this.EllipseList = new ArrayList<EllipseBean>();
	  }

	  public MouldBean(MouldBean paramMouldBean)
	  {
	    //RectBean
		this.RectList=new ArrayList<RectBean>();
	    for (RectBean rect:paramMouldBean.getRectList())
	    {
	    	this.RectList.add(new RectBean(rect));
	    }

	    //Text
	    this.TextList = new ArrayList<TextBean>();
	    for (TextBean text:paramMouldBean.getTextList())
	    {
	    	this.TextList.add(new TextBean(text));
	    }
	    
	    //Bezier
	    this.BezierList = new ArrayList<BezierBean>();
	    for (BezierBean bezier: paramMouldBean.getBezierList())
	    {
    	   this.BezierList.add(new BezierBean(bezier));
	    }
	    
	    //line
	    this.LineList = new ArrayList<LineBean>();
	    for (LineBean line:paramMouldBean.getLineList())
	    {
    	    this.LineList.add(new LineBean(line));
	    }
	    
	    //Direction
	    this.DirectionList = new ArrayList<DirectionBean>();
	    for (DirectionBean direction:paramMouldBean.getDirectionList())
	    {
    	    this.DirectionList.add(new DirectionBean(direction));
	    }
	    
	    //Circle
	    this.CircleList = new ArrayList<CircleBean>();
	    for (CircleBean circle:paramMouldBean.getCircleList())
	    {
    		this.CircleList.add(new CircleBean(circle));
	    }
	    
	    //Poly
	    this.PolyList = new ArrayList<PathPointBean>();
	    for (PathPointBean pathpoint:paramMouldBean.getPolyList())
	    {
      		this.PolyList.add(new PathPointBean(pathpoint));
	    }
	    
	    //Ellipse
	    this.EllipseList = new ArrayList<EllipseBean>();
	    for (EllipseBean ellipse: paramMouldBean.getEllipseList())
	    {
        	this.EllipseList.add(new EllipseBean(ellipse));
	    }
	    
	    this.rectobj = new RectBean(paramMouldBean.getRectobj());
	    this.rightBottomRect = new RectBean(paramMouldBean.getRightBottomRect());
	    this.rotatePoint = new XCKYPoint(paramMouldBean.getRotatePoint());
	    this.centerPoint = new XCKYPoint(paramMouldBean.getCenterPoint());
	    this.isSelect = paramMouldBean.isSelect();
	    this.isChange = paramMouldBean.isChange();
	    
	    //matrix
	    if(paramMouldBean.getMatrix()!=null)
	    	this.matrix = new MatrixBean(paramMouldBean.getMatrix());   
	    this.textSize = paramMouldBean.getTextSize();
        this.path = new XCKYPath(paramMouldBean.getPath());
        return;
	  }

	  public List<BezierBean> getBezierList()
	  {
	    return this.BezierList;
	  }

	  public XCKYPoint getCenterPoint()
	  {
	    return this.centerPoint;
	  }

	  public List<CircleBean> getCircleList()
	  {
	    return this.CircleList;
	  }

	  public List<DirectionBean> getDirectionList()
	  {
	    return this.DirectionList;
	  }

	  public List<EllipseBean> getEllipseList()
	  {
	    return this.EllipseList;
	  }

	  public List<LineBean> getLineList()
	  {
	    return this.LineList;
	  }

	  public MatrixBean getMatrix()
	  {
	    return this.matrix;
	  }

	  public XCKYPath getPath()
	  {
	    return this.path;
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

	  public List<PathPointBean> getPolyList()
	  {
	    return this.PolyList;
	  }

	  public List<RectBean> getRectList()
	  {
	    return this.RectList;
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
	    if (!this.isRotate)
	      this.rotatePoint = new XCKYPoint(this.centerPoint.x, getRectobj().top - 50.0F);
	    return this.rotatePoint;
	  }

	  public List<TextBean> getTextList()
	  {
	    return this.TextList;
	  }

	  public XCKYPaint getTextPaint()
	  {
	    this.textPaint = new XCKYPaint();
	    this.textPaint.setAntiAlias(true);
	    this.textPaint.setColor(-16777216);
	    this.textPaint.setTextSize(getTextSize());
	    return this.textPaint;
	  }

	  public int getTextSize()
	  {
	    return this.textSize;
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

	  public void setBezierList(BezierBean paramBezierBean)
	  {
	    this.BezierList.add(paramBezierBean);
	  }

	  public void setCenterPoint(XCKYPoint paramPoint)
	  {
	    this.centerPoint = paramPoint;
	  }

	  public void setChange(boolean paramBoolean)
	  {
	    this.isChange = paramBoolean;
	  }

	  public void setCircleList(CircleBean paramCircleBean)
	  {
	    this.CircleList.add(paramCircleBean);
	  }

	  public void setDirectionList(DirectionBean paramDirectionBean)
	  {
	    this.DirectionList.add(paramDirectionBean);
	  }

	  public void setEllipseList(EllipseBean paramEllipseBean)
	  {
	    this.EllipseList.add(paramEllipseBean);
	  }

	  public void setLineList(LineBean paramLineBean)
	  {
	    this.LineList.add(paramLineBean);
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

	  public void setPath(XCKYPath paramPath)
	  {
	    this.path = paramPath;
	  }

	  public void setPolyList(PathPointBean paramPathPointBean)
	  {
	    this.PolyList.add(paramPathPointBean);
	  }

	  public void setRectList(RectBean paramRectBean)
	  {
	    this.RectList.add(paramRectBean);
	  }

	  public void setRectobj(RectBean paramRectBean)
	  {
	    this.rectobj = paramRectBean;
	  }

	  public void setRightBottomRect(RectBean paramRectBean)
	  {
	    this.rightBottomRect = paramRectBean;
	  }

	  public void setRotate(boolean paramBoolean)
	  {
	    this.isRotate = paramBoolean;
	  }

	  public void setRotatePoint(XCKYPoint paramPoint)
	  {
	    this.rotatePoint = paramPoint;
	  }

	  public void setSelect(boolean paramBoolean)
	  {
	    this.isSelect = paramBoolean;
	  }

	  public void setTextList(TextBean paramTextBean)
	  {
	    this.TextList.add(paramTextBean);
	  }

	  public void setTextSize(int paramInt)
	  {
	    this.textSize = paramInt;
	  }
}
