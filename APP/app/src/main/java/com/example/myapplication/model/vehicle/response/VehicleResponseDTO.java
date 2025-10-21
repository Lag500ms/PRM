package com.example.myapplication.model.vehicle.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private String categoryId;
    private String accountId;
    private String color;
    private double price;
    private String model;
    private String version;
    private String image;
    private int quantity;
}

