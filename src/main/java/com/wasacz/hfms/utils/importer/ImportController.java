package com.wasacz.hfms.utils.importer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/import")
@Slf4j
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping("/")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> importCategory(@RequestBody ImportRequest importRequest) {
        importService.importData(importRequest.getUserId(), importRequest.getImportDataList());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
