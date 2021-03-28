package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
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
public class ExpenseCategoryVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private ExpenseCategory expenseCategory;

    private BigDecimal maximumCost;

    @NotNull
    private LocalDate validMonth;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();
}
