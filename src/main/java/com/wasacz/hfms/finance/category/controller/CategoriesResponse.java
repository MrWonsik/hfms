package com.wasacz.hfms.finance.category.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesResponse {
    private List<? extends AbstractCategoryResponse> categories;
}
