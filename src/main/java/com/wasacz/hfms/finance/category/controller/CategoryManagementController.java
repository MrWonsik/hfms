package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.category.AbstractCategory;
import com.wasacz.hfms.finance.category.CategoryServiceFactory;
import com.wasacz.hfms.finance.category.CategoryType;
import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/category")
public class CategoryManagementController {

    private final CategoryServiceFactory categoryServiceFactory;

    public CategoryManagementController(CategoryServiceFactory categoryServiceFactory) {
        this.categoryServiceFactory = categoryServiceFactory;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler({ConversionFailedException.class})
    public void handleConversionFailedExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", "Incorrect category type.");
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Incorrect category type.");
    }

    @PostMapping("/{type}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addCategory(@CurrentUser UserPrincipal user,
                                         @PathVariable("type") CategoryType categoryType,
                                         @RequestBody AbstractCategory categoryObj) {
        AbstractCategoryResponse response = categoryServiceFactory.getService(categoryType).addCategory(categoryObj, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{type}/favourite/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> setCategoryAsFavourite(@CurrentUser UserPrincipal user,
                                                    @PathVariable("id") long categoryId,
                                                    @PathVariable("type") CategoryType categoryType,
                                                    @RequestBody CategoryIsFavouriteRequest request) {
        AbstractCategoryResponse response = categoryServiceFactory.getService(categoryType).setAsFavourite(categoryId, request.getIsFavourite(), user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{type}/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> deleteCategory(@CurrentUser UserPrincipal user,
                                            @PathVariable("id") long categoryId,
                                            @PathVariable("type") CategoryType categoryType) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceFactory.getService(categoryType).deleteCategory(categoryId, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

    @GetMapping("/{type}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAllExpenseCategory(@CurrentUser UserPrincipal user,
                                                   @PathVariable("type") CategoryType categoryType) {
        CategoriesResponse categoriesResponse = categoryServiceFactory.getService(categoryType).getAllCategories(user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(categoriesResponse);
    }

    @PatchMapping("/{type}/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> editCategory(@CurrentUser UserPrincipal user,
                                                    @PathVariable("id") long categoryId,
                                                    @PathVariable("type") CategoryType categoryType,
                                                    @RequestBody EditCategoryRequest editCategoryRequest) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceFactory.getService(categoryType).editCategory(categoryId, editCategoryRequest.getCategoryName(), editCategoryRequest.getColorHex(), user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

}
