package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_MIXED_VALUE})
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addExpense(@CurrentUser UserPrincipal user,
                                        @RequestBody ExpenseObj expenseObj,
                                        @RequestPart(value = "file", required = false) MultipartFile receiptFile) {
        ExpenseResponse response = expenseService.addExpense(expenseObj, user.getUser(), receiptFile);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
