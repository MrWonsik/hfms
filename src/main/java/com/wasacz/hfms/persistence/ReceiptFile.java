package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReceiptFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String receiptFilePath;

    @NotNull
    @Column(unique = true)
    private String fileName;

    @NotNull
    @OneToOne
    private Expense expense;

    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();

    @LastModifiedDate
    @Builder.Default
    @Column(nullable = false)
    private final Instant lastModifiedDate = Instant.now();
}
