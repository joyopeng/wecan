package com.gftxcky.draw.primitive;

import java.io.Serializable;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;


public class DoorBean implements Serializable {
    private static final long serialVersionUID = -1736430429115434736L;
    private static final String TAG = DoorBean.class.getSimpleName();
    public static final float right = 0;
    private int DoorType = -1;
    private XCKYPoint LineEndPoint;
    private XCKYPoint LineStartPoint;
    private String LineUid;
    private XCKYPaint arcPaint;
    private XCKYPaint bottomPaint;
    private XCKYPoint doorEndPoint;
    private XCKYPaint doorPaint;
    private RectBean dragRect;
    public XCKYPoint endPoint;
    private boolean isChange;
    private boolean isSelect;
    private int pointNum = 2;
    private XCKYPoint[] points = new XCKYPoint[4];
    private XCKYPoint slanDoorPoint;
    public XCKYPoint startPoint;

    public DoorBean() {
    }

    public DoorBean(DoorBean doorBean) {
        this.startPoint = new XCKYPoint(doorBean.getStartPoint());
        this.endPoint = new XCKYPoint(doorBean.getEndPoint());
        this.bottomPaint = new XCKYPaint(doorBean.getBottomPaint());
        this.doorPaint = new XCKYPaint(doorBean.getDoorPaint());
        this.arcPaint = new XCKYPaint(doorBean.getArcPaint());
        this.isSelect = doorBean.isSelect();
        this.isChange = doorBean.isChange();
        this.DoorType = doorBean.getDoorType();
        this.LineStartPoint = new XCKYPoint(doorBean.getLineStartPoint());
        this.LineEndPoint = new XCKYPoint(doorBean.getLineEndPoint());
        this.LineUid = doorBean.LineUid;
        this.pointNum = doorBean.getPointNum();
    }

    public float getLinelength(float paramFloat1, float paramFloat2,
                               float paramFloat3, float paramFloat4) {
        return (float) Math.sqrt((paramFloat1 - paramFloat3)
                * (paramFloat1 - paramFloat3) + (paramFloat2 - paramFloat4)
                * (paramFloat2 - paramFloat4));
    }

    public double turnAngle(float paramFloat1, float paramFloat2,
                            float paramFloat3, float paramFloat4, float paramFloat5,
                            float paramFloat6) {
        double d = 180.0D * Math.atan2(paramFloat2 - paramFloat4, paramFloat1
                - paramFloat3) / 3.141592653589793D;
        return (360.0D + (180.0D * Math.atan2(paramFloat6 - paramFloat4,
                paramFloat5 - paramFloat3) / 3.141592653589793D - d)) % 360.0D;
    }

    public XCKYPaint getArcPaint() {
        XCKYPaint localPaint = new XCKYPaint();
        localPaint.setDither(true);
        localPaint.setStyle(XCKYPaint.Style.STROKE);
        localPaint.setAntiAlias(true);
        localPaint.setColor(-16777216);
        localPaint.setStrokeCap(XCKYPaint.Cap.SQUARE);
        localPaint.setStrokeWidth(1.0F);
        localPaint.setPathEffect(new DashPathEffect(new float[]{5.0F, 5.0F,
                5.0F, 5.0F}, 1.0F));
        this.arcPaint = localPaint;
        return this.arcPaint;
    }

    public XCKYPaint getBottomPaint() {
        XCKYPaint localPaint = new XCKYPaint();
        localPaint.setDither(true);
        localPaint.setAntiAlias(true);
        if (this.isSelect)
            localPaint.setColor(-16711936);
        else {
            localPaint.setColor(-1);
        }

        localPaint.setStrokeCap(XCKYPaint.Cap.BUTT);
        localPaint.setStrokeWidth(25.0F);
        this.bottomPaint = localPaint;
        return this.bottomPaint;
    }

    public XCKYPoint getDoorEndPoint() {
        Log.v(TAG, "pointNum = " + pointNum);
        switch (this.pointNum) {
            case 1:
                this.doorEndPoint = getPoints()[0];
                break;
            case 2:
                this.doorEndPoint = getPoints()[1];
                break;
            case 3:
                this.doorEndPoint = getPoints()[2];
                break;
            case 4:
                this.doorEndPoint = getPoints()[3];
                break;
        }

        return this.doorEndPoint;

    }

    public XCKYPaint getDoorPaint() {
        XCKYPaint localPaint = new XCKYPaint();
        localPaint.setDither(true);
        localPaint.setAntiAlias(true);
        if (this.isSelect)
            localPaint.setColor(-16711936);
        else {
            localPaint.setColor(Color.parseColor("#DAA520"));
        }

        localPaint.setStrokeCap(XCKYPaint.Cap.SQUARE);
        localPaint.setStrokeWidth(5.0F);
        this.doorPaint = localPaint;
        return this.doorPaint;
    }

    public int getDoorType() {
        if (this.startPoint.y - this.endPoint.y == 0)
            this.DoorType = 1;

        if (this.startPoint.x - this.endPoint.x == 0)
            this.DoorType = 2;
        else if (((this.startPoint.x > this.endPoint.x) && (this.startPoint.y < this.endPoint.y))
                || ((this.startPoint.x < this.endPoint.x) && (this.startPoint.y > this.endPoint.y)))
            this.DoorType = 3;
        else if (((this.startPoint.x < this.endPoint.x) && (this.startPoint.y < this.endPoint.y))
                || ((this.startPoint.x > this.endPoint.x) && (this.startPoint.y > this.endPoint.y)))
            this.DoorType = 4;

        return this.DoorType;
    }

