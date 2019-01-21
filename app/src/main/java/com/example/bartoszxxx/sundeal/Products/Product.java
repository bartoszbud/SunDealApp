package com.example.bartoszxxx.sundeal.Products;

public class Product {

    private String title;
    private String description;
    private String location;
    private String key;

    public Product() {

    }

    public Product(String item, String description, String location, String key) {
        this.title = item;
        this.description = description;
        this.location = location;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getKey() {
        return key;
    }

}
