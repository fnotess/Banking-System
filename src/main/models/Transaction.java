package main.models;

import main.util.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Transaction {

    private static int transactionIdCounter = 1;
    private LocalDate date;
    private String account;
    private String type;
    private double amount;
    private int transactionId;

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
        this.transactionId = transactionIdCounter++;
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

    public int getTransactionId() {
        return transactionId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
