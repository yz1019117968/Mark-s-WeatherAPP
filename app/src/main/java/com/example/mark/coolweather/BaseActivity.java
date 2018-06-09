package com.example.mark.coolweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 2018/5/17.
 */

public class BaseActivity extends AppCompatActivity {
    public static List<Activity> activityList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        Log.d("活动数量",String.valueOf(activityList.size()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(keyCode == KeyEvent.KEYCODE_BACK) {
             for (Activity a : activityList) {
                 a.finish();
             }
             System.exit(0);
         }

        return super.onKeyDown(keyCode, event);
    }
}
