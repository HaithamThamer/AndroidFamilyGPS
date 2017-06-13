package com.familygps.familygps;


public class Number {
    public long id;
    public String number;
    public boolean isSaveMode = false;
    public Number(long id, String number, boolean isSaveMode){
        this.id = id;
        this.number = number;
        this.isSaveMode = isSaveMode;
    }
}
