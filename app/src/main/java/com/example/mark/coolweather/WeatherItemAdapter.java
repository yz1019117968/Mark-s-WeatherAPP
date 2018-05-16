package com.example.mark.coolweather;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.coolweather.db.CityItem;
import com.example.mark.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Mark on 2017/12/15.
 */

public class WeatherItemAdapter extends RecyclerView.Adapter<WeatherItemAdapter.ViewHolder> {
    private List<CityItem> cityList;
    final private Context context;
    public WeatherItemAdapter(List<CityItem> cityList,Context context){
        this.cityList=cityList;
        this.context=context;
    }
     static class ViewHolder extends RecyclerView.ViewHolder{
         Button delete_b;
         View item_view;
        TextView item_county,item_temp,item_street;
         public ViewHolder(View view){
              super(view);
             item_view=view;
             item_county=(TextView)view.findViewById(R.id.item_county);
             item_temp=(TextView)view.findViewById(R.id.item_temp);
             delete_b=(Button)view.findViewById(R.id.delete_b);
             item_street=(TextView)view.findViewById(R.id.street);
         }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.item_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,WeatherActivity.class);
                intent.putExtra("weather_id",cityList.get(holder.getAdapterPosition()).getWeather_id());
                intent.putExtra("street",cityList.get(holder.getAdapterPosition()).getStreet());
                context.startActivity(intent);

            }
        });
        holder.delete_b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(WeatherActivity.cityList.size()==1){
                    Toast.makeText(context, "至少保留一座城市", Toast.LENGTH_SHORT).show();
                }else {
                    //删除list中的指定数据，同时在数据库表中数据删除，并重新添加
                    WeatherActivity.cityList.remove(holder.getAdapterPosition());
                    WeatherItemAdapter.this.notifyDataSetChanged();
                    DataSupport.deleteAll(CityItem.class);
                    Utility.handleCityItemData(cityList);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item_county.setText(cityList.get(position).getCityName());
        holder.item_temp.setText(cityList.get(position).getCityTmp());
        holder.item_street.setText(cityList.get(position).getStreet());
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}

