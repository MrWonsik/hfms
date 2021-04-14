package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryVersionRepository extends JpaRepository<ExpenseCategoryVersion, Long> {

    Optional<List<ExpenseCategoryVersion>> findByExpenseCategory(ExpenseCategory expenseCategory);

    Optional<ExpenseCategoryVersion> findByExpenseCategoryAndValidMonth(ExpenseCategory expenseCategory, YearMonth validMonth);
}
