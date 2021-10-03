package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.category.CategoryServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class CategoryTypeConverter implements Converter<String, CategoryServiceType> {

    @Override
    public CategoryServiceType convert(String value) {
        try {
            return CategoryServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Incorrect category type " + value);
            throw new IllegalArgumentException("Incorrect category type.");
        }
    }
}
