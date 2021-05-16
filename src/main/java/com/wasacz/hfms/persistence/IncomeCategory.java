package com.wasacz.hfms.persistence;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String categoryName;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    private String colorHex;

    @NotNull
    @Builder.Default
    private Boolean isDeleted = false;

    @NotNull
    @Builder.Default
    private Boolean isFavourite = false;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();
}
