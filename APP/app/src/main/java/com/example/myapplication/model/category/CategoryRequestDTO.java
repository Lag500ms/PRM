package com.example.myapplication.model.category;

import com.google.gson.annotations.SerializedName;

public class CategoryRequestDTO {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

