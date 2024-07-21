package com.taam.collection_management_system;

public class Item {

    private String lot;
    private String name;
    private String category;
    private String period;
    private String description;
    private String videoUrl;

    public Item() {}

    public Item(String lot, String name, String category, String period,
                String description, String videoUrl) {
        this.lot = lot;
        this.name = name;
        this.category = category;
        this.period = period;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    // Getters and setters
    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
