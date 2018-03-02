package com.gftxcky.draw.primitive;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GatherBean implements Serializable {
	private static final long serialVersionUID = 3938243546618365747L;
	  private Map<String,LineBean> LinesMap;
	  private Map<String,DoorBean>  doorPathMap;
	  private Map<String,DrawTextBean>  drawTextMap;
	  private Map<String,MouldBean> mouldBeanMap;
	  private Map<String,MouldPathBean> mouldPathMap;
	  private Map<String,PathBean> paths;
	  private Map<String,WindowBean> windowPathMap;
	  private TableBean tableBean;
	  
	  public GatherBean()
	  {
		  
	  }
	  
	  public GatherBean(GatherBean gatherbean)
	  {
		   //PathBean
			Map<String,PathBean> pathMap= new HashMap<String,PathBean>();
			for(String key:gatherbean.getPaths().keySet())
			{
				PathBean pathbean=new PathBean(gatherbean.getPaths().get(key));
				pathbean.setSelect(false);
				pathMap.put(key,pathbean);
			}
			this.setPaths(pathMap);
			//DrawTextBean
			Map<String,DrawTextBean> drawTextMap= new HashMap<String,DrawTextBean>();
			for(String key:gatherbean.getDrawTextMap().keySet())
			{
				DrawTextBean drawtextbean=new DrawTextBean(gatherbean.getDrawTextMap().get(key));
				drawtextbean.setSelect(false);
				drawTextMap.put(key,drawtextbean);
			}
			this.setDrawTextMap(drawTextMap);
			//LineBean
			Map<String,LineBean> lineMap= new HashMap<String,LineBean>();
			for(String key:gatherbean.getLinesMap().keySet())
			{
				LineBean linebean=new LineBean(gatherbean.getLinesMap().get(key));
				linebean.setSelect(false);
				lineMap.put(key,linebean);
			}
			this.setLinesMap(lineMap);
			//MouldPathBean
			Map<String,MouldPathBean> mouldPathBeanMap= new HashMap<String,MouldPathBean>();
			for(String key:gatherbean.getMouldPathMap().keySet())
			{
				MouldPathBean mouldpathbean=new MouldPathBean(gatherbean.getMouldPathMap().get(key));
				mouldpathbean.setSelect(false);
				mouldPathBeanMap.put(key,mouldpathbean);
			}
			this.setMouldPathMap(mouldPathBeanMap);
			//MouldBean
			Map<String,MouldBean> mouldBeanMap= new HashMap<String,MouldBean>();
			for(String key:gatherbean.getMouldBeanMap().keySet())
			{
				MouldBean mouldbean=new MouldBean(gatherbean.getMouldBeanMap().get(key));
				mouldbean.setSelect(false);
				mouldBeanMap.put(key,mouldbean);
			}
			this.setMouldBeanMap(mouldBeanMap);
			//WindowBean
			Map<String,WindowBean> windowBeanMap= new HashMap<String,WindowBean>();
			for(String key:gatherbean.getWindowPathMap().keySet())
			{
				WindowBean windowbean=new WindowBean(gatherbean.getWindowPathMap().get(key));
				windowbean.setSelect(false);
				windowBeanMap.put(key,windowbean);
			}
			this.setWindowPathMap(windowBeanMap);
			//DoorBean
			Map<String,DoorBean> doorBeanMap= new HashMap<String,DoorBean>();
			for(String key:gatherbean.getDoorPathMap().keySet())
			{
				DoorBean doorbean=new DoorBean(gatherbean.getDoorPathMap().get(key));
				doorbean.setSelect(false);
				doorBeanMap.put(key,doorbean);
			}
			this.setDoorPathMap(doorBeanMap);
	  }

	  public Map<String, DoorBean> getDoorPathMap()
	  {
	    return this.doorPathMap;
	  }

	  public Map<String, DrawTextBean> getDrawTextMap()
	  {
	    return this.drawTextMap;
	  }

	  public Map<String, LineBean> getLinesMap()
	  {
	    return this.LinesMap;
	  }

	  public Map<String, MouldBean> getMouldBeanMap()
	  {
	    return this.mouldBeanMap;
	  }

	  public Map<String, MouldPathBean> getMouldPathMap()
	  {
	    return this.mouldPathMap;
	  }

	  public Map<String, PathBean> getPaths()
	  {
	    return this.paths;
	  }

	  public TableBean getTableBean()
	  {
	    return this.tableBean;
	  }

	  public Map<String, WindowBean> getWindowPathMap()
	  {
	    return this.windowPathMap;
	  }

	  public void setDoorPathMap(Map<String, DoorBean> paramMap)
	  {
	    this.doorPathMap = paramMap;
	  }

	  public void setDrawTextMap(Map<String, DrawTextBean> paramMap)
	  {
	    this.drawTextMap = paramMap;
	  }

	  public void setLinesMap(Map<String, LineBean> paramMap)
	  {
	    this.LinesMap = paramMap;
	  }

	  public void setMouldBeanMap(Map<String, MouldBean> paramMap)
	  {
	    this.mouldBeanMap = paramMap;
	  }

	  public void setMouldPathMap(Map<String, MouldPathBean> paramMap)
	  {
	    this.mouldPathMap = paramMap;
	  }

	  public void setPaths(Map<String, PathBean> paramMap)
	  {
	    this.paths = paramMap;
	  }

	  public void setTableBean(TableBean paramTableBean)
	  {
	    this.tableBean = paramTableBean;
	  }

	  public void setWindowPathMap(Map<String, WindowBean> paramMap)
	  {
	    this.windowPathMap = paramMap;
	  }
}
