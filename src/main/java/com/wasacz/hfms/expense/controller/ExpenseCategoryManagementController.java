package com.wasacz.hfms.expense.controller;

import com.wasacz.hfms.expense.service.ExpenseCategoryManagementService;
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
@RequestMapping("/api/expense-category")
public class ExpenseCategoryManagementController {

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    public ExpenseCategoryManagementController(ExpenseCategoryManagementService expenseCategoryManagementService) {
        this.expenseCategoryManagementService = expenseCategoryManagementService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping("/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addExpenseCategory(@CurrentUser UserPrincipal user, @RequestBody CreateExpenseCategoryRequest createExpenseCategoryRequest) {
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.addExpenseCategory(createExpenseCategoryRequest, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryResponse);
    }

    @GetMapping("/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAllExpenseCategory(@CurrentUser UserPrincipal user) {
        ExpenseCategoriesResponse expenseCategoryResponse = expenseCategoryManagementService.getAllExpenseCategory(user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryResponse);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> editExpenseCategory(
            @CurrentUser UserPrincipal user,
            @PathVariable("id") long expenseCategoryId,
            @RequestBody EditExpenseCategoryRequest editExpenseCategoryRequest) {

        var expenseCategoryResponse = expenseCategoryManagementService
                .editExpenseCategory(expenseCategoryId, editExpenseCategoryRequest, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryResponse);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> deleteExpenseCategory(
            @PathVariable("id") long expenseCategoryId,
            @CurrentUser UserPrincipal user) {

        var expenseCategoryResponse = expenseCategoryManagementService
                .deleteExpenseCategory(expenseCategoryId, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryResponse);
    }

}
