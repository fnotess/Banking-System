package main.models;

import main.util.TimeUtils;

import java.text.ParseException;
import java.time.LocalDate;

public class Transaction {

    private static int transactionIdCounter = 1;
    private static String transactionDate;
    private LocalDate date;
    private String account;
    private String type;
    private double amount;
    private String transactionId;

    private double balance;

    public Transaction(String date, String account, String type, double amount, double balance) {

        try {
            this.date = TimeUtils.toLocalDate(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.account = account;
        this.type = type.toUpperCase();
        this.amount = amount;
        if (date.equals(transactionDate)) {
            transactionIdCounter++;
        } else {
            transactionDate = date;
            transactionIdCounter = 1;
        }
        this.transactionId = transactionDate + "-0" + transactionIdCounter;
        this.setBalance(balance);
    }

    public String getDateString() {
        try {
            return TimeUtils.toDateString(this.date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getAccount() {
        return account;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
