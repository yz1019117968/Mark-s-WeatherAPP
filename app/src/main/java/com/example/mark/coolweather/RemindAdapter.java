package com.example.mark.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Mark on 2018/1/17.
 */

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ViewHolder> {
     List<CityItem> remindList;
     Context context;
     static class ViewHolder extends RecyclerView.ViewHolder{
         TextView cityName;
          Button remind;
         public ViewHolder(View view){
               super(view);
               cityName=(TextView)view.findViewById(R.id.city_name);
               remind=(Button)view.findViewById(R.id.remind);
         }
     }
     public RemindAdapter(Context context,List<CityItem> remindList){
              this.remindList=remindList;
              this.context=context;
     }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        final RemindAdapter.ViewHolder holder=new RemindAdapter.ViewHolder(view);
        //设置提醒城市并添加定时器
        holder.remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.remind.setText("已设置");
                //保证唯一提醒城市
                for(int i=0;i<remindList.size();i++){
                    remindList.get(i).setRemindFlag("no");
                }
                Log.d("数目",String.valueOf(WeatherActivity.cityList.size()));
                remindList.get(holder.getAdapterPosition()).setRemindFlag("yes");
                RemindAdapter.this.notifyDataSetChanged();
                DataSupport.deleteAll(CityItem.class);
                Utility.handleCityItemData(remindList);
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cityName.setText(remindList.get(position).getCityName());
        if(remindList.get(position).getRemindFlag().equals("yes")){
            holder.remind.setText("已设置");
        }
        else{
            holder.remind.setText("设置提醒");
        }
    }

    @Override
    public int getItemCount() {
        return remindList.size();
    }
}