    public RectBean getDragRect() {
        int i = this.pointNum;
        XCKYPoint localPoint = null;
        switch (i) {
            case 1:
                localPoint = getPoints()[0];
                break;
            case 2:
                localPoint = getPoints()[1];
                break;
            case 3:
                localPoint = getPoints()[2];
                break;
            case 4:
                localPoint = getPoints()[3];
                break;
        }

        this.dragRect = new RectBean(-15 + localPoint.x,
                -15 + localPoint.y, 15 + localPoint.x, 15 + localPoint.y);
        return this.dragRect;
    }

    public XCKYPoint getEndPoint() {
        Log.v(TAG, "getEndPoint poingNum = " + pointNum);
        switch (this.pointNum) {
            case 2:
            case 4:
                return this.endPoint;
            case 1:
            case 3:
                return this.startPoint;
            default:
                return this.endPoint;
        }

    }


    public XCKYPoint getLineEndPoint() {
        return LineEndPoint;
    }

    public XCKYPoint getLineStartPoint() {
        return LineStartPoint;
    }

    public int getPointNum() {
        return this.pointNum;
    }

    public XCKYPoint[] getPoints() {

        float f1 = this.startPoint.x;
        float f2 = this.startPoint.y;
        float f3 = this.endPoint.x;
        float f4 = this.endPoint.y;

        XCKYPoint localPoint1 = new XCKYPoint(0, 0);
        XCKYPoint localPoint2 = new XCKYPoint(0, 0);
        XCKYPoint localPoint3 = new XCKYPoint(0, 0);
        XCKYPoint localPoint4 = new XCKYPoint(0, 0);
        getLinelength(f1, f2, f3, f4);

        float f5 = Math.abs(f1 - f3);
        float f6 = Math.abs(f2 - f4);
        if (getDoorType() == 1) {
            localPoint1 = new XCKYPoint(f1 + f6, f2 + f5);
            localPoint2 = new XCKYPoint(f3 + f6, f4 + f5);
            localPoint3 = new XCKYPoint(f1 + f6, f2 - f5);
            localPoint4 = new XCKYPoint(f3 + f6, f4 - f5);
        } else if (getDoorType() == 2) {
            localPoint1 = new XCKYPoint(f1 - f6, f2 + f5);
            localPoint2 = new XCKYPoint(f3 - f6, f4 + f5);
            localPoint3 = new XCKYPoint(f1 + f6, f2 + f5);
            localPoint4 = new XCKYPoint(f3 + f6, f4 + f5);
        } else if (getDoorType() == 3) {
            localPoint1 = new XCKYPoint(f1 + f6, f2 + f5);
            localPoint2 = new XCKYPoint(f3 + f6, f4 + f5);
            localPoint3 = new XCKYPoint(f1 - f6, f2 - f5);
            localPoint4 = new XCKYPoint(f3 - f6, f4 - f5);
        } else if (getDoorType() == 4) {
            localPoint1 = new XCKYPoint(f1 - f6, f2 + f5);
            localPoint2 = new XCKYPoint(f3 - f6, f4 + f5);
            localPoint3 = new XCKYPoint(f1 + f6, f2 - f5);
            localPoint4 = new XCKYPoint(f3 + f6, f4 - f5);
        }

        this.points[0] = localPoint1;
        this.points[1] = localPoint2;
        this.points[2] = localPoint3;
        this.points[3] = localPoint4;

        return this.points;
    }

    public XCKYPoint getSlanDoorPoint() {
        switch (this.pointNum) {
            case 1:
                this.slanDoorPoint = getPoints()[1];
                break;
            case 2:
                this.slanDoorPoint = getPoints()[0];
                break;
            case 3:
                this.slanDoorPoint = getPoints()[3];
                break;
            case 4:
                this.slanDoorPoint = getPoints()[2];
                break;
        }

        return this.slanDoorPoint;
    }

    public XCKYPoint getStartPoint() {
        Log.v(TAG, " getStartPoint poingNum = " + pointNum);
        switch (this.pointNum) {
            case 2:
            case 4:
                return this.startPoint;
            case 1:
            case 3:
                return this.endPoint;
            default:
                return this.startPoint;
        }

    }

    public boolean isChange() {
        return this.isChange;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public void setChange(boolean paramBoolean) {
        this.isChange = paramBoolean;
    }

    public void setEndPoint(XCKYPoint paramPoint) {
        this.endPoint = paramPoint;
    }

    public void setLineEndPoint(XCKYPoint paramPoint) {
        this.LineEndPoint = paramPoint;
    }

    public void setLineStartPoint(XCKYPoint paramPoint) {
        this.LineStartPoint = paramPoint;
    }

    public void setPointNum(int paramInt) {
        this.pointNum = paramInt;
    }

    public void setSelect(boolean paramBoolean) {
        this.isSelect = paramBoolean;
    }

    public void setStartPoint(XCKYPoint paramPoint) {
        this.startPoint = paramPoint;
    }

    public String getLineUid() {
        return LineUid;
    }

    public void setLineUid(String lineUid) {
        LineUid = lineUid;
    }
}
