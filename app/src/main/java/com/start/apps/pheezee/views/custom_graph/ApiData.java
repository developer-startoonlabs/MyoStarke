package com.start.apps.pheezee.views.custom_graph;

public class ApiData implements EmData {

    private int day, l , u;
    public ApiData(String day, int l, int u){
        this.day = GetDay.getDay(day);
        this.l = l;
        this.u = u;
    }
    public ApiData(int day, int l, int u) {
        this.day = day;
        this.l = l;
        this.u = u;
    }
    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getUpperBound() {
        return u;
    }

    @Override
    public int getLowerBound() {
        return l;
    }
}