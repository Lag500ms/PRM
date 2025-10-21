package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;

public class ScheduleStatusStats {
    @SerializedName("pending")
    public long pending;

    @SerializedName("confirmed")
    public long confirmed;

    @SerializedName("completed")
    public long completed;

    @SerializedName("cancelled")
    public long cancelled;
}
