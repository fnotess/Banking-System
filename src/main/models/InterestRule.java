package main.models;

import main.util.TimeUtils;

import java.text.ParseException;
import java.time.LocalDate;

public class InterestRule {
    private LocalDate date;
    private String ruleId;
    private double rate;

    public InterestRule(String date, String ruleId, double rate) throws ParseException {
        this.date = TimeUtils.toLocalDate(date);
        this.ruleId = ruleId;
        this.rate = rate;
    }

    public String getDateString() {
        try {
            return TimeUtils.toDateString(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRuleId() {
        return ruleId;
    }

    public double getRate() {
        return rate;
    }
}
