package com.wasacz.hfms.expense.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopResponse {
    private final Long id;
    private final String shopName;
    private final boolean isDeleted;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate createDate;
}
