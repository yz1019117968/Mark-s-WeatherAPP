package com.example.mark.coolweather.gson;

import java.util.List;

/**
 * Created by Mark on 2018/5/11.
 */

public class HeWeather {
    public Now now;
    public List<DailyForcast> daily_forecast;
    public List<LifeStyle> lifestyle;
    public String status;
    public Update update;
    public Basic basic;
    public class Update{
      public String loc;
    }
}
