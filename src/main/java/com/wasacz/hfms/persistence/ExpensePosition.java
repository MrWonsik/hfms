package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpensePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String expensePositionName;

    private BigDecimal size;

    @NotNull
    private BigDecimal cost;

    @NotNull
    @ManyToOne
    private Expense expense;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();

    @LastModifiedDate
    @Builder.Default
    @Column(nullable = false)
    private final Instant lastModifiedDate = Instant.now();
}
