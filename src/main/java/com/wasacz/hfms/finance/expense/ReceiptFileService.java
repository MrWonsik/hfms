package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ReceiptFile;
import com.wasacz.hfms.persistence.ReceiptFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

@Component
@Slf4j
public class ReceiptFileService {
    @Value("${app.receipt.storage.path}")
    private String destinationPath;

    private final ReceiptFileRepository repository;

    public ReceiptFileService(ReceiptFileRepository repository) {
        this.repository = repository;
    }

    public ReceiptFile saveFile(MultipartFile file, Expense expense, String expenseName, String username) {
        if (file == null) {
            return null;
        }
        String dirPath = "%s/%s".formatted(destinationPath, username);
        String filePathname = "%s/%s_%s.jpg".formatted(dirPath, expenseName, Instant.now().getEpochSecond());
        try {
            Files.createDirectories(Paths.get(dirPath));
            File newFile = new File(filePathname);
            file.transferTo(newFile);
            return repository.save(ReceiptFile.builder()
                    .fileName(newFile.getName())
                    .receiptFilePath(newFile.getParent())
                    .expense(expense)
                    .build());
        } catch (IOException e) {
            log.error("Error while save the file: {} - {}", e.getClass(), e.getMessage());
            throw new IllegalStateException("Something goes wrong while save the file. %s - %s".formatted(e.getClass(), e.getMessage()));
        }
    }

    public Optional<ReceiptFile> getFileByExpense(Long expenseId) {
        return repository.findByExpenseId(expenseId);
    }
}
