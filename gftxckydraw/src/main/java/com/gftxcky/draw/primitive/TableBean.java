package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TableBean implements Serializable {
	private static final long serialVersionUID = -397210718135317938L;
	  private Calendar calendar = Calendar.getInstance();
	  private List<XCKYPoint[]> lines;
	  private XCKYPaint paintTab;
	  private XCKYPaint paintText;
	  private XCKYPaint paintTitle;
	  private RectBean tabRect;
	  private XCKYPoint titlePoint;
	  private String titleText;
	  private List<String[]> txts;
	  private String caseType;

	  public TableBean()
	  {
	    this.txts = new ArrayList<String[]>();
	    String[] fasj = { "案发时间", "" };
	    this.txts.add(fasj);
	    String[] fadz = { "案发地点", "" };
	    this.txts.add(fadz);
	    String[] ztdw = { "制图单位", "" };
	    this.txts.add(ztdw);
	    String[] ztr = { "制图人", "" };
	    this.txts.add(ztr);
	    String[] ztsj = { "制图时间", "" };
	    this.txts.add(ztsj);
	  }

	  public TableBean(TableBean tableBean)
	  {
		  lines=new ArrayList<XCKYPoint[]>();
		  for(XCKYPoint[] pts: this.getLines())
		  {
			  XCKYPoint[] newpts=new XCKYPoint[pts.length];
			  int index=0;
			  for(XCKYPoint point:pts)
			  {
				  newpts[index]=new XCKYPoint(point);
				  index++;
			  }
			  lines.add(newpts);
		  }
		  paintText=new XCKYPaint(tableBean.getPaintText());
		  paintTitle=new XCKYPaint(tableBean.getPaintTitle());
		  paintTab=new XCKYPaint(tableBean.getPaintTab());
		  tabRect=new RectBean(tableBean.getTabRect());
		  titlePoint=new XCKYPoint(tableBean.getTitlePoint());
		  titleText=new String(tableBean.titleText);
		  txts=new ArrayList<String[]>();
		  for(String[] strarray:tableBean.getTxts())
		  {
			  String[] newstr=new String[strarray.length];
			  int index=0;
			  for(String str:strarray)
			  {
				  newstr[index]=new String(str);
				  index++;
			  }
			  txts.add(newstr);
		  }
	  }

	  public List<XCKYPoint[]> getSetLines(int w,int h)
	  {
		w=w-2;
		h=h-2;
	    this.lines = new ArrayList<XCKYPoint[]> ();
	    XCKYPoint[] arrayOfPoint1 = new XCKYPoint[2];
	    arrayOfPoint1[0] = new XCKYPoint(w-215, h - 25 * getRowcount());
	    arrayOfPoint1[1] = new XCKYPoint(w-215, h);
	    this.lines.add(arrayOfPoint1);
	    XCKYPoint[] arrayOfPoint2 = new XCKYPoint[2];
	    arrayOfPoint2[0] = new XCKYPoint(w-295, -25 + this.lines.get(0)[1].y);
	    arrayOfPoint2[1] = new XCKYPoint(w, -25 + this.lines.get(0)[1].y);
	    this.lines.add(arrayOfPoint2);
	    XCKYPoint[] arrayOfPoint3 = new XCKYPoint[2];
	    arrayOfPoint3[0] = new XCKYPoint(this.lines.get(1)[0].x, -25 + this.lines.get(1)[0].y);
	    arrayOfPoint3[1] = new XCKYPoint(this.lines.get(1)[1].x, -25 + this.lines.get(1)[1].y);
        if (((String[])this.txts.get(1))[1].length() > 13)
        {
            arrayOfPoint3[0] = new XCKYPoint(arrayOfPoint3[0].x, -25 + arrayOfPoint3[0].y);
            arrayOfPoint3[1] = new XCKYPoint(arrayOfPoint3[1].x, -25 + arrayOfPoint3[1].y);
        }
	    this.lines.add(arrayOfPoint3);
	    XCKYPoint[] arrayOfPoint4 = new XCKYPoint[2];
	    arrayOfPoint4[0] = new XCKYPoint(this.lines.get(2)[0].x, -25 + this.lines.get(2)[0].y);
	    arrayOfPoint4[1] = new XCKYPoint(this.lines.get(2)[1].x, -25 + this.lines.get(2)[1].y);
	    if (((String[])this.txts.get(2))[1].length() > 13)
	    {
	      arrayOfPoint4[0] = new XCKYPoint(arrayOfPoint4[0].x, -25 + arrayOfPoint4[0].y);
	      arrayOfPoint4[1] = new XCKYPoint(arrayOfPoint4[1].x, -25 + arrayOfPoint4[1].y);
	    }
	    this.lines.add(arrayOfPoint4);
	    XCKYPoint[] arrayOfPoint5 = new XCKYPoint[2];
	    arrayOfPoint5[0] = new XCKYPoint(this.lines.get(3)[0].x, -25 + this.lines.get(3)[0].y);
	    arrayOfPoint5[1] = new XCKYPoint(this.lines.get(3)[1].x, -25 + this.lines.get(3)[1].y);
	    if (((String[])this.txts.get(3))[1].length() > 13)
	    {
	      arrayOfPoint5[0] = new XCKYPoint( arrayOfPoint5[0].x, -25 + arrayOfPoint5[0].y);
	      arrayOfPoint5[1] = new XCKYPoint( arrayOfPoint5[1].x, -25 + arrayOfPoint5[1].y);
	    }
	    this.lines.add(arrayOfPoint5);
	    return this.lines;
	  }
	  
	  public List<XCKYPoint[]> getLines()
	  {
	    return this.lines;
	  }

	  public XCKYPaint getPaintTab()
	  {
	    this.paintTab = new XCKYPaint();
	    this.paintTab.setStrokeWidth(2.0F);
	    this.paintTab.setStyle(XCKYPaint.Style.STROKE);
	    this.paintTab.setAntiAlias(true);
	    return this.paintTab;
	  }

	  public XCKYPaint getPaintText()
	  {
	    this.paintText = new XCKYPaint();
	    this.paintText.setTextSize(15.0F);
	    this.paintText.setAntiAlias(true);
	    return this.paintText;
	  }

	  public XCKYPaint getPaintTitle()
	  {
	    this.paintTitle = new XCKYPaint();
	    this.paintTitle.setTextSize(35.0F);
	    this.paintTitle.setAntiAlias(true);
	    return this.paintTitle;
	  }

	  public int getRowcount()
	  { 
		int index = 0;
		int row = 0;
		while (index<txts.size())
		{
			String as[] = (String[])txts.get(index);
			//第二三行算字数，大于13个字算两行
			if ((index == 1 || index == 2) && as[1].length() > 13)
				row += 2;
			else
				row++;
			index++;
		} 
		return row;
	  }

	  public RectBean getTabRect()
	  {
	    return this.tabRect;
	  }
	  
	  public RectBean getSetTabRect(int w,int h)
	  {
		w=w-2;
		h=h-2;
	    this.tabRect = new RectBean(w-295, h - 25 * getRowcount(), w, h);
	    return this.tabRect;
	  }

	  public XCKYPoint getTitlePoint()
	  {
	    return this.titlePoint;
	  }

	  public String getTitleText()
	  {
	    if (this.titleText == null)
	      return "\"" + this.calendar.get(1) + "年" + (this.calendar.get(2)+1) + "月" + this.calendar.get(5) + "日\" 平面示意图";
	    return this.titleText;
	  }

	  public List<String[]> getTxts()
	  {
	    return this.txts;
	  }

	  public void setFadz(String paramString)
	  {
	    ((String[])this.txts.get(1))[1] = paramString;
	  }

	  public void setFasj(String paramString)
	  {
	    ((String[])this.txts.get(0))[1] = paramString;
	  }

	  public void setLines(List<XCKYPoint[]> paramList)
	  {
	    this.lines = paramList;
	  }

	  public void setPaintTab(XCKYPaint paramPaint)
	  {
	    this.paintTab = paramPaint;
	  }

	  public void setPaintText(XCKYPaint paramPaint)
	  {
	    this.paintText = paramPaint;
	  }

	  public void setPaintTitle(XCKYPaint paramPaint)
	  {
	    this.paintTitle = paramPaint;
	  }

	  public void setTabRect(RectBean paramRectBean)
	  {
	    this.tabRect = paramRectBean;
	  }

	  public void setTitlePoint(XCKYPoint paramPoint)
	  {
	    this.titlePoint = paramPoint;
	  }

	  public void setTitleText(String paramString)
	  {
	    this.titleText = paramString;
	  }

	  public void setTxts(List<String[]> paramList)
	  {
	    this.txts = paramList;
	  }

	  public void setZtdw(String paramString)
	  {
	    ((String[])this.txts.get(2))[1] = paramString;
	  }

	  public void setZtr(String paramString)
	  {
	    ((String[])this.txts.get(3))[1] = paramString;
	  }

	  public void setZtsj(String paramString)
	  {
	    ((String[])this.txts.get(4))[1] = paramString;
	  }

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getCaseType(){
	  	return caseType;
	}
}
