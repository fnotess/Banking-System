package test;

import main.Main;
import main.models.BankAccount;
import main.repository.BankDataStoreI;
import main.service.statement.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainTest {

    @Test
    void testGetLastDayOfMonth() {
        String lastDay = Main.getLastDayOfMonth("20230601");
        assertEquals("20230630", lastDay);
    }

    @Test
    void testGetDaysInMonth() {
        int daysInJune = Main.getDaysInMonth("20230601");
        assertEquals(30, daysInJune);
    }

    // Can add more test scenarios
}
