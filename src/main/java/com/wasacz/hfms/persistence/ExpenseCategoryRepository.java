package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

     Optional<List<ExpenseCategory>> findAllByUserAndIsDeletedFalse(User user);

     Optional<ExpenseCategory> findByIdAndUserAndIsDeletedFalse(long id, User user);

}
