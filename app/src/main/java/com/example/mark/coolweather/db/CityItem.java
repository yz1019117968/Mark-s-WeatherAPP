package com.example.mark.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Mark on 2017/12/16.
 */

public class CityItem extends DataSupport {
    int id;
    String cityName;
    String cityTmp;
    String weather_id;
    String street;
    String remindFlag;
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityTmp(String cityTmp){
        this.cityTmp=cityTmp;
    }
    public String getCityTmp(){
        return cityTmp;
    }
    public void setWeather_id(String weather_id){
        this.weather_id=weather_id;
    }
    public String getWeather_id(){
        return weather_id;
    }
    public void setStreet(String street){
        this.street=street;
    }
    public String getStreet(){
        return street;
    }
    public void setRemindFlag(String remindFlag){
        this.remindFlag=remindFlag;
    }
    public String getRemindFlag(){
        return remindFlag;
    }
}
