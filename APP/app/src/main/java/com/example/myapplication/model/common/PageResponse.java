package com.example.myapplication.model.common;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PageResponse<T> {
    @SerializedName("content")
    public List<T> content;
    @SerializedName("number")
    public int number;
    @SerializedName("size")
    public int size;
    @SerializedName("totalPages")
    public int totalPages;
    @SerializedName("totalElements")
    public long totalElements;
}
