package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/transaction/expense")
public class ExpenseTransactionController {

    private final ExpenseService expenseService;

    public ExpenseTransactionController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping(value = "/{id}/file/{receiptId}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getReceipt(@CurrentUser UserPrincipal user,
                                    @PathVariable("id") Long transactionId,
                                    @PathVariable("receiptId") Long receiptId) {
        FileReceiptResponse file = expenseService.getReceiptFile(transactionId, receiptId, user.getUser());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.getLength())
                .body(file.getBase64Resource());
    }
}
