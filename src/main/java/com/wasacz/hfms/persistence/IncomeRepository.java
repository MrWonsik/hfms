package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<List<Income>> findAllByUserAndIncomeDateIsBetween(User user, LocalDate fromDate, LocalDate toDate);

    Optional<List<Income>> findAllByUser(User userId);

    Optional<Income> findByIdAndUser(long transactionId, User user);

    Optional<List<Income>> findAllByIncomeDateIsBetweenAndCategoryId(LocalDate atDay, LocalDate atEndOfMonth, long categoryId);

    Optional<Income> findFirstByCategoryIdOrderByIncomeDateAsc(long categoryId);
}
