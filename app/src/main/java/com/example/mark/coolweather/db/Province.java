package com.example.mark.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Mark on 2017/12/12.
 */

public class Province extends DataSupport {
    private int id;
    private String pname;
    private int pcode;
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public void setPname(String name){
        pname=name;
    }
    public String getPname(){
        return pname;
    }
    public void setPcode(int code){
        pcode=code;
    }
    public int getPcode(){
        return pcode;
    }
}
