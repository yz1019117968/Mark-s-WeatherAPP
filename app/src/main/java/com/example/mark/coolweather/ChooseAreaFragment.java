package com.example.mark.coolweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mark.coolweather.db.City;
import com.example.mark.coolweather.db.County;
import com.example.mark.coolweather.db.Province;
import com.example.mark.coolweather.util.HttpUtil;
import com.example.mark.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Mark on 2017/12/12.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private int currentLevel;
    private ProgressDialog progressDialog;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private TextView titleText;
    private Button return_b;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        return_b=(Button)view.findViewById(R.id.return_b);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   if(currentLevel==LEVEL_PROVINCE){
                       selectedProvince=provinceList.get(position);
                       queryCity();
                   }
                else if(currentLevel==LEVEL_CITY){
                       selectedCity=cityList.get(position);
                       queryCounty();
                   }
                else if(currentLevel==LEVEL_COUNTY){
                       selectedCounty=countyList.get(position);
                       Intent intent=new Intent(getActivity(),WeatherActivity.class);
                       intent.putExtra("weather_id",selectedCounty.getWid());
                       Log.d("城市",selectedCounty.getName());
                       startActivity(intent);
                       getActivity().finish();
                   }
            }
        });
        return_b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_CITY){
                    queryProvince();
                }
                else if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        queryProvince();
    }
    private void queryProvince(){
        titleText.setText("朕的江山");
        return_b.setVisibility(View.INVISIBLE);
        provinceList=DataSupport.findAll(Province.class);//findall
        if(provinceList.size()>0){
            dataList.clear();//清空原数据
            for(Province province:provinceList){
                dataList.add(province.getPname());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }
        else{
            String address="http://guolin.tech/api/china/";
            queryFromServer(address,"province");
        }
    }
    private void queryCity(){
        titleText.setText("朕的"+selectedProvince.getPname());
        return_b.setVisibility(View.VISIBLE);
        cityList= DataSupport.where("pid=?",String.valueOf(selectedProvince.getPcode())).find(City.class);//find
         if(cityList.size()>0){
             dataList.clear();//清空原数据
             for(City city:cityList){
                 dataList.add(city.getCname());
             }
             adapter.notifyDataSetChanged();
             listView.setSelection(0);
             currentLevel=LEVEL_CITY;
         }
        else{
             String address="http://guolin.tech/api/china/"+selectedProvince.getPcode();
             queryFromServer(address,"city");
         }
    }
    private void queryCounty(){
        titleText.setText("朕的"+selectedCity.getCname());
        return_b.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cid=?",String.valueOf(selectedCity.getCcode())).find(County.class);

        if(countyList.size()>0){
            dataList.clear();//清空原数据
            for(County county:countyList){
                dataList.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }
        else{
            String address="http://guolin.tech/api/china/"+selectedProvince.getPcode()+"/"+selectedCity.getCcode();
            queryFromServer(address,"county");
        }
    }
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString=response.body().string();
                Boolean result=false;
                if("province".equals(type)){
                    result=Utility.handleProvinceResponse(responseString);
                }
                else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseString,selectedProvince.getPcode());
                }
                else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseString,selectedCity.getCcode());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("province".equals(type)){
                                queryProvince();
                            }
                            else if("city".equals(type)){
                                queryCity();
                            }
                            else if("county".equals(type)){
                                queryCounty();
                            }
                            closeProgressDialog();
                        }
                    });
                }
             }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());//getContext()：这个是View类中提供的方法，在继承了View的类中才可以调用，返回的是当前View运行在哪个Activity Context中。
            //getActivity和getcontext其实差不多，一般在fragment中使用的时候，用此方法获取。碎片在哪个activity里，就获取哪个activity的对象
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
