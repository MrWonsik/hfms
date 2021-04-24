package com.wasacz.hfms.finance.expense.Controller;

import com.wasacz.hfms.finance.AbstractFinance;
import com.wasacz.hfms.finance.AbstractFinanceResponse;
import com.wasacz.hfms.finance.FinanceType;
import com.wasacz.hfms.finance.IFinanceService;
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
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceServiceFactory financeServiceFactory;

    public FinanceController(FinanceServiceFactory financeServiceFactory) {
        this.financeServiceFactory = financeServiceFactory;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping(value = "/{type}/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_MIXED_VALUE})
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> add(@CurrentUser UserPrincipal user,
                                @RequestBody AbstractFinance abstractFinanceObj,
                                @RequestPart(value = "file", required = false) MultipartFile receiptFile,
                                @PathVariable("type") FinanceType financeType) {
        AbstractFinanceResponse response = financeServiceFactory.getService(financeType).add(abstractFinanceObj, user.getUser(), receiptFile);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{type}/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAll(@CurrentUser UserPrincipal user,
                                            @PathVariable("type") FinanceType financeType) {
        List<AbstractFinanceResponse> response = financeServiceFactory.getService(financeType).getAll(user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
