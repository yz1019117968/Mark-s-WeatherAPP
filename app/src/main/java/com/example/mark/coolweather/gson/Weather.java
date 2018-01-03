package com.example.mark.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mark on 2017/12/13.
 */

public class Weather {
    public String status;
    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;
    public Now now;
    public Suggestion suggestion;
}
