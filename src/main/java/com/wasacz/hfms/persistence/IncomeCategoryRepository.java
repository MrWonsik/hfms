package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

    Optional<List<IncomeCategory>> findAllByUserAndIsDeletedFalse(User user);

    Optional<IncomeCategory> findByIdAndUserAndIsDeletedFalse(long id, User user);

}
