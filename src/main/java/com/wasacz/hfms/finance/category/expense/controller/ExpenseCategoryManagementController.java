package com.wasacz.hfms.finance.category.expense.controller;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryVersionService;
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
public class ExpenseCategoryManagementController {

    private final ExpenseCategoryVersionService expenseCategoryVersionService;
    private final ExpenseCategoryVersionMapper expenseCategoryVersionMapper;

    public ExpenseCategoryManagementController(ExpenseCategoryVersionService expenseCategoryVersionService, ExpenseCategoryVersionMapper expenseCategoryVersionMapper) {
        this.expenseCategoryVersionService = expenseCategoryVersionService;
        this.expenseCategoryVersionMapper = expenseCategoryVersionMapper;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PutMapping("/expense/{id}/version")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> editMaximumAmountExpenseCategory(@CurrentUser UserPrincipal user,
                                                            @PathVariable("id") long categoryId,
                                                            @RequestBody ExpenseCategoryMaximumAmountRequest expenseCategoryMaximumAmountRequest) {
        ExpenseCategoryVersionResponse expenseCategoryVersionResponse;
        if(expenseCategoryMaximumAmountRequest.getIsValidFromNextMonth()) {
            expenseCategoryVersionResponse = expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(
                    expenseCategoryVersionService.addNewVersionForNextMonth(user.getUser(),
                                    categoryId,
                                    expenseCategoryMaximumAmountRequest.getNewMaximumAmount()));
        } else {
            expenseCategoryVersionResponse = expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(
                    expenseCategoryVersionService.editCategoryVersion(user.getUser(),
                            categoryId,
                            expenseCategoryMaximumAmountRequest.getNewMaximumAmount())
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryVersionResponse);
    }

}
