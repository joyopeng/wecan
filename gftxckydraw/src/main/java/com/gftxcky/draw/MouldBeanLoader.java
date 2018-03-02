package com.gftxcky.draw;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.gftxcky.draw.primitive.BezierBean;
import com.gftxcky.draw.primitive.CircleBean;
import com.gftxcky.draw.primitive.DirectionBean;
import com.gftxcky.draw.primitive.EllipseBean;
import com.gftxcky.draw.primitive.LineBean;
import com.gftxcky.draw.primitive.MouldBean;
import com.gftxcky.draw.primitive.PathDataBean;
import com.gftxcky.draw.primitive.PathPointBean;
import com.gftxcky.draw.primitive.RectBean;
import com.gftxcky.draw.primitive.TextBean;
import com.gftxcky.draw.primitive.XCKYPoint;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class MouldBeanLoader {
	private Context _main;

	public MouldBeanLoader(Context main)
	{
		_main = main;
	}
	
	/**
	 * 读取模型数据
	 * @param name
	 * @return
	 */
	public MouldBean loadMouldBean(String name)
	{
		String type="",borderColor="";
		float x=0f,y=0f;
		RectBean rectbean;
		MouldBean mouldbean=new MouldBean();
		XmlPullParser xmlpullparser;
		try
		{
			xmlpullparser = Xml.newPullParser();
			xmlpullparser.setInput(_main.getAssets().open((new StringBuilder("mod/")).append(name).append(".mod").toString()), "UTF-8");
			int eventType=xmlpullparser.getEventType();
			while(true)
			{
				switch(eventType)
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.END_DOCUMENT:
						return mouldbean;
					case  XmlPullParser.START_TAG:
						//读元素类型
						if (xmlpullparser.getName().equals("Element")){
							if(xmlpullparser.getAttributeValue(0).equals("RECT"))
							{
								type = xmlpullparser.getAttributeValue(0);
								borderColor = xmlpullparser.getAttributeValue(4);
								rectbean = new RectBean();
								rectbean.setRed(Integer.parseInt(borderColor.split(",")[0]));
								rectbean.setGreen(Integer.parseInt(borderColor.split(",")[1]));
								rectbean.setBlue(Integer.parseInt(borderColor.split(",")[2]));
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setRectList(rectbean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("TEXT"))
							{
								type = xmlpullparser.getAttributeValue(0);
								TextBean textbean = new TextBean();
								textbean.setSize(Integer.parseInt(xmlpullparser.getAttributeValue(5)));
								textbean.setText(xmlpullparser.getAttributeValue(6));
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setTextList(textbean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("BEZIER"))
							{
								type = xmlpullparser.getAttributeValue(0);
								BezierBean bezierbean = new BezierBean();
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setBezierList(bezierbean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("LINE"))
							{
								type = xmlpullparser.getAttributeValue(0);	
								LineBean linebean = new LineBean();
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setLineList(linebean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("DIRECTION"))
							{
								type = xmlpullparser.getAttributeValue(0);	
								DirectionBean directionbean = new DirectionBean();
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setDirectionList(directionbean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("CIRCLE"))
							{
								type = xmlpullparser.getAttributeValue(0);	
								CircleBean circlebean = new CircleBean();
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setCircleList(circlebean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("POLY"))
							{
								type = xmlpullparser.getAttributeValue(0);	
								PathPointBean pathpointbean = new PathPointBean();
								pathpointbean.setClose(Boolean.parseBoolean(xmlpullparser.getAttributeValue(5)));
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setPolyList(pathpointbean);
							}
							else if(xmlpullparser.getAttributeValue(0).equals("ELLIPSE"))
							{
								type = xmlpullparser.getAttributeValue(0);	
								EllipseBean ellipsebean = new EllipseBean();
								x = Float.parseFloat(xmlpullparser.getAttributeValue(1));
								y = Float.parseFloat(xmlpullparser.getAttributeValue(2));
								mouldbean.setEllipseList(ellipsebean);
							}
						} //读路径数据
						else if (xmlpullparser.getName().equals("Point")){
							if (type.equals("RECT"))
							{
								rectbean = mouldbean.getRectList().get(mouldbean.getRectList().size()-1);
								if (rectbean.left == 0.0F) {
									rectbean.setLeft(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									rectbean.setTop(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
								else if (rectbean.right == 0.0F) {
									rectbean.setRight(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									rectbean.setBottom(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
							}
							else if (type.equals("TEXT"))
							{
								TextBean textbean = mouldbean.getTextList().get(mouldbean.getTextList().size()-1);
								textbean.setX(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
								textbean.setY(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
							}
							else if (type.equals("BEZIER"))
							{
								BezierBean bezierbean=mouldbean.getBezierList().get(mouldbean.getBezierList().size()-1);	
								bezierbean.setPointList(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
							}
							else if (type.equals("LINE"))
							{
								LineBean linebean = mouldbean.getLineList().get(mouldbean.getLineList().size()-1);
								if (linebean.getStartPoint() == null)
								{
									linebean.setStartPoint(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
								else if (linebean.getEndPoint() == null)
								{
									linebean.setEndPoint(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
							}
							else if (type.equals("DIRECTION"))
							{
								DirectionBean directionbean = mouldbean.getDirectionList().get( mouldbean.getDirectionList().size()-1);
								if (directionbean.getStartPoint() == null)
								{
									directionbean.setStartPoint(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
								else if (directionbean.getEndPoint() == null)
								{
									directionbean.setEndPoint(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
							}
							else if (type.equals("CIRCLE"))
							{
								CircleBean circlebean = mouldbean.getCircleList().get(mouldbean.getCircleList().size()-1);
								if (circlebean.getCenterx() == 0.0F && circlebean.getCentery() == 0.0F)
								{
									circlebean.setCenterx(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									circlebean.setCentery(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
								else
								{
									circlebean.setRadiusx(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									circlebean.setRadiusy(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
							}
							else if (type.equals("POLY"))
							{
								PathPointBean pathpointbean = mouldbean.getPolyList().get(mouldbean.getPolyList().size()-1);
								if (pathpointbean.getStartPoint() == null)
								{
									pathpointbean.setStartPoint(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
								else
								{
									pathpointbean.setPointList(new XCKYPoint(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)), y + Float.parseFloat(xmlpullparser.getAttributeValue(1))));
								}
							}
							else if (type.equals("ELLIPSE"))
							{
								EllipseBean ellipsebean=mouldbean.getEllipseList().get( mouldbean.getEllipseList().size()-1);
								if (ellipsebean.getRectfx() == 0.0F && ellipsebean.getRectfy() == 0.0F)
								{
									ellipsebean.setRectfx(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									ellipsebean.setRectfy(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
								else
								{
									ellipsebean.setRectfx1(x + Float.parseFloat(xmlpullparser.getAttributeValue(0)));
									ellipsebean.setRectfy1(y + Float.parseFloat(xmlpullparser.getAttributeValue(1)));
								}
							}
						}
						break;
				}
				eventType=xmlpullparser.next();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			return mouldbean;
		}
	}

	/**
	 * 读取MAYA格式数据
	 * @param name
	 * @param dname
	 * @return
	 */
	public List<PathDataBean> loadMAYAMouldBean(String name, String dname)
	{
		List<PathDataBean> pathList=null;
		PathDataBean pathdatabean=null;
		try
		{
			XmlPullParser xmlpullparser = Xml.newPullParser();
			xmlpullparser.setInput(_main.getAssets().open((new StringBuilder("mod/")).append(name).append(".xml").toString()), "UTF-8");
			int eventType=xmlpullparser.getEventType();
			while(true)
			{
				switch(eventType)
				{
					case XmlPullParser.START_DOCUMENT:
						Log.d("test","start doc");
						pathList=new ArrayList<PathDataBean>();
						break;
					case XmlPullParser.END_DOCUMENT:
						Log.d("test","end doc");
						return pathList;
					case  XmlPullParser.START_TAG:
						if (xmlpullparser.getName().equals("geometry"))
						{
							pathdatabean = new PathDataBean();
							pathdatabean.setName(dname);
							pathList.add(pathdatabean);
						}
						else if (xmlpullparser.getName().equals("float_array"))
						{
							xmlpullparser.next();
							pathdatabean.setXYStr(xmlpullparser.getText());
						}
						else if (xmlpullparser.getName().equals("p"))
						{
							xmlpullparser.next();
							pathdatabean.setIndexStr(xmlpullparser.getText());
						}
						break;
				}
				eventType=xmlpullparser.next();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			return null;
		}
	}
}
