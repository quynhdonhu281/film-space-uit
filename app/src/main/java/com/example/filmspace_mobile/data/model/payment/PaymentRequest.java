package com.example.filmspace_mobile.data.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    @SerializedName("userId")
    private String userId;

    @SerializedName("planId")
    private String planId;

    @SerializedName("planName")
    private String planName;

    @SerializedName("amount")
    private double amount;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("orderInfo")
    private String orderInfo;

    public PaymentRequest(String userId, String planId, String planName,
                          double amount, String paymentMethod, String orderInfo) {
        this.userId = userId;
        this.planId = planId;
        this.planName = planName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderInfo = orderInfo;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
}