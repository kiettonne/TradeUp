package com.example.tradeup.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String title;
    private String price;
    private String description;
    private String category;
    private String usage;
    private String sellerId;
    private String location;
    private String condition;
    private List<String> tags;
    private List<String> images;
    private long timestamp;
    private String status;
    private int views;
    private int likes;

    public Product() {
        this.id = "";
        this.title = "";
        this.price = "0";
        this.description = "";
        this.category = "";
        this.condition = "";
        this.usage = "";
        this.location = "";
        this.tags = new ArrayList<>();
        this.images = new ArrayList<>();
        this.sellerId = "";
        this.timestamp = System.currentTimeMillis();
        this.status = "Available";
        this.views = 0;
        this.likes = 0;
    }

    public Product(String id, String title, String price, List<String> images, String description,
                   String category, String condition, String location, List<String> tags, String sellerId) {
        this.id = id != null ? id : "";
        this.title = title != null ? title : "";
        this.price = price != null ? price : "0";
        this.description = description != null ? description : "";
        this.category = category != null ? category : "";
        this.condition = condition != null ? condition : "";
        this.usage = "";
        this.location = location != null ? location : "";
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.sellerId = sellerId != null ? sellerId : "";
        this.timestamp = System.currentTimeMillis();
        this.status = "Available";
        this.views = 0;
        this.likes = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id != null ? id : ""; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title : ""; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description : ""; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price != null ? price : "0"; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category != null ? category : ""; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition != null ? condition : ""; }

    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage != null ? usage : ""; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location != null ? location : ""; }

    public List<String> getTags() { return tags != null ? tags : new ArrayList<>(); }
    public void setTags(List<String> tags) { this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>(); }

    public List<String> getImages() { return images != null ? images : new ArrayList<>(); }
    public void setImages(List<String> images) { this.images = images != null ? new ArrayList<>(images) : new ArrayList<>(); }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId != null ? sellerId : ""; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status != null ? status : "Available"; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}

