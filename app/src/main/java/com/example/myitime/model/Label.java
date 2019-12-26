package com.example.myitime.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Label implements Serializable {
    private String name;
    private ArrayList<Day> days;

    public Label(String name){
        this.name=name;
        days=new ArrayList<>();
    }

    public ArrayList<Day> getDays(){//用于导航栏的标签集，这部分功能尚未开发
        return days;
    }

    public String getName() {
        return name;
    }
}
