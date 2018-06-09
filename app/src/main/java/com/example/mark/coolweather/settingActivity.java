package com.example.mark.coolweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

public class settingActivity extends BaseActivity {
    private Switch switch1;
    private RecyclerView recycler_city;
    private RelativeLayout layout_suggest;
    private Button back_to_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        back_to_main=(Button)findViewById(R.id.back_to_main);
        layout_suggest=(RelativeLayout)findViewById(R.id.layout_suggest);
        recycler_city=(RecyclerView)findViewById(R.id.recycler_city);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recycler_city.setLayoutManager(layoutManager);
       final RemindAdapter adapter=new RemindAdapter(this,WeatherActivity.cityList);
        recycler_city.setAdapter(adapter);
        switch1=(Switch)findViewById(R.id.switch1);
        back_to_main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(settingActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(settingActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                }//如果没有获得权限，向用户申请，3为请求码
                else {
                    Log.d("没有请求权限","11111");
                    sendEmail();
                }
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switch1.setText("禁止");
                    recycler_city.setVisibility(View.GONE);
                    for(int i=0;i<WeatherActivity.cityList.size();i++){
                        WeatherActivity.cityList.get(i).setRemindFlag("no");
                    }
                    Log.d("数目",String.valueOf(WeatherActivity.cityList.size()));
                    adapter.notifyDataSetChanged();
                    DataSupport.deleteAll(CityItem.class);
                    Utility.handleCityItemData(WeatherActivity.cityList);
                }
                else{
                    switch1.setText("允许");
                    recycler_city.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void sendEmail(){
    try {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        Log.d("email", "okok");
        intent.setData(Uri.parse("mailto:"+"1019117968@qq.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "关于振哥的天气查询app的一些建议"); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "振哥好！"); // 正文
        startActivity(intent);
    }
    catch(Exception e){
        e.printStackTrace();
    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmail();
            }//若用户授权，则可执行call（）
            else {
                Toast.makeText(this, "抱歉，您拒绝了发送邮件权限", Toast.LENGTH_SHORT).show();
            }//若用户未授权，无法执行
                break;
            default:
                break;
        }
    }
}
