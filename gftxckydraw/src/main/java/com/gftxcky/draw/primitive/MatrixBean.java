package com.gftxcky.draw.primitive;

import java.io.Serializable;

import android.graphics.Matrix;

public class MatrixBean implements Serializable {
	 private static final long serialVersionUID = 8308632580383837677L;
	  private boolean isRotate = false;
	  private boolean isScale = false;
	  private boolean isTrans = false;
	  private float postScalePx;
	  private float postScalePy;
	  private float postScaleSX;
	  private float postScaleSY;
	  private float rotateDegrees;
	  private float rotatePx;
	  private float rotatePy;
	  private float translateX;
	  private float translateY;
	  private boolean issave=false;
	  private float[] matrixValues;

	  public MatrixBean()
	  {
		  
	  }
	  
	  public MatrixBean(Matrix matrix)
	  {
		  this.matrixValues=new float[9];
		  matrix.getValues(this.matrixValues);
		  issave=true;
	  }
	  

	  public MatrixBean(MatrixBean paramMatrixBean)
	  {
	    this.translateX = paramMatrixBean.translateX;
	    this.translateY = paramMatrixBean.translateY;
	    this.postScaleSX = paramMatrixBean.postScaleSX;
	    this.postScaleSY = paramMatrixBean.postScaleSY;
	    this.postScalePx = paramMatrixBean.postScalePx;
	    this.postScalePy = paramMatrixBean.postScalePy;
	    this.rotateDegrees = paramMatrixBean.rotateDegrees;
	    this.rotatePx = paramMatrixBean.rotatePx;
	    this.rotatePy = paramMatrixBean.rotatePy;
	    this.isRotate = paramMatrixBean.isRotate;
	    this.isScale = paramMatrixBean.isScale;
	    this.isTrans = paramMatrixBean.isTrans;
	    if(paramMatrixBean.getMatrixValues()!=null)
	    {
	    	this.matrixValues=paramMatrixBean.CloneMatrixValues();
	    	issave=true;
	    }
	  }
	  
	  /**
	   * 转换为矩阵值
	   * @param matrixBean
	   * @return
	   */
	  public Matrix getMatrix()
	  {
		  Matrix localMatrix = new Matrix();
		  save();
		  if(this.matrixValues!=null)
			  localMatrix.setValues(this.matrixValues);
		  return localMatrix;
	  }
	  
	  /**
	   * 转换为矩阵值
	   * @param matrixBean
	   * @return
	   */
	  public static float[] ConvertMatrixValues(MatrixBean matrixBean)
	  {
		  float[] matrix=new  float[9];
		  Matrix localMatrix = new Matrix();
			if (matrixBean.isTrans())
				localMatrix.setTranslate(matrixBean.getTranslateX(),
						matrixBean.getTranslateY());
			if (matrixBean.isRotate())
				localMatrix.setRotate(matrixBean.getRotateDegrees(),
						matrixBean.getRotatePx(),
						matrixBean.getRotatePy());
			if (matrixBean.isScale())
				localMatrix.setScale(matrixBean.getPostScaleSX(),
						matrixBean.getPostScaleSY(),
						matrixBean.getPostScalePx(),
						matrixBean.getPostScalePy());
			localMatrix.getValues(matrix);
		  return matrix;
	  }

	  /**
	   * 合并一个matrixBean效果
	   * @param matrixBean
	   */
	  public void postConcat(MatrixBean matrixBean)
	  {
		  Matrix localMatrix = new Matrix();
		  localMatrix.setValues(matrixValues);
		  Matrix otherMatrix=new Matrix();
		  otherMatrix.setValues(matrixBean.getMatrixValues());
		  localMatrix.postConcat(otherMatrix);
		  localMatrix.getValues(matrixValues);
		  issave=true;
	  }
	  
	  /**
	   * 保存当前的动作到Matrix
	   */
	  public void save()
	  {
		  if(!issave)
			  matrixValues=ConvertMatrixValues(this);
	  }
	  
	  public float[] CloneMatrixValues()
	  {
		  if(this.matrixValues!=null)
		  {
			  float[] matrixvalues=new float[9];
			  matrixvalues[0]=this.matrixValues[0];
			  matrixvalues[1]=this.matrixValues[1];
			  matrixvalues[2]=this.matrixValues[2];
			  matrixvalues[3]=this.matrixValues[3];
			  matrixvalues[4]=this.matrixValues[4];
			  matrixvalues[5]=this.matrixValues[5];
			  matrixvalues[6]=this.matrixValues[6];
			  matrixvalues[7]=this.matrixValues[7];
			  matrixvalues[8]=this.matrixValues[8];
			  return matrixvalues;
		  }
		  return null;
	  }
	  
	  public float getPostScalePx()
	  {
	    return this.postScalePx;
	  }

	  public float getPostScalePy()
	  {
	    return this.postScalePy;
	  }

	  public float getPostScaleSX()
	  {
	    return this.postScaleSX;
	  }

	  public float getPostScaleSY()
	  {
	    return this.postScaleSY;
	  }

	  public float getRotateDegrees()
	  {
	    return this.rotateDegrees;
	  }

	  public float getRotatePx()
	  {
	    return this.rotatePx;
	  }

	  public float getRotatePy()
	  {
	    return this.rotatePy;
	  }

	  public float getTranslateX()
	  {
	    return this.translateX;
	  }

	  public float getTranslateY()
	  {
	    return this.translateY;
	  }

	  public boolean isRotate()
	  {
		
	    return this.isRotate;
	  }

	  public boolean isScale()
	  {
	    return this.isScale;
	  }

	  public boolean isTrans()
	  {
	    return this.isTrans;
	  }

	  public void setPostScalePx(float postScalePx)
	  {
		  issave=false;
	    this.postScalePx = postScalePx;
	  }

	  public void setPostScalePy(float postScalePy)
	  {
		  issave=false;
	    this.postScalePy = postScalePy;
	  }

	  public void setPostScaleSX(float postScaleSX)
	  {
		  issave=false;
	    this.postScaleSX = postScaleSX;
	  }

	  public void setPostScaleSY(float postScaleSY)
	  {
		  issave=false;
	    this.postScaleSY = postScaleSY;
	  }

	  public void setRotate(boolean isRotate)
	  {
		  issave=false;
	    this.isRotate = isRotate;
	  }

	  public void setRotateDegrees(float rotateDegrees)
	  {
		  issave=false;
	    this.rotateDegrees = rotateDegrees;
	  }

	  public void setRotatePx(float rotatePx)
	  {
		  issave=false;
	    this.rotatePx = rotatePx;
	  }

	  public void setRotatePy(float rotatePy)
	  {
		  issave=false;
	    this.rotatePy = rotatePy;
	  }

	  public void setScale(boolean isScale)
	  {
		  issave=false;
	    this.isScale = isScale;
	  }

	  public void setTrans(boolean isTrans)
	  {
		  issave=false;
	    this.isTrans = isTrans;
	  }

	  public void setTranslateX(float translateX)
	  {
		  issave=false;
	    this.translateX = translateX;
	  }

	  public void setTranslateY(float translateY)
	  {
		  issave=false;
	    this.translateY = translateY;
	  }

	  public float[] getMatrixValues() {
		save();
		return matrixValues;
	  }

	  public void setMatrixValues(float[] matrix) {
		issave=true;
		this.matrixValues = matrix;
	  }
}
