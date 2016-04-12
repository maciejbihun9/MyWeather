package com.example.maciejbihun.myweather.model;

/**
 * Created by MaciekBihun on 2016-03-23.
 */
public class HourWeather {

    public HourWeather(){

    }
    private double time;
    private String summary;
    private String icon;

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
