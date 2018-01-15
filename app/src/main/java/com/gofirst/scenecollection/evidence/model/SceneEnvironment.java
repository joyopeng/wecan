package com.gofirst.scenecollection.evidence.model;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2016/7/8.
 */
@Table(name = "SceneEnvironment")
public class SceneEnvironment {
    @Id(column = "id")
    private String id;
    private String weather;
    private String envTemperature;
    private String wind;
    /**天气备注*/
    private String otherWeather;
    /**天气备注语音路径*/
    private String otherWeather_audio;
    private String lighting;
    private String sceneCondition;
    private String protectionDate;
    private String caseId;
    private String sceneType;


    @JSONField(name = "SCENE_TYPE")
    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
	
	@JSONField(name = "SCENE_TYPE")
    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

	@JSONField(name = "ENV_TEMPERATURE")
    public String getEnvTemperature() {
        return envTemperature;
    }

    public void setEnvTemperature(String envTemperature) {
        this.envTemperature = envTemperature;
    }

	@JSONField(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
	
	@JSONField(name = "OTHER_WEATHER")
    public String getOtherWeather() {
        return otherWeather;
    }

    public void setOtherWeather(String otherWeather) {
        this.otherWeather = otherWeather;
    }
	
	@JSONField(name = "PROTECTION_DATE")
    public String getProtectionDate() {
        return protectionDate;
    }

    public void setProtectionDate(String protectionDate) {
        this.protectionDate = protectionDate;
    }
	
	@JSONField(name = "WEATHER")
    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
	
	@JSONField(name = "WIND")
    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }
	
	@JSONField(name = "SCENE_CONDITION")
    public String getSceneCondition() {
        return sceneCondition;
    }

    public void setSceneCondition(String sceneCondition) {
        this.sceneCondition = sceneCondition;
    }
	
	@JSONField(name = "OTHER_WEATHER_AUDIO")
    public String getOtherWeather_audio() {
        return otherWeather_audio;
    }

    public void setOtherWeather_audio(String otherWeather_audio) {
        this.otherWeather_audio = otherWeather_audio;
    }

	@JSONField(name = "LIGHTING")
    public String getLighting() {
        return lighting;
    }

    public void setLighting(String lighting) {
        this.lighting = lighting;
    }
}
