package com.example.mark.coolweather.gson;

/**
 * Created by Mark on 2017/12/13.
 */

public class DailyForecast {
    public String date;
    public Cond cond;
    public Tmp tmp;
    public class Cond{
        public String txt_d;
    }
    public class Tmp{
        public String max;
        public String min;
    }
}
