package main;

import main.models.BankAccount;
import main.models.InterestRule;
import main.models.Transaction;
import main.repository.BankDataStore;
import main.repository.BankDataStoreI;
import main.service.statement.StatementService;
import main.service.statement.StatementServiceI;
import main.util.TimeUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class Main {

    private static final BankDataStoreI DATA_STORE = BankDataStore.getInstance();
    private static final StatementServiceI STATEMENT_SERVICE = StatementService.getInstance();

    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to AwesomeGIC Bank! What would you like to do?");
            System.out.println("[I]nput transactions");
            System.out.println("[D]efine interest rules");
            System.out.println("[P]rint statement");
            System.out.println("[Q]uit");
            System.out.print("> ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "I":
                    inputTransactions(scanner);
                    break;
                case "D":
                    defineInterestRules(scanner);
                    break;
                case "P":
                    printStatement(scanner);
                    break;
                case "Q":
                    System.out.println("Thank you for banking with AwesomeGIC Bank.");
                    System.out.println("Have a nice day!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public static void inputTransactions(Scanner scanner) throws ParseException {
        System.out.println("Please enter transaction details in <Date>|<Account>|<Type>|<Amount> format");
        System.out.println("(or enter blank to go back to the main menu):");
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.split("\\|");
        if (parts.length != 4) {
            System.out.println("Invalid input format. Please use the format <Date>|<Account>|<Type>|<Amount>.");
            return;
        }

        String date = parts[0];
        String accountNumber = parts[1];
        String type = parts[2];
        double amount;

        try {
            amount = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
            return;
        }

        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Please use YYYYMMdd.");
            return;
        }

        if (!type.equalsIgnoreCase("D") && !type.equalsIgnoreCase("W")) {
            System.out.println("Invalid transaction type. Use 'D' for deposit or 'W' for withdrawal.");
            return;
        }

        if (!DATA_STORE.bankAccountExists(accountNumber)) {
            if (type.equalsIgnoreCase("W")) {
                System.out.println("The first transaction for an account should not be a withdrawal.");
                return;
            }
            DATA_STORE.addAccount(new BankAccount(accountNumber));
        }

        BankAccount account = DATA_STORE.getBankAccount(accountNumber);
        if (type.equalsIgnoreCase("W") && !account.balanceCheck(amount)) {
            System.out.println("Withdrawal amount exceeds account balance.");
            return;
        }

        if (type.equalsIgnoreCase("D")) {
            account.deposit(date, amount, type);
            printAccountStatement(account);
        }

        if (type.equalsIgnoreCase("W")) {
            account.withdraw(date, amount);
            printAccountStatement(account);
        }
    }

    public static boolean isValidDate(String date) {
        return date.matches("^\\d{8}$");
    }

    private static void defineInterestRules(Scanner scanner) throws ParseException {
        System.out.println("Please enter interest rules details in <Date>|<RuleId>|<Rate in %> format");
        System.out.println("(or enter blank to go back to the main menu):");
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.split("\\|");
        if (parts.length != 3) {
            System.out.println("Invalid input format. Please use the format <Date>|<RuleId>|<Rate>.");
            return;
        }

        String date = parts[0];
        String ruleId = parts[1];
        double rate;

        try {
            rate = Double.parseDouble(parts[2]);
            if (rate <= 0 || rate >= 100) {
                System.out.println("Invalid interest rate. Rate should be greater than 0 and less than 100.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid rate. Please enter a valid number.");
            return;
        }

        DATA_STORE.addInterestRule(new InterestRule(date, ruleId, rate));
//            interestRules.removeIf(rule -> rule.getDate().equals(date));
//            interestRules.add(new InterestRule(date, ruleId, rate));
        printInterestRules();
    }

    private static void printInterestRules() {
        System.out.println("Interest rules:");
        System.out.println("Date     | RuleId | Rate (%)");
        for (InterestRule rule : DATA_STORE.getAllInterestRules()) {
            System.out.printf("%s | %s | %.2f%n", rule.getDateString(), rule.getRuleId(), rule.getRate());
        }
    }

    private static void printAccountStatement(BankAccount account) {
        printAccountStatement(account, null);
    }

    private static void printAccountStatement(BankAccount account, Month month) {
        System.out.println("Account: " + account.getAccountNumber());
        System.out.println("Date \t\t| Txn Id \t\t| Type \t| Amount \t| Balance \t|");
        DecimalFormat df = new DecimalFormat("0.00");

        List<Transaction> applicableTransactions = month == null
                ? account.getTransactions() : account.getTransactions().stream()
                .filter(TimeUtils.getTransactionFilterPredicate(month))
                .toList();

        for (Transaction transaction : applicableTransactions) {
            if ("I".equals(transaction.getType())) {
                continue;
            }
            System.out.printf("%s \t| %s \t| %s \t| %s \t| %s \t|\n", transaction.getDateString(), transaction.getTransactionId(),
                    transaction.getType(), df.format(transaction.getAmount()), df.format(transaction.getBalance()));
        }
    }

    private static void printStatement(Scanner scanner) throws ParseException {
        System.out.println("Please enter account and month to generate the statement <Account>|<Month>");
        System.out.println("(or enter blank to go back to the main menu):");
        System.out.print("> ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.split("\\|");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Please use the format <Account>|<Month>.");
            return;
        }

        String accountNumber = parts[0];
        String month = parts[1];

        if (!DATA_STORE.bankAccountExists(accountNumber)) {
            System.out.println("Account not found.");
            return;
        }

        int monthNumber = Integer.parseInt(month);
        Month monthObj = Month.of(monthNumber);

        BankAccount account = DATA_STORE.getBankAccount(accountNumber);
        printAccountStatement(account, monthObj);

        // Apply interest
        applyInterest(account, monthObj);
    }


    public static void applyInterest(BankAccount account, Month month) throws ParseException {
        Transaction lastTxn = account.getTransactions().get(account.getTransactions().size() - 1);
        if ("I".equals(lastTxn.getType()) && lastTxn.getDate().getDayOfMonth() == month.maxLength()
                && lastTxn.getDate().getYear() == Year.now().getValue()
                && lastTxn.getAccount().equals(account.getAccountNumber())) {
            System.out.printf("%s \t| %s \t\t\t| %s \t| %.2f \t\t| %.2f \t|%n", TimeUtils.toDateString(lastTxn.getDate()), " ",
                    "I", lastTxn.getAmount(), lastTxn.getBalance());
        } else {
            double interest = STATEMENT_SERVICE.generateMonthlyInterestForAccount(account.getAccountNumber(), month);
            Year statementYear = Year.now(); // Assume statement is for current year
            LocalDate endOfMonth = LocalDate.of(statementYear.getValue(), month.getValue(), month.maxLength());

            account.deposit(TimeUtils.toDateString(endOfMonth), interest, "I");

            System.out.printf("%s \t| %s \t\t\t| %s \t| %.2f \t\t| %.2f \t|%n", TimeUtils.toDateString(endOfMonth), " ",
                    "I", interest, account.getBalance());
        }

    }

    // Step 3: Calculate and apply interest for each day in the month
    private static double getInitialBalance(List<Transaction> transactions) {
        double initialBalance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("D") && transaction.getDateString().endsWith("01")) {
                initialBalance += transaction.getAmount();
            } else if (transaction.getType().equals("W") && transaction.getDateString().endsWith("01")) {
                initialBalance -= transaction.getAmount();
            }
        }
        return initialBalance;
    }

    // Step 3: Calculate and apply interest for each day in the month
    private static double updateDailyBalance(List<Transaction> transactions, double balance, int day, BankAccount account) {
        for (Transaction transaction : transactions) {
            int transactionDay = Integer.parseInt(transaction.getDateString().substring(6, 8));
            if (transactionDay == day) {
                if (transaction.getType().equals("D")) {
                    balance += transaction.getAmount();
                } else if (transaction.getType().equals("W")) {
                    balance -= transaction.getAmount();
                }
            }
        }
        return balance;
    }


    public static String getLastDayOfMonth(String month) {
        // Ensure the input month is in "YYYYMMDD" format
        if (month.length() != 8) {
            throw new IllegalArgumentException("Invalid month format. Use YYYYMMDD.");
        }

        int year = Integer.parseInt(month.substring(0, 4));
        int monthOfYear = Integer.parseInt(month.substring(4, 6));

        // Calculate the last day of the month
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, monthOfYear - 1, 1); // Month is 0-based, so subtract 1
        int lastDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Format the last day as "YYYYMMDD"
        return String.format("%04d%02d%02d", year, monthOfYear, lastDay);
    }


    // Step 1: Retrieve transactions for the given month
    private static List<Transaction> getTransactionsInMonth(String month, BankAccount bankAccount) {
        List<Transaction> transactionsInMonth = new ArrayList<>();
        for (Transaction transaction : bankAccount.getTransactions()) {
            String transactionMonth = transaction.getDateString().substring(4, 6);
            if (transactionMonth.equals(month)) {
                transactionsInMonth.add(transaction);
            }
        }
        return transactionsInMonth;
    }

    // Step 2: Retrieve applicable interest rules
    private static List<InterestRule> getApplicableInterestRules(String month) {
        List<InterestRule> applicableRules = new ArrayList<>();
        InterestRule closestRule = null;

        for (InterestRule rule : DATA_STORE.getAllInterestRules()) {
            String ruleMonth = rule.getDateString().substring(4, 6);
            if (ruleMonth.equals(month)) {
                applicableRules.add(rule);
            } else if (ruleMonth.compareTo(month) < 0) {
                // This rule is from a previous month, check if it's the closest one before the 1st day
                if (closestRule == null || rule.getDateString().compareTo(closestRule.getDateString()) > 0) {
                    closestRule = rule;
                }
            }
        }

        if (closestRule != null) {
            applicableRules.add(closestRule);
        }

        // Sort applicable rules by date in descending order
        Collections.sort(applicableRules, (r1, r2) -> r2.getDateString().compareTo(r1.getDateString()));
        return applicableRules;
    }

    // Step 3: Calculate and apply interest for each day in the month
    private double getDailyBalance(List<Transaction> transactions, int day) {
        double dailyBalance = 0.0;
        for (Transaction transaction : transactions) {
            int transactionDay = Integer.parseInt(transaction.getDateString().substring(6, 8));
            if (transactionDay <= day) {
                if (transaction.getType().equals("D")) {
                    dailyBalance += transaction.getAmount();
                } else if (transaction.getType().equals("W")) {
                    dailyBalance -= transaction.getAmount();
                }
            }
        }
        return dailyBalance;
    }

    // Step 4: Find the applicable interest rule for a specific day
    public static InterestRule getApplicableRuleForDay(List<InterestRule> rules, String month, int day) {
        InterestRule closestRule = null;

        for (InterestRule rule : rules) {
            String ruleMonth = rule.getDateString().substring(4, 6);
            int ruleDay = Integer.parseInt(rule.getDateString().substring(6, 8));

            if (ruleMonth.equals(month) && ruleDay <= day) {
                return rule;
            } else if (ruleMonth.compareTo(month) < 0) {
                // This rule is from a previous month, check if it's the closest one before the 1st day
                if (closestRule == null || rule.getDateString().compareTo(closestRule.getDateString()) > 0) {
                    closestRule = rule;
                }
            }
        }

        if (closestRule != null) {
            return closestRule;
        }

        return null; // No applicable rule found
    }

    // Helper method to get the number of days in the given month
    public static int getDaysInMonth(String month) {
        int year = Integer.parseInt(month.substring(0, 4));
        int monthOfYear = Integer.parseInt(month.substring(4, 6));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear - 1); // Month is 0-based
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}