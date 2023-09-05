package test;

import main.models.InterestRule;
import main.Main;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    void testIsValidDate() {
        assertTrue(Main.isValidDate("20230601"));
        assertFalse(Main.isValidDate("2023-06-01")); // Invalid format
        assertFalse(Main.isValidDate("20230632")); // Invalid day
        assertFalse(Main.isValidDate("20230000")); // Invalid month
        assertFalse(Main.isValidDate("20231301")); // Invalid month
    }

    @Test
    void testGetApplicableRuleForDay() throws ParseException {
        // Create a list of interest rules
        List<InterestRule> rules = new ArrayList<>();
        rules.add(new InterestRule("20230515", "RULE01", 1.95));
        rules.add(new InterestRule("20230615", "RULE02", 2.20));

        // Test with a day in May
        InterestRule rule1 = Main.getApplicableRuleForDay(rules, "05", 10);
        assertEquals("20230515", rule1.getDateString());

        // Test with a day in June
        InterestRule rule2 = Main.getApplicableRuleForDay(rules, "06", 20);
        assertEquals("20230615", rule2.getDateString());

        // Test with a day in July (no applicable rule)
        InterestRule rule3 = Main.getApplicableRuleForDay(rules, "07", 5);
        assertNull(rule3);
    }

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
}
