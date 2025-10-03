package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;

public class UpcomingScheduleItem {
    @SerializedName("id")
    public String id;
    @SerializedName("customer")
    public String customer;
    @SerializedName("dateTime")
    public String dateTime;
    @SerializedName("status")
    public String status;
}
