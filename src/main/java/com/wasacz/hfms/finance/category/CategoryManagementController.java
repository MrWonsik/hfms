package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
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

    private final CategoryManagementService categoryManagementService;

    public CategoryManagementController(CategoryManagementService categoryManagementService) {
        this.categoryManagementService = categoryManagementService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping("/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addCategory(@CurrentUser UserPrincipal user, @RequestBody CreateCategoryRequest createCategoryRequest) {
        AbstractCategoryResponse abstractCategoryResponse = categoryManagementService.addCategory(createCategoryRequest, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }

    @PatchMapping("/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> setCategoryAsFavourite(@CurrentUser UserPrincipal user,
                                                    @PathVariable("id") long categoryId,
                                                    @RequestBody CategoryIsFavouriteRequest categoryIsFavouriteRequest) {
        AbstractCategoryResponse abstractCategoryResponse = categoryManagementService.setAsFavourite(categoryId, categoryIsFavouriteRequest, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(abstractCategoryResponse);
    }
}
