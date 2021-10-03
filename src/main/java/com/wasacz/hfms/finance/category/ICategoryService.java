package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.controller.dto.AbstractCategoryResponse;
import com.wasacz.hfms.finance.category.controller.dto.CategoriesResponse;
import com.wasacz.hfms.persistence.User;

public interface ICategoryService {

     AbstractCategoryResponse addCategory(AbstractCategory categoryRequest, User user);

     AbstractCategoryResponse toggleFavourite(long categoryId, boolean isFavourite, User user);

     AbstractCategoryResponse deleteCategory(long categoryId, User user);

     CategoriesResponse getAllCategories(User user);

     AbstractCategoryResponse editCategory(long id, String newCategoryName, String newColorHex, User user);

     String getServiceName();
}
