package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptFileRepository extends JpaRepository<ReceiptFile, Long> {

    Optional<ReceiptFile> findByExpenseId(Long expenseId);
}
