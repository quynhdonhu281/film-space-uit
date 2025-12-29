package com.example.filmspace_mobile.data.model.payment;

public class PaymentMethod {
    private String id;
    private String name;
    private String description;
    private int iconResId;
    private String type; // "vnpay_qr", "vnpay_wallet", "atm", "credit_card"

    public PaymentMethod(String id, String name, String description, int iconResId, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}