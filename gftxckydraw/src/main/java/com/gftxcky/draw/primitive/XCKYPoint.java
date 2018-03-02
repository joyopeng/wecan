package com.gftxcky.draw.primitive;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Point;

public class XCKYPoint extends Point implements Serializable {
	private static final long serialVersionUID = 3052597029446761388L;

	  public XCKYPoint()
	  {
	  }

	  public XCKYPoint(float x, float y)
	  {
	    this.x = ((int)x);
	    this.y = ((int)y);
	  }

	  public XCKYPoint(int x, int y)
	  {
	    this.x = x;
	    this.y = y;
	  }

	  public XCKYPoint(XCKYPoint point)
	  {
	    super(point);
	  }
	  
	  public void set(float x,float y)
	  {
		  super.set((int)x,(int)y);
	  }

	  private void readObject(ObjectInputStream paramObjectInputStream)
	  {
	    try {
			paramObjectInputStream.defaultReadObject();
			this.x = paramObjectInputStream.readInt();

		    this.y = paramObjectInputStream.readInt();
		} catch (NotActiveException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }

	  private void writeObject(ObjectOutputStream paramObjectOutputStream)
	  {
	    try {
			paramObjectOutputStream.defaultWriteObject();
			paramObjectOutputStream.writeInt(this.x);
		    paramObjectOutputStream.writeInt(this.y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
}
