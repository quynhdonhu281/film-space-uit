package com.example.filmspace_mobile.data.model.payment;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionHistoryResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("transactions")
    private List<Transaction> transactions;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    // Inner class for Transaction
    public static class Transaction {
        @SerializedName("txnRef")
        private String txnRef;

        @SerializedName("amount")
        private double amount;

        @SerializedName("status")
        private String status;

        @SerializedName("planName")
        private String planName;

        @SerializedName("createdAt")
        private String createdAt;

        // Getters and Setters
        public String getTxnRef() { return txnRef; }
        public void setTxnRef(String txnRef) { this.txnRef = txnRef; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}