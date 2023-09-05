package main.service.statement;

import java.time.Month;

public interface StatementServiceI {

    Double generateMonthlyInterestForAccount(String accountID, Month month);

}
