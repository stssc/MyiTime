package com.example.myitime;

import java.util.ArrayList;

public class FalseData {//先假装这是SQLite里的标签集吧= =
    public static ArrayList<String> labels=new ArrayList<>();
    public static ArrayList<Boolean> isLabelChecked=new ArrayList<>();
    public FalseData(){
        labels.add("study");
        labels.add("work");
        labels.add("life");
        for (int i=0;i<labels.size();i++)
            isLabelChecked.add(false);
    }
}
