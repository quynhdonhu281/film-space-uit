package com.example.filmspace_mobile.data.model.payment;

public class PaymentVerifyResponse {
    private boolean success;
    private String status;
    private String userId;
    private String planId;
    private String planName;
    private double amount;
    private String transactionId;
    private String message;

    // Constructor rỗng
    public PaymentVerifyResponse() {
    }

    // Constructor đầy đủ
    public PaymentVerifyResponse(boolean success, String status, String userId,
                                 String planId, String planName, double amount,
                                 String transactionId, String message) {
        this.success = success;
        this.status = status;
        this.userId = userId;
        this.planId = planId;
        this.planName = planName;
        this.amount = amount;
        this.transactionId = transactionId;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PaymentVerifyResponse{" +
                "success=" + success +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                ", planId='" + planId + '\'' +
                ", planName='" + planName + '\'' +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}