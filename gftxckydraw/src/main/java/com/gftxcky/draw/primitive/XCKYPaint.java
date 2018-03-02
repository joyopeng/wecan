package com.gftxcky.draw.primitive;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Paint;

public class XCKYPaint extends Paint implements Serializable{
	private static final long serialVersionUID = -3219129626816734215L;

	public XCKYPaint() {
	}

	public XCKYPaint(int flags) {
		super(flags);
	}

	public XCKYPaint(Paint paint) {
		super(paint);
	}

	private void readObject(ObjectInputStream paramObjectInputStream) {
		try {
			paramObjectInputStream.defaultReadObject();
			super.setFlags(paramObjectInputStream.readInt());
			super.setColor(paramObjectInputStream.readInt());
			super.setStrokeWidth(paramObjectInputStream.readFloat());
			super.setStyle((Paint.Style) paramObjectInputStream.readObject());
			super.setStrokeCap((Paint.Cap) paramObjectInputStream.readObject());
		} catch (NotActiveException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void writeObject(ObjectOutputStream paramObjectOutputStream) {
		try {
			paramObjectOutputStream.defaultWriteObject();
			paramObjectOutputStream.writeInt(super.getFlags());
			paramObjectOutputStream.writeInt(super.getColor());
			paramObjectOutputStream.writeFloat(super.getStrokeWidth());
			paramObjectOutputStream.writeObject(super.getStyle());
			paramObjectOutputStream.writeObject(super.getStrokeCap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
