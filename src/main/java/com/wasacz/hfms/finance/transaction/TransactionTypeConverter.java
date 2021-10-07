package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.finance.ServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionTypeConverter implements Converter<String, ServiceType> {

    @Override
    public ServiceType convert(String value) {
        try {
            return ServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("Incorrect category type " + value);
            throw new IllegalArgumentException("Incorrect category type.");
        }
    }
}
