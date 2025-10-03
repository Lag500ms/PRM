package com.example.myapplication.model.schedules;

import com.google.gson.annotations.SerializedName;

public class ScheduleResponse {
    @SerializedName("id")
    public String id;
    @SerializedName("customer")
    public String customer;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("dateTime")
    public String dateTime;
    @SerializedName("status")
    public String status;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;
}
