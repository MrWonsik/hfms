package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.category.CategoryServiceProvider;
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

    private final CategoryServiceProvider categoryServiceProvider;

    public CategoryManagementController(CategoryServiceProvider categoryServiceProvider) {
        this.categoryServiceProvider = categoryServiceProvider;
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

    @PostMapping("/{type}/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addCategory(@CurrentUser UserPrincipal user,
                                         @PathVariable("type") CategoryType categoryType,
                                         @RequestBody CategoryObj categoryObj) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceProvider.addCategory(categoryObj, user.getUser(), categoryType);
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

    @PatchMapping("/{type}/favourite/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> setCategoryAsFavourite(@CurrentUser UserPrincipal user,
                                                    @PathVariable("id") long categoryId,
                                                    @PathVariable("type") CategoryType categoryType,
                                                    @RequestBody CategoryIsFavouriteRequest categoryIsFavouriteRequest) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceProvider.setAsFavourite(categoryId, categoryIsFavouriteRequest, user.getUser(), categoryType);
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

    @DeleteMapping("/{type}/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> deleteCategory(@CurrentUser UserPrincipal user,
                                            @PathVariable("id") long categoryId,
                                            @PathVariable("type") CategoryType categoryType) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceProvider.deleteCategory(categoryId, user.getUser(), categoryType);
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

    @GetMapping("/{type}/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAllExpenseCategory(@CurrentUser UserPrincipal user,
                                                   @PathVariable("type") CategoryType categoryType) {
        CategoriesResponse categoriesResponse = categoryServiceProvider.getAllCategories(user.getUser(), categoryType);
        return ResponseEntity.status(HttpStatus.OK).body(categoriesResponse);
    }

    @PatchMapping("/{type}/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> editCategory(@CurrentUser UserPrincipal user,
                                                    @PathVariable("id") long categoryId,
                                                    @PathVariable("type") CategoryType categoryType,
                                                    @RequestBody EditCategoryRequest editCategoryRequest) {
        AbstractCategoryResponse abstractCategoryResponse = categoryServiceProvider.editCategory(categoryId, editCategoryRequest.getCategoryName(), editCategoryRequest.getColorHex(), user.getUser(), categoryType);
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

}
