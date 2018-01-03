package com.example.mark.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Mark on 2017/12/12.
 */

public class County extends DataSupport {
    private int id;
    private String name;
    private String wid;
    private int cid;
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public void setWid(String wid){
        this.wid=wid;
    }
    public String getWid(){
        return wid;
    }
    public void setCid(int cid){
        this.cid=cid;
    }
    public int getCid(){
        return cid;
    }
}
