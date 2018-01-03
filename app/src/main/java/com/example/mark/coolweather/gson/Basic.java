package com.example.mark.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 2017/12/13.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
//@serializedName用于java属性与json属性的匹配，否则必须名称一致
