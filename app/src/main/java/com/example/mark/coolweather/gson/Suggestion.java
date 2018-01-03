package com.example.mark.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 2017/12/13.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;
    public class Comfort{
        public String txt;
    }
    public class CarWash{
        public String txt;
    }
    public class Sport{
        public String txt;
    }
}
