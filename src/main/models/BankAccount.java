package main.models;

import main.util.TimeUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BankAccount {

    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;

    private LocalDate lastUpdatedDate;

    public BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(String givenDate, double amount, String depositType) throws ParseException {
        LocalDate depositDate = TimeUtils.toLocalDate(givenDate);
        if (lastUpdatedDate != null && depositDate.isBefore(lastUpdatedDate)) {
            System.out.printf("Cannot deposit for past dates Last update date: %s, Deposit date: %s%n",
                    TimeUtils.toDateString(lastUpdatedDate), givenDate);
            return;
        } else {
            lastUpdatedDate = depositDate;
        }
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction(givenDate, accountNumber, depositType.toUpperCase(), amount, balance));
        }
    }

    public boolean withdraw(String givenDate, double amount) throws ParseException {
        LocalDate withdrawDate = TimeUtils.toLocalDate(givenDate);
        if (lastUpdatedDate != null && withdrawDate.isBefore(lastUpdatedDate)) {
            System.out.printf("Cannot deposit for past dates Last update date: %s, Withdraw date: %s%n",
                    TimeUtils.toDateString(lastUpdatedDate), givenDate);
            return false;
        } else {
            lastUpdatedDate = withdrawDate;
        }
        if (amount > 0 && balance >= amount) {
            System.out.println("WITHSDDRAW");
            balance -= amount;
            transactions.add(new Transaction(givenDate, accountNumber, "W", amount, balance));
            return true;
        }
        return false;
    }

    public boolean balanceCheck(double amount) {
        return amount > 0 && balance >= amount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
