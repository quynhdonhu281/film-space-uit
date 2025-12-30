package com.example.filmspace_mobile.data.model.payment;

import com.google.gson.annotations.SerializedName;

public class SubscriptionCheckResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("isPremium")
    private boolean isPremium;

    @SerializedName("subscriptionPlan")
    private String subscriptionPlan;

    @SerializedName("subscriptionStartDate")
    private String subscriptionStartDate;

    @SerializedName("subscriptionEndDate")
    private String subscriptionEndDate;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }

    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public String getSubscriptionStartDate() { return subscriptionStartDate; }
    public void setSubscriptionStartDate(String subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public String getSubscriptionEndDate() { return subscriptionEndDate; }
    public void setSubscriptionEndDate(String subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }
}