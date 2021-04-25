package com.wasacz.hfms.finance.transaction;

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
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionServiceFactory transactionServiceFactory;

    public TransactionController(TransactionServiceFactory transactionServiceFactory) {
        this.transactionServiceFactory = transactionServiceFactory;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping(value = "/{type}/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> add(@CurrentUser UserPrincipal user,
                                 @RequestParam(value = "file", required = false) MultipartFile receiptFile,
                                 @RequestPart AbstractTransaction transaction,
                                 @PathVariable("type") TransactionType transactionType) {
        AbstractTransactionResponse response = transactionServiceFactory.getService(transactionType).add(transaction, user.getUser(), receiptFile);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{type}/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAll(@CurrentUser UserPrincipal user,
                                            @PathVariable("type") TransactionType transactionType) {
        List<AbstractTransactionResponse> response = transactionServiceFactory.getService(transactionType).getAll(user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
