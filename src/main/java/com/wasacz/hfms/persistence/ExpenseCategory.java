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
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, updatable = false)
    private String categoryName;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    private String colorHex;

    @NotNull
    private Boolean isDeleted = false;

    @NotNull
    private Boolean isFavourite = false;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();
}
