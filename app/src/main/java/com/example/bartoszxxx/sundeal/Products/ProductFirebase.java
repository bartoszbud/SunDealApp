package com.example.bartoszxxx.sundeal.Products;

public class ProductFirebase extends ProductLocal {

    private String item_lowercase;

    public ProductFirebase() {

    }

    public ProductFirebase(String owner, String item, String item_lowercase, String description, String location, Boolean giveaway, String key) {
        super(owner, item, description, location, giveaway, key);
        this.item_lowercase = item_lowercase;
    }

    public String getItem_lowercase() {
        return item_lowercase;
    }

}