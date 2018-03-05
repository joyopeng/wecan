package com.gftxcky.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.gftxcky.draw.primitive.BezierBean;
import com.gftxcky.draw.primitive.CircleBean;
import com.gftxcky.draw.primitive.DirectionBean;
import com.gftxcky.draw.primitive.DoorBean;
import com.gftxcky.draw.primitive.DrawTextBean;
import com.gftxcky.draw.primitive.EllipseBean;
import com.gftxcky.draw.primitive.GatherBean;
import com.gftxcky.draw.primitive.LineBean;
import com.gftxcky.draw.primitive.MatrixBean;
import com.gftxcky.draw.primitive.MouldBean;
import com.gftxcky.draw.primitive.MouldPathBean;
import com.gftxcky.draw.primitive.PaintType;
import com.gftxcky.draw.primitive.PathBean;
import com.gftxcky.draw.primitive.PathDataBean;
import com.gftxcky.draw.primitive.PathPointBean;
import com.gftxcky.draw.primitive.RectBean;
import com.gftxcky.draw.primitive.TableBean;
import com.gftxcky.draw.primitive.WindowBean;
import com.gftxcky.draw.primitive.XCKYPaint;
import com.gftxcky.draw.primitive.XCKYPath;
import com.gftxcky.draw.primitive.XCKYPoint;

public class ExperModeCanvas extends View {

    private final String TAG = ExperModeCanvas.class.getSimpleName();
    //==参数
    //前始点，操作的感知范围
    private int _hitPointCoolEye = 100;
    //线，操作感知范围（命中点离线距离)
    private int _hitLineCoolEye = 15;
    //命中点效果，高亮大小
    private int _hitPointStyleSize = 25;
    //可操作点高亮画笔（主要用绘 制选中时的操作点）
    private XCKYPaint _selPointPaint;
    //文字画笔
    private XCKYPaint _textPaint;

    //==以下为状态控制
    //鼠标点击开始点
    private XCKYPoint _startPoint;
    //鼠标移动结束点
    private XCKYPoint _endPoint;
    //前一点坐标的x
    private float _x;
    //前一点坐标的y
    private float _y;
    //当前状态
    private int _status = 0;
    //当前画的类型
    private int _paintType = -1;
    //移动操作类型
    private int _moveOperType = 0;
    //选择模式
    private boolean _status_sel = false;
    //移动模式
    private boolean _status_move = false;
    //画线模式
    private boolean _status_line = false;
    //画窗模式
    private boolean _status_lineWindow = false;
    //画门模式
    private boolean _status_lineDoor = false;
    //文字模式
    private boolean _status_lineText = false;
    //标识是否关闭当前路径
    private boolean _colsePath = false;
    //表示当前正在画的路径
    private PathBean _pathing;
    //当前操作画笔（为当前绘画类型的画笔）
    private XCKYPaint _paint;

    //==以下为数据存储
    //当前操作的对象
    private DoorBean _doorBean;
    private WindowBean _windowBean;
    private PathBean _pathBean;
    private MouldPathBean _mouldPathBean;
    private MouldBean _mouldBean;
    //当前操作线
    private LineBean _lineBean;
    //当前操作线的连接点的线
    private LineBean _connLineBean;
    //制表信息
    private TableBean _tableBean;
    //当前图数据
    private Map<String, PathBean> _pathMap;
    private Map<String, LineBean> _lineMap;
    private Map<String, DrawTextBean> _drawTextMap;
    private Map<String, MouldPathBean> _mouldPathMap;
    private Map<String, MouldBean> _mouldMap;
    private Map<String, WindowBean> _windowMap;
    private Map<String, DoorBean> _doorMap;
    //历史记录
    private List<GatherBean> _gatherList;
    private int _curGatherIndex;
    //主应用
    private ExperModeActivity _main;

    public ExperModeCanvas(Context mainActivity) {
        super(mainActivity);
        this._main = (ExperModeActivity) mainActivity;
        this._gatherList = new ArrayList<GatherBean>();
        this._curGatherIndex = -1;
        this._lineMap = new HashMap<String, LineBean>();
        this._drawTextMap = new HashMap<String, DrawTextBean>();
        this._mouldPathMap = new HashMap<String, MouldPathBean>();
        this._mouldMap = new HashMap<String, MouldBean>();
        this._windowMap = new HashMap<String, WindowBean>();
        this._doorMap = new HashMap<String, DoorBean>();
        this._pathMap = new HashMap<String, PathBean>();
        this._tableBean = new TableBean();
        this._selPointPaint = new XCKYPaint(4);
        this._selPointPaint.setAntiAlias(true);
        this._selPointPaint.setColor(Color.GREEN);
        this._selPointPaint.setStrokeWidth(10.0F);
        this._selPointPaint.setStrokeCap(Paint.Cap.ROUND);
        this._selPointPaint.setAlpha(127);
        this._textPaint = new XCKYPaint();
        this._textPaint.setAntiAlias(true);
        this._textPaint.setColor(Color.BLACK);
        this._textPaint.setTextSize(20.0F);
        setPaint(PaintType.None);
    }

    /**
     * 设置绘画类型
     *
     * @param paramInt
     */
    public void setPaint(int paintType) {
        if (paintType == PaintType.None) {
            this._status = -1;
        }
        this._startPoint = null;
        this._endPoint = null;
        this._paint = ExperModeCanvasUtil.getPaint(paintType, this._status);
        this._status = ExperModeCanvasUtil.config();
        this._paintType = ExperModeCanvasUtil.paintType();
        this._pathing = ExperModeCanvasUtil.path();
        if (this._paintType == PaintType.None) {
            resetBtnFocus();
        } else {
            resetBtnFocus();
            setBtnFocus();
        }
    }

    /**
     * 设置Table信息
     *
     * @param tabinfo
     */
    public void setTabInfo(String tabinfo) {
        this._tableBean.setZtsj(tabinfo.split(",")[5]);
        this._tableBean.setZtr(tabinfo.split(",")[3]);
        this._tableBean.setZtdw(tabinfo.split(",")[2]);
        this._tableBean.setFadz(tabinfo.split(",")[1]);
        this._tableBean.setFasj(tabinfo.split(",")[0]);
        this._tableBean.setCaseType(tabinfo.split(",")[4]);
        String title = tabinfo.split(",")[0] + " " + tabinfo.split(",")[1] + tabinfo.split(",")[4] + "平面示意图";
        this._tableBean.setTitleText(title);
    }

    public TableBean getTableBean() {
        return _tableBean;
    }

    /**
     * @param text
     */
    public void addDrawText(String text, XCKYPoint point) {
        DrawTextBean drawtextbean = new DrawTextBean();
        drawtextbean.setText(text);
        drawtextbean.setMove(false);
        drawtextbean.setSelect(false);
        drawtextbean.setPoint(point);
        _drawTextMap.put(drawtextbean.toString(), drawtextbean);
        saveData();
        invalidate();
    }

    /**
     * 设置标题
     *
     * @param paramString
     */
    public void setTitle(String title) {
        this._tableBean.setTitleText(title);
    }

    /**
     * 得到当前点在投射到线上的坐标
     *
     * @param xstart
     * @param ystart
     * @param xend
     * @param yend
     * @param ex
     * @param ey
     * @return
     */
    private XCKYPoint getCrossPoint(float xstart, float ystart, float xend, float yend, float ex, float ey) {
        //制作一条最辅助线
        float xAusStart, yAusStart, xAusEnd, yAusEnd;

        //当前线更接近平行x轴时，制作 一根垂直于x轴的辅助线，反之制作 一根垂直于y轴的辅助线
        //有助于提高操作精度
        if (Math.abs(xend - xstart) > Math.abs(yend - ystart)) {
            xAusStart = ex;
            yAusStart = 1;
            xAusEnd = ex;
            yAusEnd = 1280;
        } else {
            xAusStart = 1;
            yAusStart = ey;
            xAusEnd = 1280;
            yAusEnd = ey;
        }

        //求出相交点x,y坐标权限值(计算时取用通过两点直线，实际为线段)
        float xmin, ymin, xmax, ymax;
        if (xend > xstart) {
            xmin = xstart;
            xmax = xend;
        } else {
            xmin = xend;
            xmax = xstart;
        }
        if (yend > ystart) {
            ymin = ystart;
            ymax = yend;
        } else {
            ymin = yend;
            ymax = ystart;
        }

        /**
         *
         * ==使用斜率，无法表式水平线，多次使用除法，精度差
         * 直线1上两点(x1,y1)、(x2,y2)，直线表达式： k1=(y1-y2)/(x1-x2)
         * 直线2上两点(x3,y3)、(x4,y4)，直线表达式： k2=(y3-y4)/(x3-x4)
         * 设交点：(x,y)
         * 则：  k1=(y-y1)/(x-x1)、k2=(y-y3)/(x-x3)
         * 解： x=(k1x1-y1-k2x3+y3)/(k1-k2)
         *    y=k2(x-x3)+y3
         *
         * DEMO:
         *
         *  float linek=Math.abs((yend==ystart)?0:(xend-xstart)/(yend-ystart)); //当前线斜率
         *  float vhk=Math.abs((yAusEnd==yAusStart)?0:(xAusEnd-xAusStart)/(yAusEnd-yAusStart));	   //水平、垂直线斜率
         *  float x,y;
         *  if(linek!=vhk)
         *  {
         * 		x=(linek*xstart-ystart-vhk*xAusStart+yAusStart)/(linek-vhk);
         * 		y=vhk*(x-xAusStart)+yAusStart;
         *    }
         * 	else
         *    {
         * 		x=ex;
         * 		y=ystart;
         *  }
         *
         *==使用行列式计算和斜率表达式
         *
         * 直线1上两点(x1,y1)、(x2,y2)，直线表达式： k1=(y1-y2)/(x1-x2)
         * 直线2上两点(x3,y3)、(x4,y4)，直线表达式： k2=(y3-y4)/(x3-x4)
         * 设相交于点(x,y)
         * (y-y1)=k1*(x-x1)
         * (y-y2)=k1*(x-x2)
         * (y-y3)=k2*(x-x3)
         * (y-y4)=k2*(x-x4)
         * 消失去k1,k2则：
         * x(y1-y2)+y(x2-x1)=x2y1-x1y2
         * x(y3-y4)+y(x4-x3)=x4y3-x3y4
         * 设常数项：
         * b1=x2y1-x1y2
         * b2=x4y3-x3y4
         * 则使用行列式求得二元一次方程组为：
         * |D|=(y1-y2)*(x4-x3)-(y3-y4)*(x2-x1)
         * |Dx|=b1*(x4-x3)-b2*(x2-x1)
         * |Dy|=b2*(y1-y2)-b1*(y3-y4)
         * 则：
         * x=|Dx|/|D|
         * y=|Dy|/|D|
         *
         * DEMO:
         *
         * float b1=xend*ystart-xstart*yend;
         * foat b2=xAusEnd*yAusStart-xAusStart*yAusEnd;
         * float D=(ystart-yend)*(xAusEnd-xAusStart)-(yAusStart-yAusEnd)*(xend-xstart);
         * float Dx=b1*(xAusEnd-xAusStart)-b2*(xend-xstart);
         * float Dy=b2*(ystart-yend)-b1*(yAusStart-yAusEnd);
         * float x,y;
         * x=Dx/D;
         * y=Dy/D;
         *
         *==使用行列式计算和两点表达式
         *
         * 设相交于点(x,y)
         * (x-x1)=k1*(x2-x1)
         * (y-y1)=k1*(y2-y1)
         * (x-x3)=k2*(y4-x3)
         * (y-y3)=k2*(y4-y3)
         * 消失去k1,k2则：
         * (y2-y1)x+(x1-x2)y=(y2-y1)x1+(x1-x2)y1
         * (y4-y3)x+(x3-x4)y=(y4-y3)x3+(x3-x4)y3
         * 设常数项：
         * b1=(y2-y1)x1+(x1-x2)y1
         * b2=(y4-y3)x3+(x3-x4)y3
         * 则使用行列式求得二元一次方程组为：
         * |D|=(y2-y1)*(x3-x4)-(x1-x2)*(y4-y3)
         * |Dx|=b1*(x3-x4)-b2*(x1-x2)
         * |Dy|=b2*(y2-y1)-b1*(y4-y3)
         * 则：
         * x=|Dx|/|D|
         * y=|Dy|/|D|
         *
         * DEMO:
         * 	float b1=(yend-ystart)*xstart+(xstart-xend)*ystart;
         *  float b2=(yAusEnd-yAusStart)*xAusStart+(xAusStart-xAusEnd)*yAusStart;
         *  float D=(yend-ystart)*(xAusStart-xAusEnd)-(xstart-xend)*(yAusEnd-yAusStart);
         *  float Dx=b1*(xAusStart-xAusEnd)-b2*(xstart-xend);
         *  float Dy=b2*(yend-ystart)-b1*(yAusEnd-yAusStart);
         *  float x,y;
         *  x=Dx/D;
         *  y=Dy/D;
         *
         *
         */
        //求出交点
        float b1 = xend * ystart - xstart * yend;
        float b2 = xAusEnd * yAusStart - xAusStart * yAusEnd;
        float D = (ystart - yend) * (xAusEnd - xAusStart) - (yAusStart - yAusEnd) * (xend - xstart);
        float Dx = b1 * (xAusEnd - xAusStart) - b2 * (xend - xstart);
        float Dy = b2 * (ystart - yend) - b1 * (yAusStart - yAusEnd);
        float x, y;
        x = Dx / D;
        y = Dy / D;

        //超出检测
        if (x > xmax)
            x = xmax;
        if (x < xmin)
            x = xmin;
        if (y > ymax)
            y = ymax;
        if (y < ymin)
            y = ymin;

        return new XCKYPoint(x, y);
    }

    /**
     * 得到当前点在投射到线上的坐标
     *
     * @param startPoint
     * @param endPoint
     * @param point
     * @return
     */
    private XCKYPoint getCrossPoint(XCKYPoint startPoint, XCKYPoint endPoint, XCKYPoint point) {
        return getCrossPoint(startPoint.x, startPoint.y, endPoint.x, endPoint.y, point.x, point.y);
    }

    /**
     * 得到当前点在投射到线上的坐标
     *
     * @param line
     * @param point
     * @return
     */
    private XCKYPoint getCrossPoint(LineBean line, XCKYPoint point) {
        return getCrossPoint(line.getStartPoint().x, line.getStartPoint().y, line.getEndPoint().x, line.getEndPoint().y, point.x, point.y);
    }

