package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class MonthlyRevenueItem {
    @SerializedName("month")
    public String month;
    @SerializedName("revenue")
    public BigDecimal revenue;
}
