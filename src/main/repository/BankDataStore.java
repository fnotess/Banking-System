package main.repository;

import main.models.BankAccount;
import main.models.InterestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BankDataStore implements BankDataStoreI {

    private static final BankDataStore INSTANCE = new BankDataStore();

    private final Map<String, BankAccount> bankAccountMap = new HashMap<>();
    private final List<InterestRule> interestRules = new ArrayList<>();

    private BankDataStore() {

    }

    public static BankDataStore getInstance() {
        return INSTANCE;
    }

    @Override
    public void addInterestRule(InterestRule interestRule) {
        interestRules.removeIf(rule -> rule.getDateString().equals(interestRule.getDateString()));
        interestRules.add(interestRule);
    }

    @Override
    public List<InterestRule> getAllInterestRules() {
        return interestRules;
    }

    @Override
    public void addAccount(BankAccount bankAccount) {
        bankAccountMap.put(bankAccount.getAccountNumber(), bankAccount);
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountMap.values().stream().toList();
    }

    @Override
    public boolean bankAccountExists(String bankAccountID) {
        return bankAccountMap.containsKey(bankAccountID);
    }

    @Override
    public BankAccount getBankAccount(String bankAccountID) {
        return bankAccountMap.get(bankAccountID);
    }
}
