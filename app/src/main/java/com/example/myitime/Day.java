package com.example.myitime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Day implements Serializable {
    private byte[] picture;
    private String title,remark;
    private Date time;
    private int period;
    private ArrayList<String> labels;

    public Day(byte[] picture, String title, String remark, Date time,int period,ArrayList<String> labels) {
        this.picture = picture;
        this.title = title;
        this.remark = remark;
        this.time = time;
        this.period=period;
        this.labels=labels;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
