package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sessiondetail {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("heldon")
    @Expose
    private Object heldon;
    @SerializedName("date")
    @Expose
    private Object date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getHeldon() {
        return heldon;
    }

    public void setHeldon(Object heldon) {
        this.heldon = heldon;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

}