package main.repository;

import main.models.BankAccount;
import main.models.InterestRule;

import java.util.List;

public interface BankDataStoreI {

    void addInterestRule(InterestRule interestRule);
    List<InterestRule> getAllInterestRules();

    void addAccount(BankAccount bankAccount);

    List<BankAccount> getAllBankAccounts();

    boolean bankAccountExists(String bankAccountID);
    BankAccount getBankAccount(String bankAccountID);


}
