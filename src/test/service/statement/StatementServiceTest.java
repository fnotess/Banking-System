package test.service.statement;

import main.exceptions.AccountNotFoundException;
import main.models.BankAccount;
import main.models.InterestRule;
import main.models.Transaction;
import main.repository.BankDataStoreI;
import main.service.statement.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatementServiceTest {

    private StatementService statementService;

    @Mock
    private BankDataStoreI dataStore;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        statementService = StatementService.getInstance();

        // Using reflection here to set the private DATA_STORE field
        Field dataStoreField = StatementService.class.getDeclaredField("DATA_STORE");
        dataStoreField.setAccessible(true);
        dataStoreField.set(statementService, dataStore);
    }

    @Test
    void generateMonthlyInterestForAccount_AccountNotFound_ThrowsException() {
        // Arrange
        String accountID = "nonexistentAccount";
        when(dataStore.getBankAccount(accountID)).thenReturn(null);

        // Act and Assert
        assertThrows(AccountNotFoundException.class, () -> statementService.generateMonthlyInterestForAccount(accountID, Month.JANUARY));
    }

    @Test
    public void testGenerateMonthlyInterestForAccount_NoTransactions() {
        String accountID = "validAccount";
        BankAccount bankAccount = new BankAccount(accountID);
        when(dataStore.getBankAccount(accountID)).thenReturn(bankAccount);

        Double interest = statementService.generateMonthlyInterestForAccount(accountID, Month.JANUARY);

        assertEquals(0.0, interest);
    }

    public List<InterestRule> createInterestRules() throws ParseException {
        List<InterestRule> rules = new ArrayList<>();
        rules.add(new InterestRule("20230610","RULE01", 5.0));
        return rules;
    }

    @Test
    void generateMonthlyInterestForAccount_CalculateInterest_Success() throws ParseException {
        // Arrange
        String accountID = "existingAccount";
        BankAccount bankAccount = new BankAccount(accountID);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("20230602","ac001","D", 100.0, 200.0));
        when(dataStore.getBankAccount(accountID)).thenReturn(bankAccount);
        when(dataStore.getAllInterestRules()).thenReturn(createInterestRules());

        // Call the method
        Double interest = statementService.generateMonthlyInterestForAccount(accountID, Month.JUNE);

        // Assert
        assertEquals(0.0, interest, 0.001);
    }

    @Test
    void calculateInterestBetweenDates_CalculateInterest_Success() throws ParseException {
        // Arrange the necessary params
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        double balance = 1000.0;
        List<InterestRule> interestRules = createInterestRules();

        // Act
        double interest = statementService.calculateInterestBetweenDates(startDate, endDate, balance, interestRules);

        // Assert
        assertEquals(1000, interest, 0.001);
    }
    // Not all scenarios are covered here .Similar to this can add more tests to cover different scenarios and improve coverage
}

