package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.expense.receiptFile.FileReceiptResponse;
import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/transaction/expense")
public class ExpenseTransactionController {

    private final ExpenseService expenseService;

    public ExpenseTransactionController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }


    @GetMapping(value = "/{id}/file")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getReceiptFile(@CurrentUser UserPrincipal user,
                                            @PathVariable("id") Long transactionId) {
        FileReceiptResponse file = expenseService.getReceiptFileByExpense(transactionId, user.getUser());
        return ResponseEntity.ok().body(file);
    }

    @DeleteMapping(value = "/{id}/file")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> deleteReceiptFile(@CurrentUser UserPrincipal user,
                                            @PathVariable("id") Long transactionId) {
        expenseService.deleteReceiptFile(transactionId, user.getUser());
        return ResponseEntity.ok().body("File has been deleted.");
    }

    @PostMapping(value = "/{id}/file")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> uploadReceiptFile(@CurrentUser UserPrincipal user,
                                               @RequestParam(value = "file", required = false) MultipartFile receiptFile,
                                               @PathVariable("id") Long expenseId) {
        FileReceiptResponse file = expenseService.uploadReceiptFile(expenseId, receiptFile, user.getUser());
        return ResponseEntity.ok().body(file);

    }
}
