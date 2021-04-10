package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryVersionRepository extends JpaRepository<ExpenseCategoryVersion, Long> {

    Optional<List<ExpenseCategoryVersion>> findExpenseCategoryVersionsByExpenseCategoryEquals(ExpenseCategory expenseCategory);
}
