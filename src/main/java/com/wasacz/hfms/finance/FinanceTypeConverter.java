package com.wasacz.hfms.finance;

import com.wasacz.hfms.finance.category.CategoryType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FinanceTypeConverter implements Converter<String, FinanceType> {

    @Override
    public FinanceType convert(String value) {
        try {
            return FinanceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Incorrect category type.");
        }
    }
}
