package com.example.myitime;

import java.util.ArrayList;

public class Label {
    private String name;
    private ArrayList<Day> days;

    public Label(String name){
        this.name=name;
        days=new ArrayList<>();
    }

    public ArrayList<Day> getDays(){
        return days;
    }

}
