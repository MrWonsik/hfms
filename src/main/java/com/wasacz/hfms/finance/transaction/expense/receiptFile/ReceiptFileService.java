package com.wasacz.hfms.finance.transaction.expense.receiptFile;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ReceiptFile;
import com.wasacz.hfms.persistence.ReceiptFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
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

    public ReceiptFile saveFile(MultipartFile file, Expense expense, String username) {
        if (file == null) {
            return null;
        }
        if(getReceiptFileByExpense(expense.getId()).isPresent()) {
            throw new IllegalStateException("File for this expense is already uploaded!");
        }
        String dirPath = "%s/%s".formatted(destinationPath, username);
        String filePathname = "%s/%s_%s.jpg".formatted(dirPath, expense.getExpenseName(), Instant.now().getEpochSecond());
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

    public Optional<ReceiptFile> getReceiptFileByExpense(Long expenseId) {
        return repository.findByExpenseId(expenseId);
    }

    public ReceiptFile getFile(Long expenseId) {
        Optional<ReceiptFile> receiptFileByExpense = getReceiptFileByExpense(expenseId);
        if(receiptFileByExpense.isEmpty()) {
            return null;
        }

        return repository.findById(receiptFileByExpense.get().getId()).orElse(null);
    }

    public FileReceiptResponse mapFileReceiptToResponse(ReceiptFile receiptFile) {
        File file = getFileByReceiptFile(receiptFile);
        return FileReceiptResponse.builder()
                .id(receiptFile.getId())
                .name(file.getName())
                .length(file.length())
                .base64Resource(getBase64Resource(file))
                .build();
    }

    private File getFileByReceiptFile(ReceiptFile receiptFile) {
        if(receiptFile == null) {
            return null;
        }

        String filePath = receiptFile.getReceiptFilePath() + "/" + receiptFile.getFileName();
        return new File(filePath);
    }

    private String getBase64Resource(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
            try (InputStream inputstream = inputStreamResource.getInputStream()) {
                return Base64.getEncoder().encodeToString(inputstream.readAllBytes());
            }
        } catch (IOException e) {
            throw new IllegalStateException("File not found.");
        }
    }

    public void deleteFileByExpense(Long expenseId) {
        Optional<ReceiptFile> file = getReceiptFileByExpense(expenseId);
        if(file.isEmpty()) {
            return;
        }
        ReceiptFile receiptFile = file.get();

        try {
            Files.delete(Path.of(receiptFile.getReceiptFilePath() + "\\" + receiptFile.getFileName()));
            repository.delete(receiptFile);
        } catch (IOException e) {
            log.error("Error while delete the file: {} - {}", e.getClass(), e.getMessage());
            throw new IllegalStateException("Something goes wrong while delete the file. %s - %s".formatted(e.getClass(), e.getMessage()));
        }
    }
}
