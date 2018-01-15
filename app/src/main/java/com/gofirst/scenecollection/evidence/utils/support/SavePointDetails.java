package com.gofirst.scenecollection.evidence.utils.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/6.
 */
public class SavePointDetails {
    private String name;
    private float absoluteX;
    private float absoluteY;
    private float scale;
    private float radiu;
    private Map<String,String> files = new HashMap<String,String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAbsoluteX() {
        return absoluteX;
    }

    public void setAbsoluteX(float absoluteX) {
        this.absoluteX = absoluteX;
    }

    public float getAbsoluteY() {
        return absoluteY;
    }

    public void setAbsoluteY(float absoluteY) {
        this.absoluteY = absoluteY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRadiu() {
        return radiu;
    }

    public void setRadiu(float radiu) {
        this.radiu = radiu;
    }

    public Map<String,String> getFiles() {
        return files;
    }

    public void setFiles(Map<String,String> files) {
        this.files = files;
    }

    public void addFile(String key,String info){
        this.files.put(key,info);
    }

    public void removeFile(String key){
        this.files.remove(key);
    }

    public void removewAllFiles(){
        this.files.clear();
    }
}
