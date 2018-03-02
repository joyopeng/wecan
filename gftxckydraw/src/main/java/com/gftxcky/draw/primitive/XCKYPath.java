package com.gftxcky.draw.primitive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import com.gftxcky.draw.primitive.XCKYPath;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

public class XCKYPath extends Path implements Serializable {
	private static final long serialVersionUID = 355634750615035436L;
	private ArrayList<PathAction> actions = new ArrayList<PathAction>();
	private MatrixBean matrix;
	
	public XCKYPath() {
		super();
	}

	public XCKYPath(XCKYPath paramPath) {
		super(paramPath);
		for(PathAction ation:paramPath.getActions())
		{
			actions.add(ation.clone());
		}
		if(paramPath.matrix!=null)
			this.matrix=new MatrixBean(paramPath.matrix);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
	    drawThisPath();
	}
	 
	@Override
	public void moveTo(float x, float y) {
	    getActions().add(new ActionMove(x, y));
	    super.moveTo(x, y);
	}
	 
	@Override
	public void lineTo(float x, float y){
	    getActions().add(new ActionLine(x, y));
	    super.lineTo(x, y);
	}
	
	@Override
	public void addRect(RectF rect,Direction dir)
	{
	    getActions().add(new ActionRect(rect,dir));
		super.addRect(rect, dir);
	}
	
	@Override
	public void addRect(float left,float top,float right,float bottom,Direction dir)
	{
		getActions().add(new ActionRect(left, top, right, bottom, dir));
		super.addRect(left, top, right, bottom, dir);
	}
	
	@Override
	public void cubicTo(float x1,float y1,float x2,float y2,float x3,float y3)
	{
		getActions().add(new ActionCubic(x1, y1, x2, y2, x3, y3));
		super.cubicTo(x1, y1, x2, y2, x3, y3);
	}
	
	@Override
	public void addCircle(float x,float y,float radius,Direction dir)
	{
		getActions().add(new ActionCircle(x, y, radius, dir));
		super.addCircle(x, y, radius, dir);
	}
	
	@Override
	public void addArc(RectF oval,float startAngle,float sweepAngle)
	{
		getActions().add(new ActionArc(oval , startAngle, sweepAngle));
		super.addArc(oval, startAngle, sweepAngle);
	}

	@Override
	public void close()
	{
		getActions().add(new ActionClose());
		super.close();
	}
	 
	public void addPath(XCKYPath path)
	{
		getActions().add(new ActionPath(path));
		super.addPath(path);
	}
	
	@Override
	public void transform(Matrix matrix)
	{
		if(this.matrix!=null)
		{
			this.matrix.postConcat(new MatrixBean(matrix));
		}
		else
		{
			this.matrix=new MatrixBean(matrix);
		}
		super.transform(matrix);
	}
	
	public void transform(MatrixBean matrix)
	{
		if(this.matrix!=null)
		{
			this.matrix.postConcat(matrix);
		}
		else
		{
			this.matrix=new MatrixBean(matrix);
		}
		super.transform(matrix.getMatrix());
	}
	
	private void drawThisPath(){
		
	    for(PathAction p : getActions()){
	        switch(p.getType())
	        {
	        	case MOVE_TO:
	        		ActionMove move=(ActionMove)p;
	        		super.moveTo(move.x,move.y);
	        		break;
	        	case LINE_TO:
	        		ActionLine line=(ActionLine)p;
	        		super.lineTo(line.x, line.y);
	        		break;
	        	case ADD_RECT:
	        		ActionRect rect=(ActionRect)p;
	        		super.addRect(rect.left,rect.top,rect.right,rect.bottom, rect.dir);
	        		break;
	        	case ADD_CIRCLE:
	        		ActionCircle circle=(ActionCircle)p;
	        		super.addCircle(circle.x, circle.y, circle.radius, circle.dir);
	        		break;
	        	case ADD_ARC:
	        		ActionArc arc=(ActionArc)p;
	        		super.addArc(new RectF(arc.left,arc.top,arc.right,arc.bottom), arc.startAngle, arc.sweepAngle);
	        		break;
	        	case CUBIC_TO:
	        		ActionCubic cubic=(ActionCubic)p;
	        		super.cubicTo(cubic.x1, cubic.y1,cubic.x2, cubic.y2, cubic.x3, cubic.y3);
	        		break;
	        	case ADD_PATH:
	        		ActionPath path=(ActionPath)p;
	        		super.addPath(path.path);
	        		break;
	        	case CLOSE:
	        		super.close();
	        		break;
	        }
	    }
	    
	    //
	    if(this.matrix!=null)
	    	super.transform(matrix.getMatrix());
	}
	 
	public ArrayList<PathAction> getActions() {
		return actions;
	}

	public void setActions(ArrayList<PathAction> actions) {
		this.actions = actions;
	}

