package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensePositionRepository extends JpaRepository<ExpensePosition, Long> {
}
