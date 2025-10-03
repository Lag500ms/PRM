package com.example.myapplication.model.orders;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRequest {
    @SerializedName("id")
    public String id;
    @SerializedName("status")
    public String status;
}
