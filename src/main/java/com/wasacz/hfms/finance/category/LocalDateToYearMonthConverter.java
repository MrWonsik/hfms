package com.wasacz.hfms.finance.category;

import java.time.LocalDate;
import java.time.YearMonth;

class LocalDateToYearMonthConverter {

    static YearMonth convertToYearMonth(LocalDate localDate) {
        return YearMonth.of(localDate.getYear(), localDate.getMonth());
    }
}
