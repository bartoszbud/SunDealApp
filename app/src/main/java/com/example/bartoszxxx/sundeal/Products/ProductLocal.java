package com.example.bartoszxxx.sundeal.Products;

public class ProductLocal extends Product {

    private Boolean giveaway;
    private String owner;

    public ProductLocal() {

    }

    public ProductLocal(String owner, String item, String description, String location, Boolean giveaway, String key) {
        super(item, description, location, key);
        this.owner = owner;
        this.giveaway = giveaway;
    }

    public Boolean getGiveaway() {
        return giveaway;
    }

    public String getOwner() {
        return owner;
    }

}
