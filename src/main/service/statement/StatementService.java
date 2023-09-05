package main.service.statement;

import main.exceptions.AccountNotFoundException;
import main.models.BankAccount;
import main.models.InterestRule;
import main.models.Transaction;
import main.repository.BankDataStore;
import main.repository.BankDataStoreI;
import main.util.TimeUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class StatementService implements StatementServiceI {

    private static final StatementService INSTANCE = new StatementService();
    BankDataStoreI DATA_STORE = BankDataStore.getInstance();

    private StatementService() {

    }

    public static StatementService getInstance() {
        return INSTANCE;
    }

    @Override
    public Double generateMonthlyInterestForAccount(String accountID, Month month) {

        BankAccount bankAccount = DATA_STORE.getBankAccount(accountID);
        if (bankAccount == null) {
            throw new AccountNotFoundException("Bank Account not found for account ID: " + accountID);
        }

        Year statementYear = Year.now(); // Assume statement is for current year

        List<InterestRule> applicableInterestRules = DATA_STORE.getAllInterestRules();

        List<Transaction> monthlyTransactions = bankAccount.getTransactions()
                .stream()
                .filter(txn -> !"I".equals(txn.getType()))
                .filter(TimeUtils.getTransactionFilterPredicate(month))
                .sorted(Comparator.comparing(Transaction::getTransactionId))
                .toList();

        double interest = 0D;
        for (int i = 0; i < monthlyTransactions.size(); i++) {
            Transaction transaction = monthlyTransactions.get(i);
            // Get the latest interest rule on or before the transaction date
            // This is optional since there might not be a rule defined on or before the transaction date
            Optional<InterestRule> matchingInterestRule = applicableInterestRules.stream()
                    .filter(rule -> rule.getDate().isBefore(transaction.getDate())
                            || rule.getDate().equals(transaction.getDate()))
                    .max(Comparator.comparing(InterestRule::getDate));


            // Get the closest next interest rule after transaction date
            // This is also optional since there might not be a rule after transaction date
            Optional<InterestRule> nextInterestRule = applicableInterestRules.stream()
                    .filter(rule -> rule.getDate().isAfter(transaction.getDate()))
                    .min(Comparator.comparing(InterestRule::getDate));
            if (matchingInterestRule.isPresent()) {
                if (i < monthlyTransactions.size() - 1
                        && monthlyTransactions.get(i).getDate().equals(monthlyTransactions.get(i + 1).getDate())) {
                    continue;
                }
                if (i < monthlyTransactions.size() - 1) {
                    LocalDate nextTxnStartDate = monthlyTransactions.get(i + 1).getDate();
                    if (nextInterestRule.isPresent()) {
                        LocalDate nextRuleStartDate = nextInterestRule.get().getDate();
                        if (nextRuleStartDate.isBefore(nextTxnStartDate)) {
                            interest += (nextRuleStartDate.getDayOfMonth() - transaction.getDate().getDayOfMonth())
                                    * (matchingInterestRule.get().getRate() / 100D) * transaction.getBalance();
                            interest += (nextTxnStartDate.getDayOfMonth() - nextRuleStartDate.getDayOfMonth())
                                    * (nextInterestRule.get().getRate() / 100D) * transaction.getBalance();
                        } else {
                            interest += (nextTxnStartDate.getDayOfMonth() - transaction.getDate().getDayOfMonth())
                                    * (matchingInterestRule.get().getRate() / 100D) * transaction.getBalance();
                        }
                    } else {
                        interest += (nextTxnStartDate.getDayOfMonth() - transaction.getDate().getDayOfMonth())
                                * (matchingInterestRule.get().getRate() / 100D) * transaction.getBalance();
                    }
                } else {
                    interest += (1 + transaction.getDate().lengthOfMonth() - transaction.getDate().getDayOfMonth())
                            * (matchingInterestRule.get().getRate() / 100D) * transaction.getBalance();
                }
            }
        }

        return interest / statementYear.length();
    }
}
