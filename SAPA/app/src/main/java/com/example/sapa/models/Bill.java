package com.example.sapa.models;
import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("bill_code")
    private String billCode;

    @SerializedName("amount")
    private double amount;

    @SerializedName("issued_at")
    private String issuedAt;

    @SerializedName("status")
    private String status;

    // No-arg constructor required by Gson
    public Bill() {}

    // Existing constructor for testing
    public Bill(String billCode, double amount, String issuedAt, String status) {
        this.billCode = billCode;
        this.amount = amount;
        this.issuedAt = issuedAt;
        this.status = status;
    }

    // Getters
    public String getBillCode() { return billCode; }
    public double getAmount() { return amount; }
    public String getIssuedAt() { return issuedAt; }
    public String getStatus() { return status; }
}
