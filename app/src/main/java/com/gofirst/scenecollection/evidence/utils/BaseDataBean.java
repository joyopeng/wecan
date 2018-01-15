package com.gofirst.scenecollection.evidence.utils;


public class BaseDataBean {
    private String cell_id;// cellid连接基站编码
    private String lac;// lac连接基站位置区域码
    private String mcc;// mcc MCC国家码
    private String mnc;// mnc MNC网号
    private String signalstrength;// signalstrength连接基站信号强度
    private String leveltext;
    private String asulevel;//alone signal unit 独立信号单元
    private String cpi;//Cell Parameter ID
    private String bsic;//基站识别码
    private String arfcn;//频点
    private String time;
    private String tac;//跟踪区
    private String pci;// Physical Cell Identifier
    private String earfcn;


    public String getCell_id() {
        return cell_id;
    }

    public void setCell_id(String cell_id) {
        this.cell_id = cell_id;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getSignalstrength() {
        return signalstrength;
    }

    public void setSignalstrength(String signalstrength) {
        this.signalstrength = signalstrength;
    }
}
