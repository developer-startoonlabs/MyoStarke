package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetReportDataResponse {

    @SerializedName("session_list")
    @Expose
    private List<SessionList> sessionList = null;
    @SerializedName("session_result")
    @Expose
    private List<SessionResult> sessionResult = null;

    public List<SessionList> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<SessionList> sessionList) {
        this.sessionList = sessionList;
    }

    public List<SessionResult> getSessionResult() {
        return sessionResult;
    }

    public void setSessionResult(List<SessionResult> sessionResult) {
        this.sessionResult = sessionResult;
    }

}