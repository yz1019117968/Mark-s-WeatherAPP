package com.example.mark.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Mark on 2017/12/12.
 */

public class City extends DataSupport {
    int id;
    String cname;
    int ccode;
    int pid;
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public void setCname(String name){
        cname=name;
    }
    public String getCname(){
        return cname;
    }
    public void setCcode(int code){
        ccode=code;
    }
    public int getCcode(){
        return ccode;
    }
    public void setPid(int pid){
        this.pid=pid;
    }
    public int getPid(){
        return pid;
    }
}
