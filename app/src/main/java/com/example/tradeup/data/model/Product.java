package com.example.tradeup.data.model;

import java.util.List;

public class Product {
    private String id;
    private String title;
    private String price;
    private List<String> images;

    public Product(String id, String title, String price, List<String> images) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.images = images;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public List<String> getImages() { return images; }
}
