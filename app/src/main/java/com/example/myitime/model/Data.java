package com.example.myitime.model;

import android.content.Context;

import com.example.myitime.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Data {
    private static ArrayList<Day> days;
    private static ArrayList<Label> labels;
    private static int themeColor=-1;
    private final static String DAY_FILE_NAME="days.txt",LABEL_FILE_NAME="labels.txt",COLOR_FILE_NAME="color.txt";

    public static ArrayList<Day> getDays(Context context) {
        if (days==null){
            days=new ArrayList<>();
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(DAY_FILE_NAME));
                days=(ArrayList<Day>)objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return days;
    }

    public static void setDays(Context context,ArrayList<Day> days) {
        Data.days=days;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(DAY_FILE_NAME, Context.MODE_PRIVATE));//调用Context的openFileOutput方法打开App内部文件
            objectOutputStream.writeObject(days);//将序列化对象days通过对象输出流写入内部文件
            objectOutputStream.close();//记得关闭输出流！！
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Label> getLabels(Context context) {
        if (labels==null){
            labels=new ArrayList<>();
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(LABEL_FILE_NAME));
                labels=(ArrayList<Label>)objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (labels.size()==0){//默认初始标签集
                labels.add(new Label("生日"));
                labels.add(new Label("学习"));
                labels.add(new Label("工作"));
                labels.add(new Label("节假日"));
            }
        }
        return labels;
    }

    public static void setLabels(Context context,ArrayList<Label> labels) {
        Data.labels=labels;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(LABEL_FILE_NAME, Context.MODE_PRIVATE));//调用Context的openFileOutput方法打开App内部文件
            objectOutputStream.writeObject(labels);//将序列化对象days通过对象输出流写入内部文件
            objectOutputStream.close();//记得关闭输出流！！
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getThemeColor(Context context) {
        if (themeColor==-1){//!=-1就说明已经有赋过值了，直接取即可，不必再多读一次文件，浪费时间
            themeColor=context.getResources().getColor(R.color.colorPrimary);//如果文件里还没有存（说明用户没有自己设置过主题色），则默认主题颜色
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(COLOR_FILE_NAME));
                themeColor=(int)objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return themeColor;
    }

    public static void setThemeColor(Context context,int themeColor) {
        if (Data.themeColor!=themeColor){//如果用户折腾了半天最后还是选择了原来的颜色就没必要再写一次文件了，浪费时间
            Data.themeColor = themeColor;
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(COLOR_FILE_NAME, Context.MODE_PRIVATE));//调用Context的openFileOutput方法打开App内部文件
                objectOutputStream.writeObject(themeColor);//将序列化对象days通过对象输出流写入内部文件
                objectOutputStream.close();//记得关闭输出流！！
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
