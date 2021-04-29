package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String incomeName;

    @NotNull
    @ManyToOne
    private IncomeCategory category;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @Builder.Default
    private LocalDate expenseDate = LocalDate.now();

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();

    @LastModifiedDate
    @Builder.Default
    @Column(nullable = false)
    private final Instant lastModifiedDate = Instant.now();
}
