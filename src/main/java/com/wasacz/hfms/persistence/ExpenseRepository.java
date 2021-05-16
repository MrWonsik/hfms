package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<List<Expense>> findAllByUserAndExpenseDateIsBetween(User user, LocalDate fromDate, LocalDate toDate);

    Optional<List<Expense>> findAllByUser(User userId);

    Optional<Expense> findByIdAndUser(long transactionId, User user);

    Optional<List<Expense>> findAllByExpenseDateIsBetweenAndCategoryId(LocalDate fromDate, LocalDate toDate, long categoryId);

    Optional<Expense> findFirstByCategoryIdOrderByExpenseDateAsc(long categoryId);
}
