package com.gofirst.scenecollection.evidence.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxiran on 2016/5/9.
 */
public class StringMap {

    private Map<String,String> stringMap;

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public StringMap() {
        stringMap = new HashMap<String,String>();


    }

    public void putByteArray(String key,byte[] bytes){
        try {
            stringMap.put(key,new String(bytes,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void putString(String key,String value){
        stringMap.put(key,value);
    }
}
