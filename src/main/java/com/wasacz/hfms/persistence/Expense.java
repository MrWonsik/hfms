package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String expenseName;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ExpenseCategory category;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Shop shop;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    private BigDecimal amount;

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
