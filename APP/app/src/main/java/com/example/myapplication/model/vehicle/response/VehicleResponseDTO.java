package com.example.myapplication.model.vehicle.response;

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

    public VehicleResponseDTO() {}
    public VehicleResponseDTO(String id, String categoryId, String accountId, String color, double price, String model, String version, String image, int quantity) {
        this.id = id;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.color = color;
        this.price = price;
        this.model = model;
        this.version = version;
        this.image = image;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

