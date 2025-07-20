package com.example.tradeup.data.model;

import java.util.List;
public class Product {
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

    public Product(String id, String title, String price, List<String> urls, String desc, String category, String condition, String location, List<String> tags, String uid) {}

    public Product(String id, String title, String description, String price, String category,
                   String condition, String usage, String location, List<String> tags,
                   List<String> images, String sellerId, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.usage = usage;
        this.location = location;
        this.tags = tags;
        this.images = images;
        this.sellerId = sellerId;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}

