package com.example.filmspace_mobile.data.model;

public class SubscriptionPlan {
    private String id;
    private String name;
    private String type; // "monthly" or "yearly"
    private double price;
    private double taxFee;
    private String description;
    private int savingsPercent;

    public SubscriptionPlan(String id, String name, String type, double price, String description, int savingsPercent) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.savingsPercent = savingsPercent;
        this.taxFee = price * 0.1; // 10% tax
    }

    public double getTotal() {
        return price + taxFee;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTaxFee() { return taxFee; }
    public void setTaxFee(double taxFee) { this.taxFee = taxFee; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSavingsPercent() { return savingsPercent; }
    public void setSavingsPercent(int savingsPercent) { this.savingsPercent = savingsPercent; }
}