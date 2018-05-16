package com.example.mark.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.mark.coolweather.db.City;
import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.db.County;
import com.example.mark.coolweather.db.Province;
import com.example.mark.coolweather.gson.HeWeather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mark on 2017/12/12.
 */

public class Utility {

    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray array=new JSONArray(response);
                for(int i=0;i<array.length();i++){
                    Province province=new Province();
                    province.setPname(array.getJSONObject(i).getString("name"));
                    province.setPcode(array.getJSONObject(i).getInt("id"));
                    province.save();
                }
                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String response,int pcode){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray array=new JSONArray(response);
                for(int i=0;i<array.length();i++){
                    City city=new City();
                    city.setCname(array.getJSONObject(i).getString("name"));
                    city.setCcode(array.getJSONObject(i).getInt("id"));
                    city.setPid(pcode);
                    city.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int ccode){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    County county = new County();
                    county.setName(array.getJSONObject(i).getString("name"));
                    county.setCid(ccode);
                    county.setWid(array.getJSONObject(i).getString("weather_id"));
                    county.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //返回weather类型变量，内存该地天气信息
    //原因详情参考返回的json数据
    public static HeWeather handleWeatherResponse(String response){
        Log.d("Response",response);
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");//获得HeWeather此数组
            String weatherContent=jsonArray.getJSONObject(0).toString();//返回HeWeather数组中唯一元素，并转换成字符串类型
            return new Gson().fromJson(weatherContent,HeWeather.class);//返回weather类型对象
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void handleCityItemData(List<CityItem> cityList){
        try{
            for(int i=0;i<cityList.size();i++){
                CityItem cityItem=new CityItem();
                cityItem.setCityName(cityList.get(i).getCityName());
                cityItem.setCityTmp(cityList.get(i).getCityTmp());
                cityItem.setWeather_id(cityList.get(i).getWeather_id());
                cityItem.setStreet(cityList.get(i).getStreet());
                cityItem.setRemindFlag(cityList.get(i).getRemindFlag());
                cityItem.save();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
