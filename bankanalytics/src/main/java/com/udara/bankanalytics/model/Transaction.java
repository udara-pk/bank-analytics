package com.udara.bankanalytics.model;

import java.io.Serializable;

public class Transaction implements Serializable{
	private String transactionId;
    private String userId;
    private String merchant;
    private String timestamp;
    private double amount;
    private String category;

    public Transaction() {}

    public Transaction(String transactionId, String userId, String merchant, String timestamp, double amount, String category) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.merchant = merchant;
        this.timestamp = timestamp;
        this.amount = amount;
        this.category = category;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
