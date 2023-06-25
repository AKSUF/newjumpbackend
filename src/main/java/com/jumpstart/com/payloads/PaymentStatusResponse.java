package com.jumpstart.com.payloads;

public class PaymentStatusResponse {
    private String paymentId;
    private String status;
    private String transactionReference;
    private Double amount;

    public PaymentStatusResponse(String paymentId, String status, String transactionReference, Double amount) {
        this.paymentId = paymentId;
        this.status = status;
        this.transactionReference = transactionReference;
        this.amount = amount;
    }

    // getters and setters

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
