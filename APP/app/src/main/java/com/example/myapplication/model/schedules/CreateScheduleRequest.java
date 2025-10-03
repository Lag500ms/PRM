package com.example.myapplication.model.schedules;

import com.google.gson.annotations.SerializedName;

public class CreateScheduleRequest {
    @SerializedName("customer")
    public String customer;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("dateTime")
    public String dateTime;
}
