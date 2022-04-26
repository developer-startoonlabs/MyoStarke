package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Overalldetail {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("bodypart")
    @Expose
    private String bodypart;
    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("download_status")
    @Expose
    private Boolean download_status;

    public Boolean getDownload_status() {
        return download_status;
    }

    public void setDownload_status(Boolean download_status) {
        this.download_status = download_status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBodypart() {
        return bodypart;
    }

    public void setBodypart(String bodypart) {
        this.bodypart = bodypart;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}