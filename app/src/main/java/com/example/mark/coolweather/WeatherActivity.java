package com.example.mark.coolweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.gson.DailyForcast;
import com.example.mark.coolweather.gson.HeWeather;
import com.example.mark.coolweather.util.HttpUtil;
import com.example.mark.coolweather.util.Utility;

import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mark on 2017/12/13.
 */

public class WeatherActivity extends BaseActivity {
    public LocationClient mLocationClient;
    String currentLocation;
    public  static List<CityItem> cityList=new ArrayList<>();//List
    private ScrollView weatherLayout;
    private TextView titleCity,degreeText,weatherInfoText,titleStreet;
    private TextView windSc,windDir,comfortText,dressText,fluText,status;
    private LinearLayout forecastLayout;
    public  SwipeRefreshLayout swipe_refresh;
    private RecyclerView recycler_city;
    private WeatherItemAdapter adapter;
    private Button add_b,delete_b,locator,setting;
    public DrawerLayout drawer_layout;
    private ImageView image;
    List<String> permissionList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        setting=(Button)findViewById(R.id.setting);
        image=(ImageView)findViewById(R.id.imageView);
        titleStreet=(TextView)findViewById(R.id.street);
        titleCity=(TextView)findViewById(R.id.title_city);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        windSc=(TextView)findViewById(R.id.wind_sc);
        windDir=(TextView)findViewById(R.id.wind_dir);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        dressText=(TextView)findViewById(R.id.dress_text);
        fluText=(TextView)findViewById(R.id.flu_text);
        status=(TextView)findViewById(R.id.status);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        swipe_refresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);//设置刷新图标颜色
        recycler_city=(RecyclerView)findViewById(R.id.recycler_city);
        add_b=(Button)findViewById(R.id.add_b);
        delete_b=(Button)findViewById(R.id.delete_b);
        locator=(Button)findViewById(R.id.location);
        drawer_layout=(DrawerLayout)findViewById(R.id.drawer_layout);
        final String weather_id;
        status.setText("点击左侧获取定位");

        //请求权限
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        Log.d(String.valueOf(permissionList.size()),"权限数量 ");
        if (!permissionList.isEmpty()&&getIntent().getStringExtra("weather_id")==null) {
            Log.d("未获取权限","请求");
            Log.d(String.valueOf(permissionList.size()),"权限数量 ");
            String[] permisssionArray = permissionList.toArray(new String[permissionList.size()]);//由列表转换为字符串型数组
            ActivityCompat.requestPermissions(WeatherActivity.this, permisssionArray, 1);//第二个参数为字符串类型数组，集体请求权限
            weather_id=null;
        }else{
            cityList=DataSupport.findAll(CityItem.class);//数据库里数据存入list
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            final String weatherContent=sharedPreferences.getString("weather",null);//到该文件中查找weather字段的内容（因为存储时键为weather），没有则返回null
            final String street=sharedPreferences.getString("street",null);
            /*String bingPic=sharedPreferences.getString("bing_pic",null);//获取必应键值对
            if(bingPic!=null){
                Glide.with(this).load(bingPic).into(image);
                Log.d("图片",bingPic);
            }
            else{
                loadBingPic();
                Log.d("图片","即时加载");
            }*/
            if(getIntent().getStringExtra("weather_id")!=null&&getIntent().getStringExtra("street")!=null){
                Log.d("定位城市进入",String.valueOf(mLocationClient.isStarted()));
                Log.d(String.valueOf(getIntent().getStringExtra("weather_id")),String.valueOf(getIntent().getStringExtra("street")));
                initLocation();
                mLocationClient.start();
                //Log.d("定位进入3",String.valueOf(mLocationClient.isStarted()));
                weather_id=null;
            }
            else if(getIntent().getStringExtra("weather_id")!=null&&getIntent().getStringExtra("street")==null){
                Log.d("选择城市进入",String.valueOf(getIntent().getStringExtra("street")));
                weather_id=getIntent().getStringExtra("weather_id");
                requestWeather(weather_id,null);
            }
           /* else if(titleStreet.getText()!=null){
                weather_id=null;
            }*/
            else if(weatherContent!=null){
                Log.d("直接打开","有缓存");
                HeWeather weather= Utility.handleWeatherResponse(weatherContent);
                weather_id=weather.basic.cid;//有缓存时通过缓存获取天气id
                showWeatherInfo(weather,street);
                //加载缓存图片
                String bingPic=sharedPreferences.getString("bing_pic",null);//获取必应键值对
                if(bingPic!=null){
                    Glide.with(this).load(bingPic).into(image);
                    Log.d("图片",bingPic);
                }
                else{
                    loadBingPic();
                    Log.d("图片","即时加载");
                }
            }
            else{
                Log.d("直接打开","没缓存");
                initLocation();
                if(mLocationClient.isStarted()==true){
                    Log.d("定位进入",String.valueOf(mLocationClient.isStarted()));
                    mLocationClient.restart();
                }
                else{
                    Log.d("定位进入",String.valueOf(mLocationClient.isStarted()));
                    mLocationClient.start();
                }
                weather_id=null;
            }//initLocation();
            Log.d("权限允许",String.valueOf(mLocationClient.isStarted()));
        }

            //Log.d("缓存","11111");
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recycler_city.setLayoutManager(layoutManager);
         adapter=new WeatherItemAdapter(cityList,this);
        recycler_city.setAdapter(adapter);
        //Log.d("recycler","....");
        locator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherActivity.this.drawer_layout.closeDrawers();
                if (!permissionList.isEmpty()) {
                    Log.d("请求权限",String.valueOf(permissionList.size()));
                    Toast.makeText(WeatherActivity.this, "请先允许获取定位权限", Toast.LENGTH_SHORT).show();
                    String[] permisssionArray = permissionList.toArray(new String[permissionList.size()]);//由列表转换为字符串型数组
                    ActivityCompat.requestPermissions(WeatherActivity.this, permisssionArray, 1);//第二个参数为字符串类型数组，集体请求权限
                }
                else {
                    initLocation();
                    if (mLocationClient.isStarted() == false) {
                        mLocationClient.start();
                        Log.d("手动定位","开启定位");
                    } else {
                        mLocationClient.restart();
                        Log.d("手动定位","重启定位");
                    }
                }
            }
        });
        add_b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cityList.size()<=4) {
                    Log.d("城市列表数目",String.valueOf(cityList.size()));
                    Intent intent = new Intent(WeatherActivity.this, Main2Activity.class);
                    WeatherActivity.this.finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(WeatherActivity.this, "最多添加5座城市", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity.this,settingActivity.class);
                startActivity(intent);
            }
        });
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                if (titleStreet.getText().equals("")) {
                    requestWeather(weather_id, null);
                    Log.d("非定位", "222222");
                } else {
                    Log.d("定位进入2",String.valueOf(mLocationClient.isStarted()));
                    Log.d("定位", "11111");
                    initLocation();
                    mLocationClient.restart();
                    //mLocationClient.stop();
                }
            }
        });
    }
    //onCreate
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0) {
                    boolean flag=false;
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "将无法自动获取天气", Toast.LENGTH_SHORT).show();
                            if(cityList.isEmpty()) {
                                Intent intent = new Intent(this, Main2Activity.class);
                                startActivity(intent);
                            }
                            flag=true;
                            break;
                            //跳转至手动选择位置
                        }
                    }
                    if(flag==false) {
                        permissionList.clear();//获取所有权限，所以清空
                        if(!mLocationClient.isStarted()) {
                            initLocation();
                            mLocationClient.start();
                        }
                        else{
                            mLocationClient.restart();
                        }
                        Log.d("定位进入4",String .valueOf(mLocationClient.isStarted()));
                    }
                } else {
                    Toast.makeText(this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);//选择获取详细位置信息
        mLocationClient.setLocOption(option);
    }
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(bdLocation.getLongitude()).append(",");
            currentPosition.append(bdLocation.getLatitude());
            String street=bdLocation.getStreet();

            int code=bdLocation.getLocType();
            currentLocation=currentPosition.toString();
            if(street!=null) {
                requestWeather(currentLocation, street);
                Log.d("street",street);
            }
            else{
                requestWeather(currentLocation, " ");
            }
            status.setText("已定位");
            Log.d("current",currentLocation);
            Log.d("code",String.valueOf(code));
            //Toast.makeText(WeatherActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
            //Log.d("street",bdLocation.getStreet());
        }
    }
    //加载必应图片
    private void loadBingPic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder().url("http://cn.bing.com/HPImageArchive.aspx?format=xml&idx=0&n=1").build();//请求url地址
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();//url返回数据
                    final String requestBingPic="http://s.cn.bing.net"+parseXMLWithPull(responseData);
                    Log.d("PICTURE",requestBingPic);
                                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                    editor.putString("bing_pic",requestBingPic);//键：weather，值：responsetext
                                    editor.apply();//以键值对的文件形式存储
                                    WeatherActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            RequestListener<String,GlideDrawable> errorListener=new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                                                Log.e("onException",e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                Log.e("onResourceReady","isFromMemoryCache:"+isFromMemoryCache+"  model:"+model+" isFirstResource: "+isFirstResource);
                                                return false;
                                            }
                                        } ;
                                            Glide.with(WeatherActivity.this).load(requestBingPic).listener(errorListener).into(image);
                                            Log.d("Picture:",requestBingPic);

                                        }
                                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private String parseXMLWithPull(String responseData){
        String urlData="";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(new StringReader(responseData));
            int eventType=parser.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName=parser.getName();
                if(eventType==XmlPullParser.START_TAG&&nodeName.equals("url")){
                    urlData=parser.nextText();
                    Log.d("url",urlData);
                    break;
                }
                eventType=parser.next();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return urlData;
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
    //请求天气信息并存入citylist以及更新cityItem表
    public void requestWeather(String weather_id,final String street){
        final String weatherUrl="https://free-api.heweather.com/s6/weather?Location="+weather_id+"&key=3dfc4d571ef14fcf8879969c874572fa";
        //Toast.makeText(WeatherActivity.this, weather_id, Toast.LENGTH_LONG).show();
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                /*WeatherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //Log.d("fail:","12345");
                        swipe_refresh.setRefreshing(false);//请求后隐藏刷新图标
                    }
                });*/
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("SUCCESS","12345");
                final String  responseText=response.body().string();//获取response字符串
               final HeWeather weather=Utility.handleWeatherResponse(responseText);
                WeatherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //String currentStr;
                            int num=-1;
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);//键：weather，值：responsetext
                            editor.putString("street",street);
                            editor.apply();//以键值对的文件形式存储
                            boolean flag=false;
                            for(int i=0;i<cityList.size();i++) {
                                Log.d(cityList.get(i).getWeather_id(),weather.basic.cid);
                                //城市weatherId相同或者cityName相同，只修改温度
                               if(cityList.get(i).getStreet()!=null){
                                    num=i;//记录第i个城市为定位城市
                                }//判断是否有定位城市
                                if(weather.basic.cid.equals(cityList.get(i).getWeather_id())){
                                    cityList.get(i).setCityTmp(weather.now.tmp+"℃");
                                    cityList.get(i).setStreet(street);
                                    DataSupport.deleteAll(CityItem.class);
                                    Utility.handleCityItemData(cityList);
                                    adapter.notifyDataSetChanged();
                                    flag=true;
                                    break;
                                }
                            }
                            //添加新城市
                            if(flag==false) {
                                CityItem cityItem = new CityItem();
                                cityItem.setCityName(weather.basic.location);
                                cityItem.setStreet(street);
                                cityItem.setCityTmp(weather.now.tmp + "℃");
                                cityItem.setWeather_id(weather.basic.cid);
                                cityItem.setRemindFlag("no");//初始均为不提醒
                                //如果已存在定位城市，定位城市替换首位
                                if(street!=null&&num!=-1){
                                   cityList.set(0,cityItem);
                                   Log.d("替换定位城市","1234567890");
                                }
                                //不存在已定位城市，定位城市设置为首位
                                else if(street!=null&&num==-1){
                                    cityList.add(0,cityItem);
                                }
                                //普通城市添加末尾
                                else{
                                    cityList.add(cityItem);
                                }
                                DataSupport.deleteAll(CityItem.class);
                                Utility.handleCityItemData(cityList);
                                adapter.notifyDataSetChanged();
                                Log.d("存储",String.valueOf(cityList.size()));
                            }
                            showWeatherInfo(weather,street);
                        }
                        else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败!", Toast.LENGTH_SHORT).show();
                           // Log.d("win:",weather.status);
                        }
                        Log.d("关闭","旋转");
                        swipe_refresh.setRefreshing(false);//请求后隐藏刷新图标
                    }
                });

            }
        });
        loadBingPic();
    }
    private void showWeatherInfo(HeWeather weather,String street){
        titleCity.setText(weather.basic.location);
        titleStreet.setText(street);
        degreeText.setText(weather.now.tmp+"℃");
        weatherInfoText.setText(weather.now.cond_txt);
        forecastLayout.removeAllViews();//去掉所有控件
        for(DailyForcast forecast:weather.daily_forecast){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);//动态加载布局(参数：布局.xml,所在容器,false)
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond_txt_d);
            maxText.setText(forecast.tmp_max+"℃");
            minText.setText(forecast.tmp_min+"℃");
            forecastLayout.addView(view);//将该控件添加入容器
        }
        windSc.setText(weather.now.wind_sc);
        windDir.setText(weather.now.wind_dir);
        comfortText.setText("舒适度："+weather.lifestyle.get(0).txt);
        dressText.setText("穿衣建议："+weather.lifestyle.get(1).txt);
        fluText.setText("流感指数："+weather.lifestyle.get(2).txt);
        weatherLayout.setVisibility(View.VISIBLE);//使滚动布局显示
        Intent intent=new Intent(this,AutoNotificationService.class);
        startService(intent);
    }
}