	public interface PathAction {
	    public enum PathActionType {LINE_TO,MOVE_TO,CUBIC_TO,ADD_RECT,ADD_CIRCLE,ADD_ARC,CLOSE,ADD_PATH};
	    public PathActionType getType();
	    public PathAction clone();
	}
	
	public class ActionClose implements PathAction, Serializable{

		private static final long serialVersionUID = 1L;

		@Override
	    public PathActionType getType() {
	        return PathActionType.CLOSE;
	    }
		
		@Override
		public PathAction clone()
		{
			return new ActionClose();
		}
	}
	 
	public class ActionMove implements PathAction, Serializable{
	    private static final long serialVersionUID = -7198142191254133295L;
	 
	    private float x,y;
	 
	    public ActionMove(float x, float y){
	        this.x = x;
	        this.y = y;
	    }
	 
	    @Override
	    public PathActionType getType() {
	        return PathActionType.MOVE_TO;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionMove(x,y);
		}
	}
	 
	public class ActionLine implements PathAction, Serializable{
	    private static final long serialVersionUID = 8307137961494172589L;
	 
	    private float x,y;
	 
	    public ActionLine(float x, float y){
	        this.x = x;
	        this.y = y;
	    }
	 
	    @Override
	    public PathActionType getType() {
	        return PathActionType.LINE_TO;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionLine(x,y);
		}
	}
	
	public class ActionRect implements PathAction, Serializable{
		private static final long serialVersionUID = -3642710530323848543L;
		private float left,right,top,bottom;
	    private Direction dir;
	 
	    public ActionRect(float left, float top,float right,float bottom,Direction dir){
	        this.left = left;
	        this.right = right;
	        this.top=top;
	        this.bottom=bottom;
	        this.dir=dir;
	    }
	    
	    public ActionRect(RectF rect,Direction dir){
	    	 this.left = rect.left;
	         this.right = rect.right;
	         this.top=rect.top;
	         this.bottom=rect.bottom;
	        this.dir=dir;
	    }
	    
	   
	    
	    @Override
	    public PathActionType getType() {
	        return PathActionType.ADD_RECT;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionRect(left,top,right,bottom,dir);
		}

	}
	
	public class ActionCubic implements PathAction, Serializable{
		private static final long serialVersionUID = -6139048094252103422L;
		private float x1,y1,x2,y2,x3,y3;
	 
	    public ActionCubic(float x1,float y1,float x2,float y2,float x3,float y3){
	        this.x1 = x1;
	        this.y1 = y1;
	        this.x2 = x2;
	        this.y2 = y2;
	        this.x3 = x3;
	        this.y3 = y3;
	    }
	    
	    @Override
	    public PathActionType getType() {
	        return PathActionType.CUBIC_TO;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionCubic(x1,y1,x2,y2,x3,y3);
		}
	}
	
	public class ActionCircle implements PathAction, Serializable{
		private static final long serialVersionUID = 2809487522549956095L;
		private float x,y,radius;
		private Direction dir;
	 
	    public ActionCircle(float x,float y,float radius,Direction dir){
	        this.x = x;
	        this.y = y;
	        this.radius = radius;
	        this.dir = dir;
	    }
	    
	    @Override
	    public PathActionType getType() {
	        return PathActionType.ADD_CIRCLE;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionCircle(x,y,radius,dir);
		}

	}
	
	public class ActionArc implements PathAction, Serializable{
		private static final long serialVersionUID = -3951669077343108463L;
		private float left,right,top,bottom;
		private float startAngle,sweepAngle;
	 
	    public ActionArc(RectF rect,float startAngle,float sweepAngle){
	        this.left = rect.left;
	        this.right = rect.right;
	        this.top=rect.top;
	        this.bottom=rect.bottom;
	        this.startAngle = startAngle;
	        this.sweepAngle = sweepAngle;
	    }
	    
	    public ActionArc(float left, float top,float right,float bottom,float startAngle,float sweepAngle){
	        this.left = left;
	        this.right =right;
	        this.top=top;
	        this.bottom=bottom;
	        this.startAngle = startAngle;
	        this.sweepAngle = sweepAngle;
	    }
	    
	    @Override
	    public PathActionType getType() {
	        return PathActionType.ADD_ARC;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionArc(left,top,right,bottom,startAngle,sweepAngle);
		}
	}

	public class ActionPath implements PathAction, Serializable{
		private static final long serialVersionUID = 2822948142338351692L;
		private XCKYPath path;
		
		public ActionPath(XCKYPath path){
	        this.path=path;
	    }
	    
	    @Override
	    public PathActionType getType() {
	        return PathActionType.ADD_PATH;
	    }
	    
		@Override
		public PathAction clone()
		{
			return new ActionPath(new XCKYPath(path));
		}
	}
}