    /**
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @return
     */
    private double attan(float x, float y, float x2, float y2, float x3, float y3) {
        //相对0，0的角度值
        double d1 = 180.0D * Math.atan2(y - y2, x - x2) / Math.PI;

        return (360.0D + (180.0D * Math.atan2(y3 - y2, x3 - x2) / 3.141592653589793D - d1)) % 360.0D;
    }


    /**
     * 求两点直线距离
     *
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */
    private float getDist(float x, float y, float x2,
                          float y2) {
        return (float) Math.sqrt((x - x2)
                * (x - x2) + (y - y2)
                * (y - y2));
    }

    /**
     * 求两点直线距离
     *
     * @param startPoint
     * @param endPoint
     * @return
     */
    private int getDist(XCKYPoint startPoint, XCKYPoint endPoint) {
        return (int) Math.sqrt((startPoint.x - endPoint.x) * (startPoint.x - endPoint.x)
                + (startPoint.y - endPoint.y) * (startPoint.y - endPoint.y));
    }


    /**
     * 从矩形实体生成矩形
     *
     * @param rectBean
     * @return
     */
    private Rect getRect(RectBean rectBean) {
        return new Rect((int) rectBean.left, (int) rectBean.top,
                (int) rectBean.right, (int) rectBean.bottom);
    }


    /**
     * 获得绘制的Path
     *
     * @param doorbean
     * @return
     */
    private XCKYPath getDoorDrawPath(DoorBean doorbean) {
        XCKYPath path = new XCKYPath();
        path.moveTo(doorbean.getStartPoint().x, doorbean.getStartPoint().y);
        path.quadTo(doorbean.getSlanDoorPoint().x, doorbean.getSlanDoorPoint().y, doorbean.getDoorEndPoint().x, doorbean.getDoorEndPoint().y);
        return path;
    }


    /**
     * 获得绘制的Path
     *
     * @param pathPointBean
     * @return
     */
    private XCKYPath getDrawPath(PathPointBean pathPointBean) {
        XCKYPath localPath = new XCKYPath();
        localPath.moveTo(pathPointBean.getStartPoint().x, pathPointBean.getStartPoint().y);
        for (XCKYPoint point : pathPointBean.getPointList()) {
            localPath.lineTo(point.x, point.y);
        }
        if (pathPointBean.isClose())
            localPath.close();
        return localPath;
    }

    /**
     * 获得绘制的Path
     *
     * @param mouldbean
     * @return
     */
    public XCKYPath getDrawPath(MouldBean mouldbean) {
        XCKYPath path = new XCKYPath();
        //RectBean
        for (RectBean rect : mouldbean.getRectList()) {
            path.addRect(new RectF(rect.left, rect.top, rect.right, rect.bottom), XCKYPath.Direction.CCW);
        }
        //BezierBean
        for (BezierBean bezierbean : mouldbean.getBezierList()) {
            XCKYPath bpath = new XCKYPath();
            bpath.moveTo(bezierbean.getPointList().get(0).x, bezierbean.getPointList().get(0).y);
            bpath.cubicTo(bezierbean.getPointList().get(1).x, bezierbean.getPointList().get(1).y, bezierbean.getPointList().get(2).x, bezierbean.getPointList().get(2).y, bezierbean.getPointList().get(3).x, bezierbean.getPointList().get(3).y);
            path.addPath(bpath);
        }
        //LineBean
        for (LineBean linebean : mouldbean.getLineList()) {
            XCKYPath lpath = new XCKYPath();
            lpath.moveTo(linebean.getStartPoint().x, linebean.getStartPoint().y);
            lpath.lineTo(linebean.getEndPoint().x, linebean.getEndPoint().y);
            path.addPath(lpath);
        }
        //DirectionBean
        for (DirectionBean directionbean : mouldbean.getDirectionList()) {
            XCKYPath dpath = new XCKYPath();
            dpath.moveTo(directionbean.getStartPoint().x, directionbean.getStartPoint().y);
            dpath.lineTo(directionbean.getEndPoint().x, directionbean.getEndPoint().y);
            path.addPath(dpath);
        }
        //CircleBean
        for (CircleBean circlebean : mouldbean.getCircleList()) {
            XCKYPath dpath = new XCKYPath();
            float dist = getDist(circlebean.getCenterx(), circlebean.getCentery(), circlebean.getRadiusx(), circlebean.getRadiusy());
            path.addCircle(circlebean.getCenterx(), circlebean.getCentery(), dist, android.graphics.Path.Direction.CCW);
            path.addPath(dpath);
        }
        //Poly
        for (PathPointBean pathpointbean : mouldbean.getPolyList()) {
            XCKYPath ppath = new XCKYPath();
            ppath.moveTo(pathpointbean.getStartPoint().x, pathpointbean.getStartPoint().y);
            for (XCKYPoint point : pathpointbean.getPointList()) {
                ppath.lineTo(point.x, point.y);
            }
            if (pathpointbean.isClose())
                ppath.close();
            path.addPath(ppath);
        }
        //Ellipse
        for (EllipseBean ellipsebean : mouldbean.getEllipseList()) {
            XCKYPath epath = new XCKYPath();
            epath.addArc(new RectF(ellipsebean.getRectfx(), ellipsebean.getRectfy(), ellipsebean.getRectfx1(), ellipsebean.getRectfy1()), 0.0F, 360F);
            path.addPath(epath);
        }
        return path;
    }

    /**
     * 获得Materix后的绘制的Path
     *
     * @param mouldbean
     * @param f1
     * @param f2
     * @return
     */
    public XCKYPath getMaterixDrawPath(MouldBean mouldBean, float postScaleSX, float postScaleSY) {
        MatrixBean matrixbean = new MatrixBean();
        matrixbean.setPostScaleSX(postScaleSX);
        matrixbean.setPostScaleSY(postScaleSY);
        matrixbean.setScale(true);
        XCKYPath path = getDrawPath(mouldBean);
        path.transform(matrixbean);
        RectF rectf = new RectF();
        path.computeBounds(rectf, true);
        if (Math.abs(rectf.left - rectf.right) > 300F || Math.abs(rectf.top - rectf.bottom) > 300F) {
            return getMaterixDrawPath(mouldBean, (float) ((double) postScaleSX - 0.05D), (float) ((double) postScaleSY - 0.05D));
        } else {
            mouldBean.setMatrix(matrixbean);
            return path;
        }
    }


    /**
     * 设置绘制的Path
     *
     * @param mouldBean
     * @return
     */
    public void loadDrawPath(MouldBean mouldbean) {
        XCKYPath path = getMaterixDrawPath(mouldbean, 1.0F, 1.0F);
        RectF rectf = new RectF();
        path.computeBounds(rectf, true);
        MatrixBean matrixbean = new MatrixBean();
        matrixbean.setTranslateX(0f - rectf.left);
        matrixbean.setTranslateY(0f - rectf.top);
        matrixbean.setTrans(true);
        path.transform(matrixbean);
        mouldbean.setPath(path);
        path.computeBounds(rectf, true);
        mouldbean.setMatrix(matrixbean);
        mouldbean.setRectobj(new RectBean(rectf.left, rectf.top, rectf.right, rectf.bottom));
        mouldbean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
        mouldbean.setRightBottomRect(new RectBean((int) rectf.right - 10, (int) rectf.bottom - 10, (int) rectf.right + 10, (int) rectf.bottom + 10));
        _mouldMap.put(mouldbean.toString(), mouldbean);
        saveData();
    }

    /**
     * 设置绘制的Path
     *
     * @param dataList
     */
    public void loadDrawPath(List<PathDataBean> dataList) {
        List<PathPointBean> pathPointList = getPathPoint(dataList, 600);
        String name = dataList.get(0).getName();
        MouldPathBean mouldpathbean = new MouldPathBean();
        mouldpathbean.setPathList(pathPointList);
        XCKYPath path = getDrawPath(pathPointList);
        RectF rectf = new RectF();
        path.computeBounds(rectf, true);
        MatrixBean matrixbean = new MatrixBean();
        matrixbean.setTranslateX(0.0F);
        matrixbean.setTranslateY(0.0F);
        matrixbean.setTrans(true);
        mouldpathbean.setMatrix(matrixbean);
        mouldpathbean.setPath(path);
        mouldpathbean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
        mouldpathbean.setRectobj(new RectBean((int) rectf.left, (int) rectf.top, (int) rectf.right, (int) rectf.bottom));
        mouldpathbean.setRightBottomRect(new RectBean(-15 + (int) rectf.right, -15 + (int) rectf.bottom, 15 + (int) rectf.right, 15 + (int) rectf.bottom));
        mouldpathbean.setMouldName(name);
        _mouldPathMap.put(mouldpathbean.toString(), mouldpathbean);
        saveData();
    }


    /**
     * 获得绘制的Path
     *
     * @param pathPointList
     * @return
     */
    private XCKYPath getDrawPath(List<PathPointBean> pathPointList) {
        XCKYPath localPath = new XCKYPath();
        for (PathPointBean pathPoint : pathPointList) {
            localPath.moveTo(pathPoint.getStartPoint().x, pathPoint.getStartPoint().y);
            for (Point point : pathPoint.getPointList()) {
                localPath.lineTo(point.x, point.y);
            }
            if (pathPoint.isClose())
                localPath.close();
        }
        return localPath;
    }

    /**
     * 从数据包中解析出PathPointBean
     *
     * @param dataList
     * @param mf
     * @return
     */
    private List<PathPointBean> getPathPoint(List<PathDataBean> dataList, float mf) {
        ArrayList xarray = new ArrayList();
        ArrayList yarray = new ArrayList();
        ArrayList<PathPointBean> patharray = new ArrayList<PathPointBean>();
        PathPointBean pathpointbean;
        String[] xyvalues, indexvalues;
        int xindex, yindex;
        float x, y;

        //
        for (PathDataBean pathdatabean : dataList) {
            xyvalues = pathdatabean.getXYStr().split(" ");
            indexvalues = pathdatabean.getIndexStr().split(" ");
            pathpointbean = new PathPointBean();
            for (int index = 0; index < indexvalues.length; index++) {
                //
                if (Integer.parseInt(indexvalues[index]) == 0)
                    xindex = 0;
                else
                    xindex = 3 * Integer.parseInt(indexvalues[index]);
                if (Integer.parseInt(indexvalues[index]) == 0)
                    yindex = 2;
                else
                    yindex = 3 * Integer.parseInt(indexvalues[index]) + 2;
                //
                x = Float.parseFloat(xyvalues[xindex]) * mf;
                y = Float.parseFloat(xyvalues[yindex]) * mf;
                if (index == 0) {
                    pathpointbean.setStartPoint(x, y);
                } else {
                    pathpointbean.setPointList(x, y);
                }
                xarray.add(Double.valueOf(x));
                yarray.add(Double.valueOf(y));
            }
            Collections.sort(xarray);
            Collections.sort(yarray);
            pathpointbean.setClose(true);
            patharray.add(pathpointbean);
        }
        if (Math.abs(((Double) xarray.get(0)).doubleValue() - ((Double) xarray.get(xarray.size() - 1)).doubleValue()) < 200D && Math.abs(((Double) yarray.get(0)).doubleValue() - ((Double) yarray.get(yarray.size() - 1)).doubleValue()) < 200D)
            return patharray;
        else
            return getPathPoint(dataList, mf - 30);

    }

