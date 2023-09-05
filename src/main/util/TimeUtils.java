package main.util;

import main.models.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class TimeUtils {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate toLocalDate(String dateString) throws ParseException {
        return LocalDate.parse(dateString, dateTimeFormatter);
    }

    public static String toDateString(LocalDate date) throws ParseException {
        return date.atStartOfDay().atZone(ZoneOffset.UTC).format(dateTimeFormatter);
    }

    public static Predicate<? super Transaction> getTransactionFilterPredicate(Month month) {
        return txn -> {
            LocalDate startDate = LocalDate.of(Year.now().getValue(), month.getValue(), 1);
            LocalDate endDate = LocalDate.of(Year.now().getValue(), month.getValue(), startDate.lengthOfMonth());
            return (txn.getDate().equals(endDate) || txn.getDate().isBefore(endDate))
                    && (txn.getDate().equals(startDate) || txn.getDate().isAfter(startDate));
        };
    }

}
