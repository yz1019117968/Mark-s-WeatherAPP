package com.example.mark.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.gson.HeWeather;
import com.example.mark.coolweather.util.HttpUtil;
import com.example.mark.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoNotificationService extends Service {
    public AutoNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onstartCommand","开启");
        UpdateWeather();
        /*Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        long systemTime=System.currentTimeMillis();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mCalendar.set(Calendar.HOUR_OF_DAY, 22);
        mCalendar.set(Calendar.MINUTE, 27);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        long selectTime = mCalendar.getTimeInMillis();
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }*/
        int AnHour=5*60*60*1000;
        long trrigerTime= SystemClock.elapsedRealtime()+AnHour;
        AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent1=new Intent(this,AutoNotificationService.class);
        PendingIntent pi=PendingIntent.getService(this,0,intent1,0);
        alarm.cancel(pi);
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trrigerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("destroy","开启");
        super.onDestroy();
    }

    private void UpdateWeather(){
        Log.d("服务启动","9999");
        CityItem remindCity=null;
        for(int i=0;i< WeatherActivity.cityList.size();i++){
            if(WeatherActivity.cityList.get(i).getRemindFlag().equals("yes")){
                remindCity=WeatherActivity.cityList.get(i);
                break;
            }
        }
        if(remindCity!=null){
        final String weatherUrl="https://free-api.heweather.com/s6/weather?Location="+remindCity.getWeather_id()+"&key=3dfc4d571ef14fcf8879969c874572fa";
        Log.d("URL",weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("进入","旋转");
                final String  responseText=response.body().string();//获取response字符串
                final HeWeather weather= Utility.handleWeatherResponse(responseText);
                        if(weather!=null&&"ok".equals(weather.status)){
                            Intent intent=new Intent(getApplicationContext(),WeatherActivity.class);
                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
                            //NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                            mBuilder.setContentTitle("今日天气").setContentText(weather.basic.location+" 温度 "+weather.now.tmp+"℃ "+weather.now.cond_txt).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.logo1);
                            if(weather.now.cond_txt.equals("晴")){
                                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.sunshine));
                                Log.d("largeIcon","1");
                            }
                            else if(weather.now.cond_txt.equals("多云")){
                                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.cloud));
                                Log.d("largeIcon","2");
                            }
                            else{
                                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.rain));
                                Log.d("largeIcon","3");
                            }
                            Log.d("sIcon","4");
                             mBuilder.setContentIntent(pi);
                            startForeground(1,mBuilder.build());//通过前台服务发送通知
                        }
                    }
        });
    }
    }
}