    /**
     * 移动线时，同步线上的门和窗
     *
     * @param startPoint
     * @param endPoint
     * @param fx
     * @param fy
     */
    private void syncWindowDoor(LineBean line, float fx, float fy) {
        //WindowBean
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = _windowMap.get(key);
            if (line.getLineUid() == windowbean.getLineUid()) {
                windowbean.setLineStartPoint(new XCKYPoint(line.getStartPoint()));
                windowbean.setLineEndPoint(new XCKYPoint(line.getEndPoint()));
                windowbean.setStartPoint(new XCKYPoint(fx + windowbean.getStartPoint().x, fy + windowbean.getStartPoint().y));
                windowbean.setEndPoint(new XCKYPoint(fx + windowbean.getEndPoint().x, fy + windowbean.getEndPoint().y));
            }
        }
        //DoorBean
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = _doorMap.get(key);
            if (line.getLineUid() == doorbean.getLineUid()) {
                doorbean.setLineStartPoint(new XCKYPoint(line.getStartPoint()));
                doorbean.setLineEndPoint(new XCKYPoint(line.getEndPoint()));
                doorbean.setStartPoint(new XCKYPoint(fx + doorbean.getStartPoint().x, fy + doorbean.getStartPoint().y));
                doorbean.setEndPoint(new XCKYPoint(fx + doorbean.getEndPoint().x, fy + doorbean.getEndPoint().y));
            }
        }
    }

    /**
     * 移动线起始、结束点时，同步线上的门和窗
     *
     * @param paramPoint1
     * @param paramPoint2
     * @param paramPoint3
     * @param paramPoint4
     */
    private void syncWindowDoor(LineBean line) {
        //WindowBean
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = _windowMap.get(key);
            if (line.getLineUid() == windowbean.getLineUid()) {
                windowbean.setLineStartPoint(new XCKYPoint(line.getStartPoint()));
                windowbean.setLineEndPoint(new XCKYPoint(line.getEndPoint()));
                windowbean.setStartPoint(getCrossPoint(line.getStartPoint(), line.getEndPoint(), windowbean.getStartPoint()));
                windowbean.setEndPoint(getCrossPoint(line.getStartPoint(), line.getEndPoint(), windowbean.getEndPoint()));
            }
        }
        //DoorBean
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = _doorMap.get(key);
            if (line.getLineUid() == doorbean.getLineUid()) {
                doorbean.setLineStartPoint(new XCKYPoint(line.getStartPoint()));
                doorbean.setLineEndPoint(new XCKYPoint(line.getEndPoint()));
                doorbean.setStartPoint(getCrossPoint(line.getStartPoint(), line.getEndPoint(), doorbean.getStartPoint()));
                doorbean.setEndPoint(getCrossPoint(line.getStartPoint(), line.getEndPoint(), doorbean.getEndPoint()));
            }
        }
    }

    /**
     * 设置画布网格
     *
     * @param canvas
     */
    private void setCanvasGrid(Canvas canvas) {
        // 实例化画笔对象
        XCKYPaint localPaint = new XCKYPaint();
        // 设置画笔颜色
        localPaint.setColor(Color.parseColor("#9D9D9D"));
        // 设置画笔宽度
        localPaint.setStrokeWidth(1.0F);
        int w = getWidth();
        int h = getHeight();

        // x轴
        for (int i = 0; i <= w; i += 50) {
            if (i != w) {
                canvas.drawLine(i - 1, 0.0F, i - 1, h, localPaint);
            } else {
                canvas.drawLine(i, 0.0F, i, h, localPaint);
            }
        }
        // y轴
        for (int i = 0; i <= h; i += 50) {
            if (i != h) {
                canvas.drawLine(0.0F, i, w, i, localPaint);
            } else {
                canvas.drawLine(0.0F, h - 1, w, h - 1, localPaint);
            }
        }
    }

    /**
     * 绘制线的长度和角度
     *
     * @param canvas
     * @param lineBean
     * @param x
     * @param y
     */
    private void drawLineText(Canvas canvas, LineBean lineBean, float hOffset,
                              int vOffset) {
        XCKYPath localPath = new XCKYPath();
        localPath.moveTo(lineBean.getStartPoint().x, lineBean.getStartPoint().y);
        localPath.lineTo(lineBean.getEndPoint().x, lineBean.getEndPoint().y);
        canvas.drawTextOnPath("长度:" + getDist(lineBean.getStartPoint(), lineBean.getEndPoint()) / 100
                        + "m 角度:" + getAngle(lineBean.getStartPoint(), lineBean.getEndPoint()) + "°", localPath,
                hOffset, vOffset, this._textPaint);
    }

    /**
     * 绘制线的长度和角度
     *
     * @param canvas
     * @param lineBean
     * @param x
     * @param y
     */
    private void drawLineText(Canvas canvas, XCKYPoint stratPoint, XCKYPoint endPoint, int hOffset,
                              int vOffset) {
        XCKYPath localPath = new XCKYPath();
        localPath.moveTo(stratPoint.x, stratPoint.y);
        localPath.lineTo(endPoint.x, endPoint.y);
        canvas.drawTextOnPath("长度:" + getDist(stratPoint, endPoint) / 100
                        + "m 角度:" + getAngle(stratPoint, endPoint) + "°", localPath,
                hOffset, vOffset, this._textPaint);
    }


    /**
     * 计算两点成线，相对水平的角度
     *
     * @param startPoint
     * @param endPoint
     * @return
     */
    private int getAngle(XCKYPoint startPoint, XCKYPoint endPoint) {
        //转换为double高精度，避免除0问题
        return Math.abs((int) (180D * (Math.atan((double) (endPoint.y - startPoint.y) / (double) (endPoint.x - startPoint.x)) / Math.PI)));
    }


    /**
     * 按点检测是否选中控件
     *
     * @param x
     * @param y
     * @return
     */
    private boolean setSelect(float x, float y) {
        XCKYPoint clickPoint = new XCKYPoint(x, y);
        //窗
        for (String key : this._windowMap.keySet()) {
            WindowBean wdbean = this._windowMap.get(key);
            if (isHitLine(wdbean.getStartPoint(), wdbean.getEndPoint(), clickPoint, _hitLineCoolEye)) {
                wdbean.setSelect(true);
                return true;
            }
        }
        //门
        for (String key : this._doorMap.keySet()) {
            DoorBean doorbean = this._doorMap.get(key);
            if (isHitLine(doorbean.getStartPoint(), doorbean.getEndPoint(), clickPoint, _hitLineCoolEye)) {
                doorbean.setSelect(true);
                return true;
            }
            if (getRect(doorbean.getDragRect()).contains((int) x, (int) y)) {
                doorbean.setSelect(true);
                return true;
            }
        }
        //线
        for (String key : this._lineMap.keySet()) {
            LineBean linebean = this._lineMap.get(key);
            if (isHitLine(linebean, clickPoint, _hitLineCoolEye)) {
                linebean.setSelect(true);
                this._lineBean = linebean;
                return true;
            }
        }
        //路径
        for (String key : this._pathMap.keySet()) {
            PathBean pathbean = _pathMap.get(key);
            List<LineBean> list = pathbean.getLineBeans();
            for (LineBean linebean : list) {
                if (isHitLine(linebean, clickPoint, _hitLineCoolEye)) {
                    Log.v(TAG,"linebean _contains ");
                    pathbean.setSelect(true);
                    this._pathBean = pathbean;
                    linebean.setSelect(true);
                    this._lineBean = linebean;
                    return true;
                }
            }
        }
        //文字
        for (String key : this._drawTextMap.keySet()) {
            DrawTextBean drawtextbean = (DrawTextBean) this._drawTextMap.get(key);
            if ((new Rect(drawtextbean.getPoint().x, drawtextbean.getPoint().y, drawtextbean.getPoint().x + 2 * (int) drawtextbean.getTextPaint().measureText(drawtextbean.getText()), drawtextbean.getPoint().y + (int) drawtextbean.getTextPaint().measureText(drawtextbean.getText()))).contains((int) x, (int) y)) {
                drawtextbean.setSelect(true);
                return true;
            }
        }
        //模型
        for (String key : this._mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = (MouldPathBean) this._mouldPathMap.get(key);
            if (getRect(mouldpathbean.getRectobj()).contains((int) x, (int) y)) {
                mouldpathbean.setSelect(true);
                return true;
            }
            if (isHitPoint(mouldpathbean.getRotatePoint(), x, y, _hitPointCoolEye)) {
                mouldpathbean.setSelect(true);
                return true;
            }
        }
        //模型
        for (String key : this._mouldMap.keySet()) {
            MouldBean mouldbean = (MouldBean) this._mouldMap.get(key);
            if (getRect(mouldbean.getRectobj()).contains((int) x, (int) y)) {
                mouldbean.setSelect(true);
                return true;
            }
            if (isHitPoint(mouldbean.getRotatePoint(), x, y, _hitPointCoolEye)) {
                mouldbean.setSelect(true);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当前选中状态
     *
     * @param x
     * @param y
     */
    private void setMoveStatus(float x, float y) {
        this._moveOperType = MoveOperType.None;
        this._windowBean = null;
        this._doorBean = null;
        this._mouldBean = null;
        this._mouldPathBean = null;
        XCKYPoint startPoint, endPoint;
        //文字
        for (String key : this._drawTextMap.keySet()) {
            DrawTextBean drawtextbean = this._drawTextMap.get(key);
            if (drawtextbean.isSelect()) {
                _moveOperType = MoveOperType.MoveText;
                return;
            }
        }
        //窗
        for (String key : this._windowMap.keySet()) {
            WindowBean wdbean = (WindowBean) this._windowMap.get(key);
            this._windowBean = wdbean;
            if (wdbean.isSelect()) {
                startPoint = wdbean.getStartPoint();
                endPoint = wdbean.getEndPoint();
                if (isHitPoint(startPoint, x, y, _hitPointCoolEye)) {
                    _moveOperType = MoveOperType.MoveLineStartPoint;
                    return;
                }
                if (isHitPoint(endPoint, x, y, _hitPointCoolEye)) {
                    _moveOperType = MoveOperType.MoveLineEndPoint;
                    return;
                }
                if (isHitLine(startPoint, endPoint, x, y, _hitLineCoolEye)) {
                    _moveOperType = MoveOperType.MoveLine;
                    wdbean.setSelect(true);
                    return;
                }
            }
        }
        this._windowBean = null;
        //门
        for (String key : this._doorMap.keySet()) {
            DoorBean doorbean = (DoorBean) this._doorMap.get(key);
            _doorBean = doorbean;
            if (doorbean.isSelect()) {
                startPoint = doorbean.getStartPoint();
                endPoint = doorbean.getEndPoint();
                if (isHitPoint(startPoint, x, y, _hitPointCoolEye)) {
                    _moveOperType = MoveOperType.MoveLineStartPoint;
                    return;
                }
                if (isHitPoint(endPoint, x, y, _hitPointCoolEye)) {
                    _moveOperType = MoveOperType.MoveLineEndPoint;
                    return;
                }
                if (getRect(doorbean.getDragRect()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MoveDoorFrame;
                    return;
                }
                if (isHitLine(startPoint, endPoint, x, y, _hitLineCoolEye)) {
                    _moveOperType = MoveOperType.MoveLine;
                    doorbean.setSelect(true);
                    this._doorBean = doorbean;
                    return;
                }
            }
        }
        this._doorBean = null;
        //线
        if (this._lineBean != null && this._pathBean == null) {
            //超始点操作检测

            if (isHitPoint(_lineBean.getStartPoint(), x, y, _hitPointCoolEye)) {
                this._moveOperType = MoveOperType.MoveLineStartPoint;
                for (String key : this._lineMap.keySet()) {
                    LineBean linebean = this._lineMap.get(key);
                    startPoint = linebean.getStartPoint();
                    endPoint = linebean.getEndPoint();
                    //找对接线
                    if (!linebean.isSelect()) {
                        //开始点与当前选中线，点重合的线
                        if (startPoint.x == this._lineBean.getStartPoint().x && startPoint.y == this._lineBean.getStartPoint().y) {
                            linebean.setEqualPoint("Start");
                            this._connLineBean = linebean;
                            return;
                        }
                        //结束点与当前选中线，点重合的线
                        if (endPoint.x == this._lineBean.getStartPoint().x && endPoint.y == this._lineBean.getStartPoint().y) {
                            linebean.setEqualPoint("End");
                            this._connLineBean = linebean;
                            return;
                        }
                    }
                }
                this._connLineBean = null;
                return;
            }
            //结束点操作检测
            if (isHitPoint(_lineBean.getEndPoint(), x, y, _hitPointCoolEye)) {
                _moveOperType = MoveOperType.MoveLineEndPoint;
                for (String key : this._lineMap.keySet()) {
                    LineBean linebean = this._lineMap.get(key);
                    startPoint = linebean.getStartPoint();
                    endPoint = linebean.getEndPoint();
                    if (!linebean.isSelect()) {
                        //开始点与当前选中线重合
                        if (startPoint.x == this._lineBean.getEndPoint().x && startPoint.y == this._lineBean.getEndPoint().y) {
                            linebean.setEqualPoint("Start");
                            this._connLineBean = linebean;
                            return;
                        }
                        //结束点与当前选中线重合
                        if (endPoint.x == this._lineBean.getEndPoint().x && endPoint.y == this._lineBean.getEndPoint().y) {
                            linebean.setEqualPoint("End");
                            this._connLineBean = linebean;
                            return;
                        }
                    }
                }
                this._connLineBean = null;
                return;
            }

            //移动线操作检测
            if (isHitLine(this._lineBean.getStartPoint(), this._lineBean.getEndPoint(), x, y, _hitLineCoolEye)) {
                _moveOperType = MoveOperType.MoveLine;
                return;
            }
        }
        if (this._lineBean != null && this._pathBean != null) {
            if (isHitPoint(_lineBean.getStartPoint(), x, y, _hitPointCoolEye)) {
                _moveOperType = MoveOperType.MoveLineStartPoint;
                for (LineBean linebean : this._pathBean.getLineBeans()) {
                    startPoint = linebean.getStartPoint();
                    endPoint = linebean.getEndPoint();
                    if (!linebean.isSelect()) {
                        //开始点与当前选中线重合
                        if (startPoint.x == this._lineBean.getStartPoint().x && startPoint.y == this._lineBean.getStartPoint().y) {
                            linebean.setEqualPoint("Start");
                            this._connLineBean = linebean;
                            return;
                        }
                        //结束点与当前选中线重合
                        if (endPoint.x == this._lineBean.getStartPoint().x && endPoint.y == this._lineBean.getStartPoint().y) {
                            linebean.setEqualPoint("End");
                            this._connLineBean = linebean;
                            return;
                        }
                    }
                }
                this._connLineBean = null;
                return;
            }
            if (isHitPoint(_lineBean.getEndPoint(), x, y, _hitPointCoolEye)) {
                _moveOperType = MoveOperType.MoveLineEndPoint;
                for (LineBean linebean : this._pathBean.getLineBeans()) {
                    startPoint = linebean.getStartPoint();
                    endPoint = linebean.getEndPoint();
                    if (!linebean.isSelect()) {
                        //开始点与当前选中线重合
                        if (startPoint.x == this._lineBean.getEndPoint().x && startPoint.y == this._lineBean.getEndPoint().y) {
                            linebean.setEqualPoint("Start");
                            this._connLineBean = linebean;
                            return;
                        }
                        //结束点与当前选中线重合
                        if (endPoint.x == this._lineBean.getEndPoint().x && endPoint.y == this._lineBean.getEndPoint().y) {
                            linebean.setEqualPoint("End");
                            this._connLineBean = linebean;
                            return;
                        }
                    }
                }
                this._connLineBean = null;
                return;
            }
            //
            if (isHitLine(_lineBean.getStartPoint(), _lineBean.getEndPoint(), x, y, _hitLineCoolEye)) {
                _moveOperType = MoveOperType.MoveLine;
                return;
            }
        }

        //模型
        for (String key : this._mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = (MouldPathBean) this._mouldPathMap.get(key);
            this._mouldPathBean = mouldpathbean;
            if (mouldpathbean.isSelect()) {
//                if (isHitPoint(mouldpathbean.getRotatePoint(), x, y, _hitPointCoolEye)) {
//                    _moveOperType = MoveOperType.MouldRotate;
//                    return;
//                }

                if (_main.scalebox.isChecked() && getRect(mouldpathbean.getRectobj()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MouldPostScale;
                    return;
                }
                if (_main.dragbox.isChecked() && getRect(mouldpathbean.getRectobj()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MouldTranslate;
                    return;
                }
            }
        }
        this._mouldPathBean = null;
        //模型
        for (String key : this._mouldMap.keySet()) {
            MouldBean mouldbean = (MouldBean) this._mouldMap.get(key);
            this._mouldBean = mouldbean;
            if (mouldbean.isSelect()) {
                if (_main.rotationbox.isChecked() && getRect(mouldbean.getRectobj()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MouldRotate;
                    return;
                }
                if (_main.scalebox.isChecked() && getRect(mouldbean.getRectobj()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MouldPostScale;
                    return;
                }
                if (_main.dragbox.isChecked() && getRect(mouldbean.getRectobj()).contains((int) x, (int) y)) {
                    _moveOperType = MoveOperType.MouldTranslate;
                    return;
                }
            }
        }
        this._mouldBean = null;

    }

    /**
     * 获得画窗Path
     *
     * @param startPoint
     * @param endPoint
     * @return
     */
    private XCKYPath[] getWindowDrawPath(XCKYPoint startPoint, XCKYPoint endPoint) {
        XCKYPath[] arrayOfPath = new XCKYPath[2];
        XCKYPath startPath = new XCKYPath();
        XCKYPath endPath = new XCKYPath();
        //对比线的状态，x坐标差值越小，越倾向于垂直于x轴，同理y坐标差值越小越倾向于垂直于y轴
        if (Math.abs(endPoint.x - startPoint.x) > Math.abs(endPoint.y - startPoint.y)) {
            startPath.moveTo(startPoint.x, startPoint.y - 4);
            startPath.lineTo(endPoint.x, endPoint.y - 4);
            startPath.lineTo(endPoint.x, endPoint.y + 4);
            startPath.lineTo(startPoint.x, startPoint.y + 4);
            startPath.close();
            startPath.moveTo(startPoint.x, startPoint.y - 12);
            startPath.lineTo(endPoint.x, endPoint.y - 12);
            startPath.lineTo(endPoint.x, endPoint.y + 12);
            startPath.lineTo(startPoint.x, startPoint.y + 12);
            startPath.close();
            endPath.moveTo(startPoint.x, startPoint.y - 12);
            endPath.lineTo(endPoint.x, endPoint.y - 12);
            endPath.lineTo(endPoint.x, endPoint.y + 12);
            endPath.lineTo(startPoint.x, startPoint.y + 12);
            endPath.close();
        } else {
            startPath.moveTo(startPoint.x - 4, startPoint.y);
            startPath.lineTo(endPoint.x - 4, endPoint.y);
            startPath.lineTo(endPoint.x + 4, endPoint.y);
            startPath.lineTo(startPoint.x + 4, startPoint.y);
            startPath.close();
            startPath.moveTo(startPoint.x - 12, startPoint.y);
            startPath.lineTo(endPoint.x - 12, endPoint.y);
            startPath.lineTo(endPoint.x + 12, endPoint.y);
            startPath.lineTo(startPoint.x + 12, startPoint.y);
            startPath.close();
            endPath.moveTo(startPoint.x - 12, startPoint.y);
            endPath.lineTo(endPoint.x - 12, endPoint.y);
            endPath.lineTo(endPoint.x + 12, endPoint.y);
            endPath.lineTo(startPoint.x + 12, startPoint.y);
            endPath.close();
        }

        arrayOfPath[0] = startPath;
        arrayOfPath[1] = endPath;
        return arrayOfPath;
    }


    /**
     * 检测是否命中线
     *
     * @param startPoint
     * @param endPoint
     * @param x
     * @param y
     * @param dvalue
     * @return
     */
    private boolean isHitLine(XCKYPoint startPoint, XCKYPoint endPoint, float x, float y, double dvalue) {
        //依次为：线长，起始点与该点距离，结束点与该点距离
        double dline, dstart, dend;
        dline = Math.sqrt((startPoint.x - endPoint.x)
                * (startPoint.x - endPoint.x)
                + (startPoint.y - endPoint.y)
                * (startPoint.y - endPoint.y));
        dstart = Math.sqrt((x - startPoint.x)
                * (x - startPoint.x) + (y - startPoint.y)
                * (y - startPoint.y));
        dend = Math.sqrt((x - endPoint.x)
                * (x - endPoint.x) + (y - endPoint.y)
                * (y - endPoint.y));
        return (dstart + dend > dline) && (dstart + dend < dline + dvalue);
    }

    /**
     * 检测是否命中线
     *
     * @param startPoint
     * @param endPoint
     * @param x
     * @param y
     * @param dvalue
     * @return
     */
    private boolean isHitLine(XCKYPoint startPoint, XCKYPoint endPoint, XCKYPoint clickPoint, double dvalue) {
        return isHitLine(startPoint, endPoint, clickPoint.x, clickPoint.y, dvalue);
    }

    /**
     * 检测是否命中线
     *
     * @param line
     * @param clickPoint
     * @return
     */
    private boolean isHitLine(LineBean line, XCKYPoint clickPoint, double dvalue) {
        return isHitLine(line.getStartPoint(), line.getEndPoint(), clickPoint, dvalue);
    }

    /**
     * 检测是否命中点
     *
     * @param point
     * @param x
     * @param y
     * @param dvalue
     * @return
     */
    private boolean isHitPoint(XCKYPoint point, float x, float y, int dvalue) {
        return getDist(point, new XCKYPoint(x, y)) <= dvalue;
    }

    /**
     * 检测是否命中点
     *
     * @param point
     * @param clickPoint
     * @param dvalue
     * @return
     */
    private boolean isHitPoint(XCKYPoint point, XCKYPoint clickPoint, int dvalue) {
        return isHitPoint(point, clickPoint.x, clickPoint.y, dvalue);
    }


    /**
     * 重置按钮焦点
     */
    private void resetBtnFocus() {
        ((ImageButton) ((Activity) this._main).findViewById(R.id.straight_line))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.oblique_line))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.polygon_line))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.straight_wall))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.oblique_wall))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.polygon_wall))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.rect_wall))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.imp_model))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.window))
                .setBackgroundResource(R.drawable.button_selector2);
        ((ImageButton) ((Activity) this._main).findViewById(R.id.door))
                .setBackgroundResource(R.drawable.button_selector2);
    }

    /**
     * 设置按钮焦点
     */
    private void setBtnFocus() {
        switch (this._paintType) {
            case PaintType.StraightLine:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.straight_line))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.ObliqueLine:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.oblique_line))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.PolygonLine:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.polygon_line))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.StraightWall:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.straight_wall))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.ObliqueWall:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.oblique_wall))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.PolygonWall:
                ((ImageButton) ((Activity) this._main)
                        .findViewById(R.id.polygon_wall))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.RectWall:
                ((ImageButton) ((Activity) this._main).findViewById(R.id.rect_wall))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.Window:
                ((ImageButton) ((Activity) this._main).findViewById(R.id.window))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
            case PaintType.Door:
                ((ImageButton) ((Activity) this._main).findViewById(R.id.door))
                        .setBackgroundResource(R.drawable.__button_down);
                return;
        }

    }

    /**
     * 取消选中Path
     */
    private void setNoSelectPath() {
        //PathBean
        for (String key : _pathMap.keySet()) {
            PathBean pathbean = _pathMap.get(key);
            if (pathbean.isSelect()) {
                for (LineBean linebean : pathbean.getLineBeans()) {
                    linebean.setSelect(false);
                }
                pathbean.setSelect(false);
            }
        }
    }

    /**
     * 设置全不选中
     */
    private void setAllNoSelect() {
        //窗
        for (String key : this._windowMap.keySet()) {
            WindowBean wdbean = (WindowBean) this._windowMap.get(key);
            wdbean.setSelect(false);
        }
        //门
        for (String key : this._doorMap.keySet()) {
            DoorBean doorbean = (DoorBean) this._doorMap.get(key);
            doorbean.setSelect(false);
        }
        //线
        for (String key : this._lineMap.keySet()) {
            LineBean linebean = (LineBean) this._lineMap.get(key);
            linebean.setSelect(false);
        }
        //路径
        for (String key : this._pathMap.keySet()) {
            PathBean pathbean = (PathBean) _pathMap.get(key);
            pathbean.setSelect(false);
            //
            List<LineBean> list = pathbean.getLineBeans();
            for (LineBean linebean : list) {
                linebean.setSelect(false);
            }
        }
        //文字
        for (String key : this._drawTextMap.keySet()) {
            DrawTextBean drawtextbean = (DrawTextBean) this._drawTextMap.get(key);
            drawtextbean.setSelect(false);
        }
        //模型
        for (String key : this._mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = (MouldPathBean) this._mouldPathMap.get(key);
            mouldpathbean.setSelect(false);
        }
        //模型
        for (String key : this._mouldMap.keySet()) {
            MouldBean mouldbean = (MouldBean) this._mouldMap.get(key);
            mouldbean.setSelect(false);
        }
    }

    /**
     * 设置当前操作区域全选 中
     */
    private void setRectSelect() {
        Rect rect;
        if (_startPoint.x < _endPoint.x && _startPoint.y < _endPoint.y)
            rect = new Rect(_startPoint.x, _startPoint.y, _endPoint.x, _endPoint.y);
        else if (_startPoint.x > _endPoint.x && _startPoint.y > _endPoint.y)
            rect = new Rect(_endPoint.x, _endPoint.y, _startPoint.x, _startPoint.y);
        else if (_startPoint.x > _endPoint.x && _startPoint.y < _endPoint.y)
            rect = new Rect(_endPoint.x, _startPoint.y, _startPoint.x, _endPoint.y);
        else if (_startPoint.x < _endPoint.x && _startPoint.y > _endPoint.y)
            rect = new Rect(_startPoint.x, _endPoint.y, _endPoint.x, _startPoint.y);
        else
            rect = new Rect(_startPoint.x, _startPoint.y, _endPoint.x, _endPoint.y);
        //LineBean
        for (String key : _lineMap.keySet()) {
            Log.v(TAG,"line key = "+key);
            LineBean linebean = _lineMap.get(key);
            if (rect.contains(linebean.getStartPoint().x, linebean.getStartPoint().y) && rect.contains(linebean.getEndPoint().x, linebean.getEndPoint().y)) {
                Log.v(TAG,"contains");
                if (linebean.isSelect())
                    linebean.setSelect(false);
                else
                    linebean.setSelect(true);
            }
        }
        //PathBean
        for (String key : _pathMap.keySet()) {
            PathBean pathbean = _pathMap.get(key);
            Log.v(TAG,"_pathMap key = "+key);
            for (LineBean line : pathbean.getLineBeans()) {
                if (rect.contains(line.getStartPoint().x, line.getStartPoint().y) && rect.contains(line.getEndPoint().x, line.getEndPoint().y)) {
                    Log.v(TAG,"_pathMap _contains ");
                    Log.v(TAG,"_pathMap pathbean is selected =  "+pathbean.isSelect());
                    if (pathbean.isSelect())
                        pathbean.setSelect(false);
                    else
                        pathbean.setSelect(true);
                }
            }
        }
        //DrawTextBean
        for (String key : _drawTextMap.keySet()) {
            DrawTextBean drawtextbean = (DrawTextBean) _drawTextMap.get(key);
            if (rect.contains(drawtextbean.getPoint().x, drawtextbean.getPoint().y)) {
                if (drawtextbean.isSelect())
                    drawtextbean.setSelect(false);
                else
                    drawtextbean.setSelect(true);
            }
        }
        //MouldPathBean
        for (String key : _mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = (MouldPathBean) _mouldPathMap.get(key);
            if (rect.contains((int) mouldpathbean.getRectobj().left, (int) mouldpathbean.getRectobj().top) && rect.contains((int) mouldpathbean.getRectobj().right, (int) mouldpathbean.getRectobj().bottom)) {
                if (mouldpathbean.isSelect())
                    mouldpathbean.setSelect(false);
                else
                    mouldpathbean.setSelect(true);
            }
        }
        //MouldBean
        for (String key : _mouldMap.keySet()) {
            MouldBean mouldbean = _mouldMap.get(key);
            if (rect.contains((int) mouldbean.getRectobj().left, (int) mouldbean.getRectobj().top) && rect.contains((int) mouldbean.getRectobj().right, (int) mouldbean.getRectobj().bottom)) {
                if (mouldbean.isSelect())
                    mouldbean.setSelect(false);
                else
                    mouldbean.setSelect(true);
            }
        }
        //WindowBean
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = _windowMap.get(key);
            if (rect.contains(windowbean.getStartPoint().x, windowbean.getStartPoint().y) && rect.contains(windowbean.getEndPoint().x, windowbean.getEndPoint().y)) {
                if (windowbean.isSelect())
                    windowbean.setSelect(false);
                else
                    windowbean.setSelect(true);
            }
        }
        //DoorBean
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = _doorMap.get(key);
            if (rect.contains(doorbean.getStartPoint().x, doorbean.getStartPoint().y) && rect.contains(doorbean.getEndPoint().x, doorbean.getEndPoint().y)) {
                if (doorbean.isSelect())
                    doorbean.setSelect(false);
                else
                    doorbean.setSelect(true);
            }
        }
    }

    /*
     * 返回所有数据
	 */
    public List<GatherBean> getData() {
        return this._gatherList;
    }

    /**
     * 加载数据
     *
     * @param gatherbean
     */
    public void setData(GatherBean gatherbean) {
        if (gatherbean.getTableBean() != null) {
            _tableBean = gatherbean.getTableBean();
        }
        _lineMap = gatherbean.getLinesMap();
        _drawTextMap = gatherbean.getDrawTextMap();
        _mouldPathMap = gatherbean.getMouldPathMap();
        _mouldMap = gatherbean.getMouldBeanMap();
        _windowMap = gatherbean.getWindowPathMap();
        _doorMap = gatherbean.getDoorPathMap();
        _pathMap = gatherbean.getPaths();

        //重新生成PATH-不存储可提高性能
//		for( MouldPathBean mouldpath:_mouldPathMap.values())
//		{
//			XCKYPath mpath = getDrawPath(mouldpath.getPathList());
//			//转换
//			if(mouldpath.getMatrix()!=null)
//			{
//				mpath.transform(mouldpath.getMatrix());
//			}
//			mouldpath.setPath(mpath);
//		}
//		//重新生成PATH
//		for( MouldBean mould:_mouldMap.values())
//		{
//			XCKYPath mpath = getDrawPath(mould);
//			//转换
//			if(mould.getMatrix()!=null)
//			{
//				mpath.transform(mould.getMatrix());
//			}
//			mould.setPath(mpath);
//		}

        //
    }

    /**
     * 保存数据：聚合所有图形数据
     */
    private void saveData() {
        GatherBean gatherbean = new GatherBean();
        //PathBean
        Map<String, PathBean> pathMap = new HashMap<String, PathBean>();
        for (String key : _pathMap.keySet()) {
            PathBean pathbean = new PathBean(_pathMap.get(key));
            pathbean.setSelect(false);
            pathMap.put(key, pathbean);
        }
        gatherbean.setPaths(pathMap);
        //DrawTextBean
        Map<String, DrawTextBean> drawTextMap = new HashMap<String, DrawTextBean>();
        for (String key : _drawTextMap.keySet()) {
            DrawTextBean drawtextbean = new DrawTextBean(_drawTextMap.get(key));
            drawtextbean.setSelect(false);
            drawTextMap.put(key, drawtextbean);
        }
        gatherbean.setDrawTextMap(drawTextMap);
        //LineBean
        Map<String, LineBean> lineMap = new HashMap<String, LineBean>();
        for (String key : _lineMap.keySet()) {
            LineBean linebean = new LineBean(_lineMap.get(key));
            linebean.setSelect(false);
            lineMap.put(key, linebean);
        }
        gatherbean.setLinesMap(lineMap);
        //MouldPathBean
        Map<String, MouldPathBean> mouldPathBeanMap = new HashMap<String, MouldPathBean>();
        for (String key : _mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = new MouldPathBean(_mouldPathMap.get(key));
            mouldpathbean.setSelect(false);
            mouldPathBeanMap.put(key, mouldpathbean);
        }
        gatherbean.setMouldPathMap(mouldPathBeanMap);
        //MouldBean
        Map<String, MouldBean> mouldBeanMap = new HashMap<String, MouldBean>();
        for (String key : _mouldMap.keySet()) {
            MouldBean mouldbean = new MouldBean(_mouldMap.get(key));
            mouldbean.setSelect(false);
            mouldBeanMap.put(key, mouldbean);
        }
        gatherbean.setMouldBeanMap(mouldBeanMap);
        //WindowBean
        Map<String, WindowBean> windowBeanMap = new HashMap<String, WindowBean>();
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = new WindowBean(_windowMap.get(key));
            windowbean.setSelect(false);
            windowBeanMap.put(key, windowbean);
        }
        gatherbean.setWindowPathMap(windowBeanMap);
        //DoorBean
        Map<String, DoorBean> doorBeanMap = new HashMap<String, DoorBean>();
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = new DoorBean(_doorMap.get(key));
            doorbean.setSelect(false);
            doorBeanMap.put(key, doorbean);
        }
        gatherbean.setDoorPathMap(doorBeanMap);

        //使用撤销、恢复后对失效元素删除
        if (_curGatherIndex >= 1 && _curGatherIndex < _gatherList.size()) {
            int count = _gatherList.size();
            while (count > _curGatherIndex) {
                _gatherList.remove(count - 1);
                count--;
            }
        }


        //保存
        gatherbean.setTableBean(_tableBean);
        _gatherList.add(gatherbean);
        _curGatherIndex = _gatherList.size();
        if (_gatherList.size() > 20) {
            _gatherList.remove(0);
            _curGatherIndex = _gatherList.size();
        }
        return;
    }

    /**
     * 返回绘制图形
     */
    public Bitmap getBitMap(int w, int h) {

        Canvas canva = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        canva.setBitmap(bitmap);
        canva.drawColor(Color.WHITE);
        canva.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.north_arrow), 38, 63, false), w - 38 - 50, 5F, null);
        //tableBean
        if (_tableBean != null) {

            canva.drawText(_tableBean.getTitleText(), (w - _tableBean.getPaintTitle().measureText(_tableBean.getTitleText())) / 2, 45F, _tableBean.getPaintTitle());
            canva.drawRect(getRect(_tableBean.getSetTabRect(w, h)), _tableBean.getPaintTab());
            int index = 0;
            for (XCKYPoint[] pointarray : _tableBean.getSetLines(w, h)) {
                canva.drawLine(pointarray[0].x, pointarray[0].y, pointarray[1].x, pointarray[1].y, _tableBean.getPaintTab());
                if (index > 0) {
                    canva.drawText((_tableBean.getTxts().get(index - 1))[0], w - 295 + 10, 18 + pointarray[0].y, _tableBean.getPaintText());
                    if ((_tableBean.getTxts().get(index - 1))[1].length() > 13) {
                        canva.drawText((_tableBean.getTxts().get(index - 1))[1].substring(0, 13), w - 210f, 18 + pointarray[0].y, _tableBean.getPaintText());
                        canva.drawText((_tableBean.getTxts().get(index - 1))[1].substring(13), w - 210f, 40 + pointarray[0].y, _tableBean.getPaintText());
                    } else {
                        canva.drawText((_tableBean.getTxts().get(index - 1))[1], w - 210f, 18 + pointarray[0].y, _tableBean.getPaintText());
                    }
                }
                if (index == _tableBean.getLines().size() - 1) {
                    canva.drawText((_tableBean.getTxts().get(index))[0], w - 295 + 10, -8 + pointarray[0].y, _tableBean.getPaintText());
                    canva.drawText((_tableBean.getTxts().get(index))[1], w - 210f, -8 + pointarray[0].y, _tableBean.getPaintText());
                }
                index++;
            }
        }
        //LineBean
        for (String key : _lineMap.keySet()) {
            LineBean linebean = _lineMap.get(key);
            canva.drawLine(linebean.getStartPoint().x, linebean.getStartPoint().y, linebean.getEndPoint().x, linebean.getEndPoint().y, linebean.getPaint());
        }
        //PathBean
        for (String key : _pathMap.keySet()) {
            PathBean pathbean = _pathMap.get(key);
            canva.drawPath(getDrawPath(pathbean.getPath()), pathbean.getPaint());
        }
        //DrawTextBean
        for (String key : _drawTextMap.keySet()) {
            DrawTextBean drawtextbean = _drawTextMap.get(key);
            if (drawtextbean.getText() != null) {
                canva.drawText(drawtextbean.getText(), drawtextbean.getPoint().x, 20 + drawtextbean.getPoint().y, _textPaint);
            }
        }
        //MouldPathBean
        for (String key : _mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = _mouldPathMap.get(key);
            XCKYPath mpath = getDrawPath(mouldpathbean.getPathList());
            //转换
            if (mouldpathbean.getMatrix() != null) {
                mpath.transform(mouldpathbean.getMatrix());
            }
            canva.drawPath(mpath, mouldpathbean.getPathPaint());
        }
        //MouldBean
        for (String key : _mouldMap.keySet()) {
            MouldBean mouldbean = _mouldMap.get(key);
            XCKYPath path = getDrawPath(mouldbean);
            //转换
            if (mouldbean.getMatrix() != null) {
                path.transform(mouldbean.getMatrix());
            }
            canva.drawPath(path, mouldbean.getPathPaint());
        }
        //WindowBean
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = _windowMap.get(key);
            XCKYPath pathArray[] = getWindowDrawPath(windowbean.getStartPoint(), windowbean.getEndPoint());
            canva.drawPath(pathArray[1], windowbean.getPathPaint());
            canva.drawPath(pathArray[0], windowbean.getPaint());
        }
        //DoorBean
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = _doorMap.get(key);
            if (doorbean.getDoorEndPoint().x > 0 || doorbean.getDoorEndPoint().y > 0) {
                canva.drawLine(doorbean.getEndPoint().x, doorbean.getEndPoint().y, doorbean.getDoorEndPoint().x, doorbean.getDoorEndPoint().y, doorbean.getDoorPaint());
            }
            XCKYPath path = getDoorDrawPath(doorbean);
            if (doorbean.getDoorEndPoint().x > 0 || doorbean.getDoorEndPoint().y > 0) {
                canva.drawPath(path, doorbean.getArcPaint());
            }
            canva.drawLine(doorbean.getStartPoint().x, doorbean.getStartPoint().y, doorbean.getEndPoint().x, doorbean.getEndPoint().y, doorbean.getBottomPaint());
        }
        canva.save();
        canva.restore();
        return bitmap;
    }


    /**
     * 恢复，还原
     */
    public Boolean restore() {
        _curGatherIndex = _curGatherIndex - 1;
        if (_curGatherIndex <= 0) {
            _curGatherIndex = 1;
            return false;
        }
        if (_curGatherIndex > 0) {
            GatherBean gatherbean = (GatherBean) _gatherList.get(_curGatherIndex - 1);
            if (gatherbean != null) {
                this.setData(new GatherBean(gatherbean));
            }
            return true;
        }
        return false;
    }

    /**
     * 撤销
     */
    public Boolean revocation() {
        _curGatherIndex = _curGatherIndex + 1;
        if (this._curGatherIndex > this._gatherList.size()) {
            this._curGatherIndex = this._gatherList.size();
            return false;
        }
        if (this._curGatherIndex > 0) {
            GatherBean gatherbean = (GatherBean) this._gatherList.get(_curGatherIndex - 1);
            if (gatherbean != null) {
                this.setData(new GatherBean(gatherbean));
            }
            return true;
        }
        return false;
    }

    /**
     * 删除选中的元素
     */
    public void deleteSelect() {
        //PathBean
        for (Iterator<Map.Entry<String, PathBean>> iter = _pathMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, PathBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //DrawTextBean
        for (Iterator<Map.Entry<String, DrawTextBean>> iter = _drawTextMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, DrawTextBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //LineBean
        for (Iterator<Map.Entry<String, LineBean>> iter = _lineMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, LineBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //MouldPathBean
        for (Iterator<Map.Entry<String, MouldPathBean>> iter = _mouldPathMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, MouldPathBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //MouldBean
        for (Iterator<Map.Entry<String, MouldBean>> iter = _mouldMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, MouldBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //WindowBean
        for (Iterator<Map.Entry<String, WindowBean>> iter = _windowMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, WindowBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //DoorBean
        for (Iterator<Map.Entry<String, DoorBean>> iter = _doorMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, DoorBean> entry = iter.next();
            if (entry.getValue().isSelect())
                iter.remove();
        }
        //
        saveData();
    }

    /**
     * 绘画
     */
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        // 设置画布上的文字
        DisplayMetrics dm = new DisplayMetrics();
        _main.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;

        canvas.drawText("比例 :1.0m==100px", 20.0F, 40.0F, this._textPaint);
        canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.north_arrow), 38,
                63, false), w - 38 - 50, 5.0F, null);
        if (this._tableBean != null) {
            canvas.drawText(this._tableBean.getTitleText(), (w - this._tableBean.getPaintTitle().measureText(this._tableBean.getTitleText())) / 2, 45.0F, this._tableBean.getPaintTitle());
        }
        // 画布背景线条
        this.setCanvasGrid(canvas);

        //文字
        for (String key : _drawTextMap.keySet()) {
            DrawTextBean drawtextbean = _drawTextMap.get(key);
            if (drawtextbean.isSelect())
                canvas.drawRect(new Rect(drawtextbean.getPoint().x, drawtextbean.getPoint().y, drawtextbean.getPoint().x + 2 * (int) drawtextbean.getTextPaint().measureText(drawtextbean.getText()), 30 + drawtextbean.getPoint().y), drawtextbean.getTextPaint());
            if (drawtextbean.getText() != null)
                canvas.drawText(drawtextbean.getText(), drawtextbean.getPoint().x, 20 + drawtextbean.getPoint().y, _textPaint);
        }

        //线
        for (String key : _lineMap.keySet()) {
            LineBean linebean = (LineBean) _lineMap.get(key);
            if (this._status_move) {
                canvas.drawCircle(linebean.getStartPoint().x, linebean.getStartPoint().y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(linebean.getEndPoint().x, linebean.getEndPoint().y, _hitPointStyleSize, _selPointPaint);
                drawLineText(canvas, linebean, 0, 30);
            }
            canvas.drawLine(linebean.getStartPoint().x, linebean.getStartPoint().y, linebean.getEndPoint().x, linebean.getEndPoint().y, linebean.getPaint());
        }

        //路径
        for (String key : this._pathMap.keySet()) {
            PathBean pathbean = _pathMap.get(key);
            //
            List<LineBean> list = pathbean.getLineBeans();
            for (LineBean linebean : list) {
                if (this._status_move) {
                    canvas.drawCircle(linebean.getStartPoint().x, linebean.getStartPoint().y, _hitPointStyleSize, _selPointPaint);
                    canvas.drawCircle(linebean.getEndPoint().x, linebean.getEndPoint().y, _hitPointStyleSize, _selPointPaint);
                    drawLineText(canvas, linebean, 0, 30);
                }
            }

            //更新绘制点
            if (pathbean.isChange()) {
                PathPointBean pathpointbean = new PathPointBean();
                //开始点
                pathpointbean.setStartPoint(list.get(0).getStartPoint().x, list.get(0).getStartPoint().y);
                for (LineBean linebean : list) {
                    pathpointbean.setPointList(linebean.getEndPoint().x, linebean.getEndPoint().y);
                }
                pathpointbean.setClose(pathbean.getPath().isClose());
                pathbean.setPath(pathpointbean);
                pathbean.setChange(false);
            } else {
                if (this._colsePath)
                    canvas.drawCircle((pathbean.getLineBeans().get(0)).getStartPoint().x, (pathbean.getLineBeans().get(0)).getStartPoint().y, 50F, pathbean.getCirclePaint());
            }
            canvas.drawPath(getDrawPath(pathbean.getPath()), pathbean.getPaint());
        }

        //模型
        for (String key : this._mouldPathMap.keySet()) {
            MouldPathBean mouldpathbean = (MouldPathBean) this._mouldPathMap.get(key);
            XCKYPath mpath = getDrawPath(mouldpathbean.getPathList());
            //转换
            if (mouldpathbean.getMatrix() != null) {
                mpath.transform(mouldpathbean.getMatrix());
            }
            canvas.drawPath(mpath, mouldpathbean.getPathPaint());
            if (mouldpathbean.isSelect()) {
                XCKYPaint paint = new XCKYPaint();
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(15F);
                canvas.drawRect(getRect(mouldpathbean.getRectobj()), mouldpathbean.getRectPaint());
                canvas.drawPoint(mouldpathbean.getRectobj().right - 5F, mouldpathbean.getRectobj().bottom - 5F, paint);
                paint.setStyle(android.graphics.Paint.Style.STROKE);
                paint.setStrokeWidth(1.0F);
                canvas.drawCircle(mouldpathbean.getRotatePoint().x, mouldpathbean.getRotatePoint().y, 10F, paint);
            }
        }

        //模型
        for (String key : this._mouldMap.keySet()) {
            MouldBean mouldbean = (MouldBean) this._mouldMap.get(key);
            XCKYPath mpath = getDrawPath(mouldbean);
            if (mouldbean.getMatrix() != null) {
                mpath.transform(mouldbean.getMatrix());
            }
            canvas.drawPath(mpath, mouldbean.getPathPaint());
            if (mouldbean.isSelect()) {
                XCKYPaint paint = new XCKYPaint();
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(15F);
                canvas.drawRect(getRect(mouldbean.getRectobj()), mouldbean.getRectPaint());
                canvas.drawPoint(mouldbean.getRectobj().right - 5F, mouldbean.getRectobj().bottom - 5F, paint);
                paint.setStyle(android.graphics.Paint.Style.STROKE);
                paint.setStrokeWidth(1.0F);
                canvas.drawCircle(mouldbean.getRotatePoint().x, mouldbean.getRotatePoint().y, 10F, paint);
            }
        }

        //窗
        if (this._windowBean != null) {
            XCKYPath[] patharray = getWindowDrawPath(_windowBean.getStartPoint(), _windowBean.getEndPoint());
            canvas.drawPath(patharray[1], _windowBean.getPathPaint());
            canvas.drawPath(patharray[0], _windowBean.getPaint());
            drawLineText(canvas, _windowBean.getStartPoint(), _windowBean.getEndPoint(), 0, 30);
        }
        for (String key : _windowMap.keySet()) {
            WindowBean windowbean = (WindowBean) _windowMap.get(key);
            XCKYPath[] patharray = getWindowDrawPath(windowbean.getStartPoint(), windowbean.getEndPoint());
            if (windowbean.isSelect()) {
                canvas.drawCircle(windowbean.getStartPoint().x, windowbean.getStartPoint().y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(windowbean.getEndPoint().x, windowbean.getEndPoint().y, _hitPointStyleSize, _selPointPaint);
            }
            canvas.drawPath(patharray[1], windowbean.getPathPaint());
            canvas.drawPath(patharray[0], windowbean.getPaint());
        }

        //门
        if (this._doorBean != null) {
            if (_doorBean.getDoorEndPoint().x > 0 || _doorBean.getDoorEndPoint().y > 0)
                canvas.drawLine(_doorBean.getEndPoint().x, _doorBean.getEndPoint().y, _doorBean.getDoorEndPoint().x, _doorBean.getDoorEndPoint().y, _doorBean.getDoorPaint());
            XCKYPath doorPath = getDoorDrawPath(_doorBean);
            if (_doorBean.getDoorEndPoint().x > 0 || _doorBean.getDoorEndPoint().y > 0)
                canvas.drawPath(doorPath, _doorBean.getArcPaint());
            canvas.drawLine(_doorBean.getStartPoint().x, _doorBean.getStartPoint().y, _doorBean.getEndPoint().x, _doorBean.getEndPoint().y, _doorBean.getBottomPaint());
            drawLineText(canvas, _doorBean.getStartPoint(), _doorBean.getEndPoint(), 0, 30);
            if (_doorBean.isSelect()) {
                canvas.drawCircle(_doorBean.getStartPoint().x, _doorBean.getStartPoint().y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(_doorBean.getEndPoint().x, _doorBean.getEndPoint().y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(getRect(_doorBean.getDragRect()).centerX(), getRect(_doorBean.getDragRect()).centerY(), 15F, _selPointPaint);
            }
            if (_doorBean.isChange()) {
                canvas.drawCircle(_doorBean.getPoints()[0].x, _doorBean.getPoints()[0].y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(_doorBean.getPoints()[1].x, _doorBean.getPoints()[1].y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(_doorBean.getPoints()[2].x, _doorBean.getPoints()[2].y, _hitPointStyleSize, _selPointPaint);
                canvas.drawCircle(_doorBean.getPoints()[3].x, _doorBean.getPoints()[3].y, _hitPointStyleSize, _selPointPaint);
            }
        }
        for (String key : _doorMap.keySet()) {
            DoorBean doorbean = _doorMap.get(key);
            if (doorbean.getDoorEndPoint().x > 0 || doorbean.getDoorEndPoint().y > 0)
                canvas.drawLine(doorbean.getEndPoint().x, doorbean.getEndPoint().y, doorbean.getDoorEndPoint().x, doorbean.getDoorEndPoint().y, doorbean.getDoorPaint());
            XCKYPath path = getDoorDrawPath(doorbean);
            if (doorbean.getDoorEndPoint().x > 0 || doorbean.getDoorEndPoint().y > 0)
                canvas.drawPath(path, doorbean.getArcPaint());
            canvas.drawLine(doorbean.getStartPoint().x, doorbean.getStartPoint().y, doorbean.getEndPoint().x, doorbean.getEndPoint().y, doorbean.getBottomPaint());
        }

        //画线状态
        if (this._status_line) {
            if (_paintType == PaintType.RectWall) {
                XCKYPath path = new XCKYPath();
                path.moveTo(_startPoint.x, _startPoint.y);
                path.lineTo(_endPoint.x, _startPoint.y);
                path.lineTo(_endPoint.x, _endPoint.y);
                path.lineTo(_startPoint.x, _endPoint.y);
                path.close();
                canvas.drawPath(path, _paint);
                //线文字说明
                drawLineText(canvas, _startPoint, new XCKYPoint(_endPoint.x, _startPoint.y), 0, 30);
                drawLineText(canvas, new XCKYPoint(_endPoint.x, _startPoint.y), _endPoint, 0, 30);
                drawLineText(canvas, _endPoint, new XCKYPoint(_startPoint.x, _endPoint.y), 0, 30);
                drawLineText(canvas, new XCKYPoint(_startPoint.x, _endPoint.y), _startPoint, 0, 30);
            } else if (_startPoint != null && _endPoint != null) {
                canvas.drawLine(_startPoint.x, _startPoint.y, _endPoint.x, _endPoint.y, _paint);
                drawLineText(canvas, _startPoint, _endPoint, 0, 30);
            }
        }

        //选择状态
        if (this._status_sel) {
            XCKYPath path = new XCKYPath();
            path.moveTo(_startPoint.x, _startPoint.y);
            path.lineTo(_endPoint.x, _startPoint.y);
            path.lineTo(_endPoint.x, _endPoint.y);
            path.lineTo(_startPoint.x, _endPoint.y);
            path.close();
            canvas.drawPath(path, _paint);
        }

        return;
    }

    /**
     * 触摸事件
     */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (this._startPoint == null) {
                this._startPoint = new XCKYPoint();
            }
            if (this._endPoint == null) {
                this._endPoint = new XCKYPoint();
            }
            this._x = motionEvent.getX();
            this._y = motionEvent.getY();
            Log.v(TAG,"ACTION_DOWN sele = "+_status);
            switch (this._status) {
                case ExperModeStatus.Sel:
                case ExperModeStatus.Move:
                    if (setSelect(_x, _y)) {
                        setMoveStatus(_x, _y);
                        this._status = ExperModeStatus.Move;
                    } else {
                        setAllNoSelect();
                        this._status = ExperModeStatus.Sel;
                        this._startPoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
                    }
                    break;
                case ExperModeStatus.Edit:
                    switch (this._paintType) {
                        case PaintType.Text:
                            this._status_lineText = true;
                            _main.getDrawText(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                            break;
                        case PaintType.PolygonLine:
                        case PaintType.PolygonWall:
                        case PaintType.RectWall:
                            _startPoint.set(motionEvent.getX(), motionEvent.getY());
                            if (_pathing.getPath() == null) {
                                _pathing.setPath(new PathPointBean());
                                _pathing.getPath().setStartPoint(motionEvent.getX(), motionEvent.getY());
                            } else {
                                _startPoint.set(_endPoint.x, _endPoint.y);
                            }
                            break;
                        case PaintType.Window:
                            //线
                            for (String key : _lineMap.keySet()) {
                                LineBean linebean = _lineMap.get(key);
                                //命中线
                                if (isHitLine(linebean.getStartPoint(), linebean.getEndPoint(), _x, _y, 2.0D)) {
                                    XCKYPoint newStartPoint;
                                    _windowBean = new WindowBean();
                                    _windowBean.setLineUid(linebean.getLineUid());
                                    _windowBean.setLineStartPoint(new XCKYPoint(linebean.getStartPoint()));
                                    _windowBean.setLineEndPoint(new XCKYPoint(linebean.getEndPoint()));
                                    newStartPoint = getCrossPoint(_windowBean.getLineStartPoint(), _windowBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                    _startPoint.set(newStartPoint.x, newStartPoint.y);
                                    _windowBean.setStartPoint(new XCKYPoint(_startPoint));
                                    this._status_lineWindow = true;
                                }
                            }
                            //路径
                            for (String key : _pathMap.keySet()) {
                                PathBean pathbean = _pathMap.get(key);
                                for (LineBean linebean : pathbean.getLineBeans()) {
                                    //命中线
                                    if (isHitLine(linebean.getStartPoint(), linebean.getEndPoint(), _x, _y, 2.0D)) {
                                        XCKYPoint newStartPoint;
                                        _windowBean = new WindowBean();
                                        _windowBean.setLineUid(linebean.getLineUid());
                                        _windowBean.setLineStartPoint(new XCKYPoint(linebean.getStartPoint()));
                                        _windowBean.setLineEndPoint(new XCKYPoint(linebean.getEndPoint()));
                                        newStartPoint = getCrossPoint(_windowBean.getLineStartPoint(), _windowBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                        _startPoint.set(newStartPoint.x, newStartPoint.y);
                                        _windowBean.setStartPoint(new XCKYPoint(_startPoint));
                                        this._status_lineWindow = true;
                                    }
                                }
                            }
                            //检测是否成功命中线
                            if (!this._status_lineWindow) {
                                _main.showToast("未点中任何线");
                                this._status_lineWindow = true;
                            }
                            break;
                        case PaintType.Door:
                            //线
                            for (String key : _lineMap.keySet()) {
                                LineBean linebean = _lineMap.get(key);
                                //命中线
                                if (isHitLine(linebean.getStartPoint(), linebean.getEndPoint(), _x, _y, 2.0D)) {
                                    XCKYPoint newStartPoint;
                                    _doorBean = new DoorBean();
                                    _doorBean.setLineUid(linebean.getLineUid());
                                    _doorBean.setLineStartPoint(new XCKYPoint(linebean.getStartPoint()));
                                    _doorBean.setLineEndPoint(new XCKYPoint(linebean.getEndPoint()));
                                    newStartPoint = getCrossPoint(_doorBean.getLineStartPoint(), _doorBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                    _startPoint.set(newStartPoint.x, newStartPoint.y);
                                    _doorBean.setStartPoint(new XCKYPoint(_startPoint));
                                    _doorBean.setEndPoint(new XCKYPoint(linebean.getEndPoint()));
                                    this._status_lineDoor = true;
                                }
                            }
                            //路径
                            for (String key : _pathMap.keySet()) {
                                PathBean pathbean = _pathMap.get(key);
                                for (LineBean linebean : pathbean.getLineBeans()) {
                                    //命中线
                                    if (isHitLine(linebean.getStartPoint(), linebean.getEndPoint(), _x, _y, 2.0D)) {
                                        XCKYPoint newStartPoint;
                                        _doorBean = new DoorBean();
                                        _doorBean.setLineUid(linebean.getLineUid());
                                        _doorBean.setLineStartPoint(new XCKYPoint(linebean.getStartPoint()));
                                        _doorBean.setLineEndPoint(new XCKYPoint(linebean.getEndPoint()));
                                        newStartPoint = getCrossPoint(_doorBean.getLineStartPoint(), _doorBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                        _startPoint.set(newStartPoint.x, newStartPoint.y);
                                        _doorBean.setStartPoint(new XCKYPoint(_startPoint));
                                        this._status_lineDoor = true;
                                    }
                                }
                            }
                            if (!this._status_lineDoor) {
                                _main.showToast("未点中任何线");
                                this._status_lineDoor = true;
                            }
                            break;
                        case PaintType.StraightLine:
                        case PaintType.ObliqueLine:
                        case PaintType.StraightWall:
                        case PaintType.ObliqueWall:
                        default:
                            _startPoint.set(motionEvent.getX(), motionEvent.getY());
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            break;
                    }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            this._status_move = false;
            Log.v(TAG,"move sele = "+_status);
            switch (this._status) {
                case ExperModeStatus.Sel:
                    this._endPoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
                    this._status_sel = true;
                    break;
                case ExperModeStatus.Move:
                    float fx = motionEvent.getX() - _x;
                    float fy = motionEvent.getY() - _y;
                    _x = motionEvent.getX();
                    _y = motionEvent.getY();
                    switch (_moveOperType) {
                        case MoveOperType.MoveLineStartPoint:
                            XCKYPoint startLinePoint;
                            XCKYPoint endLinePoint;

                            if (this._pathBean != null) {
                                this._pathBean.setChange(true);
                                //如果移动时x方向或者y方向移动较小则忽略不计
                                if (Math.abs(motionEvent.getX() - this._lineBean.getEndPoint().x) < 10F && Math.abs(motionEvent.getX() - this._lineBean.getEndPoint().x) != 0.0F) {
                                    _lineBean.setStartPoint(new XCKYPoint(_lineBean.getEndPoint().x, motionEvent.getY()));
                                    syncWindowDoor(_lineBean);
                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(_lineBean.getEndPoint().x, motionEvent.getY()));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(_lineBean.getEndPoint().x, motionEvent.getY()));
                                        //
                                        syncWindowDoor(_connLineBean);
                                    }
                                } else if (Math.abs(motionEvent.getY() - _lineBean.getEndPoint().y) < 10F && Math.abs(motionEvent.getY() - _lineBean.getEndPoint().y) != 0.0F) {
                                    _lineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getEndPoint().y));
                                    syncWindowDoor(_lineBean);
                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getEndPoint().y));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getEndPoint().y));
                                        //
                                        syncWindowDoor(_connLineBean);
                                    }
                                } else  //正常移动
                                {
                                    _lineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                    syncWindowDoor(_lineBean);

                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));

                                        //如果移动时x方向或者y方向移动较小则忽略不计
                                        if (_connLineBean.getEqualPoint().equals("Start")) {
                                            if (Math.abs(motionEvent.getX() - _connLineBean.getEndPoint().x) < 10F && Math.abs(motionEvent.getX() - _connLineBean.getEndPoint().x) != 0.0F) {
                                                _connLineBean.setStartPoint(new XCKYPoint(_connLineBean.getEndPoint().x, motionEvent.getY()));
                                                _lineBean.setStartPoint(new XCKYPoint(_connLineBean.getEndPoint().x, motionEvent.getY()));
                                            } else if (Math.abs(motionEvent.getY() - _connLineBean.getEndPoint().y) < 10F && Math.abs(motionEvent.getY() - _connLineBean.getEndPoint().y) != 0.0F) {
                                                _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getEndPoint().y));
                                                _lineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getEndPoint().y));
                                            }
                                        } else if (_connLineBean.getEqualPoint().equals("End")) {
                                            if (Math.abs(motionEvent.getX() - _connLineBean.getStartPoint().x) < 10F && Math.abs(motionEvent.getX() - _connLineBean.getStartPoint().x) != 0.0F) {
                                                _connLineBean.setEndPoint(new XCKYPoint(_connLineBean.getStartPoint().x, motionEvent.getY()));
                                                _lineBean.setStartPoint(new XCKYPoint(_connLineBean.getStartPoint().x, motionEvent.getY()));
                                            } else if (Math.abs(motionEvent.getY() - _connLineBean.getStartPoint().y) < 10F && Math.abs(motionEvent.getY() - _connLineBean.getStartPoint().y) != 0.0F) {
                                                _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getStartPoint().y));
                                                _lineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getStartPoint().y));
                                            }
                                        }

                                        //
                                        syncWindowDoor(_lineBean);
                                        syncWindowDoor(_connLineBean);
                                    }
                                }
                            } else if (_windowBean != null) {
                                startLinePoint = _windowBean.getLineStartPoint();
                                endLinePoint = _windowBean.getLineEndPoint();
                                _windowBean.setStartPoint(new XCKYPoint(getCrossPoint(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y, motionEvent.getX(), motionEvent.getY())));
                            } else if (_doorBean != null) {
                                startLinePoint = _doorBean.getLineStartPoint();
                                endLinePoint = _doorBean.getLineEndPoint();
                                _doorBean.setStartPoint(new XCKYPoint(getCrossPoint(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y, motionEvent.getX(), motionEvent.getY())));
                            } else {
                                _lineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                syncWindowDoor(_lineBean);
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MoveLineEndPoint:
                            if (_pathBean != null) {
                                this._pathBean.setChange(true);
                                //如果移动时x方向或者y方向移动较小则忽略不计
                                if (Math.abs(motionEvent.getX() - this._lineBean.getStartPoint().x) < 10F && Math.abs(motionEvent.getX() - this._lineBean.getStartPoint().x) != 0.0F) {
                                    _lineBean.setEndPoint(new XCKYPoint(_lineBean.getStartPoint().x, motionEvent.getY()));
                                    syncWindowDoor(_lineBean);
                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(_lineBean.getStartPoint().x, motionEvent.getY()));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(_lineBean.getStartPoint().x, motionEvent.getY()));
                                        //
                                        syncWindowDoor(_connLineBean);
                                    }
                                } else if (Math.abs(motionEvent.getY() - _lineBean.getStartPoint().y) < 10F && Math.abs(motionEvent.getY() - _lineBean.getStartPoint().y) != 0.0F) {
                                    _lineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getStartPoint().y));
                                    syncWindowDoor(_lineBean);
                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getStartPoint().y));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _lineBean.getStartPoint().y));
                                        //
                                        syncWindowDoor(_connLineBean);
                                    }
                                } else {
                                    _lineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                    syncWindowDoor(_lineBean);
                                    if (_connLineBean != null) {
                                        if (_connLineBean.getEqualPoint().equals("Start"))
                                            _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                        else
                                            _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));

                                        //如果移动时x方向或者y方向移动较小则忽略不计
                                        if (_connLineBean.getEqualPoint().equals("Start")) {
                                            if (Math.abs(motionEvent.getX() - _connLineBean.getEndPoint().x) < 10F && Math.abs(motionEvent.getX() - _connLineBean.getEndPoint().x) != 0.0F) {
                                                _connLineBean.setStartPoint(new XCKYPoint(_connLineBean.getEndPoint().x, motionEvent.getY()));
                                                _lineBean.setEndPoint(new XCKYPoint(_connLineBean.getEndPoint().x, motionEvent.getY()));
                                            } else if (Math.abs(motionEvent.getY() - _connLineBean.getEndPoint().y) < 10F && Math.abs(motionEvent.getY() - _connLineBean.getEndPoint().y) != 0.0F) {
                                                _connLineBean.setStartPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getEndPoint().y));
                                                _lineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getEndPoint().y));
                                            }
                                        } else if (_connLineBean.getEqualPoint().equals("End")) {
                                            if (Math.abs(motionEvent.getX() - _connLineBean.getStartPoint().x) < 10F && Math.abs(motionEvent.getX() - _connLineBean.getStartPoint().x) != 0.0F) {
                                                _connLineBean.setEndPoint(new XCKYPoint(_connLineBean.getStartPoint().x, motionEvent.getY()));
                                                _lineBean.setEndPoint(new XCKYPoint(_connLineBean.getStartPoint().x, motionEvent.getY()));
                                            } else if (Math.abs(motionEvent.getY() - _connLineBean.getStartPoint().y) < 10F && Math.abs(motionEvent.getY() - _connLineBean.getStartPoint().y) != 0.0F) {
                                                _connLineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getStartPoint().y));
                                                _lineBean.setEndPoint(new XCKYPoint(motionEvent.getX(), _connLineBean.getStartPoint().y));
                                            }
                                        }
                                        //
                                        syncWindowDoor(_lineBean);
                                        syncWindowDoor(_connLineBean);
                                    }
                                }
                            } else if (_windowBean != null) {
                                startLinePoint = _windowBean.getLineStartPoint();
                                endLinePoint = _windowBean.getLineEndPoint();
                                _windowBean.setEndPoint(getCrossPoint(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y, motionEvent.getX(), motionEvent.getY()));
                            } else if (_doorBean != null) {
                                startLinePoint = _doorBean.getLineStartPoint();
                                endLinePoint = _doorBean.getLineEndPoint();
                                _doorBean.setEndPoint(getCrossPoint(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y, motionEvent.getX(), motionEvent.getY()));
                            } else {
                                _lineBean.setEndPoint(new XCKYPoint((int) motionEvent.getX(), (int) motionEvent.getY()));
                                syncWindowDoor(_lineBean);
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MoveLine:
                            float x = 0, y = 0;
                            if (_pathBean != null) {
                                //
                                this._pathBean.setChange(true);
                                //对比线的状态，x坐标差值越小，越倾向于垂直于x轴，同理y坐标差值越小越倾向于垂直于y轴
                                //这里拖动线只做一个方向的改变
                                if (Math.abs(this._lineBean.getStartPoint().x - this._lineBean.getEndPoint().x) > Math.abs(this._lineBean.getStartPoint().y - this._lineBean.getEndPoint().y)) {
                                    x = 0f;
                                    y = fy;
                                } else {
                                    x = fx;
                                    y = 0f;
                                }

                                //
                                for (LineBean line : _pathBean.getLineBeans()) {
                                    if (!line.isSelect()) {
                                        if (line.getStartPoint().x == _lineBean.getStartPoint().x && line.getStartPoint().y == _lineBean.getStartPoint().y) {
                                            line.setStartPoint(new XCKYPoint(x + line.getStartPoint().x, y + line.getStartPoint().y));
                                            syncWindowDoor(line);
                                        } else if (line.getStartPoint().x == _lineBean.getEndPoint().x && line.getStartPoint().y == _lineBean.getEndPoint().y) {
                                            line.setStartPoint(new XCKYPoint(x + line.getStartPoint().x, y + line.getStartPoint().y));
                                            syncWindowDoor(line);
                                        } else if (line.getEndPoint().x == _lineBean.getEndPoint().x && line.getEndPoint().y == _lineBean.getEndPoint().y) {
                                            line.setEndPoint(new XCKYPoint(x + line.getEndPoint().x, y + line.getEndPoint().y));
                                            syncWindowDoor(line);
                                        } else if (line.getEndPoint().x == _lineBean.getStartPoint().x && line.getEndPoint().y == _lineBean.getStartPoint().y) {
                                            line.setEndPoint(new XCKYPoint(x + line.getEndPoint().x, y + line.getEndPoint().y));
                                            syncWindowDoor(line);
                                        }
                                    }
                                }
                                _lineBean.setStartPoint(new XCKYPoint(x + _lineBean.getStartPoint().x, y + _lineBean.getStartPoint().y));
                                _lineBean.setEndPoint(new XCKYPoint(x + _lineBean.getEndPoint().x, y + _lineBean.getEndPoint().y));
                                syncWindowDoor(_lineBean, x, y);
                            } else if (_windowBean != null) {
                                _windowBean.setStartPoint(getCrossPoint(_windowBean.getLineStartPoint(), _windowBean.getLineEndPoint(), _windowBean.getStartPoint()));
                                _windowBean.setEndPoint(getCrossPoint(_windowBean.getLineStartPoint(), _windowBean.getLineEndPoint(), _windowBean.getEndPoint()));
                            } else if (_doorBean != null) {
                                _doorBean.setStartPoint(getCrossPoint(_doorBean.getLineStartPoint(), _doorBean.getLineEndPoint(), _doorBean.getStartPoint()));
                                _doorBean.setEndPoint(getCrossPoint(_doorBean.getLineStartPoint(), _doorBean.getLineEndPoint(), _doorBean.getEndPoint()));
                            } else {
                                for (String key : _lineMap.keySet()) {
                                    LineBean linebean = _lineMap.get(key);
                                    if (linebean.isSelect()) {
                                        linebean.setStartPoint(new XCKYPoint(fx + (float) linebean.getStartPoint().x, fy + (float) linebean.getStartPoint().y));
                                        linebean.setEndPoint(new XCKYPoint(fx + (float) linebean.getEndPoint().x, fy + (float) linebean.getEndPoint().y));
                                        //
                                        syncWindowDoor(linebean, fx, fy);
                                    }
                                }
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MoveText:
                            for (String key : _drawTextMap.keySet()) {
                                DrawTextBean drawtextbean = _drawTextMap.get(key);
                                if (drawtextbean.isSelect()) {
                                    drawtextbean.setMove(true);
                                    drawtextbean.setPoint(new XCKYPoint(drawtextbean.getPoint().x + fx, drawtextbean.getPoint().y + fy));
                                }
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MouldTranslate:
                            if (_mouldPathBean != null) {
                                MatrixBean matrixbean = new MatrixBean();
                                matrixbean.setTranslateX(fx);
                                matrixbean.setTranslateY(fy);
                                matrixbean.setTrans(true);
                                _mouldPathBean.getPath().transform(matrixbean);
                                _mouldPathBean.setMatrix(matrixbean);
                                RectF rectf = new RectF();
                                _mouldPathBean.getPath().computeBounds(rectf, true);
                                _mouldPathBean.setRectobj(new RectBean((int) rectf.left, (int) rectf.top, (int) rectf.right, (int) rectf.bottom));
                                _mouldPathBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                _mouldPathBean.setRotate(false);
                            } else if (_mouldBean != null) {
                                MatrixBean matrixbean = new MatrixBean();
                                matrixbean.setTranslateX(fx);
                                matrixbean.setTranslateY(fy);
                                matrixbean.setTrans(true);
                                matrixbean.save();
                                _mouldBean.getPath().transform(matrixbean);
                                _mouldBean.setMatrix(matrixbean);
                                RectF rectf = new RectF();
                                _mouldBean.getPath().computeBounds(rectf, true);
                                _mouldBean.setRectobj(new RectBean((int) rectf.left, (int) rectf.top, (int) rectf.right, (int) rectf.bottom));
                                _mouldBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                _mouldBean.setRotate(false);
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MouldRotate:
                            if (_mouldPathBean != null) {
                                MatrixBean matrixbean = new MatrixBean();
                                matrixbean.setRotateDegrees((float) attan(_mouldPathBean.getRotatePoint().x, _mouldPathBean.getRotatePoint().y, _mouldPathBean.getCenterPoint().x, _mouldPathBean.getCenterPoint().y, motionEvent.getX(), motionEvent.getY()));
                                RectF rectf = new RectF();
                                _mouldPathBean.getPath().computeBounds(rectf, true);
                                matrixbean.setRotatePx(rectf.centerX());
                                matrixbean.setRotatePy(rectf.centerY());
                                matrixbean.setRotate(true);
                                _mouldPathBean.setMatrix(matrixbean);
                                _mouldPathBean.setRotate(true);
                                _mouldPathBean.setRotatePoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                _mouldPathBean.getPath().transform(matrixbean);
                                _mouldPathBean.getPath().computeBounds(rectf, true);
                                _mouldPathBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                _mouldPathBean.setRectobj(new RectBean(rectf.left, rectf.top, rectf.right, rectf.bottom));
                            } else if (_mouldBean != null) {
                                MatrixBean matrixbean = new MatrixBean();
                                matrixbean.setRotateDegrees((float) attan(_mouldBean.getRotatePoint().x, _mouldBean.getRotatePoint().y, _mouldBean.getCenterPoint().x, _mouldBean.getCenterPoint().y, motionEvent.getX(), motionEvent.getY()));
                                RectF rectf = new RectF();
                                _mouldBean.getPath().computeBounds(rectf, true);
                                matrixbean.setRotatePx(rectf.centerX());
                                matrixbean.setRotatePy(rectf.centerY());
                                matrixbean.setRotate(true);
                                _mouldBean.setMatrix(matrixbean);
                                _mouldBean.setRotate(true);
                                _mouldBean.setRotatePoint(new XCKYPoint(motionEvent.getX(), motionEvent.getY()));
                                _mouldBean.getPath().transform(matrixbean);
                                _mouldBean.getPath().computeBounds(rectf, true);
                                _mouldBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                _mouldBean.setRectobj(new RectBean(rectf.left, rectf.top, rectf.right, rectf.bottom));
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MouldPostScale:
                            if (_mouldPathBean != null) {
                                float sx = Math.abs(motionEvent.getX() - _mouldPathBean.getRectobj().left) / Math.abs(_mouldPathBean.getRectobj().right - _mouldPathBean.getRectobj().left);
                                float sy = Math.abs(motionEvent.getY() - _mouldPathBean.getRectobj().top) / Math.abs(_mouldPathBean.getRectobj().bottom - _mouldPathBean.getRectobj().top);
                                if (motionEvent.getX() >= _mouldPathBean.getRectobj().left + 10F && motionEvent.getY() >= _mouldPathBean.getRectobj().top + 10F) {
                                    _mouldPathBean.setRotate(false);
                                    MatrixBean matrixbean = new MatrixBean();
                                    matrixbean.setPostScaleSX(sx);
                                    matrixbean.setPostScaleSY(sx);
                                    matrixbean.setPostScalePx(_mouldPathBean.getRectobj().left);
                                    matrixbean.setPostScalePy(_mouldPathBean.getRectobj().top);
                                    matrixbean.setScale(true);
                                    RectF rectf = new RectF();
                                    _mouldPathBean.getPath().transform(matrixbean);
                                    _mouldPathBean.setMatrix(matrixbean);
                                    _mouldPathBean.getPath().computeBounds(rectf, true);
                                    _mouldPathBean.setRectobj(new RectBean(rectf.left, rectf.top, rectf.right, rectf.bottom));
                                    _mouldPathBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                }
                            } else if (_mouldBean != null) {
                                float sx = Math.abs(motionEvent.getX() - _mouldBean.getRectobj().left) / Math.abs(_mouldBean.getRectobj().right - _mouldBean.getRectobj().left);
                                float sy = Math.abs(motionEvent.getY() - _mouldBean.getRectobj().top) / Math.abs(_mouldBean.getRectobj().bottom - _mouldBean.getRectobj().top);
                                if (motionEvent.getX() >= 10F + _mouldBean.getRectobj().left && motionEvent.getY() >= 10F + _mouldBean.getRectobj().top) {
                                    _mouldBean.setRotate(false);
                                    MatrixBean matrixbean = new MatrixBean();
                                    matrixbean.setPostScaleSX(sx);
                                    matrixbean.setPostScaleSY(sx);
                                    matrixbean.setPostScalePx(_mouldBean.getRectobj().left);
                                    matrixbean.setPostScalePy(_mouldBean.getRectobj().top);
                                    matrixbean.setScale(true);
                                    RectF rectf = new RectF();
                                    _mouldBean.getPath().transform(matrixbean);
                                    _mouldBean.setMatrix(matrixbean);
                                    _mouldBean.getPath().computeBounds(rectf, true);
                                    _mouldBean.setRectobj(new RectBean(rectf.left, rectf.top, rectf.right, rectf.bottom));
                                    _mouldBean.setCenterPoint(new XCKYPoint(rectf.centerX(), rectf.centerY()));
                                }
                            }
                            this._status_move = true;
                            break;
                        case MoveOperType.MoveDoorFrame:
                            _doorBean.setChange(true);
                            int index = 0;
                            for (XCKYPoint point : _doorBean.getPoints()) {
                                if ((new Rect(point.x - 15, point.y - 15, point.x + 15, point.y + 15)).contains((int) _x, (int) _y))
                                    _doorBean.setPointNum(index + 1);
                                index++;
                            }
                            this._status_move = true;
                            break;
                        default:
                            this._status_move = true;
                            break;
                    }
                    break;
                case ExperModeStatus.Edit:
                    this._status_line = true;
                    switch (this._paintType) {
                        case PaintType.StraightLine:
                            //画直线
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            //自动垂直
                            if (Math.abs(_startPoint.x - _endPoint.x) > Math.abs(_startPoint.y - _endPoint.y))
                                _endPoint.set(motionEvent.getX(), _startPoint.y);
                            else if (Math.abs(_startPoint.x - _endPoint.x) < Math.abs(_startPoint.y - _endPoint.y))
                                _endPoint.set(_startPoint.x, motionEvent.getY());
                            else
                                _endPoint.set(_startPoint.x, _startPoint.y);
                            break;
                        case PaintType.ObliqueLine:
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            break;
                        case PaintType.PolygonLine:
                            //靠近开始点则自动闭合
                            if (Math.abs(_startPoint.x - motionEvent.getX()) < 10F)
                                _endPoint.set(_startPoint.x, motionEvent.getY());
                            else if (Math.abs(_startPoint.y - motionEvent.getY()) < 10F)
                                _endPoint.set(motionEvent.getX(), _startPoint.y);
                            else
                                _endPoint.set(motionEvent.getX(), motionEvent.getY());

                            //
                            if (_pathing.getLineBeans() != null) {
                                //检测当前点是否靠近起始点
                                if ((new Rect((_pathing.getLineBeans().get(0)).getStartPoint().x - 35, (_pathing.getLineBeans().get(0)).getStartPoint().y - 35, 35 + (_pathing.getLineBeans().get(0)).getStartPoint().x, 35 + (_pathing.getLineBeans().get(0)).getStartPoint().y)).contains((int) motionEvent.getX(), (int) motionEvent.getY()))
                                    _colsePath = true;
                                else
                                    _colsePath = false;
                            }
                            break;
                        case PaintType.StraightWall:
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            //自动垂直
                            if (Math.abs(_startPoint.x - _endPoint.x) > Math.abs(_startPoint.y - _endPoint.y))
                                _endPoint.set(motionEvent.getX(), _startPoint.y);
                            else if (Math.abs(_startPoint.x - _endPoint.x) < Math.abs(_startPoint.y - _endPoint.y))
                                _endPoint.set(_startPoint.x, motionEvent.getY());
                            else
                                _endPoint.set(_startPoint.x, _startPoint.y);
                            break;
                        case PaintType.ObliqueWall:
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            break;
                        case PaintType.PolygonWall:
                            //靠近开始点则自动闭合
                            if (Math.abs(_startPoint.x - motionEvent.getX()) < 10F)
                                _endPoint.set(_startPoint.x, motionEvent.getY());
                            else if (Math.abs(_startPoint.y - motionEvent.getY()) < 10F)
                                _endPoint.set(motionEvent.getX(), _startPoint.y);
                            else
                                _endPoint.set(motionEvent.getX(), motionEvent.getY());

                            //检测当前点是否靠近起始点
                            if (_pathing.getLineBeans() != null) {
                                if ((new Rect(_pathing.getLineBeans().get(0).getStartPoint().x - 35, _pathing.getLineBeans().get(0).getStartPoint().y - 35, _pathing.getLineBeans().get(0).getStartPoint().x + 35, _pathing.getLineBeans().get(0).getStartPoint().y + 35)).contains((int) motionEvent.getX(), (int) motionEvent.getY()))
                                    _colsePath = true;
                                else
                                    _colsePath = false;
                            }
                            break;
                        case PaintType.RectWall:
                            _endPoint.set(motionEvent.getX(), motionEvent.getY());
                            break;
                        case PaintType.Window:
                            this._status_line = false;
                            if (_windowBean != null) {
                                _windowBean.setEndPoint(getCrossPoint(_windowBean.getLineStartPoint(), _windowBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY())));
                            }
                            break;
                        case PaintType.Door:
                            this._status_line = false;
                            if (_doorBean != null) {
                                _doorBean.setEndPoint(getCrossPoint(_doorBean.getLineStartPoint(), _doorBean.getLineEndPoint(), new XCKYPoint(motionEvent.getX(), motionEvent.getY())));
                            }
                            break;
                    }
                    break;
            }
            invalidate();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Log.v(TAG,"ACTION_UP sele = "+_status);
            switch (this._status) {
                case ExperModeStatus.Sel:
                    this._status_sel = false;
                    this.setRectSelect();
                    break;
                case ExperModeStatus.Move:
                    this._status_move = false;
                    switch (_moveOperType) {
                        case MoveOperType.MoveLineStartPoint:
                        case MoveOperType.MoveLineEndPoint:
                            if (_windowBean != null) {
                                _windowBean.setSelect(false);
                                _windowBean = null;
                            } else if (_doorBean != null) {
                                _doorBean.setSelect(false);
                                _doorBean = null;
                            } else if (_pathBean != null) {
                                setNoSelectPath();
                                _pathBean = null;
                            }
                            break;
                        case MoveOperType.MoveLine:
                            if (_windowBean != null) {
                                _windowBean.setSelect(false);
                                _windowBean = null;
                            } else if (_doorBean != null) {
                                _doorBean.setSelect(false);
                                _doorBean = null;
                            } else if (_pathBean != null) {
                                setNoSelectPath();
                                _pathBean = null;
                            }
                            break;
                        case MoveOperType.MoveText:
                            for (String key : _drawTextMap.keySet()) {
                                DrawTextBean drawtextbean = _drawTextMap.get(key);
                                if (drawtextbean.isSelect()) {
                                    drawtextbean.setSelect(false);
                                    drawtextbean.setMove(false);
                                }
                            }
                            break;
                        case MoveOperType.MouldTranslate:
                        case MoveOperType.MouldRotate:
                        case MoveOperType.MouldPostScale:
                            if (_mouldPathBean != null)
                                _mouldPathBean = null;
                            else if (_mouldBean != null)
                                _mouldBean = null;
                            break;
                        case MoveOperType.MoveDoorFrame:
                            if (_doorBean != null) {
                                _doorBean.setChange(false);
                                _doorBean.setSelect(false);
                                _doorBean = null;
                            }
                    }
                    this._moveOperType = MoveOperType.None;
                    saveData();
                    break;
                case ExperModeStatus.Edit:
                    if (_pathing != null) {
                        LineBean linebean;
                        XCKYPoint point;
                        PathPointBean pathpointbean;
                        List<LineBean> linearray;
                        switch (this._paintType) {
                            case PaintType.PolygonLine:
                            case PaintType.PolygonWall:
                                if (_pathing.getLineBeans() == null) {
                                    linearray = new ArrayList<LineBean>();
                                    linebean = new LineBean();
                                    linebean.setStartPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                                    linebean.setEndPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                                    linebean.setPaint(new XCKYPaint(_paint));
                                    linearray.add(linebean);
                                    _pathing.setLineBeans(linearray);
                                } else {
                                    linearray = _pathing.getLineBeans();
                                    linebean = new LineBean();
                                    linebean.setStartPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                                    linebean.setEndPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                                    linebean.setPaint(new XCKYPaint(_paint));
                                    linearray.add(linebean);
                                }
                                if (_colsePath) {
                                    point = linearray.get(0).getStartPoint();
                                    linearray.get(linearray.size() - 1).setEndPoint(new XCKYPoint(point.x, point.y));
                                }
                                _pathing.setPaint(_paint);
                                if (_pathing.getPath() == null) {
                                    pathpointbean = new PathPointBean();
                                    pathpointbean.setStartPoint(_startPoint.x, _startPoint.y);
                                    _pathing.setPath(pathpointbean);
                                    pathpointbean.setClose(false);
                                    _pathMap.put(_pathing.toString(), _pathing);
                                } else {
                                    pathpointbean = _pathing.getPath();
                                    if (!_colsePath) {
                                        pathpointbean.setPointList(_endPoint.x, _endPoint.y);
                                        pathpointbean.setClose(false);
                                    } else {
                                        pathpointbean.setClose(true);
                                    }
                                    _pathing.setPath(pathpointbean);
                                    _pathMap.put(_pathing.toString(), _pathing);
                                }
                                if (_colsePath) {
                                    _colsePath = false;
                                    setPaint(PaintType.None);
                                }
                                break;
                            case PaintType.RectWall:
                                linearray = new ArrayList<LineBean>();
                                linebean = new LineBean();
                                linebean.setStartPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                                linebean.setEndPoint(new XCKYPoint(_endPoint.x, _startPoint.y));
                                linebean.setPaint(new XCKYPaint(_paint));
                                linearray.add(linebean);
                                linebean = new LineBean();
                                linebean.setStartPoint(new XCKYPoint(_endPoint.x, _startPoint.y));
                                linebean.setEndPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                                linebean.setPaint(new XCKYPaint(_paint));
                                linearray.add(linebean);
                                linebean = new LineBean();
                                linebean.setStartPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                                linebean.setEndPoint(new XCKYPoint(_startPoint.x, _endPoint.y));
                                linebean.setPaint(new XCKYPaint(_paint));
                                linearray.add(linebean);
                                linebean = new LineBean();
                                linebean.setStartPoint(new XCKYPoint(_startPoint.x, _endPoint.y));
                                linebean.setEndPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                                linebean.setPaint(new XCKYPaint(_paint));
                                linearray.add(linebean);
                                _pathing.setLineBeans(linearray);
                                _pathing.setPaint(_paint);
                                pathpointbean = new PathPointBean();
                                pathpointbean.setStartPoint(_startPoint.x, _startPoint.y);
                                pathpointbean.setPointList(_endPoint.x, _startPoint.y);
                                pathpointbean.setPointList(_endPoint.x, _endPoint.y);
                                pathpointbean.setPointList(_startPoint.x, _endPoint.y);
                                pathpointbean.setClose(true);
                                _pathing.setPath(pathpointbean);
                                _pathMap.put(_pathing.toString(), _pathing);
                                setPaint(PaintType.None);
                                break;
                            default:
                                _pathing.getPath().setPointList(_endPoint.x, _endPoint.y);
                                if (!_pathMap.containsKey(_pathing.toString()))
                                    _pathMap.put(_pathing.toString(), _pathing);

                                if (_pathing.getLineBeans() == null) {
                                    _pathing.setLineBeans(new ArrayList<LineBean>());
                                } else {
                                    linebean = new LineBean();
                                    linebean.setStartPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                                    linebean.setEndPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                                    linebean.setPaint(new XCKYPaint(_paint));
                                    _pathing.getLineBeans().add(linebean);
                                }
                                setPaint(PaintType.None);
                                break;
                        }
                    } else if (this._status_lineWindow) {
                        if (_windowBean != null) {
                            _windowBean.setSelect(false);
                            WindowBean windowbean = new WindowBean(_windowBean);
                            _windowMap.put(windowbean.toString(), windowbean);
                            _windowBean = null;
                        }
                        setPaint(PaintType.None);
                        this._status_lineWindow = false;
                    } else if (this._status_lineDoor) {
                        if (_doorBean != null) {
                            _doorBean.setSelect(false);
                            DoorBean doorbean = new DoorBean(_doorBean);
                            _doorMap.put(doorbean.toString(), doorbean);
                            _doorBean = null;
                        }
                        setPaint(PaintType.None);
                        this._status_lineDoor = false;
                    } else if (this._status_lineText) {
                        setPaint(PaintType.None);
                        this._status_lineText = false;
                    } else {
                        LineBean linebean = new LineBean();
                        linebean.setStartPoint(new XCKYPoint(_startPoint.x, _startPoint.y));
                        linebean.setEndPoint(new XCKYPoint(_endPoint.x, _endPoint.y));
                        linebean.setPaint(new XCKYPaint(_paint));
                        _lineMap.put(linebean.toString(), linebean);
                        setPaint(PaintType.None);
                    }
                    this._status_line = false;
                    saveData();
                    break;
            }
            invalidate();
        }
        return true;
    }

    /**
     * 当前操作状态
     *
     * @author Administrator
     */
    public class ExperModeStatus {
        /**
         * 重置（初始化）
         */
        public static final int None = -1;

        /**
         * 选择模式
         */
        public static final int Sel = 1;

        /**
         * 移动模式
         */
        public static final int Move = 2;

        /**
         * 修改（新建）模式
         */
        public static final int Edit = 3;

    }

    /**
     * 移动操作类型
     *
     * @author Administrator
     */
    public class MoveOperType {
        /**
         * 重置（初始化）
         */
        public static final int None = -1;

        /**
         * 移动线的起始点
         */
        public static final int MoveLineStartPoint = 1;

        /**
         * 移动线的 结束点
         */
        public static final int MoveLineEndPoint = 2;


        /**
         * 移动线本身
         */
        public static final int MoveLine = 3;

        /**
         * 移动文字
         */
        public static final int MoveText = 4;

        /**
         * 模型位移
         */
        public static final int MouldTranslate = 5;

        /**
         * 模型旋转
         */
        public static final int MouldRotate = 6;


        /**
         * 模型缩放
         */
        public static final int MouldPostScale = 7;

        /**
         * 移动门框
         */
        public static final int MoveDoorFrame = 8;
    }


}
