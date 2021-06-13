package com.wasacz.hfms.finance.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionTypeConverter implements Converter<String, TransactionType> {

    @Override
    public TransactionType convert(String value) {
        try {
            return TransactionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("Incorrect category type " + value);
            throw new IllegalArgumentException("Incorrect category type.");
        }
    }
}
