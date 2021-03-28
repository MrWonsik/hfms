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
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, updatable = false)
    private String shopName;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    private boolean isDeleted = false;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final Instant createdDate = Instant.now();
}
