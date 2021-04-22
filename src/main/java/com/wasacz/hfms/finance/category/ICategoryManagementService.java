package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.persistence.User;

public interface ICategoryManagementService {

     AbstractCategoryResponse addCategory(CreateCategoryRequest categoryRequest, User user);

     AbstractCategoryResponse setAsFavourite(long categoryId, boolean isFavourite, User user);

     AbstractCategoryResponse deleteCategory(long categoryId, User user);

     CategoriesResponse getAllCategories(User user);

    AbstractCategoryResponse editCategory(long id, String newCategoryName, String newColorHex, User user);
}
