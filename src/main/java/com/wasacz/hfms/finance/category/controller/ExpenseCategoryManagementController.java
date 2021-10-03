package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.category.ExpenseCategoryVersionService;
import com.wasacz.hfms.finance.category.controller.dto.ExpenseCategoryMaximumAmountRequest;
import com.wasacz.hfms.persistence.ExpenseCategoryVersion;
import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.YearMonth;

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
    public ResponseEntity<?> updateMaximumAmountExpenseCategory(@CurrentUser UserPrincipal user,
                                                                @PathVariable("id") long categoryId,
                                                                @RequestBody ExpenseCategoryMaximumAmountRequest request) {

        YearMonth now = YearMonth.now();
        ExpenseCategoryVersion expenseCategoryVersion = expenseCategoryVersionService.updateCategoryVersion(user.getUser(),
                categoryId,
                request.getNewMaximumAmount(),
                request.getIsValidFromNextMonth() ? now.plusMonths(1) : now);
        return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(expenseCategoryVersion));
    }

}
