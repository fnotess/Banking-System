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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public final class StatementService implements StatementServiceI {

    private static final StatementService INSTANCE = new StatementService();
    BankDataStoreI DATA_STORE = BankDataStore.getInstance();

    private StatementService() {

    }

    public static StatementService getInstance() {
        return INSTANCE;
    }

    /**
     * Method for calculating monthly interest for all transactions in the month, for a given account number.
     *
     * @param accountID - Account number
     * @param month     - Month
     * @return - interest applicable for the month for account
     */
    @Override
    public Double generateMonthlyInterestForAccount(String accountID, Month month) {

        // Fetch the Bank Account for the account ID
        BankAccount bankAccount = DATA_STORE.getBankAccount(accountID);
        if (bankAccount == null) {
            throw new AccountNotFoundException("Bank Account not found for account ID: " + accountID);
        }

        Year statementYear = Year.now(); // Assume statement is for current year

        // Fetch all interest rate rules
        List<InterestRule> applicableInterestRules = DATA_STORE.getAllInterestRules();

        // Filter the transactions applicable for the given month
        // Sort the transactions in the order they are received
        List<Transaction> monthlyTransactions = bankAccount.getTransactions()
                .stream()
                .filter(txn -> !"I".equals(txn.getType()))
                .filter(TimeUtils.getTransactionFilterPredicate(month)).sorted(Comparator.comparing(Transaction::getTransactionId)).toList();

        // Iterate through the transactions and calculate interest
        double interest = 0D;
        for (int i = 0; i < monthlyTransactions.size(); i++) {
            Transaction transaction = monthlyTransactions.get(i);
            LocalDate txnDate = transaction.getDate();

            if (i == 0) {
                // Calculate interest for beginning of month for the balance carried forward
                LocalDate monthStartDate = LocalDate.of(statementYear.getValue(), month.getValue(), 1);
                double openingBalance = 0d;
                if (transaction.getType().equalsIgnoreCase("D")) {
                    openingBalance = transaction.getBalance() - transaction.getAmount();
                } else {
                    openingBalance = transaction.getBalance() + transaction.getAmount();
                }
                interest += calculateInterestBetweenDates(monthStartDate, txnDate, openingBalance, applicableInterestRules);
            } else if (i == monthlyTransactions.size() - 1) {
                // Calculate interest from the last transaction date for the month, till the end of the month
                LocalDate monthEndDate = LocalDate.of(Year.now().getValue(), month.getValue(), txnDate.getMonth().maxLength());
                interest += calculateInterestBetweenDates(txnDate, monthEndDate, transaction.getBalance(), applicableInterestRules);
            } else {
                // Calculate interest for the transactions happened in the middle of the month
                Transaction prevTransaction = monthlyTransactions.get(i - 1);
                LocalDate startDate = prevTransaction.getDate();
                interest += calculateInterestBetweenDates(startDate, txnDate, prevTransaction.getBalance(), applicableInterestRules);
            }
        }

        return interest / statementYear.length();
    }

    /**
     * This method will calculate the interest for a given period of time, for a given set of interest rules, based on the
     * account balance of the beginning of the period.
     *
     * @param startDate       - Start date of the period
     * @param endDate         -  End date of the period
     * @param balance         - Balance at the start of the period
     * @param applicableRules - Interest rules applicable for the period
     * @return - Interest applicable for the given period
     */
    public double calculateInterestBetweenDates(LocalDate startDate, LocalDate endDate, Double balance, List<InterestRule> applicableRules) {
        // Create a tracking variable for the last checked date
        LocalDate lastCheckedDate = endDate;
        double interest = 0d;
        // iterate through the rules from end to the start
        // so that we can find the effective rules for the end date to start date
        for (int i = applicableRules.size() - 1; i >= 0; i--) {
            InterestRule interestRule = applicableRules.get(i);

            if (interestRule.getDate().isBefore(lastCheckedDate) && interestRule.getDate().isAfter(startDate)) {
                // For all the interest rate rules before the end of the period, and after the start of the period
                // Calculate the cumulative interest based on the interest rate rules
                interest += (interestRule.getDate().until(lastCheckedDate, ChronoUnit.DAYS)) * (interestRule.getRate() / 100d) * balance;
                lastCheckedDate = interestRule.getDate().minusDays(1);
            } else if (interestRule.getDate().isBefore(startDate)) {
                // For the last interest rate rule before start of the period (interest paid on carried forward balance)
                // Calculate the cumulative interest based on the interest rate rule
                interest += (startDate.until(lastCheckedDate, ChronoUnit.DAYS) + 1) * (interestRule.getRate() / 100d) * balance;
                break;
            }
        }
        return interest;
    }
}
