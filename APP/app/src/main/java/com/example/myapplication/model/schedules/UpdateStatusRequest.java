package com.example.myapplication.model.schedules;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRequest {
    @SerializedName("id")
    public String id;
    @SerializedName("status")
    public String status;
}
