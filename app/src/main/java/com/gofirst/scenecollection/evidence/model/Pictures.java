package com.gofirst.scenecollection.evidence.model;

/**
 * Created by Administrator on 2016/5/30.
 */
public class Pictures{

    private int id;/*goods_name,extract_fraction,extract_way,extract_man,extract_count,extract_date,extract_remark;*/
    private String pictureName;
    private String goodsName;
    private String extractFraction;
    private String extractWay;
    private String extractMan;
    private int extractCount;
    private String extractDate;
    private String extractRemark;
    private String pictureType;//blind,extractevidence,scene三中类型



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPictureName() {return pictureName;}
    public void setPictureName(String pictureName) {this.pictureName = pictureName;}

    public String getGoodsName() {return goodsName;}
    public void setGoodsName(String goodsName) {this.goodsName = goodsName;}

    public String getExtractFraction() {return extractFraction;}
    public void setExtractFraction(String extractFraction) {this.extractFraction = extractFraction;}

    public String getExtractWay() {return extractWay;}
    public void setExtractWay(String extractWay) {this.extractWay = extractWay;}

    public String getExtractMan() {return extractMan;}
    public void setExtractMan(String extractMan) {this.extractMan = extractMan;}

    public int getExtractCount() {return extractCount;}
    public void setExtractCount(int extractCount) {this.extractCount = extractCount;}

    public String getExtractDate() {return extractDate;}
    public void setExtractDate(String extractDate) {this.extractDate = extractDate;}

    public String getExtractRemark() {return extractRemark;}
    public void setExtractRemark(String extractRemark) {this.extractRemark = extractRemark;}

    public String getPictureType() {return pictureType;}
    public void setPictureType(String pictureType) {this.pictureType = pictureType;}
}